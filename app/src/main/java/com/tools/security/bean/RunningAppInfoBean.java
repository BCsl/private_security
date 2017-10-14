package com.tools.security.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by wushuangshuang on 16/9/8.
 */
public class RunningAppInfoBean implements Serializable{
    private Drawable mIcon; // 应用图标
    private String mName; // 程序名称
    private String mPkgName; // 应用包名
    private boolean mInWhite; // 是否在白名单中
    private String mMemorySize;//占用内存的大小
    private String mMemorySizeUnit;//占用内存的单位
    protected int mInvokeCount; // 使用频率
    private int mSize;

    public int getmSize() {
        return mSize;
    }

    public void setmSize(int mSize) {
        this.mSize = mSize;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getPkgName() {
        return mPkgName;
    }

    public void setPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
    }

    public boolean isInWhite() {
        return mInWhite;
    }

    public void setInWhite(boolean mInWhite) {
        this.mInWhite = mInWhite;
    }

    public int getInvokeCount() {
        return mInvokeCount;
    }

    public void setInvokeCount(int mInvokeCount) {
        this.mInvokeCount = mInvokeCount;
    }

    public String getmMemorySizeUnit() {
        return mMemorySizeUnit;
    }

    public void setmMemorySizeUnit(String mMemorySizeUnit) {
        this.mMemorySizeUnit = mMemorySizeUnit;
    }

    public String getmMemorySize() {
        return mMemorySize;
    }

    public void setmMemorySize(String mMemorySize) {
        this.mMemorySize = mMemorySize;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result +
                ((getPkgName() == null) ? 0 : getPkgName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj instanceof RunningAppInfoBean) {
            RunningAppInfoBean other = (RunningAppInfoBean) obj;

            if (other.getPkgName() != null
                    && getPkgName() != null
                    && other.getPkgName().equals(getPkgName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "RunningAppInfoBean{" +
                "mIcon=" + mIcon +
                ", mName='" + mName + '\'' +
                ", mPkgName='" + mPkgName + '\'' +
                ", mInWhite=" + mInWhite +
                ", mMemorySize='" + mMemorySize + '\'' +
                ", mMemorySizeUnit='" + mMemorySizeUnit + '\'' +
                ", mInvokeCount=" + mInvokeCount +
                ", mSize=" + mSize +
                '}';
    }

}
