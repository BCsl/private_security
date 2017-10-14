package com.tools.security.applock.db;

import android.content.ContentValues;

import com.tools.security.bean.CommLockInfo;

import org.litepal.crud.DataSupport;

import java.util.List;

import static org.litepal.crud.DataSupport.updateAll;
import static org.litepal.crud.DataSupport.where;

/**
 * Created by lzx on 2017/1/6.
 */

public class CommLockInfoDao {


    /**
     * 根据包名查找
     *
     * @param packageName
     * @return
     */
    public List<CommLockInfo> queryCommLockInfo(String packageName) {
        List<CommLockInfo> lockInfos = where("packageName = ?", packageName).find(CommLockInfo.class);
        return lockInfos;
    }


    /**
     * 查找所有
     */
    public List<CommLockInfo> queryAllCommLockInfo() {
        List<CommLockInfo> lockInfos = DataSupport.findAll(CommLockInfo.class);
        return lockInfos;
    }

    /**
     * 插入应用到数据库
     */
    public void insertCommLockInfoList(List<CommLockInfo> list) {
        DataSupport.saveAll(list);
    }

    /**
     * 插入一条数据
     */
    public void insertCommLockInfo(CommLockInfo lockInfo) {
        if (!isHasCommLockInfo(lockInfo.getPackageName())) {
            lockInfo.save();
        }
    }

    /**
     * 更新一条数据
     */
    public void updateLockPackageName(String packageName) {
        ContentValues values = new ContentValues();
        values.put("packageName", packageName);
        updateAll(CommLockInfo.class, values, "packageName = ?", packageName);
    }

    /**
     * 删除一条数据
     */
    public void deletCommLockInfo(String packageName) {
        DataSupport.deleteAll(CommLockInfo.class, "packageName = ?", packageName);
    }

    /**
     * 清空数据表
     */
    public void clearTable() {
        DataSupport.deleteAll(CommLockInfo.class);
    }

    /**
     * 判断数据是否存在
     */
    public boolean isHasCommLockInfo(String packageName) {
        List<CommLockInfo> infos = where("packageName = ?", packageName).find(CommLockInfo.class);
        return infos.size() > 0;
    }

    /**
     * 根据包名更改锁的状态
     */
    public void updateLockStatus(String packageName, boolean isLock) {
        ContentValues values = new ContentValues();
        values.put("isLocked", isLock);
        DataSupport.updateAll(CommLockInfo.class, values, "packageName = ?", packageName);
    }

    /**
     * 更改全部数据状态
     */
    public void updateAllLockStatus(boolean isLock) {
        ContentValues values = new ContentValues();
        values.put("isLocked", isLock);
        updateAll(CommLockInfo.class, values);
    }

    public void updateSetUnLockStatus(String packageName, boolean isSetUnLock) {
        ContentValues values = new ContentValues();
        values.put("isSetUnLock", isSetUnLock);
        DataSupport.updateAll(CommLockInfo.class, values, "packageName = ?", packageName);
    }

    /**
     * 模糊匹配
     */
    public List<CommLockInfo> queryBlurryList(String appName) {
        List<CommLockInfo> infos = DataSupport.where("appName like ?", "%" + appName + "%").find(CommLockInfo.class);
        return infos;
    }

    /**
     * 更新应用名
     */
    public void updateAppName(String appName, String packageName) {
        ContentValues values = new ContentValues();
        values.put("appName", appName);
        DataSupport.updateAll(CommLockInfo.class, values, "packageName = ?", packageName);
    }


}
