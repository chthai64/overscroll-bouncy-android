package com.chauthai.elasticrecyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.SystemClock;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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

    private static final double TENSION = 1000;
    private static final double FRICTION = 200;

    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int FOOTER_SIZE = 300; // dp

    private final int footerSizePx;

    private final SpringSystem mSpringSystem = SpringSystem.create();
    private final SpringConfig mSpringConfig = new SpringConfig(TENSION, FRICTION);

    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private View footerView;
    private Spring mSpringFooter;
    private DecelerateSmoothScroller mScrollerFooter;

    public AdapterWrapper(Context context, RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        mContext = context;
        mAdapter = adapter;
        mRecyclerView = recyclerView;
        footerSizePx = (int) dpToPx(FOOTER_SIZE);

        footerView = createFooterView();
        mRecyclerView.addItemDecoration(mItemDecoration);

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
    private boolean isScrollBack = false;
    private boolean footerAlreadyVisible = false;
    private int minDistanceToScrollBack = 0;
    private boolean shouldUseSpring = false;

    private boolean lockScroll = false;

    private void setupRecyclerView() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int state = recyclerView.getScrollState();

                if (state == RecyclerView.SCROLL_STATE_DRAGGING)
                    return;

                // is scrolling using scrollBY() method.
                if (state == RecyclerView.SCROLL_STATE_IDLE && dy != 0)
                    return;

                long currTime = SystemClock.elapsedRealtime(); // TODO: handle when delta time is 0
                double speed = (double) dy / (currTime - prevTime);

                int footerVisible = getFooterVisibleLength();

                if (footerVisible == 0) {
                    footerAlreadyVisible = false;
                    isScrollBack = false;

                    if (!mSpringFooter.isAtRest()) {
                        Log.d("yolo", "rest");
                        mSpringFooter.setAtRest();
                    }
                    minDistanceToScrollBack = 0;
                    prevTime = currTime;
                    return;
                }

                if (isScrollBack) {
                    prevTime = currTime;
                    return;
                }

//                Log.d("yolo", "onScroll, alreadyVisible: " + footerAlreadyVisible +
//                        ", footerVisible: " + footerVisible + ", minDistToScrollBack: " + minDistanceToScrollBack);

                if (!footerAlreadyVisible) {
                    // check if it's already exceeded the distance to scroll back
                    minDistanceToScrollBack = getMinDistanceToScrollBack(speed);
                    footerAlreadyVisible = true;

                    Log.d("yolo", "speed: " + format(speed) + ", totalSize: " + FOOTER_SIZE
                          + ", minDist: " + format(pxToDp(minDistanceToScrollBack))
                            + ", currVisible: " + format(pxToDp(footerVisible)));

                    // scroll back
                    if (footerVisible >= minDistanceToScrollBack) {
                        Log.d("yolo", "setLength 1: " + footerVisible);
                        scrollBack(footerVisible);
                    } else {
                        reduceScrollSpeed(footerVisible, speed);
                    }
                } else if (footerVisible >= minDistanceToScrollBack) {
                    Log.d("yolo", "setLength 2: " + footerVisible);
                    // scroll back
                    scrollBack(footerVisible);
                }

                prevTime = currTime;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                Log.d("yolo", "newState: " + toStringScrollState(newState));

                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:

                        break;

                    case RecyclerView.SCROLL_STATE_SETTLING:
//                        if (!isScrollBack) {
//                            prevTime = SystemClock.elapsedRealtime();
//                        }

                        break;
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shouldUseSpring = false;

                        if (!mSpringFooter.isAtRest()) {
                            Log.d("yolo", "rest");
                            mSpringFooter.setAtRest();
                        }
                        isScrollBack = false;
                        rv.stopScroll();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        int footerVisible = getFooterVisibleLength();
                        if (footerVisible > 0) {
                            Log.d("yolo", "setLength onIntercept");
                            scrollBack(footerVisible);
                        }

                        shouldUseSpring = true;
                        break;
                }

                mGestureDetector.onTouchEvent(e);

                if (lockScroll)
                    return true;

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                mGestureDetector.onTouchEvent(e);

                switch (e.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        lockScroll = false;

                        int footerVisible = getFooterVisibleLength();
                        if (footerVisible > 0) {
                            Log.d("yolo", "setLength onIntercept");
                            scrollBack(footerVisible);
                        }

                        shouldUseSpring = true;
                        break;
                }
            }
        });
    }

    private final Object lockSpring = new Object();

    private void scrollBack(int footerVisible) {
        synchronized (lockSpring) {
            isScrollBack = true;
            isFirstValue = true;

            mRecyclerView.stopScroll();
            mSpringFooter.setCurrentValue(footerVisible);
            mSpringFooter.setEndValue(0);
        }
    }

    private int getMinDistanceToScrollBack(double speed) {
        return (int) Math.min((footerSizePx / SPEED_FACTOR * pxToDp(speed)), footerSizePx);

    }

    private void reduceScrollSpeed(int currentVisible, double speed) {
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

    private boolean isFirstValue = true;
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

//                    Log.d("yolo", "spring length: " + format(springLength) +
//                            ", visible length: " + visibleLength + ", diff: " + format(diff));

                    if (diff < 0) {
                        // discard the first value
                        if (isFirstValue) {
                            isFirstValue = false;
                            return;
                        }

                        mRecyclerView.stopScroll();
                        mRecyclerView.scrollBy(0, (int) diff);
                    }

                }
            }

            @Override
            public void onSpringAtRest(Spring spring) {
//                Log.d("yolo", "onSpringRest");
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

    private final GestureDetectorCompat mGestureDetector = new GestureDetectorCompat(mContext,
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                    Log.d("yolo", "dy: " + distanceY);

                    int footerVisible = getFooterVisibleLength();
                    lockScroll = footerVisible > 0 && distanceY > 0;

//                    Log.d("yolo", "lockscroll: " + lockScroll);
                    if (lockScroll) {
                        double ratioVisible = (double) footerVisible / footerSizePx;
                        double scrollDist =  Math.max(0, distanceY - distanceY * ratioVisible);

                        mRecyclerView.scrollBy(0, (int) scrollDist);
                    }

                    return true;
                }


            });

    private final RecyclerView.ItemDecoration mItemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        }
    };

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



