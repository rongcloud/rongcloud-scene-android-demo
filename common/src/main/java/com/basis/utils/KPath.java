package com.basis.utils;

import android.os.Environment;

import java.io.File;

/**
 * @author: BaiCQ
 * @ClassName: KitPath
 * @date: 2018/8/17
 * @Description: KitPath 公共路径相关的get set 方法
 */
public class KPath {
    public final static String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    //应用缓存目录[/data/data/应用包名/cache]
    public final static String CACHE_PATH = UIKit.getContext().getCacheDir().getAbsolutePath() + File.separator;

    //应用根目录名称 sd/0/kit
    public static String SD_ROOT_NAME = "kit";
    ///data/data/应用包名/cache/kit
    public static String CACHE_ROOT_NAME = "kit";

    /**
     * @param sdRoot sd卡根目录名称
     */
    public static void setSDRoot(String sdRoot) {
        KPath.SD_ROOT_NAME = sdRoot;
    }

    /**
     * @param cacheRoot data/data 应用缓存目录名称
     */
    public static void setCacheRoot(String cacheRoot) {
        KPath.CACHE_ROOT_NAME = cacheRoot;
    }


    public static String getSDRootPath() {
        String sd_root = SD_PATH + SD_ROOT_NAME + File.separator;
        File file = new File(sd_root);
        if (!file.exists()) {
            file.mkdirs();
        }
        return sd_root;
    }

    public static String getCacheRootPath() {
        String sd_root = CACHE_PATH + CACHE_ROOT_NAME + File.separator;
        File file = new File(sd_root);
        if (!file.exists()) {
            file.mkdirs();
        }
        return sd_root;
    }

    /**
     * 获取文件存储根路径：
     * 外部存储可用，返回外部存储路径:/storage/emulated/0/Android/data/包名/files
     * 外部存储不可用，则返回内部存储路径：data/data/包名/files
     */
    public static String getFilesPath() {
        String filePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用:/storage/emulated/0/Android/data/包名/files
            filePath = UIKit.getContext().getExternalFilesDir(null).getPath();
        } else {
            //外部存储不可用，内部存储路径：data/data/com.learn.test/files
            filePath = UIKit.getContext().getFilesDir().getPath();
        }
        return filePath;
    }

    /**
     * 获取文件存储根路径：
     * 外部存储可用，返回外部存储路径:/storage/emulated/0/Android/data/包名/cache
     * 外部存储不可用，则返回内部存储路径：data/data/包名/cache
     */
    public static String getCachePath() {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用：/storage/emulated/0/Android/data/包名/cache
            cachePath = UIKit.getContext().getExternalCacheDir().getPath();
        } else {
            //外部存储不可用：/data/data/com.learn.test/cache
            cachePath = UIKit.getContext().getCacheDir().getPath();
        }
        return cachePath;
    }
}
