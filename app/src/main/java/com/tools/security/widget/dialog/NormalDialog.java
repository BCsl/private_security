package com.tools.security.widget.dialog;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.widget.dialog.base.BaseDialog;

/**
 * description:普通弹窗
 * author: xiaodifu
 * date: 2017/1/12.
 */

public class NormalDialog extends BaseDialog implements View.OnClickListener {

    private TextView titleText, contentText, leftText, rightText;
    private IOnClickListener iOnClickListener;
    private String titleStr, contentStr, leftStr, rightStr;
    private View lineView;
    private int drwableLeftResId = -1;

    public NormalDialog(Context context, IOnClickListener iOnClickListener) {
        super(context);
        this.iOnClickListener = iOnClickListener;
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
        titleText = (TextView) findViewById(R.id.text_title);
        contentText = (TextView) findViewById(R.id.text_content);
        leftText = (TextView) findViewById(R.id.text_left);
        rightText = (TextView) findViewById(R.id.text_right);
        lineView = findViewById(R.id.line_v);

        leftText.setOnClickListener(this);
        rightText.setOnClickListener(this);
    }

    protected void setUiBeforShow() {

        if (drwableLeftResId != -1) {
            Log.e("TAG", "drwableLeftResId=" + drwableLeftResId);
            Drawable drawable = context.getResources().getDrawable(drwableLeftResId);
            if (drawable != null) {
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                titleText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                titleText.setCompoundDrawablePadding(ScreenUtil.dip2px(context, 10f));
            } else {
                Log.e("TAG", "drawable =null");
            }
        }
        if (TextUtils.isEmpty(titleStr)) {
            titleText.setVisibility(View.GONE);
        } else {
            titleText.setText(titleStr);
        }
        if (TextUtils.isEmpty(contentStr)) {
            contentText.setVisibility(View.GONE);
        } else {
            contentText.setText(contentStr);
        }
        if (TextUtils.isEmpty(leftStr)) {
            leftText.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
        } else {
            leftText.setText(leftStr);
        }
        if (TextUtils.isEmpty(rightStr)) {
            rightText.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
        } else {
            rightText.setText(rightStr);
        }
    }

    @Override
    public void onAttachedToWindow() {
        setUiBeforShow();
        super.onAttachedToWindow();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_normal;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_left:
                if (iOnClickListener != null) iOnClickListener.onLeftClick();
                break;
            case R.id.text_right:
                if (iOnClickListener != null) iOnClickListener.onRightClick();
                break;
        }
    }

    public NormalDialog title(String title) {
        this.titleStr = title;
        return this;
    }

    public NormalDialog content(String content) {
        this.contentStr = content;
        return this;
    }

    public NormalDialog left(String left) {
        this.leftStr = left;
        return this;
    }

    public NormalDialog right(String right) {
        this.rightStr = right;
        return this;
    }

    public NormalDialog drawableLeft(int resId) {
        this.drwableLeftResId = resId;
        return this;
    }

    public NormalDialog leftTextColor(int resId) {
        return this;
    }

    public NormalDialog rightTextColor(int resId) {
        return this;
    }

    public interface IOnClickListener {
        void onLeftClick();

        void onRightClick();
    }
}
