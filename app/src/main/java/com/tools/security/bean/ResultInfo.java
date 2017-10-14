package com.tools.security.bean;

import android.graphics.drawable.Drawable;

import org.litepal.crud.DataSupport;

/**
 * description:结果页Item 数据类型
 * author: xiaodifu
 * date: 2016/12/19.
 */

public class ResultInfo extends DataSupport {

    private ResultType type;
    private AvlAppInfo appInfo;
    private Drawable icon;
    private String title;
    private String mark;
    private String count;

    public ResultInfo() {
    }

    public ResultInfo(ResultType type, AvlAppInfo appInfo, Drawable icon, String title, String mark, String count) {
        this.type = type;
        this.appInfo = appInfo;
        this.icon = icon;
        this.title = title;
        this.mark = mark;
        this.count = count;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public AvlAppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AvlAppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public ResultType getType() {
        return type;
    }

    public void setType(ResultType type) {
        this.type = type;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

}
