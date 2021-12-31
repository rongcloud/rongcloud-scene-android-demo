package com.meihu.beauty.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * Created by cxf on 2017/10/30.
 * 获取屏幕尺寸
 */

public class ScreenDimenUtil {

    private static ScreenDimenUtil sInstance;
    private int mStatusBarHeight;//状态栏高度
    private int mContentHeight;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenRealHeight;

    private ScreenDimenUtil() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) MhDataManager.getInstance().getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        display.getRealMetrics(dm);
        mScreenRealHeight = dm.heightPixels;

        //网上找的办法，使用反射在DecoderView未绘制出来之前计算状态栏的高度
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            mStatusBarHeight = MhDataManager.getInstance().getContext().getResources().getDimensionPixelSize(x);
            mContentHeight = mScreenHeight - mStatusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ScreenDimenUtil getInstance() {
        if (sInstance == null) {
            synchronized (ScreenDimenUtil.class) {
                if (sInstance == null) {
                    sInstance = new ScreenDimenUtil();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取屏幕的宽度
     *
     * @return
     */
    public int getScreenWdith() {
        return mScreenWidth;
    }

    /**
     * 获取屏幕的高度
     *
     * @return
     */
    public int getScreenHeight() {
        return mScreenHeight;
    }

    /**
     * 获取ContentView的高度
     *
     * @return
     */
    public int getContentHeight() {
        return mContentHeight;
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        return mStatusBarHeight;
    }

}
