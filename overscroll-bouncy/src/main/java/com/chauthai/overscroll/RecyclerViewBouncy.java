package com.chauthai.overscroll;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Chau Thai on 6/27/16.
 */
public class RecyclerViewBouncy extends RecyclerView {
    private BouncyAdapter mBouncyAdapter;
    private Adapter mOriginalAdapter;

    public RecyclerViewBouncy(Context context) {
        super(context);
    }

    public RecyclerViewBouncy(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewBouncy(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mOriginalAdapter != null) {
            mOriginalAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        }

        mOriginalAdapter = adapter;
        mBouncyAdapter = new BouncyAdapter(getContext(), this, adapter, BouncyConfig.DEFAULT);

        super.setAdapter(mBouncyAdapter);
        adapter.registerAdapterDataObserver(mAdapterDataObserver);
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        setAdapter(adapter);
    }

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
