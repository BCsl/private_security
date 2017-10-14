package com.tools.security.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.tools.security.common.AppConstants.VIRUS_MONITOR_APP_INFO;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/20.
 */

public class VirusMonitorActivity extends Activity implements View.OnClickListener {
    //来源：1、危险列表；2，Service监听；
    private int from;
    //包名
    private AvlAppInfo appInfo;

    private PackageManager packageManager;

    private ImageView iconImg;
    private ImageView menuImg;
    private TextView nameText;
    private TextView virusNameText;
    private TextView installTimeText;
    private TextView uninstallText;
    private TextView behaviorText;

    private String behavior;


    private Map<String, Object> map = new HashMap<>();

    private boolean isIgnore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_monitor);

        initView();
        initData();
    }

    private void initData() {
        from = getIntent().getIntExtra("from", 2);
        this.appInfo = SpUtil.getInstance().getBean(VIRUS_MONITOR_APP_INFO, AvlAppInfo.class);
        packageManager = getPackageManager();

        if (appInfo == null) return;
        behavior=getIntent().getStringExtra("behavior");

        virusNameText.setText(TextUtils.isEmpty(appInfo.getVirusName()) ? getString(R.string.default_virus_name) : appInfo.getVirusName());
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());
        Date date = new Date(packageInfo.firstInstallTime);
        installTimeText.setText("Install time:" + sdf.format(date));
        if (!TextUtils.isEmpty(behavior)){
            behaviorText.setText(behavior);
        }

        map.put("type", from);
        map.put("pkgname", appInfo.getPackageName());
    }

    @Override
    public void finish() {
        if (!isIgnore) {
            if (from == 2) {
                AppConfig appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
                if (appConfig.getSafeLevel() != SafeLevel.DANGER) {
                    appConfig.setSafeLevel(SafeLevel.DANGER);
                }
                appConfig.setProblemCount(appConfig.getProblemCount() + 1);
//                ArrayList<AvlAppInfo> dangerList = (ArrayList<AvlAppInfo>) SpUtil.getInstance().getList(AppConstants.DANGER_LIST, AvlAppInfo.class);
//                dangerList.add(appInfo);
//                SpUtil.getInstance().putList(AppConstants.DANGER_LIST, dangerList);
                appInfo.save();
                SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
            }
        }
        super.finish();
    }

    private void initView() {
        iconImg = (ImageView) findViewById(R.id.img_logo);
        menuImg = (ImageView) findViewById(R.id.img_menu);
        nameText = (TextView) findViewById(R.id.text_name);
        virusNameText = (TextView) findViewById(R.id.text_virus_name);
        installTimeText = (TextView) findViewById(R.id.text_install_time);
        uninstallText = (TextView) findViewById(R.id.text_uninstall);
        behaviorText= (TextView) findViewById(R.id.text_behavior);

        menuImg.setOnClickListener(this);
        uninstallText.setOnClickListener(this);

        setFinishOnTouchOutside(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_uninstall:
                SystemUtil.uninstall(VirusMonitorActivity.this, appInfo.getPackageName(), false);
                KochavaUtils.tracker(AppConstants.CLICK_DANGER_DIALOG_UNINSTALL, map);
                dismiss();
                break;
            case R.id.img_menu:
                showPopupMenu(v);
                break;
        }
    }

    //显示菜单
    private void showPopupMenu(View view) {

        final VirusMonitorPopupWindow virusMonitorPopupWindow = new VirusMonitorPopupWindow(this);
        virusMonitorPopupWindow.setOnMenuClickListener(new VirusMonitorPopupWindow.IOnMenuClickListener() {
            @Override
            public void onIgnore() {
                appInfo.setIgnored(1);
                appInfo.save();

                AppWhitePaper whitePaper = new AppWhitePaper(appInfo.getId(),appInfo.getResult(), appInfo.getVirusName(), appInfo.getPackageName(), appInfo.getSampleName());
                whitePaper.save();

                isIgnore = true;
                VirusMonitorActivity.this.sendBroadcast(new Intent(AppConstants.ACTION_FILTER_ADD_IGNORE).putExtra("package_name", appInfo.getPackageName()));
                KochavaUtils.tracker(AppConstants.CLICK_DANGER_DIALOG_IGNORE, map);
                virusMonitorPopupWindow.dismiss();
                dismiss();
            }
        });
        virusMonitorPopupWindow.showAsDropDown(view, 0, 0);
    }

    private void dismiss() {
        VirusMonitorActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        packageManager = null;
        appInfo = null;
        map = null;
        super.onDestroy();
    }
}
