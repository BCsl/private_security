package com.tools.security.applock.db;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.tools.security.bean.CommLockInfo;
import com.tools.security.common.AppConstants;
import com.tools.security.utils.DataUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lzx on 2017/1/6.
 * 数据库管理类
 */

public class CommLockInfoManager {

    private CommLockInfoDao commLockInfoDao;
    private FaviterDao mFaviterDao;
    private PackageManager mPackageManager;
    private Context mContext;


    public CommLockInfoManager(Context mContext) {
        this.mContext = mContext;
        commLockInfoDao = new CommLockInfoDao();
        mFaviterDao = new FaviterDao();
        mPackageManager = mContext.getPackageManager();
    }

    /**
     * 检查状态是否为锁定
     *
     * @param packageName
     * @return
     */
    public boolean isLockedPackageName(String packageName) {
        if (commLockInfoDao != null) {
            List<CommLockInfo> commLockInfos = commLockInfoDao.queryCommLockInfo(packageName);
            for (CommLockInfo commLockInfo : commLockInfos) {
                if (commLockInfo.isLocked()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否设置了不锁
     */
    public boolean isSetUnLock(String packageName) {
        if (commLockInfoDao != null) {
            List<CommLockInfo> commLockInfos = commLockInfoDao.queryCommLockInfo(packageName);
            for (CommLockInfo commLockInfo : commLockInfos) {
                if (commLockInfo.isSetUnLock()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setIsUnLockThisApp(String packageName, boolean isSetUnLock) {
        if (commLockInfoDao != null) {
            commLockInfoDao.updateSetUnLockStatus(packageName, isSetUnLock);
        }
    }

    public boolean isHasCommLockInfo(String packageName) {
        List<CommLockInfo> infos = DataSupport.where("packageName = ?", packageName).find(CommLockInfo.class);
        return infos.size() > 0;
    }

    /**
     * 将手机应用信息插入数据库
     */
    public synchronized void instanceCommLockInfoTable(List<ResolveInfo> resolveInfos) throws PackageManager.NameNotFoundException {
        List<CommLockInfo> list = new ArrayList<>();

        for (ResolveInfo resolveInfo : resolveInfos) {
            boolean isfaviterApp =  mFaviterDao.isHasFaviterAppInfo(resolveInfo.activityInfo.packageName); //是否为推荐加锁的app
            CommLockInfo commLockInfo = new CommLockInfo(resolveInfo.activityInfo.packageName, false, isfaviterApp); // 后续需添加默认的开启保护
            ApplicationInfo appInfo = mPackageManager.getApplicationInfo(commLockInfo.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
            String appName = mPackageManager.getApplicationLabel(appInfo).toString();
            //过滤掉一些应用
            if (!commLockInfo.getPackageName().equals(AppConstants.APP_PACKAGE_NAME) && !commLockInfo.getPackageName().equals("com.android.settings")
                    && !commLockInfo.getPackageName().equals("com.google.android.googlequicksearchbox")) {
                if (isfaviterApp) { //如果是推荐的
                    commLockInfo.setLocked(true);
                } else {
                    commLockInfo.setLocked(false);
                }
                commLockInfo.setAppName(appName);
                commLockInfo.setSetUnLock(false);

                list.add(commLockInfo);
            }
        }
        list = DataUtil.clearRepeatCommLockInfo(list);  //去除重复数据
        commLockInfoDao.insertCommLockInfoList(list);
    }

    /**
     * 删除数据
     */
    public synchronized void deleteCommLockInfoTable(List<CommLockInfo> commLockInfos) {
        for (CommLockInfo info : commLockInfos) {
            commLockInfoDao.deletCommLockInfo(info.getPackageName());
        }
    }

    /**
     * 插入一条信息到数据库
     */
    public void insertCommLockInfoTable(String packageName) {
        boolean isfaviterApp = mFaviterDao.isHasFaviterAppInfo(packageName); //是否为推荐加锁的app
        CommLockInfo commLockInfo = new CommLockInfo(packageName, false, isfaviterApp);
        if (commLockInfoDao != null) {
            if (!commLockInfo.getPackageName().equals(AppConstants.APP_PACKAGE_NAME)
                    && !commLockInfo.getPackageName().equals("com.android.settings")
                    && !isExistPackage(commLockInfo.getPackageName())) {
                if (isfaviterApp) { //如果是推荐的
                    commLockInfo.setLocked(true);
                } else {
                    commLockInfo.setLocked(false);
                }
                commLockInfoDao.insertCommLockInfo(commLockInfo);
            }
        }
    }

    /**
     * 更新包名
     */
    public void updateLockPackageName(String packageName) {
        if (commLockInfoDao != null) {
            if (commLockInfoDao.isHasCommLockInfo(packageName))
                commLockInfoDao.updateLockPackageName(packageName);
        }
    }

    /**
     * 删除一条数据
     */
    public void deletCommLockInfo(String packageName) {
        if (commLockInfoDao != null) {
            if (commLockInfoDao.isHasCommLockInfo(packageName))
                commLockInfoDao.deletCommLockInfo(packageName);
        }
    }

    /**
     * 判断是否有存在的应用
     *
     * @param packageName
     * @return
     */
    public boolean isExistPackage(String packageName) {
        if (commLockInfoDao != null) {
            List<CommLockInfo> commLockInfos = commLockInfoDao.queryAllCommLockInfo();
            for (CommLockInfo commLockInfo : commLockInfos) {
                if (commLockInfo.getPackageName().equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 清空表
     */
    public void clearTable() {
        if (commLockInfoDao != null) {
            commLockInfoDao.clearTable();
        }
    }

    /**
     * 插入
     */
    public void insertAppToDb(List<CommLockInfo> commLockInfos) {
        if (commLockInfoDao != null) {
            commLockInfoDao.insertCommLockInfoList(commLockInfos);
        }
    }

    /**
     * 查找所有
     */
    public synchronized List<CommLockInfo> getAllCommLockInfos() {
        List<CommLockInfo> commLockInfos = new ArrayList<CommLockInfo>();
        if (commLockInfoDao != null) {
            commLockInfos = commLockInfoDao.queryAllCommLockInfo();
        }
        Collections.sort(commLockInfos, commLockInfoComparator);
        return commLockInfos;
    }

    /**
     * 更改数据库app状态为已解锁
     */
    public void unlockCommApplication(String packageName) {
        if (commLockInfoDao != null) {
            commLockInfoDao.updateLockStatus(packageName, false);
        }
    }


    /**
     * 更改数据库app状态为锁定
     */
    public void lockCommApplication(String packageName) {
        if (commLockInfoDao != null) {
            commLockInfoDao.updateLockStatus(packageName, true);
        }
    }

    /**
     * 全部变为锁状态
     */
    public void lockAllCommApplication() {
        if (commLockInfoDao != null) {
            commLockInfoDao.updateAllLockStatus(true);
        }
    }

    /**
     * 全部变为解锁状态
     */
    public void unlockAllCommApplication() {
        if (commLockInfoDao != null) {
            commLockInfoDao.updateAllLockStatus(false);
        }
    }

    /**
     * 模糊匹配
     */
    public List<CommLockInfo> queryBlurryList(String appName) {
        if (commLockInfoDao != null) {
            return commLockInfoDao.queryBlurryList(appName);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 更新应用名
     */
    public void updateAppName(String appName, String packageName) {
        if (commLockInfoDao != null) {
            commLockInfoDao.updateAppName(appName, packageName);
        }
    }

    private Comparator commLockInfoComparator = new Comparator() {

        @Override
        public int compare(Object lhs, Object rhs) {
            CommLockInfo leftCommLockInfo = (CommLockInfo) lhs;
            CommLockInfo rightCommLockInfo = (CommLockInfo) rhs;

            if (leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return -1;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return -1;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return -1;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return -1;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return 1;
            } else if (leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                return 1;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                return 1;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                return 1;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                if (leftCommLockInfo.getAppInfo() != null
                        && rightCommLockInfo.getAppInfo() != null)
                    return 1;
                else
                    return 0;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && !leftCommLockInfo.isLocked()
                    && rightCommLockInfo.isFaviterApp()
                    && rightCommLockInfo.isLocked()) {
                return 1;
            } else if (!leftCommLockInfo.isFaviterApp()
                    && leftCommLockInfo.isLocked()
                    && !rightCommLockInfo.isFaviterApp()
                    && !rightCommLockInfo.isLocked()) {
                return -1;
            }
            return 0;
        }
    };
}
