package com.basis.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author: BaiCQ
 * @ClassName: SharedPreferUtil
 * @Description: SharedPreferUtil 工具类
 */
public class SharedPreferUtil {
    //sp 默认文件名
    public static String SP_FILE_NAME = "com_kit_common";

    /**
     * SharedPreferences兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }

    /**
     * 保存键值对
     *
     * @param key   key
     * @param value value
     */
    public static void set(String key, String value) {
        set(SP_FILE_NAME, key, value);
    }

    /**
     * 保存键值对
     *
     * @param key   key
     * @param value value
     */
    public static void set(String key, boolean value) {
        set(SP_FILE_NAME, key, value);
    }

    /**
     * 保存键值对
     *
     * @param fileName 文件名
     * @param key      key
     * @param value    value
     */
    public static void set(String fileName, String key, String value) {
        SharedPreferences sharedPreferences = UIKit.getContext().getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        SharedPreferencesCompat.apply(editor);
    }


    /**
     * 获取键对应的值
     *
     * @param fileName     文件名
     * @param key          key
     * @param defaultValue 默认值，无对应value时返回
     * @return value
     */
    public static String get(String fileName, String key, String defaultValue) {
        if (TextUtils.isEmpty(defaultValue)) defaultValue = "";
        return UIKit.getContext().getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS).getString(key, defaultValue);
    }

    /**
     * 保存键值对
     *
     * @param fileName 文件名
     * @param key      key
     * @param value    value
     */
    public static void set(String fileName, String key, boolean value) {
        SharedPreferences sharedPreferences = UIKit.getContext().getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 获取键对应的值
     *
     * @param fileName     文件名
     * @param key          key
     * @param defaultValue 默认值，无对应value时返回
     * @return value
     */
    public static boolean get(String fileName, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = UIKit.getContext().getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * 保存键值对
     *
     * @param fileName 文件名
     * @param key      key
     * @param value    value
     */
    public static void set(String fileName, String key, int value) {
        SharedPreferences sharedPreferences = UIKit.getContext().getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static void set(String key, int value) {
        set(SP_FILE_NAME, key, value);
    }

    /**
     * 获取键对应的值
     *
     * @param fileName     文件名
     * @param key          key
     * @param defaultValue 默认值，无对应value时返回
     * @return value
     */
    public static int get(String fileName, String key, int defaultValue) {
        SharedPreferences sharedPreferences = UIKit.getContext().getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static int get(String key, int defaultValue) {
        return get(SP_FILE_NAME, key, defaultValue);
    }

    /**
     * 获取键对应的值，找不到则返回""
     *
     * @param key key
     * @return value
     */
    public static String get(String key) {
        return get(SP_FILE_NAME, key, "");
    }

    /**
     * 获取键对应的值，找不到则返回""
     *
     * @param key key
     * @return value
     */
    public static boolean getBoolean(String key) {
        return get(SP_FILE_NAME, key, false);
    }

    /**
     * 移除key对应的项
     *
     * @param fileName 文件名
     * @param key      key
     */
    public static void remove(String fileName, String key) {
        SharedPreferences sharedPreferences = UIKit.getContext().getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     *
     * @param fileName 文件名
     */
    public static void clear(String fileName) {
        SharedPreferences sharedPreferences = UIKit.getContext().getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key对应的项是否存在
     *
     * @param fileName 文件名
     * @param key      key
     * @return 是否存在
     */
    public static boolean contatins(String fileName, String key) {
        return UIKit.getContext().getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS).contains(key);
    }

    /**
     * 返回所有键值对
     *
     * @param fileName 文件名
     * @return Map组成的键值对
     */
    public static Map<String, ?> getAll(String fileName) {
        return UIKit.getContext().getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS).getAll();
    }

}
