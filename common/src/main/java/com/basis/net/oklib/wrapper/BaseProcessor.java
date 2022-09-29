package com.basis.net.oklib.wrapper;

import com.google.gson.JsonElement;

import com.basis.net.oklib.api.ORequest;
import com.basis.net.oklib.wrapper.interfaces.IProcess;
import com.basis.net.oklib.wrapper.interfaces.IResult;
import com.basis.net.oklib.wrapper.interfaces.IWrap;

/**
 *
 */
public class BaseProcessor<IR extends IResult<R, E>, R, E, T> implements IProcess<IR, R, E, T> {
    private final static String TAG = "BaseProcessor";
    //最大重复请求次数
    private final static int MAX_REPEAT = 1;
    //重复次数
    private int repeat = 1;
    //缓存上次code
    private int lastCode = 0;
    private String lastUrl = "";

    @Override
    public final void process(int code, ORequest ORequest) {
        if (code == lastCode && lastUrl.equals(ORequest.url)) {
            //同一次请求同样的错误
            repeat++;
        } else {
            lastCode = code;
            lastUrl = ORequest.url;
            repeat = 1;
        }
        if (repeat > MAX_REPEAT) {
            OkUtil.e(TAG, "The maximum limit of repeat is " + MAX_REPEAT + " . current repeat = " + repeat);
            return;
        }
        OkUtil.e(TAG, "**************************** start process code error = " + code + " and request ****************************");
        if (processCode(code)) {
            ORequest.request();
        }
        OkUtil.e(TAG, "**************************** end   process code error = " + code + " and request ****************************");
    }

    /**
     * 处理error code
     *
     * @param code
     * @return true： 需要再次尝试请求 false：不需要再次请求
     */
    protected boolean processCode(int code) {
        return false;
    }

    @Override
    public IR processResult(IWrap wrap, Class<T> clazz) {
        if (null == clazz) {
            return (IR) new IResult.StatusResult(wrap.getCode(), wrap.getMessage());
        } else {//objResult
            OkUtil.e("processResult", "clazz:" + clazz.getSimpleName() + "  body:" + wrap.getBody());
            R result = null;
            JsonElement element = wrap.getBody();
            if (null != element) {
                result = (R) OkUtil.json2List(wrap.getBody(), clazz);
            }
            return (IR) new IResult.ObjResult(result, wrap.getPage());
        }
    }
}