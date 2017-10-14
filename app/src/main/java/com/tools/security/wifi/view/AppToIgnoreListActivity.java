package com.tools.security.wifi.view;

import android.content.pm.PackageInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tools.security.R;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.ScannedAppCacheUtil;
import com.tools.security.wifi.adapter.AddIgnoreAppAdapter;
import com.tools.security.wifi.presenter.IgnoreContract;
import com.tools.security.wifi.presenter.IgnorePresenter;

import java.util.List;

/**
 * description:wifi：本地应用列表
 * author: xiaodifu
 * date: 2017/1/16.
 */

public class AppToIgnoreListActivity extends BaseActivity implements IgnoreContract.View {

    private RecyclerView recyclerView;
    private AddIgnoreAppAdapter addIgnoreAppAdapter;
    private ScannedAppCacheUtil appCacheUtil;
    private IgnorePresenter presenter;

    @Override
    protected void init() {
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addIgnoreAppAdapter=new AddIgnoreAppAdapter(this,appCacheUtil=new ScannedAppCacheUtil());
        recyclerView.setAdapter(addIgnoreAppAdapter);

        presenter=new IgnorePresenter(this,this);
        presenter.getAppInfo();
        showProgress();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_app_list;
    }

    @Override
    public void refreshList(List<PackageInfo> appInfos) {
        hideProgress();
        addIgnoreAppAdapter.setAppInfoList(appInfos);
    }
}
