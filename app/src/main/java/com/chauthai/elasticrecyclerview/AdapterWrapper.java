package com.chauthai.elasticrecyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.util.Locale;

/**
 * Created by Chau Thai on 5/17/16.
 */
@SuppressWarnings("FieldCanBeLocal")
public class AdapterWrapper extends RecyclerView.Adapter {
    private static final double SPEED_FACTOR = 10;

    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int FOOTER_SIZE = 300; // dp

    private final int footerSizePx;

    private final SpringSystem mSpringSystem = SpringSystem.create();
    private final SpringConfig mSpringConfig = new SpringConfig(1000, 200);

    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private View footerView;
    private Spring mSpringFooter;
    private DecelerateSmoothScroller mScrollerFooter;

    private final Handler mHandlerUI = new Handler(Looper.getMainLooper());

    public AdapterWrapper(Context context, RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        mContext = context;
        mAdapter = adapter;
        mRecyclerView = recyclerView;
        footerSizePx = (int) dpToPx(FOOTER_SIZE);

        footerView = createFooterView();
        mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mScrollerFooter = new DecelerateSmoothScroller(context);

        initFooterSpring();
        setupRecyclerView();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != VIEW_TYPE_FOOTER)
            return mAdapter.onCreateViewHolder(parent, viewType);
        return new FooterHolder(footerView);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1)
            return VIEW_TYPE_FOOTER;

        return mAdapter.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mAdapter.onAttachedToRecyclerView(recyclerView);
    }

    private long prevTime = SystemClock.elapsedRealtime();
    private double speed = 0;

    private boolean isScrollBack = false;
    private boolean footerAlreadyVisible = false;
    private int minDistanceToScrollBack = 1;
    private boolean shouldUseSpring = false;

    private void setupRecyclerView() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int state = recyclerView.getScrollState();
                boolean usingScrollBy = (state == RecyclerView.SCROLL_STATE_IDLE && dy != 0);
                boolean isDragging = (state == RecyclerView.SCROLL_STATE_DRAGGING);

                computeScrollSpeed(dy);

                if (!isDragging && !usingScrollBy) {
                    int footerVisible = getFooterVisibleLength();

                    if (footerVisible == 0) {
                        footerAlreadyVisible = false;
                        isScrollBack = false;

                        if (!mSpringFooter.isAtRest()) {
                            mSpringFooter.setAtRest();
                        }
                        minDistanceToScrollBack = 1;

                    } else if (!isScrollBack) {
                        if (!footerAlreadyVisible) {
                            // check if it's already exceeded the distance to scroll back
                            minDistanceToScrollBack = getMinDistanceToScrollBack(speed);
                            footerAlreadyVisible = true;

                            // scroll back
                            if (footerVisible >= minDistanceToScrollBack) {
                                scrollBack(footerVisible);
                            } else {
                                reduceScrollSpeed(footerVisible, speed);
                            }
                        } else if (footerVisible >= minDistanceToScrollBack) {
                            scrollBack(footerVisible);
                        }
                    }
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        prevTime = SystemClock.elapsedRealtime();
                        shouldUseSpring = false;

                        if (!mSpringFooter.isAtRest()) {
                            mSpringFooter.setAtRest();
                        }
                        isScrollBack = false;
                        rv.stopScroll();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        onActionUp();
                        break;
                }

                mGestureOnIntercept = true;
                mGestureDetector.onTouchEvent(e);

                // return true so that RecyclerView won't scroll when the user scroll.
                // We scroll it using scrollBy().
                return getFooterVisibleLength() > 0;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                mGestureOnIntercept = false;
                mGestureDetector.onTouchEvent(e);

                switch (e.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mGestureOnIntercept = true;
                        onActionUp();
                        break;
                }
            }
        });
    }

    private void computeScrollSpeed(int dy) {
        long currTime = SystemClock.elapsedRealtime();

        if (firstScrollBy) {
            firstScrollBy = false;
            dy = getFooterVisibleLength();
        }
        speed = (double) dy / (currTime - prevTime);

        prevTime = currTime;
    }

    private void onActionUp() {
        int footerVisible = getFooterVisibleLength();
        if (footerVisible > 0) {
            minDistanceToScrollBack = getMinDistanceToScrollBack(speed);

            if (footerVisible < minDistanceToScrollBack) {
                reduceScrollSpeed(footerVisible, speed);
            } else {
                scrollBack(footerVisible);
            }
        }

        shouldUseSpring = true;
    }

    private final Object lockSpring = new Object();

    private void scrollBack(int footerVisible) {
        synchronized (lockSpring) {
            isScrollBack = true;
            isSpringFirstValue = true;

            mRecyclerView.stopScroll();
            mSpringFooter.setCurrentValue(footerVisible);
            mSpringFooter.setEndValue(0);
        }
    }

    /**
     * Compute the over scroll threshold to scroll back.
     * @param speed px per ms
     * @return pixels
     */
    private int getMinDistanceToScrollBack(double speed) {
        return (int) Math.min((footerSizePx / SPEED_FACTOR * pxToDp(Math.abs(speed))), footerSizePx);

    }

    private void reduceScrollSpeed(int currentVisible, double speed) {
        mRecyclerView.stopScroll();
        int distToStop = minDistanceToScrollBack;

        mScrollerFooter.setTargetPosition(getItemCount() - 1);
        mScrollerFooter.setDistanceToStop(distToStop);
        mScrollerFooter.setInitialSpeed((float) Math.abs(speed));
        mLayoutManager.startSmoothScroll(mScrollerFooter);
    }

    private int getScrollPosition() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int firstVisiblePos = layoutManager.findFirstVisibleItemPosition();
        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(firstVisiblePos);

        if (viewHolder != null && viewHolder.itemView != null) {
            View view = viewHolder.itemView;
            return view.getTop();
        }

        return 0;
    }

    private int getFooterVisibleLength() {
        View footerView = mLayoutManager.findViewByPosition(getItemCount() - 1);

        if (footerView != null) {
            return Math.max(0, mRecyclerView.getHeight() - footerView.getTop());
        }

        return 0;
    }

    private boolean isSpringFirstValue = true;
    private void initFooterSpring() {
        mSpringFooter = mSpringSystem.createSpring();
        mSpringFooter.setSpringConfig(mSpringConfig);

        mSpringFooter.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                if (!shouldUseSpring)
                    return;

                synchronized (lockSpring) {
                    int visibleLength = getFooterVisibleLength();
                    double springLength = spring.getCurrentDisplacementDistance();
                    double diff = (springLength - visibleLength);

                    if (diff <= 0) {
                        // discard the first value
                        if (isSpringFirstValue) {
                            isSpringFirstValue = false;
                            return;
                        }

                        if (!mFlingOverScrollBack) {
                            mRecyclerView.stopScroll();
                        }
                        mRecyclerView.scrollBy(0, (int) diff);
                    }
                }
            }

            @Override
            public void onSpringAtRest(Spring spring) {
                isScrollBack = false;
            }
        });
    }

    private View createFooterView() {
        View view = new View(mContext);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                 footerSizePx));
        return view;
    }

    private class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(View v) {
            super(v);
        }
    }

    private boolean firstScrollBy = false;
    private boolean mGestureOnIntercept = true;
    private boolean mFlingOverScrollBack = false;  // fling back while over scrolled.

    private final GestureDetectorCompat mGestureDetector = new GestureDetectorCompat(mContext,
            new GestureDetector.SimpleOnGestureListener() {
                int scrollByCount = 0;

                @Override
                public boolean onDown(MotionEvent e) {
                    scrollByCount = 0;
                    mFlingOverScrollBack = false;
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    int footerVisible = getFooterVisibleLength();

                    if (footerVisible > 0) {
                        scrollByCount++;
                        firstScrollBy = (scrollByCount == 1);

                        double ratioVisible = (double) footerVisible / footerSizePx;
                        double scrollDist = distanceY - distanceY * ratioVisible;

                        if (distanceY > 0) {
                            scrollDist = Math.abs(scrollDist);
                        } else {
                            scrollDist = -Math.abs(scrollDist);
                        }

                        mRecyclerView.scrollBy(0, (int) scrollDist);
                    }

                    // still in onTouchEvent, manually scroll the recycler view.
                    else if (footerVisible == 0 && !mGestureOnIntercept) {
                        mRecyclerView.scrollBy((int) distanceX, (int) distanceY);
                    }

                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, final float velocityY) {
                    // footer not visible, use regular fling.
                    if (getFooterVisibleLength() == 0) {
                        mHandlerUI.post(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.fling((int) -velocityX, (int) -velocityY);
                            }
                        });
                    }

                    // footer is visible, only fling if it's fling back.
                    else if (!mGestureOnIntercept && velocityY > 0) {
                        mHandlerUI.post(new Runnable() {
                            @Override
                            public void run() {
                                mFlingOverScrollBack = true;
                                mRecyclerView.fling((int) -velocityX, (int) -velocityY);
                            }
                        });
                    }

                    return true;
                }
            });

    private double dpToPx(double dp) {
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return  dp * ((double) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private double pxToDp(double px) {
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return  px / ((double) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private String format(double value) {
        return String.format(Locale.US, "%1$,.2f", value);
    }

    private String toStringScrollState(int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                return "dragging";
            case RecyclerView.SCROLL_STATE_IDLE:
                return "idle";
            case RecyclerView.SCROLL_STATE_SETTLING:
                return "settling";
        }
        return "";
    }
}



