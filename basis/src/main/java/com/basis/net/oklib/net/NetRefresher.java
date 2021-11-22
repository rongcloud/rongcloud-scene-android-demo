package com.basis.net.oklib.net;

import com.basis.net.oklib.api.Method;
import com.basis.net.oklib.api.ORequest;
import com.basis.net.oklib.wrapper.GeneralWrapperCallBack;
import com.basis.net.oklib.wrapper.OkUtil;
import com.basis.net.oklib.wrapper.interfaces.BusiCallback;
import com.basis.net.oklib.wrapper.interfaces.ILoadTag;
import com.basis.net.oklib.wrapper.interfaces.IPage;
import com.basis.net.oklib.wrapper.interfaces.IParse;
import com.basis.net.oklib.wrapper.interfaces.IResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class NetRefresher<T> implements BusiCallback<IResult.ObjResult<List<T>>, List<T>, IPage, T> {
    public final static String TAG = "NetRefresher";
    protected int current = 0;//当前页的索引
    //由于使用request.request() 此处在回调callback不能配置死，由控制器动态维护
    //和最后一次请求绑定
    protected boolean refresh = false;//是否刷新标识
    protected Page page;
    protected IOpe operator;
    private Class<T> tClass;
    private ORequest<List<T>> oRequest;

    public NetRefresher(Class<T> clazz, Page page, IOpe operator) {
        this.tClass = clazz;
        this.page = page;
        this.operator = operator;
        current = page.getFirstIndex();
    }

    /**
     * 相同参数再次请求数据  根据refresh 修改 currentPage
     *
     * @param refresh
     */
    protected final void requestAgain(boolean refresh, final IOpe operator) {
        if (null != oRequest) {
            Map<String, Object> params = oRequest.param();
            if (null != params && null != page && params.containsKey(page.getKeyPage())) {
                current = refresh ? page.getFirstIndex() : Integer.valueOf(params.get(page.getKeyPage()).toString()) + 1;
                params.put(page.getKeyPage(), current);
            }
            this.refresh = refresh;
            oRequest = oRequest.request();
        } else {
            if (null != operator) {
                operator.onCustomerRequestAgain(refresh);
            } else {
                OkUtil.e(TAG, "Because Of The Processor is null, The Request To Refresh Data will be Discarded !");
            }
        }
    }

    /**
     * 请求数据
     *
     * @param dialog    ILoadTag load视图
     * @param url       地址
     * @param params    参数
     * @param parser    自定义数据解析器
     * @param method    get/post
     * @param isRefresh 刷新标识
     */
    public final void request(ILoadTag dialog,
                              String url,
                              Map<String, Object> params,
                              IParse parser,
                              Method method,
                              boolean isRefresh) {
        if (null != operator && null != page) {//operator 不为空 需要分页处理
            if (isRefresh) current = page.getFirstIndex();
            if (null == params) params = new HashMap<>(2);
            if (!params.containsKey(page.getKeyPage())) {
                params.put(page.getKeySize(), page.geSize());
                params.put(page.getKeyPage(), current);
            }
        }
        this.refresh = isRefresh;
//        ORequest = Request.requestForPage(dialog, url, params, parser, method, this);
        GeneralWrapperCallBack<IResult.WrapResult<List<T>, Page>, List<T>, Page, T> generalCallBack = new GeneralWrapperCallBack<>(
                dialog, parser, this
        );
        oRequest = ORequest.Builder.method(method)
                .url(url)
                .param(params)
                .callback(generalCallBack)
                .build()
                .request();
    }

    /************ BsiCallBack ***********/

    @Override
    public void onResult(IResult.ObjResult<List<T>> result) {
        if (null != operator) {
            onRefreshData(result.getResult(), refresh);//注意 此处不是使用的isRefresh
        }
    }

    @Override
    public void onError(int code, String errMsg) {
        OkUtil.e(TAG, "onError: [" + code + "] " + errMsg);
        if (null != operator) {
            onRefreshData(null, refresh);
            operator.onError(code, errMsg);
        }
    }

    @Override
    public void onAfter() {
        OkUtil.e(TAG, "onAfter");
    }

    @Override
    public Class<T> onGetType() {
        return tClass;
    }

    protected abstract void onRefreshData(List<T> data, boolean refresh);

}