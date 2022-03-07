package com.basis.utils;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;


/**
 * Created by gyn on 2021/11/15
 */
public class UiUtils {

    /**
     * @param dp dp值
     * @return px
     */
    public static int dp2px(float dp) {
        float density = UIKit.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    /**
     * @param px px值
     * @return dp
     */
    public static int px2dp(int px) {
        float density = UIKit.getContext().getResources().getDisplayMetrics().density;
        return (int) ((px / density + 0.5f));
    }


    /**
     * 获取屏幕高度,包括状态栏
     */
    public static int getFullScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }
        return dm.heightPixels;
    }

    /**
     * 获取屏幕高度,不包括状态栏
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕中位置
     */
    public static int[] getLocation(View view) {
        int[] location = new int[2];
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            location[0] = rect.left;
            location[1] = rect.top;
        } else {
            view.getLocationOnScreen(location);
        }
        return location;
    }

}
