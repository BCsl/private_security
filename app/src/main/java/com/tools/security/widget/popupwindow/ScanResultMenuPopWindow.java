package com.tools.security.widget.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.settings.IgnoreActivity;
import com.tools.security.utils.ScreenUtil;

/**
 * Created by lzx on 2016/12/28.
 * email：386707112@qq.com
 * 功能：扫描结果页的 PopupWindow
 */

public class ScanResultMenuPopWindow extends PopupWindow implements View.OnClickListener {
    private View mContentView;
    private TextView ignoreText;
    private Activity context;


    public ScanResultMenuPopWindow(final Activity context) {
        super(context);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.pop_scan_result, null);
        ignoreText = (TextView) mContentView.findViewById(R.id.text_ignore);
        ignoreText.setOnClickListener(this);
        mContentView.setFocusableInTouchMode(true);
        setFocusable(true);

        // 设置SelectPicPopupWindow的View
        this.setContentView(mContentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ScreenUtil.getPhoneWidth(context) * 35 / 100);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.pop_menu_anim);
        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(new BitmapDrawable());
//        this.setOutsideTouchable(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_ignore:
                context.startActivity(new Intent(context, IgnoreActivity.class).putExtra("from",1));
                context.overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
                break;
        }
    }
}
