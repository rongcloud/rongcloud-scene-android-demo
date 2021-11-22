/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.radioroom.callback;

/**
 * 有返回结果的回调
 *
 * @param <T> 数据类型
 */
public interface RCRadioRoomResultCallback<T> extends RCRadioRoomBaseCallback {

    /**
     * 成功回调
     *
     * @param data 成功返回数据
     */
    void onSuccess(T data);

    @Override
    void onError(int code, String message);
}
