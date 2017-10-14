package com.tools.security.settings;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.tools.security.R;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.SecurityApplication;
import com.tools.security.common.WebActivity;
import com.tools.security.utils.FaceBookShareUtils;
import com.tools.security.utils.KochavaUtils;
import com.tools.security.utils.ToastUtil;


/**
 * description:关于我们
 * author: xiaodifu
 * date: 2016/12/13.
 */

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {


    private TextView mVersionName;
    private static final int SHARE_COMPLETE = 0;   //成功
    private static final int SHARE_CANCEL = 1;      //取消
    private static final int SHARE_ERROR = 2;        //错误
    private TextView mPrivacyPolicyBtn, mEulaBtn;
    private CallbackManager callbackManager ;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHARE_COMPLETE:
                    break;
                case SHARE_CANCEL:
                    break;
                case SHARE_ERROR:
                    ToastUtil.showShort("Network is weak, please try again");
                    break;
                default:
                    break;
            }
        }
    };

    private FacebookCallback facebookCallback =   new FacebookCallback() {
        @Override
        public void onSuccess(Object o) {
            mHandler.sendEmptyMessage(SHARE_COMPLETE);
        }
        @Override
        public void onCancel() {
            mHandler.sendEmptyMessage(SHARE_CANCEL);
        }
        @Override
        public void onError(FacebookException error) {
            mHandler.sendEmptyMessage(SHARE_ERROR);
            Log.i("facebook_error", error.toString());
        }
    };


    @Override
    protected int getContentViewId() {
        return R.layout.activity_about;
    }

    @Override
    protected void init() {
        mVersionName = (TextView) findViewById(R.id.version_name);
        mPrivacyPolicyBtn = (TextView) findViewById(R.id.privacy_policy_btn);
        mEulaBtn = (TextView) findViewById(R.id.eula_btn);

        mVersionName.setText("Version " + SecurityApplication.getInstance().getVersionName());
        mPrivacyPolicyBtn.setOnClickListener(this);
        mEulaBtn.setOnClickListener(this);

    }

    @Override
    protected int getOptionsMenuId() {
        return R.menu.menu_about_share_facebook;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_share_facebook) {
            callbackManager=  CallbackManager.Factory.create();
            new FaceBookShareUtils(this,callbackManager,facebookCallback)
                    .share("Ultra Security-Antivirus&App Lock on Google Play",
                            getResources().getString(R.string.share_facebook_content_dec),
                            AppConstants.GOOGLE_PLAY_URL);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(AboutUsActivity.this, WebActivity.class);
        String url = "";
        String title = "";
        switch (v.getId()) {
            case R.id.privacy_policy_btn:
                KochavaUtils.tracker(AppConstants.CLICK_ABOUT_PRIVACY);
                url = AppConstants.PROVACY_POLICY_URL;
                title = getString(R.string.privacy_policy);
                break;
            case R.id.eula_btn:
                KochavaUtils.tracker(AppConstants.CLICK_ABOUT_EULA);
                url = AppConstants.EULA_URL;
                title = getString(R.string.eula);
                break;
        }
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
