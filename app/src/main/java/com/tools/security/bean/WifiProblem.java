package com.tools.security.bean;

import org.litepal.crud.DataSupport;

/**
 * description:wifi扫描结果
 * author: xiaodifu
 * date: 2017/1/17.
 */

public class WifiProblem extends DataSupport {
    public static final int TYPE_CONNECT = 0;
    public static final int TYPE_CAPTIVE = 1;
    public static final int TYPE_ARP = 2;
    public static final int TYPE_DEVICE = 3;
    public static final int TYPE_MITM = 4;
    public static final int TYPE_ENCRITION = 5;
    public static final int TYPE_SPEED = 6;


    private int result;
    private String name;
    private int type;


    public WifiProblem() {
    }

    public WifiProblem(int result, String name, int type) {
        this.result = result;
        this.name = name;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
