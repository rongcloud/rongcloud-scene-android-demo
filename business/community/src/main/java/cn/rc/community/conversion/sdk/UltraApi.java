package cn.rc.community.conversion.sdk;

import android.widget.EditText;

import com.basis.wapper.IResultBack;

import java.util.List;

import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;

public interface UltraApi {

    // 超级群会话类型
    Conversation.ConversationType ULTRA_GROUP = Conversation.ConversationType.ULTRA_GROUP;
    // 默认消息count
    int MSG_COUNT = 20;

    /**
     * 获取会话信息
     *
     * @param targetId   会话id
     * @param channelId  频道id
     * @param resultBack callback
     */
    void getConversion(String targetId, String channelId, IResultBack<Conversation> resultBack);

    /**
     * 获取单个频道的未读数
     *
     * @param targetId   会话id
     * @param channelId  频道id
     * @param resultBack callback
     */
    void getChannelUnreadCount(String targetId, String channelId, IResultBack<Integer> resultBack);


    /**
     * 获取社区所有频道的未读数
     *
     * @param targetId   会话id
     * @param resultBack callback
     */
    void getAllChannelUnreadCount(String targetId, IResultBack<Integer> resultBack);

    /**
     * 获取最新消息
     *
     * @param targetId   会话id
     * @param channelId  频道id
     * @param resultBack callback
     */
    void getLastMessages(String targetId, String channelId, IResultBack<List<Message>> resultBack);

    /**
     * 获取指定消息前的20条历史消息
     *
     * @param lastMessage  指定的消息
     * @param filterMsgTag 过滤的objectName
     * @param resultBack   callback
     */
    void getHistoryMessages(Message lastMessage, String filterMsgTag, IResultBack<List<Message>> resultBack);


    void getMessage(String uid, IResultBack<Message> resultBack);

    /**
     * 发送普通消息
     *
     * @param targetId  会话id
     * @param channelId 频道id
     * @param content   content
     * @param callback  callback
     * @param editText  输入框
     */
    void sendMessage(String targetId, String channelId, EditText editText, MessageContent content, SendMessageCallback callback);


    /**
     * 发送媒体消息
     *
     * @param targetId  会话id
     * @param channelId 频道id
     * @param content   content
     * @param callback  callback
     */
    void sendMediaMessage(String targetId, String channelId, MessageContent content, SendMessageCallback callback);

    /**
     * 发送图片消息
     *
     * @param targetId
     * @param channelId
     * @param content
     * @param callback
     */
    void sendImageMessage(String targetId, String channelId, MessageContent content, SendMessageCallback callback);


    /**
     * 主动去修改消息
     */
    void modifyUltraGroupMessage(Message message, WrapperResultCallback wrapperResultCallback);

    /**
     * 包装结果集callback
     *
     * @param <T>
     */
    class WrapperResultCallback<T> extends IRongCoreCallback.ResultCallback<T> {
        private IResultBack<T> resultBack;

        public WrapperResultCallback(IResultBack<T> resultBack) {
            this.resultBack = resultBack;
        }

        @Override
        public void onSuccess(T result) {
            if (null != resultBack) resultBack.onResult(result);
        }

        @Override
        public void onError(IRongCoreEnum.CoreErrorCode e) {
            if (null != resultBack) resultBack.onResult(null);

        }
    }
}
