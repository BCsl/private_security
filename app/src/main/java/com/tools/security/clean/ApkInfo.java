package com.tools.security.clean;

/**
 * Author：wushuangshuang on 16/12/6 15:27
 * Function：安装包信息
 */
public class ApkInfo {
    private String mApkName; // apk的名称
    private String mApkPkgName; // apk的包名
    private String mApkVersion; // apk的版本信息
    private boolean mIsInstalled; // 是否已安装

    public String getApkName() {
        return mApkName;
    }

    public void setApkName(String mApkName) {
        this.mApkName = mApkName;
    }

    public String getApkPkgName() {
        return mApkPkgName;
    }

    public void setApkPkgName(String mApkPkgName) {
        this.mApkPkgName = mApkPkgName;
    }

    public String getApkVersion() {
        return mApkVersion;
    }

    public void setApkVersion(String mApkVersion) {
        this.mApkVersion = mApkVersion;
    }

    public void setIsInstalled(boolean isInstalled) {
        mIsInstalled = isInstalled;
    }

    public boolean isInstalled() {
        return mIsInstalled;
    }
}

