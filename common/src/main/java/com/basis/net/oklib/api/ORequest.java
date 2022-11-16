package com.basis.net.oklib.api;

import android.text.TextUtils;

import java.util.Map;

import com.basis.net.oklib.api.core.Core;
import com.basis.net.oklib.api.core.IOCallBack;
import com.basis.net.oklib.wrapper.OkUtil;

public class ORequest<T> {
    private final static String TAG = "ORequest";
    public Object tag;
    public String url;
    public Map<String, Object> param;
    public IOCallBack<T, Object> callBack;
    private Method method;

    private ORequest() {
    }

    /**
     * 输出参数
     *
     * @param url
     * @param method
     * @param params
     */
    private static void logParams(String url, String method, Map<String, Object> params) {
        OkUtil.e(TAG, " : ------------- start --------------------");
        OkUtil.e(TAG, " : url = " + url + "  method = " + method);
        int size = null == params ? 0 : params.size();
        if (size > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                OkUtil.e(TAG, " : " + entry.getKey() + " = " + OkUtil.obj2Json(entry.getValue()));
            }
        } else {
            OkUtil.e(TAG, " : 参数【无】");
        }
        OkUtil.e(TAG, " : -------------   end --------------------");
    }

    public Map<String, Object> param() {
        return param;
    }

    public void cancel() {
        Core.core().cancel(tag);
    }

    public ORequest<T> request() {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("Request Fail For Request Url is Null !");
        }
        if (null == callBack) {
            OkUtil.e(TAG, "The Request No Callback， Are You Set It ?");
        }
        logParams(url, method.name(), param);
        //设置request
        if (null != callBack) callBack.set(this);
        if (Method.post == method) {
            Core.core().post(tag, url, param, callBack);
        } else if (Method.get == method) {
            Core.core().get(tag, url, param, callBack);
        } else if (Method.delete == method) {
            Core.core().delete(tag, url, param, callBack);
        } else if (Method.put == method) {
            Core.core().put(tag, url, param, callBack);
        }
        return this;
    }

    @Override
    public String toString() {
        return "ReQuest{" +
                "url='" + url + '\'' +
                ", param=" + param +
                ", callBack=" + callBack +
                ", method='" + method + '\'' +
                '}';
    }

    /**
     * ReQuest.Builder
     *
     * @param <T>
     */
    public static class Builder<T> {
        private Object tag;
        private String url;
        private Map<String, Object> param;
        private IOCallBack<T, ORequest<T>> callBack;
        private Method method;

        private Builder() {
        }

        public static Builder method(Method method) {
            Builder builder = new Builder();
            builder.method = method;
            return builder;
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder param(Map<String, Object> param) {
            this.param = param;
            return this;
        }

        public Builder callback(IOCallBack<T, ORequest<T>> callBack) {
            this.callBack = callBack;
            return this;
        }

        public ORequest build() {
            ORequest ORequest = new ORequest();
            ORequest.method = method;
            ORequest.tag = null != tag ? tag : url + method;
            ORequest.url = url;
            ORequest.param = param;
            ORequest.callBack = callBack;
            return ORequest;
        }
    }
}
