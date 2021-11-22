package cn.rong.combusis.common.utils;

import android.os.Handler;
import android.os.Looper;

public final class HandlerUtils {

    private volatile static Handler INSTANCE;

    private HandlerUtils() {
        // No instance.
    }

    public static void mainThreadPost(Runnable runnable) {
        getInstance().post(runnable);
    }

    public static void mainThreadPostDelayed(Runnable runnable, long delayMillis) {
        getInstance().postDelayed(runnable, delayMillis);
    }

    private static Handler getInstance() {
        if (INSTANCE == null) {
            synchronized (HandlerUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Handler(Looper.getMainLooper());
                }
            }
        }
        return INSTANCE;
    }
}