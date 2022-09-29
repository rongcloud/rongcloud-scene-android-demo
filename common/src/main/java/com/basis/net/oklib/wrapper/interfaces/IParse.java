package com.basis.net.oklib.wrapper.interfaces;

/**
 * @author: BaiCQ
 * @ClassName: Processor
 * @date: 2018/6/27
 * @Description: 数据解析：统一数据格式，将response的数据封装成Wrapper返回
 */

public interface IParse<W extends IWrap> {
    /**
     * 统一解析字段
     *
     * @param httpcode http状态码
     * @param resJson  body
     * @return
     */
    W parse(int httpcode, String resJson) throws Exception;

    /**
     * 状态判断
     *
     * @param code
     * @return
     */
    boolean ok(int code);
}