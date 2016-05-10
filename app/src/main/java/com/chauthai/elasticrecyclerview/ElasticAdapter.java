package com.chauthai.elasticrecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chau Thai on 5/4/16.
 */
public class ElasticAdapter extends RecyclerView.Adapter {
    private static final int MAX_SPRING_LENGTH = 20;
    private static final double TENSION = 100;
    private static final double FRICTION = 20;

    private final SpringSystem mSpringSystem = SpringSystem.create();
    private final SpringConfig mSpringConfig = new SpringConfig(TENSION, FRICTION);
    private final RecyclerView mRecyclerView;

    private final Map<Integer, Spring> mMapSpring = new HashMap<>(); // adapter position -> spring

    private List<String> mDataSet;
    private LayoutInflater mInflater;

    private int mDragPosition = 0;

    public ElasticAdapter(RecyclerView recyclerView, Context context, List<String> dataSet) {
        mRecyclerView = recyclerView;
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);
        setupRecyclerView();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.row, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (mDataSet != null && position < mDataSet.size()) {
            Holder holder = (Holder) viewHolder;
            holder.textView.setText(mDataSet.get(position));

            // setup spring
            if (!mMapSpring.containsKey(position)) {
                Spring spring = createSpring();
                spring.addListener(new SimpleSpringListener() {
                    @Override
                    public void onSpringUpdate(Spring spring) {
                        mRecyclerView.invalidateItemDecorations();
                    }
                });

                mMapSpring.put(position, spring);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null)
            return 0;
        return mDataSet.size();
    }

    private void setupRecyclerView() {
        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        View child = getClosestChild(rv, e);
                        mDragPosition = rv.getChildAdapterPosition(child);
                        break;
                }

                return false;
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }
        });

        mRecyclerView.addItemDecoration(mItemDecoration);
    }

    private Spring createSpring() {
        final Spring spring = mSpringSystem.createSpring();
        spring.setSpringConfig(mSpringConfig);
        spring.setOvershootClampingEnabled(true);

        return spring;
    }

    private View getClosestChild(RecyclerView rv, MotionEvent e) {
        final float x = e.getX(0);
        final float y = e.getY(0);
        final float height = rv.getHeight();

        float lowerY = y;
        float upperY = y;

        while (lowerY >= 0 || upperY <= height) {
            if (lowerY >= 0) {
                View child = rv.findChildViewUnder(x, lowerY);
                if (child != null)
                    return child;
            }

            if (lowerY <= height) {
                View child = rv.findChildViewUnder(x, upperY);
                if (child != null)
                    return child;
            }

            lowerY--;
            upperY++;
        }

        return null;
    }

    private final RecyclerView.ItemDecoration mItemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int adapterPosition = mRecyclerView.getChildAdapterPosition(view);
            Spring springBelow = mMapSpring.get(adapterPosition - 1);

            if (springBelow != null) {
                outRect.top = (int) springBelow.getCurrentValue();
            }
        }
    };

    private class Holder extends RecyclerView.ViewHolder {
        TextView textView;

        public Holder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.text);
        }
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
}
