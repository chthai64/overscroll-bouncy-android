package com.chauthai.overscrolldemo;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by Chau Thai on 5/4/16.
 */
public class MyAdapter extends RecyclerView.Adapter {
    private List<InboxItem> mDataSet;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final Transformation avatarTransformation;

    public MyAdapter(Context context, List<InboxItem> dataSet) {
        mDataSet = dataSet;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        avatarTransformation = ImageUtil.getCircleTransformation(1, ContextCompat.getColor(context, R.color.gray));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.inbox_row, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (mDataSet != null && position < mDataSet.size()) {
            InboxItem item = mDataSet.get(position);

            Holder holder = (Holder) viewHolder;
            holder.primaryText.setText(item.getTitle());
            holder.secondaryText.setText(item.getContent());

            // Set avatar thumbnail
            Picasso.with(mContext)
                    .load(item.getAvatarResId())
                    .transform(avatarTransformation)
                    .into(holder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null)
            return 0;
        return mDataSet.size();
    }

    private class Holder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView primaryText;
        TextView secondaryText;

        public Holder(View view) {
            super(view);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            primaryText = (TextView) view.findViewById(R.id.primary_text);
            secondaryText = (TextView) view.findViewById(R.id.secondary_text);
        }
    }
}
