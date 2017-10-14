package com.tools.security.applock.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.CommLockInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/1/6.
 */

public class LockFirstAdapter extends RecyclerView.Adapter<LockFirstAdapter.LockFirstViewHolder> {

    private List<CommLockInfo> mLockInfos;
    // private List<CommLockInfo> mCopyLockInfos;
    private Context mContext;
    private PackageManager packageManager;
    private OnItemClickListener mOnItemClickListener;

    public LockFirstAdapter(Context context) {
        mContext = context;
        packageManager = context.getPackageManager();
        mLockInfos = new ArrayList<>();
        //    mCopyLockInfos = new ArrayList<>();
    }

    public void setLockInfos(List<CommLockInfo> lockInfos) {
        mLockInfos.clear();
        mLockInfos.addAll(lockInfos);
//        mCopyLockInfos.clear();
//        mCopyLockInfos.addAll(lockInfos);
        notifyDataSetChanged();
    }


    @Override
    public LockFirstViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lock_first_list, parent, false);
        return new LockFirstViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LockFirstViewHolder holder, final int position) {
        final CommLockInfo info = mLockInfos.get(position);
        holder.mAppName.setText(packageManager.getApplicationLabel(info.getAppInfo()));
        holder.mCheckBox.setChecked(info.isLocked());

        String appName = (String) packageManager.getApplicationLabel(info.getAppInfo());
        if (info.getPackageName().equals("com.android.gallery3d")) { //相册
            holder.mAppDescribe.setText(R.string.lock_app_desc_gallery3d);
        } else if (info.getPackageName().equals("com.android.mms") || info.getPackageName().equals("com.tencent.mm")) {//微信，信息
            holder.mAppDescribe.setText(R.string.lock_app_desc_msg);
        } else if (appName.contains("相册") || appName.contains("Gallery") || appName.contains("gallery")) {
            holder.mAppDescribe.setText(R.string.lock_app_desc_gallery3d);
        } else { //其他
            holder.mAppDescribe.setText(R.string.lock_app_desc_info);
        }
        holder.mAppDescribe.setVisibility(info.isFaviterApp() ? View.VISIBLE : View.GONE);
        ApplicationInfo appInfo = info.getAppInfo();
        if (appInfo != null) {
            holder.mAppIcon.setImageDrawable(packageManager.getApplicationIcon(appInfo));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.OnItemClick(info, position);
                }
            }
        });
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.OnItemClick(info, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLockInfos.size();
    }

//    @Override
//    public Filter getFilter() {
//        SearchFilter filter = new SearchFilter(mLockInfos, mCopyLockInfos, packageManager);
//        filter.setListener(new SearchFilter.OnPublishResultsListener() {
//            @Override
//            public void notifyData() {
//                notifyDataSetChanged();
//            }
//
//            @Override
//            public void resetData(List<CommLockInfo> list) {
//                setLockInfos(mCopyLockInfos);
//            }
//        });
//        return filter;
//    }

    public class LockFirstViewHolder extends RecyclerView.ViewHolder {

        private ImageView mAppIcon;
        private TextView mAppName, mAppDescribe;
        private CheckBox mCheckBox;

        public LockFirstViewHolder(View itemView) {
            super(itemView);
            mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            mAppName = (TextView) itemView.findViewById(R.id.app_name);
            mAppDescribe = (TextView) itemView.findViewById(R.id.app_describe);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void OnItemClick(CommLockInfo info, int position);
    }


}
