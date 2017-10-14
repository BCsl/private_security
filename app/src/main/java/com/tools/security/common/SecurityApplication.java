package com.tools.security.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.avl.engine.AVLEngine;
import com.batmobi.Ad;
import com.batmobi.BatAdConfig;
import com.batmobi.lock.ChargeLockSDK;
import com.batmobi.lock.common.LockAdConfig;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.giftbox.statistic.utiltool.UtilTool;
import com.kochava.android.tracker.Feature;
import com.marswin89.marsdaemon.config.DaemonConfig;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.tools.security.applock.view.unlock.GestureUnlockActivity;
import com.tools.security.applock.view.unlock.NumberUnlockActivity;
import com.tools.security.main.MainActivity;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.statistics.ScheduleTaskHandler;
import com.tools.security.utils.volley.request.RequestManager;

import org.litepal.LitePalApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * description: 应用上下文
 * author: xiaodifu
 * date: 2016/12/12.
 */

public class SecurityApplication extends LitePalApplication {
    private static SecurityApplication application;
    private static final String TAG = "SECURITY";
    private static String sOperatorName;
    private Feature kTracker;
    private static List<BaseActivity> activityList; //acticity管理

    @Override
    public void onCreate() {
        super.onCreate();
//        LeakCanary.install(this);
        application = this;
        activityList = new ArrayList<>();
        //异常初始化
        AppExceptionHandler.getInstance().init(application);
        //SharePrefence工具类初始化
        SpUtil.getInstance().init(application);
        //初始化日志打印库
        Logger.init(TAG).logLevel(AppConstants.IS_TEST_SERVER ? LogLevel.FULL : LogLevel.NONE);
        RequestManager.init(application);//Volley初始化
        /*-------------------BATMOBI，充电锁屏---------------------*/
        BatAdConfig cfg = new BatAdConfig();
        cfg.setCreatives(Ad.AD_CREATIVE_SIZE_1200x627);
        LockAdConfig lockAdConfig = new LockAdConfig(AppConstants.BATMOBI_CHARGE_LOCK_PLACEMENT_ID, AppConstants.BATMOBI_SCREEN_LOCK_PLACEMENT_ID, AppConstants.BATMOBI_NOTIFICATION_PLACEMENT_ID);
        ChargeLockSDK.init(getApplicationContext(), AppConstants.BATMOBI_APPKEY, cfg, lockAdConfig);
        ChargeLockSDK.setUserSwitch(getApplicationContext(), false);
        ChargeLockSDK.setScreenLockSwitch(getApplicationContext(), false);
        ChargeLockSDK.setOnChargeLockLaunchListener(new ChargeLockSDK.OnChargeLockLaunchListener() {
            @Override
            public void onChargeLockLaunch(int type) {
                //锁屏启动时回调，1-充电锁屏，2-普通锁屏
            }
        });
        /*----------------------------------------------------*/

        //启动基础信息统计
        ScheduleTaskHandler.getScheduleTaskHandler(application).startTask();
        UtilTool.enableLog(AppConstants.IS_TEST_SERVER);
        //初始化kochava
        initKochava();
        //AVL初始化
        int result = AVLEngine.init(application);
        Log.d("TAG","result:" + result);
        AVLEngine.setLanguage(application, AVLEngine.LANGUAGE_ENGLISH);
        SpUtil.getInstance().putBoolean(AppConstants.AVL_INIT_RESULT_SUCCESS, result == 0);

        //facebook初始化
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public void doForCreate(BaseActivity activity) {
        activityList.add(activity);
    }

    public void doForFinish(BaseActivity activity) {
        activityList.remove(activity);
    }

    public void clearAllActivity() {
        try {
            for (BaseActivity activity : activityList) {
                if (activity != null && !clearAllWhiteList(activity))
                    activity.clear();
            }
            activityList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean clearAllWhiteList(BaseActivity activity) {
        return activity instanceof NumberUnlockActivity || activity instanceof GestureUnlockActivity;
    }

    private void initKochava() {
//        Feature.enableDebug(true);
//        Feature.setErrorDebug(true);
        kTracker = new Feature(getApplicationContext(), AppConstants.IS_TEST_SERVER ? AppConstants.KOCHAVA_APP_GUID_TEST : AppConstants.KOCHAVA_APP_GUID);
    }

    /**
     * 获取Kochava实例
     *
     * @return
     */
    public static Feature getTracker() {
        return getInstance().kTracker;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DaemonConfig.getInstance().daemon(base);
    }

    /**
     * 获取AppContext实例
     *
     * @return
     */
    public static SecurityApplication getInstance() {
        return application;
    }

    /**
     * 退出所有Activity
     */
    public void exit() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //重启应用
    public void restartApp() {
        SecurityApplication.getInstance().exit();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            PackageManager pm = application.getPackageManager();
            pi = pm.getPackageInfo(application.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    //保存日志
    public void saveLog(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = ex.getCause();
        }
        printWriter.close();

        String result = writer.toString();
        sb.append(result);

        DateFormat mDateFormat = new SimpleDateFormat("MM_dd_HH_mm_ss");
        String filename = mDateFormat.format(new Date()) + ".txt";
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = "/sdcard/crash";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(new File(dir, filename));
                fos.write(sb.toString().getBytes());
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    //版本名
    public String getVersionName() {
        return getPackageInfo().versionName;
    }

    //版本号
    public int getVersionCode() {
        return getPackageInfo().versionCode;
    }

    public String getOperatorName() {
        if (!TextUtils.isEmpty(sOperatorName)) {
            return sOperatorName;
        }
        try {
            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            sOperatorName = manager.getNetworkOperatorName();
            return sOperatorName;
        } catch (Throwable e) {
        }
        return sOperatorName;
    }

    public int getOsVersion() {
        return Build.VERSION.SDK_INT;
    }

    public String getScreenSize() {
        final WindowManager windowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        final DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        String dpi = metrics.widthPixels + "*" + metrics.heightPixels;
        return dpi;
    }
}
