package com.basis;

import android.content.Context;
import android.content.Intent;

/**
 * 自定义全局广播的监听
 */
public interface CustomerReceiver {
    void onReceive(Context context, Intent intent);
}