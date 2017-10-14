package com.tools.security.main.presenter;

import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/19.
 */

public interface MainContract {

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {
        void checkVirusLib();
    }
}
