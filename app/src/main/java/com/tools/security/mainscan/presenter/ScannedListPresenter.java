package com.tools.security.mainscan.presenter;

import android.os.AsyncTask;

import com.tools.security.bean.AvlAppInfo;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/13.
 */

public class ScannedListPresenter implements ScannedListContract.Presenter {
    private ScannedListContract.View view;
    private LoadAppInfoAsynctask loadAppInfoAsynctask;
    private List<AvlAppInfo> scannedList;

    public ScannedListPresenter(ScannedListContract.View view) {
        this.view = view;
    }


    @Override
    public void getAppInfo() {
        loadAppInfoAsynctask = new LoadAppInfoAsynctask();
        loadAppInfoAsynctask.executeOnExecutor(Executors.newCachedThreadPool());
    }

    @Override
    public void onDestory() {
        if (loadAppInfoAsynctask != null && loadAppInfoAsynctask.getStatus() != AsyncTask.Status.FINISHED) {
            loadAppInfoAsynctask.cancel(true);
        }
        scannedList = null;
    }

    class LoadAppInfoAsynctask extends AsyncTask<String, String, List<AvlAppInfo>> {

        @Override
        protected List<AvlAppInfo> doInBackground(String... params) {
            scannedList=DataSupport.where("ignored = ?","0").find(AvlAppInfo.class);
            return scannedList;
        }

        @Override
        protected void onPostExecute(List<AvlAppInfo> appInfos) {
            view.refreshList(appInfos);
        }
    }
}
