package com.tools.security.bean;

import android.text.TextUtils;

/**
 * Created by lzx on 2016/12/16.
 * email：386707112@qq.com
 * 功能：浏览器历史纪录
 */

public class BrowserHistory {
    /**
     * 链接
     * <p>Type: TEXT (URL)</p>
     */
    public String url;

    /**
     * 项目被访问的时间。
     * <p>Type: NUMBER</p>
     */
    public String visits;

    /**
     * 项目最后一次访问的日期，以纪元为单位的毫秒数。
     * <p>Type: NUMBER (date in milliseconds since January 1, 1970)</p>
     */
    public String date;

    public long longdate;


    /**
     * 表示项目是书签的标志。值为1表示书签，值为0表示历史项目。
     * <p>Type: INTEGER (boolean)</p>
     */
    public int bookmark;

    /**
     * 书签或历史项目的用户可见标题。
     * <p>Type: TEXT</p>
     */
    public String title;

    /**
     * 项目创建的日期，以纪元为单位的毫秒数。
     * <p>类型：NUMBER（自1970年1月1日起的日期，以毫秒为单位）</ p>
     */
    public String created;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVisits() {
        return visits;
    }

    public void setVisits(String visits) {
        this.visits = visits;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public String getTitle() {
        if (TextUtils.isEmpty(title))
            return url;
        else
            return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public long getLongdate() {
        return longdate;
    }

    public void setLongdate(long longdate) {
        this.longdate = longdate;
    }
}
