package com.tools.security.scanfiles.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.AvlFileInfo;
import com.tools.security.bean.CommonResult;
import com.tools.security.bean.FunctionAd;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.result.CommonResultActivity;
import com.tools.security.scanfiles.adapter.ScanFileDangerAdapter;
import com.tools.security.scanfiles.presenter.ScanFileDangerContract;
import com.tools.security.scanfiles.presenter.ScanFileDangerPresenter;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.widget.DividerItemDecoration;
import com.tools.security.widget.animator.SlideInLeftAnimator;

import java.io.File;
import java.util.ArrayList;

/**
 * description:深度扫描：危险
 * author: xiaodifu
 * date: 2017/1/7.
 */

public class ScanFilesDangerActivity extends BaseActivity implements ScanFileDangerContract.View, ScanFileDangerAdapter.IDelelteCallback, View.OnClickListener {

    private RecyclerView recyclerView;
    private LinearLayout deleteAllLayout;
    private ImageView dangerImg;
    private TextView dangerText;
    private TextView markText;

    private ScanFileDangerAdapter scanFileDangerAdapter;
    private ScanFileDangerPresenter presenter;
    private ArrayList<AvlFileInfo> avlFileInfos;
    private LinearLayoutManager layoutManager;

    private static final int MSG_REMOVE_ALL = 1;
    private Runnable removeRunnable;

    int removeIndex = 0;

    private int totalCount;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_REMOVE_ALL:
                    scanFileDangerAdapter.notifyItemRemoved(removeIndex);
                    removeIndex++;
                    onDelete();
                    break;
            }
        }
    };

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        deleteAllLayout = (LinearLayout) findViewById(R.id.layout_delete_all);
        markText = (TextView) findViewById(R.id.text_mark);
        dangerImg = (ImageView) findViewById(R.id.img_status);
        dangerText = (TextView) findViewById(R.id.text_status);

        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        scanFileDangerAdapter = new ScanFileDangerAdapter(this, this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.divide_line_10dp));
        recyclerView.setAdapter(scanFileDangerAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                float scrollY = getScollYDistance();
                float alpha = 0f;
                if (scrollY > ScreenUtil.dip2px(ScanFilesDangerActivity.this, 150f)) {
                    alpha = 0.0f;
                } else {
                    alpha = 1f - scrollY / ScreenUtil.dip2px(ScanFilesDangerActivity.this, 150f);
                }
                if (layoutManager.findFirstVisibleItemPosition() > 0) alpha = 0.0f;
                dangerImg.setAlpha(alpha);
                dangerText.setAlpha(alpha);
                markText.setAlpha(alpha);

            }
        });
    }

    public int getScollYDistance() {
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }

    private void initData() {
        removeRunnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 200);
                handler.sendEmptyMessage(MSG_REMOVE_ALL);
            }
        };

        deleteAllLayout.setOnClickListener(this);
        presenter = new ScanFileDangerPresenter(this);
        presenter.loadFile();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.red));
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_deep_scan_danger;
    }

    @Override
    public void refreshList(ArrayList<AvlFileInfo> avlFileInfos) {
        if (avlFileInfos == null) return;
        this.avlFileInfos = avlFileInfos;
        totalCount = avlFileInfos.size();
        markText.setText(getString(R.string.problems_found, "" + totalCount));
        scanFileDangerAdapter.setData(avlFileInfos);
    }

    @Override
    public void onDelete() {
        totalCount--;
        markText.setText(getString(R.string.problems_found, "" + totalCount));
        if (totalCount == 0) {
            Bundle bundle = new Bundle();
            CommonResult commonResult = new CommonResult("Safe", getString(R.string.scanned_files, SpUtil.getInstance().getString(AppConstants.SD_CARD_FILE_COUNT)), FunctionAd.SCANLE_FILE, ScreenUtil.dip2px(this, 76f));
            bundle.putSerializable("result", commonResult);
            startActivity(new Intent(this, CommonResultActivity.class).putExtras(bundle));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_delete_all:
                avlFileInfos = scanFileDangerAdapter.getData();
                if (avlFileInfos != null) {
                    for (int i = 0; i < avlFileInfos.size(); i++) {
                        AvlFileInfo avlFileInfo = avlFileInfos.get(i);
                        File file = new File(avlFileInfo.getPath());
                        if (file != null && file.isFile()) file.delete();
                    }
                }
                handler.postDelayed(removeRunnable, 200);
                handler.sendEmptyMessage(MSG_REMOVE_ALL);
                break;
        }
    }
}
