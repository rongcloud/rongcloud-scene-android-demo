package com.basis.wapper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import com.basis.utils.Logger;
import com.basis.utils.UIKit;

import java.util.ArrayList;
import java.util.List;

public class RCRefreshBuffer<T> extends HandlerThread implements IBuffer<T> {
    private final static String TAG = "RCRefreshBuffer";
    private final static Object lock = new Object();
    private final static int WHAT_LOOP = 100001;
    private final static int WHAT_APPLY = 100002;
    // 默认间隔300ms
    private final static long DEF_INTERVAL = 300;

    private final List<T> _caches = new ArrayList<>();
    private Handler _handler;
    private long interval;
    private int bufferSize = -1;
    private OnOutflowListener<T> onOutflowListener;


    public RCRefreshBuffer(long interval) {
        super(TAG);
        this.interval = interval < 1 ? DEF_INTERVAL : interval;
        start();
        _handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                int what = msg.what;
                if (WHAT_LOOP == what) {
//                    Logger.e(TAG, "from loop");
                    handleOutflow();
                    loop(true);
                } else if (WHAT_APPLY == what) {
//                    Logger.e(TAG, "from apply");
                    handleOutflow();
                }
            }
        };
        loop(true);// 默认开启轮训
    }

    private void loop(boolean loop) {
        if (null != _handler) {
            if (!loop) {
                _handler.sendEmptyMessageDelayed(WHAT_APPLY, 0);
            } else {
                _handler.removeMessages(WHAT_LOOP);
                _handler.sendEmptyMessageDelayed(WHAT_LOOP, interval);
            }
        }
    }

    @Override
    public void apply(T t) {
        if (null != t) {
            synchronized (lock) {
                _caches.add(t);
            }
        }
        if (bufferSize > 0) {
            int count = _caches.size();
            if (0 == count % bufferSize) {
                loop(false);
            }
        }
    }

    @Override
    public void apply(List<T> ts) {
        int count = null == ts ? 0 : ts.size();
        if (count > 0) {
            synchronized (lock) {
                for (int i = 0; i < count; i++) {
                    T t = ts.get(i);
                    if (null != t) _caches.add(t);
                }
            }
        }
        if (bufferSize > 0) {
            int cs = _caches.size();
            if (0 == cs % bufferSize) {
                loop(false);
            }
        }
    }

    @NonNull
    private List<T> getCaches() {
        int count = _caches.size();
        List<T> result = new ArrayList<>();
        if (count > 0) {
            synchronized (lock) {
                result.addAll(_caches);
                _caches.clear();
            }
        }
        return result;
    }

    void handleOutflow() {
        List<T> caches = getCaches();
        if (null != onOutflowListener && !caches.isEmpty()) {
            Logger.e(TAG, "handleOutflow: count = " + caches.size());
            UIKit.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onOutflowListener.onOutflow(caches);
                }
            });
        }
    }

    @Override
    public void setInterval(long interval) {
        this.interval = interval < 1 ? DEF_INTERVAL : interval;
    }

    @Override
    public void setSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void setOnOutflowListener(OnOutflowListener<T> onOutflowListener) {
        this.onOutflowListener = onOutflowListener;
    }

    @Override
    public void release() {
        quitSafely();
    }
}