package com.basis.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;


/**
 * @author: BaiCQ
 * @ClassName: ScreenUtil
 * @Description: 屏幕相关的工具类
 */
public class ScreenUtil {
    private final static String TAG = "ScreenUtil";
    //默认全面高宽比例系数
    private final static double All_SCREEN = 2.0;
    private static Point scPoint;

    /**
     * 获取设备的point，封装有屏幕尺寸信息
     * @return
     */
    public static Point getScreenPoint() {
        if (scPoint == null) {
            scPoint = new Point(0, 0);
            WindowManager wm = (WindowManager) UIKit.getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getSize(scPoint);
        }
        return scPoint;
    }

    public static int getScreenWidth() {
        return getScreenPoint().x;
    }

    public static int getScreenHeight() {
        return getScreenPoint().y;
    }

    /**
     * 否是全面屏
     * @return
     */
    public static boolean isFullScreen(){
        if (null == scPoint){
            scPoint = getScreenPoint();
        }
        double ratio = scPoint.y / (double) scPoint.x;
        Logger.e(TAG,"width = "+ scPoint.x + " height = "+ scPoint.y + " ratio = "+ratio);
        return ratio >= All_SCREEN;
    }
}
