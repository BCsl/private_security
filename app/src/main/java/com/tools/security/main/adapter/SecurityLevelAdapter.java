package com.tools.security.main.adapter;

/**
 * Created by Zhizhen on 2017/1/23.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.SecurityLevel;
import com.tools.security.utils.LockUtil;
import com.tools.security.utils.SystemUtil;

import java.util.List;

public class SecurityLevelAdapter extends RecyclerView.Adapter<SecurityLevelAdapter.ViewContentHolder> {

    private Context mContext;
    private List<SecurityLevel> mList;
    private OnItemClickListener mOnItemClickListener;


    public SecurityLevelAdapter(Context context, List<SecurityLevel> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_security_level, parent, false);
        return new ViewContentHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewContentHolder holder, final int position) {
        final SecurityLevel level = mList.get(position);
        holder.mTitle.setText(level.getTitleStr());
        holder.mDesc.setText(level.getDescStr());
        if (level.getIndex() == 0) {
            if (!SystemUtil.isAccessibilitySettingsOn(mContext)) {
                level.setEnable(false);
                holder.mImageIcon.setImageResource(R.drawable.safe_level_accessibilitly_false);
                holder.mBtnEnable.setVisibility(View.VISIBLE);
                holder.mIcEnable.setVisibility(View.GONE);
                holder.mBtnEnable.setBackgroundResource(R.drawable.bg_frame_btn_blue);
                holder.mBtnEnable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onAccessibilityItemClick(level, position + 1, 0);
                        }
                    }
                });
            } else {
                level.setEnable(true);
                holder.mImageIcon.setImageResource(R.drawable.safe_level_accessibilitly_true);
                holder.mBtnEnable.setVisibility(View.GONE);
                holder.mIcEnable.setVisibility(View.VISIBLE);
            }
        } else if (level.getIndex() == 1) {
            if (!SystemUtil.isNotificationSettingOn(mContext)) {
                level.setEnable(false);
                holder.mImageIcon.setImageResource(R.drawable.safe_level_notifi_false);
                holder.mBtnEnable.setVisibility(View.VISIBLE);
                holder.mIcEnable.setVisibility(View.GONE);
                holder.mBtnEnable.setBackgroundResource(R.drawable.bg_frame_btn_blue);
                holder.mBtnEnable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onNotificationItemClick(level, position + 1, 2);
                        }
                    }
                });
            } else {
                level.setEnable(true);
                holder.mImageIcon.setImageResource(R.drawable.safe_level_notifi_true);
                holder.mBtnEnable.setVisibility(View.GONE);
                holder.mIcEnable.setVisibility(View.VISIBLE);
            }
        } else if (level.getIndex() == 2) {
            if (!LockUtil.isStatAccessPermissionSet(mContext)) {
                level.setEnable(false);
                holder.mImageIcon.setImageResource(R.drawable.safe_level_access_false);
                holder.mBtnEnable.setVisibility(View.VISIBLE);
                holder.mIcEnable.setVisibility(View.GONE);
                holder.mBtnEnable.setBackgroundResource(R.drawable.bg_frame_btn_blue);
                holder.mBtnEnable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onUsageAccessItemClick(level, position + 1, 2);
                        }
                    }
                });
            } else {
                level.setEnable(true);
                holder.mImageIcon.setImageResource(R.drawable.safe_level_access_true);
                holder.mBtnEnable.setVisibility(View.GONE);
                holder.mIcEnable.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListener {
        void onAccessibilityItemClick(SecurityLevel level, int position, int index);

        void onNotificationItemClick(SecurityLevel level, int position, int index);

        void onUsageAccessItemClick(SecurityLevel level, int position, int index);
    }

    public class ViewContentHolder extends RecyclerView.ViewHolder {
        public ImageView mImageIcon, mIcEnable;
        public TextView mTitle;
        public TextView mDesc;
        public TextView mBtnEnable;


        public ViewContentHolder(View itemView) {
            super(itemView);
            mIcEnable = (ImageView) itemView.findViewById(R.id.ic_enable);
            mImageIcon = (ImageView) itemView.findViewById(R.id.security_level_img);
            mTitle = (TextView) itemView.findViewById(R.id.security_level_title);
            mDesc = (TextView) itemView.findViewById(R.id.security_level_desc);
            mBtnEnable = (TextView) itemView.findViewById(R.id.security_level_enable);
        }
    }
}
