package com.chauthai.elasticrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Chau Thai on 6/22/16.
 */
public class OverScrollHelper {
    private RecyclerView mRecyclerView;
    private Context mContext;
    private BouncyAdapter mBouncyAdapter;

    public OverScrollHelper(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
    }

    public void bindAdapter(RecyclerView.Adapter adapter) {
        mBouncyAdapter = new BouncyAdapter(mContext, mRecyclerView, adapter);
        mRecyclerView.setAdapter(mBouncyAdapter);
    }
}
