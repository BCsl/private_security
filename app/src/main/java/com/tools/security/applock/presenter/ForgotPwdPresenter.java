package com.tools.security.applock.presenter;

import android.text.TextUtils;

import com.tools.security.bean.ResponseResult;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.SecurityHttp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lzx on 2017/1/15.
 */

public class ForgotPwdPresenter implements ForgotPwdContract.Presenter {

    private ForgotPwdContract.View mView;

    public ForgotPwdPresenter(ForgotPwdContract.View view) {
        mView = view;
    }

    @Override
    public void sendEmail(String emailAddress) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", emailAddress);
        map.put("code", 0);
        SecurityHttp.doPost(AppConstants.LOCK_FUID, map, new SecurityHttp.ResultListener() {
            @Override
            public void onResultWithHeader(ResponseResult responseResult) {
                int status = responseResult.getStatus();
                if (status == 1) {
                    mView.sendEmailSuccess();
                } else {
                    String msg = responseResult.getMsg();
                    mView.sendEmailError(msg);
                }
            }

            @Override
            public void onFailed(int code, String info) {
                mView.sendEmailError(info);
            }
        });
    }

    @Override
    public void checkCode(String emailAddress, String code) {
        if (!TextUtils.isEmpty(code)) {
            Map<String, Object> map = new HashMap<>();
            map.put("email", emailAddress);
            map.put("code", code);
            SecurityHttp.doPost(AppConstants.LOCK_FUID, map, new SecurityHttp.ResultListener() {
                @Override
                public void onResultWithHeader(ResponseResult responseResult) {
                    if (responseResult.getStatus() == 1) {
                        mView.checkCodeSuccess();
                    } else {
                        String msg = responseResult.getMsg();
                        mView.checkCodeError(msg);
                    }
                }

                @Override
                public void onFailed(int code, String info) {
                    mView.checkCodeError(info);
                }
            });
        }
    }
}
