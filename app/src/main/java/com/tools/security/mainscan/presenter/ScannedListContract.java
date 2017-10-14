package com.tools.security.mainscan.presenter;

import com.tools.security.bean.AvlAppInfo;
import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;

import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/13.
 */

public interface ScannedListContract {
    interface View extends BaseView<Presenter> {
        void refreshList(List<AvlAppInfo> appInfos);
    }

    interface Presenter extends BasePresenter {
        void getAppInfo();
        void onDestory();
    }
}
