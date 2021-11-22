package com.basis.net.oklib.wrapper.interfaces;

/**
 * 业务回调接口
 *
 * @param <IR> result类型
 * @param <R>  IResult<R, E> 中R类型
 * @param <E>  IResult<R, E> 中E类型
 * @param <T>  Type Class 类型
 */
public interface BusiCallback<IR extends IResult<R, E>, R, E, T> {

    /**
     * @param result
     */
    void onResult(IR result);

    /**
     * @param code   状态码
     * @param errMsg 错误信息
     */
    void onError(int code, String errMsg);

    void onAfter();

    /**
     * 解析实体的类型
     *
     * @return
     */
    Class<T> onGetType();
}
