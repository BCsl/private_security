package com.tools.security.applock.db;

import com.tools.security.bean.FaviterInfo;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by lzx on 2017/1/7.
 */

public class FaviterDao {

    /**
     * 插入一条数据
     *
     * @param packageName
     * @return
     */
    public void addNewFaviterApp(String packageName) {
        if (!isHasFaviterAppInfo(packageName)) {
            FaviterInfo faviterInfo = new FaviterInfo();
            faviterInfo.setPackageName(packageName);
            faviterInfo.save();
        }
    }

    /**
     * 判断数据是否存在
     */
    public boolean isHasFaviterAppInfo(String packageName) {
        List<FaviterInfo> infos = DataSupport.where("packageName = ?", packageName).find(FaviterInfo.class);
        return infos.size() > 0;
    }

    /**
     * 清空数据表
     */
    public void clearTable() {
        DataSupport.deleteAll(FaviterInfo.class);
    }

    /**
     * 插入一个列表到数据库
     */
    public void insertFaviterInfoList(List<FaviterInfo> list) {
        clearTable();
        DataSupport.saveAll(list);
    }
}
