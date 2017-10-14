package com.tools.security.bean;

import org.litepal.crud.DataSupport;

/**
 * description:下载文件
 * author: xiaodifu
 * date: 2017/1/8.
 */

public class DownloadFile extends DataSupport {
    private FileType fileType;
    private String name;
    private String from;
    private String absPath;
    private SafeLevel safeLevel;

    public DownloadFile(FileType fileType, String name, String from, String absPath, SafeLevel safeLevel) {
        this.fileType = fileType;
        this.name = name;
        this.from = from;
        this.absPath = absPath;
        this.safeLevel = safeLevel;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public SafeLevel getSafeLevel() {
        return safeLevel;
    }

    public void setSafeLevel(SafeLevel safeLevel) {
        this.safeLevel = safeLevel;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbsPath() {
        return absPath;
    }

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }

    public enum FileType {
        APK("APK", 1), IMAGE("IMAGE", 2), ZIP("ZIP", 3), OTHER("OTHER", 4);

        private String type;
        private int index;

        FileType(String type, int index) {
            this.type = type;
            this.index = index;
        }
    }
}
