package com.tools.security.scanfiles.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.tools.security.utils.volley.image.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description:scanfile安全结果页adapter
 * author: xiaodifu
 * date: 2017/1/15.
 */

public class FileSafeResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_TRANSPARENT) {
            return new TransparentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_file_transparent, parent, false));
        } else if (viewType == VIEW_TYPE_FUNCTION) {
            return new FunctionAdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_funtion, parent, false));
        } else if (viewType == VIEW_TYPE_AD_COOPERATE) {
            return new CooperateAdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_cooperate, parent, false));
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
                    if (isBatMobiAd){
                        object=batmobiNativeAds.get(position-1);
                    }else {
                        object=facebookNativeAds.get(position-1);
                    }
                } else {
                    if (isBatMobiAd){
                        object=batmobiNativeAds.get(position - functionAds.size() - cooperationAds.size() - 1);
                    }else {
                        object=facebookNativeAds.get(position - functionAds.size() - cooperationAds.size() - 1);
                    }
                }
                if (object instanceof com.facebook.ads.NativeAd) {
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
                    normalHolder.nameText.setText(nativeAd.getAdTitle());
                    normalHolder.markText.setText(nativeAd.getAdBody());
                    nativeAd.registerViewForInteraction(normalHolder.itemView);
                } else if (object instanceof com.batmobi.Ad) {
                    Ad ad = (Ad) object;
                    String iconUrl = ad.getIcon();
//                    String contentUrl=ad.get
                    String appName = ad.getName();
                    String campid = ad.getCampId();
                    String click_url = ad.getDescription();
//                    if (url != null && !url.equals("")) {
//                        ImageUtils.displayNet1(url, normalHolder.img);
//                        normalHolder.camp_id.setText(campid);
//                        normalHolder.click_url.setText(click_url);
//                        normalHolder.appName.setText(appName);
//                    }
                    batNativeAd.registerView(normalHolder.itemView,ad);
                }
                break;
            case VIEW_TYPE_FUNCTION:
                FunctionAdViewHolder functionHolder = (FunctionAdViewHolder) viewHolder;
                FunctionAd functionAd = null;
                if ((batmobiNativeAds.size()+facebookNativeAds.size()) == 0) {
                    functionAd = functionAds.get(position - 1);
                } else {
                    functionAd = functionAds.get(position - 2);
                }
                break;
            case VIEW_TYPE_AD_COOPERATE:
                CooperateAdViewHolder cooperateHolder = (CooperateAdViewHolder) viewHolder;
                CooperationAd cooperationAd = null;
                if ((batmobiNativeAds.size()+facebookNativeAds.size())== 0) {
                    cooperationAd = cooperationAds.get(position - 1 - functionAds.size());
                } else {
                    cooperationAd = cooperationAds.get(position - 2 - functionAds.size());
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_TRANSPARENT;
        }

        if ((batmobiNativeAds.size()+facebookNativeAds.size()) > 0) {
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

    public void setData(ArrayList<Object> obj, ArrayList<FunctionAd> functionAds, ArrayList<CooperationAd> cooperationAds) {

        if (obj != null && obj.size() > 0) {
            if (obj.get(0) instanceof BatNativeAd) {
                isBatMobiAd = true;
                batNativeAd = (BatNativeAd) obj.get(0);
                batmobiNativeAds=batNativeAd.getAds();
            } else if (obj.get(0) instanceof com.facebook.ads.NativeAd) {
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
        return batmobiNativeAds.size()+facebookNativeAds.size() + functionAds.size() + cooperationAds.size() + 1;
    }
}
