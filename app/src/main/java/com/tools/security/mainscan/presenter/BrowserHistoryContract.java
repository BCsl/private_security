package com.tools.security.mainscan.presenter;

import com.tools.security.bean.BrowserHistory;
import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;

import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/21.
 */

public interface BrowserHistoryContract {
    interface View extends BaseView{
        void refreshList(List<BrowserHistory> list);
        void clearSuccess();
        void clearError();
    }

    interface Presenter extends BasePresenter{
        void loadHistory();
        void clearHistory();
        void onDestroy();
    }
}
