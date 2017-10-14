package com.tools.security.common.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.batmobi.Ad;
import com.batmobi.BatNativeAd;
import com.facebook.ads.NativeAd;
import com.tools.security.R;
import com.tools.security.bean.CooperationAd;
import com.tools.security.bean.FunctionAd;
import com.tools.security.common.viewhoder.CooperateAdViewHolder;
import com.tools.security.common.viewhoder.FunctionAdViewHolder;
import com.tools.security.common.viewhoder.NormalNativeAdViewHolder;
import com.tools.security.common.viewhoder.TransparentViewHolder;
import com.tools.security.scanfiles.view.ScanFilesActivity;
import com.tools.security.utils.AppUtils;
import com.tools.security.utils.SystemUtil;
import com.tools.security.utils.volley.image.ImageUtils;
import com.tools.security.widget.dialog.LockPermissionsDialog;
import com.tools.security.widget.shimmer.Shimmer;
import com.tools.security.wifi.view.WifiMainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * description:scanfile安全结果页adapter
 * author: xiaodifu
 * date: 2017/1/15.
 */

public class CommonResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TRANSPARENT = 1;
    private static final int VIEW_TYPE_FUNCTION = 2;
    private static final int VIEW_TYPE_AD_NORMAL = 3;
    private static final int VIEW_TYPE_AD_COOPERATE = 4;

    private BatNativeAd batNativeAd;
    private List<Ad> batmobiNativeAds = new ArrayList<>();
    private List<Object> facebookNativeAds = new ArrayList<>();
    //判断是否是batmobi的广告
    private boolean isBatMobiAd = false;

    private ArrayList<FunctionAd> functionAds = new ArrayList<>();
    private ArrayList<CooperationAd> cooperationAds = new ArrayList<>();

    private Context context;
    private int transHeight;

    public CommonResultAdapter(Context context, int transHeight) {
        this.context = context;
        this.transHeight = transHeight;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TRANSPARENT) {
            View view = new View(context);
            if (transHeight <= 0) {
                transHeight = 100;
            }
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, transHeight);
            view.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            view.setLayoutParams(layoutParams);
            return new TransparentViewHolder(view);
        } else if (viewType == VIEW_TYPE_FUNCTION) {
            return new FunctionAdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_funtion, parent, false));
        } else if (viewType == VIEW_TYPE_AD_COOPERATE) {
            return new CooperateAdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_funtion, parent, false));
        }

        return new NormalNativeAdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_normal_native, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int itemType = viewHolder.getItemViewType();
        switch (itemType) {
            case VIEW_TYPE_TRANSPARENT:
                break;
            case VIEW_TYPE_AD_NORMAL:
                NormalNativeAdViewHolder normalHolder = (NormalNativeAdViewHolder) viewHolder;
                Object object = null;
                if (position == 1) {
                    if (isBatMobiAd) {
                        object = batmobiNativeAds.get(position - 1);
                    } else {
                        object = facebookNativeAds.get(position - 1);
                    }
                } else {
                    if (isBatMobiAd) {
                        object = batmobiNativeAds.get(position - functionAds.size() - cooperationAds.size() - 1);
                    } else {
                        object = facebookNativeAds.get(position - functionAds.size() - cooperationAds.size() - 1);
                    }
                }
                if (object instanceof NativeAd) {
                    NativeAd nativeAd = (NativeAd) object;
                    NativeAd.Image iconImg = nativeAd.getAdIcon();
                    NativeAd.Image contentImg = nativeAd.getAdCoverImage();
                    String iconUrl = null;
                    String contentUrl = null;
                    if (iconImg != null) iconUrl = iconImg.getUrl();
                    if (contentImg != null) contentUrl = contentImg.getUrl();
                    if (!TextUtils.isEmpty(iconUrl))
                        ImageUtils.displayNet2(iconUrl, normalHolder.iconImg, R.drawable.ic_default_90, R.drawable.ic_default_90);
                    if (!TextUtils.isEmpty(contentUrl))
                        ImageUtils.displayNet2(contentUrl, normalHolder.contentImg, R.drawable.ic_defalut_1200, R.drawable.ic_defalut_1200);
                    normalHolder.nameText.setText("" + nativeAd.getAdTitle());
                    normalHolder.markText.setText("" + nativeAd.getAdBody());
                    normalHolder.installText.setText(TextUtils.isEmpty(nativeAd.getAdCallToAction()) ? "INSTALL" : nativeAd.getAdCallToAction());
                    nativeAd.registerViewForInteraction(normalHolder.itemView);
                } else if (object instanceof Ad) {
                    Ad ad = (Ad) object;
                    String iconUrl = ad.getIcon();
                    if (!TextUtils.isEmpty(iconUrl)) {
                        ImageUtils.displayNet2(iconUrl, normalHolder.iconImg, R.drawable.ic_default_90, R.drawable.ic_default_90);
                    }
                    List<String> contentUrls = ad.getCreatives(Ad.AD_CREATIVE_SIZE_1200x627);
                    if (contentUrls != null && contentUrls.size() > 0) {
                        if (!TextUtils.isEmpty(contentUrls.get(0))) {
                            ImageUtils.displayNet2(contentUrls.get(0), normalHolder.contentImg, R.drawable.ic_defalut_1200, R.drawable.ic_defalut_1200);
                        }
                    }
                    normalHolder.nameText.setText("" + ad.getName());
                    normalHolder.markText.setText("" + ad.getDescription());
                    normalHolder.installText.setText(TextUtils.isEmpty(ad.getRecommendMessage()) ? "INSTALL" : ad.getRecommendMessage());
                    batNativeAd.registerView(normalHolder.itemView, ad);
                }
//                Shimmer shimmer=new Shimmer();
//                shimmer.setDuration(1000);
//                shimmer.start(normalHolder.installText);
//                shimmer.setRepeatCount(Integer.MAX_VALUE);
                break;
            case VIEW_TYPE_FUNCTION:
                FunctionAdViewHolder functionHolder = (FunctionAdViewHolder) viewHolder;
                FunctionAd functionAd = null;
                if ((batmobiNativeAds.size() + facebookNativeAds.size()) == 0) {
                    functionAd = functionAds.get(position - 1);
                } else {
                    functionAd = functionAds.get(position - 2);
                }
                switch (functionAd.getType()) {
                    case FunctionAd.APP_LOCK:
                        functionHolder.iconImg.setImageResource(R.drawable.img_ad_applocak);
                        break;
                    case FunctionAd.WIFI:
                        functionHolder.iconImg.setImageResource(R.drawable.img_ad_wifi);
                        break;
                    case FunctionAd.PERMISSION:
                        functionHolder.iconImg.setImageResource(R.drawable.img_ad_permission);
                        break;
                    case FunctionAd.VIRUS:
                        functionHolder.iconImg.setImageResource(R.drawable.img_ad_virus);
                        break;
                    case FunctionAd.SCANLE_FILE:
                        functionHolder.iconImg.setImageResource(R.drawable.img_ad_deep_scan);
                        break;
                }
                functionHolder.titleText.setText("" + functionAd.getName());
                functionHolder.markText.setText("" + functionAd.getMark());
                functionHolder.fixText.setText("" + functionAd.getFix());

                functionHolder.itemView.setTag(functionAd.getType());
                functionHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch ((int) v.getTag()) {
                            case FunctionAd.APP_LOCK:
                                AppUtils.gotoAppLockActivity(context);
                                break;
                            case FunctionAd.PERMISSION:
                                context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                                if (Build.VERSION.SDK_INT >= 23) {
                                    if (!Settings.canDrawOverlays(context)) {
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    } else {
                                        new LockPermissionsDialog(context).show();
                                    }
                                }
                                break;
                            case FunctionAd.SCANLE_FILE:
                                context.startActivity(new Intent(context, ScanFilesActivity.class));
                                break;
                            case FunctionAd.WIFI:
                                context.startActivity(new Intent(context, WifiMainActivity.class));
                                break;
                        }
                    }
                });
                break;
            case VIEW_TYPE_AD_COOPERATE:
                CooperateAdViewHolder cooperateHolder = (CooperateAdViewHolder) viewHolder;
                CooperationAd cooperationAd = null;
                if ((batmobiNativeAds.size() + facebookNativeAds.size()) == 0) {
                    cooperationAd = cooperationAds.get(position - 1 - functionAds.size());
                } else {
                    cooperationAd = cooperationAds.get(position - 2 - functionAds.size());
                }
                cooperateHolder.titleText.setText("" + cooperationAd.getName());
                cooperateHolder.markText.setText("" + cooperationAd.getMark());
                switch (cooperationAd.getType()) {
                    case CooperationAd.CLEAN:
                        cooperateHolder.iconImg.setImageResource(R.drawable.img_ad_clean);
                        break;
                    case CooperationAd.POWER:
                        cooperateHolder.iconImg.setImageResource(R.drawable.img_ad_power);
                        break;
                }
                cooperateHolder.fixText.setText(cooperationAd.getFix());
                cooperateHolder.itemView.setTag(cooperationAd);
                cooperateHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CooperationAd cooperationAd1 = (CooperationAd) v.getTag();
                        if (SystemUtil.appExist(cooperationAd1.getPackageName(), context)) {
                            Intent openCleanIntent = context.getPackageManager().getLaunchIntentForPackage(cooperationAd1.getPackageName());
                            context.startActivity(openCleanIntent);
                        } else {
                            AppUtils.gotoGoogleMarket(context, cooperationAd1.getUrl(), true);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_TRANSPARENT;
        }

        if ((batmobiNativeAds.size() + facebookNativeAds.size()) > 0) {
            //有广告
            if (position == 1) {
                //位置1为广告
                return VIEW_TYPE_AD_NORMAL;
            } else {
                //出0、1的位置处理
                if (position < functionAds.size() + 2) {
                    //有功能
                    //功能位置处理
                    return VIEW_TYPE_FUNCTION;
                } else {
                    if (position < functionAds.size() + cooperationAds.size() + 2) {
                        //内推位置处理
                        return VIEW_TYPE_AD_COOPERATE;
                    } else {
                        //非内推位置处理
                        return VIEW_TYPE_AD_NORMAL;
                    }
                }
            }
        } else {
            //无广告
            if (position < functionAds.size() + 1) {
                //有功能
                //功能位置处理
                return VIEW_TYPE_FUNCTION;
            } else {
                return VIEW_TYPE_AD_COOPERATE;
            }
        }
    }

    public void setData(List<Object> obj, ArrayList<FunctionAd> functionAds, ArrayList<CooperationAd> cooperationAds) {

        if (obj != null && obj.size() > 0) {
            if (obj.get(0) instanceof BatNativeAd) {
                isBatMobiAd = true;
                batNativeAd = (BatNativeAd) obj.get(0);
                batmobiNativeAds = batNativeAd.getAds();
            } else if (obj.get(0) instanceof NativeAd) {
                isBatMobiAd = false;
                facebookNativeAds = obj;
            }
        }

        if (functionAds != null) this.functionAds = functionAds;
        if (cooperationAds != null) this.cooperationAds = cooperationAds;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return batmobiNativeAds.size() + facebookNativeAds.size() + functionAds.size() + cooperationAds.size() + 1;
    }
}
