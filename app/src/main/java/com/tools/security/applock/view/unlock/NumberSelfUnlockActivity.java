package com.tools.security.applock.view.unlock;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.applock.db.CommLockInfoManager;
import com.tools.security.applock.presenter.NumberUnLockContract;
import com.tools.security.applock.presenter.NumberUnLockPresenter;
import com.tools.security.applock.view.ForgotPwdActivity;
import com.tools.security.applock.view.LockMainActivity;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/1/13.
 * 应用内数字解锁
 */

public class NumberSelfUnlockActivity extends BaseActivity implements View.OnClickListener, NumberUnLockContract.View {
    private TextView mLockTitle, mLockTip, mForgotBtn;
    private ImageView mNumPoint_1, mNumPoint_2, mNumPoint_3, mNumPoint_4;
    private TextView mNumber_0, mNumber_1, mNumber_2, mNumber_3, mNumber_4, mNumber_5, mNumber_6, mNumber_7, mNumber_8, mNumber_9;
    private ImageView mNumberDel;

    private static final int COUNT = 4; //4个点
    private List<String> numInput; //存储输入数字的列表
    private List<ImageView> pointList;
    private String lockPwd = "";//密码
    private String pkgName; //解锁应用的包名
    private String actionFrom;//按返回键的操作
    private NumberUnLockPresenter mPresenter;
    private Handler mHandler = new Handler();
    private TextView[] numberArray;
    private CommLockInfoManager mManager;

    //判断页面是否是从底部进入的
    private boolean isFromBottom = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_num_self_unlock;
    }


    @Override
    protected void init() {
        isFromBottom = getIntent().getIntExtra("from", 0) == 0 ? false : true;

        mForgotBtn = (TextView) findViewById(R.id.btn_forgot);
        mLockTitle = (TextView) findViewById(R.id.lock_title);
        mLockTip = (TextView) findViewById(R.id.lock_tip);
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

        numberArray = new TextView[]{mNumber_0, mNumber_1, mNumber_2, mNumber_3, mNumber_4, mNumber_5, mNumber_6, mNumber_7, mNumber_8, mNumber_9};
        for (TextView textView : numberArray) {
            textView.setOnClickListener(this);
            textView.setTextColor(Color.parseColor("#595959"));
            textView.setBackgroundResource(R.drawable.bg_num_cycle_gray);
        }

        mNumberDel.setImageResource(R.drawable.number_del_gray);
        mNumberDel.setOnClickListener(this);
        mForgotBtn.setOnClickListener(this);
        initNumLayout();
    }

    /**
     * 初始化数据
     */
    private void initNumLayout() {
        //获取解锁应用的包名
        pkgName = getIntent().getStringExtra(AppConstants.LOCK_PACKAGE_NAME);
        //获取按返回键的操作
        actionFrom = getIntent().getStringExtra(AppConstants.LOCK_FROM);
        //获取密码
        lockPwd = SpUtil.getInstance().getString(AppConstants.LOCK_PWD, "");
        mPresenter = new NumberUnLockPresenter(this);
        mManager = new CommLockInfoManager(this);
        numInput = new ArrayList<>();
        pointList = new ArrayList<>(COUNT);
        pointList.add(mNumPoint_1);
        pointList.add(mNumPoint_2);
        pointList.add(mNumPoint_3);
        pointList.add(mNumPoint_4);
        for (ImageView iv : pointList) {
            iv.setImageResource(R.drawable.ic_lock_pin_2);
        }
    }

    @Override
    protected void onHomeClick() {
        super.onHomeClick();
        if (isFromBottom) overridePendingTransition(R.anim.slide_scale_in,R.anim.slide_bottom_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.btn_forgot:
                startActivity(new Intent(NumberSelfUnlockActivity.this, ForgotPwdActivity.class));
                overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
                break;
        }
    }

    private void clickNumber(TextView textView) {
        mPresenter.clickNumber(numInput, pointList, textView.getText().toString(), lockPwd);
    }

    private void deleteNumber() {
        if (numInput.size() == 0) {
            return;
        }
        pointList.get(numInput.size() - 1).setImageResource(R.drawable.ic_lock_pin_2);
        numInput.remove(numInput.size() - 1);
    }


    @Override
    public void unLockSuccess() {
        if (actionFrom.equals(AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY)) {
            Intent intent=new Intent(NumberSelfUnlockActivity.this, LockMainActivity.class);
            if (isFromBottom)intent.putExtra("from",1);
            startActivity(intent);
            if (isFromBottom)overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_scale_out);
            finish();
        } else if (actionFrom.equals(AppConstants.LOCK_FROM_FINISH)) {
            mManager.unlockCommApplication(pkgName);
            finish();
        }else if(actionFrom.equals(AppConstants.LOCK_FROM_UNLOCK)){
            mManager.setIsUnLockThisApp(pkgName, true);
            mManager.unlockCommApplication(pkgName);
            sendBroadcast(new Intent(GestureUnlockActivity.FINISH_UNLOCK_THIS_APP));
            finish();
        }
    }

    @Override
    public void unLockError(int retryNum) {
        if (retryNum == 0) {

        }
        String format = getResources().getString(R.string.password_error_count);
//        String str = String.format(format, retryNum);
//        mLockTip.setText(str);
//        mLockTip.setTextColor(Color.RED);

//        把密码图标变成红色
        for (ImageView iv : pointList) {
            iv.setImageResource(R.drawable.number_point_error);
        }


    }

    @Override
    public void clearPassword() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (ImageView iv : pointList) {
                    iv.setImageResource(R.drawable.ic_lock_pin_2);
                }
            }
        }, 2000);
    }

    @Override
    public void setNumberPointImageResource(List<String> numInput) {
        int index = 0;
        for (ImageView iv : pointList) {
            if (index++ < numInput.size()) {
                iv.setImageResource(R.drawable.ic_lock_pin);
            } else {
                iv.setImageResource(R.drawable.ic_lock_pin_2);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

}
