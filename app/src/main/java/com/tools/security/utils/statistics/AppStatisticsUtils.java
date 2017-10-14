package com.tools.security.utils.statistics;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;

import com.tools.security.utils.AppUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppStatisticsUtils {
    private static String sAppVersion;
    private static int sAppVersionCode = -2;
    private static String sAndroidId = "";
    private static String sImsi = "";
    private static String sCarrier = "";
    private static boolean sCarrierInited;
    private static String sIMEI = "";
    private static String sModel = "";
    public static final int SPTYPE_MOBILE = 0; // 中国移动
    public static final int SPTYPE_UNICOM = 1; // 中国联通
    public static final int SPTYPE_TELECOM = 2; // 中国电信
    private static String sAdvertisingId = "";

    public static final String NETWORK_WIFI = "WIFI";
    public static final String NETWORK_2G = "2g";
    public static final String NETWORK_3G = "3g";
    public static final String NETWORK_4G = "4g";
    public static final String NETWORK_UNKOWN = "unknown";
    private static final String PARAM_BATMOBI_FILE_LATEST = "batmobi_update_file_latest";
    private static final String PARAM_UPDATE_FILE_TIME = "batmobi_dynamic_update_time";
    private static final String PARAM_GOOGLE_ADVERTISINGID = "batmobi_google_advertisingId";
    private static final String SHAREDPREFERENCES_BATMOBI_SETTINGS = "sharedpreferences_batmobi_settings";
    // GooglePaly包名
    public static final String MARKET_PACKAGE = "com.android.vending";

    /**
     * 获取android id
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        if (TextUtils.isEmpty(sAndroidId)) {
            sAndroidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
       // Logger.d("安卓id = " + sAndroidId);
        return sAndroidId;
    }

    /**
     * 获取国家
     *
     * @param context
     * @return
     */
    public static String getCountry(Context context) {
        String ret = "";

        /*try {
            TelephonyManager telManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telManager != null) {
                ret = telManager.getSimCountryIso().toUpperCase();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }*/

        ret = Locale.getDefault().getCountry().toUpperCase();
        if (TextUtils.isEmpty(ret)) {
            ret = "ZZ";
        }
        return null == ret ? "error" : ret;
    }

    public static String getIMEI(Context context) {
        if (TextUtils.isEmpty(sIMEI)) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                sIMEI = telephonyManager.getDeviceId();
            } catch (Throwable e) {
            }
        }
        return sIMEI;
    }

    /**
     * 获取imsi
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        if (TextUtils.isEmpty(sImsi)) {
            try {
                TelephonyManager mTelephonyMgr = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                sImsi = mTelephonyMgr.getSubscriberId().equals("null") ? "" : mTelephonyMgr.getSubscriberId();
            } catch (Throwable e) {
            }
        }
        return sImsi;
    }

    /**
     * 获取用户运营商代码
     */
    public static String getImsi(Context context) {
        String simOperator = "000";
        try {
            if (context != null) {
                // 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
                TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                simOperator = manager.getSimOperator();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return simOperator;
    }

    /**
     * 获取机型
     *
     * @return String 品牌和型号，如 ：HUAWEI G750-T20
     */
    public static String getModelName() {
        if (TextUtils.isEmpty(sModel)) {
            sModel = getSystemProperties("ro.yunos.model"); // YunOS的机型名称
            if (TextUtils.isEmpty(sModel)) {
                sModel = android.os.Build.MODEL;
            }
        }
        return sModel;
    }

    /**
     * 获取手机的版本名 比如：6.0.1
     *
     * @param
     * @return
     */
    public static String getPhoneName() {
        return VERSION.RELEASE;
    }

    /**
     * 获取手机的版本名 比如：23
     *
     * @param
     * @return
     */
    public static String getPhoneCode() {
        return VERSION.SDK;
    }

    public static String getSystemProperties(String prop) {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getDeclaredMethod("get", String.class,
                    String.class);
            String value = (String) method.invoke(null, prop, "");
            return value;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取总运存大小
     *
     * @param
     * @return
     */
    public static long getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue();// 获得系统总内存，单位是KB
            localBufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return initial_memory / 1024;
    }

    /**
     * 获取cpu个数
     *
     * @return
     */
    public static int getCPU() {
        return Runtime.getRuntime().availableProcessors();
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
     * 获取总的内存大小
     *
     * @return
     */
    public static double getTotalInternalMemorySize() {
        String str1 = "/proc/meminfo";
        String str2 = "0";
        FileReader fr = null;
        BufferedReader localBufferedReader = null;
        try {
            fr = new FileReader(str1);
            localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                // Log.i(TAG, "---" + str2);
                Pattern p = Pattern.compile("(\\d+)");
                Matcher m = p.matcher(str2);
                if (m.find()) {
                    str2 = m.group(1);
                }
                return Long.valueOf(str2) * 1000;
            }
            localBufferedReader.close();
        } catch (IOException e) {

        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (localBufferedReader != null) {
                    localBufferedReader.close();
                }
            } catch (IOException e) {
            }
        }
        return 0;
    }

    /**
     * 获取内存的占用率
     *
     * @param context
     * @return
     */
    public static String getMemoryRate(Context context) {
        double rate = 1 - getAvailableInternalMemorySize(context)
                / getTotalInternalMemorySize();
        NumberFormat nt = NumberFormat.getPercentInstance();
        // 设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(2);
        return nt.format(rate);
    }

    /**
     * 获取cpu的使用率
     *
     * @return
     */
    public static String getProcessCpuRate() {
        long cpuTime = getAppCpuTime();
        long totalCpuTime = getTotalCpuTime();
        String rate = String.valueOf(cpuTime * 1.0 / totalCpuTime).substring(0, 4) + "%";
        return rate;
    }

    public static long getTotalCpuTime() { // 获取系统总CPU使用时间
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long totalCpu = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        return totalCpu;
    }

    public static long getAppCpuTime() { // 获取应用占用的CPU时间
        String[] cpuInfos = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }

    /**
     * 获取屏幕宽高
     *
     * @param context
     * @return
     */
    public static String getScreenSize(Context context) {
        if (null == context) {
            return "";
        }

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();// 手机屏幕的宽度
        int height = wm.getDefaultDisplay().getHeight();// 手机屏幕的高度
        return width + "x" + height;
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
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                result = true;
            }
        }

        return result;
    }

    /**
     * 网络详细类别，GPRS/EDGE/LTE等
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isAvailable()
                        && info.getState() == NetworkInfo.State.CONNECTED) {
                    switch (info.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            return NETWORK_WIFI;
                        case ConnectivityManager.TYPE_MOBILE:
                            switch (info.getSubtype()) {
                                case TelephonyManager.NETWORK_TYPE_GPRS:
                                    return "GPRS";
                                case TelephonyManager.NETWORK_TYPE_EDGE:
                                    return "EDGE";
                                case TelephonyManager.NETWORK_TYPE_CDMA:
                                    return "CDMA";
                                case TelephonyManager.NETWORK_TYPE_1xRTT:
                                    return "1xRTT";
                                case TelephonyManager.NETWORK_TYPE_IDEN:
                                    return "IDEN";
                                case TelephonyManager.NETWORK_TYPE_UMTS:
                                    return "UMTS";
                                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                                    return "EVDO0";
                                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                                    return "EVDOA";
                                case TelephonyManager.NETWORK_TYPE_HSDPA:
                                    return "HSUPA";
                                case TelephonyManager.NETWORK_TYPE_HSUPA:
                                    return "HSUPA";
                                case TelephonyManager.NETWORK_TYPE_HSPA:
                                    return "HSPA";
                                case 12:// 兼容SDK8
                                    return "EVDOB";
                                case 14:
                                    return "EHRPD";
                                case 15:
                                    return "HSPAP";
                                case 13:
                                    return "LTE";
                                case 16:
                                    return "GSM";
                                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                                    return NETWORK_UNKOWN;
                            }
                        case ConnectivityManager.TYPE_MOBILE_DUN:
                        case ConnectivityManager.TYPE_MOBILE_HIPRI:
                        case ConnectivityManager.TYPE_MOBILE_MMS:
                        case ConnectivityManager.TYPE_MOBILE_SUPL:
                        case ConnectivityManager.TYPE_WIMAX:
                            return NETWORK_UNKOWN;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return NETWORK_UNKOWN;
        }

        return NETWORK_UNKOWN;
    }

    public static String getLauguage(Context context) {
        Locale locale = Locale.getDefault();
        String language = String.format("%s_%s", locale.getLanguage()
                .toLowerCase(), locale.getCountry().toLowerCase());
        return language;
    }

    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getApplicationInfo().packageName;
    }

    /**
     * 获取Android SDK版本
     *
     * @param context
     * @return
     */
    public static int getOsVersion(Context context) {
        return VERSION.SDK_INT;
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
     * 纬度
     *
     * @param context
     * @return
     */
    public static String getLatitude(Context context) {
        Location location = getLastKnownLocation(context);
        if (location != null) {
            return String.valueOf(location.getLatitude());
        } else {
            return "";
        }
    }

    /**
     * 经度
     *
     * @param context
     * @return
     */
    public static String getLongitude(Context context) {
        Location location = getLastKnownLocation(context);
        if (location != null) {
            return String.valueOf(location.getLongitude());
        } else {
            return "";
        }
    }

    /**
     * 获取地理位置
     *
     * @param context
     * @return
     */
    public static Location getLastKnownLocation(Context context) {
        Location result = null;
        try {
            LocationManager locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null) {
                return null;
            }
            Location gpsLocation = null;
            try {
                gpsLocation = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
            } catch (IllegalArgumentException e) {
            }

            Location networkLocation = null;
            try {
                networkLocation = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } catch (SecurityException e) {
            } catch (IllegalArgumentException e) {
            }

            if (gpsLocation == null && networkLocation == null) {
                return null;
            }

            if (gpsLocation != null && networkLocation != null) {
                if (gpsLocation.getTime() > networkLocation.getTime()) {
                    result = gpsLocation;
                } else {
                    result = networkLocation;
                }
            } else if (gpsLocation != null) {
                result = gpsLocation;
            } else {
                result = networkLocation;
            }
        } catch (Throwable e) {

        }
        return result;
    }

    /**
     * 获取应用版本
     *
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        if (sAppVersion == null) {
            initAppVersion(context);
        }
        return sAppVersion;
    }

    private static void initAppVersion(Context context) {
        if (context == null) {
            sAppVersion = "0.0";
            return;
        }
        try {
            sAppVersion = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            sAppVersion = "0.0";
        }
    }

    /**
     * 获取应用版本
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        if (sAppVersionCode < 0) {
            initAppVersionCode(context);
        }
        return sAppVersionCode;
    }

    /**
     * @param context
     */
    private static void initAppVersionCode(Context context) {
        if (context == null) {
            sAppVersionCode = 0;
            return;
        }
        try {
            sAppVersionCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            sAppVersionCode = 0;
        }
    }

    /**
     * 获取时区
     *
     * @param context
     * @return
     */
    public static int getTZ(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getRawOffset() / (60 * 60 * 1000);
    }

    /**
     * 获取运营商
     *
     * @param context
     * @return
     */
    public static String getCarrier(Context context) {
        if (!sCarrierInited) {
            initCarrier(context);
        }
        return sCarrier;
    }

    private static void initCarrier(Context context) {
        if (context == null) {
            sCarrierInited = true;
            return;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager == null) {
            sCarrierInited = true;
            return;
        }
        sCarrier = telephonyManager.getNetworkOperator();
        if (TextUtils.isEmpty(sCarrier)) {
            sCarrier = "";
        }
        sCarrierInited = true;
    }

    /**
     * 获取横竖屏参数
     *
     * @param context
     * @return
     */
    public static String getOrientation(Context context) {
        if (context == null) {
            return "";
        }
        int orientation = context.getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            return "landscape";
        } else {
            return "portrait";
        }
    }

    /**
     * 获取存取数据
     *
     * @param
     * @param context
     * @return
     */
    public static SharedPreferences getSharedPreferences(String name,
                                                         Context context) {
        if (null != context) {
            SharedPreferences preferences = null;
            if (VERSION.SDK_INT > 10)
                preferences = context.getSharedPreferences(name, 0x0004);
            else
                preferences = context.getSharedPreferences(name,
                        Context.MODE_PRIVATE);
            return preferences;
        }
        return null;
    }

    /**
     * 获取已经安装的应用列表
     *
     * @param context
     * @return
     */
    public static Set<String> getInstalledApps(Context context) {
        Set<String> apps = new HashSet<String>();
        if (null != context) {
            List<PackageInfo> packageInfos = context.getPackageManager()
                    .getInstalledPackages(0);

            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo packageInfo = packageInfos.get(i);

                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    // 非系统的应用
                    apps.add(packageInfo.packageName);
                } else if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    // 本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
                    apps.add(packageInfo.packageName);
                }
            }
        }
        return apps;
    }

    /**
     * 获取GoogleAdvertisingID
     *
     * @param context
     * @return
     */
    public static String getAdvertisingId(Context context) {
        if (null == sAdvertisingId) {
            sAdvertisingId = AppUtils.getGoogleAdvertisingId(context);
        }
        return sAdvertisingId;
    }

    /**
     * 获取已安装列表
     *
     * @param context
     * @return
     */
    public static String getInstallAppsAsString(Context context) {
        StringBuilder str_blt = new StringBuilder();
        try {
            Set<String> apps = getInstalledApps(context);
            for (String app : apps) {
                str_blt.append(',').append(app);
            }
            if (str_blt.length() > 0) {
                str_blt.deleteCharAt(0);
            }
        } catch (Throwable e) {
        }
        return str_blt.toString();
    }

    /**
     * 获取最新更新的file
     *
     * @param context
     * @return
     */
    public static String getLastestFileName(Context context) {
        if (null == context) {
            return null;
        }
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_BATMOBI_SETTINGS, context);
        return preferences.getString(PARAM_BATMOBI_FILE_LATEST, "");
    }

    /**
     * 设置更新的file
     *
     * @param context
     * @param fileName
     */
    public static void setLastestFileName(Context context, String fileName) {
        if (null == context || TextUtils.isEmpty(fileName)) {
            return;
        }
        Editor editor = getSharedPreferences(
                SHAREDPREFERENCES_BATMOBI_SETTINGS, context).edit();
        editor.putString(PARAM_BATMOBI_FILE_LATEST, fileName);
        editor.commit();
    }

    /**
     * 获取更新文件时间
     *
     * @param context
     */
    public static long getUpdateFileTime(Context context) {
        if (null == context) {
            return 0;
        }
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_BATMOBI_SETTINGS, context);
        return preferences.getLong(PARAM_UPDATE_FILE_TIME, 0);
    }

    /**
     * 设置更新时间
     *
     * @param context
     * @return
     */
    public static void setUpdateFileTime(Context context, long updatetime) {
        if (null == context || updatetime < 1) {
            return;
        }
        Editor editor = getSharedPreferences(
                SHAREDPREFERENCES_BATMOBI_SETTINGS, context).edit();
        editor.putLong(PARAM_UPDATE_FILE_TIME, updatetime);
        editor.commit();
    }

    /**
     * 获取配置设置
     *
     * @param context
     * @return
     */
    public static SharedPreferences getSettingsSharedPreferences(Context context) {
        return getSharedPreferences(SHAREDPREFERENCES_BATMOBI_SETTINGS, context);
    }

    /**
     * 获取meta信息
     *
     * @param context
     * @param key
     * @return
     */
    public static String getValueFromMainifest(Context context, String key) {
        if (null == context || TextUtils.isEmpty(key)) {
            return null;
        }
        String value = "";
        try {
            value = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA).metaData
                    .getString(key);
            if (null == value) {
                int v = context.getPackageManager().getApplicationInfo(
                        context.getPackageName(), PackageManager.GET_META_DATA).metaData
                        .getInt(key, 0);
                if (v > 0) {
                    value = String.valueOf(v);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 设置meta-data数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setValueToMainifest(Context context, String key, String value) {
        ApplicationInfo appi;
        if (TextUtils.isEmpty(key)) {
            return;
        }
        try {
            appi = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            appi.metaData.putString(key, value);
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 获取存储自升级的jar
     *
     * @return
     */
    public static File getFolder(Context context) {
        String path = context.getFilesDir().getAbsolutePath();
        File dir = new File(path, "batfiles");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取文件
     *
     * @param fileName
     * @return
     */
    public static File getFile(Context context, String fileName) {
        return new File(getFolder(context), fileName);
    }

    /**
     * 判断应用是否存在(安装)
     *
     * @param context
     * @param packageName 应用包名
     * @return
     */
    public static boolean isAppExist(final Context context, final String packageName) {
        if (context == null || packageName == null) {
            return false;
        }
        boolean result = false;
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SHARED_LIBRARY_FILES);
            result = true;
        } catch (NameNotFoundException e) {
            result = false;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }


    /**
     * 通过反射获取gmail的帐号，由于比较耗时，如果拿到帐号后就缓存起来
     *
     * @param context
     * @return
     */
    private static ArrayList<String> mGmail = null;

    public static String getGmail(Context context) {
        if (null == mGmail) {
            mGmail = new ArrayList<String>();
            try {
                ClassLoader classLoader = context.getClassLoader();

                Class<?> accountManagerClass = classLoader.loadClass("android.accounts.AccountManager");
                Method get = accountManagerClass.getDeclaredMethod("get", Context.class);
                Object accountManagerInstance = get.invoke(null, context);
                Method getAccounts = accountManagerClass.getDeclaredMethod("getAccountsByType", String.class);
                Object[] accounts = (Object[]) getAccounts.invoke(accountManagerInstance, "com.google");
                String temp = null;
                for (int i = 0; i < accounts.length; i++) {
                    temp = (String) accounts[i].getClass().getDeclaredField("name").get(accounts[i]);
                    if (!TextUtils.isEmpty(temp) && !mGmail.contains(temp)) {
                        mGmail.add(temp);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String gmail = mGmail.toString();
        return gmail.substring(1, gmail.length() - 1);
    }


    /**
     * 获取北京时间
     *
     * @param milliseconds
     * @return
     */
    public static String getBeiJinTime(long milliseconds) {
        String stamp = "";
        try {
            Date now = new Date(milliseconds);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            stamp = dateFormat.format(now);
        } catch (Exception e) {
        }
        return stamp;
    }
}
