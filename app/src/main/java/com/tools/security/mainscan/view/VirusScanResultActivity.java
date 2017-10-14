package com.tools.security.mainscan.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tools.security.R;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.WifiProblem;
import com.tools.security.bean.config.AppConfig;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.wifi.adapter.WifiProblemAdapter;
import com.tools.security.wifi.view.WifiProblemResultActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2017/1/19.
 */

public class VirusScanResultActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private WifiProblemAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private TextView statusText;
    private TextView markText;
    private LinearLayout headerLayout;
    private TextView resolveText;
    private ImageView statusImg;
    private View resolveLayout;

    private ValueAnimator animAppBar;
    private boolean isChangeStatusImgAlpha = false;

    private AppConfig appConfig;
    private ArrayList<AvlAppInfo> dangerList = new ArrayList<>();
    private int broswerHistoryCount;

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        statusText = (TextView) findViewById(R.id.text_problems);
        markText = (TextView) findViewById(R.id.text_desc);
        headerLayout = (LinearLayout) findViewById(R.id.layout_header);
        resolveText = (TextView) findViewById(R.id.text_resovle_all);
        resolveLayout=findViewById(R.id.layout_resolve);

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
                if (scrollY > ScreenUtil.dip2px(VirusScanResultActivity.this, 126f)) {
                    alpha = 0.0f;
                } else {
                    alpha = 1f - scrollY / ScreenUtil.dip2px(VirusScanResultActivity.this, 126f);
                }
                if (linearLayoutManager.findFirstVisibleItemPosition() > 0) alpha = 0.0f;
                statusText.setAlpha(alpha);
                markText.setAlpha(alpha);
            }
        });
        resolveText.setOnClickListener(this);
    }

    public int getScollYDistance() {
        int position = linearLayoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = linearLayoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }

    private void initData() {
        appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);

        dangerList = (ArrayList<AvlAppInfo>) DataSupport.where("ignored = ? and result = ?", "0", "1").find(AvlAppInfo.class);

        broswerHistoryCount = SpUtil.getInstance().getInt(AppConstants.BROSWER_HISTORY_COUNT);
    }


    /**
     * 头部view动画
     */
    private void initAppBarAnim() {
        final int phoneHeight = ScreenUtil.getPhoneHeight(VirusScanResultActivity.this);
        final int tranY = phoneHeight - AppUtils.dip2px(VirusScanResultActivity.this, 230);
        final CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        animAppBar = ValueAnimator.ofInt(0, tranY);
        animAppBar.setInterpolator(new AccelerateInterpolator());
        animAppBar.setDuration(350).start();
        animAppBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                params.height = phoneHeight - value;
                headerLayout.setLayoutParams(params);
                if (value > AppUtils.dip2px(VirusScanResultActivity.this, 80)) {
                    if (resolveLayout.getVisibility() == View.GONE)
                        resolveLayout.setVisibility(View.VISIBLE);
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

    //设置清理按钮进来的动画
    private void startDoneTextAnim() {

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(resolveLayout, "alpha", 0.0f, 1.0f);
        objectAnimator1.setDuration(320);

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(resolveLayout, "translationX", ScreenUtil.getPhoneWidth(this), 0);
        objectAnimator2.setDuration(320);
        objectAnimator2.setInterpolator(new LinearInterpolator());

        objectAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                resolveText.setVisibility(View.VISIBLE);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator1, objectAnimator2);
        animatorSet.setStartDelay(320);
        animatorSet.start();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_virus_problem_result;
    }

    @Override
    public void onClick(View v) {

    }
}
