package com.basis.net.oklib.wrapper.interfaces;

import java.util.Map;

import okhttp3.Headers;

/**
 * @author: BaiCQ
 * @ClassName: Processor
 * @date: 2018/6/27
 */
public interface IHeader {

    Map<String, String> onAddHeader();

    void onCacheHeader(Headers headers);
}