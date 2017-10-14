package com.giftbox.statistic.connect;

//CHECKSTYLE:OFF

import android.content.Context;

import com.giftbox.statistic.StatisticsManager;
import com.giftbox.statistic.beans.PostBean;
import com.giftbox.statistic.utiltool.UtilTool;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

public abstract class BaseConnectHandle {
	private static final int HTTP_REQUEST_TIMEOUT = 30 * 1000;
	public static final int RET_ERRO_NONE = 0;
	public static final int RET_ERRO_EXCEPTION = 1;
	public static final int RET_ERRO_MALFORMEDURLEXCEPTION = 2;
	// TODO 调试地址需要更换
	private static final String POST_DATA_DEBUG_URL = "http://sts.batmobi.net/statLog?ptl=10&is_zip=1";

	protected HttpURLConnection mUrlConn;
	protected Context mContext;
	public static final String JSON_REPONSE_RESULT = "upload_status";
	public static final String JSON_REPONSE_RESULT_OK = "OK";
	public static final String JSON_IS_DISPLAY_MARKET = "is_display_market";
	public static final String CONTROL_STATUS = "control_status";
	public static final String STATISTICS_DATA_CODE = "UTF-8";
	public static final String BASIC_CONTROL = "&is_response_json=1";
	public static final String UPLOAD_URL = "http://sts.batmobi.net/commonstat";

	public synchronized void postData(PostBean bean) {
		if (BaseConnectHandle.RET_ERRO_MALFORMEDURLEXCEPTION == prepareConnect(
				bean.mFunId, bean.mData)) {// 外界传入错误的url直接忽略该条数据
			bean.mState = PostBean.STATE_POSTFAILED;
			return;
		}
		onPost(bean);
	}

	public StringBuilder buildData(PostBean bean) {
		StringBuilder builder = new StringBuilder(bean.mData);
		PostBean tmp = bean.mNext;
		while (tmp != null && tmp.mData != null) {
			builder.append("\r\n");
			builder.append(tmp.mData);
			tmp = tmp.mNext;
		}

		return builder;
	}

	/**
	 * <br>
	 * 功能简述:真正上传数据接口 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param bean
	 */
	public abstract void onPost(PostBean bean);

	public BaseConnectHandle(Context context) {
		mContext = context;
	}

	public int prepareConnect(int funid, String urlString) {
		int ret = RET_ERRO_NONE;
		try {
			URL url = null;
			if (StatisticsManager.getInstance(mContext).getDebugMode()) {
				url = new URL(POST_DATA_DEBUG_URL);
			} else {
				url = new URL(BaseConnectHandle.UPLOAD_URL);
			}

			if (funid == StatisticsManager.URL_RQUEST_FUNID) {
				url = new URL(urlString);
			}

			Proxy proxy = null;
			HttpURLConnection urlConn;
			if (UtilTool.isCWWAPConnect(mContext)
					&& UtilTool.getNetWorkType(mContext) != UtilTool.NETTYPE_UNICOM) {
				try {
					if (UtilTool.getNetWorkType(mContext) == UtilTool.NETTYPE_TELECOM) {
						@SuppressWarnings("deprecation")
						String proxyHost = android.net.Proxy.getDefaultHost();
						@SuppressWarnings("deprecation")
						int port = android.net.Proxy.getDefaultPort();
						proxy = new Proxy(Proxy.Type.HTTP,
								new InetSocketAddress(proxyHost, port));
					} else {
						// 联通的3gwap经测试不需设置代理
						String host = UtilTool.getProxyHost(mContext);
						int port = UtilTool.getProxyPort(mContext);
						proxy = new Proxy(Proxy.Type.HTTP,
								new InetSocketAddress(host, port));
					}
				} catch (Exception e) {
				}
			}
			if (proxy != null) {
				urlConn = (HttpURLConnection) url.openConnection(proxy);
			} else {
				urlConn = (HttpURLConnection) url.openConnection();
			}
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setRequestMethod("POST");
			urlConn.setUseCaches(false);
			urlConn.setInstanceFollowRedirects(true);
			urlConn.setConnectTimeout(HTTP_REQUEST_TIMEOUT);
			urlConn.setReadTimeout(HTTP_REQUEST_TIMEOUT);

			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			mUrlConn = urlConn;
		} catch (MalformedURLException e) {
			ret = RET_ERRO_MALFORMEDURLEXCEPTION;
		} catch (Exception e) {
			e.printStackTrace();
			ret = RET_ERRO_EXCEPTION;
		}

		return ret;
	}
}
