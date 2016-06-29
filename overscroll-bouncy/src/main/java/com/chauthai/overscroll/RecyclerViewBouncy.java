/**
 The MIT License (MIT)

 Copyright (c) 2016 Chau Thai

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

package com.chauthai.overscroll;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * A RecyclerView which supports iOS-like over-scroll style.
 */
public class RecyclerViewBouncy extends RecyclerView {
    private BouncyAdapter mBouncyAdapter;
    private Adapter mOriginalAdapter;
    private BouncyConfig mConfig = BouncyConfig.DEFAULT;

    public RecyclerViewBouncy(Context context) {
        super(context);
        init(context, null);
    }

    public RecyclerViewBouncy(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RecyclerViewBouncy(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mOriginalAdapter != null) {
            mOriginalAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        }

        // wrap the original adapter inside the BouncyAdapter
        mOriginalAdapter = adapter;
        mBouncyAdapter = new BouncyAdapter(getContext(), this, adapter, mConfig);

        super.setAdapter(mBouncyAdapter);
        adapter.registerAdapterDataObserver(mAdapterDataObserver);
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        setAdapter(adapter);
    }

    /**
     * @param layout only supports LinearLayoutManager
     */
    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (!(layout instanceof LinearLayoutManager)) {
            throw new RuntimeException("RecyclerView must use LinearLayoutManager");
        }

        super.setLayoutManager(layout);
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position + 1);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(position + 1);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (context != null && attributeSet != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attributeSet,
                    R.styleable.RecyclerViewBouncy,
                    0, 0
            );

            BouncyConfig.Builder builder = new BouncyConfig.Builder();

            if (a.hasValue(R.styleable.RecyclerViewBouncy_tension)) {
                builder.setTension(a.getInteger(R.styleable.RecyclerViewBouncy_tension, 0));
            }

            if (a.hasValue(R.styleable.RecyclerViewBouncy_friction)) {
                builder.setFriction(a.getInteger(R.styleable.RecyclerViewBouncy_friction, 0));
            }

            if (a.hasValue(R.styleable.RecyclerViewBouncy_gapLimit)) {
                builder.setGapLimit(a.getInteger(R.styleable.RecyclerViewBouncy_gapLimit, 0));
            }

            if (a.hasValue(R.styleable.RecyclerViewBouncy_speedFactor)) {
                builder.setSpeedFactor(a.getInteger(R.styleable.RecyclerViewBouncy_speedFactor, 0));
            }

            if (a.hasValue(R.styleable.RecyclerViewBouncy_viewCountEstimateSize)) {
                builder.setViewCountEstimateSize(a.getInteger(
                        R.styleable.RecyclerViewBouncy_viewCountEstimateSize, 0));
            }

            if (a.hasValue(R.styleable.RecyclerViewBouncy_maxAdapterSizeToEstimate)) {
                builder.setMaxAdapterSizeToEstimate(a.getInteger(
                        R.styleable.RecyclerViewBouncy_maxAdapterSizeToEstimate, 0));
            }

            mConfig = builder.build();
        }
    }

    private final AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mBouncyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mBouncyAdapter.notifyItemRangeChanged(positionStart + 1, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mBouncyAdapter.notifyItemRangeChanged(positionStart + 1, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mBouncyAdapter.notifyItemRangeInserted(positionStart + 1, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mBouncyAdapter.notifyItemRangeRemoved(positionStart + 1, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mBouncyAdapter.notifyItemMoved(fromPosition + 1, toPosition + 1);
        }
    };
}
