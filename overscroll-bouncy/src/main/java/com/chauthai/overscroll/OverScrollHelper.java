package com.chauthai.overscroll;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Chau Thai on 6/22/16.
 */
public class OverScrollHelper {
    private final RecyclerView mRecyclerView;
    private final Context mContext;
    private final BouncyConfig mConfig;
    private BouncyAdapter mBouncyAdapter;


    private OverScrollHelper(Context context, RecyclerView recyclerView, BouncyConfig config) {
        mContext = context;
        mRecyclerView = recyclerView;
        mConfig = config;
    }

    public void bindAdapter(RecyclerView.Adapter adapter) {
        mBouncyAdapter = new BouncyAdapter(mContext, mRecyclerView, adapter, mConfig);
        mRecyclerView.setAdapter(mBouncyAdapter);
    }

    public void scrollToPosition(int position) {
        mRecyclerView.scrollToPosition(position + 1);
    }

    public void smoothScrollToPosition(int position) {
        mRecyclerView.smoothScrollToPosition(position + 1);
    }
}
