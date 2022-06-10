package com.basis.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.interfaces.DataObserver;
import com.basis.adapter.interfaces.IAdapte;
import com.basis.adapter.interfaces.IHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用适配器
 *
 * @param <T>
 */
public abstract class RcyAdapter<T, VH extends IHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IAdapte<T, VH> {
    protected Context context;
    private LayoutInflater inflater;
    // 布局id和itemType的映射关系
    private SparseArray<Integer> itemTypes;
    private List<T> data;
    private DataObserver observer;

    public RcyAdapter(Context context, int... itemLayoutId) {
        this.context = context;
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
        if (refreshView instanceof RecyclerView) {
            ((RecyclerView) refreshView).setAdapter(this);
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
    public synchronized void setData(List<T> list, boolean refresh) {
        if (refresh) {
            data.clear();
        }
        if (null != list) {
            data.addAll(list);
        }
        if (null != observer) {
            observer.onObserve(data.size());
        }
        notifyDataSetChanged();
    }

    @Override
    public synchronized boolean removeItem(T item) {
        if (null == data) return false;
        int i = data.indexOf(item);
        boolean flag = data.remove(item);
        notifyDataSetChanged();
        if (flag) notifyItemRemoved(i);
        if (null != observer) {
            observer.onObserve(data.size());
        }
        return flag;
    }

    @Override
    public void updateItem(T item) {
        if (null == data) return;
        int i = data.indexOf(item);
        if (i > -1) {
            notifyItemChanged(i);
        }
    }

    @Override
    public void insertItem(T item, boolean isLast) {
        if (null == data) return;
        if (isLast) {
            data.add(item);
            notifyItemInserted(data.size() - 1);
        } else {
            data.add(0, item);
            notifyItemInserted(0);
        }
    }

    @Override
    public void clear() {
        if (null == data) return;
        data.clear();
        notifyDataSetChanged();
    }

    @Override
    public RcyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = getLayoutIdByViewType(viewType);
        if (layoutId < 0) {
            throw new IllegalArgumentException("No ViewHolder Setted for ViewType =" + viewType);
        }
        return newCustomerHolder(parent, layoutId);
    }

    @Override
    public int getItemCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        int layoutId = getItemLayoutId(getItem(position), position);
        if (layoutId == -1) return -1;
        Integer type = itemTypes.get(layoutId);
        if (null == type) {
            throw new IllegalArgumentException("No ViewType Setted for position =" + position);
        }
        return type;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
//        Log.e("RcyAdapter", "onViewRecycled");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        T dt = getItem(position);
        int layout = getItemLayoutId(dt, position);
        convert((VH) holder, dt, position, layout);
    }

    /************************以上RecyclerView.Adapter的方法*********************/
    @Override
    public T getItem(int position) {
        int count = getItemCount();
        if (position < 0 || count == 0 || position >= count) return null;
        return data.get(position);
    }

    /**
     * 根据itemType 获取布局id
     */
    private int getLayoutIdByViewType(int itemType) {
        int size = itemTypes.size();
        for (int i = 0; i < size; i++) {
            int layoutId = itemTypes.keyAt(i);
            Integer type = itemTypes.get(layoutId);
            if (type == itemType) {
                return layoutId;
            }
        }
        return -1;
    }

    /**
     * 若自定义ViewHolder可以继承复写该方法
     *
     * @param parent
     * @param layoutId
     * @return
     */
    public RcyHolder newCustomerHolder(ViewGroup parent, int layoutId) {
        View itemView = inflater.inflate(layoutId, parent, false);
        return new RcyHolder(itemView);
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
     * @param holder
     * @param t
     * @param position 索引
     * @param layoutId 布局id 多种布局时返回
     */
    @Override
    public abstract void convert(VH holder, T t, int position, int layoutId);
}
