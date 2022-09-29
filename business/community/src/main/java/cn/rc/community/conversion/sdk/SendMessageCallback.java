package cn.rc.community.conversion.sdk;

import io.rong.imlib.model.Message;

public interface SendMessageCallback {

    default void onAttached(Message message) {
    }

    ;

    default void onProgress(Message message, int progress) {
    }

    void onSuccess(Message message);

    void onError(Message message, int code, String reason);
}