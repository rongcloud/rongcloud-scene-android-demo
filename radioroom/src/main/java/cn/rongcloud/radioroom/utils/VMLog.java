package cn.rongcloud.radioroom.utils;

import android.util.Log;

import cn.rongcloud.rtc.base.RTCErrorCode;
import io.rong.imlib.IRongCoreEnum;

public class VMLog {
    private static boolean debug = true;

    public static void setDebug(boolean debug) {
        VMLog.debug = debug;
    }

    public static void v(String TAG, String message) {
        if (debug) Log.v(TAG, message);
    }

    public static void d(String TAG, String message) {
        if (debug) Log.d(TAG, message);
    }

    public static void e(String TAG, String message) {
        if (debug) Log.e(TAG, message);
    }

    public static void e(String TAG, String method, RTCErrorCode e) {
        if (debug)
            Log.e(TAG, method + ": 【code】" + e.getValue() + " reason ：" + e.getReason());
    }

    public static void e(String TAG, String method, IRongCoreEnum.CoreErrorCode e) {
        if (debug)
            Log.e(TAG, method + ": 【code】" + e.code + " reason ：" + e.msg);
    }
}
