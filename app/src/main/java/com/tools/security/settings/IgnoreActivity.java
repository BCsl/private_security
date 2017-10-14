package com.tools.security.settings;

import android.content.ContentValues;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.tools.security.R;
import com.tools.security.bean.config.AppConfig;
import com.tools.security.bean.AppWhitePaper;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.SafeLevel;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.settings.adapter.IgnoreAdapter;
import com.tools.security.settings.presenter.IgnoreContract;
import com.tools.security.settings.presenter.IgnorePresenter;
import com.tools.security.utils.SpUtil;
import com.tools.security.widget.animator.SlideInLeftAnimator;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * description:白名单
 * author: xiaodifu
 * date: 2016/12/13.
 */

public class IgnoreActivity extends BaseActivity implements IgnoreContract.View, IgnoreAdapter.OnDelCallback {

    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private IgnorePresenter presenter;
    private IgnoreAdapter adapter;
    private ArrayList<AppWhitePaper> list;
    private AppConfig appConfig;

    //判断页面是否是从右侧进入的
    private boolean isFromRight = false;

    @Override
    protected void init() {
        initView();
        initData();
        presenter = new IgnorePresenter(this);
        presenter.loadData(this);
    }

    private void initData() {
        appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
        isFromRight = getIntent().getIntExtra("from", 0) == 0 ? false : true;
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_ignore);
        emptyView = (LinearLayout) findViewById(R.id.text_empty_ignore);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        adapter = new IgnoreAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_ignore;
    }

    @Override
    public void refresh(List<AppWhitePaper> list) {
        if (list != null && list.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            this.list = (ArrayList<AppWhitePaper>) list;
            adapter.setData(list);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (isFromRight) overridePendingTransition(R.anim.slide_left_in2, R.anim.slide_right_out2);
    }

    @Override
    public void onDel(int position) {
        AppWhitePaper whitePaper = list.get(position);
        AvlAppInfo avlAppInfo = DataSupport.find(AvlAppInfo.class, whitePaper.getAvlId());
        whitePaper.delete();
        if (avlAppInfo != null) {
            avlAppInfo.setIgnored(0);
            avlAppInfo.update(avlAppInfo.getId());
        } else {
            new AvlAppInfo(whitePaper.getResult(), whitePaper.getVirusName(), whitePaper.getPkgName(), whitePaper.getSampleName(), "", 0).save();
        }

        list.remove(position);
        adapter.setData(list);
        if (appConfig.getSafeLevel() != SafeLevel.DANGER) {
            appConfig.setSafeLevel(SafeLevel.DANGER);
        }
        appConfig.setProblemCount(appConfig.getProblemCount() + 1);
        SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
    }

    @Override
    protected void onHomeClick() {
        SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
        super.onHomeClick();
    }

    @Override
    protected void onDestroy() {
        presenter = null;
        adapter = null;
        list = null;
        appConfig = null;
        super.onDestroy();
    }
}
