package com.tools.security.utils.internet;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * <strong>HttpResponse</strong><br/>
 * <ul>
 * <strong>Constructor</strong>
 * <li>{@link HttpResponse#HttpResponse()}</li>
 * <li>{@link HttpResponse#HttpResponse(String)}</li>
 * </ul>
 * <ul>
 * <strong>Get</strong>
 * <li>{@link #getResponseBody()}</li>
 * <li>{@link #getUrl()}</li>
 * <li>{@link #getExpiredTime()} expires time</li>
 * <li>{@link #getExpiresHeader()}</li>
 * </ul>
 * <ul>
 * <strong>Setting</strong>
 * <li>{@link #setUrl(String)}</li>
 * <li>{@link #setResponseBody(String)}</li>
 * <li>{@link #setResponseHeader(String, String)}</li>
 * <li>{@link #setResponseHeaders(Map)}</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-12
 */
public class HttpResponse {
    /**
     * http url
     **/
    private String url;
    /**
     * http响应内容
     **/
    private String responseBody;
    /**
     * http相应头
     */
    private Map<String, String> responseHeaders;
    /**
     * 响应类型标记
     **/
    private int type;
    /**
     * 超时时间
     **/
    private long expiredTime;
    /**
     * 是否在客户端缓存中
     **/
    private boolean isInCache;

    private boolean isInitExpiredTime;
    /**
     * http状态码
     * <ul>
     * <li>1xx: Informational
     * <li>2xx: Success
     * <li>3xx: Redirection
     * <li>4xx: Client Error
     * <li>5xx: Server Error
     * </ul>
     */
    private int responseCode = -1;

    public HttpResponse(String url) {
        this.url = url;
        type = 0;
        isInCache = false;
        isInitExpiredTime = false;
        responseHeaders = new HashMap<String, String>();
    }

    public HttpResponse() {
        responseHeaders = new HashMap<String, String>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * get reponse code
     *
     * @return An <code>int</code> representing the three digit HTTP Status-Code.
     * <ul>
     * <li>200: Success
     * <li>10001: MalformedURLException Error
     * <li>10002: IOException Error
     * <li>other code: Information
     * </ul>
     */
    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * not avaliable now
     *
     * @return
     */
    private Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }


    public int getType() {
        return type;
    }


    public void setType(int type) {
        if (type < 0) {
            throw new IllegalArgumentException("The type of HttpResponse cannot be smaller than 0.");
        }
        this.type = type;
    }


    public void setExpiredTime(long expiredTime) {
        isInitExpiredTime = true;
        this.expiredTime = expiredTime;
    }


    public long getExpiredTime() {
        if (isInitExpiredTime) {
            return expiredTime;
        } else {
            isInitExpiredTime = true;
            return expiredTime = getExpiresInMillis();
        }
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiredTime;
    }


    public boolean isInCache() {
        return isInCache;
    }


    public HttpResponse setInCache(boolean isInCache) {
        this.isInCache = isInCache;
        return this;
    }

    public String getExpiresHeader() {
        try {
            return responseHeaders == null ? null : (String) responseHeaders.get(HttpConstants.EXPIRES);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * http cache-control in reponse header
     *
     * @return -1 represents http error or no cache-control in response headers, or max-age in seconds
     */
    private long getCacheControlMaxAge() {
        try {
            String cacheControl = (String) responseHeaders.get(HttpConstants.CACHE_CONTROL);
            if (!TextUtils.isEmpty(cacheControl)) {
                int start = cacheControl.indexOf("max-age=");
                if (start != -1) {
                    int end = cacheControl.indexOf(",", start);
                    String maxAge;
                    if (end != -1) {
                        maxAge = cacheControl.substring(start + "max-age=".length(), end);
                    } else {
                        maxAge = cacheControl.substring(start + "max-age=".length());
                    }
                    return Long.parseLong(maxAge);
                }
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * get expires
     *
     * @return <ul>
     * <li>if max-age in cache-control is exists, return current time plus it</li>
     * <li>else return expires</li>
     * <li>if something error, return -1</li>
     * </ul>
     */
    private long getExpiresInMillis() {
        long maxAge = getCacheControlMaxAge();
        if (maxAge != -1) {
            return System.currentTimeMillis() + maxAge * 1000;
        } else {
            String expire = getExpiresHeader();
            if (!TextUtils.isEmpty(expire)) {
                return HttpUtils.parseGmtTime(getExpiresHeader());
            }
        }
        return -1;
    }

    /**
     * set response header
     *
     * @param field
     * @param newValue
     */
    public void setResponseHeader(String field, String newValue) {
        if (responseHeaders != null) {
            responseHeaders.put(field, newValue);
        }
    }

    /**
     * get response header, not avaliable now
     *
     * @param field
     */
    private Object getResponseHeader(String field) {
        return responseHeaders == null ? null : responseHeaders.get(field);
    }
}
