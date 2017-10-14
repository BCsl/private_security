package com.tools.security.mainscan.presenter;

import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/13.
 */

public interface MainScanContract {
    interface View extends BaseView<Presenter>{
        void onScanningProgress(int progress,String countStr,int status);
        void onScanningEnd(String result, int status, boolean haveProblem);
        void onScanningFinished();
    }

    interface Presenter extends BasePresenter{
        void init();
        void start();
        void localScan();
        void loadBrowser();
        void localFileScan();
        void onDestory();
        void loadNormalAd();
    }


}
