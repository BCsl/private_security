package com.tools.security.wifi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/13.
 */

public class WifiDevicesAdapter extends RecyclerView.Adapter {


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class NormalViewHolder extends RecyclerView.ViewHolder {
        public NormalViewHolder(View itemView) {
            super(itemView);
        }
    }
}
