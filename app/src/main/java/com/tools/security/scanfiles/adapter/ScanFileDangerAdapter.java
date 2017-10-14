package com.tools.security.scanfiles.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.AvlFileInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * description:扫描文件，危险结果页
 * author: xiaodifu
 * date: 2017/1/12.
 */

public class ScanFileDangerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<AvlFileInfo> list = new ArrayList<>();
    private Context context;
    private PackageManager packageManager;
    private IDelelteCallback iDelelteCallback;

    private static final int VIEW_TYPE_TRANSPARENT = 1;
    private static final int VIEW_TYPE_NORMAL = 2;

    public ScanFileDangerAdapter(Context context, IDelelteCallback iDelelteCallback) {
        this.context = context;
        this.iDelelteCallback = iDelelteCallback;
        packageManager = context.getPackageManager();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TRANSPARENT) {
            return new TransViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sd_transparent, parent, false));
        }
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_scan_file_danger, null));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_NORMAL) {
            final MyViewHolder holder = (MyViewHolder) viewHolder;
            final AvlFileInfo avlFileInfo = list.get(position-1);
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(avlFileInfo.getPath(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                applicationInfo.sourceDir = avlFileInfo.getPath();
                applicationInfo.publicSourceDir = avlFileInfo.getPath();
                holder.logoImg.setImageDrawable(applicationInfo.loadIcon(packageManager));
            } else {
                holder.logoImg.setImageResource(R.drawable.ic_apk);
            }

            String resultTypeStr = "";
            if (avlFileInfo.getDangerLevel() == 1) {
                resultTypeStr = context.getString(R.string.malicious);
            } else {
                resultTypeStr = context.getString(R.string.risky);
            }
            String[] names = avlFileInfo.getPath().split("/");
            holder.nameText.setText(names[names.length - 1]);
            holder.typeText.setText(resultTypeStr);
            holder.virusNameText.setText(context.getString(R.string.classification, avlFileInfo.getVirusName()));
            holder.delText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(avlFileInfo.getPath());
                    if (file != null && file.isFile()) {
                        file.delete();
                    }
                    avlFileInfo.delete();
                    if (iDelelteCallback != null) iDelelteCallback.onDelete();
                    notifyItemRemoved(holder.getAdapterPosition());
                    try {
                        list.remove(holder.getAdapterPosition());
                    }catch (Exception e){
                        if (list.size()>0){
                            list.remove(0);
                        }
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public ArrayList<AvlFileInfo> getData(){
        return list;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return VIEW_TYPE_TRANSPARENT;
        }
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return list == null ? 1 : list.size() + 1;
    }

    public void setData(ArrayList<AvlFileInfo> avlFileInfos) {
        if (avlFileInfos == null) return;
        this.list = avlFileInfos;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImg;
        TextView typeText;
        TextView nameText;
        TextView virusNameText;
        TextView delText;

        public MyViewHolder(View itemView) {
            super(itemView);
            logoImg = (ImageView) itemView.findViewById(R.id.img_logo);
            typeText = (TextView) itemView.findViewById(R.id.text_category);
            nameText = (TextView) itemView.findViewById(R.id.text_name);
            virusNameText = (TextView) itemView.findViewById(R.id.text_class);
            delText = (TextView) itemView.findViewById(R.id.text_del);
        }
    }

    class TransViewHolder extends RecyclerView.ViewHolder {
        public TransViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface IDelelteCallback {
        void onDelete();
    }
}
