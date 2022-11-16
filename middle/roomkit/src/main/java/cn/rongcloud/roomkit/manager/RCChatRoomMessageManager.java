package cn.rongcloud.roomkit.manager;

import android.text.TextUtils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/2/16
 * @time 10:17 上午
 * 统一发送消息管理类
 */
public class RCChatRoomMessageManager {
    public static PublishSubject<MessageWrapperModel> messageSubject = PublishSubject.create();

    /**
     * 发送消息集合
     *
     * @param roomId
     * @param messageContents
     * @param showLocation
     * @param onSuccess
     * @param onError
     */
    public static void sendChatMessages(String roomId,
                                        List<MessageContent> messageContents,
                                        Boolean showLocation,
                                        Function1 onSuccess,
                                        Function2 onError) {
        for (MessageContent messageContent : messageContents) {
            sendChatMessage(roomId, messageContent, showLocation, onSuccess, onError);
        }
    }

    /**
     * 发送单条消息
     *
     * @param roomId
     * @param messageContent
     * @param showLocation
     * @param onSuccess
     * @param onError
     */
    public static void sendChatMessage(String roomId,
                                       MessageContent messageContent,
                                       Boolean showLocation,
                                       Function1 onSuccess,
                                       Function2 onError) {
        RongCoreClient.getInstance().sendMessage(Conversation.ConversationType.CHATROOM,
                roomId,
                messageContent,
                null,
                null,
                new IRongCoreCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onSuccess(Message message) {
                        if (message != null) {
                            if (onSuccess != null) onSuccess.invoke(message.getMessageId());
                            if (showLocation) {
                                messageSubject.onNext(new MessageWrapperModel(roomId, messageContent));
                            }
                        }

                    }

                    @Override
                    public void onError(Message message, IRongCoreEnum.CoreErrorCode coreErrorCode) {
                        if (message != null) {
                            if (onError != null)
                                onError.invoke(coreErrorCode, message.getMessageId());
                        }
                    }
                });
    }

    /**
     * 发送本地消息，不执行发送逻辑，只做转发到本地接收处
     *
     * @param roomId
     * @param messageContent
     */
    public static void sendLocationMessage(String roomId, MessageContent messageContent) {
        messageSubject.onNext(new MessageWrapperModel(roomId, messageContent));
    }

    /**
     * 消息回调
     *
     * @param roomId
     * @return
     */
    public static @NonNull Observable<MessageContent> obMessageReceiveByRoomId(String roomId) {
        return messageSubject.observeOn(AndroidSchedulers.mainThread()).
                filter(new Predicate<MessageWrapperModel>() {
                    @Override
                    public boolean test(MessageWrapperModel messageWrapperModel) throws Throwable {
                        if (TextUtils.equals(messageWrapperModel.roomId, roomId)) {
                            return true;
                        }
                        return false;
                    }
                }).map(new Function<MessageWrapperModel, MessageContent>() {
            @Override
            public MessageContent apply(MessageWrapperModel messageWrapperModel) throws Throwable {
                return messageWrapperModel.messageContent;
            }
        });
    }

    /**
     * 接收消息
     *
     * @param roomId
     * @param messageContent
     */
    public static void onReceiveMessage(String roomId, MessageContent messageContent) {
        messageSubject.onNext(new MessageWrapperModel(roomId, messageContent));
    }

    static class MessageWrapperModel {
        public String roomId;
        public MessageContent messageContent;

        public MessageWrapperModel(String roomId, MessageContent messageContent) {
            this.roomId = roomId;
            this.messageContent = messageContent;
        }
    }
}
