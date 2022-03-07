/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.radioroom.callback;

/**
 * 无返回结果的回调
 */
public interface RCRadioRoomCallback extends RCRadioRoomBaseCallback {

    /**
     * 成功
     */
    void onSuccess();

    void onError(int code, String message);
}
