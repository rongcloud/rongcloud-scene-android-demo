package com.basis.adapter;

import android.content.Context;

import com.basis.adapter.interfaces.IHolder;

/**
 * 通用适配器
 *
 * @param <T>
 */
public abstract class RcySAdapter<T, VH extends IHolder> extends RcyAdapter<T, VH> {

    private int layoutId;

    public RcySAdapter(Context context, int itemLayoutId) {
        super(context, itemLayoutId);
        this.layoutId = itemLayoutId;
    }

    @Override
    public int getItemLayoutId(T item, int position) {
        return layoutId;
    }

    @Override
    public void convert(VH holder, T t, int position, int layoutId) {
        convert(holder, t, position);
    }

    public abstract void convert(VH holder, T t, int position);
}
