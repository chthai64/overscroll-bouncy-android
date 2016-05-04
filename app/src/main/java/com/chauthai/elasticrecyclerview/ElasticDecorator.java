package com.chauthai.elasticrecyclerview;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * Created by Chau Thai on 5/4/16.
 */
public class ElasticDecorator extends RecyclerView.ItemDecoration {
    private static final int MAX_PADDING = 50;
    private int topPadding = 0;
    private RecyclerView mRecyclerView;

    private static final int MIN_BOUNCE_THRESHOLD = 2;
    private int mPrevScrollState = RecyclerView.SCROLL_STATE_IDLE;
    private int mLastScrollValue = 0;

    int offset = 0;


    public ElasticDecorator(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mLastScrollValue = dy;
                offset += dy;

                Log.d("yolo", "scroll: " + recyclerView.computeVerticalScrollOffset() +
                        ", offset: " + offset);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                boolean shouldBounce = (newState == RecyclerView.SCROLL_STATE_IDLE) &&
//                        (mPrevScrollState == RecyclerView.SCROLL_STATE_SETTLING) &&
//                        (pxToDp(Math.abs(mLastScrollValue)) >= MIN_BOUNCE_THRESHOLD);
//
//                Log.d("yolo", getStateText(mPrevScrollState));
//
//                if (shouldBounce) {
//                    Log.d("yolo", "bounce");
//                }

                mPrevScrollState = newState;
            }
        });


        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
//        topPadding++;
//            parent.invalidateItemDecorations();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);

        outRect.top = topPadding;

//        Log.d("yolo", "getItemOffsets, position: " + parent.getChildAdapterPosition(view));
    }

    private String getStateText(int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
                return "idle";
            case RecyclerView.SCROLL_STATE_DRAGGING:
                return "dragging";
            case RecyclerView.SCROLL_STATE_SETTLING:
                return "settling";
        }

        return state + "";
    }

    private int pxToDp(int px) {
        Resources resources = mRecyclerView.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
