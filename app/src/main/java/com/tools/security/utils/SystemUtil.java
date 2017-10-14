package com.tools.security.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.tools.security.service.MyAccessibilityService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 手机系统相关信息工具类
 *
 * @author hogan
 */
public class SystemUtil {

    /**
     * 获取androidID
     *
     * @param context
     * @return
     */
    public static String getAndroidID(Context context) {
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * 获取国家名称
     *
     * @param context
     * @return
     */
    public static String getLocal(Context context) {
        String ret = null;
       /* try {
            TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telManager != null) {
                ret = telManager.getSimCountryIso().toUpperCase();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(ret)) {
            ret = Locale.getDefault().getCountry().toUpperCase();
        }*/
        ret = Locale.getDefault().getCountry().toUpperCase();
        if (TextUtils.isEmpty(ret)) {
            ret = "ZZ";
        }
        return null == ret ? "error" : ret;
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
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

    public static boolean isSystemPackage(Context context, String packageName) {
        PackageInfo packageInfo;
        return (packageInfo = getPackageInfo(context, packageName)) == null ? false : (packageInfo.applicationInfo.flags & 1) != 0;
    }

    public static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;

        try {
            packageInfo = packageManager.getPackageInfo(packageName, 4224);
        } catch (PackageManager.NameNotFoundException var3) {
            ;
        }

        return packageInfo;
    }

    public static List<PackageInfo> getLocalAppsPkgInfo(Context context) {
        final int MAX_ATTEMPTS = 3;
        for (int i = 0; i < MAX_ATTEMPTS; i++) {

            try {
                List<PackageInfo> pkgInfoList = context.getPackageManager().getInstalledPackages(
                        PackageManager.GET_PERMISSIONS | PackageManager.GET_PROVIDERS);

                return pkgInfoList;
            } catch (RuntimeException re) {

                // Just wait for cooling down
                try {
                    Thread.sleep(100);
                } catch (Exception e) {

                }
            }
        }
        return new ArrayList<PackageInfo>();
    }

    public static boolean appExist(String packageName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getApplicationInfo(packageName, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 卸载指定包名的应用
     *
     * @param packageName
     */
    public static void uninstall(Context context, String packageName, boolean needNewTask) {
        if (checkApplication(context, packageName)) {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent intent = new Intent(Intent.ACTION_DELETE);
            if (needNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(packageURI);
            context.startActivity(intent);
        }
    }

    /**
     * 判断该包名的应用是否安装
     *
     * @param packageName
     * @return
     */
    public static boolean checkApplication(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }


    /**
     * 检测辅助功能是否开启
     *
     * @param mContext
     * @return boolean
     */
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + MyAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
        }
        return false;
    }

    /**
     * 是否有开启通知栏服务
     */
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    public static boolean isNotificationSettingOn(Context mContext) {
        String pkgName = mContext.getPackageName();
        final String flat = Settings.Secure.getString(mContext.getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void addShortcut(Activity activity, String name, int resId, Class targetClazz) {
        if (hasShortcut(activity, name)) return;
        Intent addShortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        // 不允许重复创建
        addShortcutIntent.putExtra("duplicate", false);

        // 名字
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        // 图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(activity,
                        resId));

        // 设置关联程序
        Intent launcherIntent = new Intent();
        launcherIntent.setClass(activity, targetClazz);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

        // 发送广播
        activity.sendBroadcast(addShortcutIntent);
    }

    /**
     * 判断当前应用在桌面是否有桌面快捷方式
     *
     * @param cx
     */
    public static boolean hasShortcut(Context cx, String title) {
        boolean result = false;

        final String uriStr;
        if (android.os.Build.VERSION.SDK_INT < 8) {
            uriStr = "content://com.android.launcher.settings/favorites?notify=true";
        } else {
            uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
        }
        final Uri CONTENT_URI = Uri.parse(uriStr);
        final Cursor c = cx.getContentResolver().query(CONTENT_URI, null,
                "title=?", new String[]{title}, null);
        if (c != null && c.getCount() > 0) {
            result = true;
        }
        return result;
    }

    /**
     * 安装应用
     *
     * @param context
     * @param file      文件
     * @param isNewTask 是否要设newtask flag
     */
    public static void installFromFile(Context context, File file, boolean isNewTask) {
        Intent intent = new Intent();
        if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
