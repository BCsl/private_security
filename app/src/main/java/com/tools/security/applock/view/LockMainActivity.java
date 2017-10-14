package com.tools.security.applock.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.applock.adapter.MainLockAdapter;
import com.tools.security.applock.presenter.AppLockContract;
import com.tools.security.applock.presenter.AppLockPresenter;
import com.tools.security.applock.view.unlock.GestureSelfUnlockActivity;
import com.tools.security.applock.view.unlock.NumberSelfUnlockActivity;
import com.tools.security.bean.CommLockInfo;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.TextChangedListenerAdapter;
import com.tools.security.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/1/5.
 * 应用锁主界面
 */

public class LockMainActivity extends BaseActivity implements AppLockContract.View, View.OnClickListener {

    private ImageView mBtnBack, mBtnSearch, mBtnSetting;
    private EditText mBarSearch;
    private TextView mBarTitle;

    private MainLockAdapter mMainLockAdapter;
    private RecyclerView mRecyclerView;

    private AppLockPresenter mPresenter;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout mTopItemLayout;
    private TextView mLockAppType;
    private CheckBox mCheckBoxSys, mCheckBoxApp;
    private String titleSystem, titleApps;

    private LockMainReceiver mLockMainReceiver;
    private List<CommLockInfo> localList;

    public static final String ACTION_CLICK_HOME = "action_click_home";
    private boolean isClickHome = false;
    private InputMethodManager imm;

    //判断页面是否是从底部进入的
    private boolean isFromBottom = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_lock_main;
    }

    @Override
    protected void init() {
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBarSearch = (EditText) findViewById(R.id.bar_search);
        mBtnSearch = (ImageView) findViewById(R.id.btn_search);
        mBarTitle = (TextView) findViewById(R.id.bar_title);
        mBtnSetting = (ImageView) findViewById(R.id.btn_setting);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mTopItemLayout = (LinearLayout) findViewById(R.id.top_item_layout);
        mLockAppType = (TextView) findViewById(R.id.lock_app_type);
        mCheckBoxSys = (CheckBox) findViewById(R.id.checkbox_sys);
        mCheckBoxApp = (CheckBox) findViewById(R.id.checkbox_app);

        mTopItemLayout.setVisibility(View.GONE);
        localList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMainLockAdapter = new MainLockAdapter(this);
        mRecyclerView.setAdapter(mMainLockAdapter);
        mPresenter = new AppLockPresenter(this, this);
        mLockMainReceiver = new LockMainReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS); //home键
        filter.addAction(Intent.ACTION_SCREEN_OFF); //锁屏
        filter.addAction(ACTION_CLICK_HOME); //锁屏
        registerReceiver(mLockMainReceiver, filter);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        showProgress();
        mPresenter.loadAppInfo(this, true);
        mCheckBoxSys.setOnClickListener(this);
        mCheckBoxApp.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);
        mBtnSetting.setOnClickListener(this);

        titleSystem = getString(R.string.lock_system);
        titleApps = getString(R.string.lock_apps);
        mCheckBoxSys.setVisibility(View.VISIBLE);
        mCheckBoxSys.setChecked(SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_SELECT_ALL_SYS, false));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View stickyInfoView = recyclerView.findChildViewUnder(mTopItemLayout.getMeasuredWidth() / 2, 5);
                if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {
                    String title = String.valueOf(stickyInfoView.getContentDescription());
                    mCheckBoxApp.setVisibility(title.equals(titleSystem) ? View.GONE : View.VISIBLE);
                    mCheckBoxSys.setVisibility(title.equals(titleSystem) ? View.VISIBLE : View.GONE);
                    if (mCheckBoxApp.getVisibility() == View.VISIBLE) {
                        boolean isSelectAll = SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_SELECT_ALL_APP, false);
                        if (isSelectAll) {
                            mCheckBoxApp.setChecked(true);
                        } else {
                            mCheckBoxApp.setChecked(false);
                        }
                    }
                    mLockAppType.setText(title);
                }
                View transInfoView = recyclerView.findChildViewUnder(mTopItemLayout.getMeasuredWidth() / 2, mTopItemLayout.getMeasuredHeight() + 1);
                if (transInfoView != null && transInfoView.getTag() != null) {
                    int transViewStatus = (int) transInfoView.getTag();
                    int dealtY = transInfoView.getTop() - mTopItemLayout.getMeasuredHeight();
                    if (transViewStatus == MainLockAdapter.HAS_STICKY_VIEW) {
                        if (transInfoView.getTop() > 0) {
                            mTopItemLayout.setTranslationY(dealtY);
                        } else {
                            mTopItemLayout.setTranslationY(0);
                        }
                    } else if (transViewStatus == MainLockAdapter.NONE_STICKY_VIEW) {
                        mTopItemLayout.setTranslationY(0);
                    }
                }
            }
        });


        mBarSearch.addTextChangedListener(new TextChangedListenerAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (s.length() == 0) {
                    mMainLockAdapter.setLockInfos(localList);
                } else {
                    mPresenter.searchAppInfo(s.toString(), new AppLockPresenter.ISearchResultListener() {
                        @Override
                        public void onSearchResult(List<CommLockInfo> commLockInfos) {
                            mMainLockAdapter.setLockInfos(commLockInfos);
                        }
                    });
                }
            }
        });

        isFromBottom = getIntent().getIntExtra("from", 0) != 0;
    }

    @Override
    public void finish() {
        super.finish();
        if (isFromBottom)overridePendingTransition(R.anim.slide_scale_in,R.anim.slide_bottom_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkbox_sys:
                SpUtil.getInstance().putBoolean(AppConstants.LOCK_IS_SELECT_ALL_SYS, mCheckBoxSys.isChecked());
                mMainLockAdapter.changeSysLockStatus(mCheckBoxSys);
                break;
            case R.id.checkbox_app:
                mMainLockAdapter.changeUserLockStatus(mCheckBoxApp);
                break;
            case R.id.btn_back:
                if (mBarSearch.getVisibility() == View.GONE) {
                    finish();
                } else {
                    mBarSearch.setVisibility(View.GONE);
                    mBtnSearch.setVisibility(View.VISIBLE);
                    mBarTitle.setVisibility(View.VISIBLE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
            case R.id.btn_search:
                mBarSearch.setVisibility(View.VISIBLE);
                mBtnSearch.setVisibility(View.GONE);
                mBarTitle.setVisibility(View.GONE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.btn_setting:
                startActivity(new Intent(LockMainActivity.this, LockSettingActivity.class));
                overridePendingTransition(R.anim.slide_right_in2,R.anim.slide_left_out2);
                break;
        }
    }

    @Override
    public void loadAppInfoSuccess(List<CommLockInfo> list) {
        this.localList = list;
        mTopItemLayout.setVisibility(View.VISIBLE);
        mMainLockAdapter.setLockInfos(list);
        hideProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isClickHome) {
            gotoUnlockActivity();
        }
    }

    private class LockMainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                gotoUnlockActivity();
            } else if (action.equals(ACTION_CLICK_HOME)) {
                finish();
            } else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                isClickHome = true;
            }
        }
    }

    private void gotoUnlockActivity() {
        isClickHome = false;
        Intent mIntent;
        int lockType = SpUtil.getInstance().getInt(AppConstants.LOCK_TYPE);
        if (lockType == 0) { //图形
            mIntent = new Intent(LockMainActivity.this, GestureSelfUnlockActivity.class);
        } else { //数字
            mIntent = new Intent(LockMainActivity.this, NumberSelfUnlockActivity.class);
        }
        mIntent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY);
        startActivity(mIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mBarSearch.getVisibility() == View.VISIBLE) {
            mBarSearch.setVisibility(View.GONE);
            mBtnSearch.setVisibility(View.VISIBLE);
            mBarTitle.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        unregisterReceiver(mLockMainReceiver);
    }
}
