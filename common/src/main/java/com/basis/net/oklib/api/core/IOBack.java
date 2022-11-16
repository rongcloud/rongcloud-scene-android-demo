package com.basis.net.oklib.api.core;

import okhttp3.Request;
import okhttp3.Response;

interface IOBack<T> {

    /**
     * UI Thread
     * 请求Request在build（）前执行，此处可自主添加本次请求header
     *
     * @param builder request相关的操作
     */
    void onBefore(Request.Builder builder);

    /**
     * 非UI Thread
     *
     * @param response
     * @return
     * @throws Exception
     */
    T onParse(Response response) throws Exception;

    /**
     * UI Thread
     *
     * @param progress 当前进度
     * @param total    总进度
     */
    void onProgress(float progress, long total);

    /**
     * UI Thread
     *
     * @param result
     */
    void onResult(T result);

    /**
     * UI Thread
     */
    void onError(int code, String msg);

    /**
     * UI Thread
     */
    void onAfter();
}