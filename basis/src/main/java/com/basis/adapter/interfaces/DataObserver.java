package com.basis.adapter.interfaces;

/**
 * 因适配器 removeItem删除数据时
 * 从有数据变为无数据是 需通知控制器 处理ui跟新
 */
public interface DataObserver {
    void onObserve(int length);
}