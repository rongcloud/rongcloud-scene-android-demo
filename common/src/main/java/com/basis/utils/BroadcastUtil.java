package com.basis.utils;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author: BaiCQ
 * @ClassName: BroadcastUtil
 * @date: 2018/8/20
 * @Description: BroadcastUtil 广播管理者
 */
public class BroadcastUtil {

    /**
     * 注册全局广播
     * @param receiver
     * @param actions
     */
    public static void registerReceiver(BroadcastReceiver receiver, ArrayList<String> actions) {
        if (null == receiver) return;
        UIKit.getContext().registerReceiver(receiver, buildFilter(actions));
    }

    /**
     * 解绑全局广播
     *
     * @param receiver
     */
    public static void unregisterReceiver(BroadcastReceiver receiver) {
        if (null == receiver) return;
        UIKit.getContext().unregisterReceiver(receiver);
    }

    /**
     * 发送全局广播
     *
     * @param action
     * @param data
     */
    public static void sendBroadcast(String action, Serializable data) {
        UIKit.getContext().sendBroadcast(buildIntent(action, data));
    }

    /**
     * 发送有序广播
     *
     * @param action
     * @param data
     */
    public static void sendOrderedBroadcast(String action, Serializable data) {
        UIKit.getContext().sendOrderedBroadcast(buildIntent(action, data), null);
    }

    /**
     * 发送终结广播：一条广播，携带最终处理数据的接收器（必定执行）
     *
     * @param action
     * @param data
     * @param endReceiver 最终处理数据的接收器
     */
    public static void sendEndBroadcast(String action, Serializable data, BroadcastReceiver endReceiver) {
        /**
         *  intent - 要发送的广播意图；
         *  receiverPermission - 发送的广播的权限，如果是null，即认为没有，任何符合条件的接收器都能收到；
         *  resultReceiver - 最终处理数据的接收器，也就是自己定义的；
         *  scheduler - 自定义的一个handler，来处理resultReceiver的回调，（其实就是设置运行这个接收器的线程），如果为null，默认在主线程；
         *  后面三个并不重要，通常情况下一次为：-1,null,null。（Activity.RESULT_OK 即 -1）
         */
        UIKit.getContext().sendOrderedBroadcast(buildIntent(action, data), null, endReceiver, null, -1, null, null);
    }

    /**
     * 根据action 构建intent
     *
     * @param action
     * @return
     */
    private static Intent buildIntent(String action, Serializable data) {
        return new Intent().setAction(action).putExtra(UIKit.KEY_BASE, data);
    }

    /**
     * 根据action集构建意图过滤器IntentFilter
     *
     * @param actions
     * @return
     */
    private static IntentFilter buildFilter(ArrayList<String> actions) {
        IntentFilter filter = new IntentFilter();
        if (null != actions && actions.size() > 0) {
            for (String action : actions) {
                filter.addAction(action);
            }
        }
        return filter;
    }
}
