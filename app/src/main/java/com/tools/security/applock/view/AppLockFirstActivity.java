package com.tools.security.applock.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.applock.adapter.LockFirstAdapter;
import com.tools.security.applock.db.CommLockInfoManager;
import com.tools.security.applock.presenter.AppLockContract;
import com.tools.security.applock.presenter.AppLockPresenter;
import com.tools.security.applock.view.lock.FirstCreateActivity;
import com.tools.security.bean.CommLockInfo;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.TextChangedListenerAdapter;
import com.tools.security.common.helper.HeaderViewRecyclerAdapter;
import com.tools.security.service.LockAppLoadService;
import com.tools.security.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 首次进入锁屏界面
 * Created by lzx on 2017/1/6.
 */

public class AppLockFirstActivity extends BaseActivity implements View.OnClickListener, AppLockContract.View {

    private ImageView mBtnBack, mBtnSearch;
    private EditText mBarSearch;
    private TextView mBarTitle;
    private RecyclerView mRecyclerView;
    private TextView mLockBtn;
    private LockFirstAdapter mLockFirstAdapter;
    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;
    private AppLockPresenter mPresenter;
    private int index = 0;
    private int lockType;
    private CommLockInfoManager mLockInfoManager;
    private TextView mLockHeaderTip;
    private SpannableStringBuilder mStringBuilder;
    private ForegroundColorSpan mColorSpan;
    private AppLockFirstReceiver mReceiver;
    public static final String ACTION_FINISH = "action_finish";
    private ArrayList<CommLockInfo> mLockList; //保存的加锁应用
    private ArrayList<CommLockInfo> mUnLockList; //保存的没加锁应用

    private List<CommLockInfo> localList;
    private InputMethodManager imm;

    //判断页面是否是从底部进入的
    private boolean isFromBottom = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_lock_first;
    }

    @Override
    protected void init() {
        isFromBottom = getIntent().getIntExtra("from", 0) != 0;

        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBarSearch = (EditText) findViewById(R.id.bar_search);
        mBtnSearch = (ImageView) findViewById(R.id.btn_search);
        mBarTitle = (TextView) findViewById(R.id.bar_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLockBtn = (TextView) findViewById(R.id.lock_btn);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LockAppLoadService.ACTION_LOAD_APP_SUCCESS);
        filter.addAction(ACTION_FINISH);
        mReceiver = new AppLockFirstReceiver();
        registerReceiver(mReceiver, filter);

        mLockBtn.setOnClickListener(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mLockList = new ArrayList<>();
        mUnLockList = new ArrayList<>();
        localList = new ArrayList<>();
        mColorSpan = new ForegroundColorSpan(Color.RED);
        mLockInfoManager = new CommLockInfoManager(this);
        mPresenter = new AppLockPresenter(this, this);
        mLockFirstAdapter = new LockFirstAdapter(this);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mLockFirstAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        View headView = LayoutInflater.from(this).inflate(R.layout.layout_lock_first_header, mRecyclerView, false);
        mLockHeaderTip = (TextView) headView.findViewById(R.id.lock_list_tip);
        mHeaderViewRecyclerAdapter.addHeaderView(headView);
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);

        mBarTitle.setText(R.string.app_lock_title);

        //锁的方式（数字还是图案）
        lockType = SpUtil.getInstance().getInt(AppConstants.LOCK_TYPE);

        mLockFirstAdapter.setOnItemClickListener(new LockFirstAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(CommLockInfo info, int position) {
                if (info.isLocked()) {
                    info.setLocked(false);
                    index--;
                    if (index == 0) {
                        mLockBtn.setEnabled(false);
                    }
                    mLockList.remove(info);
                    mUnLockList.add(info);
                    // mLockInfoManager.unlockCommApplication(info.getPackageName()); //更改数据库锁状态
                } else {
                    info.setLocked(true);
                    index++;
                    if (!mLockBtn.isEnabled()) {
                        mLockBtn.setEnabled(true);
                    }
                    mLockList.add(info);
                    mUnLockList.remove(info);
                    //mLockInfoManager.lockCommApplication(info.getPackageName());
                }
                setHeaderTipString(index);
                mLockFirstAdapter.notifyItemChanged(position);
            }
        });

        mBtnBack.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);
        mBarSearch.addTextChangedListener(new TextChangedListenerAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (s.length() == 0) {
                    mLockFirstAdapter.setLockInfos(localList);
                } else {
                    mPresenter.searchAppInfo(s.toString(), new AppLockPresenter.ISearchResultListener() {
                        @Override
                        public void onSearchResult(List<CommLockInfo> commLockInfos) {
                            mLockFirstAdapter.setLockInfos(commLockInfos);
                        }
                    });
                }
            }
        });

        showProgress();

        //加载应用列表
        mPresenter.loadAppInfo(AppLockFirstActivity.this, false);
    }

    private void setHeaderTipString(int index) {
        String num = String.valueOf(index);
        mStringBuilder = new SpannableStringBuilder(num + " " + getString(R.string.lock_recommend_tip1));
        mStringBuilder.setSpan(mColorSpan, 0, num.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mLockHeaderTip.setText(mStringBuilder);

        String format = getResources().getString(R.string.lock_first_btn);
        String str = String.format(format, num);
        mLockBtn.setText(str);
    }

    @Override
    protected void onHomeClick() {
        super.onHomeClick();
        if (isFromBottom) overridePendingTransition(R.anim.slide_scale_in,R.anim.slide_bottom_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lock_btn:
                if (mLockFirstAdapter.getItemCount() != 0) {
                    Intent intent = new Intent(this, FirstCreateActivity.class);
                    intent.putParcelableArrayListExtra("lock_list", mLockList);
                    intent.putParcelableArrayListExtra("unlock_list", mUnLockList);
                    startActivity(intent);
                }
                break;
            case R.id.btn_back:
                if (mBarSearch.getVisibility() == View.GONE) {
                    finish();
                } else {
                    mBarSearch.setVisibility(View.GONE);
                    mBtnSearch.setVisibility(View.VISIBLE);
                    mBarTitle.setVisibility(View.VISIBLE);
                }
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.btn_search:
                mBarSearch.setVisibility(View.VISIBLE);
                mBtnSearch.setVisibility(View.GONE);
                mBarTitle.setVisibility(View.GONE);

                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
        }
    }

    @Override
    public void loadAppInfoSuccess(List<CommLockInfo> list) {
        this.localList = list;
        int index = SpUtil.getInstance().getInt(AppConstants.LOCK_FAVITER_NUM);
        this.index = index;
        setHeaderTipString(index);
        mLockFirstAdapter.setLockInfos(list);
        for (CommLockInfo commLockInfo : list) {
            if (commLockInfo.isLocked()) {
                mLockList.add(commLockInfo);
            }
        }
        hideProgress();
        if (list.size() == 0) {
            mPresenter.loadAppInfo(AppLockFirstActivity.this, false);
        }
    }

    private class AppLockFirstReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(LockAppLoadService.ACTION_LOAD_APP_SUCCESS)) {

            } else if (action.equals(ACTION_FINISH)) {
                finish();
            }
        }
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
        unregisterReceiver(mReceiver);
    }
}
