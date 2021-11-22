package com.basis.net.oklib.wrapper;

import com.basis.net.oklib.api.OCallBack;
import com.basis.net.oklib.wrapper.interfaces.BusiCallback;
import com.basis.net.oklib.wrapper.interfaces.ILoadTag;
import com.basis.net.oklib.wrapper.interfaces.IParse;
import com.basis.net.oklib.wrapper.interfaces.IProcess;
import com.basis.net.oklib.wrapper.interfaces.IResult;
import com.basis.net.oklib.wrapper.interfaces.IWrap;

import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * @param <IR> result IResult<R,E>的类型 注意：BaseProcessor 仅支持 StatueResult的类型 和 ObjResult<List<T>>的结果
 * @param <R>  Integer 或 List<T>
 * @param <E>  String  或 Boolen
 * @param <T>  Void    或 T 实体
 */
public class GeneralWrapperCallBack<IR extends IResult<R, E>, R, E, T> extends OCallBack<IR> {
    protected final String TAG = this.getClass().getSimpleName();
    private final IParse parser;
    private final IProcess<IR, R, E, T> processor;
    public BusiCallback<IR, R, E, T> busiCallback;
    private IWrap wrapper;
    private ILoadTag iLoadTag;

    /**
     * @param iLoadTag     loadTag
     * @param parser       解析器
     * @param busiCallback 业务回调
     */
    public GeneralWrapperCallBack(ILoadTag iLoadTag, IParse parser, BusiCallback busiCallback) {
        this.busiCallback = busiCallback;
        this.iLoadTag = iLoadTag;
        this.processor = OkHelper.get().getProcessor();
        this.parser = null == parser ? OkHelper.get().getParser() : parser;
    }

    /**
     * requestAgain修改callback使用
     */
    public void setBsiCallback(BusiCallback<IR, R, E, T> busiCallback) {
        this.busiCallback = busiCallback;
    }

    @Override
    public void onBefore(Request.Builder builder) {
        wrapper = null;//置空
        if (null != iLoadTag) iLoadTag.show();
        if (null != OkHelper.get().getHeadCacher()) { //添加header
            Map<String, String> hs = OkHelper.get().getHeadCacher().onAddHeader();
            if (null == hs || hs.isEmpty()) return;
            for (Map.Entry<String, String> en : hs.entrySet()) {
                builder.addHeader(en.getKey(), en.getValue());
            }
        }
    }

    @Override
    public final IR onParse(Response response) throws Exception {
        if (null != OkHelper.get().getHeadCacher()) {//缓存header
            OkHelper.get().getHeadCacher().onCacheHeader(response.headers());
        }
        String res = response.body().string();
        int httpCode = response.code();
        OkUtil.e(TAG, "url = " + get().url + "\nhttpCode = " + httpCode + " res = " + res);
        if (null == res || "".equals(res)) {
            OkUtil.e(TAG, "未知错误：Response No Body ！");
            return null;
        }
        if (null == parser) {
            OkUtil.e(TAG, "No Set Parser ！");
            return null;
        }
        wrapper = parser.parse(httpCode, res);
        if (null == wrapper) {
            OkUtil.e(TAG, "Parse Wrapper Error !");
            return null;
        }
        if (null == processor) {
            OkUtil.e(TAG, "No Set Processor ！");
            return null;
        }
        if (!parser.ok(wrapper.getCode())) {
            if (null != processor) processor.process(wrapper.getCode(), get());
        }
        return processor.processResult(wrapper, busiCallback.onGetType());
    }

    @Override
    public void onResult(IR result) {
        if (null == result) {
            if (null != busiCallback) busiCallback.onError(-1, "No Result！");
        } else if (null != wrapper && !parser.ok(wrapper.getCode())) {
            if (null != busiCallback) busiCallback.onError(wrapper.getCode(), wrapper.getMessage());
        } else {
            if (null != busiCallback) busiCallback.onResult(result);
        }
    }

    @Override
    public void onError(int code, String msg) {
        if (null != busiCallback) busiCallback.onError(code, msg);
    }

    @Override
    public void onAfter() {
        if (null != iLoadTag) {
            iLoadTag.dismiss();
            iLoadTag = null;
        }
        if (null != busiCallback) {
            busiCallback.onAfter();
        }
    }
}
