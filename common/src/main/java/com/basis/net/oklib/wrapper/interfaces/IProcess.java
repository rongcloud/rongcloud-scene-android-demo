package com.basis.net.oklib.wrapper.interfaces;


import com.basis.net.oklib.api.ORequest;

/**
 * @param <R> result的类型
 * @param <E> extra的类型
 * @param <T> 解析实体的类 result是实体：R和T一样
 *            result是集合：R 是List<T>
 * @author: BaiCQ
 * @ClassName: Processor
 * @date: 2018/6/27
 * @Description: 处理器接口 封装有错误处理和数据处理
 */
public interface IProcess<IR extends IResult<R, E>, R, E, T> {
    /**
     * 错误处理
     *
     * @param code
     * @param request
     */
    void process(int code, ORequest request);

    /**
     * 数据处理
     * 从Wrapper中提取业务数据
     *
     * @param wrap
     * @return
     */
    IR processResult(IWrap wrap, Class<T> clazz);
}
