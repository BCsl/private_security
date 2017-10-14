package com.tools.security.bean;

import android.graphics.drawable.Drawable;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * description:内推应用AD
 * author: xiaodifu
 * date: 2017/1/18.
 */

public class CooperationAd extends DataSupport{

    public static final int CLEAN = 1;
    public static final int POWER= 2;

    @Column(unique = true)
    private int type;
    private String name;
    private String mark;
    private String fix;
    private String url;
    private String packageName;

    public CooperationAd() {
    }

    public CooperationAd(int type, String name, String mark, String fix, String url, String packageName) {
        this.type = type;
        this.name = name;
        this.mark = mark;
        this.fix = fix;
        this.url = url;
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFix() {
        return fix;
    }

    public void setFix(String fix) {
        this.fix = fix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
