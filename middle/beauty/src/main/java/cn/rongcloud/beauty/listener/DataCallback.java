package cn.rongcloud.beauty.listener;

/**
 * @author gyn
 * @date 2022/10/17
 */
public interface DataCallback<T> {
    void onResult(T t);
}
