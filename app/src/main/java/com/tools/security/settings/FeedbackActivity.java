package com.tools.security.settings;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.SecurityApplication;
import com.tools.security.common.TextChangedListenerAdapter;
import com.tools.security.widget.popupwindow.FeedbackPopWindow;

/**
 * Created by lzx on 2016/12/30.
 * 用户反馈界面
 */

public class FeedbackActivity extends BaseActivity {

    private EditText mEditFeedBack;
    private MenuItem mMenuItem;
    private TextView mBtnSelect;
    private ImageView mImgTriangle;
    private FeedbackPopWindow popWindow;
    private String mFeedbackType = "Common";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void init() {
        mEditFeedBack = (EditText) findViewById(R.id.edit_feedback);
        mBtnSelect = (TextView) findViewById(R.id.btn_select);
        mImgTriangle = (ImageView) findViewById(R.id.img_triangle);
        popWindow = new FeedbackPopWindow(FeedbackActivity.this);

        mEditFeedBack.addTextChangedListener(new TextChangedListenerAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (s.length() > 0) {
                    mMenuItem.getIcon().setAlpha(255);
                    mMenuItem.setEnabled(true);
                } else {
                    mMenuItem.getIcon().setAlpha(178);
                    mMenuItem.setEnabled(false);
                }
            }
        });
        mBtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnSelect.setText(getString(R.string.feedback_common));
                popWindow.setWidth(mBtnSelect.getWidth());
                popWindow.showAsDropDown(mBtnSelect);
                mImgTriangle.setRotation(180);
                popWindow.setOnItemClick(new FeedbackPopWindow.OnItemClick() {
                    @Override
                    public void onClick(String text, String feedbackType) {
                        mFeedbackType = feedbackType;
                        mBtnSelect.setText(text);
                        popWindow.dismiss();
                    }
                });
                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mImgTriangle.setRotation(0);
                    }
                });
            }
        });
    }

    private void feedback(String bodyString) {
        String subject = "[" + getString(R.string.app_name) + " " + SecurityApplication.getInstance().getVersionName() + " android feedback]";

        StringBuilder body = new StringBuilder(bodyString);
        body.append("\n\nFeedback Type:" + mFeedbackType);
        body.append("\r\nDevice Brand:" + SecurityApplication.getInstance().getOperatorName());
        body.append("\r\nOs Version:").append(SecurityApplication.getInstance().getOsVersion());
        body.append("\r\n\r\nSceen Density:").append(SecurityApplication.getInstance().getScreenSize());
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
        startActivity(emailIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        mMenuItem = menu.findItem(R.id.menu_feedback_send);
        mMenuItem.getIcon().setAlpha(178);
        mMenuItem.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_feedback_send) {
            String editString = mEditFeedBack.getText().toString().trim();
            if (!TextUtils.isEmpty(editString)) {
                feedback(editString);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
