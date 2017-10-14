package com.tools.security.bean;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Author：wushuangshuang on 16/10/27 16:08
 * Function：文件缓存总的分类
 */
public class FileCacheGroup extends DataSupport{

    public static final int TYPE_LOG=1;
    public static final int TYPE_APK=2;
    public static final int TYPE_TEMP=3;

    private FileCacheBean.FileCacheType mType; // 文件类型
    private String mTitle; // 名称
    private float mTotalCacheSize; // 总共的缓存大小
    private boolean mIsChecked; // 是否被选中
    private boolean mIsLoadFinish; // 是否加载完成
    private List<FileCacheBean> mChilds;
    private int type;

    public FileCacheGroup() {
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return mTitle;
    }

    public FileCacheBean.FileCacheType getType() {
        return mType;
    }

    public void setType(FileCacheBean.FileCacheType mType) {
        this.mType = mType;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public float getTotalCacheSize() {
        return mTotalCacheSize;
    }

    public void setTotalCacheSize(float mTotalCacheSize) {
        this.mTotalCacheSize = mTotalCacheSize;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean mIsChecked) {
        this.mIsChecked = mIsChecked;
    }

    public boolean isIsLoadFinish() {
        return mIsLoadFinish;
    }

    public void setIsLoadFinish(boolean mIsLoadFinish) {
        this.mIsLoadFinish = mIsLoadFinish;
    }

    public List<FileCacheBean> getChilds() {
        return mChilds;
    }

    public void setChilds(List<FileCacheBean> mChilds) {
        this.mChilds = mChilds;
    }
}
