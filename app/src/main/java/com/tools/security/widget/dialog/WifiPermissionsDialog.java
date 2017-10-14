package com.tools.security.widget.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.utils.AppUtils;
import com.tools.security.widget.dialog.base.BaseDialog;

/**
 * Created by lzx on 2017/1/14.
 * 释放带宽dialog
 */

public class WifiPermissionsDialog extends BaseDialog {

    private TextView mDialogTitle;
    private TextView mBtnGot;
    private ImageView mIcSettingList, mAppIcon, mAppIcon2, mIcSwitch2, mIcPoint, mIcHand;
    private TextView mAppTitle, mAppTitle2;
    private View mBgFrame;
    private ScrollView scrollView;
    private Context mContext;

    private ValueAnimator moveAnim, downhandAnim, uphandAnim;
    private ObjectAnimator frameAnim, dismissAlphaAnim;

    private int titleResId = 0;

    public WifiPermissionsDialog(Context context) {
        super(context);
        mContext = context;
    }

    public WifiPermissionsDialog(Context context, int titleResId) {
        super(context);
        mContext = context;
        this.titleResId = titleResId;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_permissions_wifi;
    }

    @Override
    protected float setWidthScale() {
        return 0.9f;
    }

    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }

    @Override
    protected void init() {
        setCanceledOnTouchOutside(false);
        getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        mDialogTitle = (TextView) findViewById(R.id.dialog_title);
        mBtnGot = (TextView) findViewById(R.id.btn_got);
        mIcSettingList = (ImageView) findViewById(R.id.ic_setting_list);
        mAppIcon = (ImageView) findViewById(R.id.app_icon);
        mAppIcon2 = (ImageView) findViewById(R.id.app_icon_2);
        mIcSwitch2 = (ImageView) findViewById(R.id.ic_switch_2);
        mIcPoint = (ImageView) findViewById(R.id.ic_point);
        mIcHand = (ImageView) findViewById(R.id.ic_hand);
        mAppTitle = (TextView) findViewById(R.id.app_title);
        mAppTitle2 = (TextView) findViewById(R.id.app_title_2);
        mBgFrame = findViewById(R.id.bg_frame);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        if (titleResId != 0) {
            mDialogTitle.setText(titleResId);
        }

        //移动
        moveAnim = ValueAnimator.ofFloat(0, AppUtils.dip2px(mContext, 154));
        moveAnim.setDuration(1500);
        //显示框
        frameAnim = ObjectAnimator.ofFloat(mBgFrame, "alpha", 0f, 1f);
        frameAnim.setDuration(500);
        //移动手指
        downhandAnim = ValueAnimator.ofFloat(0, AppUtils.dip2px(mContext, 70));
        downhandAnim.setDuration(500);
        //透明度
        dismissAlphaAnim = ObjectAnimator.ofFloat(mBgFrame, "alpha", 1f, 0f);
        dismissAlphaAnim.setDuration(500);
        dismissAlphaAnim.setStartDelay(200);
        //移动手
        uphandAnim = ValueAnimator.ofFloat(0, AppUtils.dip2px(mContext, 70));
        uphandAnim.setDuration(700);

        moveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                mIcSettingList.setTranslationY(-value);
                mAppIcon.setTranslationY(-value);
                mAppTitle.setTranslationY(-value);
                mIcPoint.setTranslationY(-value);
                mIcHand.setTranslationY(-value);
            }
        });
        moveAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mBgFrame.setVisibility(View.VISIBLE);
                frameAnim.start();
            }
        });
        frameAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIcPoint.setVisibility(View.INVISIBLE);
                downhandAnim.start();
            }
        });
        downhandAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                mIcPoint.setTranslationY(-AppUtils.dip2px(mContext, 154) + value);
                mIcHand.setTranslationY(-AppUtils.dip2px(mContext, 154) + value);
            }
        });
        downhandAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dismissAlphaAnim.start();
            }
        });
        dismissAlphaAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIcSettingList.setVisibility(View.INVISIBLE);
                mAppTitle.setVisibility(View.INVISIBLE);
                mAppIcon.setVisibility(View.INVISIBLE);
                mAppIcon2.setVisibility(View.VISIBLE);
                mAppTitle2.setVisibility(View.VISIBLE);
                mIcSwitch2.setVisibility(View.VISIBLE);
                uphandAnim.start();
            }
        });
        uphandAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                mIcHand.setTranslationY(-AppUtils.dip2px(mContext, 84) - value);
                mIcHand.setTranslationX(value / 2);
            }
        });
        uphandAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIcSwitch2.setImageResource(R.drawable.ic_open_grey);
            }
        });
        moveAnim.start();

        mBtnGot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                superDismiss();
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void dismiss() {
        if (moveAnim != null && moveAnim.isRunning()) moveAnim.cancel();
        if (downhandAnim != null && downhandAnim.isRunning()) downhandAnim.cancel();
        if (uphandAnim != null && uphandAnim.isRunning()) uphandAnim.cancel();
        if (frameAnim != null && frameAnim.isRunning()) frameAnim.cancel();
        if (dismissAlphaAnim != null && dismissAlphaAnim.isRunning()) dismissAlphaAnim.cancel();
        if (uphandAnim != null && uphandAnim.isRunning()) uphandAnim.cancel();
        moveAnim = null;
        downhandAnim = null;
        uphandAnim = null;
        frameAnim = null;
        dismissAlphaAnim = null;
        uphandAnim = null;
        super.dismiss();
    }
}
