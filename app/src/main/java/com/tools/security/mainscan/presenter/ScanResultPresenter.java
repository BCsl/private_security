package com.tools.security.mainscan.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.tools.security.bean.config.AppConfig;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.FileCacheGroup;
import com.tools.security.bean.SafeLevel;
import com.tools.security.clean.FileUtils;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.BrowserUtils;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.SystemUtil;

import java.util.ArrayList;

import static com.tools.security.common.AppConstants.ACTION_FILTER_CLEAR_BROSWER_HISTORY;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/22.
 */

public class ScanResultPresenter implements ScanResultConract.Presenter {
    private ClearHistoryAsyncTask clearHistoryAsyncTask;
    private ClearJunkFileAsyncTask clearJunkFileAsyncTask;
    private ScanResultConract.View view;
    private Activity context;

    public ScanResultPresenter(ScanResultConract.View view, Activity context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void resolveAll(ArrayList<AvlAppInfo> dangerList, boolean cleanHistory, boolean cleanJunk) {
        SpUtil.getInstance().putBoolean(AppConstants.IS_RESOLVE_ALL, true);
        if (dangerList != null) {
            for (int i = dangerList.size() - 1; i >= 0; i--) {
                AvlAppInfo appInfo = dangerList.get(i);
                SystemUtil.uninstall(context, appInfo.getPackageName(), false);
            }
        }
        if (cleanHistory) {
            int currentCleanedCount = SpUtil.getInstance().getInt(AppConstants.CLEANED_PRIVACY_COUNT);
            SpUtil.getInstance().putInt(AppConstants.CLEANED_PRIVACY_COUNT, currentCleanedCount + 1);
            clearHistoryAsyncTask = new ClearHistoryAsyncTask();
            clearHistoryAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
        }
        if (cleanJunk) {
            int currentCleanedCount = SpUtil.getInstance().getInt(AppConstants.CLEANED_JUNK_COUNT);
            SpUtil.getInstance().putInt(AppConstants.CLEANED_JUNK_COUNT, currentCleanedCount + 1);
            clearJunkFileAsyncTask = new ClearJunkFileAsyncTask();
            clearJunkFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onDestory() {
        if (clearHistoryAsyncTask != null) {
            clearHistoryAsyncTask.cancel(true);
            clearHistoryAsyncTask = null;
        }

        if (clearJunkFileAsyncTask != null) {
            clearJunkFileAsyncTask.cancel(true);
            clearJunkFileAsyncTask = null;
        }
        view = null;
        context = null;
    }

    //清除浏览器历史记录AsyncTask
    class ClearHistoryAsyncTask extends AsyncTask<Context, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Context... params) {
            BrowserUtils.clearBrowserHistory(params[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            SpUtil.getInstance().putInt(AppConstants.BROSWER_HISTORY_COUNT, 0);
            AppConfig appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
            appConfig.setProblemCount(appConfig.getProblemCount() - 1);
            if (appConfig.getProblemCount() == 0) {
                appConfig.setSafeLevel(SafeLevel.SAFE);
            }
            SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
            context.sendBroadcast(new Intent(ACTION_FILTER_CLEAR_BROSWER_HISTORY));
        }
    }

    //清理垃圾文件
    class ClearJunkFileAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            FileCacheGroup logFileCacheGroup = SpUtil.getInstance().getBean(AppConstants.FILE_CACHE_GROUP_LOG, FileCacheGroup.class);
            FileCacheGroup apkFileCacheGroup = SpUtil.getInstance().getBean(AppConstants.FILE_CACHE_GROUP_APK, FileCacheGroup.class);
            FileCacheGroup systempFileCacheGroup = SpUtil.getInstance().getBean(AppConstants.FILE_CACHE_GROUP_SYS_TEMP, FileCacheGroup.class);
            double cleanedSize = 0d;

            if (logFileCacheGroup != null) {
                FileUtils.freeJunkInfos(logFileCacheGroup.getChilds());
                cleanedSize += logFileCacheGroup.getTotalCacheSize();
            }
            if (apkFileCacheGroup != null) {
                FileUtils.freeJunkInfos(apkFileCacheGroup.getChilds());
                cleanedSize += apkFileCacheGroup.getTotalCacheSize();
            }
            if (systempFileCacheGroup != null) {
                FileUtils.freeJunkInfos(systempFileCacheGroup.getChilds());
                cleanedSize += systempFileCacheGroup.getTotalCacheSize();
            }

            Double d=Double.parseDouble(SpUtil.getInstance().getString(AppConstants.CLEANED_JUNK_FILE_SIZE,"0"));
            SpUtil.getInstance().putString(AppConstants.CLEANED_JUNK_FILE_SIZE,(d+cleanedSize)+"");
            SpUtil.getInstance().putString(AppConstants.AD_JUNK_SIZE, "0f");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_LOG, null);
            SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_APK, null);
            SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_SYS_TEMP, null);
            // TODO: 2016/12/22 安全配置更新
            AppConfig appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
            appConfig.setProblemCount(appConfig.getProblemCount() - 1);
            if (appConfig.getProblemCount() == 0) {
                appConfig.setSafeLevel(SafeLevel.SAFE);
            }
            SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
            context.sendBroadcast(new Intent(AppConstants.ACTION_FILTER_CLEAN_JUNK));
        }
    }


}
