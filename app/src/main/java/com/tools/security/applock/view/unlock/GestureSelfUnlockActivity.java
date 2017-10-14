package com.tools.security.applock.view.unlock;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.applock.LockPatternViewPattern;
import com.tools.security.applock.db.CommLockInfoManager;
import com.tools.security.applock.view.ForgotPwdActivity;
import com.tools.security.applock.view.LockMainActivity;
import com.tools.security.applock.view.LockSettingActivity;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.LockPatternUtils;
import com.tools.security.widget.LockPatternView;

import java.util.List;

/**
 * Created by lzx on 2017/1/13.
 * 应用内图案解锁
 */

public class GestureSelfUnlockActivity extends BaseActivity {

    private TextView mLockTitle, mLockTip, mForgotBtn;
    private LockPatternView mLockPatternView;

    private LockPatternUtils mLockPatternUtils;
    private LockPatternViewPattern mPatternViewPattern;
    private boolean unlockFlag = false;
    private boolean bPwdIsCorrent = true;
    private int mFailedPatternAttemptsSinceLastTimeout = 0;
    private String actionFrom;//按返回键的操作
    private String pkgName; //解锁应用的包名
    private CommLockInfoManager mManager;

    //判断页面是否是从底部进入的
    private boolean isFromBottom = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_gesture_self_unlock;
    }


    @Override
    protected void onHomeClick() {
        super.onHomeClick();
        if (isFromBottom) overridePendingTransition(R.anim.slide_scale_in,R.anim.slide_bottom_out);
    }

    @Override
    protected void init() {
        isFromBottom = getIntent().getIntExtra("from", 0) == 0 ? false : true;

        mForgotBtn = (TextView) findViewById(R.id.btn_forgot);
        mLockTitle = (TextView) findViewById(R.id.lock_title);
        mLockTip = (TextView) findViewById(R.id.lock_tip);
        mLockPatternView = (LockPatternView) findViewById(R.id.unlock_lock_view);
        mManager = new CommLockInfoManager(this);
        //获取解锁应用的包名
        pkgName = getIntent().getStringExtra(AppConstants.LOCK_PACKAGE_NAME);
        //获取按返回键的操作
        actionFrom = getIntent().getStringExtra(AppConstants.LOCK_FROM);

        initLockPatternView();

        mForgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GestureSelfUnlockActivity.this, ForgotPwdActivity.class));
                overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
            }
        });
    }

    /**
     * 初始化解锁控件
     */
    private void initLockPatternView() {
        mLockPatternView.setGesturePatternItemBg(R.drawable.gesture_pattern_item_bg_2_1);
        mLockPatternView.setGesturePatternSelected(R.drawable.gesture_pattern_selected_2);
        mLockPatternView.setGesturePatternSelectedWrong(R.drawable.gesture_pattern_selected_wrong_2);
        // mLockPatternView.setIshideline(true);
        mLockPatternUtils = new LockPatternUtils(this);
        mPatternViewPattern = new LockPatternViewPattern(mLockPatternView);
        mPatternViewPattern.setPatternListener(new LockPatternViewPattern.onPatternListener() {
            @Override
            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
                if (mLockPatternUtils.checkPattern(pattern)) { //解锁成功,更改数据库状态
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    if (actionFrom.equals(AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY)) {
                        Intent intent=new Intent(GestureSelfUnlockActivity.this, LockMainActivity.class);
                        if (isFromBottom) intent.putExtra("from",1);
                        startActivity(intent);
                        if (isFromBottom)overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_scale_out);
                        finish();
                    } else if (actionFrom.equals(AppConstants.LOCK_FROM_FINISH)) {
                        mManager.unlockCommApplication(pkgName);
                        finish();
                    } else if (actionFrom.equals(AppConstants.LOCK_FROM_SETTING)) {
                        startActivity(new Intent(GestureSelfUnlockActivity.this, LockSettingActivity.class));
                        overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
                        finish();
                    } else if (actionFrom.equals(AppConstants.LOCK_FROM_UNLOCK)) {
                        mManager.setIsUnLockThisApp(pkgName, true);
                        mManager.unlockCommApplication(pkgName);
                        sendBroadcast(new Intent(GestureUnlockActivity.FINISH_UNLOCK_THIS_APP));
                        finish();
                    }
                } else {
                    bPwdIsCorrent = false;
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
                        mFailedPatternAttemptsSinceLastTimeout++;
                        int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT - mFailedPatternAttemptsSinceLastTimeout;
                        if (retry >= 0) {
//                            String format = getResources().getString(R.string.password_error_count);
//                            String str = String.format(format, retry);
//                            mLockTip.setText(str);
//                            mLockTip.setTextColor(Color.RED);
                        }
                    } else {
                        //ToastUtil.showShort(getString(R.string.password_short));
                    }
                    if (mFailedPatternAttemptsSinceLastTimeout >= 3) { //失败次数大于3次

                    }
                    if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) { //失败次数大于阻止用户前的最大错误尝试次数

                    } else {
                        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
                    }
                }
            }
        });
        mLockPatternView.setOnPatternListener(mPatternViewPattern);
        mLockPatternView.setTactileFeedbackEnabled(true);
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    @Override
    public void onBackPressed() {
        if (actionFrom.equals(AppConstants.LOCK_FROM_SETTING)) {
        //   sendBroadcast(new Intent(LockSettingActivity.FINISH_ACTION));
        }
        super.onBackPressed();
    }
}
