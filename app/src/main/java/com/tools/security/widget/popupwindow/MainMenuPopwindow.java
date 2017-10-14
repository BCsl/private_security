package com.tools.security.widget.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.applock.view.AppLockFirstActivity;
import com.tools.security.common.AppConstants;
import com.tools.security.common.SecurityApplication;
import com.tools.security.scanfiles.view.ScanFilesActivity;
import com.tools.security.settings.FeedbackActivity;
import com.tools.security.settings.IgnoreActivity;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.KochavaUtils;
import com.tools.security.utils.ScreenUtil;

/**
 * description:首页Menu弹窗
 * author: xiaodifu
 * date: 2016/12/13.
 */

public class MainMenuPopwindow extends PopupWindow implements View.OnClickListener {

    private View mContentView;
    private TextView sdcardText, ignoreText, feedbackText, rateText, aboutText;
    private Context context;
    private Activity mActivity;

    public MainMenuPopwindow(final Context context, Activity mActivity) {
        super(context);
        this.context = context;
        this.mActivity = mActivity;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.pop_main_menu, null);
        sdcardText = (TextView) mContentView.findViewById(R.id.text_sdcard);
        ignoreText = (TextView) mContentView.findViewById(R.id.text_ignore);
        feedbackText = (TextView) mContentView.findViewById(R.id.text_feedback);
        rateText = (TextView) mContentView.findViewById(R.id.text_rate);
        aboutText = (TextView) mContentView.findViewById(R.id.text_about);

        sdcardText.setOnClickListener(this);
        ignoreText.setOnClickListener(this);
        feedbackText.setOnClickListener(this);
        rateText.setOnClickListener(this);
        aboutText.setOnClickListener(this);

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
            case R.id.text_sdcard:
                KochavaUtils.tracker(AppConstants.CLICK_MENU_SD_CARD);
                /*boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                if (sdCardExist) {
                    MainActivity.requestPermission(mActivity);
                } else {
                    ToastUtil.showShort("No Sdcard");
                }*/
                context.startActivity(new Intent(context, ScanFilesActivity.class));
                break;
            case R.id.text_ignore:
                KochavaUtils.tracker(AppConstants.CLICK_MENU_IGNORE);
                context.startActivity(new Intent(context, IgnoreActivity.class));
                break;
            case R.id.text_feedback:
//                KochavaUtils.tracker(AppConstants.CLICK_MENU_FEEDBACK);
                context.startActivity(new Intent(context, FeedbackActivity.class));
                break;
            case R.id.text_rate:
                KochavaUtils.tracker(AppConstants.CLICK_MENU_RATE);
                AppUtils.gotoGoogleMarket(context, AppConstants.GOOGLE_PLAY_URL, true);
                break;
            case R.id.text_about:
                KochavaUtils.tracker(AppConstants.CLICK_MENU_ABOUT);
                //context.startActivity(new Intent(context, AboutUsActivity.class));
                context.startActivity(new Intent(context, AppLockFirstActivity.class));
                break;
            default:
                break;
        }
        dismiss();
    }


    private void feedback() {
        String subject = "[" + context.getString(R.string.app_name) + " " + SecurityApplication.getInstance().getVersionName() + " android feedback]";
        StringBuilder body = new StringBuilder("Device Brand:" + SecurityApplication.getInstance().getOperatorName());
        body.append("\r\nOs Version:").append(SecurityApplication.getInstance().getOsVersion());
        body.append("\r\n\r\n\r\nSceen Density:").append(SecurityApplication.getInstance().getScreenSize());
        body.append("\r\nVersion:").append(SecurityApplication.getInstance().getVersionName());
        gotoEmail(subject, body.toString(), AppConstants.ULTRA_EMAIL);
    }

    private void gotoEmail(String subject, String body, String... receivers) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, receivers);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        if (!TextUtils.isEmpty(body)) {
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body.toString());
        }
        emailIntent.setType("plain/text");
        context.startActivity(emailIntent);
    }

//
//    public void show(View v){
//        getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
//        int i = getContentView().getMeasuredWidth();
//        showAsDropDown(v,j-i,14);
//    }
}
