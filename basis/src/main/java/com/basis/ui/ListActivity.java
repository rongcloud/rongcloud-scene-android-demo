package com.basis.ui;

import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.interfaces.IHolder;
import com.basis.net.Controller;
import com.basis.net.IOperator;
import com.basis.net.LoadTag;
import com.basis.net.oklib.api.Method;
import com.basis.net.oklib.wrapper.interfaces.ILoadTag;
import com.basis.net.oklib.wrapper.interfaces.IParse;
import com.kit.utils.Logger;

import java.util.List;
import java.util.Map;

/**
 * @param <ND> 接口数据类型
 * @param <AD> 适配器数据类型 一般情况：和ND类型一致
 * @param <VH> 适配器的holder类型 IRefresh的类型是Listview VH是LvHolder，若是RecylerView VH是RcyHolder
 */
public abstract class ListActivity<ND, AD, VH extends IHolder> extends BaseActivity implements IOperator<ND, AD, VH>, IListRefresh<ND> {
    private Class<ND> tClass;
    private Controller<ND, AD, VH> controller;

    protected RecyclerView.LayoutManager onSetLayoutManager() {
        return new LinearLayoutManager(activity);
    }

    @Override
    public final void init() {
        tClass = (Class<ND>) ObjUtil.getTType(getClass())[0];
        RHolder holder = new RHolder(getLayout());
        controller = new Controller(holder, tClass, this) {
            @Override
            protected RecyclerView.LayoutManager onSetLayoutManager() {
                return ListActivity.this.onSetLayoutManager();
            }
        };
        initView();
    }

    /*************** IListRefresh 实现 ***************/
    @Override
    public abstract void initView();

    @Override
    public void request(String tag, String mUrl, Map<String, Object> params, Method method, boolean isRefresh) {
        if (null != controller) {
            ILoadTag itag = TextUtils.isEmpty(tag) ? null : new LoadTag(activity, tag);
            controller.request(itag, mUrl, params, null, method, isRefresh);
        }
    }

    /**
     * @param tag       进度条显示msg
     * @param isRefresh 是否刷新
     * @param mUrl      mUrl
     * @param params    参数 注意：不包含page
     * @param method    Post/get
     * @param parser    解析器
     */
    @Override
    public void request(String tag, String mUrl, Map<String, Object> params, IParse parser, Method method, boolean isRefresh) {
        if (null != controller) {
            ILoadTag itag = TextUtils.isEmpty(tag) ? null : new LoadTag(activity, tag);
            controller.request(itag, mUrl, params, parser, method, isRefresh);
        }
    }

    /**
     * 刷新适配器数据
     *
     * @param netData   接口放回数据
     * @param isRefresh 是否刷新
     */
    public void refresh(List<ND> netData, boolean isRefresh) {
        if (null != controller) controller.onRefreshData(netData, isRefresh);
    }

    /*************** IOperator 被动回调***************/
    @Override
    public void onCustomerRequestAgain(boolean refresh) {
        Logger.e(TAG, "onCustomerRequestAgain:refresh = " + refresh);
    }

    /**
     * @param netData 此次请求的数据
     */
    @Override
    public List<AD> onTransform(List<ND> netData) {
        return (List<AD>) netData;
    }

    /**
     * @param netData 设置给适配器的数据
     */
    @Override
    public List<AD> onPreSetData(List<AD> netData) {
        return netData;
    }

    @Override
    public void onError(int code, String msg) {
        Logger.e(TAG, "onError: [" + code + "] " + msg);
    }
}