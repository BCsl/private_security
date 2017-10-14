package com.tools.security.widget.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.common.AppConstants;
import com.tools.security.settings.FeedbackActivity;
import com.tools.security.utils.SpUtil;

/**
 * description:引导反馈dialog
 * author: xiaodifu
 * date: 2017/1/3.
 */

public class FeedbackDialog extends Dialog implements View.OnClickListener {
    private RelativeLayout contentView;
    private TextView cancelText, sureText;
    private Context context;
    /**
     * (DisplayMetrics)设备密度
     */
    protected DisplayMetrics dm;


    public FeedbackDialog(Context context) {
        super(context, R.style.DialogTransparent);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_feedback);
        setCancelable(false);

        initData();
        initView();
    }

    private void initData() {
        dm = context.getResources().getDisplayMetrics();
    }

    private void initView() {
        contentView = (RelativeLayout) findViewById(R.id.linear_feedback);
        cancelText = (TextView) findViewById(R.id.text_cancel);
        sureText = (TextView) findViewById(R.id.text_sure);

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
                ObjectAnimator.ofFloat(contentView, "translationX", 250 * dm.density, 0), //
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
                ObjectAnimator.ofFloat(contentView, "translationX", 0, -250 * dm.density), //
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_cancel:
                SpUtil.getInstance().putBoolean(AppConstants.FINISHED_RATE_OR_FEEDBACK, true);
                dismiss();
                break;
            case R.id.text_sure:
                SpUtil.getInstance().putBoolean(AppConstants.FINISHED_RATE_OR_FEEDBACK, true);
                context.startActivity(new Intent(context, FeedbackActivity.class));
                dismiss();
                break;
        }
    }
}
