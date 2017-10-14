package com.tools.security.widget.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.batmobi.Ad;
import com.batmobi.BatNativeAd;
import com.facebook.ads.NativeAd;
import com.tools.security.R;
import com.tools.security.utils.ScreenUtil;
import com.tools.security.utils.volley.image.ImageUtils;
import com.tools.security.widget.RoundedImageView;
import com.tools.security.widget.SelectableRoundedImageView;
import com.tools.security.widget.dialog.base.BaseDialog;
import com.tools.security.widget.shimmer.Shimmer;
import com.tools.security.widget.shimmer.ShimmerTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * description:试试手气dialog
 * author: xiaodifu
 * date: 2017/1/19.
 */

public class AdTryDialog extends BaseDialog {

    private Context context;
    private boolean loadSuccess;
    private RelativeLayout mDialogLayout;
    private RoundedImageView mRoundedImageView;
    private RoundedImageView mImgIcon;
    private ImageView mIcFailed;
    private SelectableRoundedImageView mIcAd;
    private TextView mTextName, mTextMark;
    private LinearLayout mRatingBar;
    private ShimmerTextView mShimmerTextView;
    private List<Object> adObjects;
    private BatNativeAd batNativeAd;
    private List<Ad> batmobiNativeAds = new ArrayList<>();
    private List<Object> facebookNativeAds = new ArrayList<>();


    private String iconUrl = null;
    private String contentUrl = null;
    private List<String> contentUrls;

    public AdTryDialog(Context context, boolean loadSuccess, List<Object> adObjects) {
        super(context);
        this.context = context;
        this.loadSuccess = loadSuccess;
        this.adObjects = adObjects;
    }

    @Override
    protected float setWidthScale() {
        return 0.9f;
    }

    @Override
    protected AnimatorSet setEnterAnim() {
        final AnimatorSet set = new AnimatorSet();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mDialogLayout, "translationX", -(ScreenUtil.getPhoneWidth(context)), 0);
        set.setDuration(500);
        set.play(objectAnimator);
        return set;
    }

    @Override
    protected AnimatorSet setExitAnim() {
        final AnimatorSet set = new AnimatorSet();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mDialogLayout, "translationX", 0, ScreenUtil.getPhoneWidth(context));
        set.setDuration(500);
        set.play(objectAnimator);
        return set;
    }

    @Override
    protected void init() {
        setCanceledOnTouchOutside(false);
        mDialogLayout = (RelativeLayout) findViewById(R.id.dialog_layout);
        mRoundedImageView = (RoundedImageView) findViewById(R.id.img_ad_content);
        mImgIcon = (RoundedImageView) findViewById(R.id.img_icon);
        mIcFailed = (ImageView) findViewById(R.id.ic_failed);
        mIcAd = (SelectableRoundedImageView) findViewById(R.id.icon_ad);
        mTextName = (TextView) findViewById(R.id.text_name);
        mTextMark = (TextView) findViewById(R.id.text_mark);
        mRatingBar = (LinearLayout) findViewById(R.id.text_rating);
        mShimmerTextView = (ShimmerTextView) findViewById(R.id.text_learn_more);

        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(2000);
        shimmer.setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        shimmer.start(mShimmerTextView);

        if (loadSuccess) {
            if (adObjects != null && adObjects.size() > 0) {
                Object object = null;
                if (adObjects.get(0) instanceof BatNativeAd) {
                    batNativeAd = (BatNativeAd) adObjects.get(0);
                    batmobiNativeAds = batNativeAd.getAds();
                    object = batmobiNativeAds.get(0);
                } else if (adObjects.get(0) instanceof NativeAd) {
                    facebookNativeAds = adObjects;
                    object = facebookNativeAds.get(0);
                }
                if (object == null) return;
                if (object instanceof NativeAd) {
                    NativeAd nativeAd = (NativeAd) object;
                    NativeAd.Image iconImg = nativeAd.getAdIcon();
                    NativeAd.Image contentImg = nativeAd.getAdCoverImage();

                    if (iconImg != null) iconUrl = iconImg.getUrl();
                    if (contentImg != null) contentUrl = contentImg.getUrl();


                    if (!TextUtils.isEmpty(iconUrl))
                        ImageUtils.display2(iconUrl, mImgIcon, context, R.drawable.ic_default_90, R.drawable.ic_default_90);
                    //ImageUtils.displayNet2(iconUrl, mImgIcon, R.drawable.ic_default_90, R.drawable.ic_default_90);
                    if (!TextUtils.isEmpty(contentUrl))
                        ImageUtils.display2(contentUrl, mRoundedImageView, context, R.drawable.ic_defalut_1200, R.drawable.ic_defalut_1200);
                    //ImageUtils.displayNet2(contentUrl, mRoundedImageView, R.drawable.ic_defalut_1200, R.drawable.ic_defalut_1200);
                    mTextName.setText("" + nativeAd.getAdTitle());
                    mTextMark.setText("" + nativeAd.getAdBody());

                    nativeAd.registerViewForInteraction(mShimmerTextView);

                } else if (object instanceof Ad) {
                    Ad ad = (Ad) object;
                    String iconUrl = ad.getIcon();
                    if (!TextUtils.isEmpty(iconUrl)) {
                        ImageUtils.display2(iconUrl, mImgIcon, context, R.drawable.ic_default_90, R.drawable.ic_default_90);
                        //ImageUtils.displayNet2(iconUrl, mImgIcon, R.drawable.ic_default_90, R.drawable.ic_default_90);
                    }
                    contentUrls = ad.getCreatives(Ad.AD_CREATIVE_SIZE_1200x627);
                    if (contentUrls != null && contentUrls.size() > 0) {
                        if (!TextUtils.isEmpty(contentUrls.get(0))) {
                            ImageUtils.display2(contentUrls.get(0), mRoundedImageView, context, R.drawable.ic_defalut_1200, R.drawable.ic_defalut_1200);
                            //ImageUtils.displayNet2(contentUrls.get(0), mRoundedImageView, R.drawable.ic_defalut_1200, R.drawable.ic_defalut_1200);
                        }
                    }
                    mTextName.setText("" + ad.getName());
                    mTextMark.setText("" + ad.getDescription());

                    batNativeAd.registerView(mShimmerTextView, ad);
                }
            }
        } else {
            mRoundedImageView.setVisibility(View.GONE);
            mImgIcon.setVisibility(View.GONE);
            mTextName.setVisibility(View.GONE);
            mTextMark.setVisibility(View.GONE);
            mRatingBar.setVisibility(View.GONE);
            mShimmerTextView.setVisibility(View.GONE);
            mIcAd.setVisibility(View.GONE);
            mIcFailed.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.layout_ad_try;
    }
}
