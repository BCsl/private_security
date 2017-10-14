package com.tools.security.bean;

import org.litepal.crud.DataSupport;

/**
 * description:下载防护的bean
 * author: xiaodifu
 * date: 2017/1/9.
 */

public class DownloadScanPath extends DataSupport{
    private String path;
    private String name;
    private String from;

    public DownloadScanPath(String path, String name, String from) {
        this.path = path;
        this.name = name;
        this.from = from;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
