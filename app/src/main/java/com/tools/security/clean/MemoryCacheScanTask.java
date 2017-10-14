package com.tools.security.clean;

import android.os.AsyncTask;

import com.tools.security.bean.FileCacheBean;
import com.tools.security.bean.FileCacheGroup;
import com.tools.security.bean.RunningAppInfoBean;
import com.tools.security.common.SecurityApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Author：wushuangshuang on 16/11/6 14:53
 * Function：
 */
public class MemoryCacheScanTask extends AsyncTask<Void, Void, Void> {
    private IScanCallback mScanCallback;
    private List<RunningAppInfoBean> mRunningAppInfoBeans;
    private FileCacheGroup mFileGroupBeans;
    private float mTotoalSize;
    private List<FileCacheBean> mMemroyApps;
    private Random mRandom;

    public MemoryCacheScanTask(IScanCallback callback) {
        mScanCallback = callback;
        mRandom = new Random();
    }

    @Override
    protected Void doInBackground(Void... params) {
        mScanCallback.onBegin();
        mRunningAppInfoBeans = RecentProcessUtil.getRunningApps3(SecurityApplication.getInstance());
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mRunningAppInfoBeans != null && mRunningAppInfoBeans.size() > 0) {
            mMemroyApps = new ArrayList<FileCacheBean>();
            mFileGroupBeans = new FileCacheGroup();
            mFileGroupBeans.setType(FileCacheBean.FileCacheType.MEMORYJUNK);

            for (RunningAppInfoBean bean : mRunningAppInfoBeans) {
                if (bean != null) {
                    FileCacheBean cacheBean = new FileCacheBean();
                    cacheBean.setType(FileCacheBean.FileCacheType.MEMORYJUNK);
                    if (bean.getIcon() == null) {
                        continue;
                    }
                    String name = bean.getName();
                    cacheBean.setIsHadSeondName(true);
                    cacheBean.setSecondTitle(name);
                    name += mRandom.nextInt(100);
                    cacheBean.setTitle(name);
                    cacheBean.setCacheSize(bean.getmSize() * 1024);
                    cacheBean.setPackageName(bean.getPkgName());
                    mMemroyApps.add(cacheBean);
                    mTotoalSize += cacheBean.getCacheSize();
                }
            }
            float totalSize = 0;
            for(FileCacheBean fileCacheBean : mMemroyApps) {
                totalSize += fileCacheBean.getCacheSize();
            }

            mFileGroupBeans.setTotalCacheSize(mTotoalSize);
            mFileGroupBeans.setChilds(mMemroyApps);
        }

        mScanCallback.onFinish(mFileGroupBeans);
    }
}