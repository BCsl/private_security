package com.tools.security.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tools.security.R;
import com.tools.security.common.SecurityApplication;

/**
 * description: Toast工具类
 * author: xiaodifu
 * date: 2016/7/24.
 */
public class ToastUtil {

    private static Toast mToast;

    public static void showLong(String string) {
        if (string == null || string.trim().equals("")) return;
        View layout = LayoutInflater.from(SecurityApplication.getInstance()).inflate(R.layout.layout_toast, null);
        layout.getBackground().setAlpha(200);

        /*设置布局*/
        TextView textView = (TextView) layout.findViewById(R.id.text_content);
        textView.setText(string);

        if (mToast == null) {
            mToast = new Toast(SecurityApplication.getInstance());
        }
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.BOTTOM, 0, ScreenUtil.dip2px(SecurityApplication.getInstance(),60f));
        mToast.setView(layout);
        mToast.show();
    }

    public static void showShort(String string) {
        if (string == null || string.trim().equals("")) return;
        View layout = LayoutInflater.from(SecurityApplication.getInstance()).inflate(R.layout.layout_toast, null);
        layout.getBackground().setAlpha(200);

        /*设置布局*/
        TextView textView = (TextView) layout.findViewById(R.id.text_content);
        textView.setText(string);

        if (mToast == null) {
            mToast = new Toast(SecurityApplication.getInstance());
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.BOTTOM, 0, ScreenUtil.dip2px(SecurityApplication.getInstance(),60f));
        mToast.setView(layout);
        mToast.show();
    }

}
