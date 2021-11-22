package com.basis.net;

import android.view.View;

import com.bcq.refresh.IRefresh;

public interface IRHolder {

    IRefresh getRefresh();

    View getNone();

    View getShow();

    void showType(Type type);

    enum Type {show, none}
}