package com.basis.widget.interfaces;

import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public interface IDialog<T extends IDialog> {

    T setOnDismissListener(DialogInterface.OnDismissListener dismissListener);

    void dismiss();


    void show();

    /**
     * 是否正在显示
     *
     * @return true 显示，false 没显示
     */
    boolean isShowing();

    /**
     * 设置点击外部区域取消弹框
     *
     * @param outsideCancele
     */
    T setOutsideCanceled(boolean outsideCancele);

    /**
     * 设置标题
     *
     * @param titleId
     */
    T setTitle(@StringRes int titleId);

    /**
     * 设置信息
     *
     * @param msgRes
     */
    T setMessage(@StringRes int msgRes);

    /**
     * 设置信息
     *
     * @param message
     */
    T setMessage(CharSequence message);

    /**
     * @param text
     * @param onclick
     */
    T setSureButton(@StringRes int text, // 按钮文本
                    View.OnClickListener onclick);

    /**
     * 设置确定按钮
     *
     * @param text    按钮文本
     * @param color   文本颜色
     * @param bg      按钮背景
     * @param onclick 点击事件
     */
    T setSureButton(@StringRes int text, @ColorRes int color, @DrawableRes int bg, View.OnClickListener onclick);

    /**
     * 设置确定按钮
     *
     * @param text         按钮文本
     * @param color        文本颜色
     * @param bg           按钮背景
     * @param clickDismiss 点击是否消失 处理一些特殊需求 点击确定按钮后弹框不消失
     * @param onclick      点击事件
     */
    T setSureButton(@StringRes int text, @ColorRes int color, @DrawableRes int bg, boolean clickDismiss, View.OnClickListener onclick);

    /**
     * 设置取消按钮
     *
     * @param text 按钮文本
     */
    T setCancelButton(@StringRes int text);

    /**
     * 设置取消按钮
     *
     * @param text  按钮文本
     * @param color 文本颜色
     * @param bg    按钮背景
     */
    T setCancelButton(@StringRes int text, @ColorRes int color, @DrawableRes int bg);

    T setCancelButton(@StringRes int text, View.OnClickListener onclick);

    /**
     * 添加自定义Content视图组件
     *
     * @param customView
     */
    T addCustomContentView(View customView);

    /**
     * 隐藏取消按钮
     */
    T hideCancelButton();

    /**
     * 隐藏确定按钮
     */
    T hideSureButton();

    /**
     * 默认风格弹框
     *
     * @param title     是否显示默认标题
     * @param sureClick 确定按钮事件
     */
    T defalutStyle(boolean title, View.OnClickListener sureClick);

    /**
     * 取消风格弹框
     *
     * @param title 是否显示默认标题
     */
    T cancelStyle(boolean title);

    /**
     * 确认风格弹框
     *
     * @param title     是否显示默认标题
     * @param sureClick 确定按钮事件
     */
    T sureStyle(boolean title, View.OnClickListener sureClick);

    /**
     * 删除风格弹框
     *
     * @param title     是否显示默认标题
     * @param sureClick 确定按钮事件
     */
    T deleteStyle(boolean title, View.OnClickListener sureClick);
}
