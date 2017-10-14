package com.tools.security.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.ads.NativeAd;
import com.tools.security.applock.view.AppLockFirstActivity;
import com.tools.security.applock.view.unlock.GestureSelfUnlockActivity;
import com.tools.security.applock.view.unlock.NumberSelfUnlockActivity;
import com.tools.security.bean.FunctionAd;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.statistics.AdvertisingIdClient;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import static com.giftbox.statistic.utiltool.Machine.getNetworkType;

/**
 * Created by lzx on 2016/12/13.
 * email：386707112@qq.com
 * 功能：
 */

public class AppUtils {
    // GooglePaly包名
    public static final String MARKET_PACKAGE = "com.android.vending";
    // 进入软件详细页面
    public static final String GOOGLE_MARKET_APP_DETAIL = "market://details?id=";
    // 浏览器版本的电子市场详情地址
    public static final String BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTP = "http://play.google.com/store/apps/details";
    public static final String BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTPS = "https://play.google.com/store/apps/details";


    public static Pattern compileEmailAddress() {
        return Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "("
                + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
    }

    public static boolean isEmailValid(String email) {
        Pattern emailPattern = compileEmailAddress();
        if (emailPattern.matcher(email).matches()) {
            return true;
        }

        return false;
    }

    public static void hideStatusBar(Window window, boolean enable) {
        WindowManager.LayoutParams p = window.getAttributes();
        if (enable)
            //|=：或等于，取其一
            p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        else
            //&=：与等于，取其二同时满足，     ~ ： 取反
            p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);

        window.setAttributes(p);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static int[] getScreenSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    /**
     * 判断应用是否已经启动
     *
     * @param context     一个context
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            if (processInfos.get(i).processName.equals(packageName)) {
                Log.i("NotificationLaunch",
                        String.format("the %s is running, isAppAlive return true", packageName));
                return true;
            }
        }
        Log.i("NotificationLaunch",
                String.format("the %s is not running, isAppAlive return false", packageName));
        return false;
    }

    /**
     * 获取屏幕的分辨率
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getPhoneHeight(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.heightPixels;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getPhoneWidth(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.widthPixels;
    }

    /**
     * dip转px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return true:可用; false:不可用
     */
    public static boolean isNetworkOK(Context context) {
        if (context == null) {
            return false;
        }

        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                result = true;
            }
        }

        return result;
    }


    /**
     * 跳到应用锁
     *
     * @param activity
     */
    public static void gotoAppLockActivity(Context activity) {
        boolean isFirstLock = SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_FIRST_LOCK, true);
        int lockType = SpUtil.getInstance().getInt(AppConstants.LOCK_TYPE);
        Intent intent;
        if (isFirstLock) { //如果第一次
            intent = new Intent(activity, AppLockFirstActivity.class);
        } else {
            //判断是什么类型的锁屏
            if (lockType == 0) { //图形
                intent = new Intent(activity, GestureSelfUnlockActivity.class);
            } else { //数字
                intent = new Intent(activity, NumberSelfUnlockActivity.class);
            }
        }
        intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, AppConstants.APP_PACKAGE_NAME); //传自己的包名
        intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY);
        activity.startActivity(intent);
        //更新功能最后使用时间
        List<FunctionAd> functionAds = DataSupport.where(" type = ?", "" + FunctionAd.APP_LOCK).find(FunctionAd.class);
        if (functionAds != null && functionAds.size() > 0) {
            FunctionAd functionAd = functionAds.get(0);
            functionAd.setLast_user_time(System.currentTimeMillis());
            functionAd.save();
        }
    }

    /**
     * 打开Google电子市场
     *
     * @param context
     * @param uriString     uri
     * @param isOpenBrowser 是否在未安装Google客户端的情况下使用浏览器
     * @return
     */
    public static boolean gotoGoogleMarket(Context context, String uriString, boolean isOpenBrowser) {
        if (context == null || TextUtils.isEmpty(uriString)) {
            return false;
        }
        // 判断Google市场是否安装
        if (isMarketExist(context)) {
            try {
                // 判断是否为Market开头的Uri,如果不是,则进行转换.
                if (!uriString.startsWith(GOOGLE_MARKET_APP_DETAIL)) {
                    if (uriString.startsWith(BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTP)) {
                        int start = uriString.indexOf("id=");
                        uriString = uriString.substring(start + "id=".length());
                    } else if (uriString.startsWith(BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTPS)) {
                        int start = uriString.indexOf("id=");
                        uriString = uriString.substring(start + "id=".length());
                    } else if (uriString.startsWith("http://") || uriString.startsWith("https://")) {
                        return gotoBrowser(context, uriString);
                    }
                    uriString = GOOGLE_MARKET_APP_DETAIL + uriString;
                }
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));

                marketIntent.setPackage(MARKET_PACKAGE);
                if (context instanceof Activity) {
                    marketIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                } else {
                    marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    marketIntent.addFlags(0X00008000); // Intent.FLAG_ACTIVITY_CLEAR_TASK
                    // need API11
                }
                context.startActivity(marketIntent);
                return true;
            } catch (Exception e) {
                gotoBrowser(context, uriString);
            }
        } else if (isOpenBrowser) {
            // 使用浏览器打开
            return gotoBrowser(context, uriString);
        }
        return false;
    }

    /**
     * 通过浏览器打开Google Market
     *
     * @param context
     * @param uriString
     * @return
     */
    protected static boolean gotoBrowser(Context context, String uriString) {
        if (context == null || TextUtils.isEmpty(uriString)) {
            return false;
        }
        // 修改Ur
        if (!uriString.startsWith(BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTP) && !uriString.startsWith(BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTPS)) {
            // 判断是否为Market或非http://等开始的Url,如果是,则去除.
            if (uriString.startsWith(GOOGLE_MARKET_APP_DETAIL) || (!uriString.startsWith("http://") && !uriString.startsWith("https://"))) {
                if (uriString.startsWith(GOOGLE_MARKET_APP_DETAIL)) {
                    uriString = uriString.replace(GOOGLE_MARKET_APP_DETAIL, "?id=");
                }
                // 将Url修改成https://play.google.com/store/apps/details?id=
                uriString = BROWSER_GOOGLE_MARKET_APP_DETAIL_HTTPS + uriString;
            }
        }
        try {
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            // 获取已安装的浏览器列表
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> resolveList = pm.queryIntentActivities(intent, 0);
            // 获取第一个浏览器启动
            if (resolveList != null && resolveList.size() > 0) {
                ActivityInfo activityInfo = resolveList.get(0) != null ? resolveList.get(0).activityInfo : null;
                String packageName = activityInfo != null ? activityInfo.packageName : null;
                String activityName = activityInfo != null ? activityInfo.name : null;
                if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(activityName)) {
                    intent.setClassName(packageName, activityName);
                }
            }
            if (context instanceof Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(0X00008000); // Intent.FLAG_ACTIVITY_CLEAR_TASK
                // need API11
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 手机上是否有电子市场
     *
     * @param context
     * @return
     */
    protected static boolean isMarketExist(final Context context) {
        return isAppExist(context, MARKET_PACKAGE);
    }


    /**
     * 检测设备中是否有安装指定应用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppExist(final Context context, final String packageName) {
        if (null == context || TextUtils.isEmpty(packageName)) {
            return false;
        }

        boolean result = false;
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SHARED_LIBRARY_FILES);
            result = true;
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
        }
        return version;
    }

    /**
     * 获取Google Advertising Id
     * 注:该方法需要在异步线程中调用,因为AdvertisingIdClient.getAdvertisingIdInfo(mContext)不能在UI线程中执行.
     *
     * @return the device specific Advertising ID provided by the Google Play Services, <em>null</em> if an error occurred.
     */
    private static String sGoogleId;

    public static String getGoogleAdvertisingId(Context context) {
        if (sGoogleId != null) {
            return sGoogleId;
        }
        AdvertisingIdClient.AdInfo adInfo = null;
        try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (adInfo != null) {
            sGoogleId = adInfo.getId();
            return sGoogleId;
        } else {
            return "UNABLE-TO-RETRIEVE";
        }
    }

    /**
     * 获取当前网络状态::->Wi-Fi\GPRS\3G\4G
     *
     * @param context
     * @return
     */
    //==============================网络类型==========================
    //未知网络
    public static final int NETWORK_TYPE_UNKOWN = 0x00;
    //wifi网络
    public static final int NETWORK_TYPE_WIFI = 0x01;
    //2G网络
    public static final int NETWORK_TYPE_2G = 0x02;
    //3G网络
    public static final int NETWORK_TYPE_3G = 0x03;
    //4G网络
    public static final int NETWORK_TYPE_4G = 0x04;
    //其他网络，如热点、代理、以太网等.
    public static final int NETWORK_TYPE_OTHER = 0x05;

    public static String buildNetworkState(Context context) {
        String ret = "unknown";
        //获取网络类型
        int networkType = getNetworkType(context);
        if (networkType == NETWORK_TYPE_UNKOWN) {
            //未知网络
            ret = "unknown";
        } else if (networkType == NETWORK_TYPE_WIFI) {
            //wifi网络
            ret = "wifi";
        } else if (networkType == NETWORK_TYPE_2G) {
            //2G网络
            ret = "2g";
        } else if (networkType == NETWORK_TYPE_3G) {
            //3G网络
            ret = "3g";
        } else if (networkType == NETWORK_TYPE_4G) {
            //4G网络
            ret = "4g";
        } else if (networkType == NETWORK_TYPE_OTHER) {
            //其他网络,如热点、代理、以太网等.
            ret = "other";
        }
        return ret;
    }


    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取设备类型 是平板还是手机
     *
     * @param context
     * @return
     */
    public static int getDeviceType(Context context) {
        boolean isTablet = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        if (isTablet)
            return 1;
        else
            return 0;
    }

    /**
     * 获取可用的内存大小
     *
     * @param context
     * @return
     */
    public static double getAvailableInternalMemorySize(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    /**
     * 获取Rom大小
     *
     * @param context
     * @return
     */
    public static String getRomSpace(Context context) {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockCount = stat.getBlockCount();
            long blockSize = stat.getBlockSize();
            return "" + blockCount * blockSize / 1024 / 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getGmail(Context context) {
        if (Build.VERSION.SDK_INT >= 8) {
            // API level 8+
            Pattern emailPattern = AppUtils.compileEmailAddress();
            Account[] accounts = AccountManager.get(context).getAccounts();

            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches() && account.type.contains("google")) {
                    String possibleEmail = account.name;
                    return possibleEmail;
                }
            }
        }
        return "";
    }

    /**
     * 获取Mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取User Agent
     *
     * @param context
     * @return
     */
    public static String getUserAgent(Context context) {
        return System.getProperty("http.agent");
    }

    //判断是否为模拟器 0 代表 不是 1 代表是
    public static int isEmulator(Context context) {
        String[] args = {"/system/bin/cat", "/procuinfo"};
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            String imsi = tm.getSubscriberId();
            String operatorName = tm.getNetworkOperatorName();
            /*Log.d("xha", "isEmulator() returned: " + "imei=" + tm.getDeviceId() + ",imsi=" + tm.getSubscriberId() + ",model=" + Build.MODEL
                    + ",brand=" + Build.BRAND + ",board=" + Build.BOARD + ",devide=" + Build.DEVICE + ",product=" + Build.PRODUCT + ",hardward=" + Build.HARDWARE+",operationName="+tm.getNetworkOperatorName());*/
            if ((imei != null && imei.equals("000000000000000")) || (imsi != null && imsi.equals("310260000000000")) || (operatorName != null && operatorName.toLowerCase().equals("android"))) {
                return 1;
            }
            if ((Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk")) || Build.BRAND.equals("generic")
                    || Build.BOARD.equals("unknown") || Build.DEVICE.equals("generic")
                    || Build.PRODUCT.equals("sdk") || Build.HARDWARE.equals("goldfish")) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 判断广告是否为Facebook广告
     *
     * @param obj
     */
    public static void isFacebookAd(List<Object> obj) {
        if (!AppConstants.IS_TEST_SERVER) return;
        if (obj != null && obj.size() > 0) {
            if (obj.get(0) instanceof NativeAd) {
                ToastUtil.showShort("Facebook Ad.");
            }
        }
    }

}
