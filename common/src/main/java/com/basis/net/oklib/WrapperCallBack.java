package com.basis.net.oklib;


import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Map;

import com.basis.net.oklib.api.OCallBack;
import com.basis.net.oklib.wrapper.OkHelper;
import com.basis.net.oklib.wrapper.OkUtil;
import com.basis.net.oklib.wrapper.Wrapper;

import okhttp3.Request;
import okhttp3.Response;

public abstract class WrapperCallBack extends OCallBack<Wrapper> {//IOCallBack
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onBefore(Request.Builder builder) {
        if (null == builder) return;
        if (null != OkHelper.get().getHeadCacher()) { //添加header
            Map<String, String> hs = OkHelper.get().getHeadCacher().onAddHeader();
            if (null == hs || hs.isEmpty()) return;
            for (Map.Entry<String, String> en : hs.entrySet()) {
                if (!TextUtils.isEmpty(en.getValue())) {
                    builder.addHeader(en.getKey(), en.getValue());
                }
            }
        }
    }

    @Override
    public Wrapper onParse(Response response) throws Exception {
        Wrapper wrapper = new Wrapper();
        wrapper.setCode(response.code());
        try {
            String string = response.body().string();
            OkUtil.e("Wrapper", "string = " + string);
            if (!TextUtils.isEmpty(string)) {
                JsonObject result = JsonParser.parseString(string).getAsJsonObject();
                if (null != result) {
                    if (result.has("code")) {
                        wrapper.setCode(result.get("code").getAsInt());
                    }
                    if (result.has("msg")) {
                        wrapper.setMessage(result.get("msg").getAsString());
                    }
                    if (result.has("data")) {
                        wrapper.setBody(result.get("data"));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wrapper;
    }

    @Override
    public void onProgress(float progress, long total) {
    }

    @Override
    public void onAfter() {
        Log.e(TAG, "onAfter:");
    }

    @Override
    public abstract void onResult(Wrapper result);

    @Override
    public void onError(int code, String msg) {
        Log.e(TAG, "onError:[" + code + "] message = " + msg);
    }
}
