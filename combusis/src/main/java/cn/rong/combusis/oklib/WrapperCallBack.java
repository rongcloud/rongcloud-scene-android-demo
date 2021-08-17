package cn.rong.combusis.oklib;


import okhttp3.Request;
import okhttp3.Response;

public abstract class WrapperCallBack extends OCallBack<Wrapper> {
    @Override
    public void onBefore(Request.Builder builder) {
    }

    @Override
    public Wrapper onParse(Response response) throws Exception {
        return new Wrapper(response);
    }

    @Override
    public void onProgress(float progress, long total) {
    }

    @Override
    public abstract void onResult(Wrapper result);

    @Override
    public void onError(int code, String msg) {
    }

    @Override
    public void onAfter() {
    }
}
