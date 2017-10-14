package com.tools.security.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avl.engine.AVLAppInfo;
import com.avl.engine.AVLEngine;
import com.tools.security.R;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.DownloadScanPath;
import com.tools.security.clean.ApkInfo;
import com.tools.security.clean.FileUtils;
import com.tools.security.common.AppConstants;
import com.tools.security.download.SdCardDownloadListener;
import com.tools.security.settings.DownloadAlertActivity;
import com.tools.security.settings.VirusMonitorActivity;
import com.tools.security.utils.FileUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.widget.dialog.DownloadAlertDialog;
import com.tools.security.widget.dialog.ScanDownloadMoreDialog;

import java.io.File;

/**
 * description:下载保护Service
 * author: xiaodifu
 * date: 2017/1/8.
 */

public class SdCardDownloadService extends Service implements SdCardDownloadListener.ICreatedCallback, View.OnClickListener {

    private SdCardDownloadListener sdCardDownloadListener1;
    private SdCardDownloadListener sdCardDownloadListener2;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private PackageManager packageManager;

    private RelativeLayout scanningView;
    private TextView statusText;
    private TextView fromText;
    private RelativeLayout scanResultView;
    private ImageView logoImg;
    private TextView nameImg;
    private ImageView closeImg;
    private TextView moreText;
    private TextView installText;
    private String absPath;

    //显示扫描View
    private static final int MSG_UPDATE_SCANNING_VIEW = 1;
    //移除扫描View
    private static final int MSG_REMOVE_SCANNING_VIEW = 2;
    //显示结果View
    private static final int MSG_UPDATE_RESULT_VIEW = 3;
    //移除结果View
    private static final int MSG_REMOVE_RESULT_VIEW = 4;

    private Handler handler;
    private Runnable scanningRunnable;
    private Runnable resultRunnable;
    private DownloadScanPath currentPath;
    private PackageInfo packageInfo;
    private boolean scanningViewRemoved = false;
    private boolean resultViewRemoved = false;

    @Override
    public void onCreate() {
        super.onCreate();
        initView();
        initData();
    }

    private void initData() {
        String outPath = FileUtil.getStoragePath(this, true);
        String inPath = FileUtil.getStoragePath(this, false);


        if (!TextUtils.isEmpty(outPath)) {
            sdCardDownloadListener1 = new SdCardDownloadListener(new DownloadScanPath(outPath + "/Download", "", "Browser"), this);
            sdCardDownloadListener1.startWatching();
        }

        if (!TextUtils.isEmpty(inPath)) {
            sdCardDownloadListener2 = new SdCardDownloadListener(new DownloadScanPath(inPath + "/Download", "", "Browser"), this);
            sdCardDownloadListener2.startWatching();
        }

        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, 0, 0,
                PixelFormat.TRANSPARENT);
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.gravity = Gravity.TOP;

        packageManager = getPackageManager();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_UPDATE_SCANNING_VIEW:
                        // TODO: 2017/1/9 待完善，要添加扫描动画以及结果动画
                        windowManager.addView(scanningView, layoutParams);
                        scanningViewRemoved = false;
                        break;
                    case MSG_REMOVE_SCANNING_VIEW:
                        if (!scanningViewRemoved) {
                            windowManager.removeView(scanningView);
                            updateScanResultView();
                            scanningViewRemoved = true;
                        }
                        break;
                    case MSG_UPDATE_RESULT_VIEW:
                        windowManager.addView(scanResultView, layoutParams);
                        resultViewRemoved = false;
                        break;
                    case MSG_REMOVE_RESULT_VIEW:
                        if (!resultViewRemoved) {
                            windowManager.removeView(scanResultView);
                            resultViewRemoved = true;
                        }
                        break;
                }
            }
        };

        scanningRunnable = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_REMOVE_SCANNING_VIEW);
            }
        };

        resultRunnable = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_REMOVE_RESULT_VIEW);
            }
        };
    }

    private void initView() {
        scanningView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.layout_scan_download, null);
        statusText = (TextView) scanningView.findViewById(R.id.text_status);
        fromText = (TextView) scanningView.findViewById(R.id.text_from);
        scanResultView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.layout_scan_dl_result, null);
        logoImg = (ImageView) scanResultView.findViewById(R.id.img_logo);
        nameImg = (TextView) scanResultView.findViewById(R.id.text_title);
        closeImg = (ImageView) scanResultView.findViewById(R.id.img_close);
        moreText = (TextView) scanResultView.findViewById(R.id.text_more);
        installText = (TextView) scanResultView.findViewById(R.id.text_install);

        closeImg.setOnClickListener(this);
        moreText.setOnClickListener(this);
        installText.setOnClickListener(this);
    }

    //检测到目标文件夹新增了文件、弹出检测框
    private void updateScanningView() {
        statusText.setText(getString(R.string.scanning,currentPath.getName()) );
        fromText.setText(getString(R.string.dl_scan_from, currentPath.getFrom()));

        handler.sendEmptyMessage(MSG_UPDATE_SCANNING_VIEW);
        handler.postDelayed(scanningRunnable, MSG_REMOVE_SCANNING_VIEW);
    }

    //更新检测结果
    private void updateScanResultView() {
        if (currentPath.getName().endsWith(".apk")) {
            packageInfo = packageManager.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
            if (packageInfo == null) {
                //如果拿到结果为空，则不作处理
                Log.e("TAG", "apkInfo == null");
            } else {
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                applicationInfo.sourceDir = absPath;
                applicationInfo.publicSourceDir = absPath;
                logoImg.setImageDrawable(applicationInfo.loadIcon(packageManager));
                nameImg.setText(Html.fromHtml(getString(R.string.dl_scan_result_title, applicationInfo.loadLabel(packageManager))));
                handler.sendEmptyMessage(MSG_UPDATE_RESULT_VIEW);
                handler.postDelayed(resultRunnable, 5000);
            }
        } else {
            //如果不是apk包，暂时不作处理

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void created(DownloadScanPath downloadScanPath) {
        currentPath = downloadScanPath;
        absPath = currentPath.getPath() + "/" + currentPath.getName();
        if (new String(absPath).toUpperCase().endsWith(".APK")) {
            AVLAppInfo appScanRes = AVLEngine.Scan(absPath);
            if (appScanRes != null && appScanRes.getDangerLevel() == 1) {
                AvlAppInfo avlAppInfo = new AvlAppInfo(appScanRes.getDangerLevel(), appScanRes.getVirusName(), appScanRes.getPackageName(), appScanRes.getAppName(), appScanRes.getPath(), 0);
                if (Build.VERSION.SDK_INT >= 23) {
                    SpUtil.getInstance().putBean(AppConstants.VIRUS_MONITOR_APP_INFO, avlAppInfo);
                    Intent intent1 = new Intent(SdCardDownloadService.this, DownloadAlertActivity.class);
                    intent1.putExtra("path", absPath);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SdCardDownloadService.this.startActivity(intent1);
                } else {
                    SpUtil.getInstance().putBean(AppConstants.VIRUS_MONITOR_APP_INFO, avlAppInfo);
                    new DownloadAlertDialog(this, absPath,true).show();
                }
            } else {
                updateScanningView();

            }
        } else {
            updateScanningView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_close:
                handler.sendEmptyMessage(MSG_REMOVE_RESULT_VIEW);
                break;
            case R.id.text_more:
                handler.sendEmptyMessage(MSG_REMOVE_RESULT_VIEW);
                new ScanDownloadMoreDialog(SdCardDownloadService.this, packageInfo, absPath).show();
                break;
            case R.id.text_install:
                handler.sendEmptyMessage(MSG_REMOVE_RESULT_VIEW);
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(absPath)),
                        "application/vnd.android.package-archive");
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //资源回收，如果服务被杀死了
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sdCardDownloadListener1 != null) {
            sdCardDownloadListener1.stopWatching();
            sdCardDownloadListener1 = null;
        }
        if (sdCardDownloadListener2 != null) {
            sdCardDownloadListener2.stopWatching();
            sdCardDownloadListener2 = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        scanningView = null;
        windowManager = null;
        layoutParams = null;
        packageManager = null;
    }
}
