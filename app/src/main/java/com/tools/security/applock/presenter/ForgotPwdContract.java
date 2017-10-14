package com.tools.security.applock.presenter;

import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;
import com.tools.security.settings.presenter.IgnoreContract;

/**
 * Created by lzx on 2017/1/15.
 */

public interface ForgotPwdContract {
    interface View extends BaseView<IgnoreContract.Presenter> {

        void sendEmailSuccess();
        void sendEmailError(String msg);
        void checkCodeSuccess();
        void checkCodeError(String msg);
    }

    interface Presenter extends BasePresenter {
        void sendEmail(String emailAddress);

        void checkCode(String emailAddress,String code);
    }
}
