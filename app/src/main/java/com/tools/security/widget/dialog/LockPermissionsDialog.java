package com.tools.security.widget.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.utils.AppUtils;
import com.tools.security.widget.dialog.base.BaseDialog;

/**
 * Created by lzx on 2017/1/14.
 * 应用锁申请权限dialog
 */

public class LockPermissionsDialog extends BaseDialog {

    private Context mContext;
    private TextView mBtnGot;
    private ImageView mIcHand, mSwitchLock;
    private ValueAnimator moveAnim;

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_permissions_lock;
    }

    public LockPermissionsDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected float setWidthScale() {
        return 0.8f;
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
        mBtnGot = (TextView) findViewById(R.id.btn_got);
        mIcHand = (ImageView) findViewById(R.id.ic_hand);
        mSwitchLock = (ImageView) findViewById(R.id.switch_lock);

        //移动
        moveAnim = ValueAnimator.ofFloat(0, AppUtils.dip2px(mContext, 134));
        moveAnim.setDuration(1500);
        moveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                mIcHand.setTranslationY(-value);
                mIcHand.setTranslationX(value / 2);
            }
        });
        moveAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mSwitchLock.setImageResource(R.drawable.ic_open_grey);
            }
        });
        moveAnim.start();
      //  moveAnim.setRepeatCount(1000);
        mBtnGot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        moveAnim = null;
        super.dismiss();
    }
}
