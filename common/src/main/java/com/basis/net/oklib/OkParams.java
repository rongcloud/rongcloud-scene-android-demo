package com.basis.net.oklib;

import java.util.HashMap;

/**
 * @author gyn
 * @date 2021/9/28
 */
public class OkParams {

    private HashMap<String, Object> mParams = new HashMap<>();

    public OkParams add(String key, Object obj) {
        mParams.put(key, obj);
        return this;
    }

    public HashMap<String, Object> build() {
        return mParams;
    }

    public static OkParams Builder() {
        return new OkParams();
    }
}
