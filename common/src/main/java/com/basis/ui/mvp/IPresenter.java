package com.basis.ui.mvp;

import androidx.lifecycle.Lifecycle;

/**
 * @author gyn
 * @date 2021/9/24
 */
public interface IPresenter<V extends IBaseView> {
    void attachView(V mView, Lifecycle lifecycle);

    void detachView();
}
