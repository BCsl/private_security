package com.tools.security.wifi.presenter;

import android.content.pm.PackageInfo;

import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;
import com.tools.security.mainscan.presenter.ScannedListContract;

import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/16.
 */

public interface IgnoreContract {
    interface View extends BaseView<IgnoreContract.Presenter> {
        void refreshList(List<PackageInfo> appInfos);
    }

    interface Presenter extends BasePresenter {
        void getAppInfo();
        void onDestory();
    }
}
