/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.radioroom.callback;


public interface RCRadioRoomBaseCallback {
    /**
     * 错误回调
     *
     * @param code    错误吗
     * @param message 错误的描述信息
     */
    void onError(int code, String message);
}
