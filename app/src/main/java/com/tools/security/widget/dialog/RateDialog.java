package com.tools.security.widget.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tools.security.R;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;

/**
 * description:引导评分dialog
 * author: xiaodifu
 * date: 2017/1/3.
 */

public class RateDialog extends Dialog implements View.OnClickListener {
    private LinearLayout contentView;
    private TextView cancelText, sureText, titleText;
    private View cheersBg;
    private ImageView cheersImg;
    private Context context;
    private String subStr;
    private RateType rateType;
    /**
     * (DisplayMetrics)设备密度
     */
    protected DisplayMetrics dm;

    public RateDialog(Context context, RateType rateType, String subStr) {
        super(context, R.style.DialogTransparent);
        this.context = context;
        this.rateType = rateType;
        this.subStr = subStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rate);
        setCancelable(false);

        initData();
        initView();
    }

    private void initData() {
        dm = context.getResources().getDisplayMetrics();
    }

    private void initView() {
        contentView = (LinearLayout) findViewById(R.id.linear_rate);
        cancelText = (TextView) findViewById(R.id.text_cancel);
        sureText = (TextView) findViewById(R.id.text_sure);
        cheersBg = findViewById(R.id.view_cheers_bg);
        cheersImg = (ImageView) findViewById(R.id.img_cheers);
        titleText = (TextView) findViewById(R.id.text_title);

        cheersBg.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(context,80)));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ScreenUtil.dip2px(context,212), ScreenUtil.dip2px(context,82));
        layoutParams.topMargin = ScreenUtil.dip2px(context,16);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        cheersImg.setLayoutParams(layoutParams);

        String titleStr = "";
        switch (rateType) {
            case VIRUS:
                titleStr = context.getString(R.string.dialog_rate_virus,subStr);
                break;
            case JUNK:
                titleStr = context.getString(R.string.dialog_rate_junk,subStr);
                break;
            case PRIVACY:
                titleStr = context.getString(R.string.dialog_rate_privacy);
                break;
            case PART:
                titleStr = context.getString(R.string.dialog_rate_part);
                break;
            case ALL:
                titleStr = context.getString(R.string.dialog_rate_all);
                break;
        }

        titleText.setText(titleStr);

        cancelText.setOnClickListener(this);
        sureText.setOnClickListener(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (dm.widthPixels * 0.83f);
        window.setAttributes(layoutParams);

        showEnterAnim();
    }

    @Override
    public void dismiss() {
        showDismissAnim();
    }

    private void superDismiss() {
        super.dismiss();
    }

    private void showEnterAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(//
                ObjectAnimator.ofFloat(contentView, "translationY", -250 * dm.density, 0), //
                ObjectAnimator.ofFloat(contentView, "alpha", 0.2f, 1));
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                superDismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    private void showDismissAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(//
                ObjectAnimator.ofFloat(contentView, "translationY", 0, 250 * dm.density), //
                ObjectAnimator.ofFloat(contentView, "alpha", 1, 0));
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                superDismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                superDismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onClick(View v) {
        SpUtil.getInstance().putBoolean(AppConstants.FINISHED_RATE_OR_FEEDBACK, true);
        switch (v.getId()) {
            case R.id.text_cancel:
                dismiss();
                new FeedbackDialog(context).show();
                break;
            case R.id.text_sure:
                dismiss();
                AppUtils.gotoGoogleMarket(context, AppConstants.GOOGLE_PLAY_URL, true);
                break;
        }
    }

    public enum RateType {
        VIRUS,
        JUNK,
        PRIVACY,
        PART,
        ALL
    }
}
