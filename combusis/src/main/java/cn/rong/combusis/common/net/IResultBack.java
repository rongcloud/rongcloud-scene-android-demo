package cn.rong.combusis.common.net;

/**
 * 统一返回接口
 *
 * @param <T>
 */
public interface IResultBack<T> {
    void onResult(T t);
}
