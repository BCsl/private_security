package com.tools.security.settings.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.AppWhitePaper;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.KochavaUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description:忽略列表Adapter
 * author: xiaodifu
 * date: 2016/12/14.
 */

public class IgnoreAdapter extends RecyclerView.Adapter<IgnoreAdapter.MyViewHolder> {

    private ArrayList<AppWhitePaper> list = new ArrayList<>();
    private Context context;
    private PackageManager packageManager;
    private OnDelCallback onDelCallback;


    public IgnoreAdapter(Context context, OnDelCallback callback) {
        this.context = context;
        this.onDelCallback = callback;
        packageManager = context.getPackageManager();
    }

    public void setData(List<AppWhitePaper> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ignore, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AppWhitePaper whitePaper = list.get(position);
        if (whitePaper != null) {
            try {
                holder.logoImg.setImageDrawable(packageManager.getApplicationIcon(whitePaper.getPkgName()));
                holder.nameText.setText(whitePaper.getSampleName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            holder.delImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    list.remove(holder.getAdapterPosition());
//                    notifyItemRemoved(holder.getAdapterPosition());
//                    notifyDataSetChanged();
                    onDelCallback.onDel(holder.getAdapterPosition());

                    Map<String, Object> map = new HashMap<>();
                    map.put("pkgname", whitePaper.getPkgName());
                    KochavaUtils.tracker(AppConstants.CLICK_IGNORE_DELETE, map);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView logoImg;
        private TextView nameText;
        private ImageView delImg;

        public MyViewHolder(View itemView) {
            super(itemView);
            logoImg = (ImageView) itemView.findViewById(R.id.img_logo);
            nameText = (TextView) itemView.findViewById(R.id.text_name);
            delImg = (ImageView) itemView.findViewById(R.id.img_del);
        }
    }

    public interface OnDelCallback {
        void onDel(int position);
    }
}
