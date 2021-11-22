package com.basis.net;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.BasisHelper;
import com.basis.adapter.interfaces.DataObserver;
import com.basis.adapter.interfaces.IAdapte;
import com.basis.adapter.interfaces.IHolder;
import com.basis.net.oklib.net.NetRefresher;
import com.basis.net.oklib.net.Page;
import com.basis.net.oklib.wrapper.interfaces.IPage;
import com.basis.net.oklib.wrapper.interfaces.IResult;
import com.bcq.refresh.IRefresh;
import com.kit.UIKit;
import com.kit.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <ND> 适配器数据类型
 * @param <AD> 接口数据类型
 * @param <VH> 接口数据类型
 */
public class Controller<ND, AD, VH extends IHolder> extends NetRefresher<ND> implements DataObserver {
    private final String TAG = "Controller";
    private IOperator<ND, AD, VH> operator;
    //适配器使用功能集合 泛型不能使用 T 接口返回类型有可能和适配器使用的不一致
    private List<AD> adapterList = new ArrayList<>();
    private IAdapte<AD, VH> mAdapter;
    private IRHolder holder;

    public Controller(IRHolder holder, Class<ND> tclazz, IOperator<ND, AD, VH> operator) {
        this(holder, tclazz, operator, BasisHelper.getPage());
    }

    public Controller(IRHolder holder, Class<ND> tclazz, IOperator<ND, AD, VH> operator, Page page) {
        super(tclazz, page, operator);
        this.holder = holder;
        this.operator = operator;
        initialize();
    }

    protected RecyclerView.LayoutManager onSetLayoutManager() {
        return new LinearLayoutManager(UIKit.getContext());
    }

    private void initialize() {
        holder.getRefresh().enableRefresh(true);
        holder.getRefresh().enableLoad(true);
        if (holder.getRefresh() instanceof RecyclerView) {
            ((RecyclerView) holder.getRefresh()).setLayoutManager(onSetLayoutManager());
        }
        mAdapter = operator.onSetAdapter();
        mAdapter.setRefreshView((View) holder.getRefresh());
        //注意此处一定要在setRefreshView后，否则别覆盖
        mAdapter.setDataObserver(this);
        holder.getRefresh().setLoadListener(new IRefresh.LoadListener() {
            @Override
            public void onRefresh() {
                requestAgain(true, operator);
            }

            @Override
            public void onLoad() {
                requestAgain(false, operator);
            }
        });
        holder.getNone().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAgain(true, operator);
            }
        });
    }

    @Override
    public void onResult(IResult.ObjResult<List<ND>> result) {
        super.onResult(result);
        //处理enable load
        IPage page = result.getExtra();
        holder.getRefresh().enableLoad(page.getTotal() > current * this.page.geSize());
    }

    @Override
    public void onAfter() {
        super.onAfter();
        if (null != holder && null != holder.getRefresh()) {
            holder.getRefresh().refreshComplete();
            holder.getRefresh().loadComplete();
        }
    }

    /**
     * 设置适配器数据回调
     *
     * @param netData   接口放回数据
     * @param isRefresh 是否刷新
     */
    @Override
    public void onRefreshData(List<ND> netData, boolean isRefresh) {
        /* 当页数据转换处理 */
        List<AD> preData = operator.onTransform(netData);
        if (isRefresh) adapterList.clear();
        if (null != preData) adapterList.addAll(preData);
        /* 设置适配器前 */
        List<AD> temp = operator.onPreSetData(adapterList);
        if (null != temp && !temp.isEmpty()) {
            if (null != holder) holder.showType(IRHolder.Type.show);
            mAdapter.setData(temp, true);
        } else {
            if (null != holder) holder.showType(IRHolder.Type.none);
        }
    }

    /**
     * 因适配器 removeItem 导致数据从有到无是回调
     * BsiAdapter.OnNoDataListeren 接口
     */
    @Override
    public void onObserve(int length) {
        Logger.e(TAG, "onObserve: len = " + length);
        if (null != holder) holder.showType(length == 0 ? IRHolder.Type.none : IRHolder.Type.show);
    }
}