package com.basis.ui;

/**
 * @author: BaiCQ
 * @ClassName: IBasis
 * @Description: UI的接口
 */
public interface IBasis {

    /**
     * 设置布局
     */
    int setLayoutId();

    /**
     * 初始化
     */
    void init();

    /**
     * 刷新UI回调接口 供fragment刷数据使用
     *
     * @param obj
     */
    default void onRefresh(Object obj) {
    }

    /**
     * 网络变化回调
     */
    default void onNetChange() {
    }

    default void onLogout() {
    }
}
