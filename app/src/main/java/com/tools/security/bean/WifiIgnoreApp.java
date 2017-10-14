package com.tools.security.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * description:wifi释放宽带白名单
 * author: xiaodifu
 * date: 2017/1/16.
 */

public class WifiIgnoreApp extends DataSupport {
    private long id;
    @Column(unique = true)
    private String packageName;
    private int myapp;

    public WifiIgnoreApp(int myapp, String packageName) {
        this.myapp = myapp;
        this.packageName = packageName;
    }

    public int getMyapp() {
        return myapp;
    }

    public void setMyapp(int myapp) {
        this.myapp = myapp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
