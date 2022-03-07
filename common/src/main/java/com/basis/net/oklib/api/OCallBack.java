package com.basis.net.oklib.api;

import com.basis.net.oklib.api.core.Dispatcher;
import com.basis.net.oklib.api.core.IOCallBack;
import okhttp3.Request;
import okhttp3.Response;

public class OCallBack<R> implements IOCallBack<R, ORequest<R>> {
    private ORequest<R> ORequest;

    @Override
    public void onBefore(Request.Builder request) {
    }

    @Override
    public void onProgress(float progress, long total) {
    }

    @Override
    public R onParse(Response response) throws Exception {
        return null;
    }

    @Override
    public void onResult(R result) {
    }

    @Override
    public void onError(int code, String msg) {

    }

    @Override
    public void onAfter() {
    }

    /**
     * 分发UI Thread
     *
     * @param run
     */
    protected void dispatch(Runnable run) {
        Dispatcher.get().dispatch(run);
    }

    @Override
    public void set(ORequest<R> ORequest) {
        this.ORequest = ORequest;
    }

    @Override
    public ORequest<R> get() {
        return ORequest;
    }
}
