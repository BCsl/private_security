package com.tools.security.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * description:wifi状态
 * author: xiaodifu
 * date: 2017/1/19.
 */

public class WifiState extends DataSupport {

    //安全
    public static final int TYPE_SAFE = 1;
    //风险
    public static final int TYPE_RISK = 2;
    //危险
    public static final int TYPE_DANGER = 3;
    //未知（未扫描过）
    public static final int TYPE_UNKNOW = 4;

    private String ssid;
    private String bssid;
    private int type;

    public WifiState() {
    }

    public WifiState(String ssid, String bssid, int type) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.type = type;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
