package com.basis.net.oklib.api.callback;


import com.basis.net.oklib.api.OCallBack;
import okhttp3.Response;

public abstract class StringIOCallBack extends OCallBack<String> {
    @Override
    public String onParse(Response response) throws Exception {
        return response.body().string();
    }
}
