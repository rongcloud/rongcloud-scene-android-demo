package com.basis.adapter.listview;

import android.content.Context;

import com.basis.adapter.interfaces.IHolder;

/**
 * @author: BaiCQ
 * @createTime: 2017/2/28 10:11
 * @className: Sample Adapter
 * @Description: 一种viewType通用adapter
 */
public abstract class LvSAdapter<T, VH extends IHolder> extends LvAdapter<T, VH> {
    private int layoutId;

    public LvSAdapter(Context context, int itemLayoutId) {
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
