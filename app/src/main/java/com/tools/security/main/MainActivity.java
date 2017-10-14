package com.tools.security.main;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;
import com.tools.security.R;
import com.tools.security.applock.view.AppLockFirstActivity;
import com.tools.security.applock.view.unlock.GestureSelfUnlockActivity;
import com.tools.security.applock.view.unlock.NumberSelfUnlockActivity;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.bean.SafeLevel;
import com.tools.security.bean.config.AppConfig;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.main.presenter.MainContract;
import com.tools.security.main.presenter.MainPresenter;
import com.tools.security.mainscan.view.MainScanActivity;
import com.tools.security.mainscan.view.MainScanResultActivity;
import com.tools.security.service.LockAppLoadService;
import com.tools.security.service.LockService;
import com.tools.security.utils.ColorUtil;
import com.tools.security.utils.KochavaUtils;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.StringUtil;
import com.tools.security.utils.ToastUtil;
import com.tools.security.utils.statistics.SecurityStaticOperator;
import com.tools.security.utils.statistics.StatisticConstant;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.widget.MyParticleSystem;
import com.tools.security.widget.dialog.AdTryDialog;
import com.tools.security.widget.dialog.RateDialog;
import com.tools.security.wifi.view.WifiMainActivity;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import static com.tools.security.service.LockAppLoadService.ACTION_START_LOAD_APP;

/**
 * description:主页Activity
 * author: xiaodifu
 * date: 2016/12/12.
 */

public class MainActivity extends BaseActivity implements MainContract.View, View.OnClickListener, LuckAnimation.LuckAnimationListener, View.OnTouchListener {

    private LinearLayout mBtnScanLayout;
    private ImageView storeImg, moreImg;
    private View scanImg;
    private View circleImg1, circleImg2;
    private View pressCircleView;
    private TextView statusText;
    private TextView statusDetailsText;
    private TextView scanTimeText;
    private DrawerLayout drawerLayout;
    private TextView wifiSecurityText, appLockText;

    //粒子效果
    private ParticleSystem ps;
    //应用配置
    private AppConfig appConfig;
    //当前页面渐变色表示安全级别
    private SafeLevel currentSafeLevel = SafeLevel.SAFE;
    private static final int HANDLER_MSG_HEART = 0;
    private static final int HANDLER_MSG_TYR = 1;

    //圆是否被按住
    private boolean circleIsPressed = false;
    //页面是否暂停了
    private boolean isPause = false;
    private Handler handler;

    //预防多次点击scan
    private boolean isStartedScan = false;

    private MyParticleSystem particleSystem;

    int width;
    int top;
    int left;

    private RateDialog rateDialog;

    private BroadcastReceiver normalBroadcastReceiver;

    private ObjectAnimator animator1;
    private ObjectAnimator animator2;
    private ObjectAnimator animator3;
    private ObjectAnimator animator4;
    private ObjectAnimator animator5;
    private ObjectAnimator animator6;
    private ObjectAnimator animator7;
    private ObjectAnimator animator8;
    private ObjectAnimator animator9;

    private AnimatorSet tryAnimatorSet;

    private PackageManager packageManager;

    private LuckAnimation luckAnimation;

    //加载试试手气是否已经完成
    private boolean loadTryFinished = true;

    private MainPresenter presenter;

    private boolean isShowAdDialog = true;
    private AdTryDialog mAdTryDialog;

    private Thread heartAnimThread;

    @Override
    protected void init() {
        initHandler();
        initData();
        initView();
        initReceiver();
        initService();
    }

    private void initHandler() {
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HANDLER_MSG_HEART:
                        playHeartbeatAnimation();
                        break;
                    case HANDLER_MSG_TYR:
                        startTryAnim();
                        break;
                }
            }
        };
    }

    private void initData() {
        //统计
        KochavaUtils.tracker(AppConstants.ON_APP_START);
        SecurityStaticOperator.uploadTab76(this, StatisticConstant.OPERATION_SHOW, StatisticConstant.TAB_LAUNCHER_APP);

        packageManager = getPackageManager();

        presenter = new MainPresenter(this, this);
        presenter.checkVirusLib();
    }

    private void initView() {
        width = (int) ((ScreenUtil.getPhoneWidth(this) * 3 / (float) 5) * 57 / 70);
        top = (int) (ScreenUtil.getPhoneHeight(this) * 2 / (float) 5 - (ScreenUtil.getPhoneWidth(this) * 3 / (float) 10) * 57 / 70);
        left = (int) ((ScreenUtil.getPhoneHeight(this) - width) / (float) 2);

        storeImg = (ImageView) findViewById(R.id.img_store);
        moreImg = (ImageView) findViewById(R.id.img_more);
        scanImg = findViewById(R.id.img_scan);
        statusText = (TextView) findViewById(R.id.text_status);
        statusDetailsText = (TextView) findViewById(R.id.text_status_details);
        circleImg1 = findViewById(R.id.img_circle1);
        circleImg2 = findViewById(R.id.img_circle2);
        pressCircleView = findViewById(R.id.view_circle_press);
        mBtnScanLayout = (LinearLayout) findViewById(R.id.btn_scan_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        wifiSecurityText = (TextView) findViewById(R.id.wifi_security_text);
        appLockText = (TextView) findViewById(R.id.app_lock_text);

        storeImg.setOnClickListener(this);
        moreImg.setOnClickListener(this);
        scanImg.setOnClickListener(this);
        mBtnScanLayout.setOnClickListener(this);
        wifiSecurityText.setOnClickListener(this);
        appLockText.setOnClickListener(this);

        //让Scan圆的直径为手机宽度的3/5，水平居中，距离顶部的距离（topMargin）为手机屏幕高度的2/5-圆的半径
        RelativeLayout.LayoutParams scanLayoutParams = new RelativeLayout.LayoutParams((int) (ScreenUtil.getPhoneWidth(this) * 7 / (float) 10), (int) (ScreenUtil.getPhoneWidth(this) * 7 / (float) 10));
        scanLayoutParams.topMargin = (int) (ScreenUtil.getPhoneHeight(this) * 2 / (float) 5 - ScreenUtil.getPhoneWidth(this) * 7 / (float) 20);
        scanLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        scanImg.setLayoutParams(scanLayoutParams);

        //让Circle圆的大小是Scanle圆的666/738
        RelativeLayout.LayoutParams circleLayoutParams = new RelativeLayout.LayoutParams((int) ((ScreenUtil.getPhoneWidth(this) * 7 / (float) 10) * 1.15f * 670 / 742), (int) ((ScreenUtil.getPhoneWidth(this) * 7 / (float) 10) * 1.15f * 670 / 742));
        circleLayoutParams.topMargin = (int) (ScreenUtil.getPhoneHeight(this) * 2 / (float) 5 - (ScreenUtil.getPhoneWidth(this) * 7 / (float) 20) * 1.15f * 680 / 742);
        circleLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        circleImg1.setLayoutParams(circleLayoutParams);
        circleImg2.setLayoutParams(circleLayoutParams);

        //让释放时扩散圆的大小为按压后圆的大小
        RelativeLayout.LayoutParams pressCircleLayoutParams = new RelativeLayout.LayoutParams((int) ((ScreenUtil.getPhoneWidth(this) * 7 / (float) 10) * 0.9f * 670 / 742), (int) ((ScreenUtil.getPhoneWidth(this) * 7 / (float) 10) * 0.9f * 670 / 742));
        pressCircleLayoutParams.topMargin = (int) (ScreenUtil.getPhoneHeight(this) * 2 / (float) 5 - (ScreenUtil.getPhoneWidth(this) * 7 / (float) 20) * 0.9f * 680 / 742);
        pressCircleLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        pressCircleView.setLayoutParams(pressCircleLayoutParams);

        //设置按压效果
        scanImg.setOnTouchListener(this);
        wifiSecurityText.setOnTouchListener(this);
        appLockText.setOnTouchListener(this);


        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                sendBroadcast(new Intent(AppConstants.ACTION_FILTER_MAIN_DRAWER_CLOSED));
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        //开启心跳动画
        startHeartAnim();
    }

    //初始化服务
    private void initService() {
        Intent intent = new Intent(this, LockAppLoadService.class);
        intent.setAction(ACTION_START_LOAD_APP);
        startService(intent);
    }

    //初始化广播
    private void initReceiver() {
        normalBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppConstants.ACTION_FILTER_ADD_IGNORE)) {
                    String packageName = intent.getStringExtra("package_name");
                    setStatus();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.ACTION_FILTER_ADD_IGNORE);

        registerReceiver(normalBroadcastReceiver, intentFilter);
    }

    //取消心跳圆动画
    private void cancleHeartAnim() {
        if (animator1 != null && animator1.isRunning()) animator1.cancel();
        if (animator2 != null && animator2.isRunning()) animator2.cancel();
        if (animator3 != null && animator3.isRunning()) animator3.cancel();
        if (animator5 != null && animator5.isRunning()) animator5.cancel();
        if (animator6 != null && animator6.isRunning()) animator6.cancel();
    }

    //按压压下效果
    private void pressHeartAnim(float scaleX, float scaleY,View view) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", scaleX, 0.9f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", scaleY, 0.9f);
        ObjectAnimator scaleAnimator=ObjectAnimator.ofPropertyValuesHolder(view,pvhScaleX,pvhScaleY);
        scaleAnimator.setDuration(200);
        scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimator.start();
    }

    //按压抬起效果
    private void releaseHeartAnim(View view) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 0.9f, 1.0f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 0.9f, 1.0f);
        ObjectAnimator scaleAnimator=ObjectAnimator.ofPropertyValuesHolder(view,pvhScaleX,pvhScaleY);
        scaleAnimator.setDuration(200);
        scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimator.start();
    }

    //释放扩散圆动画
    private void expandCircleAnim() {
        PropertyValuesHolder scaleX=PropertyValuesHolder.ofFloat("scaleX", 0.9f, 3.5f);
        PropertyValuesHolder scaleY=PropertyValuesHolder.ofFloat("scaleY", 0.9f, 3.5f);
        ObjectAnimator scaleAnimator=ObjectAnimator.ofPropertyValuesHolder(pressCircleView,scaleX,scaleY);
        scaleAnimator.setDuration(200);
        scaleAnimator.setInterpolator(new DecelerateInterpolator());

        scaleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                pressCircleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pressCircleView.setScaleX(1.0f);
                pressCircleView.setScaleY(1.0f);
                pressCircleView.setVisibility(View.GONE);
                startScan();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleAnimator.start();
    }

    //试试手气摇一摇动画
    private void startTryAnim() {
        if (animator8 == null) {
            animator8 = ObjectAnimator.ofFloat(storeImg, "rotation", 0f, 40f, -40f, 20f, -20f, 10f, -10f, 0f);
            animator8.setDuration(1200);
        }

        if (animator9 == null) {
            animator9 = ObjectAnimator.ofFloat(storeImg, "alpha", 1.0f, 0.99f, 1.0f);
            animator9.setDuration(3500);
        }

        if (tryAnimatorSet == null) {
            tryAnimatorSet = new AnimatorSet();
            tryAnimatorSet.play(animator8);
            tryAnimatorSet.play(animator9).after(animator8);
            tryAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (isPause) return;
                    tryAnimatorSet.start();
                }
            });
            tryAnimatorSet.setStartDelay(500);
        }

        tryAnimatorSet.start();
    }

    //点击扫描做的事
    private void startScan() {
        startActivity(new Intent(MainActivity.this, MainScanActivity.class));
        overridePendingTransition(R.anim.fast_alpha_in, R.anim.fast_alpha_out);
        KochavaUtils.tracker(AppConstants.CLICK_HOME_SCAN);
        appConfig.setScanned(true);
        SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setStatusBar() {
        int mStatusBarColor = getResources().getColor(R.color.primary);
        StatusBarUtil.setColorForDrawerLayout(this, (DrawerLayout) findViewById(R.id.drawer_layout_main), mStatusBarColor, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStartedScan = false;
        isPause = false;
        setStatus();
        initRateFeedbackDialog();
        //开启试试手气摇一摇动画
        startTryAnim();

        if (SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE, false)) {
            startService(new Intent(this, LockService.class));
        }

    }

    //设置是否显示评分及反馈弹窗
    private void initRateFeedbackDialog() {
        if (rateDialog != null && rateDialog.isShowing()) return;
        if (SpUtil.getInstance().getBoolean(AppConstants.FINISHED_RATE_OR_FEEDBACK, false)) return;
        int cleanedVirusCount = SpUtil.getInstance().getInt(AppConstants.CLEANED_VIRUS_COUNT);
        int cleanedPrivacyCount = SpUtil.getInstance().getInt(AppConstants.CLEANED_PRIVACY_COUNT);
        int cleanedJunkCount = SpUtil.getInstance().getInt(AppConstants.CLEANED_JUNK_COUNT);
        if (cleanedVirusCount > 0) {
            switch (currentSafeLevel) {
                case SAFE:
                    if (cleanedJunkCount > 0 || cleanedPrivacyCount > 0) {
                        rateDialog = new RateDialog(this, RateDialog.RateType.ALL, "");
                        rateDialog.show();
                    } else {
                        rateDialog = new RateDialog(this, RateDialog.RateType.VIRUS, "" + cleanedVirusCount);
                        rateDialog.show();
                    }
                    break;
                case SUSPICIOUS:
                case DANGER:
                    rateDialog = new RateDialog(this, RateDialog.RateType.PART, "");
                    rateDialog.show();
                    break;
                default:
                    break;
            }
        } else {
            if (cleanedJunkCount > 1 || cleanedPrivacyCount > 1) {
                if (cleanedPrivacyCount > 1) {
                    if (cleanedPrivacyCount > 1) {
                        switch (currentSafeLevel) {
                            case SAFE:
                                rateDialog = new RateDialog(this, RateDialog.RateType.ALL, "");
                                rateDialog.show();
                                break;
                            case SUSPICIOUS:
                            case DANGER:
                                rateDialog = new RateDialog(this, RateDialog.RateType.PART, "");
                                rateDialog.show();
                                break;
                            default:
                                break;
                        }
                    } else {
                        rateDialog = new RateDialog(this, RateDialog.RateType.PRIVACY, "");
                        rateDialog.show();
                    }
                } else {
                    double junksize = Double.parseDouble(SpUtil.getInstance().getString(AppConstants.CLEANED_JUNK_FILE_SIZE, "0"));
                    rateDialog = new RateDialog(this, RateDialog.RateType.JUNK, StringUtil.getFormatSize(junksize));
                    rateDialog.show();
                }
            } else {
                //不弹
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        startParticleAnim();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    //开启粒子动画
    private void startParticleAnim() {
        if (particleSystem != null) return;
        particleSystem = new MyParticleSystem(this, 4, R.drawable.ic_point, 5000);
        particleSystem.setScaleRange(1.0f, 2.0f);
        particleSystem.setSpeedRange(0.0001f, 0.001f);
        particleSystem.setFadeIn(1500);
        particleSystem.setFadeOut(1500);
        particleSystem.emitWithGravity(drawerLayout, Gravity.NO_GRAVITY, 0.4f);
    }

    //设置界面安全状态
    private void setStatus() {
        appConfig = SpUtil.getInstance().getBean(AppConstants.APP_CONFIG, AppConfig.class);
        if (appConfig == null) {
            appConfig = new AppConfig(false, SafeLevel.SUSPICIOUS, 0);
            SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
        }
        //去除危险列表中已经删除的恶意应用
        List<AvlAppInfo> dangerList = DataSupport.where("ignored = ? and result = ?", "0", "1").find(AvlAppInfo.class);
        if (dangerList != null && dangerList.size() > 0) {
            Iterator<AvlAppInfo> iterator = dangerList.iterator();
            int removedCount = 0;
            while (iterator.hasNext()) {
                AvlAppInfo appInfo = iterator.next();
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(appInfo.getPackageName(), 0);
                    if (packageInfo == null) {
                        appInfo.delete();
                        iterator.remove();
                        ++removedCount;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    appInfo.delete();
                    iterator.remove();
                    ++removedCount;
                }
            }
            if (removedCount != 0) {
                int problemCount = appConfig.getProblemCount() - removedCount;
                appConfig.setProblemCount(problemCount);
            }

            if (dangerList.size() == 0) {
                if (appConfig.getProblemCount() > 0) {
                    appConfig.setSafeLevel(SafeLevel.SUSPICIOUS);
                } else {
                    appConfig.setSafeLevel(SafeLevel.SAFE);
                }
            } else {
                appConfig.setSafeLevel(SafeLevel.DANGER);
            }
        }

        //预防出现问题0，状态又不是安全
        if (appConfig.getProblemCount() == 0 && appConfig.isScanned() == true) {
            appConfig.setSafeLevel(SafeLevel.SAFE);
        }

        switch (appConfig.getSafeLevel()) {
            default:
            case SAFE:
                statusText.setText("Safe");
                statusDetailsText.setText("System protected");
                break;
            case SUSPICIOUS:
                statusText.setText("Suspicious");
                String problemCountStr = appConfig.getProblemCount() > 1 ? appConfig.getProblemCount() + " Issues found" : appConfig.getProblemCount() + " Issues found";
                statusDetailsText.setText(appConfig.isScanned() ? problemCountStr : "Never Scanned");
                break;
            case DANGER:
                statusText.setText("Danger");
                statusDetailsText.setText(appConfig.getProblemCount() + (appConfig.getProblemCount() > 1 ? " Dangered" : " Dangered"));
                break;
        }

        if (appConfig.getSafeLevel() != currentSafeLevel) {
            startColorTranslation(currentSafeLevel, appConfig.getSafeLevel());
        }
        currentSafeLevel = appConfig.getSafeLevel();

        SpUtil.getInstance().putBean(AppConstants.APP_CONFIG, appConfig);
    }

    @Override
    public void onClick(View v) {
        if (!loadTryFinished) return;
        switch (v.getId()) {
            case R.id.img_store:
                if (isShowAdDialog) {
                    // 存储当前按钮的点击时间
                    long currentDate = Calendar.getInstance().get(Calendar.DATE);
                    SpUtil.getInstance().putLong(AppConstants.SYSTEME_CURRENT_DATE, currentDate);
                    luckAnimation = new LuckAnimation(MainActivity.this, (RelativeLayout) findViewById(R.id.id_main_content));
                    luckAnimation.setLuckAnimationListener(this);
                    luckAnimation.startAnimation();
                    isShowAdDialog = false;
                    loadTryFinished = false;
                    drawerLayout.setEnabled(false);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
                break;
            case R.id.img_more:
                boolean isOpen = drawerLayout.isDrawerOpen(findViewById(R.id.left_menu));
                if (isOpen) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(findViewById(R.id.left_menu));
                }
                break;
            case R.id.img_scan:
                if (!isStartedScan) {
                    isStartedScan = true;
                    expandCircleAnim();
                }
                break;
            case R.id.btn_scan_layout:
                KochavaUtils.tracker(AppConstants.CLICK_HOME_STATE_AREA);
                switch (currentSafeLevel) {
                    case SAFE:
                        startActivity(new Intent(MainActivity.this, MainScanActivity.class));
                        break;
                    case SUSPICIOUS:
                        if (appConfig.isScanned()) {
                            startActivity(new Intent(MainActivity.this, MainScanResultActivity.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, MainScanActivity.class));
                        }
                        break;
                    case DANGER:
                        startActivity(new Intent(MainActivity.this, MainScanResultActivity.class));
                        break;
                }
                break;
            case R.id.wifi_security_text:
                startActivity(new Intent(this, WifiMainActivity.class).putExtra("from", 1));
                overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_scale_out);
                break;
            case R.id.app_lock_text:
                gotoAppLockActivity();
                overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_scale_out);
                break;
        }
    }

    @Override
    public void onLuckAdLoadError() {
        loadTryFinished = true;
        drawerLayout.setEnabled(true);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mAdTryDialog = new AdTryDialog(MainActivity.this, false, null);
        mAdTryDialog.show();
        storeImg.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdTryDialog.dismiss();
                isShowAdDialog = true;
                luckAnimation = null;
            }
        }, 2000);
    }

    @Override
    public void onLuckAdLoadSuccess(List<Object> objects) {
        loadTryFinished = true;
        drawerLayout.setEnabled(true);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mAdTryDialog = new AdTryDialog(MainActivity.this, true, objects);
        mAdTryDialog.show();
        mAdTryDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isShowAdDialog = true;
                luckAnimation = null;
            }
        });
    }

    @Override
    public void onDialogDismiss() {
        if (mAdTryDialog.isShowing()) {
            mAdTryDialog.dismiss();
        }
    }

    /**
     * 关闭DrawerLayout
     */
    public void closeDrawerLayout() {
        if (drawerLayout.isDrawerOpen(findViewById(R.id.left_menu))) {
            drawerLayout.closeDrawers();
        }
    }

    //颜色渐变动画
    private synchronized void startColorTranslation(final SafeLevel current, final SafeLevel target) {
        int animResId = -1;
        int gradientStartColor = -1;
        int gradientEndColor = -1;
        int primary = 0xff1f90f9;
        int orange = 0xfff59c2f;
        int red = 0xffd73358;
        switch (current) {
            case SAFE:
                gradientStartColor = primary;
                if (target == SafeLevel.SUSPICIOUS) {
                    animResId = R.animator.bg_color_blue_orange;
                    gradientEndColor = orange;
                } else if (target == SafeLevel.DANGER) {
                    animResId = R.animator.bg_color_blue_red;
                    gradientEndColor = red;
                }
                break;
            default:
            case SUSPICIOUS:
                gradientStartColor = orange;
                if (target == SafeLevel.SAFE) {
                    animResId = R.animator.bg_color_orange_blue;
                    gradientEndColor = primary;
                } else if (target == SafeLevel.DANGER) {
                    animResId = R.animator.bg_color_orange_red;
                    gradientEndColor = red;
                }
                break;
            case DANGER:
                gradientStartColor = red;
                if (target == SafeLevel.SAFE) {
                    gradientEndColor = primary;
                    animResId = R.animator.bg_color_red_blue;
                } else if (target == SafeLevel.SUSPICIOUS) {
                    gradientEndColor = orange;
                    animResId = R.animator.bg_color_red_orange;
                }
                break;
        }
        if (animResId == -1) return;
        ObjectAnimator objectAnimator1 = (ObjectAnimator) AnimatorInflater.loadAnimator(MainActivity.this, animResId);
        objectAnimator1.setEvaluator(new ArgbEvaluator());
        objectAnimator1.setTarget(drawerLayout);
        objectAnimator1.start();
        if (gradientStartColor == -1 || gradientEndColor == -1) return;
        ColorUtil.translateStatusBarColors(this, gradientStartColor, gradientEndColor, 1500, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                switch (target) {
                    case SAFE:
                        StatusBarUtil.setColorForDrawerLayout(MainActivity.this, drawerLayout, getResources().getColor(R.color.primary), 0);
                        break;
                    case SUSPICIOUS:
                        StatusBarUtil.setColorForDrawerLayout(MainActivity.this, drawerLayout, getResources().getColor(R.color.orange), 0);
                        break;
                    case DANGER:
                        StatusBarUtil.setColorForDrawerLayout(MainActivity.this, drawerLayout, getResources().getColor(R.color.red), 0);
                        break;
                }
            }
        });

        //  添加侧滑栏相关颜色变化
        switch (target) {
            case SAFE:
                sendBroadcast(new Intent(AppConstants.ACTION_FILTER_CHANGE_COLOR).putExtra("safe_level", 1));
                break;
            case SUSPICIOUS:
                sendBroadcast(new Intent(AppConstants.ACTION_FILTER_CHANGE_COLOR).putExtra("safe_level", 2));
                break;
            case DANGER:
                sendBroadcast(new Intent(AppConstants.ACTION_FILTER_CHANGE_COLOR).putExtra("safe_level", 3));
                break;
        }
    }

    // 按钮模拟心脏跳动
    private void playHeartbeatAnimation() {
        if (circleIsPressed || isPause) return;
        AnimatorSet heartAnimatorSet = new AnimatorSet();
        int duration;
        switch (currentSafeLevel) {
            default:
            case SAFE:
                duration = 300;
                break;
            case SUSPICIOUS:
                duration = 250;
                break;
            case DANGER:
                duration = 200;
                break;
        }
        //scan圆放大XY
        if (animator1 == null) {
            PropertyValuesHolder scaleX=PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.15f);
            PropertyValuesHolder scaleY=PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.15f);
            animator1=ObjectAnimator.ofPropertyValuesHolder(scanImg,scaleX,scaleY);
            animator1.setInterpolator(new AccelerateInterpolator());
            animator1.setDuration(duration);
        }

        //scan圆缩小XY
        if (animator2 == null) {
            PropertyValuesHolder scaleX=PropertyValuesHolder.ofFloat("scaleX", 1.15f, 1.0f);
            PropertyValuesHolder scaleY=PropertyValuesHolder.ofFloat("scaleY", 1.15f, 1.0f);
            animator2=ObjectAnimator.ofPropertyValuesHolder(scanImg,scaleX,scaleY);
            animator2.setInterpolator(new DecelerateInterpolator());
            animator2.setDuration(duration* 2);
        }


        //circle1圆放大XY、模糊
        if (animator4 == null) {

            PropertyValuesHolder scaleX=PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.5f);
            PropertyValuesHolder scaleY=PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.5f);
            PropertyValuesHolder alpha=PropertyValuesHolder.ofFloat("alpha", 0.8f, 0.0f);
            animator4=ObjectAnimator.ofPropertyValuesHolder(circleImg1,scaleX,scaleY,alpha);
            animator4.setInterpolator(new AccelerateDecelerateInterpolator());
            animator4.setDuration(duration * 4);
        }

        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                circleImg1.setVisibility(View.VISIBLE);
            }
        });

        //安全等级为危险时：心跳多震动一次,圈多散发一个
        if (currentSafeLevel == SafeLevel.DANGER) {
            //scan圆XY缩小：第一次
            if (animator3 == null) {
                PropertyValuesHolder scaleX=PropertyValuesHolder.ofFloat("scaleX", 1.15f, 1.05f);
                PropertyValuesHolder scaleY=PropertyValuesHolder.ofFloat("scaleY", 1.15f, 1.05f);
                animator3 = ObjectAnimator.ofPropertyValuesHolder(scanImg,scaleX,scaleY);
                animator3.setInterpolator(new DecelerateInterpolator());
                animator3.setDuration(duration);
            }

            //scan圆Xy放大：第二次
            if (animator5 == null) {
                PropertyValuesHolder scaleX=PropertyValuesHolder.ofFloat("scaleX", 1.05f, 1.15f);
                PropertyValuesHolder scaleY=PropertyValuesHolder.ofFloat("scaleY", 1.05f, 1.15f);
                animator5 = ObjectAnimator.ofPropertyValuesHolder(scanImg,scaleX,scaleY);
                animator5.setInterpolator(new AccelerateInterpolator());
                animator5.setDuration(duration);
            }

            //scan圆Xy缩小：第二次
            if (animator6 == null) {
                PropertyValuesHolder scaleX=PropertyValuesHolder.ofFloat("scaleX", 1.15f, 1.0f);
                PropertyValuesHolder scaleY=PropertyValuesHolder.ofFloat("scaleY", 1.15f, 1.0f);
                animator6 = ObjectAnimator.ofPropertyValuesHolder(scanImg,scaleX,scaleY);
                animator6.setInterpolator(new DecelerateInterpolator());
                animator6.setDuration(duration * 2);
            }

            //circle圆2放大XY
            if (animator7 == null) {
                PropertyValuesHolder scaleX=PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.5f);
                PropertyValuesHolder scaleY=PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.5f);
                PropertyValuesHolder alpha=PropertyValuesHolder.ofFloat("alpha", 0.8f, 0.0f);
                animator7 = ObjectAnimator.ofPropertyValuesHolder(circleImg2,scaleX,scaleY);
                animator7.setInterpolator(new AccelerateDecelerateInterpolator());
                animator7.setDuration(duration * 4);
            }

            animator5.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    circleImg2.setVisibility(View.VISIBLE);
                }
            });

            heartAnimatorSet.play(animator1);
            heartAnimatorSet.play(animator3).with(animator4).after(animator1);
            heartAnimatorSet.play(animator5).after(animator3);
            heartAnimatorSet.play(animator6).with(animator7).after(animator5);
        } else {
            circleImg2.setVisibility(View.INVISIBLE);
            heartAnimatorSet.play(animator1);
            heartAnimatorSet.play(animator2).with(animator4).after(animator1);
        }

        heartAnimatorSet.start();
    }

    private void startHeartAnim() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int sleepTime = 1800;
                switch (currentSafeLevel) {
                    default:
                    case SAFE:
                        sleepTime = 2400;
                        break;
                    case SUSPICIOUS:
                        sleepTime = 1800;
                        break;
                    case DANGER:
                        sleepTime = 1600;
                        break;
                }
                handler.sendEmptyMessage(HANDLER_MSG_HEART);
                handler.postDelayed(heartAnimThread,sleepTime);
            }
        };

        heartAnimThread=new Thread(runnable);
        heartAnimThread.start();
    }

    private void gotoAppLockActivity() {
        boolean isFirstLock = SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_FIRST_LOCK, true);
        int lockType = SpUtil.getInstance().getInt(AppConstants.LOCK_TYPE);
        Intent intent;
        if (isFirstLock) { //如果第一次
            intent = new Intent(this, AppLockFirstActivity.class);
        } else {
            //判断是什么类型的锁屏
            if (lockType == 0) { //图形
                intent = new Intent(this, GestureSelfUnlockActivity.class);
            } else { //数字
                intent = new Intent(this, NumberSelfUnlockActivity.class);
            }
        }
        intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, AppConstants.APP_PACKAGE_NAME); //传自己的包名
        intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY);
        intent.putExtra("from", 1);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(findViewById(R.id.left_menu))) {
            drawerLayout.closeDrawers();
            return;
        }
        //屏蔽返回键
        if (!loadTryFinished) {
            return;
        }

        //当上次弹这句话的时间超过十分钟，则可以再次弹
        if (System.currentTimeMillis()-SpUtil.getInstance().getLong(AppConstants.LAST_MAIN_BACK_TIME,0)>10*60*60*1000l){
            ToastUtil.showLong("Ultra Security is protecting your device");
            SpUtil.getInstance().putLong(AppConstants.LAST_MAIN_BACK_TIME,System.currentTimeMillis());
        }

        //返回时和home键效果一样
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(normalBroadcastReceiver);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.img_scan:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        circleIsPressed = true;
                        cancleHeartAnim();
                        pressHeartAnim(scanImg.getScaleX(), scanImg.getScaleY(),scanImg);
                        scanImg.setScaleX(0.9f);
                        scanImg.setScaleY(0.9f);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        circleIsPressed = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        releaseHeartAnim(scanImg);
                        scanImg.setScaleX(1.0f);
                        scanImg.setScaleY(1.0f);
                        circleIsPressed = false;
                        break;
                }
                break;
            case R.id.wifi_security_text:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressHeartAnim(wifiSecurityText.getScaleX(), wifiSecurityText.getScaleY(),wifiSecurityText);
                        wifiSecurityText.setScaleX(0.9f);
                        wifiSecurityText.setScaleY(0.9f);
                        break;
                    case MotionEvent.ACTION_UP:
                        releaseHeartAnim(wifiSecurityText);
                        wifiSecurityText.setScaleX(1.0f);
                        wifiSecurityText.setScaleY(1.0f);
                        break;
                }
                break;
            case R.id.app_lock_text:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressHeartAnim(appLockText.getScaleX(), appLockText.getScaleY(),appLockText);
                        appLockText.setScaleX(0.9f);
                        appLockText.setScaleY(0.9f);
                        break;
                    case MotionEvent.ACTION_UP:
                        releaseHeartAnim(appLockText);
                        appLockText.setScaleX(1.0f);
                        appLockText.setScaleY(1.0f);
                        break;
                }
                break;
        }

        return false;
    }
}
