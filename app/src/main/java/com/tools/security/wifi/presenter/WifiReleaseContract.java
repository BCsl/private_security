package com.tools.security.wifi.presenter;

import com.tools.security.bean.WifiReleaseApp;
import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/14.
 */

public interface WifiReleaseContract {
    interface View extends BaseView{
        void refresh(ArrayList<WifiReleaseApp> appList);
    }

    interface Presenter extends BasePresenter{
        void loadAppFlow();
        void onDestory();
    }
}
