package com.basis.net;

import android.view.View;

import com.bcq.refresh.IRefresh;

public interface IRHolder {

    enum Type {show, none}

    IRefresh getRefresh();

    View getNone();

    View getShow();

    void showType(Type type);
}