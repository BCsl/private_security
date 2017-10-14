package com.giftbox.statistic.connect;

import android.content.Context;

public class PostFactory {

	public synchronized static BaseConnectHandle produceHandle(Context context, int funid) {
		BaseConnectHandle handle = null;
		handle = new BasicConnHandle(context);
		
		return handle;
	}
}
