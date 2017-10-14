package com.tools.security.utils;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.giftbox.statistic.encrypt.CryptTool;
import com.orhanobut.logger.Logger;
import com.tools.security.bean.ResponseResult;
import com.tools.security.common.AppConstants;
import com.tools.security.common.SecurityApplication;
import com.tools.security.utils.internet.HttpUtils;
import com.tools.security.utils.statistics.AppStatisticsUtils;
import com.tools.security.utils.volley.request.RequestManager;
import com.tools.security.utils.volley.request.VolleyRequest;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by lzx on 2017/1/14.
 * 网络请求类
 */

public class SecurityHttp {
    //服务器返回的状态码
    public static final int STATUS_SUCCESS = 1;//处理成功
    public static final int STATUS_SEVER_ERROR = -1;//服务器处理出错
    public static final int STATUS_CLIENT_ERROR = -2; //业务处理异常


    /*自定义状态码*/
    public static final int ERROR_VOLLEY_NULL = 20000;//Volley 返回null
    public static final int ERROR_CODE_NO_NETWROK = 20001;//网络异常
    public static final int ERROR_JSON_ERROR = 20005;//Json解析异常
    public static final int ERROR_VOLLEY_ERROR = 20006;//Volley返回错误

    //服务器返回的状态码
    public static final String ERROR_MSG_NO_NETWORK = "Internet connection failed.Please check your net work.";
    public static final String ERROR_SERVER_BUSY = "Server Busy,Please Try Again.";

    /**
     * post请求
     *
     * @param funId
     * @param params
     * @param listener
     */
    public static void doPost(int funId, Map<String, Object> params, HttpUtils.IHttpListener listener) {
        JSONObject jsonObject = new JSONObject(params);
        HttpUtils.httpAsyPost(getUrl(funId), encryptParams(jsonObject), listener);
    }

    public static void doPost(final int funId, Map<String, Object> params, final ResultListener resultListener) {
        if (!AppUtils.isConnected(SecurityApplication.getInstance())) {
            if (resultListener != null) {
                resultListener.onFailed(ERROR_CODE_NO_NETWROK, ERROR_MSG_NO_NETWORK);
            }
            return;
        }
        final JSONObject jsonObject = new JSONObject(params);
        VolleyRequest request = new VolleyRequest(Request.Method.POST, getUrl(funId), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    resultListener.onFailed(ERROR_VOLLEY_NULL, "Volley response is null");
                    return;
                }
                if (resultListener != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        ResponseResult responseResult = new ResponseResult();
                        responseResult.resolveJsonObject(jsonObject.getJSONObject("result"));
                        int status = responseResult.getStatus();
                        int errorcode = responseResult.getErrorcode();
                        if (status == STATUS_SUCCESS) {
                            resultListener.onResultWithHeader(responseResult);
                        } else {
                            resultListener.onFailed(status, responseResult.getMsg());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (resultListener != null) {
                            resultListener.onFailed(ERROR_JSON_ERROR, "Json error");
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.d("VolleyError = " + error.getMessage());
                if (error == null) {
                    resultListener.onFailed(ERROR_VOLLEY_ERROR, "error is null");
                    return;
                }
                if (resultListener != null) {
                    if (error.networkResponse != null) {
                        resultListener.onFailed(ERROR_VOLLEY_ERROR,ERROR_SERVER_BUSY);
                    } else {
                        resultListener.onFailed(ERROR_VOLLEY_ERROR, ERROR_SERVER_BUSY);
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return encryptParams(jsonObject);
            }
        };
        RequestManager.addRequest(request, getUrl(funId));
    }

    public interface ResultListener {
        /**
         * 成功返回请求头信息
         */
        void onResultWithHeader(ResponseResult responseResult);

        /**
         * 数据错误
         *
         * @param code 错误码
         * @param info 错误信息
         */
        void onFailed(int code, String info);
    }


    /**
     * 得到url
     */
    private static String getUrl(int funId) {
        String url = AppConstants.IS_TEST_SERVER ? AppConstants.TEST_SERVER : AppConstants.FORMAL_SERVER;
        url = url + funId + "&rd=" + System.currentTimeMillis();
        return url;
    }

    /**
     * 对请求加密和压缩
     *
     * @param jsonObject 请求参数
     */
    private static Map<String, String> encryptParams(JSONObject jsonObject) {
        Map<String, String> params = new HashMap<String, String>();
        try {
            //提交的json格式请求数据,加密再压缩
            jsonObject.put("phead", createRequestHead(SecurityApplication.getInstance()));
            String data = toString(jsonObject);

            data = URLEncoder.encode(data, "UTF-8");
            data = CryptTool.encrypt(data, AppConstants.PKEY);
            String temp = data;
            data = ZipUtils.gzip(data.getBytes());

            // 压缩
            params.put("data", data);
            params.put("pkey", AppConstants.PKEY);
            params.put("sign", MD5Utils.md5(AppConstants.PKEY + temp + AppConstants.PKEY.hashCode()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 得到请求头参数
     */
    private static JSONObject createRequestHead(Context context) {
        if (context == null) {
            return null;
        }
        JSONObject pHead = new JSONObject();
        try {
            //协议版本号
            pHead.put("pversion", 2);
            //手机androidId
            pHead.put("aid", AppStatisticsUtils.getAndroidId(context));

            //用户ID(以手机IMEI号作为区分)
            pHead.put("imei", AppStatisticsUtils.getIMEI(context));
            // Google Advertising Id
            pHead.put("adv_id", AppUtils.getGoogleAdvertisingId(context));
            //apAppConstant.PKEY
            pHead.put("appkey", AppConstants.SECURITY_APPKEY);
            //channel
            pHead.put("channel", AppConstants.CHANNEL);
            String local = AppStatisticsUtils.getCountry(context);
            pHead.put("local", local); // 国家(大写:CN)
            //语言(小写:en)
            pHead.put("lang", Locale.getDefault().getLanguage().toLowerCase());
            //客户端版本名
            pHead.put("sdk_name", SecurityApplication.getInstance().getVersionName());
            //客户端版本号
            pHead.put("sdk_code", SecurityApplication.getInstance().getVersionCode());
            //运营商编码
            pHead.put("imsi", AppStatisticsUtils.getImsi(context));
            //系统版本名:4.1.2
            pHead.put("sys_name", Build.VERSION.RELEASE);
            //系统版本号
            pHead.put("sys_code", Build.VERSION.SDK);
            //手机型号
            pHead.put("mode", Build.VERSION.RELEASE);
            //手机分辨率(720*1280)
            final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            final Display display = windowManager.getDefaultDisplay();
            final DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            String dpi = metrics.widthPixels + "*" + metrics.heightPixels;
            pHead.put("screen_size", dpi);
            //网络类型(unknown,wifi,gprs,3g,4g,other)
            pHead.put("net_type", AppUtils.buildNetworkState(context));
            //是否为平板
            pHead.put("is_tablet", AppUtils.getDeviceType(context));
            //ram大小
            pHead.put("ram", AppUtils.getAvailableInternalMemorySize(context));
            //rom大小
            pHead.put("rom", AppUtils.getRomSpace(context));
            //运营商
            pHead.put("operator", AppStatisticsUtils.getCarrier(context));
            //cpu个数
            pHead.put("cpu", AppStatisticsUtils.getCPU());
            //时区
            pHead.put("tz", AppStatisticsUtils.getTZ(context) + "");
            //经纬度
            //   pHead.put("latitude", PhoneUtils.getLatitude(context) + "," + PhoneUtils.getLongitude(context));
            pHead.put("latitude", "" + "," + "");
            //gamil
            pHead.put("gmail", AppUtils.getGmail(context));
            //user-agent
            pHead.put("mac", AppUtils.getMacAddress(context));
            pHead.put("ua", AppUtils.getUserAgent(context));
            //平台  0代表ios 1代表Android
            pHead.put("platform", 1);
            //是否是虚拟机
            pHead.put("is_virtual_machine", AppUtils.isEmulator(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pHead;
    }

    /**
     * 将Object类型转换为String,且去除空格
     */
    private static String toString(Object obj) {
        if (obj == null) {
            obj = "";
        }
        return obj.toString().trim();
    }


}
