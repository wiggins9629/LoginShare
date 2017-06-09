package com.yunjin.microlove.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.yunjin.microlove.app.MyApplication;

/**
 * @Description App相关工具类
 * @Author 一花一世界
 */

public class ApkUtil {

    public static Context getContext() {
        return MyApplication.getContext();
    }

    /**
     * @Description 获取应用程序名称
     */
    public static String getAppName() {
        try {
            PackageManager packageManager = getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return getContext().getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
