package com.tools.security.mainscan.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.ScannedApp;
import com.tools.security.utils.BimmapCacheUtil;
import com.tools.security.utils.ScannedAppCacheUtil;
import com.tools.security.utils.SystemUtil;

import java.util.List;

/**
 * Created by xiaodifu on 3/9/16.
 */
public class ScannedAppAdapter extends RecyclerView.Adapter<ScannedAppAdapter.ViewHolder> {


    List<AvlAppInfo> appInfoList;
    Context context;
    PackageManager packageManager;
    ScannedAppCacheUtil cacheUtil;

    public ScannedAppAdapter(Context context, ScannedAppCacheUtil scannedAppCacheUtil) {
        this.context = context;
        packageManager = context.getPackageManager();
        this.cacheUtil = scannedAppCacheUtil;
    }

    public void setAppInfoList(List<AvlAppInfo> appInfoList) {
        this.appInfoList = appInfoList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_scanned, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AvlAppInfo appInfo = appInfoList.get(position);
        if (null != appInfo) {
            try {
                ScannedApp scannedApp = cacheUtil.get(appInfo.getPkgName());
                if (scannedApp == null) {
                    Drawable icon = packageManager.getApplicationIcon(appInfo.getPackageName());
                    holder.logoImg.setImageDrawable(icon);
                    String appName = packageManager.getApplicationInfo(appInfo.getPackageName(), 0).loadLabel(packageManager).toString();
                    holder.nameText.setText(appName);
                    String appFrom = packageManager.getInstallerPackageName(appInfo.getPackageName());
                    String appFromStr;
                    if (TextUtils.isEmpty(appFrom)) {
                        appFromStr = "Unknown";
                    } else if (appFrom.equals("com.amazon.venezia")) {
                        appFromStr = "Amazon Store";
                    } else if (appFrom.equals("com.android.vending")) {
                        appFromStr = "Google Play Store";
                    } else {
                        appFromStr = "Unknown";
                    }
                    boolean isSystemApp = SystemUtil.isSystemPackage(context, appInfo.getPackageName());
                    String markStr = isSystemApp ? "System application" : "Source:" + appFromStr;
                    holder.markText.setText(markStr);
                    int statusColor = -1;
                    String statusStr = null;
                    switch (appInfo.getDangerLevel()) {
                        case 0:
                        case 2:
                            statusColor = context.getResources().getColor(R.color.bg_green_normal);
                            statusStr = "Safe";
                            holder.statusText.setTextColor(statusColor);
                            holder.statusText.setText(statusStr);
                            break;
                        case 1:
                            statusColor = context.getResources().getColor(R.color.red);
                            statusStr = "Malicious";
                            holder.statusText.setTextColor(statusColor);
                            holder.statusText.setText(statusStr);
                            break;
                        //不显示风险应用，只显示恶意应用
//                    case 2:
//                        holder.statusText.setTextColor(context.getResources().getColor(R.color.orange));
//                        holder.statusText.setText("Risky");
//                        break;
                    }

                    scannedApp = new ScannedApp(icon, appInfo.getPackageName(), appName, markStr, statusColor, statusStr);
                    cacheUtil.put(appInfo.getPkgName(), scannedApp);
                } else {
                    holder.logoImg.setImageDrawable(scannedApp.getIcon());
                    holder.nameText.setText(scannedApp.getAppName());
                    holder.markText.setText(scannedApp.getMarkStr());
                    holder.statusText.setTextColor(scannedApp.getStatusColor());
                    holder.statusText.setText(scannedApp.getStatusStr());
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return appInfoList != null ? appInfoList.size() : 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView logoImg;
        private TextView nameText;
        private TextView markText;
        private TextView statusText;

        public ViewHolder(View itemView) {
            super(itemView);
            logoImg = (ImageView) itemView.findViewById(R.id.img_logo);
            nameText = (TextView) itemView.findViewById(R.id.text_name);
            markText = (TextView) itemView.findViewById(R.id.text_mark);
            statusText = (TextView) itemView.findViewById(R.id.text_status);
        }
    }
}
