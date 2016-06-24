package com.chauthai.elasticrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Chau Thai on 6/22/16.
 */
public class OverScrollHelper {
    private static final double DEFAULT_SPEED_FACTOR = 5;
    private static final int DEFAULT_GAP_LIMIT = 300; // dp
    private static final int VIEW_COUNT_ESTIMATE_SIZE = 5;
    private static final int MAX_ADAPTER_SIZE_TO_ESTIMATE = 20;

    private final RecyclerView mRecyclerView;
    private final Context mContext;
    private BouncyAdapter mBouncyAdapter;

    private final int mGapLimit;
    private final double mSpeedFactor ;
    private final int mTension;
    private final int mFriction;
    private final int mViewCountEstimateSize;
    private final int mMaxAdapterSizeToEstimate;

    private OverScrollHelper(
            Context context,
            RecyclerView recyclerView,
            int gapLimit,
            double speedFactor,
            int viewCountToEstimateSize,
            int maxAdapterSizeToEstimate,
            int friction,
            int tension)
    {
        mContext = context;
        mRecyclerView = recyclerView;
        mGapLimit = gapLimit;
        mSpeedFactor = speedFactor;
        mViewCountEstimateSize = viewCountToEstimateSize;
        mMaxAdapterSizeToEstimate = maxAdapterSizeToEstimate;
        mFriction = friction;
        mTension = tension;
    }

    public void bindAdapter(RecyclerView.Adapter adapter) {
        mBouncyAdapter = new BouncyAdapter.Builder(mContext, mRecyclerView, adapter)
                .setGapLimit(mGapLimit)
                .setSpeedFactor(mSpeedFactor)
                .setSpringConfig(mTension, mFriction)
                .setViewCountEstimateSize(mViewCountEstimateSize)
                .setMaxAdapterSizeToEstimate(mMaxAdapterSizeToEstimate)
                .build();

        mRecyclerView.setAdapter(mBouncyAdapter);
    }

    public static class Builder {
        // required
        private Context nestedContext;
        private RecyclerView nestedRecyclerView;

        // optional
        private int nestedGapLimit = DEFAULT_GAP_LIMIT;
        private double nestedSpeedFactor = DEFAULT_SPEED_FACTOR;
        private int nestedTension = -1;
        private int nestedFriction = -1;
        private int nestedViewCountEstimateSize = VIEW_COUNT_ESTIMATE_SIZE;
        private int nestedMaxAdapterSizeToEstimate = MAX_ADAPTER_SIZE_TO_ESTIMATE;

        public Builder(Context context, RecyclerView recyclerView) {
            nestedContext = context;
            nestedRecyclerView = recyclerView;
        }

        public Builder setGapLimit(int gapLimit) {
            nestedGapLimit = gapLimit;
            return this;
        }

        public Builder setSpeedFactor(double speedFactor) {
            nestedSpeedFactor = speedFactor;
            return this;
        }

        public Builder setSpringConfig(int tension, int friction) {
            nestedTension = tension;
            nestedFriction = friction;
            return this;
        }

        public Builder setViewCountEstimateSize(int count) {
            nestedViewCountEstimateSize = count;
            return this;
        }

        public Builder setMaxAdapterSizeToEstimate(int size) {
            nestedMaxAdapterSizeToEstimate = size;
            return this;
        }

        public OverScrollHelper build() {
            return new OverScrollHelper(
                    nestedContext,
                    nestedRecyclerView,
                    nestedGapLimit,
                    nestedSpeedFactor,
                    nestedViewCountEstimateSize,
                    nestedMaxAdapterSizeToEstimate,
                    nestedFriction,
                    nestedTension
            );
        }
    }
}
