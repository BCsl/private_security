package com.tools.security.wifi.view;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.StringUtil;
import com.tools.security.wifi.adapter.DeviceScanAdapter;
import com.tools.security.wifi.core.devicescan.DeviceScanManager;
import com.tools.security.wifi.core.devicescan.DeviceScanResult;
import com.tools.security.wifi.core.devicescan.IP_MAC;
import com.tools.security.wifi.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class WifiDeviceScanActivity extends BaseActivity implements DeviceScanResult, View.OnClickListener {
    private DeviceScanManager manager;
    private RecyclerView mRecyclerView;
    private DeviceScanAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private RelativeLayout countLayout;
    private TextView speedTestText;
    private TextView countText;
    private List<IP_MAC> mDeviceList = new ArrayList<IP_MAC>();
    private String localIp, gateIp, localMac;
    //判断页面是否是从底部进入的
    private boolean isFromBottom = false;

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initView() {
        countLayout = (RelativeLayout) findViewById(R.id.layout_count);
        countText = (TextView) findViewById(R.id.text_device_count);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview_wifi_device);
        speedTestText = (TextView) findViewById(R.id.text_speed_test);

        localIp = NetworkUtil.getIPAddress(true);
        gateIp = NetworkUtil.getGateWayIp(this);
        localMac = NetworkUtil.getLocalMac(this);
        mAdapter = new DeviceScanAdapter(this, localIp, gateIp);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findFirstVisibleItemPosition() >= mAdapter.getCountViewPosition()) {
                    if (layoutManager.findFirstVisibleItemPosition() == 0) return;
                    countLayout.setVisibility(View.VISIBLE);
                } else {
                    countLayout.setVisibility(View.GONE);
                }
            }
        });

        speedTestText.setOnClickListener(this);
    }

    private void initData() {
        isFromBottom = getIntent().getIntExtra("from", 0) == 0 ? false : true;

        manager = new DeviceScanManager();
        manager.startScan(getApplicationContext(), this);

        IP_MAC myself = new IP_MAC(localIp, localMac);
        myself.mManufacture = Build.MANUFACTURER;
        myself.mDeviceName = Build.MODEL;
        mDeviceList.add(myself);
        mAdapter.setData(mDeviceList);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_wifi_device_scan;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (manager != null) {
            manager.stopScan();
            manager = null;
        }
        mDeviceList = null;
    }

    @Override
    protected void onHomeClick() {
        super.onHomeClick();
        if (isFromBottom) overridePendingTransition(R.anim.slide_scale_in,R.anim.slide_bottom_out);
    }

    @Override
    public void deviceScanResult(IP_MAC ip_mac) {
        if (!mDeviceList.contains(ip_mac)) {
            mDeviceList.add(ip_mac);
            mAdapter.setData(StringUtil.sortIpMac(mDeviceList, localIp, gateIp));
            countText.setText(Html.fromHtml(getString(R.string.wifi_devices_count, "" + mDeviceList.size())));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_speed_test:
                startActivity(new Intent(WifiDeviceScanActivity.this, WifiScanningActivity.class));
                finish();
                break;
        }
    }
}
