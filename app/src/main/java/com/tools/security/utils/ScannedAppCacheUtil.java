package com.tools.security.utils;

import android.support.v4.util.LruCache;
import android.util.Log;

import com.tools.security.bean.ScannedApp;

/**
 * description:缓存已扫描应用列表
 * author: xiaodifu
 * date: 2017/1/15.
 */

public class ScannedAppCacheUtil {
    private LruCache<String, ScannedApp> mMemoryCache;

    public ScannedAppCacheUtil() {
        mMemoryCache = new LruCache(1024 * 1024 * 4);
    }

    public void clearCache() {
        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                mMemoryCache.evictAll();
            }
            mMemoryCache = null;
        }
    }

    /**
     * 存
     *
     * @param key
     * @param bitmap
     */
    public synchronized void put(String key, ScannedApp bitmap) {
        if (mMemoryCache.get(key) == null) {
            if (key != null && bitmap != null)
                mMemoryCache.put(key, bitmap);
        } else
            Log.w("TAG", "the res is aready exits");
    }


    /**
     * 取
     *
     * @param key
     * @return
     */
    public synchronized ScannedApp get(String key) {
        ScannedApp bm = mMemoryCache.get(key);
        if (key != null) {
            return bm;
        }
        return null;
    }

    /**
     * 移除某个
     *
     * @param key
     */
    public synchronized void remove(String key) {
        if (key != null) {
            if (mMemoryCache != null) {
                ScannedApp bm = mMemoryCache.remove(key);
                bm = null;
            }
        }
    }
}
