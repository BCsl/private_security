package com.tools.security.mainscan.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avl.engine.AVLEngine;
import com.tools.security.R;
import com.tools.security.bean.AppWhitePaper;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.ResultInfo;
import com.tools.security.bean.ResultType;
import com.tools.security.common.AppConstants;
import com.tools.security.mainscan.view.BrowserHistoryActivity;
import com.tools.security.mainscan.view.ScannedListActivity;
import com.tools.security.utils.KochavaUtils;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.StringUtil;
import com.tools.security.utils.SystemUtil;
import com.tools.security.widget.dialog.JunkFileCleanDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * description:扫描结果页Adapter
 * author: xiaodifu
 * date: 2016/12/19.
 */

public class ScanResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ResultInfo> normalList = new ArrayList<>();
    private ArrayList<AvlAppInfo> dangerList = new ArrayList<>();
    private Activity context;
    private PackageManager packageManager;

    private static final int VIEW_TYPE_VIRUS = 1;
    private static final int VIEW_TYPE_NORMAL = 2;


    public ScanResultAdapter(Activity context) {
        this.context = context;
        packageManager = context.getPackageManager();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_VIRUS) {
            return new VirusViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_virus, parent, false));
        }
        return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_normal, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == VIEW_TYPE_VIRUS) {
            //恶意软件Item赋值处理
            final VirusViewHolder virusViewHolder = (VirusViewHolder) holder;
            final AvlAppInfo appInfo = dangerList.get(position);
            ApplicationInfo applicationInfo = null;
            PackageInfo packageInfo = null;
            try {
                applicationInfo = packageManager.getApplicationInfo(appInfo.getPackageName(), 0);
                packageInfo = packageManager.getPackageInfo(appInfo.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return;
            }
            virusViewHolder.logoImg.setImageDrawable(applicationInfo.loadIcon(packageManager));
            virusViewHolder.nameText.setText(applicationInfo.loadLabel(packageManager));
            SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.default_simple_date_format));
            sdf.setTimeZone(TimeZone.getDefault());
            Date date = new Date(packageInfo.firstInstallTime);
            virusViewHolder.dateText.setText(context.getString(R.string.install_time) + sdf.format(date));
            String resultTypeStr="";
            if (appInfo.getDangerLevel()==1){
                resultTypeStr="Malicious";
            }else {
                resultTypeStr="Risky";
            }
            virusViewHolder.typeText.setText(resultTypeStr);
            String virusName = context.getString(R.string.containning_virus, (TextUtils.isEmpty(appInfo.getVirusName()) ? context.getString(R.string.default_virus_name) : appInfo.getVirusName()));
            virusViewHolder.virusNameText.setText(virusName);
            virusViewHolder.virusNameText.setTextColor(context.getResources().getColor(R.color.black_2));
            virusViewHolder.adviceText.setText(Html.fromHtml(context.getString(R.string.advice_uninstall)));

            virusViewHolder.uninstallText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("pkgname", appInfo.getPackageName());
                    KochavaUtils.tracker(AppConstants.CLICK_SCAN_RESULT_UNINSTALL, map);

                    SpUtil.getInstance().putBoolean(AppConstants.IS_RESOLVE_ALL, false);
                    SpUtil.getInstance().putInt(AppConstants.SCAN_RESULT_UPDATE_POSITION, virusViewHolder.getAdapterPosition());
                    SystemUtil.uninstall(context, appInfo.getPackageName(), false);
                }
            });
            virusViewHolder.ignoreText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("pkgname", appInfo.getPackageName());
                    KochavaUtils.tracker(AppConstants.CLICK_SCAN_RESULT_IGNORE, map);

                    AvlAppInfo avlAppInfo=appInfo;
                    avlAppInfo.setIgnored(1);
                    avlAppInfo.update(avlAppInfo.getId());

                    AppWhitePaper whitePaper = new AppWhitePaper(appInfo.getId(),appInfo.getResult(), appInfo.getVirusName(), appInfo.getPackageName(), appInfo.getSampleName());
                    whitePaper.save();

                    SpUtil.getInstance().putInt(AppConstants.SCAN_RESULT_UPDATE_POSITION, virusViewHolder.getAdapterPosition());
                    context.sendBroadcast(new Intent(AppConstants.ACTION_FILTER_ADD_IGNORE).putExtra("package_name", appInfo.getPackageName()));
                }
            });
        } else {
            //非恶意软件Item赋值处理
            final NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            final ResultInfo resultInfo = normalList.get(position - dangerList.size());
            normalViewHolder.iconImg.setImageDrawable(resultInfo.getIcon());
            normalViewHolder.titleText.setText(resultInfo.getTitle());
            normalViewHolder.countText.setText("" + resultInfo.getCount());
            normalViewHolder.countText.setVisibility(resultInfo.getType() == ResultType.NORMAL ? View.GONE : View.VISIBLE);
            normalViewHolder.markText.setText(resultInfo.getType() == ResultType.NORMAL ? Html.fromHtml(context.getResources().getString(R.string.result_normal, resultInfo.getMark(), StringUtil.addComma(new DecimalFormat("0").format((System.currentTimeMillis() / 20000f))))) : "" + resultInfo.getMark());
            normalViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SpUtil.getInstance().putBoolean(AppConstants.IS_RESOLVE_ALL, false);
                    switch (resultInfo.getType()) {
                        case NORMAL:
                            KochavaUtils.tracker(AppConstants.CLICK_SCAN_RESULT_DEFINITION);
                            context.startActivity(new Intent(context, ScannedListActivity.class));
                            break;
                        case BROWSER:
                            SpUtil.getInstance().putInt(AppConstants.SCAN_RESULT_UPDATE_POSITION, normalViewHolder.getAdapterPosition());
                            context.startActivity(new Intent(context, BrowserHistoryActivity.class));
                            break;
                        case JUNK:
                            Map<String, Object> map = new HashMap<>();
                            map.put("junk", "" + resultInfo.getCount());
                            KochavaUtils.tracker(AppConstants.CLICK_SCAN_RESULT_JUNK, map);
                            SpUtil.getInstance().putInt(AppConstants.SCAN_RESULT_UPDATE_POSITION, normalViewHolder.getAdapterPosition());
                            new JunkFileCleanDialog(context).show();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return normalList.size() + dangerList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < dangerList.size()) {
            return VIEW_TYPE_VIRUS;
        }
        return VIEW_TYPE_NORMAL;
    }

    //设置数据
    public void setData(List<ResultInfo> normalList, List<AvlAppInfo> dangerList) {
        this.normalList.clear();
        this.normalList.addAll(normalList);
        this.dangerList.clear();
        this.dangerList.addAll(dangerList);
        notifyDataSetChanged();
    }

    public void removeItem(boolean isDanger, int position) {
        try {
            if (isDanger) {
                dangerList.remove(position);
            } else {
                normalList.remove(position);
            }
            notifyItemRemoved(position);
//            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class NormalViewHolder extends RecyclerView.ViewHolder {

        ImageView iconImg;
        TextView titleText;
        TextView markText;
        TextView countText;
        ImageView triangleImg;
        RelativeLayout mRelativeLayout;

        public NormalViewHolder(View itemView) {
            super(itemView);
            iconImg = (ImageView) itemView.findViewById(R.id.img_icon);
            titleText = (TextView) itemView.findViewById(R.id.text_title);
            markText = (TextView) itemView.findViewById(R.id.text_mark);
            triangleImg = (ImageView) itemView.findViewById(R.id.img_triangle);
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_result_normal);
            countText = (TextView) itemView.findViewById(R.id.text_count);
        }
    }

    class VirusViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImg;
        TextView typeText;
        TextView nameText;
        TextView virusNameText;
        TextView dateText;
        TextView ignoreText;
        TextView uninstallText;
        TextView adviceText;

        public VirusViewHolder(View itemView) {
            super(itemView);
            logoImg = (ImageView) itemView.findViewById(R.id.img_logo);
            typeText = (TextView) itemView.findViewById(R.id.text_category);
            nameText = (TextView) itemView.findViewById(R.id.text_name);
            virusNameText = (TextView) itemView.findViewById(R.id.text_virus_name);
            dateText = (TextView) itemView.findViewById(R.id.text_install_date);
            ignoreText = (TextView) itemView.findViewById(R.id.text_ignore);
            uninstallText = (TextView) itemView.findViewById(R.id.text_uninstall);
            adviceText = (TextView) itemView.findViewById(R.id.text_advice);
        }
    }

}
