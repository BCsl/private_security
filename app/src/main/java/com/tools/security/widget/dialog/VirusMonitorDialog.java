package com.tools.security.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.config.AppConfig;
import com.tools.security.bean.AppWhitePaper;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.SafeLevel;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.KochavaUtils;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.SystemUtil;
import com.tools.security.widget.popupwindow.VirusMonitorPopupWindow;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 * description:监听安装的弹窗
 * author: xiaodifu
 * date: 2016/12/19.
 */

public class VirusMonitorDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private AlertDialog alertDialog;
    //来源：1，危险列表；2.监听
    private int from;
    //包名
    private AvlAppInfo appInfo;

    private Context context;
    private PackageManager packageManager;

    private ImageView iconImg;
    private ImageView menuImg;
    private TextView nameText;
    private TextView virusNameText;
    private TextView installTimeText;
    private TextView uninstallText;
    private TextView behaviorText;

    private String behavior;

    private boolean isIgnore = false;
    private Map<String, Object> map = new HashMap<>();

    public VirusMonitorDialog(Context context, int from, AvlAppInfo appInfo,String behavior) {
        this.context = context;
        this.from = from;
        this.appInfo = appInfo;
        this.behavior=behavior;
        initView();
        initData();
    }

    private void initView() {
        alertDialog = new AlertDialog.Builder(context).create();
        Window window = alertDialog.getWindow();
        if (from == 2) window.setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        window.setContentView(R.layout.dialog_install_monitor);
        window.setWindowAnimations(R.style.dialog_anim_monitor);
        iconImg = (ImageView) window.findViewById(R.id.img_logo);
        menuImg = (ImageView) window.findViewById(R.id.img_menu);
        nameText = (TextView) window.findViewById(R.id.text_name);
        virusNameText = (TextView) window.findViewById(R.id.text_virus_name);
        installTimeText = (TextView) window.findViewById(R.id.text_install_time);
        uninstallText = (TextView) window.findViewById(R.id.text_uninstall);
        behaviorText= (TextView) window.findViewById(R.id.text_behavior);

        menuImg.setOnClickListener(this);
        uninstallText.setOnClickListener(this);
        alertDialog.setOnDismissListener(this);
    }

    private void initData() {
        packageManager = context.getPackageManager();
        if (appInfo == null) return;

        virusNameText.setText(TextUtils.isEmpty(appInfo.getVirusName()) ? context.getString(R.string.default_virus_name) : appInfo.getVirusName());
        ApplicationInfo applicationInfo = null;
        PackageInfo packageInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(appInfo.getPackageName(), 0);
            packageInfo = packageManager.getPackageInfo(appInfo.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        iconImg.setImageDrawable(applicationInfo.loadIcon(packageManager));
        nameText.setText(applicationInfo.loadLabel(packageManager));
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.default_simple_date_format));
        sdf.setTimeZone(TimeZone.getDefault());
        Date date = new Date(packageInfo.firstInstallTime);
        installTimeText.setText(context.getString(R.string.install_time) + sdf.format(date));
        if (!TextUtils.isEmpty(behavior)){
            behaviorText.setText(behavior);
        }

        map.put("type", from);
        map.put("pkgname", appInfo.getPackageName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_uninstall:
                SystemUtil.uninstall(context, appInfo.getPackageName(), true);
                KochavaUtils.tracker(AppConstants.CLICK_DANGER_DIALOG_UNINSTALL, map);
                dismiss();
                break;
            case R.id.img_menu:
                showPopupMenu(v);
                break;
        }
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        if (alertDialog == null) return;
        alertDialog.dismiss();
    }

    //显示菜单
    private void showPopupMenu(View view) {
        final VirusMonitorPopupWindow virusMonitorPopupWindow = new VirusMonitorPopupWindow(context);
        virusMonitorPopupWindow.setOnMenuClickListener(new VirusMonitorPopupWindow.IOnMenuClickListener() {
            @Override
            public void onIgnore() {
                isIgnore = true;
                KochavaUtils.tracker(AppConstants.CLICK_DANGER_DIALOG_IGNORE, map);

                appInfo.setIgnored(1);
                appInfo.save();

                AppWhitePaper whitePaper = new AppWhitePaper(appInfo.getId(),appInfo.getResult(), appInfo.getVirusName(), appInfo.getPackageName(), appInfo.getSampleName());
                whitePaper.save();

                Intent intent = new Intent();
                intent.setAction(AppConstants.ACTION_FILTER_ADD_IGNORE);

                boolean isSuccess = updateDangerList(appInfo.getPackageName());
                if (isSuccess) {
                    intent.setAction(AppConstants.ACTION_FILTER_REMOVED_VIRUS_APP);
                }
                intent.putExtra("package_name", appInfo.getPackageName());
                context.sendBroadcast(intent);

                virusMonitorPopupWindow.dismiss();
                dismiss();
            }
        });
        virusMonitorPopupWindow.showAsDropDown(view, 0, 0);
    }

    /**
     * 更新危险列表
     *
     * @param packageName
     */
    private boolean updateDangerList(String packageName) {
        ArrayList<AvlAppInfo> dangerList = (ArrayList<AvlAppInfo>) DataSupport.where("ignored = ? and result = ?", "0", "1").find(AvlAppInfo.class);
        if (!TextUtils.isEmpty(packageName)) {
            Iterator<AvlAppInfo> iterator = dangerList.iterator();
            boolean hasChanged = false;
            while (iterator.hasNext()) {
                AvlAppInfo appInfo = iterator.next();
                if (appInfo.getPackageName().equals(packageName)) {
                    appInfo.delete();
                    iterator.remove();
                    hasChanged = true;
                }
            }
            if (hasChanged) {
                AppConfig appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
                if (dangerList.size() == 0) appConfig.setSafeLevel(SafeLevel.DANGER);
                int problemCount = appConfig.getProblemCount();
                appConfig.setProblemCount(problemCount - 1);
                SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
            }
            return hasChanged;
        } else {
            return false;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!isIgnore) {
            AppConfig appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
//            ArrayList<AvlAppInfo> dangerList = (ArrayList<AvlAppInfo>) SpUtil.getInstance().getList(AppConstants.DANGER_LIST, AvlAppInfo.class);
            int problemCount = appConfig.getProblemCount();
            appConfig.setProblemCount(problemCount + 1);
            appConfig.setSafeLevel(SafeLevel.DANGER);
//            dangerList.add(appInfo);
            SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
//            SpUtil.getInstance().putList(AppConstants.DANGER_LIST, dangerList);
            appInfo.save();
        }
    }

}
