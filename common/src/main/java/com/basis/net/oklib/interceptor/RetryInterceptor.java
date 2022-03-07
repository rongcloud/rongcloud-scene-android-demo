package com.basis.net.oklib.interceptor;

import java.io.IOException;

import com.basis.net.oklib.wrapper.OkUtil;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {
    private static final String TAG = RetryInterceptor.class.getSimpleName();

    // 最大重试次数
    public int maxRetry;
    // 假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
    private int retryNum = 0;

    public RetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        OkUtil.d(TAG, "retryNum=" + retryNum);
        Response response = chain.proceed(request);
        while (!response.isSuccessful() && retryNum < maxRetry) {
            retryNum++;
            OkUtil.d(TAG, "retryNum=" + retryNum);
            response = chain.proceed(request);
        }
        return response;
    }
}