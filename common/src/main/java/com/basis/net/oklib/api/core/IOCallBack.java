package com.basis.net.oklib.api.core;

public interface IOCallBack<T, E> extends IOBack<T> {

    void set(E e);

    E get();
}