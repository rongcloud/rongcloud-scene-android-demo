package com.basis.ui.mvp;

/**
 * @author gyn
 * @date 2021/9/24
 */
public interface IBaseView {

    void showLoading(String msg);

    void dismissLoading();

    void showEmpty();

    void showToast(String message);
}
