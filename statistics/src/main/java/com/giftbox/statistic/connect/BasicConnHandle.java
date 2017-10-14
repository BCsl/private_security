package com.giftbox.statistic.connect;

import android.content.Context;
import android.util.Log;

import com.giftbox.statistic.beans.PostBean;
import com.giftbox.statistic.encrypt.CryptTool;
import com.giftbox.statistic.utiltool.UtilTool;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;


public class BasicConnHandle extends BaseConnectHandle {
	// TODO 这个key是否需要更换
	public static final String STATISTICS_DATA_ENCRYPT_KEY = "BatMobi_2016";
	public static String data = "";

	public BasicConnHandle(Context context) {
		super(context);
	}

	@Override
	public void onPost(PostBean bean) {
		bean.mState = PostBean.STATE_POSTING;
		String statisticsData = null;
		StringBuilder buffer = null;
		if (bean != null) {
			buffer = buildData(bean);
		}
		if (buffer != null) {
			statisticsData = buffer.toString();
			data = statisticsData;
			try {
					statisticsData = URLEncoder.encode(statisticsData, BaseConnectHandle.STATISTICS_DATA_CODE);
					//加密
					statisticsData = CryptTool.encrypt(statisticsData, STATISTICS_DATA_ENCRYPT_KEY);
					// 压缩
					statisticsData = UtilTool.gzip(statisticsData.getBytes());
				
				if (statisticsData != null) {
					DataOutputStream out = new DataOutputStream(mUrlConn.getOutputStream());
					//out.writeBytes(statisticsData);
					out.write(statisticsData.getBytes());
					out.flush();
					out.close();
				}
				if (mUrlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					bean.mState = PostBean.STATE_POSTED;
					Log.i("TongJiTest","上传成功："+data);
				}else{
					bean.mState = PostBean.STATE_POSTFAILED;
					Log.i("TongJiTest","上传失败："+data);
				}
				mUrlConn.disconnect();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
