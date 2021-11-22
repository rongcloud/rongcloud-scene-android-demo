package com.basis.adapter.listview;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.basis.adapter.interfaces.DataObserver;
import com.basis.adapter.interfaces.IAdapte;
import com.basis.adapter.interfaces.IHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * @author: BaiCQ
 * @createTime: 2017/2/28 10:12
 * @className: RefreshAdapter
 * @Description: 通用适配器:支持多类型viewType
 */
public abstract class LvAdapter<T, VH extends IHolder> extends BaseAdapter implements IAdapte<T, VH> {
    protected Context mContext;
    private List<T> data;
    private LayoutInflater inflater;
    // 布局id和itemType的映射关系
    private SparseArray<Integer> itemTypes;
    private DataObserver observer;

    public LvAdapter(Context context, int... itemLayoutId) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        itemTypes = new SparseArray<>();
        data = new ArrayList<>();
        int[] ids = itemLayoutId;
        int len = ids == null ? 0 : ids.length;
        for (int i = 0; i < len; i++) {
            itemTypes.put(ids[i], i);
        }
    }

    @Override
    public <V extends View> void setRefreshView(V refreshView) {
        if (refreshView instanceof ListView) {
            ((ListView) refreshView).setAdapter(this);
        } else {
            throw new IllegalArgumentException("No Support View Type :" + refreshView.getClass().getSimpleName());
        }
    }

    @Override
    public void setDataObserver(DataObserver observer) {
        this.observer = observer;
    }

    @Override
    public List<T> getData() {
        return data;
    }

    @Override
    public synchronized final void setData(List<T> list, boolean refresh) {
        if (refresh) {
            data.clear();
        }
        if (null != list) {
            data.addAll(list);
            notifyDataSetChanged();
        }
        if (null != observer) {
            observer.onObserve(data.size());
        }
    }

    public void clear() {
        if (null != data) data.clear();
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public T getItem(int position) {
        int count = getCount();
        if (position < 0 || count == 0 || position >= count) return null;
        return data.get(position);
    }

    public synchronized boolean removeItem(T item) {
        if (null == data) return false;
        boolean flag = data.remove(item);
        if (null != observer) {
            observer.onObserve(data.size());
        }
        return flag;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        int count = itemTypes.size();
        if (count < 1) count = 1;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int layoutId = getItemLayoutId(getItem(position), position);
        Integer type = itemTypes.get(layoutId);
        if (null == type) {
            throw new IllegalArgumentException("No ViewType Setted for position =" + position);
        }
        return type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T t = getItem(position);
        int layoutId = getItemLayoutId(t, position);
        if (layoutId < 0) {
            throw new IllegalArgumentException("No ItemView Setted for posotopm =" + position);
        }
        LvHolder lvHolder;
        if (convertView == null) {
            convertView = inflater.inflate(layoutId, parent, false);
            lvHolder = new LvHolder(convertView);
            convertView.setTag(lvHolder);
        } else {
            lvHolder = (LvHolder) convertView.getTag();
        }
        convert((VH) lvHolder, t, position, layoutId);
        return lvHolder.rootView();
    }

    /**
     * 根据position 和 数据 获取itemView的布局id
     *
     * @param item
     * @param position
     * @return
     */
    @Override
    public abstract int getItemLayoutId(T item, int position);

    /**
     * 绑定数据
     *
     * @param lvHolder
     * @param t
     * @param position 索引
     * @param layoutId 布局id 多种布局时返回
     */
    @Override
    public abstract void convert(VH lvHolder, T t, int position, int layoutId);
}
