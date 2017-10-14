package com.tools.security.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.batmobi.AdError;
import com.batmobi.BatAdBuild;
import com.batmobi.BatAdType;
import com.batmobi.BatNativeAd;
import com.batmobi.BatmobiLib;
import com.batmobi.FacebookAdConfig;
import com.batmobi.IAdListener;
import com.tools.security.R;
import com.tools.security.common.AppConstants;
import com.tools.security.common.BaseActivity;
import com.tools.security.utils.AppUtils;

import java.util.List;

/**
 * description:广告测试
 * author: xiaodifu
 * date: 2017/1/16.
 */

public class BatmobiAdDemoActivity extends BaseActivity {

    private ListView mListView;
    private ProgressDialog mProgressDialog;
    private BatNativeAd mNativeAd;
    private List<Object> mFacebookNativeAds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        mListView = (ListView) findViewById(R.id.my_native);
        mProgressDialog = new ProgressDialog(BatmobiAdDemoActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("loading..");
        loadNativeAd();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_batmobi_test;
    }


    private void loadNativeAd() {
        FacebookAdConfig config = new FacebookAdConfig();
        config.setRequestNativeAdCount(20);
        BatAdBuild.Builder build = new BatAdBuild.Builder(BatmobiAdDemoActivity.this,
                AppConstants.BATMOBI_VIRUS_RESULT_PLACEMENT_ID,
                BatAdType.NATIVE.getType(),
                new IAdListener() {
                    @Override
                    public void onAdLoadFinish(List<Object> obj) {
                        AppUtils.isFacebookAd(obj);
                        if (obj == null || obj.size() < 1) {
                            return;
                        }
                        if (obj.get(0) instanceof BatNativeAd) {
                            mNativeAd = (BatNativeAd) obj.get(0);
                            refreshView();
                        } else if (obj.get(0) instanceof com.facebook.ads.NativeAd) {
                            mFacebookNativeAds = obj;
                            refreshFacebookView();
                        }
                    }

                    @Override
                    public void onAdError(AdError error) {
                        if (null != mProgressDialog) {
                            mProgressDialog.dismiss();
                        }
                        Toast.makeText(BatmobiAdDemoActivity.this, error.getMsg(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAdClosed() {

                    }

                    @Override
                    public void onAdShowed() {

                    }

                    @Override
                    public void onAdClicked() {
                        Toast.makeText(BatmobiAdDemoActivity.this, "点击", Toast.LENGTH_SHORT).show();
                    }
                })
                //.setAdsNum(15)
                .setFacebookConfig(config);
        BatmobiLib.load(build.build());

    }

    private void refreshView() {
        if (BatmobiAdDemoActivity.this != null) {
            AdListAdapter mAdListAdaptor = new AdListAdapter(BatmobiAdDemoActivity.this, mNativeAd);
            mListView.setAdapter(mAdListAdaptor);
            if (null != mProgressDialog) {
                mProgressDialog.dismiss();
            }
        }
    }

    private void refreshFacebookView() {
        if (BatmobiAdDemoActivity.this != null) {
            FacebookAdListAdaptor mFaceBookAdaptor = new FacebookAdListAdaptor(BatmobiAdDemoActivity.this, mFacebookNativeAds);
            mListView.setAdapter(mFaceBookAdaptor);
            if (null != mProgressDialog) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
        if (mNativeAd != null) {
            mNativeAd.clean();
        }
    }
}
