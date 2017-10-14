package com.tools.security.applock.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.CommLockInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/1/23.
 */

public class LockSuccessAdapter extends RecyclerView.Adapter<LockSuccessAdapter.LockSuccessViewHolder> {

    private List<CommLockInfo> mList;
    private PackageManager packageManager;

    public LockSuccessAdapter(Context context) {
        mList = new ArrayList<>();
        packageManager = context.getPackageManager();
    }

    public void setList(List<CommLockInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public LockSuccessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lock_success, parent, false);
        return new LockSuccessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LockSuccessViewHolder holder, int position) {
        CommLockInfo info = mList.get(position);
        holder.mAppLabel.setText(packageManager.getApplicationLabel(info.getAppInfo()));
        ApplicationInfo appInfo = info.getAppInfo();
        if (appInfo != null) {
            holder.mImageIcon.setImageDrawable(packageManager.getApplicationIcon(appInfo));
        }
        holder.mLine.setVisibility(position == mList.size() - 1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class LockSuccessViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageIcon;
        public TextView mAppLabel;
        public View mLine;

        public LockSuccessViewHolder(View itemView) {
            super(itemView);
            mImageIcon = (ImageView) itemView.findViewById(R.id.img_icon);
            mAppLabel = (TextView) itemView.findViewById(R.id.tv_app_title);
            mLine = itemView.findViewById(R.id.line);
        }
    }
}