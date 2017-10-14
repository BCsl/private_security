package com.tools.security.wifi.presenter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.tools.security.bean.AvlAppInfo;
import com.tools.security.mainscan.presenter.ScannedListContract;
import com.tools.security.mainscan.presenter.ScannedListPresenter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * description:加载本地应用列表
 * author: xiaodifu
 * date: 2017/1/16.
 */

public class IgnorePresenter implements IgnoreContract.Presenter {
    private IgnoreContract.View view;
    private IgnorePresenter.LoadAppInfoAsynctask loadAppInfoAsynctask;
    private Context context;


    public IgnorePresenter(Context context, IgnoreContract.View view) {
        this.context = context;
        this.view = view;
    }


    @Override
    public void getAppInfo() {
        loadAppInfoAsynctask = new IgnorePresenter.LoadAppInfoAsynctask();
        loadAppInfoAsynctask.executeOnExecutor(Executors.newCachedThreadPool());
    }

    @Override
    public void onDestory() {
        if (loadAppInfoAsynctask != null && loadAppInfoAsynctask.getStatus() != AsyncTask.Status.FINISHED) {
            loadAppInfoAsynctask.cancel(true);
        }
    }

    class LoadAppInfoAsynctask extends AsyncTask<Void, Void, List<PackageInfo>> {

        @Override
        protected List<PackageInfo> doInBackground(Void... params) {
            List<PackageInfo> list = getLocalAppsPkgInfo(context);
            return list;
        }

        @Override
        protected void onPostExecute(List<PackageInfo> appInfos) {
            view.refreshList(appInfos);
        }
    }

    public static List<PackageInfo> getLocalAppsPkgInfo(Context context) {
        final int MAX_ATTEMPTS = 3;
        for (int i = 0; i < MAX_ATTEMPTS; i++) {

            try {
                List<PackageInfo> pkgInfoList = context.getPackageManager().getInstalledPackages(
                        PackageManager.GET_PERMISSIONS | PackageManager.GET_PROVIDERS);

                Log.d("TAG", "=> Total installed packages: " + pkgInfoList.size());
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

}
