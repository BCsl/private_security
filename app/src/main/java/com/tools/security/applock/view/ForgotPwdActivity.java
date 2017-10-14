package com.tools.security.applock.view;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.applock.presenter.ForgotPwdContract;
import com.tools.security.applock.presenter.ForgotPwdPresenter;
import com.tools.security.applock.view.lock.GestureCreateActivity;
import com.tools.security.applock.view.lock.NumberCreateActivity;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.TextChangedListenerAdapter;
import com.tools.security.main.MainActivity;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.ToastUtil;
import com.tools.security.widget.jumpingview.JumpingBeans;

/**
 * Created by lzx on 2017/1/7.
 * 忘记密码
 */

public class ForgotPwdActivity extends BaseActivity implements View.OnClickListener, ForgotPwdContract.View {

    private TextView mLockEmail, mBtnSend, mBtnReset;
    private EditText mEtVerCode;
    private String lockEmail = "";
    private ForgotPwdContract.Presenter mPresenter;
    private JumpingBeans jumpingBeans;
    private static final int REQUEST_CHANGE_PWD = 3;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_lock_forgetpwd;
    }

    @Override
    protected void init() {
        mLockEmail = (TextView) findViewById(R.id.tv_lock_email);
        mBtnSend = (TextView) findViewById(R.id.btn_send_email);
        mBtnReset = (TextView) findViewById(R.id.btn_reset);
        mEtVerCode = (EditText) findViewById(R.id.et_ver_code);
        mPresenter = new ForgotPwdPresenter(this);
        mBtnReset.setEnabled(false);
        mBtnSend.setOnClickListener(this);
        mBtnReset.setOnClickListener(this);
        lockEmail = SpUtil.getInstance().getString(AppConstants.LOCK_EMAIL, "");
        mLockEmail.setText(lockEmail);

        mEtVerCode.addTextChangedListener(new TextChangedListenerAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (s.length() > 0) {
                    mBtnReset.setEnabled(true);
                    mBtnReset.setBackgroundResource(R.drawable.bg_btn_blue);
                } else {
                    mBtnReset.setEnabled(false);
                    mBtnReset.setBackgroundResource(R.drawable.bg_btn_gray);
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in2,R.anim.slide_right_out2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_email:
                mPresenter.sendEmail(lockEmail);
                mBtnSend.setEnabled(false);
                jumpingBeans = JumpingBeans.with(mBtnSend).appendJumpingDots().build();
                mBtnReset.setEnabled(true);
                mBtnReset.setBackgroundResource(R.drawable.bg_btn_blue);
                break;
            case R.id.btn_reset:
                String verCode = mEtVerCode.getText().toString().trim();
                if (!TextUtils.isEmpty(verCode)) {
                    mPresenter.checkCode(lockEmail, verCode);
                    mBtnReset.setEnabled(false);
                    jumpingBeans = JumpingBeans.with(mBtnReset).appendJumpingDots().build();
                } else {
                    ToastUtil.showShort(getString(R.string.lock_reset_code_incorrect));
                }
                break;
        }
    }

    @Override
    public void sendEmailSuccess() {
        jumpingBeans.stopJumping();
        mBtnSend.setText(R.string.lock_send_btn);
        mBtnSend.setEnabled(true);
        ToastUtil.showShort(getString(R.string.lock_email_send_success));
    }

    @Override
    public void sendEmailError(String msg) {
        jumpingBeans.stopJumping();
        mBtnSend.setText(R.string.lock_send_btn);
        mBtnSend.setEnabled(true);
        ToastUtil.showShort(msg);
    }

    @Override
    public void checkCodeSuccess() {
        jumpingBeans.stopJumping();
        mBtnReset.setText(R.string.lock_reset_btn);
        mEtVerCode.setText("");
        mBtnReset.setEnabled(true);
        Intent intent;
        int lockType = SpUtil.getInstance().getInt(AppConstants.LOCK_TYPE);
        if (lockType == 0) {
            intent = new Intent(ForgotPwdActivity.this, GestureCreateActivity.class);
        } else {
            intent = new Intent(ForgotPwdActivity.this, NumberCreateActivity.class);
        }
        startActivityForResult(intent, REQUEST_CHANGE_PWD);
    }

    @Override
    public void checkCodeError(String msg) {
        jumpingBeans.stopJumping();
        mBtnReset.setText(R.string.lock_reset_btn);
        mBtnReset.setEnabled(true);
        ToastUtil.showShort(getString(R.string.lock_check_code_error));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHANGE_PWD) {
                ToastUtil.showShort(getString(R.string.lock_reset_success));
                startActivity(new Intent(ForgotPwdActivity.this, MainActivity.class));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jumpingBeans != null) {
            jumpingBeans.stopJumping();
        }
    }
}
