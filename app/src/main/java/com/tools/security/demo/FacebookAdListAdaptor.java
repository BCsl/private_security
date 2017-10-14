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
import com.facebook.ads.NativeAd;
import com.tools.security.R;
import com.tools.security.utils.volley.image.ImageUtils;

import java.util.List;


public class FacebookAdListAdaptor extends BaseAdapter {
	private Context context;

	private List<Object> mFacebookAds;

	static class ViewHolder {
		TextView click_url;
		NetworkImageView img;
		NetworkImageView img_bg;
		TextView appName;
		TextView click_route;
	}

	public FacebookAdListAdaptor(Context context, List<Object> facebookAds) {
		this.context = context;
		mFacebookAds = facebookAds;
	}

	@Override
	public int getCount() {
		if (mFacebookAds != null) {
			return mFacebookAds.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int index) {

		return mFacebookAds.get(index);
	}

	@Override
	public long getItemId(int index) {

		return index;
	}

	@Override
	public View getView(final int index, View contentView, ViewGroup root) {
		ViewHolder viewHolder = null;
		if (contentView == null) {
			viewHolder = new ViewHolder();
			contentView = LayoutInflater.from(context).inflate(R.layout.facebook_itmeview, null);

			viewHolder.click_url = (TextView) contentView.findViewById(R.id.click_url);
			viewHolder.appName = (TextView) contentView.findViewById(R.id.app_name);
			viewHolder.click_route = (TextView) contentView.findViewById(R.id.click_route);
			viewHolder.img = (NetworkImageView) contentView.findViewById(R.id.icon);
			viewHolder.img_bg = (NetworkImageView) contentView.findViewById(R.id.img_bg);
			contentView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) contentView.getTag();
		}

		NativeAd item = (NativeAd) mFacebookAds.get(index);

		NativeAd.Image bgImage = item.getAdCoverImage();
		NativeAd.Image iconImage = item.getAdIcon();
		Log.e("TAG","ad getAdBody="+item.getAdBody());
		Log.e("TAG","ad getAdCallToAction="+item.getAdCallToAction());
		Log.e("TAG","ad getAdChoicesLinkUrl="+item.getAdChoicesLinkUrl());
		Log.e("TAG","ad getAdSocialContext="+item.getAdSocialContext());
		Log.e("TAG","ad getAdSubtitle="+item.getAdSubtitle());
		Log.e("TAG","ad getAdTitle="+item.getAdTitle());
		Log.e("TAG","ad getId="+item.getId());
		Log.e("TAG","ad getAdStarRating="+item.getAdStarRating());
		Log.e("TAG","ad getAdViewAttributes="+item.getAdViewAttributes());
		String bgUrl = null;
		String iconUrl = null;
		if (bgImage != null) {
			bgUrl = bgImage.getUrl();
			Log.e("TAG","AD bgUrl="+bgUrl);
		}

		if (iconImage != null) {
			iconUrl = iconImage.getUrl();
			Log.e("TAG","AD iconUrl="+iconUrl);
		}

		String appName = item.getAdTitle();
		String click_url = item.getAdBody();
		Log.i("wss", "iconUrl = " + iconUrl);
		Log.i("wss", "bgUrl = " + bgUrl);
		if (iconUrl != null && !iconUrl.equals("")) {
			ImageUtils.displayNet1(iconUrl,viewHolder.img);
			ImageUtils.displayNet1(bgUrl,viewHolder.img_bg);
			viewHolder.click_url.setText(click_url);
			viewHolder.appName.setText(appName);
		}
		item.registerViewForInteraction(contentView);
		return contentView;
	}
	
}
