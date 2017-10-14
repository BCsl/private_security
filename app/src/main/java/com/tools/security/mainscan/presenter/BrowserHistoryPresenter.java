package com.tools.security.mainscan.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.tools.security.bean.BrowserHistory;
import com.tools.security.utils.BrowserUtils;

import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/21.
 */

public class BrowserHistoryPresenter implements BrowserHistoryContract.Presenter {

    private LoadBrowserHistory loadBrowserHistory;
    private clearBrowserHistory mClearBrowserHistory;
    private Context context;

    private BrowserHistoryContract.View view;

    public BrowserHistoryPresenter(Context context, BrowserHistoryContract.View view) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void loadHistory() {
        loadBrowserHistory = new LoadBrowserHistory();
        loadBrowserHistory.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
    }

    @Override
    public void clearHistory() {
        mClearBrowserHistory = new clearBrowserHistory();
        mClearBrowserHistory.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
    }

    @Override
    public void onDestroy() {
        if (loadBrowserHistory != null && loadBrowserHistory.getStatus() != AsyncTask.Status.FINISHED) {
            loadBrowserHistory.cancel(true);
        }
        if (mClearBrowserHistory != null && mClearBrowserHistory.getStatus() != AsyncTask.Status.FINISHED) {
            mClearBrowserHistory.cancel(true);
        }
    }

    //加载浏览历史记录
    private class LoadBrowserHistory extends AsyncTask<Context, String, List<BrowserHistory>> {

        @Override
        protected List<BrowserHistory> doInBackground(Context... params) {
            List<BrowserHistory> browserHistories = BrowserUtils.getBrowserHistory(params[0]);
            return browserHistories;
        }

        @Override
        protected void onPostExecute(List<BrowserHistory> browserHistories) {
            super.onPostExecute(browserHistories);
            view.refreshList(browserHistories);
        }
    }

    private class clearBrowserHistory extends AsyncTask<Context, String, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            boolean isSuccess = BrowserUtils.clearBrowserHistory(params[0]);
            return isSuccess;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean)
                view.clearSuccess();
            else
                view.clearError();

        }
    }


}
