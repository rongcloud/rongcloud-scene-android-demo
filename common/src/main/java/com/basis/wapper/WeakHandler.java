package com.basis.wapper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 弱引用hander
 *
 * @param <T>
 */
public abstract class WeakHandler<T> extends Handler {

    private WeakReference<T> defaultt;

    public WeakHandler(T inst) {
        this(inst, Looper.getMainLooper());
    }

    public WeakHandler(T inst, Looper looper) {
        super(looper);
        this.defaultt = new WeakReference<T>(inst);
    }

    @Override
    public final void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (null == defaultt || defaultt.get() == null) return;

        handleMessage(msg, defaultt.get());

    }

    public abstract void handleMessage(Message msg, T t);
}