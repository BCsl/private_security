package com.tools.security.bean;

/**
 * Created by Zhizhen on 2017/1/22.
 */

public class SecurityLevel {

    private int index;
    private String mTitleStr;
    private String mDescStr;
    private boolean isEnable;

    public SecurityLevel(int index, String titleStr, String descStr) {
        this.index = index;
        this.mTitleStr = titleStr;
        this.mDescStr = descStr;
    }

    public int getIndex() {
        return index;
    }

    public String getTitleStr() {
        return mTitleStr;
    }

    public String getDescStr() {
        return mDescStr;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
