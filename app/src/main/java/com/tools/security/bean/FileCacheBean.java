package com.tools.security.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Author：wushuangshuang on 16/10/20 17:16
 * Function：文件缓存数据bean
 */
public class FileCacheBean extends DataSupport implements Serializable, Comparable<FileCacheBean> {
    private FileCacheType mType; // 文件类型
    private String mTitle; // 文件标题
    private String mPackageName; // 包名：仅当是应用时才不为空
    private String mDescription; // 文件描述
    private float mCacheSize; // 文件缓存大小
    private CacheUnit mCacheUnit; // 文件单位
    private String mCachePath; // 缓存路径
    private int mContainFileCount; // 包含文件个数
    private int mContainFolderCount; // 包含文件夹个数
    private boolean mIsChecked; // 是否被选中
    private String mSecondTitle; // 备用名称
    private String mApkAppVersion; // Apk的版本信息
    private boolean mApkIsInstalled; // apk是否安装
    private String mAppVersion; // 安装应用版本信息
    private boolean mIsHadSeondName; // 是否有备用名

    public boolean isIsChecked() {
        return mIsChecked;
    }

    public void setIsChecked(boolean mIsChecked) {
        this.mIsChecked = mIsChecked;
    }

    public FileCacheType getType() {
        return mType;
    }

    public void setType(FileCacheType mType) {
        this.mType = mType;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public float getCacheSize() {
        return mCacheSize;
    }

    public void setCacheSize(float mCacheSize) {
        this.mCacheSize = mCacheSize;
    }

    public CacheUnit getCacheUnit() {
        return mCacheUnit;
    }

    public void setCacheUnit(CacheUnit mCacheUnit) {
        this.mCacheUnit = mCacheUnit;
    }

    public String getCachePath() {
        return mCachePath;
    }

    public void setCachePath(String mCachePath) {
        this.mCachePath = mCachePath;
    }

    public int getContainFileCount() {
        return mContainFileCount;
    }

    public void setContainFileCount(int mContainFileCount) {
        this.mContainFileCount = mContainFileCount;
    }

    public int getContainFolderCount() {
        return mContainFolderCount;
    }

    public void setContainFolderCount(int mContainFolderCount) {
        this.mContainFolderCount = mContainFolderCount;
    }

    public String getSecondTitle() {
        return mSecondTitle;
    }

    public void setSecondTitle(String mSecondTitle) {
        this.mSecondTitle = mSecondTitle;
    }

    public boolean isIsHadSeondName() {
        return mIsHadSeondName;
    }

    public void setIsHadSeondName(boolean mIsHadSeondName) {
        this.mIsHadSeondName = mIsHadSeondName;
    }

    public String getApkAppVersion() {
        return mApkAppVersion;
    }

    public void setApkAppVersion(String mAppVersion) {
        this.mApkAppVersion = mAppVersion;
    }

    public String getAppVersion() {
        return mAppVersion;
    }

    public void setAppVersion(String appVersion) {
        mAppVersion = appVersion;
    }

    public boolean isApkInstalled() {
        return mApkIsInstalled;
    }

    public void setApkIsInstalled(boolean isInstalled) {
        mApkIsInstalled = isInstalled;
    }

    @Override
    public int compareTo(FileCacheBean another) {
        String title = "System Cache";

        if (this.getTitle() != null && this.getTitle().equals(title)) {
            return 1;
        }

        if (another.getTitle() != null && another.getTitle().equals(title)) {
            return -1;
        }

        if (this.getCacheSize() > another.getCacheSize()) {
            return 1;
        } else if (this.getCacheSize() < another.getCacheSize()) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 缓存单位
     */
    public enum CacheUnit {
        B("B"),
        KB("KB"),
        MB("MB"),
        GB("GB");

        private String mValue;

        CacheUnit(String value) {
            mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }

    /**
     * 文件缓存类型
     */
    public enum FileCacheType {
        SYSCACHEJUNK("System Cache Junk"), // 系统缓存
        CACHEJUNK("Cache Junk"), // 应用缓存
        ADJUNK("AD Junk"), // 广告文件
        RESIDUALFILES("Residual Files"), // 残留文件
        SYSTEMPFILES("System Temp Files"), // 系统临时文件
        OBSOLETEAPK("Obsolete Apks"), // 残留的安装包
        RESIDUALLOGS("Residual Logs"), // 残留日志
        MEMORYJUNK("Memory Junk"), // 内存清理
        BIGFILES("Big Files"), // 大文件
        NOTEXITS("Not Exits"); // 不存在的

        private String mValue;

        FileCacheType(String value) {
            mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }
}
