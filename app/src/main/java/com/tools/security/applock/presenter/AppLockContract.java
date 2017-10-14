package com.tools.security.applock.presenter;

import android.content.Context;

import com.tools.security.bean.CommLockInfo;
import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;
import com.tools.security.settings.presenter.IgnoreContract;

import java.util.List;

/**
 * Created by lzx on 2017/1/6.
 */

public interface AppLockContract {
    interface View extends BaseView<IgnoreContract.Presenter> {

        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context, boolean isSort);

        void loadLockAppInfo(Context context);

        void searchAppInfo(String search, AppLockPresenter.ISearchResultListener listener);

        void onDestroy();
    }
}
