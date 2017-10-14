package com.tools.security.wifi.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.tools.security.R;
import com.tools.security.widget.dialog.NormalDialog;
import com.tools.security.wifi.core.devicescan.IP_MAC;

import java.util.List;

/**
 * description:共享设备Adapter
 * author: xiaodifu
 * date: 2017/1/13.
 */

public class DeviceScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<IP_MAC> mDeviceList;
    private String mLocalIp;
    private String mGateIp;
    private int countViewPosition = 0;

    private static final int VIEW_TYPE_COUNT = 1;
    private static final int VIEW_TYPE_NORMAL = 2;


    public DeviceScanAdapter(Context context, String localIp,
                             String gateip) {
        this.mContext = context;
        this.mLocalIp = localIp;
        this.mGateIp = gateip;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_COUNT)
            return new CountHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.item_wifi_devices_count, parent, false));

        return new DeviceHolder(LayoutInflater.from(mContext).inflate(
                R.layout.item_wifi_devices_scan, parent, false));
    }

    public int getCountViewPosition() {
        return countViewPosition;
    }

    public void setData(List<IP_MAC> mDeviceList) {
        this.mDeviceList = mDeviceList;
        if (mDeviceList == null || mDeviceList.size() == 0) return;
        //判断统计数量的View的位置

        if (mDeviceList.get(0).mIp.equals(mGateIp)) {
            if (mDeviceList.size() >= 2) {
                if (mDeviceList.get(1).mIp.equals(mLocalIp)) {
                    countViewPosition = 2;
                } else {
                    countViewPosition = 1;
                }
            } else {
                countViewPosition = 1;
            }
        } else {
            if (mDeviceList.size() > 2) {
                if (mDeviceList.get(1).mIp.equals(mLocalIp)) {
                    countViewPosition = 1;
                } else {
                    countViewPosition = 0;
                }
            } else {
                if (mDeviceList.get(0).mIp.equals(mLocalIp)) {
                    countViewPosition = 1;
                } else {
                    countViewPosition = 0;
                }
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == countViewPosition) {
            return VIEW_TYPE_COUNT;
        }
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_COUNT) {
            CountHolder holder = (CountHolder) viewHolder;
            holder.countText.setText(Html.fromHtml(mContext.getString(R.string.wifi_devices_count, "" + mDeviceList.size())));
        } else {
            final DeviceHolder holder = (DeviceHolder) viewHolder;
            IP_MAC ip_mac = null;
            if (countViewPosition > position) {
                ip_mac = mDeviceList.get(position);
            } else {
                ip_mac = mDeviceList.get(position - 1);
            }
            if (ip_mac != null) {
                holder.ipText.setText(ip_mac.mIp);
                int typeImgResId;
                if (ip_mac.mIp.equals(mLocalIp)) {
                    typeImgResId = R.drawable.ic_device_me;
                    holder.deviceManufactureTextView.setText(mContext.getString(R.string.your_phone));
                } else if (ip_mac.mIp.equals(mGateIp)) {
                    typeImgResId = R.drawable.ic_device_router;
                    holder.deviceManufactureTextView.setText(mContext.getString(R.string.gate_net));
                } else {
                    if (ip_mac.mManufacture.equals(mContext.getString(R.string.apple))) {
                        typeImgResId = R.drawable.ic_devices_apple;
                    } else {
                        typeImgResId = R.drawable.ic_devices_pc;
                    }
                    holder.deviceManufactureTextView.setText(ip_mac.mManufacture);
                }
                holder.typeImg.setImageResource(typeImgResId);

                final IP_MAC finalIp_mac = ip_mac;
                final int finaltypeImgResId = typeImgResId;
                final NormalDialog[] normalDialog = {null};
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        normalDialog[0] = new NormalDialog(mContext, new NormalDialog.IOnClickListener() {
                            @Override
                            public void onLeftClick() {

                            }

                            @Override
                            public void onRightClick() {
                                normalDialog[0].superDismiss();
                            }
                        });
                        normalDialog[0].title(holder.deviceManufactureTextView.getText().toString())
                                .right(mContext.getString(R.string.ok))
                                .content(mContext.getString(R.string.device_details, finalIp_mac.mManufacture, finalIp_mac.mIp, finalIp_mac.mMac))
                                .drawableLeft(finaltypeImgResId)
                                .show();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mDeviceList == null) return 1;
        return mDeviceList.size() + 1;
    }

    class DeviceHolder extends RecyclerView.ViewHolder {
        ImageView typeImg;
        ImageView menuImg;
        TextView ipText;
        TextView deviceManufactureTextView;

        public DeviceHolder(View itemView) {
            super(itemView);
            typeImg = (ImageView) itemView.findViewById(R.id.img_type);
            menuImg = (ImageView) itemView.findViewById(R.id.img_menu);
            ipText = (TextView) itemView.findViewById(R.id.text_ip);
            deviceManufactureTextView = (TextView) itemView
                    .findViewById(R.id.text_device_manufacture);
        }
    }

    class CountHolder extends RecyclerView.ViewHolder {
        TextView countText;

        public CountHolder(View itemView) {
            super(itemView);
            countText = (TextView) itemView.findViewById(R.id.text_device_count);
        }
    }
}
