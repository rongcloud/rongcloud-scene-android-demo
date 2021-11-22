package com.basis.adapter;

import android.content.Context;

import com.basis.adapter.interfaces.IHolder;


public abstract class SampleAdapter<T, VH extends IHolder> extends RefreshAdapter<T, VH> {
    private int layoutId;

    public SampleAdapter(Context context, int layoutId) {
        super(context, layoutId);
        this.layoutId = layoutId;
    }

    @Override
    public int getItemLayoutId(T item, int position) {
        return layoutId;
    }

    @Override
    public abstract void convert(VH iHolder, T t, int position, int layoutId);
}
