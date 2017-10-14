package com.tools.security.applock.presenter;

import android.widget.ImageView;

import com.tools.security.R;
import com.tools.security.bean.InputResult;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.StringUtil;

import java.util.List;

/**
 * Created by lzx on 2017/1/8.
 */

public class NumberCreatePresenter implements NumberCreateContract.Presenter {

    private static final int COUNT = 4; //4个点
    private boolean checkInput = false;
    private String tmpPassword;

    private NumberCreateContract.View mView;

    public NumberCreatePresenter(NumberCreateContract.View view) {
        mView = view;
    }

    @Override
    public void clickNumber(List<String> numInput, List<ImageView> pointList, String number) {
        if (numInput.size() < COUNT) {
            numInput.add(number);
        }
        int index = 0;
        for (ImageView iv : pointList) {
            if (index++ < numInput.size()) {
                mView.setNumberPointImageResource(iv, R.drawable.num_point_check);
            } else {
                mView.setNumberPointImageResource(iv, R.drawable.num_point);
            }
        }
        StringBuffer pBuffer = new StringBuffer();
        for (String s : numInput) {
            pBuffer.append(s);
        }
        doForResult(numInput, pointList, inputCheck(numInput, pBuffer.toString()));
    }

    private InputResult inputCheck(List<String> numInput, String password) {
        InputResult result;
        if (numInput.size() == COUNT) {
            if (checkInput) {
                if (password.equals(tmpPassword)) {
                    result = InputResult.SUCCESS;
                } else {
                    result = InputResult.ERROR;
                }
            } else {
                tmpPassword = password;
                result = InputResult.ONCE;
            }
        } else {
            result = InputResult.CONTINUE;
        }
        return result;
    }

    private void doForResult(List<String> numInput, List<ImageView> pointList, InputResult result) {
        switch (result) {
            case CONTINUE:
                break;
            case ONCE:
                checkInput = true;
                numInput.clear();
                mView.updateLockTipString(R.string.num_create_text_02, false);
                mView.completedFirstTime();
                break;
            case SUCCESS:
                String md5 = StringUtil.toMD5(tmpPassword);
                SpUtil.getInstance().putInt(AppConstants.LOCK_TYPE, 1); //设置模式为数字
                SpUtil.getInstance().putString(AppConstants.LOCK_PWD, md5); //保存密码
                // SpUtil.getInstance().putBoolean(AppConstants.LOCK_IS_FIRST_LOCK, false);
                mView.createLockSuccess();
                break;
            case ERROR:
                checkInput = true;
                numInput.clear();
                mView.updateLockTipString(R.string.num_create_text_03, true);
                break;
            default:
                break;
        }
    }

    public void clear() {

    }

    @Override
    public void onDestroy() {

    }
}
