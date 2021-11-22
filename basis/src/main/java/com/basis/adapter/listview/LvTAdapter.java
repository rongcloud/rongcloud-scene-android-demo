package com.basis.adapter.listview;

import android.content.Context;

import com.basis.adapter.interfaces.IHolder;

/**
 * @author: BaiCQ
 * @createTime: 2017/2/28 10:09
 * @className: TopAdapter
 * @Description: position = 0 是特殊处理的 通用adapter
 */
public abstract class LvTAdapter<T, VH extends IHolder> extends LvAdapter<T, VH> {
    protected int topLayoutId;
    protected int layoutId;

    private LvTAdapter(Context context, int layoutId, int topId) {
        super(context, layoutId, topId);
        this.topLayoutId = topId;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public T getItem(int position) {
        if (position == 0) return null;
        return super.getItem(position - 1);
    }

    @Override
    public int getItemLayoutId(T item, int position) {
        if (position == 0) {
            return topLayoutId;
        } else {
            return layoutId;
        }
    }

    @Override
    public void convert(VH holder, T t, int position, int layoutId) {
        convert(holder, t, position);
        if (layoutId == topLayoutId) {
            convertTop(holder, t);
        } else {
            convert(holder, t, position);
        }
    }

    protected abstract int convertTop(VH lvHolder, Object item);

    public abstract void convert(VH holder, T t, int position);
}
