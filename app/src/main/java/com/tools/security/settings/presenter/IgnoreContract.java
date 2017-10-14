package com.tools.security.settings.presenter;

import android.content.Context;

import com.tools.security.bean.AppWhitePaper;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;

import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/14.
 */

public interface IgnoreContract {
    interface View extends BaseView<Presenter> {
        void refresh(List<AppWhitePaper> list);
    }

    interface Presenter extends BasePresenter {
        void loadData(Context context);
    }
}
