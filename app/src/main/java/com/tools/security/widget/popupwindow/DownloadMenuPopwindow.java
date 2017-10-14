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
import com.tools.security.download.DownLoadSecurityActivity;
import com.tools.security.settings.FeedbackActivity;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SystemUtil;

/**
 * description:下载保护Menu弹窗
 * author: xiaodifu
 * date: 2016/12/13.
 */

public class DownloadMenuPopwindow extends PopupWindow implements View.OnClickListener {

    private View mContentView;
    private TextView createSCText, feedbackText;
    private Context context;
    private Activity mActivity;

    public DownloadMenuPopwindow(final Context context, Activity mActivity) {
        super(context);
        this.context = context;
        this.mActivity = mActivity;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.pop_download_menu, null);
        createSCText = (TextView) mContentView.findViewById(R.id.text_create_sc);
        feedbackText = (TextView) mContentView.findViewById(R.id.text_feedback);

        createSCText.setOnClickListener(this);
        feedbackText.setOnClickListener(this);

        mContentView.setFocusableInTouchMode(true);
        setFocusable(true);


        // 设置SelectPicPopupWindow的View
        this.setContentView(mContentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ScreenUtil.getPhoneWidth(context) * 48 / 100);
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
            case R.id.text_create_sc:
                SystemUtil.addShortcut(mActivity, mActivity.getString(R.string.widget_name_download), R.mipmap.wiget_download, DownLoadSecurityActivity.class);
                break;
            case R.id.text_feedback:
                context.startActivity(new Intent(context, FeedbackActivity.class));
                break;
            default:
                break;
        }
        dismiss();
    }

}
