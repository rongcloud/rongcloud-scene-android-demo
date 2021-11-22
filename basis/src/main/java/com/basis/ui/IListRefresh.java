package com.basis.ui;

import com.basis.net.oklib.api.Method;
import com.basis.net.oklib.wrapper.interfaces.IParse;

import java.util.List;
import java.util.Map;

/**
 * 列表展示ui组件的统一主动处理接口
 *
 * @param <T> 网络数据解析封装实体
 */
public interface IListRefresh<T> {

    /**
     * init 视图组件
     */
    void initView();

    /**
     * 手动刷新适配器数据
     *
     * @param netData   接口放回数据
     * @param isRefresh 是否刷新
     */
    void refresh(List<T> netData, boolean isRefresh);

    /**
     * 请求列表数据
     *
     * @param tag       进度条显示msg
     * @param api       mUrl
     * @param params    参数 注意：不包含page
     * @param method    Post/get
     * @param isRefresh 是否刷新
     */
    void request(String tag, String api, Map<String, Object> params, Method method, boolean isRefresh);

    /**
     * 请求列表数据
     *
     * @param tag       进度条显示msg
     * @param api       mUrl
     * @param params    参数 注意：不包含page
     * @param method    Post/get
     * @param parser    解析器 自定义解析器
     * @param isRefresh 是否刷新
     */
    void request(String tag, String api, Map<String, Object> params, IParse parser, Method method, boolean isRefresh);
}
