package com.basis.wapper;

import java.util.List;

/**
 * 延迟刷新频率接口
 * 1、将单位时间内的数据按添加顺序打包，并统一流转输出上抛
 */
public interface IBuffer<T> {

    /**
     * 添加单个数据
     *
     * @param t 单个数据
     */
    void apply(T t);

    /**
     * 添加多个数据
     *
     * @param ts 数据集
     */
    void apply(List<T> ts);

    /**
     * 设置流转的时间间隔
     *
     * @param interval 流转间隔
     */
    void setInterval(long interval);

    /**
     * 设置buffer 大小 如 bufferSize > 0 在定时流转输出基础上，固定的时间间隔内，数据条数每达到设置的bufferSize会额外进行一次流转输出，并不影响定时流转输出。
     *
     * @param bufferSize buffer 大小, 默认：-1，不设置大小，只以定时间隔interval输出数据。
     */
    void setSize(int bufferSize);

    /**
     * 设置数据流转监听
     *
     * @param onOutflowListener 流转监听
     */
    void setOnOutflowListener(OnOutflowListener<T> onOutflowListener);

    /**
     * 退出队列
     */
    void release();

    /**
     * 数据流转监听
     *
     * @param <T>
     */
    interface OnOutflowListener<T> {
        void onOutflow(List<T> data);
    }
}
