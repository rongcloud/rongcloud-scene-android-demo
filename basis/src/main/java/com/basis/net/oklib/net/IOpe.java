package com.basis.net.oklib.net;

public interface IOpe<T> {

    void onError(int status, String errMsg);

    /**
     * Fix问题：首次使用缓存数据，并没有请求接口此时Request为空，在刷新列表后的处理请求会丢失
     * 自定义重新请求任务，刷新或加重跟多引起的
     *
     * @param refresh
     */
    void onCustomerRequestAgain(boolean refresh);
}