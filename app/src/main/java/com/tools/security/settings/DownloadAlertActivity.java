package com.tools.security.settings;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.SafeLevel;
import com.tools.security.bean.config.AppConfig;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.SpUtil;

import java.io.File;

import static com.tools.security.common.AppConstants.VIRUS_MONITOR_APP_INFO;

/**
 * description:下载到病毒apk时的警告窗
 * author: xiaodifu
 * date: 2016/12/20.
 */

public class DownloadAlertActivity extends Activity implements View.OnClickListener {
    private AvlAppInfo appInfo;
    private PackageManager packageManager;
    private ImageView iconImg;
    private ImageView closeImg;
    private TextView nameText;
    private TextView virusNameText;
    private TextView deleteText;
    private String absPath;

    private boolean isDeleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_apk_alert);

        initView();
        initData();
    }

    private void initData() {
        this.appInfo = SpUtil.getInstance().getBean(VIRUS_MONITOR_APP_INFO, AvlAppInfo.class);
        packageManager = getPackageManager();

        absPath = getIntent().getStringExtra("path");

        if (appInfo == null) return;

        virusNameText.setText(TextUtils.isEmpty(appInfo.getVirusName()) ? getString(R.string.default_virus_name) : appInfo.getVirusName());
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(appInfo.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        iconImg.setImageDrawable(applicationInfo.loadIcon(packageManager));
        nameText.setText(applicationInfo.loadLabel(packageManager));
    }

    @Override
    public void finish() {
        if (!isDeleted) {
            AppConfig appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
            if (appConfig.getSafeLevel() != SafeLevel.DANGER) {
                appConfig.setSafeLevel(SafeLevel.DANGER);
            }
            appConfig.setProblemCount(appConfig.getProblemCount() + 1);
            appInfo.save();
            SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
        }
        super.finish();
    }

    private void initView() {
        iconImg = (ImageView) findViewById(R.id.img_logo);
        closeImg = (ImageView) findViewById(R.id.img_close);
        nameText = (TextView) findViewById(R.id.text_name);
        virusNameText = (TextView) findViewById(R.id.text_virus_name);
        deleteText = (TextView) findViewById(R.id.text_del);

        closeImg.setOnClickListener(this);
        deleteText.setOnClickListener(this);

        setFinishOnTouchOutside(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_del:
                File file = new File(absPath);
                if (file != null && file.isFile()) {
                    file.delete();
                }
                isDeleted=true;
                finish();
                break;
            case R.id.img_close:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        packageManager = null;
        appInfo = null;
        super.onDestroy();
    }
}
