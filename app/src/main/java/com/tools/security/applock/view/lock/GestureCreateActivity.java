/*******************************************************************************
 * Copyright (c) 2015 btows.com.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.tools.security.applock.view.lock;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.applock.LockPatternViewPattern;
import com.tools.security.applock.presenter.GestureCreateContract;
import com.tools.security.applock.presenter.GestureCreatePresenter;
import com.tools.security.bean.LockStage;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.LockPatternUtils;
import com.tools.security.utils.ToastUtil;
import com.tools.security.widget.LockPatternView;

import java.util.ArrayList;
import java.util.List;


/**
 * 创建图形解锁
 */
public class GestureCreateActivity extends BaseActivity implements View.OnClickListener, GestureCreateContract.View {

    private LockPatternView mLockPatternView;
    private TextView mLockTip;

    private LockStage mUiStage = LockStage.Introduction;
    public static final int ID_EMPTY_MESSAGE = -1;
    protected List<LockPatternView.Cell> mChosenPattern = null; //密码
    private static final String KEY_PATTERN_CHOICE = "chosenPattern";
    private static final String KEY_UI_STAGE = "uiStage";
    private final List<LockPatternView.Cell> mAnimatePattern = new ArrayList<>();
    private LockPatternUtils mLockPatternUtils;
    private LockPatternViewPattern mPatternViewPattern;
    private GestureCreatePresenter mGestureCreatePresenter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_gesture_lock;
    }

    @Override
    protected void initBundle(Bundle savedInstanceState) {
        super.initBundle(savedInstanceState);
        mLockTip = (TextView) findViewById(R.id.tv_lock_tip);
        mLockPatternView = (LockPatternView) findViewById(R.id.lock_pattern_view);

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
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in2,R.anim.slide_right_out2);
    }

    @Override
    protected void init() {

    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 初始化锁屏控件
     */
    private void initLockPatternView() {
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
            ToastUtil.showShort(text);
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
        mLockPatternView.postDelayed(mClearPatternRunnable, 1000);
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
        mChosenPattern = null;
        clearPattern();
    }

    /**
     * 画成功了
     */
    @Override
    public void ChoiceConfirmed() {
        mLockPatternUtils.saveLockPattern(mChosenPattern); //保存密码
        clearPattern();
        setResult(RESULT_OK);
        finish();
    }

}
