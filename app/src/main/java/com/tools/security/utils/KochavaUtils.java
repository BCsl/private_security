package com.tools.security.utils;

import com.tools.security.common.SecurityApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by lzx on 2016/12/13.
 * email：386707112@qq.com
 * 功能：
 */

public class KochavaUtils {
    /**
     * kochava统计方法  带参数的统计
     */
    public static void tracker(String eventName, Map<String, Object> params) {
        if (SecurityApplication.getTracker() == null) return;
        JSONObject valuePayload = new JSONObject();
        try {
            if (params != null && params.size() > 0) {
                for (String key : params.keySet()) {
                    valuePayload.put(key, params.get(key));
                }
            }
            String valueString = valuePayload.toString();
            SecurityApplication.getTracker().event(eventName, valueString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不带参数的统计
     *
     * @param eventName
     */
    public static void tracker(String eventName) {
        if (SecurityApplication.getTracker() == null) return;
        SecurityApplication.getTracker().event(eventName, "");
    }
}
