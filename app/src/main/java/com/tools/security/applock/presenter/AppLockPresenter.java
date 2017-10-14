package com.tools.security.applock.presenter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.tools.security.R;
import com.tools.security.applock.db.CommLockInfoManager;
import com.tools.security.bean.CommLockInfo;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.SpUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lzx on 2017/1/6.
 */

public class AppLockPresenter implements AppLockContract.Presenter {

    private AppLockContract.View mView;
    private PackageManager mPackageManager;
    private CommLockInfoManager mLockInfoManager;
    private Context mContext;
    private LoadAppInfo mLoadAppInfo;
    private SearchInfoAsyncTask mSearchInfoAsyncTask;
    private LoadLockAsyncTask mLoadLockAsyncTask;

    public AppLockPresenter(AppLockContract.View view, Context mContext) {
        mView = view;
        this.mContext = mContext;
        mPackageManager = mContext.getPackageManager();
        mLockInfoManager = new CommLockInfoManager(mContext);


    }

    /**
     * 加载所有app
     *
     * @param context
     * @param isSort
     */
    @Override
    public void loadAppInfo(Context context, boolean isSort) {
        mLoadAppInfo = new LoadAppInfo(isSort);
        mLoadAppInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 加载上锁的app
     *
     * @param context
     */
    @Override
    public void loadLockAppInfo(Context context) {
        mLoadLockAsyncTask = new LoadLockAsyncTask();
        mLoadLockAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 加载搜索的app
     *
     * @param search
     * @param listener
     */
    @Override
    public void searchAppInfo(String search, ISearchResultListener listener) {
        new SearchInfoAsyncTask(listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, search);
    }

    @Override
    public void onDestroy() {
        if (mLoadAppInfo != null && mLoadAppInfo.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadAppInfo.cancel(true);
        }
        if (mSearchInfoAsyncTask != null && mSearchInfoAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            mSearchInfoAsyncTask.cancel(true);
        }
        if (mLoadLockAsyncTask != null && mLoadLockAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            mLoadLockAsyncTask.cancel(true);
        }
    }

    private class LoadAppInfo extends AsyncTask<Void, String, List<CommLockInfo>> {

        private boolean isSort = false;

        public LoadAppInfo(boolean isSort) {
            this.isSort = isSort;
        }

        @Override
        protected List<CommLockInfo> doInBackground(Void... params) {
            List<CommLockInfo> commLockInfos = mLockInfoManager.getAllCommLockInfos();
            Iterator<CommLockInfo> infoIterator = commLockInfos.iterator();
            int favoriteNum = 0;
            int sysAppNum = 0;
            int userAppNum = 0;

            while (infoIterator.hasNext()) {
                CommLockInfo info = infoIterator.next();
                try {
                    ApplicationInfo appInfo = mPackageManager.getApplicationInfo(info.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                    if (appInfo == null || mPackageManager.getApplicationIcon(appInfo) == null) {
                        infoIterator.remove(); //将有错的app移除
                        continue;
                    } else {
                        info.setAppInfo(appInfo); //给列表ApplicationInfo赋值
                        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) { //判断是否是系统应用 ApplicationInfo#isSystemApp()
                            info.setSysApp(true);
                            info.setTopTitle(mContext.getString(R.string.lock_system));
                        } else {
                            info.setSysApp(false);
                            info.setTopTitle(mContext.getString(R.string.lock_apps));
                        }
                        //  mLockInfoManager.updateAppName(info.getAppName(),info.getPackageName());
                    }
                    //获取推荐应用总数
                    if (info.isLocked()) {
                        favoriteNum++;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    infoIterator.remove();
                }
            }
            SpUtil.getInstance().putInt(AppConstants.LOCK_FAVITER_NUM, favoriteNum);

            if (isSort) {
                List<CommLockInfo> sysList = new ArrayList<>();
                List<CommLockInfo> userList = new ArrayList<>();
                for (CommLockInfo info : commLockInfos) {
                    if (info.isSysApp()) {
                        sysList.add(info);
                        sysAppNum++;
                    } else {
                        userList.add(info);
                        userAppNum++;
                    }
                }
                SpUtil.getInstance().putInt(AppConstants.LOCK_SYS_APP_NUM, sysAppNum);
                SpUtil.getInstance().putInt(AppConstants.LOCK_USER_APP_NUM, userAppNum);
                commLockInfos.clear();
                commLockInfos.addAll(sysList);
                commLockInfos.addAll(userList);
            }
            return commLockInfos;
        }

        @Override
        protected void onPostExecute(List<CommLockInfo> commLockInfos) {
            super.onPostExecute(commLockInfos);
            mView.loadAppInfoSuccess(commLockInfos);
        }
    }

    private class SearchInfoAsyncTask extends AsyncTask<String, Void, List<CommLockInfo>> {
        private ISearchResultListener mSearchResultListener;

        public SearchInfoAsyncTask(ISearchResultListener searchResultListener) {
            mSearchResultListener = searchResultListener;
        }

        @Override
        protected List<CommLockInfo> doInBackground(String... params) {
            List<CommLockInfo> commLockInfos = mLockInfoManager.queryBlurryList(params[0]);
            Iterator<CommLockInfo> infoIterator = commLockInfos.iterator();
            while (infoIterator.hasNext()) {
                CommLockInfo info = infoIterator.next();
                try {
                    ApplicationInfo appInfo = mPackageManager.getApplicationInfo(info.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                    if (appInfo == null || mPackageManager.getApplicationIcon(appInfo) == null) {
                        infoIterator.remove(); //将有错的app移除
                        continue;
                    } else {
                        info.setAppInfo(appInfo); //给列表ApplicationInfo赋值
                        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) { //判断是否是系统应用 ApplicationInfo#isSystemApp()
                            info.setSysApp(true);
                            info.setTopTitle(mContext.getString(R.string.lock_system));
                        } else {
                            info.setSysApp(false);
                            info.setTopTitle(mContext.getString(R.string.lock_apps));
                        }
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    infoIterator.remove();
                }
            }
            List<CommLockInfo> sysList = new ArrayList<>();
            List<CommLockInfo> userList = new ArrayList<>();
            for (CommLockInfo info : commLockInfos) {
                if (info.isSysApp()) {
                    sysList.add(info);
                } else {
                    userList.add(info);
                }
            }
            commLockInfos.clear();
            commLockInfos.addAll(sysList);
            commLockInfos.addAll(userList);
            return commLockInfos;
        }

        @Override
        protected void onPostExecute(List<CommLockInfo> commLockInfos) {
            super.onPostExecute(commLockInfos);
            mSearchResultListener.onSearchResult(commLockInfos);
        }
    }

    public interface ISearchResultListener {
        void onSearchResult(List<CommLockInfo> commLockInfos);
    }

    private class LoadLockAsyncTask extends AsyncTask<String, Void, List<CommLockInfo>> {

        @Override
        protected List<CommLockInfo> doInBackground(String... params) {
            List<CommLockInfo> commLockInfos = mLockInfoManager.getAllCommLockInfos();
            Iterator<CommLockInfo> infoIterator = commLockInfos.iterator();

            while (infoIterator.hasNext()) {
                CommLockInfo info = infoIterator.next();
                try {
                    ApplicationInfo appInfo = mPackageManager.getApplicationInfo(info.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                    if (appInfo == null || mPackageManager.getApplicationIcon(appInfo) == null) {
                        infoIterator.remove(); //将有错的app移除
                        continue;
                    } else {
                        info.setAppInfo(appInfo); //给列表ApplicationInfo赋值
                        if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) { //判断是否是系统应用 ApplicationInfo#isSystemApp()
                            info.setSysApp(true);
                            info.setTopTitle(mContext.getString(R.string.lock_system));
                        } else {
                            info.setSysApp(false);
                            info.setTopTitle(mContext.getString(R.string.lock_apps));
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    infoIterator.remove();
                }
            }

            List<CommLockInfo> list = new ArrayList<>();
            for (CommLockInfo info : commLockInfos) {
                if (info.isLocked()) {
                    list.add(info);
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<CommLockInfo> commLockInfos) {
            super.onPostExecute(commLockInfos);
            mView.loadAppInfoSuccess(commLockInfos);
        }

    }

}
