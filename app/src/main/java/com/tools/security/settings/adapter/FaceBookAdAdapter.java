package com.tools.security.settings.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.NativeAd;
import com.tools.security.R;
import com.tools.security.utils.imageloader.ImageLoader;

import java.util.List;

/**
 * Created by lzx on 2016/12/16.
 * email：386707112@qq.com
 * 功能：
 */

public class FaceBookAdAdapter extends RecyclerView.Adapter<FaceBookAdAdapter.FaceBookViewHolder> {

    private List<Object> mFacebookAds;
    private Context mContext;

    private ImageLoader mImageLoader;

    public FaceBookAdAdapter(List<Object> facebookAds, Context context) {
        mFacebookAds = facebookAds;
        mContext = context;
        mImageLoader = new ImageLoader(context);
    }

    @Override
    public FaceBookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_facebook_ad, null);
        return new FaceBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FaceBookViewHolder holder, int position) {
        NativeAd item = (NativeAd) mFacebookAds.get(position);

        NativeAd.Image bgImage = item.getAdCoverImage();
        NativeAd.Image iconImage = item.getAdIcon();
        String bgUrl = null;
        String iconUrl = null;
        if (bgImage != null) {
            bgUrl = bgImage.getUrl();
        }

        if (iconImage != null) {
            iconUrl = iconImage.getUrl();
        }

        String appName = item.getAdTitle();
        String click_url = item.getAdBody();

        if (iconUrl != null && !iconUrl.equals("")) {
            mImageLoader.bindBitmap(bgUrl, holder.mImageBg, 200, 200);
            holder.mClickUrl.setText(click_url);
            holder.mAppName.setText(appName);
        }
        item.registerViewForInteraction(holder.itemView);
    }

    @Override
    public int getItemCount() {
        if (mFacebookAds != null) {
            return mFacebookAds.size();
        }
        return 0;
    }

    public class FaceBookViewHolder extends RecyclerView.ViewHolder {

        private TextView mClickUrl, mAppName, mClickRoute;
        private ImageView mIcon, mImageBg;


        public FaceBookViewHolder(View itemView) {
            super(itemView);
            mClickUrl = (TextView) itemView.findViewById(R.id.click_url);
            mAppName = (TextView) itemView.findViewById(R.id.app_name);
            mClickRoute = (TextView) itemView.findViewById(R.id.click_route);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mImageBg = (ImageView) itemView.findViewById(R.id.img_bg);
        }
    }
}
