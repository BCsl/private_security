package com.tools.security.bean;

import android.graphics.drawable.Drawable;

/**
 * description:已扫描应用的缓存
 * author: xiaodifu
 * date: 2017/1/15.
 */

public class ScannedApp {
    private Drawable icon;
    private String packageName;
    private String appName;
    private String markStr;
    private int statusColor;
    private String statusStr;

    public ScannedApp() {
    }

    public ScannedApp(Drawable icon, String packageName, String appName,  String markStr, int statusColor, String statusStr) {
        this.icon = icon;
        this.packageName = packageName;
        this.appName = appName;
        this.markStr = markStr;
        this.statusColor = statusColor;
        this.statusStr = statusStr;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMarkStr() {
        return markStr;
    }

    public void setMarkStr(String markStr) {
        this.markStr = markStr;
    }

    public int getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(int statusColor) {
        this.statusColor = statusColor;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }
}
