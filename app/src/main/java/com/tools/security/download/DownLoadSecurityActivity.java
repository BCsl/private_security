package com.tools.security.download;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.DownloadFile;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.download.presenter.DownloadContract;
import com.tools.security.download.presenter.DownloadPresenter;
import com.tools.security.utils.ToastUtil;
import com.tools.security.widget.popupwindow.DownloadMenuPopwindow;

import java.util.List;

/**
 * description:下载安全主界面
 * author: xiaodifu
 * date: 2017/1/6.
 */

public class DownLoadSecurityActivity extends BaseActivity implements View.OnClickListener, DownloadContract.View {

    private DownloadMenuPopwindow menuPopwindow;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ImageView backImg;
    private ImageView menuImg;
    private DownloadAdapter adapter;
    private DownloadContract.Presenter presenter;
    private List<DownloadFile> fileArrayList;
    private static final int STORAGE_REQUEST_CODE = 1;
    private BroadcastReceiver receiver;

    @Override
    protected void init() {
        initView();
        requestPermission(this);
    }

    private void initView() {
        menuPopwindow = new DownloadMenuPopwindow(this, this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_download);
        emptyView = (TextView) findViewById(R.id.text_empty);
        backImg = (ImageView) findViewById(R.id.img_back);
        menuImg = (ImageView) findViewById(R.id.img_menu);

        adapter = new DownloadAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        backImg.setOnClickListener(this);
        menuImg.setOnClickListener(this);
    }

    private void initReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(AppConstants.UPDATE_DOWNLOAD_SECURITY_LIST);

        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showProgress();
                presenter.loadData();
            }
        };

        registerReceiver(receiver,intentFilter);
    }

    private void start(){
        initReceiver();
        presenter = new DownloadPresenter(this, this);
        showProgress();
        presenter.loadData();
    }

    private void requestPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
            } else {
                start();
            }
        } else {
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                start();
            } else {
                ToastUtil.showShort("Permissions Denied");
                finish();
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_download_security;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onHomeClick();
                break;
            case R.id.img_menu:
                menuPopwindow.showAsDropDown(menuImg);
                break;
        }
    }

    @Override
    public void refreshData(List<DownloadFile> pathList) {
        this.fileArrayList = pathList;
        if (fileArrayList == null || fileArrayList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return;
        }
        hideProgress();
        adapter.setData(fileArrayList);
    }

    @Override
    protected void onHomeClick() {
        super.onHomeClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileArrayList=null;
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
    }
}
