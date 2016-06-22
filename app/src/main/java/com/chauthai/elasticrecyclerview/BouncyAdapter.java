package com.chauthai.elasticrecyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.Locale;

/**
 * Created by Chau Thai on 5/17/16.
 */
@SuppressWarnings("FieldCanBeLocal")
public class BouncyAdapter extends RecyclerView.Adapter implements SpringScroller.SpringScrollerListener {
    private static final double DEFAULT_SPEED_FACTOR = 5;
    private static final int DEFAULT_GAP_LIMIT = 300; // dp
    private static final int GAP_SIZE = 1000; // dp

    private static final int VIEW_COUNT_ESTIMATE_SIZE = 5;
    private static final int MAX_ADAPTER_SIZE_TO_ESTIMATE = 20;

    private static final int VIEW_TYPE_HEADER = 1111;
    private static final int VIEW_TYPE_FOOTER = 2222;

    private int mGapLimitPx;
    private int mGapLimitDp = DEFAULT_GAP_LIMIT;
    private double mSpeedFactor = DEFAULT_SPEED_FACTOR;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private final View mFooterView;
    private final View mHeaderView;

    private final DecelerateSmoothScroller mScroller;
    private final SpringScroller mSpringScroller;

    private long mPrevTime = SystemClock.elapsedRealtime();
    private double mSpeed = 0;
    private int mPrevFooterVisible = 0;

    private boolean mIsScrollBack = false;
    private int minDistanceToScrollBack = 1;
    private boolean mShouldUseSpring = false;

    private final Handler mHandlerUI = new Handler(Looper.getMainLooper());

    public BouncyAdapter(Context context, final RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        if (recyclerView == null)
            throw new RuntimeException("null RecyclerView");

        if (adapter == null)
            throw new RuntimeException(("null adapter"));

        if (!(recyclerView.getLayoutManager() instanceof LinearLayoutManager))
            throw new RuntimeException("RecyclerView must use LinearLayoutManager");

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("yolo", "height: " + recyclerView.getHeight());
            }
        });

        mContext = context;
        mAdapter = adapter;
        mRecyclerView = recyclerView;
        mGapLimitPx = (int) dpToPx(mGapLimitDp);

        mFooterView = createGapView();
        mHeaderView = createGapView();

        mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mScroller = new DecelerateSmoothScroller(context);
        mSpringScroller = new SpringScroller(this);

        initRecyclerView();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER)
            return new HeaderHolder(mHeaderView);

        if (viewType == VIEW_TYPE_FOOTER)
            return new FooterHolder(mFooterView);

        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position != 0 && position != getItemCount() - 1) {
            mAdapter.onBindViewHolder(holder, position - 1);
        }
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_HEADER;

        if (position == getItemCount() - 1)
            return VIEW_TYPE_FOOTER;

        return mAdapter.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mAdapter.onAttachedToRecyclerView(recyclerView);
    }

    private boolean isSpringFirstValue = true;

    @Override
    public void onUpdate(int currX, int currY) {
        if (!mShouldUseSpring)
            return;

        synchronized (lockSpring) {
            final int visibleHeader = getHeaderVisibleLength();
            final int visibleFooter = getFooterVisibleLength();

            int diff = currY;

            if (visibleHeader > 0) {
                diff -= visibleHeader;
            } else {
                diff -= visibleFooter;
            }

//            Log.d("yolo", "diff: " + diff + ", footer: " + visibleFooter + ", currY: " + currY);

            if (diff < 0) {
                // discard the first value
                if (isSpringFirstValue) {
                    isSpringFirstValue = false;
                    return;
                }

                if (!mFlingOverScrollBack) {
                    mRecyclerView.stopScroll();
                }

                if (visibleHeader > 0) {
                    diff *= -1;
                }

                if (diff != 0) {
//                    mPrevFooterVisible = Math.max(0, mPrevFooterVisible + diff);
                    mRecyclerView.scrollBy(0, diff);
                }
            }
        }
    }

    @Override
    public void onAtRest() {
        mIsScrollBack = false;
    }

    private void initRecyclerView() {
        mRecyclerView.scrollToPosition(1);
        initOnScrollListener();
        initTouchListener();
    }

    private void initOnScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean gapAlreadyVisible = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (footerOccupiesWholeView()) {
                    mPrevFooterVisible = Math.max(0, mPrevFooterVisible + dy);
                }

                final int state = recyclerView.getScrollState();
                final boolean usingScrollBy = (state == RecyclerView.SCROLL_STATE_IDLE && dy != 0);
                final boolean isDragging = (state == RecyclerView.SCROLL_STATE_DRAGGING);

//                Log.d("yolo", "header: " + getHeaderVisibleLength() + ", footer: " + getFooterVisibleLength()
//                 + ", isScrollBack: " + mIsScrollBack + ", isDragging: " + isDragging + ", usingScrollBy: " + usingScrollBy
//                 + ", minDistStop: " + minDistanceToScrollBack);

                computeScrollSpeed(dy);

                if (!isDragging && !usingScrollBy) {
                    final int footerVisible = getFooterVisibleLength();
                    final int headerVisible = getHeaderVisibleLength();

                    final boolean scrolledBackToOtherSide = mIsScrollBack && ((dy > 0 && footerVisible > 0)
                            || (dy < 0 && headerVisible > 0));

                    if (scrolledBackToOtherSide) {
                        gapAlreadyVisible = true;
                        mIsScrollBack = false;
                        mSpringScroller.stopScroll();
                        minDistanceToScrollBack = getMinDistanceToScrollBack(mSpeed, headerVisible, footerVisible);
                    }

                    if (footerVisible == 0 && headerVisible == 0) {
                        gapAlreadyVisible = false;
                        mIsScrollBack = false;

                        mSpringScroller.stopScroll();
                        minDistanceToScrollBack = 1;

                    } else if (!mIsScrollBack) {
                        if (!gapAlreadyVisible) {
                            // check if it's already exceeded the distance to scroll back
                            minDistanceToScrollBack = getMinDistanceToScrollBack(mSpeed, headerVisible, footerVisible);
                            gapAlreadyVisible = true;

                            // scroll back
                            if (headerVisible >= minDistanceToScrollBack || footerVisible >= minDistanceToScrollBack) {
                                Log.d("yolo", "scrollBack 1");
                                scrollBack(headerVisible, footerVisible);
                            } else {
                                Log.d("yolo", "reduce speed onScrolled");
                                reduceScrollSpeed(mSpeed, headerVisible, footerVisible);
                            }
                        } else if (headerVisible >= minDistanceToScrollBack || footerVisible >= minDistanceToScrollBack) {
                            Log.d("yolo", "scrollBack 2");
                            scrollBack(headerVisible, footerVisible);
                        }
                    }
                }
            }
        });

    }

    private void initTouchListener() {
        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPrevTime = SystemClock.elapsedRealtime();
                        mShouldUseSpring = false;

                        mSpringScroller.stopScroll();
                        mIsScrollBack = false;
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
                return shouldInterceptTouch();
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

    private void onActionUp() {
        final int footerVisible = getFooterVisibleLength();
        final int headerVisible = getHeaderVisibleLength();
        final boolean overScrolled = (footerVisible > 0 || headerVisible > 0);

        if (overScrolled) {
            minDistanceToScrollBack = getMinDistanceToScrollBack(mSpeed, headerVisible, footerVisible);
            boolean reduceHeaderSpeed = (headerVisible > 0) && (headerVisible < minDistanceToScrollBack);
            boolean reduceFooterSpeed = (footerVisible > 0) && (footerVisible < minDistanceToScrollBack);

            if (reduceHeaderSpeed || reduceFooterSpeed) {
                Log.d("yolo", "reduce speed onActionUp");
                reduceScrollSpeed(mSpeed, headerVisible, footerVisible);
            } else {
                Log.d("yolo", "scrollBack onActionUp");
                scrollBack(headerVisible, footerVisible);
            }
        }

        mShouldUseSpring = true;
    }

    private boolean shouldInterceptTouch() {
        final int headerVisible = getHeaderVisibleLength();
        final int footerVisible = getFooterVisibleLength();

        return headerVisible > 0 || footerVisible > 0;
    }

    private void computeScrollSpeed(int dy) {
        long currTime = SystemClock.elapsedRealtime();

        if (mFirstScrollBy) {
            mFirstScrollBy = false;

            if (dy > 0)
                dy = getFooterVisibleLength();
            else if (dy < 0)
                dy = getHeaderVisibleLength();
        }

        mSpeed = (double) dy / (currTime - mPrevTime);
        mPrevTime = currTime;
    }

    private final Object lockSpring = new Object();

    private void scrollBack(int headerVisible, int footerVisible) {
        synchronized (lockSpring) {
            mIsScrollBack = true;
            isSpringFirstValue = true;

            mRecyclerView.stopScroll();

            if (headerVisible > 0) {
                mSpringScroller.startScroll(0, headerVisible);
            } else {
                mSpringScroller.startScroll(0, footerVisible);
            }
        }
    }

    /**
     * Compute the over scroll threshold to scroll back.
     * @param speed px per ms
     * @return pixels
     */
    private int getMinDistanceToScrollBack(double speed, int headerVisible, int footerVisible) {
        if (headerVisible > 0) {
            if (speed >= 0)
                return 0;

            return (int) Math.min((mGapLimitPx / mSpeedFactor * pxToDp(-speed)), mGapLimitPx);
        }

        if (footerVisible == 0 || speed <= 0)
            return 0;

        return (int) Math.min((mGapLimitPx / mSpeedFactor * pxToDp(speed)), mGapLimitPx);
    }

    private void reduceScrollSpeed(double speed, int headerVisible, int footerVisible) {
        mRecyclerView.stopScroll();
        int distToStop = minDistanceToScrollBack;

        if (headerVisible > 0) {
            mScroller.setScrollVector(new PointF(0, -1));
            mScroller.setTargetPosition(0);
        } else {
            mScroller.setScrollVector(new PointF(0, 1));
            mScroller.setTargetPosition(getItemCount() - 1);
        }

        mScroller.setDistanceToStop(distToStop);
        mScroller.setInitialSpeed((float) Math.abs(speed));
        mLayoutManager.startSmoothScroll(mScroller);
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

    private boolean mFirstScrollBy = false;
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
                    final int headerVisible = getHeaderVisibleLength();
                    final int footerVisible = getFooterVisibleLength();

                    int visible = (headerVisible > 0)? headerVisible : footerVisible;

                    if (visible > 0) {
                        scrollByCount++;
                        mFirstScrollBy = (scrollByCount == 1);

                        double ratioVisible = (double) visible / mGapLimitPx;
                        double scrollDist = Math.abs(distanceY - distanceY * ratioVisible);

                        if (distanceY < 0) {
                            scrollDist *= -1;
                        }

                        mRecyclerView.scrollBy(0, (int) scrollDist);
                    }

                    // still in onTouchEvent, manually scroll the recycler view.
                    else if (visible == 0 && !mGestureOnIntercept) {
                        mRecyclerView.scrollBy((int) distanceX, (int) distanceY);
                    }

                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, final float velocityY) {
                    final int headerVisible = getHeaderVisibleLength();
                    final int footerVisible = getFooterVisibleLength();

                    final boolean gapVisible = headerVisible > 0 || footerVisible > 0;
                    final boolean isFlingOverBack = gapVisible && !mGestureOnIntercept &&
                            ((headerVisible > 0 && velocityY < 0) || (footerVisible > 0 && velocityY > 0));

//                    Log.d("yolo", "gapVisible: " + gapVisible + ", isFlingOverBack: " + isFlingOverBack);

                    // gaps are not visible, use regular fling.
                    if (!gapVisible && !mGestureOnIntercept) {
                        mHandlerUI.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("yolo", "fling1: " + (int) -velocityY);
                                mRecyclerView.fling((int) -velocityX, (int) -velocityY);
                            }
                        });
                    }

                    // gap is visible, only fling if it's fling back.
                    else if (isFlingOverBack) {
                        mHandlerUI.post(new Runnable() {
                            @Override
                            public void run() {
                                mFlingOverScrollBack = true;
                                Log.d("yolo", "fling2: " + (int) -velocityY);
                                mRecyclerView.fling((int) -velocityX, (int) -velocityY);
                            }
                        });
                    }

                    return true;
                }
            });

    private int contentHeightLessThanView() {
        return Math.max(mRecyclerView.getHeight() - estimateContentHeight(), 0);
    }

    private int estimateContentHeight() {
        int total = 0;
        int count = 0;

        for (int i = mAdapter.getItemCount() - 1; i >= 0 && count < VIEW_COUNT_ESTIMATE_SIZE; i--) {
            View view = mLayoutManager.findViewByPosition(i + 1);

            if (view != null) {
                Rect rect = new Rect();
                mLayoutManager.getDecoratedBoundsWithMargins(view, rect);
                int itemHeight = Math.abs(rect.height());

                count++;
                total += itemHeight;
            }
        }


        if (count > 0) {
            double average = (double) total / count;
            return (int) (average * mAdapter.getItemCount());
        }

        return 0;
    }

    private int getFooterVisibleLength() {
        if (footerOccupiesWholeView()) {
            return mPrevFooterVisible;
        }

        // footer is not visible
        if (mLayoutManager.findLastVisibleItemPosition() != getItemCount() - 1) {
            mPrevFooterVisible = 0;
            return 0;
        }

        int result = mRecyclerView.getHeight() - mFooterView.getTop() - mRecyclerView.getPaddingBottom();

        if (mAdapter.getItemCount() <= MAX_ADAPTER_SIZE_TO_ESTIMATE) {
            result -= contentHeightLessThanView();
        }

        result = Math.max(0, result);
        mPrevFooterVisible = result;
        return result;
    }

    private int getHeaderVisibleLength() {
        // header is not visible
        if (mLayoutManager.findFirstVisibleItemPosition() != 0)
            return 0;

        return Math.max(0, mHeaderView.getBottom() - mRecyclerView.getPaddingTop());
    }

    private boolean footerOccupiesWholeView() {
        return (mAdapter.getItemCount() > 0 && mRecyclerView.findChildViewUnder(0,0) == mFooterView);
    }

    private View createGapView() {
        View view = new View(mContext);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                (int) dpToPx(GAP_SIZE)));
        view.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_green_light));
        return view;
    }

    private class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(View v) {
            super(v);
        }
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {
        public HeaderHolder(View v) {
            super(v);
        }
    }

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



