package com.tools.security.mainscan.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avl.engine.AVLEngine;
import com.tools.security.R;
import com.tools.security.bean.CommonResult;
import com.tools.security.bean.FunctionAd;
import com.tools.security.bean.config.AppConfig;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.FileCacheGroup;
import com.tools.security.bean.ResultInfo;
import com.tools.security.bean.ResultType;
import com.tools.security.bean.SafeLevel;
import com.tools.security.common.result.CommonResultActivity;
import com.tools.security.mainscan.adapter.ScanResultAdapter;
import com.tools.security.mainscan.presenter.ScanResultConract;
import com.tools.security.mainscan.presenter.ScanResultPresenter;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.KochavaUtils;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.StringUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.widget.DividerItemDecoration;
import com.tools.security.widget.animator.SlideInLeftAnimator;
import com.tools.security.widget.popupwindow.ScanResultMenuPopWindow;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.tools.security.bean.SafeLevel.DANGER;
import static com.tools.security.bean.SafeLevel.SAFE;

/**
 * description:云查杀结果页面
 * author: xiaodifu
 * date: 2016/12/13.
 */

public class MainScanResultActivity extends BaseActivity implements View.OnClickListener, ScanResultConract.View {

    private ImageView backImg;
    private TextView doneText;
    private RecyclerView resultRecyclerview;
    private int problemCount;
    private SafeLevel safeLevel = SafeLevel.SAFE;
    private ArrayList<AvlAppInfo> dangerList = new ArrayList<>();
    private int broswerHistoryCount;
    private ArrayList<ResultInfo> resultInfos = new ArrayList<>();
    private ScanResultAdapter adapter;
    private BroadcastReceiver receiver;
    private double junkFileSize;
    private AppConfig appConfig;
    private FileCacheGroup logFileCacheGroup;
    private FileCacheGroup apkFileCacheGroup;
    private FileCacheGroup systempFileCacheGroup;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayoutManager linearLayoutManager;
    private Toolbar toolbar;

    private ImageView statusImg;
    private TextView statusText;
    private TextView markText;
    private boolean needChangeSafeMark = false;

    private ScanResultPresenter presenter;

    private ImageView mBtnMenu;
    private ScanResultMenuPopWindow mMenuPopWindow;

    private LinearLayout mLinearDone;
    private ValueAnimator animAppBar;
    private boolean isChangeStatusImgAlpha = false;

    private static final int MSG_FINISH = 1;

    private boolean isFinished = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_FINISH:
                    if (!isFinished) {
                        isFinished = true;
                        Bundle bundle = new Bundle();
                        CommonResult commonResult = new CommonResult("Safe", getString(R.string.all_threat_resolved), FunctionAd.VIRUS, ScreenUtil.dip2px(MainScanResultActivity.this, 76f));
                        bundle.putSerializable("result", commonResult);
                        startActivity(new Intent(MainScanResultActivity.this, CommonResultActivity.class).putExtras(bundle));
                        onHomeClick();
                    }
                    break;
            }
        }
    };

    @Override
    protected void init() {
        initView();
        initAppBarAnim();
        firstInitData();
        refreshData(true);
        initReceiver();
        presenter = new ScanResultPresenter(MainScanResultActivity.this, MainScanResultActivity.this);
    }

    @SuppressLint("NewApi")
    private void initView() {
        mBtnMenu = (ImageView) findViewById(R.id.btn_menu);
        doneText = (TextView) findViewById(R.id.text_done);
        backImg = (ImageView) findViewById(R.id.img_back);
        resultRecyclerview = (RecyclerView) findViewById(R.id.recycler_view_result);
        resultRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.drawable.divide_line_10dp));
        statusImg = (ImageView) findViewById(R.id.img_status);
        statusText = (TextView) findViewById(R.id.text_status);
        markText = (TextView) findViewById(R.id.text_mark);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_scan_result);
        mLinearDone = (LinearLayout) findViewById(R.id.linear_done);

        resultRecyclerview.setItemAnimator(new SlideInLeftAnimator());
        adapter = new ScanResultAdapter(this);
        resultRecyclerview.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this));
        resultRecyclerview.setAdapter(adapter);
        resultRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstVisiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    int lastVisiblePostion = linearLayoutManager.findLastVisibleItemPosition();

                    if (firstVisiblePosition == 0 && lastVisiblePostion + 1 != linearLayoutManager.getChildCount()) {
                        appBarLayout.setExpanded(true, true);
                    }
                }
            }
        });

        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));

        backImg.setOnClickListener(this);
        doneText.setOnClickListener(this);
        mBtnMenu.setOnClickListener(this);
        setSupportActionBar(toolbar);

        mMenuPopWindow = new ScanResultMenuPopWindow(this);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (getSupportActionBar().getHeight() - appBarLayout.getHeight() == verticalOffset) {
                    setTitle();
                } else {
                    collapsingToolbarLayout.setTitle("");
                }
                if (isChangeStatusImgAlpha) {
                    float alpha = 1 - Math.abs((float) verticalOffset / (appBarLayout.getHeight() - getSupportActionBar().getHeight()));
                    statusImg.setAlpha(alpha);
                    statusText.setAlpha(alpha);
                    markText.setAlpha(alpha);
                }
            }
        });

        statusImg.setAlpha(0f);
        statusText.setAlpha(0f);
        markText.setAlpha(0f);
    }

    /**
     * AppBarLayout动画
     */
    private void initAppBarAnim() {
        final int phoneHeight = ScreenUtil.getPhoneHeight(MainScanResultActivity.this);
        final int tranY = phoneHeight - AppUtils.dip2px(MainScanResultActivity.this, 230);
        final CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        animAppBar = ValueAnimator.ofInt(0, tranY);
        animAppBar.setInterpolator(new AccelerateInterpolator());
        animAppBar.setDuration(350).start();
        animAppBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                params.height = phoneHeight - value;
                appBarLayout.setLayoutParams(params);
                if (value > AppUtils.dip2px(MainScanResultActivity.this, 80)) {
                    if (mLinearDone.getVisibility() == View.GONE)
                        mLinearDone.setVisibility(View.VISIBLE);
                }
            }
        });
        animAppBar.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(statusImg, "alpha", 0.3f, 1.0f);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(statusText, "alpha", 0.3f, 1.0f);
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(markText, "alpha", 0.3f, 1.0f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animator1, animator2, animator3);
                animatorSet.setDuration(500);
                animatorSet.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        isChangeStatusImgAlpha = true;
                    }
                });
                startDoneTextAnim();
            }
        });
    }

    //初始化数据
    private void firstInitData() {
        appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
        dangerList = (ArrayList<AvlAppInfo>) DataSupport.where("ignored = ? and result = ?", "0", "1").find(AvlAppInfo.class);

        broswerHistoryCount = SpUtil.getInstance().getInt(AppConstants.BROSWER_HISTORY_COUNT);
    }

    //刷新数据
    private void refreshData(boolean isFirst) {
        if (isFirst) {
            logFileCacheGroup = SpUtil.getInstance().getBean(AppConstants.FILE_CACHE_GROUP_LOG, FileCacheGroup.class);
            apkFileCacheGroup = SpUtil.getInstance().getBean(AppConstants.FILE_CACHE_GROUP_APK, FileCacheGroup.class);
            systempFileCacheGroup = SpUtil.getInstance().getBean(AppConstants.FILE_CACHE_GROUP_SYS_TEMP, FileCacheGroup.class);
            double logSize = (logFileCacheGroup == null ? 0 : logFileCacheGroup.getTotalCacheSize());
            double apkSize = (apkFileCacheGroup == null ? 0 : apkFileCacheGroup.getTotalCacheSize());
            double tempSize = (systempFileCacheGroup == null ? 0 : systempFileCacheGroup.getTotalCacheSize());

            double adSize = Double.parseDouble(SpUtil.getInstance().getString(AppConstants.AD_JUNK_SIZE, "0"));
            junkFileSize = logSize + apkSize + tempSize + adSize;
        }
        if (dangerList != null && dangerList.size() > 0) {
            safeLevel = DANGER;
        } else if (broswerHistoryCount > 0 || junkFileSize > 0d) {
            safeLevel = SafeLevel.SUSPICIOUS;
        } else {
            safeLevel = SafeLevel.SAFE;
        }
        problemCount = (dangerList == null ? 0 : dangerList.size()) + (broswerHistoryCount > 0 ? 1 : 0) + (junkFileSize > 0d ? 1 : 0);

        appConfig.setSafeLevel(safeLevel);
        appConfig.setScanned(true);
        appConfig.setProblemCount(problemCount);
        SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
        saveData();
        if (safeLevel==SAFE) return;
        resultInfos = new ArrayList<>();
        switch (safeLevel) {
            case SAFE:
//                StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.primary));
                if (isFirst)
                    resultInfos.add(new ResultInfo(ResultType.NORMAL, null, getResources().getDrawable(R.drawable.ic_scan_result), getString(R.string.malware_scan), DataSupport.where("ignored = ?", "0").count(AvlAppInfo.class) + "", "" + 0));
                doneText.setTextAppearance(this, R.style.TextView_BlueStroke);
                doneText.setBackgroundResource(R.drawable.bg_result_category);
                doneText.setTextColor(getResources().getColor(R.color.bg_blue_normal));
                doneText.setText(R.string.done);

                break;
            case SUSPICIOUS:
//                StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.orange));
                if (isFirst && broswerHistoryCount > 0) {
                    resultInfos.add(new ResultInfo(ResultType.BROWSER, null,
                            getResources().getDrawable(R.drawable.ic_broswer), getString(R.string.browser_privacy), getString(R.string.personal_browser_history), "" + broswerHistoryCount));
                }
                if (isFirst && junkFileSize > 0f) {
                    String junkSizeStr = StringUtil.getFormatSize(junkFileSize);
                    resultInfos.add(new ResultInfo(ResultType.JUNK, null, getResources().getDrawable(R.drawable.ic_junk), getString(R.string.junk), getString(R.string.junk_scanned), junkSizeStr));
                }
                doneText.setBackgroundResource(R.drawable.bg_btn_blue);
                doneText.setTextColor(getResources().getColor(R.color.white));
                doneText.setText(R.string.clean_cap);
                break;
            case DANGER:
                if (isFirst && broswerHistoryCount > 0) {
                    resultInfos.add(new ResultInfo(ResultType.BROWSER, null, getResources().getDrawable(R.drawable.ic_broswer),
                            getString(R.string.browser_privacy), getString(R.string.personal_browser_history), "" + broswerHistoryCount));
                }
                if (isFirst && junkFileSize > 0f) {
                    String junkSizeStr = StringUtil.getFormatSize(junkFileSize);
                    resultInfos.add(new ResultInfo(ResultType.JUNK, null, getResources().getDrawable(R.drawable.ic_junk), getString(R.string.junk),
                            getString(R.string.junk_scanned), junkSizeStr));
                }
                doneText.setBackgroundResource(R.drawable.bg_btn_blue);
                doneText.setTextColor(getResources().getColor(R.color.white));
                doneText.setText(R.string.resolve_all);
                break;
        }

        setHead();
        if (isFirst) {
            adapter.setData(resultInfos, dangerList);
        }
    }

    //保存数据
    private void saveData() {
        SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
        SpUtil.getInstance().putInt(AppConstants.BROSWER_HISTORY_COUNT, broswerHistoryCount);
        SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_LOG, logFileCacheGroup);
        SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_APK, apkFileCacheGroup);
        SpUtil.getInstance().putBean(AppConstants.FILE_CACHE_GROUP_SYS_TEMP, systempFileCacheGroup);

        if (safeLevel == SAFE) {
            handler.sendEmptyMessage(MSG_FINISH);
        }
    }

    //设置清理按钮进来的动画
    private void startDoneTextAnim() {

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mLinearDone, "alpha", 0.0f, 1.0f);
        objectAnimator1.setDuration(320);

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mLinearDone, "translationX", ScreenUtil.getPhoneWidth(this), 0);
        objectAnimator2.setDuration(320);
        objectAnimator2.setInterpolator(new LinearInterpolator());

        objectAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                doneText.setVisibility(View.VISIBLE);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator1, objectAnimator2);
        animatorSet.setStartDelay(320);
        animatorSet.start();
    }

    //初始化广播：监听应用卸载、添加忽略文件的广播，及时更新界面
    private void initReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;
                needChangeSafeMark = true;
                if (intent.getAction().equals(AppConstants.ACTION_FILTER_ADD_IGNORE)) {
                    String packageName = intent.getStringExtra("package_name");
                    Iterator<AvlAppInfo> infoIterator = dangerList.iterator();
                    boolean hasChanged = false;
                    while (infoIterator.hasNext()) {
                        AvlAppInfo appInfo = infoIterator.next();
                        if (appInfo.getPackageName().equals(packageName)) {
                            infoIterator.remove();
                            appInfo.delete();
                            hasChanged = true;
                        }
                    }
                    int position = SpUtil.getInstance().getInt(AppConstants.SCAN_RESULT_UPDATE_POSITION);
                    adapter.removeItem(true, position);
                    if (hasChanged) refreshData(false);
                } else if (intent.getAction().equals(AppConstants.ACTION_FILTER_CLEAR_BROSWER_HISTORY)) {
                    broswerHistoryCount = 0;
//                    int position = SpUtil.getInstance().getInt(AppConstants.SCAN_RESULT_UPDATE_POSITION);
//                    adapter.removeItem(false, position);
                    refreshData(true);
                } else if (intent.getAction().equals(AppConstants.ACTION_FILTER_REMOVED_VIRUS_APP)) {
                    String packgeName = intent.getStringExtra("package_name");
                    /*boolean isRemoveAll = SpUtil.getInstance().getBoolean(AppConstants.UNINSTALL_ALL, false);
                    int position = 0;
                    if (isRemoveAll) {
                        ArrayList<UninstallAppInfo> infos = (ArrayList<UninstallAppInfo>) SpUtil.getInstance().getList(AppConstants.UNINSTALL_ALL_LIST, UninstallAppInfo.class);
                        for (int i = 0; i < infos.size(); i++) {
                            UninstallAppInfo uninstallAppInfo = infos.get(i);
                            if (uninstallAppInfo.packageName.equals(packgeName)) {
                                position = uninstallAppInfo.index;
                                return;
                            }
                        }
                    } else {
                        position = SpUtil.getInstance().getInt(AppConstants.SCAN_RESULT_UPDATE_POSITION);
                    }*/
                    if (!TextUtils.isEmpty(packgeName)) {
                        Iterator<AvlAppInfo> iterator = dangerList.iterator();
                        boolean hasChanged = false;
                        while (iterator.hasNext()) {
                            AvlAppInfo appInfo = iterator.next();
                            if (appInfo.getPackageName().equals(packgeName)) {
                                iterator.remove();
                                appInfo.delete();
                                hasChanged = true;
                            }
                        }
                        if (hasChanged) {
                            /*try {
                                adapter.notifyItemRemoved(position);
                                dangerList.remove(position);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            int position1 = SpUtil.getInstance().getInt(AppConstants.SCAN_RESULT_UPDATE_POSITION);
                            adapter.removeItem(true, position1);*/
                            refreshData(true);
                        }
                    }
                } else if (intent.getAction().equals(AppConstants.ACTION_FILTER_CLEAN_JUNK)) {
                    refreshData(true);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.ACTION_FILTER_ADD_IGNORE);
        intentFilter.addAction(AppConstants.ACTION_FILTER_CLEAR_BROSWER_HISTORY);
        intentFilter.addAction(AppConstants.ACTION_FILTER_REMOVED_VIRUS_APP);
        intentFilter.addAction(AppConstants.ACTION_FILTER_CLEAN_JUNK);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }

    private void setTitle() {
        int problemCount = appConfig.getProblemCount();
        switch (appConfig.getSafeLevel()) {
            case SAFE:
                collapsingToolbarLayout.setTitle("Safe");
                break;
            case SUSPICIOUS:
                collapsingToolbarLayout.setTitle("Suspicious(" + problemCount + ")");
                break;
            case DANGER:
                collapsingToolbarLayout.setTitle("In Danger(" + problemCount + ")");
                break;
        }
    }

    private void setHead() {
        int problemCount = appConfig.getProblemCount();
        switch (appConfig.getSafeLevel()) {
            case SAFE:
                StatusBarUtil.setColor(this, getResources().getColor(R.color.primary), 38);
                collapsingToolbarLayout.setBackgroundResource(R.drawable.bg_main_primary);
                statusImg.setImageResource(R.drawable.ic_safe);
                statusText.setText("Safe");
                markText.setText(needChangeSafeMark ? "All threats resolved" : "No threat found");
                break;
            case SUSPICIOUS:
                StatusBarUtil.setColor(this, getResources().getColor(R.color.orange), 38);
                collapsingToolbarLayout.setBackgroundResource(R.drawable.bg_main_orange);
                statusImg.setImageResource(R.drawable.ic_sups);
                statusText.setText("Suspicious");
                markText.setText(problemCount + (problemCount > 1 ? " issues found" : " issue found"));
                break;
            case DANGER:
                StatusBarUtil.setColor(this, getResources().getColor(R.color.red), 38);
                collapsingToolbarLayout.setBackgroundResource(R.drawable.bg_main_red);
                statusImg.setImageResource(R.drawable.ic_danger);
                statusText.setText("In Danger");
                markText.setText(problemCount + (problemCount > 1 ? " problems found" : " problem found"));
                break;
        }
    }

    @Override
    public void finish() {
        saveData();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        if (presenter != null) {
            presenter.onDestory();
            presenter = null;
        }
        safeLevel = null;
        dangerList = null;
        resultInfos = null;
        adapter = null;
        appConfig = null;
        logFileCacheGroup = null;
        systempFileCacheGroup = null;
        apkFileCacheGroup = null;
        mMenuPopWindow = null;
        animAppBar = null;
        super.onDestroy();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_cloud_scan_result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_done:
                SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
                Map<String, Object> map = new HashMap<>();
                switch (safeLevel) {
                    case SAFE:
                        map.clear();
                        map.put("state", 1); //安全
                        KochavaUtils.tracker(AppConstants.CLICK_SCAN_RESULT_DONE, map);
                        onHomeClick();
                        break;
                    case SUSPICIOUS:
                        map.clear();
                        map.put("state", 2); //可疑
                        if (broswerHistoryCount > 0) {
                            map.put("privacy", broswerHistoryCount + ""); //隐私数量
                        }
                        if (junkFileSize > 0f) {
                            map.put("junk", StringUtil.getFormatSize(junkFileSize)); //垃圾数量
                        }
                        KochavaUtils.tracker(AppConstants.CLICK_SCAN_RESULT_DONE, map);

                        presenter.resolveAll(null, broswerHistoryCount != 0, junkFileSize != 0d);
                        break;
                    case DANGER:
                        map.clear();
                        map.put("state", 3); //危险
                        if (dangerList.size() > 0) {
                            map.put("virus", dangerList.size() + ""); //病毒数量
                        }
                        if (broswerHistoryCount > 0) {
                            map.put("privacy", broswerHistoryCount + ""); //隐私数量
                        }
                        if (junkFileSize > 0f) {
                            map.put("junk", StringUtil.getFormatSize(junkFileSize)); //垃圾数量
                        }
                        KochavaUtils.tracker(AppConstants.CLICK_SCAN_RESULT_DONE, map);

                        if (dangerList != null && dangerList.get(0) != null) {
                            presenter.resolveAll(dangerList, broswerHistoryCount != 0, junkFileSize != 0d);
                        } else {
                            presenter.resolveAll(null, broswerHistoryCount != 0, junkFileSize != 0d);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case R.id.img_back:
                onHomeClick();
                break;
            case R.id.btn_menu:
                KochavaUtils.tracker(AppConstants.CLICK_RESULT_IGNORELIST);
                mMenuPopWindow.showAsDropDown(mBtnMenu, 0, 0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMenuPopWindow.isShowing()) {
            mMenuPopWindow.dismiss();
        }
    }

    @Override
    public void updateClean() {

    }
}
