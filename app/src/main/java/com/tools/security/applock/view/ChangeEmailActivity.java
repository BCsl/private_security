package com.tools.security.applock.view;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.utils.LockUtil;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.ToastUtil;

/**
 * Created y lzx on 2017/1/9.
 * 修改保密邮箱
 */

public class ChangeEmailActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEdChangeEmail;
    private TextView mBtnDone;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_lock_change_email;
    }

    @Override
    protected void init() {
        mCustomTitleTextView.setTextSize(18);
        mEdChangeEmail = (EditText) findViewById(R.id.edit_change_email);
        mBtnDone = (TextView) findViewById(R.id.btn_done);
        mBtnDone.setOnClickListener(this);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in2,R.anim.slide_right_out2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done:
                String email = mEdChangeEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email) || !LockUtil.checkEmailFormat(email)) {
                    ToastUtil.showShort("Please enter the correct email");
                } else {
                    SpUtil.getInstance().putString(AppConstants.LOCK_EMAIL, email);
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }
}
