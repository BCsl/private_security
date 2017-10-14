package com.tools.security.applock.presenter;

import android.widget.ImageView;

import com.tools.security.common.BasePresenter;
import com.tools.security.common.BaseView;
import com.tools.security.settings.presenter.IgnoreContract;

import java.util.List;

/**
 * Created by lzx on 2017/1/8.
 */

public interface NumberCreateContract {
    interface View extends BaseView<IgnoreContract.Presenter> {
        void setNumberPointImageResource(ImageView iv, int resId); //设置点的图片

        void updateLockTipString(int resId, boolean isToast);//更新提示信息

        void createLockSuccess(); //创建密码成功

        void completedFirstTime(); //输入第一次后
    }

    interface Presenter extends BasePresenter {

        void clickNumber(List<String> numInput, List<ImageView> pointList, String number);

        void onDestroy();
    }
}
