package com.basis.net.oklib.api.body;

import okhttp3.RequestBody;

/**
 * Multi Party 的参数
 * key：映射map中的key
 * value：ibody
 * name body   分别对应一下参数：
 * addFormDataPart(name,fileName,RequestBody)
 */
public interface IBody {
    /**
     * body的名称
     *
     * @return
     */
    String name();

    /**
     * body
     *
     * @return
     */
    RequestBody body();
}
