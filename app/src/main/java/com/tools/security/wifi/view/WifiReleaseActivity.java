package com.tools.security.wifi.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.CommonResult;
import com.tools.security.bean.FunctionAd;
import com.tools.security.bean.WifiReleaseApp;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.SecurityApplication;
import com.tools.security.common.result.CommonResultActivity;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SystemUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.widget.dialog.WifiPermissionsDialog;
import com.tools.security.wifi.adapter.ReleaseAdapter;
import com.tools.security.wifi.presenter.WifiReleaseContract;
import com.tools.security.wifi.presenter.WifiReleasePresenter;

import java.util.ArrayList;

/**
 * description:释放带宽
 * author: xiaodifu
 * date: 2017/1/13.
 */

public class WifiReleaseActivity extends BaseActivity implements WifiReleaseContract.View, View.OnClickListener, ReleaseAdapter.ICheckCallback {

    private WifiReleasePresenter presenter;
    private RecyclerView flowRecyclerview;
    private TextView releaseText;
    private ReleaseAdapter adapter;
    private ImageView slowlyImg;
    private TextView countText;
    private LinearLayoutManager linearLayoutManager;
    private int checkedCount = 0;
    private BroadcastReceiver receiver;
    //判断页面是否是从底部进入的
    private boolean isFromBottom = false;

    @Override
    protected void init() {
        initView();
        initReceiver();
        initData();
    }

    private void initView() {
        flowRecyclerview = (RecyclerView) findViewById(R.id.recycler_view_flow);
        releaseText = (TextView) findViewById(R.id.text_release);
        slowlyImg = (ImageView) findViewById(R.id.img_slowly);
        countText = (TextView) findViewById(R.id.text_release_count);

        adapter = new ReleaseAdapter(this, this);
        flowRecyclerview.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this));
        flowRecyclerview.setAdapter(adapter);
        releaseText.setOnClickListener(this);

        flowRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                float scrollY = getScollYDistance();
                float alpha = 0f;
                if (scrollY > ScreenUtil.dip2px(WifiReleaseActivity.this, 150f)) {
                    alpha = 0.0f;
                } else {
                    alpha = 1f - scrollY / ScreenUtil.dip2px(WifiReleaseActivity.this, 150f);
                }
                if (linearLayoutManager.findFirstVisibleItemPosition() > 0) alpha = 0.0f;
                slowlyImg.setAlpha(alpha);
                countText.setAlpha(alpha);
            }
        });
    }

    private void initReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<WifiReleaseApp> list = adapter.getData();
                if (list == null || list.size() == 0) return;
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list", list);
                startActivity(new Intent(WifiReleaseActivity.this, WifiReleasingActivity.class).putExtras(bundle));
                WifiReleaseActivity.this.finish();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.ACCESSIBILITY_SERVICE_CONNECTED);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public int getScollYDistance() {
        int position = linearLayoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = linearLayoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }

    private void initData() {
        isFromBottom = getIntent().getIntExtra("from", 0) == 0 ? false : true;

        presenter = new WifiReleasePresenter(this, this);
        showProgress();
        releaseText.setEnabled(false);
        presenter.loadAppFlow();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.orange));
    }

    @Override
    public void finish() {
        super.finish();
        if (isFromBottom) overridePendingTransition(R.anim.slide_scale_in,R.anim.slide_bottom_out);
        presenter.onDestory();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_wifi_release;
    }

    @Override
    public void refresh(ArrayList<WifiReleaseApp> appList) {
        hideProgress();
        releaseText.setEnabled(true);
        if (appList == null || appList.size() == 0) {
            Bundle bundle = new Bundle();
            CommonResult commonResult = new CommonResult(getString(R.string.excellent), getString(R.string.all_threat_resolved), FunctionAd.RELEASING, ScreenUtil.dip2px(WifiReleaseActivity.this, 76f));
            bundle.putSerializable("result", commonResult);
            startActivity(new Intent(WifiReleaseActivity.this, CommonResultActivity.class).putExtras(bundle));
            WifiReleaseActivity.this.finish();
        } else {
            checkedCount = appList.size();
            countText.setText(getString(R.string.release_bandwidth_count, "" + appList.size()));
            adapter.setData(appList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_release:
                if (SystemUtil.isAccessibilitySettingsOn(SecurityApplication.getInstance())) {
                    ArrayList<WifiReleaseApp> list = adapter.getData();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("list", list);
                    startActivity(new Intent(WifiReleaseActivity.this, WifiReleasingActivity.class).putExtras(bundle));
                    finish();
                } else {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    new WifiPermissionsDialog(this).show();
                }
                break;
        }
    }

    @Override
    public void onChecked(boolean checked) {
        if (checked) {
            checkedCount++;
        } else {
            checkedCount--;
        }
        countText.setText(getString(R.string.release_bandwidth_count, "" + checkedCount));
        if (checkedCount == 0) {
            releaseText.setEnabled(false);
            releaseText.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn_blue_disable));
        } else {
            releaseText.setEnabled(true);
            releaseText.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn_blue));
        }
    }
}
