package com.tools.security.download;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.DownloadFile;
import com.tools.security.bean.SafeLevel;
import com.tools.security.utils.BitmapUtil;
import com.tools.security.utils.FileUtil;
import com.tools.security.widget.dialog.DownloadAlertDialog;
import com.tools.security.widget.dialog.DownloadItemDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * description:下载列表Adapter
 * author: xiaodifu
 * date: 2017/1/6.
 */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.MyViewHolder> {

    private List<DownloadFile> pathList = new ArrayList<>();
    private Context context;
    private PackageManager packageManager;

    public DownloadAdapter(Context context) {
        this.context = context;
        packageManager = context.getPackageManager();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DownloadFile downloadFile = pathList.get(position);
        holder.nameText.setText(downloadFile.getName());
        holder.fromText.setText(context.getString(R.string.download_from, downloadFile.getFrom()));
        if (downloadFile.getSafeLevel() == SafeLevel.DANGER) {
            holder.statusText.setText("Danger");
            holder.statusText.setCompoundDrawables(context.getResources().getDrawable(R.drawable.ic_dd_danger), null, null, null);
            holder.statusText.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            holder.statusText.setText("Safe");
            holder.statusText.setCompoundDrawables(context.getResources().getDrawable(R.drawable.ic_dd_safe), null, null, null);
            holder.statusText.setTextColor(context.getResources().getColor(R.color.green));
        }

        switch (downloadFile.getFileType()) {
            case APK:
                PackageInfo packageInfo = packageManager.getPackageArchiveInfo(downloadFile.getAbsPath(), PackageManager.GET_ACTIVITIES);
                if (packageInfo != null) {
                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                    applicationInfo.sourceDir = downloadFile.getAbsPath();
                    applicationInfo.publicSourceDir = downloadFile.getAbsPath();
                    holder.iconImg.setImageDrawable(applicationInfo.loadIcon(packageManager));
                } else {
                    holder.iconImg.setImageResource(R.drawable.ic_apk);
                }
                break;
            case IMAGE:
                Bitmap bitmap = BitmapUtil.getImageThumbnail(downloadFile.getAbsPath(), 80, 80);
                if (bitmap != null) {
                    holder.iconImg.setImageBitmap(bitmap);
                } else {
                    holder.iconImg.setImageResource(R.drawable.ic_picture);
                }
                break;
            case ZIP:
                holder.iconImg.setImageResource(R.drawable.ic_zip);
                break;
            case OTHER:
                holder.iconImg.setImageResource(R.drawable.ic_zip);
                break;
        }

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (downloadFile.getFileType()) {
                            case APK:
                                if (downloadFile.getSafeLevel() == SafeLevel.DANGER) {
                                    new DownloadAlertDialog(context, downloadFile.getAbsPath(), false).show();
                                } else {
                                    Intent intent = FileUtil.openFile(downloadFile.getAbsPath());
                                    context.startActivity(intent);
                                }
                                break;
                            default:
                                Intent intent = FileUtil.openFile(downloadFile.getAbsPath());
                                context.startActivity(intent);
                                break;
                        }
                    }
                }
        );

        holder.menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadItemDialog(context, downloadFile).show();
            }
        });
    }

    public void setData(List<DownloadFile> data) {
        this.pathList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;
        ImageView iconImg;
        TextView nameText;
        TextView statusText;
        TextView fromText;
        ImageView menuImg;

        public MyViewHolder(View itemView) {
            super(itemView);
            dayText = (TextView) itemView.findViewById(R.id.text_time_group);
            iconImg = (ImageView) itemView.findViewById(R.id.img_icon);
            nameText = (TextView) itemView.findViewById(R.id.text_name);
            statusText = (TextView) itemView.findViewById(R.id.text_status);
            fromText = (TextView) itemView.findViewById(R.id.text_from);
            menuImg = (ImageView) itemView.findViewById(R.id.img_menu);
        }
    }
}
