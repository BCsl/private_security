package com.tools.security.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.tools.security.bean.BrowserHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lzx on 2016/12/16.
 * email：386707112@qq.com
 * 功能：浏览器相关操作工具类
 * <p>
 * chrome浏览器
 * "content://com.android.chrome.browser/bookmarks";
 * "content://com.chrome.beta.browser/bookmarks";
 * "content://com.chrome.dev.browser/bookmarks";
 */

public class BrowserUtils {

    /**
     * 获取浏览器历史记录	包括date title url
     */
    @SuppressLint("SimpleDateFormat")
    public static List<BrowserHistory> getBrowserHistory(Context mContext) {
        List<BrowserHistory> browserHistory = new ArrayList<>();
        List<BrowserHistory> histories = getBrowserHistory(mContext, Uri.parse("content://browser/bookmarks"), false);
        List<BrowserHistory> chromeHistories = getBrowserHistory(mContext, Uri.parse("content://com.android.chrome.browser/bookmarks"), false);
        browserHistory.addAll(histories);
        browserHistory.addAll(chromeHistories);

        return browserHistory;
    }

    /**
     * 记录总数
     */
    public static int getBrowserHistoryCount(Context mContext) {
        List<BrowserHistory> histories = getBrowserHistory(mContext, Uri.parse("content://browser/bookmarks"), true);
        List<BrowserHistory> chromeHistories = getBrowserHistory(mContext, Uri.parse("content://com.android.chrome.browser/bookmarks"), true);
        return histories.size() + chromeHistories.size();
    }

    /**
     * 只拿系统浏览器和谷歌浏览器数据
     */
    public static List<BrowserHistory> getBrowserHistoryOriginal(Context mContext) {
        List<BrowserHistory> browserHistory = new ArrayList<>();
        List<BrowserHistory> histories = getBrowserHistory(mContext, Uri.parse("content://browser/bookmarks"), true);
        List<BrowserHistory> chromeHistories = getBrowserHistory(mContext, Uri.parse("content://com.android.chrome.browser/bookmarks"), true);
        browserHistory.addAll(histories);
        browserHistory.addAll(chromeHistories);
        return browserHistory;
    }

    /**
     * 清空所有数据
     */
    public static boolean clearBrowserHistory(Context mContext) {
        int deleteNum = clearBrowserHistory(mContext, Uri.parse("content://browser/bookmarks"));
        int chromeDeleteNum = clearBrowserHistory(mContext, Uri.parse("content://com.android.chrome.browser/bookmarks"));

        if (deleteNum > 0 || chromeDeleteNum > 0) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static List<BrowserHistory> getBrowserHistory(Context mContext, Uri uri, boolean isOriginal) {
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
        List<BrowserHistory> browserHistoryList = new ArrayList<>();
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, "date desc");

            Date visits_d = null, date_d = null;
            long visits_l = 0, date_l = 0;

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String url = cursor.getString(cursor.getColumnIndex("url"));
                    if (isOriginal) {
                        visits_l = cursor.getLong(cursor.getColumnIndex("visits"));
                        date_l = cursor.getLong(cursor.getColumnIndex("date"));
                    } else {
                        visits_d = new Date(cursor.getLong(cursor.getColumnIndex("visits")));
                        date_d = new Date(cursor.getLong(cursor.getColumnIndex("date")));
                    }
                    int bookmark = cursor.getInt(cursor.getColumnIndex("bookmark"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    BrowserHistory history = new BrowserHistory();
                    history.url = url;
                    if (isOriginal) {
                        history.visits = String.valueOf(visits_l);
                        history.longdate = date_l;
                    } else {
                        history.visits = sfd.format(visits_d);
                        history.date = sfd.format(date_d);
                    }
                    history.bookmark = bookmark;
                    history.title = title;
                    if (history.bookmark == 0) {
                        browserHistoryList.add(history);
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return browserHistoryList;
    }

    private static int clearBrowserHistory(Context mContext, Uri uri) {
        try {
            ContentResolver contentResolver = mContext.getContentResolver();
            int deleteRowNum = contentResolver.delete(uri, null, null);
            return deleteRowNum;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
