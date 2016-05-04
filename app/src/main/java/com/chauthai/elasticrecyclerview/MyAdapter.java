package com.chauthai.elasticrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Chau Thai on 5/4/16.
 */
public class MyAdapter extends RecyclerView.Adapter {
    private List<String> mDataSet;
    private LayoutInflater mInflater;

    public MyAdapter(Context context, List<String> dataSet) {
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);
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
        }
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null)
            return 0;
        return mDataSet.size();
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView textView;

        public Holder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.text);
        }
    }
}
