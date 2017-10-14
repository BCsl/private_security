package com.tools.security.clean;

import android.os.AsyncTask;
import android.os.Environment;

import com.tools.security.bean.FileCacheBean;
import com.tools.security.bean.FileCacheGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Function：系统残留、临时、日志扫描器
 */
public class AllSystemTempFileScanTask extends AsyncTask<Void, Void, Void> {
    private final int SCAN_LEVEL = 3; // 扫描的层数
    private IScanCallback mScanCallback;
    private FileCacheGroup mFileCacheGroup;
    private FileCacheBean.FileCacheType mFileCacheType;
    private List<FileCacheBean> mSystemTempBeans; // 系统临时文件
    private List<FileCacheBean> mObsoleteApkBeans; // 残留的安装包
    private List<FileCacheBean> mResidualLogBeans; // 残留日志

    private List<String> mFileTitles;
    private Random mRandom;

    public AllSystemTempFileScanTask(IScanCallback callback, FileCacheBean.FileCacheType type) {
        mScanCallback = callback;
        mFileCacheGroup = new FileCacheGroup();
        mFileCacheType = type;
        mSystemTempBeans = new ArrayList<FileCacheBean>();
        mObsoleteApkBeans = new ArrayList<FileCacheBean>();
        mResidualLogBeans = new ArrayList<FileCacheBean>();
        mFileTitles = new ArrayList<String>();
        mRandom = new Random();
    }

    private void travelPath(File root, int level) {
        if (root == null || !root.exists() || level > SCAN_LEVEL) {
            return;
        }

        File[] lists = root.listFiles();
        if (lists == null || lists.length == 0) {
            return;
        }

        for (File file : lists) {
            if (file.isFile()) {
                String name = file.getName();
                FileCacheBean bean = null;
                if (name.endsWith(".apk") && mFileCacheType == FileCacheBean.FileCacheType.OBSOLETEAPK) {
                    bean = new FileCacheBean();
                    bean.setCacheSize(file.length());
                    if (mFileTitles.contains(name)) {
                        bean.setIsHadSeondName(true);
                        bean.setSecondTitle(name);
                        int endIndex = name.indexOf(".apk");
                        String temp = name.substring(0, endIndex);
                        temp += mRandom.nextInt(100);
                        name = temp + ".apk";

                    }
                    mFileTitles.add(name);
                    // TODO 解析出 apk 文件详细内容
                    bean.setTitle(name);
                    bean.setType(mFileCacheType);
                    bean.setCachePath(file.getAbsolutePath());
                    ApkInfo info = FileUtils.getApkDrawable(bean.getCachePath());
                    if (info == null) {
                        continue;
                    }
                    String apkVersion = info.getApkVersion();
                    String apkPkgName = info.getApkPkgName();
                    bean.setApkIsInstalled(info.isInstalled());
                    if (apkVersion != null) {
                        bean.setApkAppVersion(apkVersion);
                    }

                    if (apkPkgName != null) {
                        bean.setPackageName(apkPkgName);
                    }
                    mObsoleteApkBeans.add(bean);
                    mFileCacheGroup.setTotalCacheSize(bean.getCacheSize() + mFileCacheGroup.getTotalCacheSize());
                } else if (name.endsWith(".log") && mFileCacheType == FileCacheBean.FileCacheType.RESIDUALLOGS) {
                    bean = new FileCacheBean();
                    if (mFileTitles.contains(name)) {
                        bean.setIsHadSeondName(true);
                        bean.setSecondTitle(name);
                        int endIndex = name.indexOf(".log");
                        String temp = name.substring(0, endIndex);
                        temp += mRandom.nextInt(100);
                        name = temp + ".log";
                    }
                    mFileTitles.add(name);
                    bean.setTitle(name);
                    bean.setType(mFileCacheType);
                    bean.setCacheSize(file.length());
                    bean.setCachePath(file.getAbsolutePath());
                    mResidualLogBeans.add(bean);
                    mFileCacheGroup.setTotalCacheSize(bean.getCacheSize() + mFileCacheGroup.getTotalCacheSize());
                } else if ((name.endsWith(".tmp") || name.endsWith(".temp")) && mFileCacheType ==
                        FileCacheBean.FileCacheType.SYSTEMPFILES) {
                    bean = new FileCacheBean();
                    if (mFileTitles.contains(name)) {
                        bean.setIsHadSeondName(true);
                        bean.setSecondTitle(name);
                        int endIndex = name.indexOf(".tmp");
                        String temp = name.substring(0, endIndex);
                        temp += mRandom.nextInt(100);
                        name = temp + ".tmp";
                    }
                    mFileTitles.add(name);

                    mFileTitles.add(name);
                    bean.setTitle(name);
                    bean.setType(mFileCacheType);
                    bean.setCacheSize(file.length());
                    bean.setCachePath(file.getAbsolutePath());
                    mSystemTempBeans.add(bean);
                    mFileCacheGroup.setTotalCacheSize(bean.getCacheSize() + mFileCacheGroup.getTotalCacheSize());
                }

                if (bean != null) {
                    mScanCallback.onProgress(bean);
                }
            } else {
                if (level < SCAN_LEVEL) {
                    travelPath(file, level + 1);
                }
            }
        }

        mFileCacheGroup.setType(mFileCacheType);
        mFileCacheGroup.setTitle(mFileCacheType.getValue());
    }

    @Override
    protected Void doInBackground(Void... params) {
        mScanCallback.onBegin();
        File externalDir = Environment.getExternalStorageDirectory();
        File path = Environment.getDataDirectory();
        if (externalDir != null) {
            travelPath(externalDir, 0);
            travelPath(path, 0);
        }

        if (mFileCacheGroup.getTotalCacheSize() > 0L) {
            if (mFileCacheType ==
                    FileCacheBean.FileCacheType.OBSOLETEAPK) {
                Collections.sort(mObsoleteApkBeans);
                Collections.reverse(mObsoleteApkBeans);
                mFileCacheGroup.setChilds(mObsoleteApkBeans);
            } else if (mFileCacheType ==
                    FileCacheBean.FileCacheType.SYSTEMPFILES) {
                Collections.sort(mSystemTempBeans);
                Collections.reverse(mSystemTempBeans);
                mFileCacheGroup.setChilds(mSystemTempBeans);
            } else if (mFileCacheType ==
                    FileCacheBean.FileCacheType.RESIDUALLOGS) {
                Collections.sort(mResidualLogBeans);
                Collections.reverse(mResidualLogBeans);
                mFileCacheGroup.setChilds(mResidualLogBeans);
            }
        }

        mScanCallback.onFinish(mFileCacheGroup);
        return null;
    }
}
