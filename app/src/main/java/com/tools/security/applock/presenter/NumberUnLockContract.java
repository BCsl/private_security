package com.tools.security.applock.presenter;

import android.widget.ImageView;

import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;
import com.tools.security.settings.presenter.IgnoreContract;

import java.util.List;

/**
 * Created by lzx on 2017/1/8.
 */

public interface NumberUnLockContract {
    interface View extends BaseView<IgnoreContract.Presenter> {
        void unLockSuccess();
        void unLockError(int retryNum);
        void clearPassword();
        void setNumberPointImageResource(List<String> numInput); //设置点的图片
    }

    interface Presenter extends BasePresenter {

        void clickNumber(List<String> numInput, List<ImageView> pointList, String number,String lockPwd);

        void onDestroy();
    }
}
