package com.tools.security.mainscan.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.BrowserHistory;
import com.tools.security.mainscan.adapter.BrowserHistoryAdapter;
import com.tools.security.mainscan.presenter.BrowserHistoryContract;
import com.tools.security.mainscan.presenter.BrowserHistoryPresenter;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.ToastUtil;
import com.tools.security.widget.animator.SlideInLeftAnimator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tools.security.common.AppConstants.ACTION_FILTER_CLEAR_BROSWER_HISTORY;
import static com.tools.security.common.AppConstants.BROSWER_HISTORY_COUNT;

/**
 * description:系统浏览器历史记录
 * author: xiaodifu
 * date: 2016/12/19.
 */

public class BrowserHistoryActivity extends BaseActivity implements BrowserHistoryContract.View, View.OnClickListener {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_broswer_history;
    }

    private BrowserHistoryAdapter mBrowserHistoryAdapter;
    private RecyclerView mRecyclerView;
    private TextView mCleanBtn;
    private BrowserHistoryPresenter browserHistoryPresenter;
    private int historyCount = 0;

    @Override
    protected void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mCleanBtn = (TextView) findViewById(R.id.clean_btn);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        browserHistoryPresenter = new BrowserHistoryPresenter(this, this);
        mBrowserHistoryAdapter = new BrowserHistoryAdapter(this);
        mRecyclerView.setAdapter(mBrowserHistoryAdapter);

        showProgress();
        browserHistoryPresenter.loadHistory();

        mCleanBtn.setOnClickListener(this);
    }

    @Override
    public void refreshList(List<BrowserHistory> list) {
        hideProgress();
        historyCount = list.size();
        mBrowserHistoryAdapter.setData(list);
    }

    @Override
    public void clearSuccess() {
        mBrowserHistoryAdapter.cleanHistory();
        SpUtil.getInstance().putInt(BROSWER_HISTORY_COUNT, 0);
        int currentCleanedCount = SpUtil.getInstance().getInt(AppConstants.CLEANED_PRIVACY_COUNT);
        SpUtil.getInstance().putInt(AppConstants.CLEANED_PRIVACY_COUNT, currentCleanedCount + 1);
        sendBroadcast(new Intent(ACTION_FILTER_CLEAR_BROSWER_HISTORY));
        BrowserHistoryActivity.this.finish();
    }

    @Override
    public void clearError() {
        ToastUtil.showShort(getString(R.string.clear_error_tip));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clean_btn:
                Map<String, Object> map = new HashMap<>();
                map.put("count", historyCount);
//               KochavaUtils.tracker(AppConstants.CLICK_BROWSER_PRIVACY_CLEAN, map);
                browserHistoryPresenter.clearHistory();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        browserHistoryPresenter.onDestroy();
    }
}
