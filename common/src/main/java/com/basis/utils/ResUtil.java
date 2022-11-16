package com.basis.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author: BaiCQ
 * @ClassName: ResUtil
 * @Description: 读取res目录下的内容的工具类
 */
public class ResUtil {
    public static final String LAYTOUT = "layout";
    public static final String DRAWABLE = "drawable";
    public static final String MIPMAP = "mipmap";
    public static final String MENU = "menu";
    public static final String RAW = "raw";
    public static final String ANIM = "anim";
    public static final String STRING = "string";
    public static final String STYLE = "style";
    public static final String STYLEABLE = "styleable";
    public static final String INTEGER = "integer";
    public static final String ID = "id";
    public static final String DIMEN = "dimen";
    public static final String COLOR = "color";
    public static final String BOOL = "bool";
    public static final String ATTR = "attr";

    /**
     * 根据资源名获得资源id
     *
     * @param context 上下文
     * @param name    资源名
     * @param type    资源类型
     * @return 资源id，找不到返回0
     */
    public static int getResourceId(Context context, String name, String type) {
        Resources resources = null;
        PackageManager pm = context.getPackageManager();
        try {
            resources = context.getResources();
            return resources.getIdentifier(name, type, context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 从assets目录下读取文件内容
     *
     * @param fileName 文件名
     * @return 文件字节流
     */
    public static byte[] readBytesFromAssets(String fileName) {
        InputStream is = null;
        byte[] buffer = null;
        try {
            is = UIKit.getAssets().open(fileName);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(is);
        }
        return buffer;
    }

    /**
     * 从assets目录读取文本
     *
     * @param fileName 文件名
     * @return 文本内容
     */
    public static String readStringFromAssets(String fileName) {
        String result = "";
        byte[] buffer = readBytesFromAssets(fileName);
        result = new String(buffer, StandardCharsets.UTF_8);
        return result;
    }


    /**
     * 从res/raw目录下读取文件内容
     *
     * @param rawId rawId
     * @return 文件字节流
     */
    public static byte[] readBytesFromRaw(int rawId) {
        InputStream is = null;
        byte[] buffer = null;
        try {
            is = UIKit.getResources().openRawResource(rawId);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(is);
        }
        return buffer;
    }

    /**
     * 从raw目录读取文本
     *
     * @param rawId id值
     * @return 文本内容
     */
    public static String readStringFromRaw(int rawId) {
        String result = null;
        byte[] buffer = readBytesFromRaw(rawId);
        result = new String(buffer, StandardCharsets.UTF_8);
        return result;
    }

    /**
     * 获得字符串
     *
     * @param strId 字符串id
     * @return 字符串
     */
    public static String getString(int strId) {
        return UIKit.getResources().getString(strId);
    }

    /**
     * 获得颜色
     *
     * @param colorId 颜色id
     * @return 颜色
     */
    public static int getColor(int colorId) {
        return UIKit.getResources().getColor(colorId);
    }

    /**
     * 获取Drawable
     *
     * @param drawableId
     * @return
     */
    public static Drawable getDrawable(int drawableId) {
        return UIKit.getResources().getDrawable(drawableId);
    }

    /**
     * 获取指定资源id对应的字符串数组
     *
     * @param resID 字符串数组资源ID
     * @return (测试通过)
     */
    public static String[] getStringArray(int resID) {
        return UIKit.getResources().getStringArray(resID);
    }

    /**
     * 获取指定资源id对应的整型数组
     * @param resID 整型数组资源ID
     * @return
     */
    public static int[] getIntArray(int resID) {
        return UIKit.getResources().getIntArray(resID);
    }

    /**
     * 获取指定资源id对应的尺寸
     *
     * @param resID 尺寸资源ID
     * @return 返回的单位为 px(dp * density = px)(sp * density = px)
     */
    public static float getDimension(int resID) {
        return UIKit.getResources().getDimension(resID) * getDensity();
    }

    public static float getDimensionDP(int resID) {
        return UIKit.getResources().getDimension(resID);
    }

    private static float density = -1;
    public static float getDensity() {
        if (density < 0){
            density =  UIKit.getResources().getDisplayMetrics().density;
        }
        return density;
    }

}
