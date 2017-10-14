package com.tools.security.wifi.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.ScannedApp;
import com.tools.security.bean.WifiIgnoreApp;
import com.tools.security.utils.ScannedAppCacheUtil;
import com.tools.security.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaodifu on 3/9/16.
 */
public class AddIgnoreAppAdapter extends RecyclerView.Adapter<AddIgnoreAppAdapter.ViewHolder> {

    List<PackageInfo> appInfoList;
    Context context;
    PackageManager packageManager;
    ScannedAppCacheUtil cacheUtil;

    public AddIgnoreAppAdapter(Context context, ScannedAppCacheUtil scannedAppCacheUtil) {
        this.context = context;
        packageManager = context.getPackageManager();
        this.cacheUtil = scannedAppCacheUtil;
    }

    public void setAppInfoList(List<PackageInfo> appInfoList) {
        this.appInfoList = appInfoList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_wifi_add_ignore, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PackageInfo appInfo = appInfoList.get(position);
        if (null != appInfo) {
            try {
                ScannedApp scannedApp = cacheUtil.get(appInfo.packageName);
                if (scannedApp == null) {
                    Drawable icon = packageManager.getApplicationIcon(appInfo.packageName);
                    holder.logoImg.setImageDrawable(icon);
                    String appName = packageManager.getApplicationInfo(appInfo.packageName, 0).loadLabel(packageManager).toString();
                    holder.nameText.setText(appName);
                    scannedApp = new ScannedApp(icon, appInfo.packageName, appName, "", 0, "");
                    cacheUtil.put(appInfo.packageName, scannedApp);
                } else {
                    holder.logoImg.setImageDrawable(scannedApp.getIcon());
                    holder.nameText.setText(scannedApp.getAppName());
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            holder.addImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new WifiIgnoreApp(1,appInfo.packageName).save();
                    appInfoList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    ToastUtil.showShort("Added to Ignore List");
                }
            });
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
        private ImageView addImg;

        public ViewHolder(View itemView) {
            super(itemView);
            logoImg = (ImageView) itemView.findViewById(R.id.img_logo);
            nameText = (TextView) itemView.findViewById(R.id.text_name);
            addImg = (ImageView) itemView.findViewById(R.id.img_add);
        }
    }
}
