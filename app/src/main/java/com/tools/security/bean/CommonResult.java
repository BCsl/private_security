package com.tools.security.bean;

import java.io.Serializable;

/**
 * description:公共结果页头部数据
 * author: xiaodifu
 * date: 2017/1/18.
 */

public class CommonResult implements Serializable {

    private String title;
    private String mark;
    private int functionType;
    private int headerHeight;

    public CommonResult(String title, String mark, int functionType, int headerHeight) {
        this.title = title;
        this.mark = mark;
        this.functionType = functionType;
        this.headerHeight = headerHeight;
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

    public int getFunctionType() {
        return functionType;
    }

    public void setFunctionType(int functionType) {
        this.functionType = functionType;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }
}
