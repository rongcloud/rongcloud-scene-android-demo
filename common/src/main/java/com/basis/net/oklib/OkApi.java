package com.basis.net.oklib;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.basis.net.oklib.api.Method;
import com.basis.net.oklib.api.ORequest;
import com.basis.net.oklib.api.body.BitmapBody;
import com.basis.net.oklib.api.body.FileBody;
import com.basis.net.oklib.api.callback.FileIOCallBack;
import com.basis.net.oklib.api.core.Core;
import com.basis.net.oklib.api.core.IOCallBack;
import okhttp3.OkHttpClient;

public class OkApi {

    /**
     * 设置配置信息
     *
     * @param builder
     */
    public static void config(OkHttpClient.Builder builder) {
        Core.core().config(builder);
    }

    public static ORequest<File> download(String url, Map<String, Object> params, FileIOCallBack fileCallBack) {
        return ORequest.Builder.method(Method.get)
                .url(url)
                .param(params)
                .callback(fileCallBack)
                .build()
                .request();
    }

    public static <T> ORequest<T> bitmap(String url, String key, BitmapBody bitmap, IOCallBack<T, ORequest<T>> callBack) {
        Map<String, Object> params = new HashMap<>(2);
        params.put(key, bitmap);
        return post(url, params, callBack);
    }

    public static <T> ORequest<T> file(String url, String key, FileBody fileBody, IOCallBack<T, ORequest<T>> callBack) {
        Map<String, Object> params = new HashMap<>(2);
        params.put(key, fileBody);
        return post(url, params, callBack);
    }

    public static <T> ORequest<T> get(String url, Map<String, Object> params, IOCallBack<T, ORequest<T>> callBack) {
        return ORequest.Builder.method(Method.get)
                .url(url)
                .param(params)
                .callback(callBack)
                .build()
                .request();
    }

    public static <T> ORequest<T> post(String url, Map<String, Object> params, IOCallBack<T, ORequest<T>> callBack) {
        return ORequest.Builder.method(Method.post)
                .url(url)
                .param(params)
                .callback(callBack)
                .build()
                .request();
    }

    public static <T> ORequest<T> put(String url, Map<String, Object> params, IOCallBack<T, ORequest<T>> callBack) {
        return ORequest.Builder.method(Method.put)
                .url(url)
                .param(params)
                .callback(callBack)
                .build()
                .request();
    }

    public static <T> ORequest<T> delete(String url, Map<String, Object> params, IOCallBack<T, ORequest<T>> callBack) {
        return ORequest.Builder.method(Method.delete)
                .url(url)
                .param(params)
                .callback(callBack)
                .build()
                .request();
    }

}
