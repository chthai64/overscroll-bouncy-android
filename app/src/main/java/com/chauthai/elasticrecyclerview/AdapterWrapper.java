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
    private static final double TENSION = 100;
    private static final double FRICTION = 40;

    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int FOOTER_SIZE = 300; // dp

    private final SpringSystem mSpringSystem = SpringSystem.create();
    private final SpringConfig mSpringConfig = new SpringConfig(TENSION, FRICTION);

    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private View footerView;
    private Spring mSpringFooter;
    private ConstantSmoothScroller mScrollerFooter;

    public AdapterWrapper(Context context, RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        mContext = context;
        mAdapter = adapter;
        mRecyclerView = recyclerView;
        footerView = createFooterView();
        mRecyclerView.addItemDecoration(mItemDecoration);
        mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        mScrollerFooter = new ConstantSmoothScroller(context);

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

    private void setupRecyclerView() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mAdapter.getItemCount() > 0) {
                    int footerVisibleLength = getFooterVisibleLength();

//                    Log.d("yolo", "dy: " + dy);
                    if (footerVisibleLength > 0) {
                        if (dy == 0 || dy == 1) {
                            if (!isScrollBack) {
                                mSpringFooter.setCurrentValue(footerVisibleLength);
                                mSpringFooter.setEndValue(0);
                                isScrollBack = true;
                            }
                        }
                        else if (dy > 1) {
                            long delta = SystemClock.elapsedRealtime() - prevTime;
                            float speed = (float) dy / delta;
                            float visibleRatio = (float) footerVisibleLength / dpToPx(FOOTER_SIZE) * 2;
                            speed = Math.max(0, speed - speed * visibleRatio);

                            mScrollerFooter.setScrollSpeed(speed);
                            mScrollerFooter.setTargetPosition(getItemCount() - 1);
                            mScrollerFooter.setScrollVector(new PointF(0, 1));

                            mLayoutManager.startSmoothScroll(mScrollerFooter);

                            isScrollBack = false;
                            mSpringFooter.setAtRest();
                        }
                    } else {
                        mSpringFooter.setAtRest();
                    }

                    prevTime = SystemClock.elapsedRealtime();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                Log.d("yolo", "newState: " + toStringScrollState(newState));

                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        int footerVisible = getFooterVisibleLength();

                        if (footerVisible > 0 && !isScrollBack) {
                            mSpringFooter.setCurrentValue(footerVisible);
                            mSpringFooter.setEndValue(0);
                            isScrollBack = true;
                        }

                        break;
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        break;
                }

                mGestureDetector.onTouchEvent(e);
                return false;
            }
        });
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
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        View footerView = layoutManager.findViewByPosition(getItemCount() - 1);

        if (footerView != null) {
            return Math.max(0, mRecyclerView.getBottom() - footerView.getTop());
        }
        return 0;
    }

    private void initFooterSpring() {
        mSpringFooter = mSpringSystem.createSpring();
        mSpringFooter.setSpringConfig(mSpringConfig);

        mSpringFooter.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float currSpeed = Math.abs((float) mSpringFooter.getVelocity() / 1000.0f);

                mScrollerFooter.setTargetPosition(mAdapter.getItemCount() - 1);
                mScrollerFooter.setScrollVector(new PointF(0, -1));
                mScrollerFooter.forceVerticalSnap(ConstantSmoothScroller.SNAP_TO_END);
                mScrollerFooter.setScrollSpeed(currSpeed);

                mLayoutManager.startSmoothScroll(mScrollerFooter);
            }
        });
    }

    private View createFooterView() {
        View view = new View(mContext);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                dpToPx(FOOTER_SIZE)));
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
                    return true;
                }
            });

    private final RecyclerView.ItemDecoration mItemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        }
    };

    private int dpToPx(int dp) {
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private int pxToDp(int px) {
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
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



