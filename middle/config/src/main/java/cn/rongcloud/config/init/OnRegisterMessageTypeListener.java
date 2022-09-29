package cn.rongcloud.config.init;

import io.rong.imlib.model.Message;

public interface OnRegisterMessageTypeListener {
    void onRegisterMessageType();

    default void onReceivedMessage(Message message) {
    }
}