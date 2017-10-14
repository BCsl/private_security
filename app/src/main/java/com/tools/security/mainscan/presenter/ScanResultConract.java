package com.tools.security.mainscan.presenter;

import com.tools.security.bean.AvlAppInfo;
import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;

import java.util.ArrayList;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/22.
 */

public interface ScanResultConract {

    interface View extends BaseView {
        void updateClean();
    }

    interface Presenter extends BasePresenter {
        void resolveAll(ArrayList<AvlAppInfo> dangerList, boolean cleanHistory, boolean cleanJunk);
        void onDestory();
    }

}
