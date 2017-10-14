package com.tools.security.widget.dialog;

import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.widget.dialog.base.BaseDialog;

/**
 * description:检测更新的dialog
 * author: xiaodifu
 * date: 2017/1/11.
 */

public class CheckingVirusLibDialog extends BaseDialog implements View.OnClickListener {
    private TextView cancelText;
    private ICancelCheckCallback cancelCheckCallback;

    public CheckingVirusLibDialog(Context context, ICancelCheckCallback cancelCheckCallback) {
        super(context);
        this.cancelCheckCallback = cancelCheckCallback;
    }

    @Override
    protected float setWidthScale() {
        return 0.85f;
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
        setCancelable(false);
        cancelText = (TextView) findViewById(R.id.text_cancel);
        cancelText.setOnClickListener(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_checking_virus_lib;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_cancel:
                dismiss();
                cancelCheckCallback.cancelCheck();
                break;
        }
    }

    public interface ICancelCheckCallback {
        void cancelCheck();
    }
}
