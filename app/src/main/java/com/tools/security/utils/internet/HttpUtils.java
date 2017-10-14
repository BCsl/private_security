package com.tools.security.utils.internet;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/**
 * HttpUtils
 */
public class HttpUtils {

    private static final String URL_AND_PARA_SEPARATOR = "?";
    private static final String PARAMETERS_SEPARATOR = "&";
    private static final String PATHS_SEPARATOR = "/";
    private static final String EQUAL_SIGN = "=";

    private static final int CONNECT_TIME_OUT = 5000; //超时时间
    private static final int READ_TIME_OUT = 8000;

    private static final SimpleDateFormat GMT_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    private HttpUtils() {
        throw new AssertionError();
    }

    /**
     * http get 同步请求
     */
    public static HttpResponse httpGet(String httpUrl) {
        return httpGet(new HttpRequest(httpUrl));
    }

    /**
     * http get 同步请求
     */
    public static String httpGetString(HttpRequest request) {
        HttpResponse response = httpGet(request);
        return response == null ? null : response.getResponseBody();
    }

    /**
     * http get 同步请求
     */
    public static String httpGetString(String httpUrl) {
        HttpResponse response = httpGet(new HttpRequest(httpUrl));
        return response == null ? null : response.getResponseBody();
    }

    /**
     * http get 异步请求
     */
    public static void httpAsyGet(String url, IHttpListener listener) {
        new HttpStringAsyncTask(listener).execute(url);
    }

    /**
     * http get 异步请求
     */
    public static void httpGet(HttpRequest request, IHttpListener listener) {
        new HttpRequestAsyncTask(listener).execute(request);
    }

    /**
     * http post 同步
     */
    public static HttpResponse httpPost(String httpUrl) {
        return httpPost(new HttpRequest(httpUrl));
    }

    /**
     * http post 同步
     */
    public static String httpPostString(String httpUrl) {
        HttpResponse response = httpPost(new HttpRequest(httpUrl));
        return response == null ? null : response.getResponseBody();
    }

    /**
     * http post 同步
     */
    public static String httpPostString(String httpUrl, Map<String, String> parasMap) {
        HttpResponse response = httpPost(new HttpRequest(httpUrl, parasMap));
        return response == null ? null : response.getResponseBody();
    }

    /**
     * http post 异步
     */
    public static void httpAsyPost(HttpRequest request, IHttpListener listener) {
        new HttpPostRequestAsyncTask(listener).execute(request);
    }

    /**
     * http post 异步
     */
    public static void httpAsyPost(String httpUrl, Map<String, String> parasMap, IHttpListener listener) {
        new HttpPostRequestAsyncTask(listener).execute(new HttpRequest(httpUrl, parasMap));
    }

    /**
     * http get
     */
    private static HttpResponse httpGet(HttpRequest request) {
        if (request == null) {
            return null;
        }
        URL url;
        HttpURLConnection httpURLConnection = null;
        HttpResponse response = new HttpResponse(request.getUrl());
        response.setResponseHeaders(request.getRequestProperties());
        response.setExpiredTime(CONNECT_TIME_OUT);
        try {
            // 根据URL地址创建URL对象
            url = new URL(request.getUrl());
            // 获取HttpURLConnection对象
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设置请求方式，默认为GET
            httpURLConnection.setRequestMethod("GET");
            // 设置连接超时
            httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
            // 设置读取超时
            httpURLConnection.setReadTimeout(READ_TIME_OUT);
            // 响应码为200表示成功，否则失败。
            if (httpURLConnection.getResponseCode() != 200) {
                response.setResponseCode(httpURLConnection.getResponseCode());
                response.setResponseBody("request failed");
            }
            // 获取网络的输入流
            InputStream is = httpURLConnection.getInputStream();
            // 读取输入流中的数据
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = bis.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            bis.close();
            is.close();
            // 响应的数据
            byte[] responseData = baos.toByteArray();
            //返回成功数据
            response.setResponseCode(httpURLConnection.getResponseCode());
            response.setResponseBody(new String(responseData));
        } catch (MalformedURLException e) {
            response.setResponseCode(10001);
            response.setResponseBody(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            response.setResponseCode(10002);
            response.setResponseBody(e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return response;
    }

    /**
     * http post
     */
    private static HttpResponse httpPost(HttpRequest request) {
        if (request == null) {
            return null;
        }
        URL url;
        HttpURLConnection httpURLConnection = null;
        HttpResponse response = new HttpResponse(request.getUrl());  //设置url
        response.setResponseHeaders(request.getRequestProperties()); //设置响应头
        response.setExpiredTime(CONNECT_TIME_OUT);
        try {
            url = new URL(request.getUrl());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(CONNECT_TIME_OUT);
            httpURLConnection.setReadTimeout(READ_TIME_OUT);
            // 设置运行输入
            httpURLConnection.setDoInput(true);
            // 设置运行输出
            httpURLConnection.setDoOutput(true);
            // 请求的数据
            String data = request.getParas();
            // 将请求的数据写入输出流中
            OutputStream os = httpURLConnection.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            bos.write(data.getBytes());
            bos.flush();
            bos.close();
            os.close();
            if (httpURLConnection.getResponseCode() == 200) {
                response.setResponseCode(200); //设置响应代码
                InputStream is = httpURLConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = bis.read(bytes)) != -1) {
                    baos.write(bytes, 0, len);
                }
                is.close();
                bis.close();
                // 响应的数据
                byte[] responseData = baos.toByteArray();
                response.setResponseBody(new String(responseData));  //设置响应内容
            } else {
                response.setResponseCode(httpURLConnection.getResponseCode());
                response.setResponseBody("request failed");
            }
        } catch (MalformedURLException e) {
            response.setResponseCode(10001);
            response.setResponseBody(e.getMessage());
        } catch (IOException e) {
            response.setResponseCode(10002);
            response.setResponseBody(e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return response;
    }

    /**
     * join url and paras
     */
    private static String getUrlWithParas(String url, Map<String, String> parasMap) {
        StringBuilder urlWithParas = new StringBuilder(TextUtils.isEmpty(url) ? "" : url);
        String paras = joinParas(parasMap);
        if (!TextUtils.isEmpty(paras)) {
            urlWithParas.append(URL_AND_PARA_SEPARATOR).append(paras);
        }
        return urlWithParas.toString();
    }

    /**
     * join url and encoded paras
     */
    private static String getUrlWithValueEncodeParas(String url, Map<String, String> parasMap) {
        StringBuilder urlWithParas = new StringBuilder(TextUtils.isEmpty(url) ? "" : url);
        String paras = joinParasWithEncodedValue(parasMap);
        if (!TextUtils.isEmpty(paras)) {
            urlWithParas.append(URL_AND_PARA_SEPARATOR).append(paras);
        }
        return urlWithParas.toString();
    }

    /**
     * join paras
     */
    private static String joinParas(Map<String, String> parasMap) {
        if (parasMap == null || parasMap.size() == 0) {
            return null;
        }

        StringBuilder paras = new StringBuilder();
        Iterator<Map.Entry<String, String>> ite = parasMap.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) ite.next();
            paras.append(entry.getKey()).append(EQUAL_SIGN).append(entry.getValue());
            if (ite.hasNext()) {
                paras.append(PARAMETERS_SEPARATOR);
            }
        }
        return paras.toString();
    }

    /**
     * 拼接post参数
     */
    public static String joinParasWithEncodedValue(Map<String, String> parasMap) {
        StringBuilder paras = new StringBuilder("");
        if (parasMap != null && parasMap.size() > 0) {
            Iterator<Map.Entry<String, String>> ite = parasMap.entrySet().iterator();
            try {
                while (ite.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) ite.next();
                    paras.append(entry.getKey()).append(EQUAL_SIGN).append(HttpHelp.utf8Encode(entry.getValue()));
                    if (ite.hasNext()) {
                        paras.append(PARAMETERS_SEPARATOR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return paras.toString();
    }

    /**
     * append a key and value pair to url
     */
    private static String appendParaToUrl(String url, String paraKey, String paraValue) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url);
        if (!url.contains(URL_AND_PARA_SEPARATOR)) {
            sb.append(URL_AND_PARA_SEPARATOR);
        } else {
            sb.append(PARAMETERS_SEPARATOR);
        }
        return sb.append(paraKey).append(EQUAL_SIGN).append(paraValue).toString();
    }

    /**
     * parse gmt time to long
     *
     * @param gmtTime likes Thu, 11 Apr 2013 10:20:30 GMT
     * @return -1 represents exception otherwise time in milliseconds
     */
    public static long parseGmtTime(String gmtTime) {
        try {
            return GMT_FORMAT.parse(gmtTime).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * set HttpRequest to HttpURLConnection
     *
     * @param request       source request
     * @param urlConnection destin url connection
     */
    private static void setURLConnection(HttpRequest request, HttpURLConnection urlConnection) {
        if (request == null || urlConnection == null) {
            return;
        }

        setURLConnection(request.getRequestProperties(), urlConnection);
        if (request.getConnectTimeout() >= 0) {
            urlConnection.setConnectTimeout(request.getConnectTimeout());
        }
        if (request.getReadTimeout() >= 0) {
            urlConnection.setReadTimeout(request.getReadTimeout());
        }
    }

    /**
     * set HttpURLConnection property
     *
     * @param requestProperties
     * @param urlConnection
     */
    private static void setURLConnection(Map<String, String> requestProperties, HttpURLConnection urlConnection) {
        if (HttpHelp.isMapEmpty(requestProperties) || urlConnection == null) {
            return;
        }

        for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
            if (!TextUtils.isEmpty(entry.getKey())) {
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * set HttpURLConnection to HttpResponse
     *
     * @param urlConnection source url connection
     * @param response      destin response
     */
    private static void setHttpResponse(HttpURLConnection urlConnection, HttpResponse response) {
        if (response == null || urlConnection == null) {
            return;
        }
        try {
            response.setResponseCode(urlConnection.getResponseCode());
        } catch (IOException e) {
            response.setResponseCode(-1);
        }
        response.setResponseHeader(HttpConstants.EXPIRES, urlConnection.getHeaderField("Expires"));
        response.setResponseHeader(HttpConstants.CACHE_CONTROL, urlConnection.getHeaderField("Cache-Control"));
    }

    /**
     * get 异步任务类
     */
    private static class HttpStringAsyncTask extends AsyncTask<String, Void, HttpResponse> {

        private IHttpListener listener;

        public HttpStringAsyncTask(IHttpListener listener) {
            this.listener = listener;
        }

        protected HttpResponse doInBackground(String... url) {
            if (HttpHelp.isEmpty(url)) {
                return null;
            }
            return httpGet(url[0]);
        }

        protected void onPostExecute(HttpResponse httpResponse) {
            if (httpResponse.getResponseCode() == 200) {
                if (listener != null)
                    listener.onSuccess(httpResponse.getResponseBody());
            } else {
                if (listener != null) {
                    listener.onError(httpResponse.getResponseCode(), httpResponse.getResponseBody());
                }
            }
        }
    }

    /**
     * get 异步任务类
     */
    private static class HttpRequestAsyncTask extends AsyncTask<HttpRequest, Void, HttpResponse> {

        private IHttpListener listener;

        public HttpRequestAsyncTask(IHttpListener listener) {
            this.listener = listener;
        }

        protected HttpResponse doInBackground(HttpRequest... httpRequest) {
            if (HttpHelp.isEmpty(httpRequest)) {
                return null;
            }
            return httpGet(httpRequest[0]);
        }


        protected void onPostExecute(HttpResponse httpResponse) {
            if (httpResponse.getResponseCode() == 200) {
                if (listener != null)
                    listener.onSuccess(httpResponse.getResponseBody());
            } else {
                if (listener != null) {
                    listener.onError(httpResponse.getResponseCode(), httpResponse.getResponseBody());
                }
            }
        }
    }

    /**
     * post异步任务类
     */
    private static class HttpPostRequestAsyncTask extends AsyncTask<HttpRequest, Void, HttpResponse> {

        private IHttpListener listener;

        public HttpPostRequestAsyncTask(IHttpListener listener) {
            this.listener = listener;
        }

        @Override
        protected HttpResponse doInBackground(HttpRequest... httpRequest) {
            if (HttpHelp.isEmpty(httpRequest)) {
                return null;
            }
            return httpPost(httpRequest[0]);
        }

        @Override
        protected void onPostExecute(HttpResponse httpResponse) {
            super.onPostExecute(httpResponse);
            if (httpResponse.getResponseCode() == 200) {
                if (listener != null)
                    listener.onSuccess(httpResponse.getResponseBody());
            } else {
                if (listener != null) {
                    listener.onError(httpResponse.getResponseCode(), httpResponse.getResponseBody());
                }
            }
        }
    }

    /**
     * 回调接口
     */
    public interface IHttpListener {


        void onSuccess(String resultJson);

        void onError(int code, String msg);


    }


}
