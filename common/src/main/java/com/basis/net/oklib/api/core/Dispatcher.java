package com.basis.net.oklib.api.core;

import android.os.Handler;
import android.os.Looper;

/**
 * UI Thread 分发器
 */
public class Dispatcher {
    private static final Dispatcher instance = new Dispatcher();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private Dispatcher() {
    }

    public static Dispatcher get() {
        return instance;
    }


    public void dispatch(Runnable runnable) {
        handler.post(runnable);
    }

    public void dispatch(Runnable runnable, int delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }
}