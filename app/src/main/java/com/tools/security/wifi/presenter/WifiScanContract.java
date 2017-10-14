package com.tools.security.wifi.presenter;

import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;

import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/13.
 */

public interface WifiScanContract {
    interface View extends BaseView{
    }
    interface Presenter extends BasePresenter{
        void scan();
        void onDestory();
    }
}
