package com.tools.security.wifi.view;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.WifiProblem;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.NetworkUtil;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.wifi.adapter.WifiProblemAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * description:wifi扫描有问题结果页
 * author: xiaodifu
 * date: 2017/1/17.
 */

public class WifiProblemResultActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private WifiProblemAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private TextView problemsText;
    private TextView descText;
    private LinearLayout headerLayout;
    private TextView changeWifiText;

    private List<WifiProblem> resultList;

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initData() {
        if (NetworkUtil.isConnected(this)) {
            resultList = DataSupport.findAll(WifiProblem.class);
            descText.setText(getIntent().getStringExtra("wifi")+" Wifi.Messages might be leaked.");
        } else {
            resultList = new ArrayList<>();
            resultList.add(new WifiProblem(0, getString(R.string.wi_fi_is_connected), WifiProblem.TYPE_CONNECT));
            descText.setText("Please open your wifi.");
        }
        adapter.setData(resultList);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.orange));
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        problemsText = (TextView) findViewById(R.id.text_problems);
        descText = (TextView) findViewById(R.id.text_desc);
        headerLayout = (LinearLayout) findViewById(R.id.layout_wifi_problem);
        changeWifiText = (TextView) findViewById(R.id.text_change_wifi);

        adapter = new WifiProblemAdapter();
        recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                super.onScrolled(recyclerView, dx, dy);
                float scrollY = getScollYDistance();
                float alpha = 0f;
                if (scrollY > ScreenUtil.dip2px(WifiProblemResultActivity.this, 126f)) {
                    alpha = 0.0f;
                } else {
                    alpha = 1f - scrollY / ScreenUtil.dip2px(WifiProblemResultActivity.this, 126f);
                }
                if (linearLayoutManager.findFirstVisibleItemPosition() > 0) alpha = 0.0f;
                problemsText.setAlpha(alpha);
                descText.setAlpha(alpha);
            }
        });
        changeWifiText.setOnClickListener(this);
    }

    public int getScollYDistance() {
        int position = linearLayoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = linearLayoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_wifi_problem_result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_change_wifi:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                finish();
                break;
        }
    }
}
