package com.chauthai.elasticrecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Chau Thai on 5/4/16.
 */
public class ElasticAdapter extends RecyclerView.Adapter {
    private static final int MAX_SPRING_LENGTH = 20;
    private static final double TENSION = 100;
    private static final double FRICTION = 50;

    private final SpringSystem mSpringSystem = SpringSystem.create();
    private final SpringConfig mSpringConfig = new SpringConfig(TENSION, FRICTION);

    private LinearLayoutManager layoutManager;
    private final RecyclerView mRecyclerView;
    private final Context mContext;

    private final Map<Integer, Spring> mMapSpring = new HashMap<>(); // adapter position -> spring1

    private List<String> mDataSet;
    private LayoutInflater mInflater;

    private int mDragPosition = 0;
    private Spring spring;

    public ElasticAdapter(final RecyclerView recyclerView, Context context, List<String> dataSet) {
        mContext = context;
        mRecyclerView = recyclerView;
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);

        spring = createSpring();
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                // do something here
//                offset = (float) spring1.getCurrentValue();
//                Log.d("yolo", "offset: " + format(offset));
//                mRecyclerView.invalidateItemDecorations();
            }
        });

        layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
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

            // setup spring1
//            if (!mMapSpring.containsKey(position)) {
//                Spring spring1 = createSpring();
//                spring1.addListener(new SimpleSpringListener() {
//                    @Override
//                    public void onSpringUpdate(Spring spring1) {
//                        mRecyclerView.invalidateItemDecorations();
//                    }
//                });
//
//                mMapSpring.put(position, spring1);
//            }
        }
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null)
            return 0;
        return mDataSet.size();
    }

    boolean scrolling = false;

    private void setupRecyclerView() {
        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(mRecyclerView.getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        mDragPosition = getDragPosition(e);
//                        offset = 0;
//                        mRecyclerView.invalidateItemDecorations();
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        scrolling = true;
//                        double currValue = Math.max(0, offset + distanceY);
//                        double prevVel = spring1.getVelocity();
//
//                        spring1.setCurrentValue(currValue);
//                        if (currValue > 0)
//                            spring1.setVelocity(prevVel);
//                        spring1.setEndValue(0);

//                        offset = (float) currValue;
//                        mRecyclerView.invalidateItemDecorations();

                        return true;
                    }


                });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        View child = getClosestChild(rv, e);
//                        mDragPosition = rv.getChildAdapterPosition(child);

                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        scrolling = false;
                        break;
                }

                gestureDetector.onTouchEvent(e);
                return false;
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (scrolling) {
//                    double currValue = Math.max(0, spring1.getCurrentValue() + dy);
//                    double prevVel = spring1.getVelocity();
//
//                    spring1.setCurrentValue(currValue);
//                    spring1.setVelocity(prevVel);
//                    spring1.setEndValue(0);

//                    View lastView = layoutManager.findViewByPosition(mDataSet.size() - 1);
//                    if (lastView != null) {
//                        Log.d("yolo", "lastView top: " + lastView.getTop());
//                    }
                }

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

    private int getDragPosition(MotionEvent e) {
        View child = getClosestChild(mRecyclerView, e);
        return mRecyclerView.getChildAdapterPosition(child);
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

    private float offset = 0.0f;

    private final RecyclerView.ItemDecoration mItemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int adapterPosition = mRecyclerView.getChildAdapterPosition(view);

            if (adapterPosition == mDragPosition) {
                outRect.bottom = (int) offset;
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

    private String format(double value) {
        return String.format(Locale.US, "%1$,.2f", value);
    }
}
