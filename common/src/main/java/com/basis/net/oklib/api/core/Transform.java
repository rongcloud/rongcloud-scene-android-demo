package com.basis.net.oklib.api.core;

import android.net.Uri;
import android.util.Log;

import java.util.Map;

import com.basis.net.oklib.api.FormType;
import com.basis.net.oklib.api.body.IBody;
import com.basis.net.oklib.wrapper.OkUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * ok core 转换工具类
 */
public class Transform {
    public final static MediaType json = MediaType.parse("application/json; charset=utf-8");

    private static FormType formType = FormType.json;

    public static void setFormType(FormType formType) {
        Transform.formType = formType;
    }

    private static MultipartBody.Builder param2Builder(Map<String, Object> params) {
        if (null != params && !params.isEmpty()) {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof IBody) {//IBody 类型
                    IBody body = (IBody) value;
                    builder.addFormDataPart(key, body.name(), body.body());
                } else {
                    builder.addFormDataPart(entry.getKey(), entry.getValue().toString());
                }
            }
            return builder;
        }
        return null;
    }

    private static MultipartBody.Builder param2Builder(String key, Object value) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (null != value) {
            if (value instanceof IBody) {//IBody 类型
                IBody body = (IBody) value;
                builder.addFormDataPart(key, body.name(), body.body());
            } else {
                builder.addFormDataPart(key, value.toString());
            }
        }
        return builder;
    }

    /**
     * @param params Map参数
     * @return RequestBody
     */
    public static RequestBody param2Body(Map<String, Object> params) {
        boolean hasBody = false;
        if (null != params) {
            for (Object v : params.values()) {
                if (v instanceof IBody) {
                    hasBody = true;
                    break;
                }
            }
        }
        if (hasBody) {
            return param2Builder(params).build();
        }
        switch (formType) {
            case form:
                return param2Builder(params).build();
            default:
                return RequestBody.Companion.create(OkUtil.obj2Json(params), json);
        }
    }

    public static RequestBody param2Body(Object params) {
        String jsonParams = OkUtil.obj2Json(params);
        Log.e("Transform", "params : " + jsonParams);
        return RequestBody.create(json, jsonParams);
    }

    /**
     * @param key   参数key
     * @param value 参数值
     * @return RequestBody
     */
    public static RequestBody param2Body(String key, Object value) {
        return param2Builder(key, value).build();
    }

    /**
     * get请求时：在url上拼接参数
     *
     * @param url    原url
     * @param params 参数map
     * @return 拼接参数后的url
     */
    public static String urlAppendParam(String url, Map<String, Object> params) {
        if (null == url || null == params || params.isEmpty()) return url;
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String v = entry.getValue() == null ? "" : entry.getValue().toString();
            builder.appendQueryParameter(entry.getKey(), v);
        }
        return builder.build().toString();
    }

}
