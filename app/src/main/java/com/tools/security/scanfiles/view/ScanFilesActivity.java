package com.tools.security.scanfiles.view;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.batmobi.AdError;
import com.batmobi.BatAdBuild;
import com.batmobi.BatAdType;
import com.batmobi.BatmobiLib;
import com.batmobi.FacebookAdConfig;
import com.batmobi.IAdListener;
import com.tools.security.R;
import com.tools.security.bean.CommonResult;
import com.tools.security.bean.FunctionAd;
import com.tools.security.common.AdStaticConstant;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.common.result.CommonResultActivity;
import com.tools.security.scanfiles.presenter.ScanFilesContract;
import com.tools.security.scanfiles.presenter.ScanFilesPresenter;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.SpUtil;
import com.tools.security.utils.ToastUtil;
import com.tools.security.utils.statusbar.StatusBarUtil;
import com.tools.security.widget.ScanSdcardView;
import com.tools.security.widget.dialog.NormalDialog;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * description:sd卡扫描
 * author: xiaodifu
 * date: 2016/12/13.
 */

public class ScanFilesActivity extends BaseActivity implements ScanFilesContract.View {

    private TextView mScanResultView;
    private TextView mScanCountView;
    private TextView mVirusCountText;
    private ScanSdcardView mScanSdcardView;
    private RelativeLayout mContentView;
    private ImageView mIconSdCard;
    private ScanFilesPresenter mPresenter;
    private NormalDialog cancelDialog;
    private ObjectAnimator objectAnimator1, objectAnimator2, objectAnimator3, objectAnimator4, sdcardAnimator;
    private AnimatorSet animatorSet;
    private String fileCount;
    private boolean scanFinished = false;
    private boolean haveVirus = false;
    private boolean animStarted = false;
    private static final int STORAGE_REQUEST_CODE = 1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_sdcard_scan;
    }

    @Override
    protected void init() {
        mPresenter = new ScanFilesPresenter(this, this);
        mContentView = (RelativeLayout) findViewById(R.id.id_main_content);
        mScanResultView = (TextView) findViewById(R.id.scan_result_view);
        mScanCountView = (TextView) findViewById(R.id.scan_count_view);
        mVirusCountText = (TextView) findViewById(R.id.text_virus_count);
        mScanSdcardView = new ScanSdcardView(this);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = ScreenUtil.getPhoneHeight(this) * 1 / 5;
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mContentView.addView(mScanSdcardView, layoutParams);

        mIconSdCard = new ImageView(this);
        mIconSdCard.setImageResource(R.drawable.ic_sdcard);
        RelativeLayout.LayoutParams sdcardParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sdcardParams.topMargin = ScreenUtil.getPhoneHeight(this) * 48 / 100;
        sdcardParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mContentView.addView(mIconSdCard, sdcardParams);

        requestPermission(this);
    }

    private void startScan(){
        //开始扫描
        initData();
        mPresenter.startScanSdcard();
        //开始sdcard图标动画
        sdcardAnimator = ObjectAnimator.ofFloat(mIconSdCard, "alpha", 1f, 0.4f);
        sdcardAnimator.setDuration(1200);
        sdcardAnimator.setRepeatMode(ValueAnimator.REVERSE);
        sdcardAnimator.setRepeatCount(1000);
        sdcardAnimator.start();
        loadAd();
    }

    private void initData() {
        //更新功能最后使用时间
        List<FunctionAd> functionAds2 = DataSupport.where(" type = ?", "" + FunctionAd.SCANLE_FILE).find(FunctionAd.class);
        if (functionAds2 != null && functionAds2.size() > 0) {
            FunctionAd functionAd = functionAds2.get(0);
            functionAd.setLast_user_time(System.currentTimeMillis());
            functionAd.update(functionAd.getId());
        }
    }

    private void requestPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
            } else {
                startScan();
            }
        } else {
            startScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                startScan();
            } else {
                finish();
                ToastUtil.showShort("Permissions Denied");
            }
        }
    }

    @Override
    public void onUpdateProgress(String perResult, float arcPer) {
        mScanSdcardView.setData(perResult, arcPer);
        if (perResult.equals("100")) onScanFinish();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }

    @Override
    public void onUpdateText(String filePath, String count) {
        // TODO: 2017/1/20 未知闪退原因
        try{
            mScanResultView.setText(filePath);
            mScanCountView.setText(count + " " + getString(R.string.file));
        }catch (Exception e){
            e.printStackTrace();
        }
        fileCount = count;
    }

    @Override
    public void onUpdateVirusCount(int count) {
        mVirusCountText.setText(getString(R.string.threats, "" + count));
        if (!animStarted) {
            startVirusAnim();
            animStarted = true;
        }
    }

    private void loadAd(){
        AdStaticConstant.ads=null;
        FacebookAdConfig config = new FacebookAdConfig();
        config.setRequestNativeAdCount(6);
        BatAdBuild.Builder build = new BatAdBuild.Builder(this,
                AppConstants.BATMOBI_VIRUS_RESULT_PLACEMENT_ID,
                BatAdType.NATIVE.getType(),
                new IAdListener() {
                    @Override
                    public void onAdLoadFinish(List<Object> obj) {
                        AppUtils.isFacebookAd(obj);
                        AdStaticConstant.ads=obj;
                        AdStaticConstant.normalAdSaveTime=System.currentTimeMillis();
                    }

                    @Override
                    public void onAdError(AdError error) {
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

    //开启扫描到病毒时的动画：背景色遍红，病毒文字闪烁
    private void startVirusAnim() {
        objectAnimator1 = ObjectAnimator.ofFloat(mVirusCountText, "scaleX", 1.0f, 0.98f);
        objectAnimator1.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator1.setRepeatCount(1000);
        objectAnimator1.setDuration(1200);
        objectAnimator2 = ObjectAnimator.ofFloat(mVirusCountText, "scaleY", 1.0f, 0.98f);
        objectAnimator2.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator2.setRepeatCount(1000);
        objectAnimator2.setDuration(1200);
        objectAnimator3 = ObjectAnimator.ofFloat(mVirusCountText, "alpha", 0.95f, 0.6f);
        objectAnimator3.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator3.setDuration(1200);
        objectAnimator3.setRepeatCount(1000);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator1, objectAnimator2, objectAnimator3);
        animatorSet.start();

        objectAnimator4 = (ObjectAnimator) AnimatorInflater.loadAnimator(ScanFilesActivity.this, R.animator.bg_color_blue_red);
        objectAnimator4.setEvaluator(new ArgbEvaluator());
        objectAnimator4.setTarget(mContentView);
        objectAnimator4.start();
    }

    @Override
    public void onScanFinish() {
        scanFinished = true;
        mPresenter.onDestroy();
        SpUtil.getInstance().putString(AppConstants.SD_CARD_FILE_COUNT, fileCount);
        if (cancelDialog != null && cancelDialog.isShowing()) cancelDialog.superDismiss();
        if (!haveVirus) {
            Bundle bundle = new Bundle();
            CommonResult commonResult = new CommonResult("Safe", getString(R.string.scanned_files, SpUtil.getInstance().getString(AppConstants.SD_CARD_FILE_COUNT)), FunctionAd.SCANLE_FILE, ScreenUtil.dip2px(this, 76f));
            bundle.putSerializable("result", commonResult);
            startActivity(new Intent(ScanFilesActivity.this, CommonResultActivity.class).putExtras(bundle));
            finish();
        } else {
            startActivity(new Intent(ScanFilesActivity.this, ScanFilesDangerActivity.class));
            finish();
        }
    }

    @Override
    protected void onHomeClick() {
        if (!scanFinished) {
            cancelDialog = new NormalDialog(this, new NormalDialog.IOnClickListener() {
                @Override
                public void onLeftClick() {
                    mPresenter.onDestroy();
                    ScanFilesActivity.this.finish();
                }

                @Override
                public void onRightClick() {
                    cancelDialog.superDismiss();
                }
            });
            cancelDialog.title(getString(R.string.scanning_file_cancel_title))
                    .left(getString(R.string.cancel_cap))
                    .right(getString(R.string.cap_continue))
                    .show();
        } else {
            super.onHomeClick();
        }
    }

    @Override
    public void refreshResult(boolean haveVirus) {
        this.haveVirus = haveVirus;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelDialog = null;
        objectAnimator1 = null;
        objectAnimator2 = null;
        objectAnimator3 = null;
        objectAnimator4 = null;
        sdcardAnimator = null;
        if (mScanSdcardView != null) {
            mScanSdcardView.onDestory();
            mScanSdcardView = null;
        }
        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.cancel();
            animatorSet = null;
        }
    }

}
