package com.tools.security.common.result;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.batmobi.AdError;
import com.batmobi.BatAdBuild;
import com.batmobi.BatAdType;
import com.batmobi.BatmobiLib;
import com.batmobi.FacebookAdConfig;
import com.batmobi.IAdListener;
import com.tools.security.R;
import com.tools.security.bean.CommonResult;
import com.tools.security.bean.CooperationAd;
import com.tools.security.bean.FunctionAd;
import com.tools.security.common.AdStaticConstant;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.adapter.CommonResultAdapter;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.LockUtil;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * description:公用安全结果页
 * author: xiaodifu
 * date: 2017/1/18.
 */

public class CommonResultActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;

    private LinearLayout headerLayout;
    private ImageView iconImg;
    private View bgView;
    private TextView titleText;
    private TextView markText;
    private ImageView backImg;

    private CommonResultAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private ArrayList<FunctionAd> functionAds;
    private ArrayList<CooperationAd> cooperationAds;
    private List<Object> normalAds;
    private CommonResult commonResult;
    private int headerHeght;
    private int headerLayoutHeight;

    private String placementId = null;

    //是否已经刷新了数据
    private boolean isRefreshedData = false;

    private static final int MSG_PUSH_TOP = 1;
    private static final int MAX_LOADING_TIME = 2000;

    //动画是否已经完成，若完成，则可以返回
    private boolean animFinished = false;

    //判断页面是否是从底部进入的
    private boolean isFromBottom = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PUSH_TOP:
                    if (!isRefreshedData) {
                        adapter.setData(normalAds, functionAds, cooperationAds);
                    }
                    startPushAnim();
                    break;
            }
        }
    };

    @Override
    protected void init() {
        initData();
        initView();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_PUSH_TOP);
            }
        }, MAX_LOADING_TIME);
    }

    private void initData() {
        isFromBottom = getIntent().getIntExtra("from", 0) == 0 ? false : true;
        Bundle bundle = getIntent().getExtras();
        commonResult = (CommonResult) bundle.getSerializable("result");
        if (commonResult != null) {
            headerHeght = commonResult.getHeaderHeight();
        } else {
            headerHeght = ScreenUtil.dip2px(this, 76f);
        }

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            headerLayoutHeight = headerHeght + actionBarHeight;
        } else {
            headerLayoutHeight = headerHeght + ScreenUtil.dip2px(this, 54f);
        }
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        headerLayout = (LinearLayout) findViewById(R.id.layout_header);
        bgView = findViewById(R.id.bg_view);
        iconImg = (ImageView) findViewById(R.id.img_icon);
        titleText = (TextView) findViewById(R.id.text_title);
        markText = (TextView) findViewById(R.id.text_mark);
        backImg = (ImageView) findViewById(R.id.img_back);

        adapter = new CommonResultAdapter(this, headerHeght);
        recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                super.onScrolled(recyclerView, dx, dy);
                float scrollY = getScollYDistance();
                float alpha = 0f;
                if (scrollY > headerHeght) {
                    alpha = 0.0f;
                } else {
                    alpha = 1f - scrollY / headerHeght;
                }
                if (linearLayoutManager.findFirstVisibleItemPosition() > 0) alpha = 0.0f;
                titleText.setAlpha(alpha);
                markText.setAlpha(alpha);
            }
        });

        if (commonResult != null) {
            titleText.setText(commonResult.getTitle());
            markText.setText(commonResult.getMark());

        }
        backImg.setOnClickListener(this);

        startRotateAnim();
        loadData();
    }

    public int getScollYDistance() {
        int position = linearLayoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = linearLayoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }

    @Override
    public void finish() {
        super.finish();
        if (isFromBottom) overridePendingTransition(R.anim.slide_scale_in,R.anim.slide_bottom_out);
    }

    private void loadData() {
        boolean applocked = SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE, false);

        //是否有应用访问量权限
        boolean hasStatAccessPermission = false;
        if (LockUtil.isNoOption(this)) {
            if (LockUtil.isStatAccessPermissionSet(this)) {
                hasStatAccessPermission = true;
            } else {
                hasStatAccessPermission = false;
            }
        } else {
            hasStatAccessPermission = true;
        }

        //查询功能广告条件
        String condition = " type not in (" + commonResult.getFunctionType() + (!applocked ? "" : ", " + FunctionAd.APP_LOCK) + (!hasStatAccessPermission ? "" : ", " + FunctionAd.PERMISSION) + " ) ";
        functionAds = (ArrayList<FunctionAd>) DataSupport.where(condition).order("last_user_time asc").find(FunctionAd.class);
        cooperationAds = (ArrayList<CooperationAd>) DataSupport.findAll(CooperationAd.class);

        switch (commonResult.getFunctionType()) {
            default:
            case FunctionAd.VIRUS:
                placementId = AppConstants.BATMOBI_VIRUS_RESULT_PLACEMENT_ID;
                break;
            case FunctionAd.WIFI:
                placementId = AppConstants.BATMOBI_WIFI_RESULT_PLACEMENT_ID;
                break;
            case FunctionAd.SCANLE_FILE:
                placementId = AppConstants.BATMOBI_SCAN_FILES_PLACEMENT_ID;
                break;
            case FunctionAd.RELEASING:
                placementId = AppConstants.BATMOBI_WIFI_RELEASING_PLACEMENT_ID;
                break;
        }

        List<Object> localAds = AdStaticConstant.ads;
        if (localAds != null && AdStaticConstant.normalAdSaveTime > System.currentTimeMillis() - 60 * 60 * 1000l) {
            normalAds = localAds;
            isRefreshedData = true;
            adapter.setData(normalAds, functionAds, cooperationAds);
        } else {
            loadNetAds();
        }

    }

    private void loadNetAds() {
        AdStaticConstant.ads = null;
        FacebookAdConfig config = new FacebookAdConfig();
        config.setRequestNativeAdCount(6);
        BatAdBuild.Builder build = new BatAdBuild.Builder(CommonResultActivity.this,
                placementId,
                BatAdType.NATIVE.getType(),
                new IAdListener() {
                    @Override
                    public void onAdLoadFinish(List<Object> obj) {
                        normalAds = obj;
                        isRefreshedData = true;
                        adapter.setData(normalAds, functionAds, cooperationAds);
                        AppUtils.isFacebookAd(obj);
                    }

                    @Override
                    public void onAdError(AdError error) {
                        isRefreshedData = true;
                        adapter.setData(normalAds, functionAds, cooperationAds);
                    }

                    @Override
                    public void onAdClosed() {

                    }

                    @Override
                    public void onAdShowed() {

                    }

                    @Override
                    public void onAdClicked() {
                    }
                })
                .setAdsNum(6)
                .setFacebookConfig(config);
        BatmobiLib.load(build.build());
    }

    private void startRotateAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(iconImg, "rotationY", 180, 0, -180, -90, -45, 0);
        animator.setDuration(MAX_LOADING_TIME);
        animator.start();
    }

    private void startPushAnim() {
        final ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 1.0f, headerLayoutHeight / (float) ScreenUtil.getPhoneHeight(this), Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.0F);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(700);

        PropertyValuesHolder holderTransY = PropertyValuesHolder.ofFloat("translationY", 0f, -ScreenUtil.getPhoneHeight(this) / 3f);
        PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
        ObjectAnimator objectAnimatorLogo = ObjectAnimator.ofPropertyValuesHolder(iconImg, holderTransY, holderAlpha);
        objectAnimatorLogo.setDuration(700);

        int[] titleLocation = new int[2];
        titleText.getLocationInWindow(titleLocation);
        int titleTransY = titleLocation[1] - ScreenUtil.dip2px(this, 54f);
        ObjectAnimator objectAnimatorTitle = ObjectAnimator.ofFloat(titleText, "translationY", 0f, -titleTransY);
        objectAnimatorTitle.setDuration(700);

        ObjectAnimator objectAnimatorMark = ObjectAnimator.ofFloat(markText, "translationY", 0f, -(titleTransY + ScreenUtil.dip2px(this, 5f)));
        objectAnimatorMark.setDuration(700);

        final TranslateAnimation translate = new TranslateAnimation(0f, 0f, ScreenUtil.getPhoneHeight(this), 0f);
        translate.setDuration(700);
        translate.setFillAfter(true);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimatorLogo, objectAnimatorTitle, objectAnimatorMark);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                bgView.startAnimation(scaleAnimation);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.startAnimation(translate);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                backImg.setVisibility(View.VISIBLE);
                animFinished = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_common_result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onHomeClick();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!animFinished) return;
        super.onBackPressed();
    }
}
