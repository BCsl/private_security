package com.tools.security.scanfiles.presenter;

import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;

/**
 * Created by lzx on 2016/12/19.
 * email：386707112@qq.com
 * 功能：
 */

public interface ScanFilesContract {
    interface View extends BaseView<ScanFilesContract.Presenter> {
        void onUpdateProgress(String perResult,  float arcPer);

        void onUpdateText(String showResult,String count);

        void onUpdateVirusCount(int count);

        void onScanFinish();

        void refreshResult(boolean haveVirus);

    }

     interface Presenter extends BasePresenter {
        void startScanSdcard();

        void onDestroy();
    }
}
