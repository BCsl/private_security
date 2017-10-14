package com.tools.security.applock.view.lock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.applock.LockPatternViewPattern;
import com.tools.security.applock.db.CommLockInfoManager;
import com.tools.security.applock.presenter.GestureCreateContract;
import com.tools.security.applock.presenter.GestureCreatePresenter;
import com.tools.security.applock.presenter.NumberCreateContract;
import com.tools.security.applock.presenter.NumberCreatePresenter;
import com.tools.security.applock.view.AppLockFirstActivity;
import com.tools.security.applock.view.LockSuccessActivity;
import com.tools.security.bean.CommLockInfo;
import com.tools.security.bean.LockStage;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.service.LockService;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.LockPatternUtils;
import com.tools.security.utils.LockUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.ToastUtil;
import com.tools.security.widget.LockPatternView;
import com.tools.security.widget.dialog.LockPermissionsDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 第一次创建应用锁界面
 * Created by lzx on 2017/1/8.
 */

public class FirstCreateActivity extends BaseActivity implements View.OnClickListener, GestureCreateContract.View, NumberCreateContract.View {

    private TextView mStepOne, mStepTwo, mStepThree, mLockTip;
    private View mLineOneToTwo, mLineTwoToThree;
    private LockPatternView mLockPatternView;

    private LinearLayout mNumLockLayout;
    private ImageView mNumPoint_1, mNumPoint_2, mNumPoint_3, mNumPoint_4;
    private TextView mNumber_0, mNumber_1, mNumber_2, mNumber_3, mNumber_4, mNumber_5, mNumber_6, mNumber_7, mNumber_8, mNumber_9;
    private ImageView mNumberDel;

    private TextView mSwitchLock;

    private LinearLayout mEmailLayout;
    private EditText mEditEmail;
    private TextView mDoneBtn;

    private int currLocType = 0;//当前锁类型 0 图案 1 数字

    //图案锁相关
    private LockStage mUiStage = LockStage.Introduction;
    public static final int ID_EMPTY_MESSAGE = -1;
    protected List<LockPatternView.Cell> mChosenPattern = null; //密码
    private static final String KEY_PATTERN_CHOICE = "chosenPattern";
    private static final String KEY_UI_STAGE = "uiStage";
    private final List<LockPatternView.Cell> mAnimatePattern = new ArrayList<>();
    private LockPatternUtils mLockPatternUtils;
    private LockPatternViewPattern mPatternViewPattern;
    private GestureCreatePresenter mGestureCreatePresenter;

    //数字锁相关
    private static final int COUNT = 4; //4个点
    private List<String> numInput; //存储输入数字的列表
    private List<ImageView> pointList;
    private NumberCreateContract.Presenter mPresenter;

    private ObjectAnimator rotationAnimator;

    private ArrayList<CommLockInfo> mLockList; //保存的加锁应用
    private ArrayList<CommLockInfo> mUnLockList; //保存的没加锁应用
    private CommLockInfoManager mLockInfoManager;
    private boolean isDoned = false;
    private InputMethodManager imm;
    private int RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1;
    private int RESULT_ACTION_MANAGE_OVERLAY_PERMISSION = 2;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_first_create_lock;
    }

    @Override
    protected void initBundle(Bundle savedInstanceState) {
        super.initBundle(savedInstanceState);
        mLineOneToTwo = findViewById(R.id.one_to_two);
        mLineTwoToThree = findViewById(R.id.two_to_three);
        mStepOne = (TextView) findViewById(R.id.step_one);
        mStepTwo = (TextView) findViewById(R.id.step_two);
        mStepThree = (TextView) findViewById(R.id.step_three);
        mLockTip = (TextView) findViewById(R.id.lock_tip);
        mLockPatternView = (LockPatternView) findViewById(R.id.lock_pattern_view);
        mNumLockLayout = (LinearLayout) findViewById(R.id.num_lock_layout);
        mNumPoint_1 = (ImageView) findViewById(R.id.num_point_1);
        mNumPoint_2 = (ImageView) findViewById(R.id.num_point_2);
        mNumPoint_3 = (ImageView) findViewById(R.id.num_point_3);
        mNumPoint_4 = (ImageView) findViewById(R.id.num_point_4);
        mNumber_0 = (TextView) findViewById(R.id.number_0);
        mNumber_1 = (TextView) findViewById(R.id.number_1);
        mNumber_2 = (TextView) findViewById(R.id.number_2);
        mNumber_3 = (TextView) findViewById(R.id.number_3);
        mNumber_4 = (TextView) findViewById(R.id.number_4);
        mNumber_5 = (TextView) findViewById(R.id.number_5);
        mNumber_6 = (TextView) findViewById(R.id.number_6);
        mNumber_7 = (TextView) findViewById(R.id.number_7);
        mNumber_8 = (TextView) findViewById(R.id.number_8);
        mNumber_9 = (TextView) findViewById(R.id.number_9);
        mNumberDel = (ImageView) findViewById(R.id.number_del);
        mSwitchLock = (TextView) findViewById(R.id.switch_lock);
        mEmailLayout = (LinearLayout) findViewById(R.id.email_layout);
        mEditEmail = (EditText) findViewById(R.id.email_edit);
        mDoneBtn = (TextView) findViewById(R.id.done_btn);

        // 初始化演示动画
        mAnimatePattern.add(LockPatternView.Cell.of(0, 0));
        mAnimatePattern.add(LockPatternView.Cell.of(0, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(1, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(2, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(2, 2));

        mGestureCreatePresenter = new GestureCreatePresenter(this, this);
        initLockPatternView();
        if (savedInstanceState == null) {
            mGestureCreatePresenter.updateStage(LockStage.Introduction);
        } else {
            final String patternString = savedInstanceState.getString(KEY_PATTERN_CHOICE);
            if (patternString != null) {
                mChosenPattern = LockPatternUtils.stringToPattern(patternString);
            }
            mGestureCreatePresenter.updateStage(LockStage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
        }

        // 初始化数字锁
        initNumLayout();

        mLockInfoManager = new CommLockInfoManager(this);
        mLockList = getIntent().getParcelableArrayListExtra("lock_list");
        mUnLockList = getIntent().getParcelableArrayListExtra("unlock_list");
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void init() {
        mSwitchLock.setText(R.string.lock_switch_number_code);
        mSwitchLock.setOnClickListener(this);
        mDoneBtn.setOnClickListener(this);
        mNumber_0.setOnClickListener(this);
        mNumber_1.setOnClickListener(this);
        mNumber_2.setOnClickListener(this);
        mNumber_3.setOnClickListener(this);
        mNumber_4.setOnClickListener(this);
        mNumber_5.setOnClickListener(this);
        mNumber_6.setOnClickListener(this);
        mNumber_7.setOnClickListener(this);
        mNumber_8.setOnClickListener(this);
        mNumber_9.setOnClickListener(this);
        mNumberDel.setOnClickListener(this);
        rotationAnimator = ObjectAnimator.ofFloat(mDoneBtn, "rotationX", 0, 360);
        rotationAnimator.setDuration(500);
        rotationAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // mDoneBtn.setText("Success");
                String lockEmail = mEditEmail.getText().toString().trim();

                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) { //如果大于21
                    if (!LockUtil.isStatAccessPermissionSet(FirstCreateActivity.this)) { //如果没权限
                        if (LockUtil.isNoOption(FirstCreateActivity.this)) { //如果有设置界面
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (!Settings.canDrawOverlays(FirstCreateActivity.this)) { //没悬浮窗权限转跳授权界面
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    startActivityForResult(intent, RESULT_ACTION_MANAGE_OVERLAY_PERMISSION);
                                } else {
                                    gotoPermissionActivity(); //有悬浮窗权限转跳到授权界面
                                }
                            } else {
                                gotoPermissionActivity(); //小于23转跳到授权界面
                            }
                        } else {
                            gotoLockMainActivity(lockEmail); //没设置界面 直接转跳
                        }
                    } else {
                        gotoLockMainActivity(lockEmail); //有权限直接转跳
                    }
                } else {
                    gotoLockMainActivity(lockEmail); //小于21直接转跳
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_ACTION_MANAGE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(FirstCreateActivity.this)) {
                    gotoPermissionActivity(); //有悬浮窗权限转跳到授权界面
                }
            }
        } else if (requestCode == RESULT_ACTION_USAGE_ACCESS_SETTINGS) {
            String lockEmail = mEditEmail.getText().toString().trim();
            if (mEmailLayout.getVisibility() == View.VISIBLE
                    && !TextUtils.isEmpty(mEditEmail.getText().toString().trim())
                    && LockUtil.isStatAccessPermissionSet(FirstCreateActivity.this)
                    && LockUtil.checkEmailFormat(lockEmail)
                    && isDoned) {
                gotoLockMainActivity(lockEmail);
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_lock:
                if (mSwitchLock.getText().toString().equals(getString(R.string.lock_switch_number_code))) {
                    currLocType = 1;
                    mLockPatternView.setVisibility(View.GONE);
                    mNumLockLayout.setVisibility(View.VISIBLE);
                    mSwitchLock.setText(R.string.lock_switch_gesture_code);
                    mLockTip.setText(R.string.num_create_text_01);
                    SpUtil.getInstance().putInt(AppConstants.LOCK_TYPE, 1);
                } else if (mSwitchLock.getText().toString().equals(getString(R.string.lock_switch_gesture_code))) {
                    currLocType = 0;
                    mLockPatternView.setVisibility(View.VISIBLE);
                    mNumLockLayout.setVisibility(View.GONE);
                    mSwitchLock.setText(R.string.lock_switch_number_code);
                    mLockTip.setText(R.string.lock_tip_1);
                    SpUtil.getInstance().putInt(AppConstants.LOCK_TYPE, 0);
                } else if (mSwitchLock.getText().toString().equals(getString(R.string.lock_switch_reset))) {
                    //恢复到第一步
                    clearPattern();
                    setStepOne();
                    initNumLayout();
                }
                break;
            case R.id.number_0:
            case R.id.number_1:
            case R.id.number_2:
            case R.id.number_3:
            case R.id.number_4:
            case R.id.number_5:
            case R.id.number_6:
            case R.id.number_7:
            case R.id.number_8:
            case R.id.number_9:
                clickNumber((TextView) v);
                break;
            case R.id.number_del:
                deleteNumber();
                break;
            case R.id.done_btn:
                String lockEmail = mEditEmail.getText().toString().trim();
                if (TextUtils.isEmpty(lockEmail)) {
                    ToastUtil.showLong(getString(R.string.lock_email_tip));
                } else if (!LockUtil.checkEmailFormat(lockEmail)) {
                    ToastUtil.showLong(getString(R.string.lock_email_error_tip));
                } else {
                    isDoned = true;
                    rotationAnimator.start();
                }
                break;
        }
    }

    private void gotoLockMainActivity(String lockEmail) {
        sendBroadcast(new Intent(AppLockFirstActivity.ACTION_FINISH));

        for (CommLockInfo pro : mLockList) {
            mLockInfoManager.lockCommApplication(pro.getPackageName());
        }
        for (CommLockInfo pro : mUnLockList) {
            mLockInfoManager.unlockCommApplication(pro.getPackageName());
        }
        SpUtil.getInstance().putBoolean(AppConstants.LOCK_STATE, true); //开启应用锁开关

        startService(new Intent(this, LockService.class));
        SpUtil.getInstance().putString(AppConstants.LOCK_EMAIL, lockEmail);
        SpUtil.getInstance().putBoolean(AppConstants.LOCK_IS_FIRST_LOCK, false);
        startActivity(new Intent(this, LockSuccessActivity.class));
        finish();
    }

    private void gotoPermissionActivity() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, RESULT_ACTION_USAGE_ACCESS_SETTINGS);
        new LockPermissionsDialog(FirstCreateActivity.this).show();
    }

    //************************************图案锁相关*************************************************

    /**
     * 恢复到第一步
     */
    private void setStepOne() {
        mGestureCreatePresenter.updateStage(LockStage.Introduction);
        mStepOne.setBackgroundResource(R.drawable.bg_white_round);
        mStepOne.setText("1");
        mLineOneToTwo.setBackgroundColor(ContextCompat.getColor(this, R.color.white_tr4));
        mStepTwo.setBackgroundResource(R.drawable.bg_white80_round);
        if (currLocType == 1) {
            mSwitchLock.setText(R.string.lock_switch_gesture_code);
        } else {
            mSwitchLock.setText(R.string.lock_switch_number_code);
        }
    }

    /**
     * 初始化锁屏控件
     */
    private void initLockPatternView() {
        mLockPatternView.setLineColorRight(0x66ffffff);
        mLockPatternUtils = new LockPatternUtils(this);
        mPatternViewPattern = new LockPatternViewPattern(mLockPatternView);
        mPatternViewPattern.setPatternListener(new LockPatternViewPattern.onPatternListener() {
            @Override
            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
                mGestureCreatePresenter.onPatternDetected(pattern, mChosenPattern, mUiStage);
            }
        });
        mLockPatternView.setOnPatternListener(mPatternViewPattern);
        mLockPatternView.setTactileFeedbackEnabled(true);
    }

    /**
     * 更新当前锁的状态
     */
    @Override
    public void updateUiStage(LockStage stage) {
        mUiStage = stage;
    }

    /**
     * 更新当前密码
     */
    @Override
    public void updateChosenPattern(List<LockPatternView.Cell> mChosenPattern) {
        this.mChosenPattern = mChosenPattern;
    }

    /**
     * 更新提示信息
     */
    @Override
    public void updateLockTip(String text, boolean isToast) {
        if (isToast) {
            ToastUtil.showLong(text);
        } else {
            mLockTip.setText(text);
        }
    }

    /**
     * 更新提示信息
     */
    @Override
    public void setHeaderMessage(int headerMessage) {
        mLockTip.setText(headerMessage);
    }

    /**
     * LockPatternView的一些配置
     */
    @Override
    public void lockPatternViewConfiguration(boolean patternEnabled, LockPatternView.DisplayMode displayMode) {
        if (patternEnabled) {
            mLockPatternView.enableInput();
        } else {
            mLockPatternView.disableInput();
        }
        mLockPatternView.setDisplayMode(displayMode);
    }

    /**
     * 初始化
     */
    @Override
    public void Introduction() {
        clearPattern();
    }

    /**
     * 展示帮助动画
     */
    @Override
    public void HelpScreen() {
        mLockPatternView.setPattern(LockPatternView.DisplayMode.Animate, mAnimatePattern);
    }

    /**
     * 路径太短
     */
    @Override
    public void ChoiceTooShort() {
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);  //路径太短
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    /**
     * 画完第一步转到第二步
     */
    @Override
    public void moveToStatusTwo() {
        mStepOne.setText("");
        mStepOne.setBackgroundResource(R.drawable.ic_lock_first_nav_suc);
        mStepTwo.setBackgroundResource(R.drawable.bg_white_round);
        mLineOneToTwo.setBackgroundColor(Color.WHITE);
        //  mSwitchLock.setVisibility(View.INVISIBLE);
        mSwitchLock.setText(R.string.lock_switch_reset);
    }

    /**
     * 清空控件路径
     */
    @Override
    public void clearPattern() {
        mLockPatternView.clearPattern();
    }

    /**
     * 第一次和第二次画得不一样
     */
    @Override
    public void ConfirmWrong() {
        //mChosenPattern = new ArrayList<>();
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);  //路径太短
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 500);
    }

    /**
     * 画成功了
     */
    @Override
    public void ChoiceConfirmed() {
        mStepTwo.setText("");
        mStepOne.setBackgroundResource(R.drawable.ic_lock_first_nav_suc);
        mStepTwo.setBackgroundResource(R.drawable.ic_lock_first_nav_suc);
        mStepThree.setBackgroundResource(R.drawable.bg_white_round);
        mLineOneToTwo.setBackgroundColor(Color.WHITE);
        mLineTwoToThree.setBackgroundColor(Color.WHITE);
        mLockPatternUtils.saveLockPattern(mChosenPattern); //保存密码

        SpUtil.getInstance().putInt(AppConstants.LOCK_TYPE, 0);

        mEditEmail.setText(AppUtils.getGmail(this));

        mLockTip.setText(R.string.lock_tip_3);
        mLockPatternView.setVisibility(View.GONE);
        //  mSwitchLock.setVisibility(View.INVISIBLE);
        mSwitchLock.setVisibility(View.GONE);
        mEmailLayout.setVisibility(View.VISIBLE);
        clearPattern();

        mEditEmail.setFocusable(true);
        mEditEmail.requestFocus();
        mEditEmail.setCursorVisible(true);
        //弹出软键盘
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }
    //************************************图案锁end*************************************************

    //************************************数字锁相关*************************************************

    /**
     * 初始化数据
     */
    private void initNumLayout() {
        mPresenter = new NumberCreatePresenter(this);
        numInput = new ArrayList<>();
        pointList = new ArrayList<>(COUNT);
        pointList.add(mNumPoint_1);
        pointList.add(mNumPoint_2);
        pointList.add(mNumPoint_3);
        pointList.add(mNumPoint_4);
        for (ImageView iv : pointList) {
            iv.setImageResource(R.drawable.num_point);
        }
    }

    /**
     * 点击数字
     */
    private void clickNumber(TextView btn) {
        mPresenter.clickNumber(numInput, pointList, btn.getText().toString().trim());
    }

    /**
     * 删除按钮
     */
    private void deleteNumber() {
        if (numInput.size() == 0) {
            return;
        }
        pointList.get(numInput.size() - 1).setImageResource(R.drawable.num_point);
        numInput.remove(numInput.size() - 1);
    }

    /**
     * 设置密码
     */
    @Override
    public void setNumberPointImageResource(ImageView iv, int resId) {
        iv.setImageResource(resId);
    }

    /**
     * 更新提示
     */
    @Override
    public void updateLockTipString(int resId, boolean isToast) {
        if (isToast) {
            ToastUtil.showLong(getString(resId));
            // initNumLayout();

        } else {
            mLockTip.setText(resId);
        }
        mLockTip.postDelayed(showImageRunnable, 500);
        if (resId == R.string.num_create_text_03) {
            //恢复到第一步
//            mStepOne.setBackgroundResource(R.drawable.bg_white_round);
//            mStepOne.setText("1");
//            mLineOneToTwo.setBackgroundColor(ContextCompat.getColor(this, R.color.white_tr4));
//            mStepTwo.setBackgroundResource(R.drawable.bg_white80_round);
//            mLockTip.setText(R.string.num_create_text_01);
//            mSwitchLock.setVisibility(View.VISIBLE);
//            numInput.clear();
//            mLockTip.postDelayed(showImageRunnable, 500);
        }
    }

    private Runnable showImageRunnable = new Runnable() {
        @Override
        public void run() {
            for (ImageView iv : pointList) {
                iv.setImageResource(R.drawable.num_point);
            }
        }
    };

    /**
     * 第一步到第二步
     */
    @Override
    public void completedFirstTime() {
        moveToStatusTwo();
        mLockTip.postDelayed(showImageRunnable, 500);
    }

    /**
     * 成功了
     */
    @Override
    public void createLockSuccess() {
        mStepTwo.setText("");
        mStepOne.setBackgroundResource(R.drawable.ic_lock_first_nav_suc);
        mStepTwo.setBackgroundResource(R.drawable.ic_lock_first_nav_suc);
        mStepThree.setBackgroundResource(R.drawable.bg_white_round);
        mLineOneToTwo.setBackgroundColor(Color.WHITE);
        mLineTwoToThree.setBackgroundColor(Color.WHITE);

        SpUtil.getInstance().putInt(AppConstants.LOCK_TYPE, 1);

        mEditEmail.setText(AppUtils.getGmail(this));

        mLockTip.setText(R.string.lock_tip_3);
        mNumLockLayout.setVisibility(View.GONE);
        mLockPatternView.setVisibility(View.GONE);
        mSwitchLock.setVisibility(View.INVISIBLE);
        mEmailLayout.setVisibility(View.VISIBLE);
        mSwitchLock.setVisibility(View.GONE);
        mEditEmail.setFocusable(true);
        //弹出软键盘
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    //************************************数字锁end*************************************************


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGestureCreatePresenter.onDestroy();
        mPresenter.onDestroy();
        rotationAnimator = null;
    }
}
