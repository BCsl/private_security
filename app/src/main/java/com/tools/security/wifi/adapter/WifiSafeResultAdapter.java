package com.tools.security.wifi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tools.security.R;

/**
 * description:wifi安全结果页adapter
 * author: xiaodifu
 * date: 2017/1/15.
 */

public class WifiSafeResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TRANSPARENT = 1;
    private static final int VIEW_TYPE_NORMAL = 2;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TRANSPARENT) {
            return new TransViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_transparent, parent, false));
        }
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_safe_result, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_NORMAL) {
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_TRANSPARENT;
        }
        return VIEW_TYPE_NORMAL;
    }


    @Override
    public int getItemCount() {
        // TODO: 2017/1/16 记得加一
        return 15;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    class TransViewHolder extends RecyclerView.ViewHolder {
        public TransViewHolder(View itemView) {
            super(itemView);
        }
    }
}
