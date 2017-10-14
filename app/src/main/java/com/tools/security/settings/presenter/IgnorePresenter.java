package com.tools.security.settings.presenter;

import android.content.Context;
import android.content.pm.PackageManager;

import com.tools.security.bean.AppWhitePaper;
import com.tools.security.bean.AvlAppInfo;
import com.tools.security.utils.DataUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * description:
 * author: xiaodifu
 * date: 2016/12/14.
 */

public class IgnorePresenter implements IgnoreContract.Presenter {

    private IgnoreContract.View view;

    public IgnorePresenter(IgnoreContract.View view) {
        this.view = view;
    }

    @Override
    public void loadData(Context context) {
        List<AppWhitePaper> list = DataSupport.findAll(AppWhitePaper.class);
        PackageManager packageManager = context.getPackageManager();
        Iterator<AppWhitePaper> whitePaperIterator = list.iterator();
        //若应用已经卸载，则去除
        while (whitePaperIterator.hasNext()) {
            AppWhitePaper whitePaper = whitePaperIterator.next();
            try {
                if (packageManager.getPackageInfo(whitePaper.getPkgName(), 0) == null) {
                    whitePaper.delete();
                    AvlAppInfo.deleteAll(AvlAppInfo.class, "pkgName = ? ", "'" + whitePaper.getPkgName() + "'");
                    whitePaperIterator.remove();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                AvlAppInfo.deleteAll(AvlAppInfo.class, "pkgName = ? ", "'" + whitePaper.getPkgName() + "'");
                whitePaper.delete();
                whitePaperIterator.remove();
            }
        }
        //去重
//        if (list == null) list = new ArrayList<>();
//        list = DataUtil.clearRepeatWhitePaper(list);
//        AppWhitePaper.deleteAll(AppWhitePaper.class);
//        AppWhitePaper.saveAll(list);
        view.refresh(list);
    }
}
