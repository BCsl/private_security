package com.tools.security.applock.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.applock.adapter.LockSuccessAdapter;
import com.tools.security.applock.presenter.AppLockContract;
import com.tools.security.applock.presenter.AppLockPresenter;
import com.tools.security.bean.CommLockInfo;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.helper.HeaderViewRecyclerAdapter;

import java.util.List;

/**
 * 加锁成功页面
 * Created by lzx on 2017/1/23.
 */

public class LockSuccessActivity extends BaseActivity implements AppLockContract.View {

    private RecyclerView mRecyclerView;
    private TextView mBtnDone;
    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;
    private LockSuccessAdapter mLockSuccessAdapter;
    private AppLockContract.Presenter mPresenter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_lock_success;
    }

    @Override
    protected void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mBtnDone = (TextView) findViewById(R.id.btn_done);

        mPresenter = new AppLockPresenter(this, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLockSuccessAdapter = new LockSuccessAdapter(this);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mLockSuccessAdapter);
        View headView = LayoutInflater.from(this).inflate(R.layout.layout_lock_success, mRecyclerView, false);
        mHeaderViewRecyclerAdapter.addHeaderView(headView);
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        showProgress();
        mPresenter.loadLockAppInfo(this);
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LockSuccessActivity.this, LockMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void loadAppInfoSuccess(List<CommLockInfo> list) {
        hideProgress();
        mLockSuccessAdapter.setList(list);
    }


}
