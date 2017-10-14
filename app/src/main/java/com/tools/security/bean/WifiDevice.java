package com.tools.security.bean;

/**
 * description:Wifi连接设备
 * author: xiaodifu
 * date: 2017/1/13.
 */

public class WifiDevice {
    private String name;
    private String brand;
    private String ip;
    private String mac;

    public WifiDevice(String name, String brand, String ip, String mac) {
        this.name = name;
        this.brand = brand;
        this.ip = ip;
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
