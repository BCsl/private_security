package com.tools.security.clean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.tools.security.common.SecurityApplication;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/20.
 */

public class RunningAppsUtil {
    public static PackageInfo getInstalledPackageInfo(String packageName) {
        PackageManager packageManager = SecurityApplication.getInstance().getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    public static String getAppName(Context context, String packageName) {
        String name = null;
        PackageManager packageManager = context.getPackageManager();
        try {
            name = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static ApplicationInfo getAppInfo(Context context, String packageName) {
        ApplicationInfo applicationInfo = null;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return applicationInfo;
    }

    public static boolean isSystemApp(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 1) != 0;
    }
}
