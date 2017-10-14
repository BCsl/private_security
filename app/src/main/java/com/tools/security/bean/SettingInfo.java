package com.tools.security.bean;

/**
 * Created by Zhizhen on 2017/1/12.
 */

public class SettingInfo {

    private String title;
    private boolean isOpen;

    public SettingInfo(String title, boolean isOpen){

        this.title = title;
        this.isOpen = isOpen;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

}
