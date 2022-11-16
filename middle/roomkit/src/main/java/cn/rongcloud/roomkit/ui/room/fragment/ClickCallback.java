package cn.rongcloud.roomkit.ui.room.fragment;

/**
 * @author gyn
 * @date 2021/9/28
 */
public interface ClickCallback<T> {
    void onResult(T result, String msg);
}
