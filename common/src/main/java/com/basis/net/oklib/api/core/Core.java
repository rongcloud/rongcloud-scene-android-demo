package com.basis.net.oklib.api.core;

import java.io.IOException;
import java.util.Map;

import com.basis.net.oklib.wrapper.OkUtil;
import com.basis.net.oklib.wrapper.Error;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class Core {
    private static final Core core = new Core();
    private OkHttpClient mClient;
    private OkHttpClient.Builder mBuilder;
    private boolean refresh = false;

    private Core() {
        mBuilder = new OkHttpClient.Builder();
        addHttpLoggingInterceptor();
    }

    public static Core core() {
        return core;
    }

    private OkHttpClient client() {
        if (refresh || null == mClient) {
            mClient = null == mBuilder ? new OkHttpClient() : mBuilder.build();
        }
        return mClient;
    }

    public void config(OkHttpClient.Builder builder) {
        this.mBuilder = builder;
        addHttpLoggingInterceptor();
        refresh = true;
    }

    private void addHttpLoggingInterceptor() {
        if (mBuilder != null) {
            mBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
    }

    public boolean cancel(Object tag) {
        if (tag == null) return false;
        for (Call call : client().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
                return true;
            }
        }
        for (Call call : client().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
                return true;
            }
        }
        return false;
    }

    public void cancelAll() {
        for (Call call : client().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : client().dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    /**
     * @param url      请求地址
     * @param params   参数
     *                 key：String value：object（Ibody）
     * @param callBack 回调
     */
    public <T> void post(Object tag, String url, Map<String, Object> params, IOBack<T> callBack) {
        post(tag, url, Transform.param2Body(params), callBack);
    }

    /**
     * @param url      请求地址
     * @param params   参数
     *                 key：String value：object（Ibody）
     * @param callBack 回调
     */
    public <T> void delete(Object tag, String url, Map<String, Object> params, IOBack<T> callBack) {
        delete(tag, url, Transform.param2Body(params), callBack);
    }

    /**
     * @param url      请求地址
     * @param params   参数
     * @param callBack 回调
     */
    public <T> void get(Object tag, String url, Map<String, Object> params, IOBack<T> callBack) {
        Request.Builder builder = new Request.Builder()
                .url(Transform.urlAppendParam(url, params))
                .tag(tag);
        callBack.onBefore(builder);
        request(builder.build(), callBack);
    }

    public <T> void put(Object tag, String url, Object params, IOBack<T> callBack) {
        put(tag, url, Transform.param2Body(params), callBack);
    }

    protected <T> void put(Object tag, String url, RequestBody body, IOBack<T> callBack) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .tag(tag)
                .put(body);
        callBack.onBefore(builder);
        request(builder.build(), callBack);
    }

    /**
     * @param url      请求地址
     * @param body     body
     * @param callBack 回调
     */
    protected <T> void post(Object tag, String url, RequestBody body, IOBack<T> callBack) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .tag(tag)
                .post(body);
        callBack.onBefore(builder);
        request(builder.build(), callBack);
    }

    /**
     * @param url      请求地址
     * @param body     body
     * @param callBack 回调
     */
    protected <T> void delete(Object tag, String url, RequestBody body, IOBack<T> callBack) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .tag(tag)
                .delete(body);
        callBack.onBefore(builder);
        request(builder.build(), callBack);
    }

    /**
     * 真实执行请求的封装
     *
     * @param request  请求封装体
     * @param callback 回调
     */
    private final <T> void request(Request request, final IOBack<T> callback) {
        if (!Utils.isNetworkAvailable()) {
            dispatchFail(Error.NO_CONNECTED, "No Connected !", callback);
            return;
        }
        client().newCall(request)
                .enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        dispatchFail(Error.REQUEST_IO, e.getLocalizedMessage(), callback);
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) {
                        try {
                            if (call.isCanceled()) {
                                dispatchFail(Error.REQUEST_CANCEL, "Request Cancel ！", callback);
                                return;
                            }
                            if (null == callback) {
                                OkUtil.e("Core#request", "The Request No Set Callback !");
                                return;
                            }
                            T o = callback.onParse(response);
                            dispatchSuccess(o, callback);
                        } catch (Exception e) {
                            dispatchFail(Error.REQUEST_ERR, e.getLocalizedMessage(), callback);
                        } finally {
                            if (response.body() != null) {
                                response.body().close();
                            }
                        }

                    }
                });
    }

    private void dispatchFail(final int code, final String msg, final IOBack callback) {
        Dispatcher.get().dispatch(new Runnable() {
            @Override
            public void run() {
                if (null != callback) {
                    callback.onError(code, msg);
                    callback.onAfter();
                }
            }
        });
    }

    private <T> void dispatchSuccess(final T obj, final IOBack callback) {
        Dispatcher.get().dispatch(new Runnable() {
            @Override
            public void run() {
                if (null != callback) {
                    callback.onResult(obj);
                    callback.onAfter();
                }
            }
        });
    }
}
