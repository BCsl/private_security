package com.tools.security.widget.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.applock.view.ForgotPwdActivity;
import com.tools.security.applock.view.unlock.GestureSelfUnlockActivity;
import com.tools.security.applock.view.unlock.NumberSelfUnlockActivity;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;

/**
 * Created by lzx on 2017/1/8.
 */

public class UnLockMenuPopWindow extends PopupWindow implements View.OnClickListener {

    private View mContentView;
    private Activity mContext;
    private TextView menuNotLock, menuForgotPwd, menuSetting, checkboxPattern;
    private String pkgName;
    private Intent intent;
    private Drawable drawableSelect;
    private Drawable drawableNormal;
    private Drawable drawInvisible;
    public static final String UPDATE_LOCK_VIEW = "update_lock_view";


    public UnLockMenuPopWindow(final Activity context, String pkgName, boolean isShowCheckboxPattern) {
        super(context);
        this.mContext = context;
        this.pkgName = pkgName;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.pop_unlock_menu, null);
        menuNotLock = (TextView) mContentView.findViewById(R.id.menu_not_lock);
        menuForgotPwd = (TextView) mContentView.findViewById(R.id.menu_forgot_pwd);
        menuSetting = (TextView) mContentView.findViewById(R.id.menu_setting);
        checkboxPattern = (TextView) mContentView.findViewById(R.id.checkbox_pattern);
        checkboxPattern.setVisibility(isShowCheckboxPattern ? View.VISIBLE : View.GONE);
        menuNotLock.setOnClickListener(this);
        menuForgotPwd.setOnClickListener(this);
        menuSetting.setOnClickListener(this);
        checkboxPattern.setOnClickListener(this);
        mContentView.setFocusableInTouchMode(true);
        setFocusable(true);
        this.setContentView(mContentView);
        this.setWidth(ScreenUtil.getPhoneWidth(context) * 55 / 100);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.pop_menu_anim);
        this.setBackgroundDrawable(new BitmapDrawable());

        drawableSelect = mContext.getResources().getDrawable(R.drawable.menu_pattern_selected);
        drawableNormal = mContext.getResources().getDrawable(R.drawable.menu_pattern_select);
        drawInvisible = mContext.getResources().getDrawable(R.drawable.menu_pattern_invisible);
        drawableSelect.setBounds(0, 0, drawableSelect.getMinimumWidth(), drawableSelect.getMinimumHeight());
        drawableNormal.setBounds(0, 0, drawableNormal.getMinimumWidth(), drawableNormal.getMinimumHeight());
        drawInvisible.setBounds(0, 0, drawableNormal.getMinimumWidth(), drawableNormal.getMinimumHeight());

    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        boolean ishideline = SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_HIDE_LINE, false);
        if (ishideline) {
            checkboxPattern.setCompoundDrawables(drawInvisible, null, drawableSelect, null);
        } else {
            checkboxPattern.setCompoundDrawables(drawInvisible, null, drawableNormal, null);
        }
    }

    @Override
    public void onClick(View v) {
        int lockType = SpUtil.getInstance().getInt(AppConstants.LOCK_TYPE);
        switch (v.getId()) {
            case R.id.menu_not_lock:
                if (lockType == 0) {
                    intent = new Intent(mContext, GestureSelfUnlockActivity.class);
                } else {
                    intent = new Intent(mContext, NumberSelfUnlockActivity.class);
                }
                intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, pkgName);
                intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_UNLOCK);
                mContext.startActivity(intent);
                mContext.overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
                break;
            case R.id.menu_forgot_pwd:
                mContext.startActivity(new Intent(mContext, ForgotPwdActivity.class));
                mContext.overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
                break;
            case R.id.menu_setting:
                if (lockType == 0) {
                    intent = new Intent(mContext, GestureSelfUnlockActivity.class);
                } else {
                    intent = new Intent(mContext, NumberSelfUnlockActivity.class);
                }
                intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, pkgName);
                intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_SETTING);
                mContext.startActivity(intent);
                mContext.overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
                break;
            case R.id.checkbox_pattern:
                boolean ishideline = SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_HIDE_LINE, false);
                if (ishideline) {
                    checkboxPattern.setCompoundDrawables(drawInvisible, null, drawableSelect, null);
                    SpUtil.getInstance().putBoolean(AppConstants.LOCK_IS_HIDE_LINE, false);
                } else {
                    checkboxPattern.setCompoundDrawables(drawInvisible, null, drawableNormal, null);
                    SpUtil.getInstance().putBoolean(AppConstants.LOCK_IS_HIDE_LINE, true);
                }
                mContext.sendBroadcast(new Intent(UPDATE_LOCK_VIEW));
                break;
        }
        dismiss();
    }
}
