package com.tools.security.applock.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.applock.view.lock.GestureCreateActivity;
import com.tools.security.applock.view.lock.NumberCreateActivity;
import com.tools.security.applock.view.unlock.GestureSelfUnlockActivity;
import com.tools.security.applock.view.unlock.NumberSelfUnlockActivity;
import com.tools.security.bean.LockAutoTime;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.ToastUtil;
import com.tools.security.widget.dialog.SelectLockTimeDialog;

/**
 * Created by lzx on 2017/1/6.
 * 应用锁设置界面
 */
public class LockSettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DialogInterface.OnDismissListener {

    private TextView mCbLockTypeNum, mCbLockTypePattern;
    private TextView mBtnChangePwd, mBtnEmail, mBtnSelectTime, mLockTime;
    private SwitchCompat mSwitchAllowExit, mSwitchRelockScreen;

    private Intent intent;
    private static final int REQUEST_GESTRUE_CREATE = 0;
    private static final int REQUEST_NUMBER_CREATE = 1;
    private static final int REQUEST_CHANGE_EMAIL = 2;
    private static final int REQUEST_CHANGE_PWD = 3;

    private Drawable drawableSelect;
    private Drawable drawableNormal;
    private String currLockType; //当前的锁屏模式
    private String TYPE_GESTURE = "type_Gesture";
    private String TYPE_NUMBER = "type_Number";

    private LockSettingReceiver mLockSettingReceiver;
    public static final String ON_ITEM_CLICK_ACTION = "on_item_click_action";
    public static final String FINISH_ACTION = "finish_action";
    private SelectLockTimeDialog dialog;
    private boolean isClickHome = false;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_lock_setting;
    }

    @Override
    protected void init() {
        mCbLockTypeNum = (TextView) findViewById(R.id.lock_type_num);
        mCbLockTypePattern = (TextView) findViewById(R.id.lock_type_pattern);
        mBtnChangePwd = (TextView) findViewById(R.id.btn_change_pwd);
        mBtnEmail = (TextView) findViewById(R.id.btn_email);
        mBtnSelectTime = (TextView) findViewById(R.id.btn_select_time);
        mLockTime = (TextView) findViewById(R.id.lock_time);
        mSwitchAllowExit = (SwitchCompat) findViewById(R.id.switch_allow_exit);
        mSwitchRelockScreen = (SwitchCompat) findViewById(R.id.switch_relock_screen);

        mLockSettingReceiver = new LockSettingReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ON_ITEM_CLICK_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_OFF); //锁屏
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS); //home键
        filter.addAction(FINISH_ACTION);
        registerReceiver(mLockSettingReceiver, filter);

        dialog = new SelectLockTimeDialog(this, "");
        dialog.setOnDismissListener(this);

        String lockEmail = SpUtil.getInstance().getString(AppConstants.LOCK_EMAIL, "");
        mBtnEmail.setText("Email:" + lockEmail);
        int lockType = SpUtil.getInstance().getInt(AppConstants.LOCK_TYPE);
        drawableSelect = getResources().getDrawable(R.drawable.lock_select);
        drawableNormal = getResources().getDrawable(R.drawable.lock_unselect);
        drawableSelect.setBounds(0, 0, drawableSelect.getMinimumWidth(), drawableSelect.getMinimumHeight());
        drawableNormal.setBounds(0, 0, drawableNormal.getMinimumWidth(), drawableNormal.getMinimumHeight());
        mCbLockTypePattern.setCompoundDrawables(null, null, lockType == 0 ? drawableSelect : drawableNormal, null);
        mCbLockTypeNum.setCompoundDrawables(null, null, lockType != 0 ? drawableSelect : drawableNormal, null);
        currLockType = lockType == 0 ? TYPE_GESTURE : TYPE_NUMBER;

        boolean isLockAutoScreenTime = SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_SCREEN_TIME, false);
        boolean isLockAutoScreen = SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_SCREEN, false);
        if (isLockAutoScreenTime) {
            mSwitchAllowExit.setChecked(true);
        }
        if (isLockAutoScreen) {
            mSwitchRelockScreen.setChecked(true);
        }
        String apartTitle = SpUtil.getInstance().getString(AppConstants.LOCK_APART_TITLE, "");
        mLockTime.setText(apartTitle);

        mCbLockTypeNum.setOnClickListener(this);
        mCbLockTypePattern.setOnClickListener(this);

        mBtnEmail.setOnClickListener(this);
        mBtnChangePwd.setOnClickListener(this);
        mBtnSelectTime.setOnClickListener(this);
        mLockTime.setOnClickListener(this);
        mSwitchAllowExit.setOnCheckedChangeListener(this);
        mSwitchRelockScreen.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_email:
                startActivityForResult(new Intent(LockSettingActivity.this, ChangeEmailActivity.class), REQUEST_CHANGE_EMAIL);
                overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
                break;
            case R.id.btn_change_pwd:
                int lockType = SpUtil.getInstance().getInt(AppConstants.LOCK_TYPE);
                if (lockType == 0) {
                    intent = new Intent(LockSettingActivity.this, GestureCreateActivity.class);
                } else {
                    intent = new Intent(LockSettingActivity.this, NumberCreateActivity.class);
                }
                startActivityForResult(intent, REQUEST_CHANGE_PWD);
                overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
                break;
            case R.id.lock_time:
            case R.id.btn_select_time:
                String title = SpUtil.getInstance().getString(AppConstants.LOCK_APART_TITLE, "");
                dialog.setTitle(title);
                dialog.show();
                break;
            case R.id.lock_type_pattern:
                if (!currLockType.equals(TYPE_GESTURE)) {
                    intent = new Intent(LockSettingActivity.this, GestureCreateActivity.class);
                    startActivityForResult(intent, REQUEST_GESTRUE_CREATE);
                    overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
                }
                break;
            case R.id.lock_type_num:
                if (!currLockType.equals(TYPE_NUMBER)) {
                    intent = new Intent(LockSettingActivity.this, NumberCreateActivity.class);
                    startActivityForResult(intent, REQUEST_NUMBER_CREATE);
                    overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
                }
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in2, R.anim.slide_right_out2);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_allow_exit:
                if (isChecked && !dialog.isShowing()) {
                    dialog.show();
                }
                if (!isChecked) {
                    mLockTime.setText("");
                    SpUtil.getInstance().putString(AppConstants.LOCK_APART_TITLE, "");
                    SpUtil.getInstance().putLong(AppConstants.LOCK_APART_MILLISENCONS, 0L);
                }
                SpUtil.getInstance().putBoolean(AppConstants.LOCK_AUTO_SCREEN_TIME, isChecked);
                break;
            case R.id.switch_relock_screen:
                SpUtil.getInstance().putBoolean(AppConstants.LOCK_AUTO_SCREEN, isChecked);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CHANGE_EMAIL:
                    String lockEmail = SpUtil.getInstance().getString(AppConstants.LOCK_EMAIL, "");
                    mBtnEmail.setText("Email:" + lockEmail);
                    break;
                case REQUEST_GESTRUE_CREATE:
                    mCbLockTypePattern.setCompoundDrawables(null, null, drawableSelect, null);
                    mCbLockTypeNum.setCompoundDrawables(null, null, drawableNormal, null);
                    SpUtil.getInstance().putInt(AppConstants.LOCK_TYPE, 0);
                    currLockType = TYPE_GESTURE;
                    break;
                case REQUEST_NUMBER_CREATE:
                    mCbLockTypePattern.setCompoundDrawables(null, null, drawableNormal, null);
                    mCbLockTypeNum.setCompoundDrawables(null, null, drawableSelect, null);
                    SpUtil.getInstance().putInt(AppConstants.LOCK_TYPE, 1);
                    currLockType = TYPE_NUMBER;
                    break;
                case REQUEST_CHANGE_PWD:
                    ToastUtil.showShort(getString(R.string.lock_reset_success));
                    break;
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (TextUtils.isEmpty(mLockTime.getText().toString())) {
            mSwitchAllowExit.setChecked(false);
        }
    }


    private class LockSettingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ON_ITEM_CLICK_ACTION)) {
                LockAutoTime info = intent.getParcelableExtra("info");
                mSwitchAllowExit.setChecked(true);
                mLockTime.setText(info.getTitle());
                SpUtil.getInstance().putString(AppConstants.LOCK_APART_TITLE, info.getTitle());
                SpUtil.getInstance().putLong(AppConstants.LOCK_APART_MILLISENCONS, info.getTime());
                SpUtil.getInstance().putBoolean(AppConstants.LOCK_AUTO_SCREEN_TIME, true);
                dialog.dismiss();
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                gotoUnLockActivity();
            } else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                sendBroadcast(new Intent(LockMainActivity.ACTION_CLICK_HOME));
                isClickHome = true;
            } else if (action.equals(FINISH_ACTION)) {
                // finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isClickHome) {
            gotoUnLockActivity();
            finish();
            isClickHome = false;
        }
    }

    private void gotoUnLockActivity() {
        Intent mIntent;
        int lockType = SpUtil.getInstance().getInt(AppConstants.LOCK_TYPE);
        if (lockType == 0) { //图形
            mIntent = new Intent(LockSettingActivity.this, GestureSelfUnlockActivity.class);
        } else { //数字
            mIntent = new Intent(LockSettingActivity.this, NumberSelfUnlockActivity.class);
        }
        mIntent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_SETTING);
        startActivity(mIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLockSettingReceiver);
    }
}
