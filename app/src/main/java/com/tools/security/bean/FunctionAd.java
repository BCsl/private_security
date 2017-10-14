package com.tools.security.bean;

import android.graphics.drawable.Drawable;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * description:应用内功能广告
 * author: xiaodifu
 * date: 2017/1/18.
 */

public class FunctionAd extends DataSupport {
    public static final int APP_LOCK = 1;
    public static final int WIFI = 2;
    public static final int PERMISSION = 3;
    public static final int VIRUS = 4;
    public static final int SCANLE_FILE = 5;
    public static final int RELEASING = 6;

    private long id;
    @Column(unique = true)
    private int type;
    private String name;
    private String mark;
    private String fix;
    private long last_user_time;

    public FunctionAd() {
    }

    public FunctionAd(int type, String name, String mark, String fix, long last_user_time) {
        this.type = type;
        this.name = name;
        this.mark = mark;
        this.fix = fix;
        this.last_user_time = last_user_time;
    }

    public long getLast_user_time() {
        return last_user_time;
    }

    public void setLast_user_time(long last_user_time) {
        this.last_user_time = last_user_time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getFix() {
        return fix;
    }

    public void setFix(String fix) {
        this.fix = fix;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
