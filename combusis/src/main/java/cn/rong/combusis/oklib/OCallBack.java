package cn.rong.combusis.oklib;


import okhttp3.Request;
import okhttp3.Response;

public abstract class OCallBack<R> implements IOBack<R> {

    @Override
    public void onBefore(Request.Builder builder) {

    }

    @Override
    public abstract R onParse(Response response) throws Exception;

    @Override
    public void onProgress(float progress, long total) {

    }

    @Override
    public abstract void onResult(R result);

    @Override
    public void onError(int code, String msg) {

    }

    @Override
    public void onAfter() {

    }
}
