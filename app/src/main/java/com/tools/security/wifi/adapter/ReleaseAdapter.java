package com.tools.security.wifi.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.WifiReleaseApp;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.StringUtil;

import java.util.ArrayList;

/**
 * description:释放带宽ADAPTER
 * author: xiaodifu
 * date: 2017/1/15.
 */

public class ReleaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TRANSPARENT = 1;
    private static final int VIEW_TYPE_NORMAL = 2;

    private Context context;
    private ArrayList<WifiReleaseApp> list;
    private PackageManager packageManager;
    private ICheckCallback callback;
    private SpannableStringBuilder mStringBuilder;
    private AbsoluteSizeSpan mAbsoluteSizeSpan;

    public ReleaseAdapter(Context context, ICheckCallback checkCallback) {
        this.context = context;
        this.callback = checkCallback;
        packageManager = context.getPackageManager();
        mAbsoluteSizeSpan = new AbsoluteSizeSpan(AppUtils.dip2px(context, 12));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TRANSPARENT) {
            return new TransViewHolder(LayoutInflater.from(context).inflate(R.layout.item_release_transparent, parent, false));
        }
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_release, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_TYPE_NORMAL) {
            final MyViewHolder holder = (MyViewHolder) viewHolder;
            final WifiReleaseApp wifiReleaseApp = list.get(position - 1);
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(wifiReleaseApp.getPackageName(), 0);
                holder.logoImg.setImageDrawable(applicationInfo.loadIcon(packageManager));
                holder.nameText.setText(applicationInfo.loadLabel(packageManager));

                String sizeString = StringUtil.getFormatSize(wifiReleaseApp.getFlow());
                mStringBuilder = new SpannableStringBuilder(sizeString);

                mStringBuilder.setSpan(mAbsoluteSizeSpan, sizeString.indexOf(" ") + 1, sizeString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.flowText.setText(mStringBuilder);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            holder.checkBox.setChecked(wifiReleaseApp.isChecked());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    wifiReleaseApp.setChecked(isChecked);
                    list.set(holder.getAdapterPosition() - 1, wifiReleaseApp);
                    if (callback != null) callback.onChecked(isChecked);
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.checkBox.performClick();
                }
            });
        }
    }

    public ArrayList<WifiReleaseApp> getData() {
        ArrayList<WifiReleaseApp> wifiReleaseApps = new ArrayList<>();
        for (WifiReleaseApp app : list) {
            if (app.isChecked()) {
                wifiReleaseApps.add(app);
            }
        }
        return wifiReleaseApps;
    }

    public void setData(ArrayList<WifiReleaseApp> wifiReleaseApps) {
        this.list = wifiReleaseApps;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_TRANSPARENT;
        }
        return VIEW_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return list == null ? 1 : list.size() + 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImg;
        TextView nameText;
        TextView flowText;
        CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            logoImg = (ImageView) itemView.findViewById(R.id.img_logo);
            nameText = (TextView) itemView.findViewById(R.id.text_name);
            flowText = (TextView) itemView.findViewById(R.id.text_flow);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_release);
        }
    }

    class TransViewHolder extends RecyclerView.ViewHolder {
        public TransViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface ICheckCallback {
        void onChecked(boolean checked);
    }
}
