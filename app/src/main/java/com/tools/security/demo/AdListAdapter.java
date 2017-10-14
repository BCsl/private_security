package com.tools.security.demo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.batmobi.Ad;
import com.batmobi.BatNativeAd;
import com.tools.security.R;
import com.tools.security.utils.volley.image.ImageUtils;

import java.util.List;


class AdListAdapter extends BaseAdapter {
    private Context context;

    private BatNativeAd mNativeAd;

    private static class ViewHolder {
        TextView camp_id;
        TextView click_url;
        NetworkImageView img;
        TextView appName;
        TextView click_route;
    }

    AdListAdapter(Context context, BatNativeAd nativeAd) {
        this.context = context;
        mNativeAd = nativeAd;
        RequestQueue mQueue = Volley.newRequestQueue(context);
    }

    @Override
    public int getCount() {
        if (mNativeAd != null && mNativeAd.getAds() != null) {
            return mNativeAd.getAds().size();
        }
        return 0;
    }

    @Override
    public Object getItem(int index) {

        return mNativeAd.getAds().get(index);
    }

    @Override
    public long getItemId(int index) {

        return index;
    }

    @Override
    public View getView(final int index, View contentView, ViewGroup root) {
        ViewHolder viewHolder;
        if (contentView == null) {
            viewHolder = new ViewHolder();
            contentView = LayoutInflater.from(context).inflate(R.layout.aditem, null);

            viewHolder.camp_id = (TextView) contentView.findViewById(R.id.camp_id);
            viewHolder.click_url = (TextView) contentView.findViewById(R.id.click_url);
            viewHolder.appName = (TextView) contentView.findViewById(R.id.app_name);
            viewHolder.click_route = (TextView) contentView.findViewById(R.id.click_route);
            viewHolder.img = (NetworkImageView) contentView.findViewById(R.id.icon);
            contentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) contentView.getTag();
        }

        Ad item = mNativeAd.getAds().get(index);

        Log.e("TAG","ad getDescription="+item.getDescription());
        Log.e("TAG","ad getName="+item.getName());
        Log.e("TAG","ad getCampId="+item.getCampId());
        Log.e("TAG","ad getInstalls="+item.getInstalls());
        Log.e("TAG","ad getPackageName="+item.getPackageName());
        Log.e("TAG","ad getRecommendMessage="+item.getRecommendMessage());
        Log.e("TAG","ad getAppType="+item.getAppType());
        Log.e("TAG","ad getStoreRating="+item.getStoreRating());
        Log.e("TAG","ad getRate="+item.getRate());
        Log.e("TAG","ad getIcon="+item.getIcon());
        String url = item.getIcon();

        String appName = item.getName();
        String campid = item.getCampId();
        String click_url = item.getDescription();
        if (url != null && !url.equals("")) {
            ImageUtils.displayNet1(url,viewHolder.img);

            viewHolder.camp_id.setText(campid);
            viewHolder.click_url.setText(click_url);
            viewHolder.appName.setText(appName);
        }
        mNativeAd.registerView(contentView, item);

        return contentView;
    }

}
