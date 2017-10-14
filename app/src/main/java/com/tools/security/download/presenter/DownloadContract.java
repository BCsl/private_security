package com.tools.security.download.presenter;

import com.tools.security.bean.DownloadFile;
import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;

import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/10.
 */

public interface DownloadContract {

    interface View extends BaseView {
        void refreshData(List<DownloadFile> downloadFiles);
    }

    interface Presenter extends BasePresenter {
        void loadData();
        void onDetory();
    }

}
