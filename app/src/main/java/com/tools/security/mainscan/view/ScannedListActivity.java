package com.tools.security.mainscan.view;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.ListView;

import com.tools.security.R;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.mainscan.adapter.ScannedAppAdapter;
import com.tools.security.mainscan.presenter.ScannedListContract;
import com.tools.security.mainscan.presenter.ScannedListPresenter;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.ScannedAppCacheUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.widget.DividerItemDecoration;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * description:已扫描的应用列表
 * author: xiaodifu
 * date: 2016/12/13.
 */

public class ScannedListActivity extends BaseActivity implements ScannedListContract.View {

    private RecyclerView recyclerView;
    private ScannedAppAdapter adapter;
    private ScannedListPresenter presenter;

    private ScannedAppCacheUtil scannedAppCacheUtil;

    @Override
    protected void init() {
        initView();
        presenter = new ScannedListPresenter(this);
        presenter.getAppInfo();
        showProgress();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.list_scanned);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.divide_line_normal));
        adapter = new ScannedAppAdapter(this, scannedAppCacheUtil = new ScannedAppCacheUtil());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_scaned_list;
    }

    @Override
    public void refreshList(List<AvlAppInfo> infos) {
        onTitleChanged("App Scanned(" + DataSupport.where("ignored = ?", "0").count(AvlAppInfo.class) + ")", -1);

        adapter.setAppInfoList(infos);
        hideProgress();
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.onDestory();
            presenter = null;
        }
        if (scannedAppCacheUtil != null) {
            scannedAppCacheUtil.clearCache();
            scannedAppCacheUtil = null;
        }
        adapter = null;
        super.onDestroy();
    }


}
