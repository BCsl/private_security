package com.tools.security.applock.adapter;

import android.content.pm.PackageManager;
import android.widget.Filter;

import com.tools.security.bean.CommLockInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lzx on 2017/1/8.
 * 条件过滤
 */

public class SearchFilter extends Filter {

    private List<CommLockInfo> mLockInfos = new ArrayList<>();
    private List<CommLockInfo> mCopyLockInfos = new ArrayList<>();
    private PackageManager packageManager;
    private OnPublishResultsListener mListener;

    public SearchFilter(List<CommLockInfo> lockInfos, List<CommLockInfo> copyLockInfos, PackageManager packageManager) {
        mLockInfos = lockInfos;
        mCopyLockInfos = copyLockInfos;
        this.packageManager = packageManager;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        //初始化过滤结果对象
        FilterResults results = new FilterResults();
        //假如搜索为空的时候，将复制的数据添加到原始数据，用于继续过滤操作
        if (results.values == null) {
            mLockInfos.clear();
            mLockInfos.addAll(mCopyLockInfos);
        }
        //关键字为空的时候，搜索结果为复制的结果
        if (constraint == null || constraint.length() == 0) {
            results.values = mCopyLockInfos;
            results.count = mCopyLockInfos.size();
        } else {
            String prefixString = constraint.toString();
            final int count = mLockInfos.size();
            //用于存放暂时的过滤结果
            final List<CommLockInfo> newValues = new ArrayList<CommLockInfo>();
            for (int i = 0; i < count; i++) {
                final CommLockInfo value = mLockInfos.get(i);
                String appName = packageManager.getApplicationLabel(value.getAppInfo()).toString();

                if (appName.contains(prefixString)) {
                    newValues.add(value);
                } else {
                    //过来空字符开头
                    final String[] words = appName.split(" ");
                    final int wordCount = words.length;
                    // Start at index 0, in case valueText starts with space(s)
                    for (int k = 0; k < wordCount; k++) {
                        if (words[k].contains(prefixString)) {
                            newValues.add(value);
                            break;
                        }
                    }
                }
            }
            results.values = newValues;
            results.count = newValues.size();
        }
        return results;//过滤结果
    }

    public static String exChange(String str) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append(Character.toLowerCase(c));
                } else if (Character.isLowerCase(c)) {
                    sb.append(Character.toUpperCase(c));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 转大写
     */
    public String toUpperCase(String str) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (Character.isLowerCase(c)) {
                    sb.append(Character.toUpperCase(c));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 转小写
     */
    public String toLowerCase(String str) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        return sb.toString();
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mLockInfos.clear();//清除原始数据
        if (results.values == null)
            return;
        mLockInfos.addAll((Collection<? extends CommLockInfo>) results.values);//将过滤结果添加到这个对象
        if (results.count > 0) {
            if (mListener != null) {
                mListener.notifyData();
            }
        } else {
            //关键字不为零但是过滤结果为空刷新数据
            if (constraint.length() != 0) {
                if (mListener != null) {
                    mListener.notifyData();
                }
                return;
            }
            //加载复制的数据，即为最初的数据
            if (mListener != null) {
                mListener.resetData(mCopyLockInfos);
            }
        }
    }

    public void setListener(OnPublishResultsListener listener) {
        mListener = listener;
    }

    public interface OnPublishResultsListener {
        void notifyData();

        void resetData(List<CommLockInfo> list);
    }
}
