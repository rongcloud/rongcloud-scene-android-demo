package cn.rc.community.conversion.controller.interfaces;

import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.wapper.IResultBack;

import java.util.List;

import cn.rc.community.OnConvertListener;
import cn.rc.community.conversion.controller.WrapperMessage;
import cn.rc.community.conversion.sdk.SendMessageCallback;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.HistoryMessageOption;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.ReceivedProfile;
import io.rong.imlib.model.UltraGroupTypingStatusInfo;

public interface IManager {
    int COUNT = 20;

    /**
     * 设置消息绑定视图监听
     *
     * @param attachListener 监听
     */
    void setOnMessageAttachListener(OnMessageAttachListener attachListener);

    /**
     * 设置频道的相关监听
     *
     * @param onChannelListener
     */
    void setOnChannelListener(OnChannelListener onChannelListener);

    /**
     * 设置消息长按监听
     *
     * @param listener 监听
     */
    void setOnMessageLongClick(IMessageAdapter.OnItemLongClickListener<WrapperMessage> listener);

    /**
     * 设置消息点击监听
     *
     * @param listener 监听
     */
    void setOnMessageClick(IMessageAdapter.OnItemClickListener<WrapperMessage> listener);

    /**
     * 绑定频道
     *
     * @param targetId  会话id
     * @param channelId 会话下的频道id
     */
    void attachChannel(String targetId, String channelId);

    /**
     * 绑定视图
     *
     * @param recyclerView 列表组件
     */
    void attachView(RecyclerView recyclerView);

    /**
     * 获取适配器中的消息
     *
     * @return 消息集合
     */
    List<WrapperMessage> getMessages();

    /**
     * 根据消息的uid 获取消息
     *
     * @param uid        uid
     * @param resultBack 回调
     */
    void getMessage(String uid, IResultBack<Message> resultBack);

    /**
     * message转换成显示的内容
     *
     * @param message 消息
     * @return 显示内容
     */
    String messageToContent(MessageContent message);

    /**
     * message提取固定格式的日期，格式如下：
     * 当天：      今天 hh:mm
     * 昨天：      昨天 hh:mm
     * 昨天以前：   02月26日 21：00
     *
     * @param message 消息
     * @return 固定格式的日期串
     */
    String messageToDate(Message message);

    /**
     * ui 插入一条消息
     *
     * @param message        Message
     * @param last           插入到最后
     * @param isScrollBottom 是否滑动到最底部
     * @return
     */
    WrapperMessage insertMessage(Message message, boolean last, boolean isScrollBottom);

    /**
     * 发送普通消息
     *
     * @param
     * @param content  content
     * @param editText
     * @param callback 回调
     */
    void sendMessage(MessageContent content, EditText editText, SendMessageCallback callback);


    /**
     * 删除消息
     *
     * @param message
     */
    void deleteMessage(WrapperMessage message);

    /**
     * 编辑消息
     */
    void editMessage(WrapperMessage message, IResultBack<Boolean> callBack);


    /**
     * 发送媒体消息
     *
     * @param content  content
     * @param callback 回调
     */
    void sendMediaMessage(MessageContent content, SendMessageCallback callback);

    /**
     * 发送图片消息
     *
     * @param content  content
     * @param callback 回调
     */
    void sendImageMessage(MessageContent content, SendMessageCallback callback);

    /**
     * 获取第一条未读消息
     *
     * @param resultBack
     */
    void getFirstUnReadMessage(IResultBack<Message> resultBack);

    /**
     * 获取本地超级群-当前频道-所有消息
     * 方法先从本地获取历史消息，本地有缺失的情况下会从服务端同步缺失的部分。当本地没有更多消息的时候，会从服务端拉取
     *
     * @param dateTime              分界点消息的时间戳
     * @param pageNum               消息数量
     * @param pullOrder             向上还是向下
     * @param isScrollBottom        插入列表以后，消息列表是否定位到最底部
     * @param iGetMessageCallbackEx
     */
    void getMessages(long dateTime, int pageNum, HistoryMessageOption.PullOrder pullOrder, boolean isScrollBottom, IRongCoreCallback.IGetMessageCallbackEx iGetMessageCallbackEx);

    void getLatestMessages(IRongCoreCallback.ResultCallback<List<Message>> callback);

    void sendUltraGroupTypingStatus(IRongCoreCallback.OperationCallback callback);

    void getSystemMessage(long time, String[] objectName, IResultBack<List<Message>> resultBack);

    void clearSystemMessagesUnreadStatus();

    void getSystemMessage(int lastMessageId, String objectName, IResultBack<List<Message>> resultBack);

    /**
     * 获取会话详情
     *
     * @param resultBack 回调
     */
    void getConversion(IResultBack<Conversation> resultBack);

    /**
     * 获取本地特定超级群所有频道列表
     */
    void getConversationListForAllChannel(IResultBack iResultBack);


    /**
     * 消息删除-删除本地所有频道指定时间之前的消息
     */
    void deleteUltraGroupMessagesForAllChannel(long timestamp, IRongCoreCallback.ResultCallback<Boolean> callback);

    /**
     * 删除消息-删除本地特定频道指定时间之前的消息
     */
    void deleteUltraGroupMessages(String targetId, String channelId, long timestamp, IRongCoreCallback.ResultCallback<Boolean> callback);

    /**
     * 删除消息-删除服务端特定频道指定时间之前的消息
     */
    void deleteRemoteUltraGroupMessages(String targetId, String channelId, long timestamp, IRongCoreCallback.OperationCallback callback);

    /**
     * 获取所有频道的消息未读数
     */
    void getUltraGroupUnreadCount(IResultBack<Integer> resultBack);

    /**
     * 获取指定频道的消息未读数
     */
    void getChannelUnreadCount(IResultBack<Integer> resultBack);

    /**
     * 撤销消息
     *
     * @param iMessage
     */
    void recallMessage(WrapperMessage iMessage);

    /**
     * 上报消息的已读状态
     *
     * @param sentTime 会话中已读的最后一条消息的发送时间戳
     * @param callback
     */
    void clearMessagesUnreadStatus(long sentTime, IRongCoreCallback.OperationCallback callback);

    /**
     * 清空会话中的所有消息数量
     *
     * @param callback
     */
    void clearAllMessageUnreadStatus(IRongCoreCallback.OperationCallback callback);

    int containMessage(Message message);

    /**
     * 消息绑定监听
     */
    interface OnMessageAttachListener {
        //消息的绑定标识
        AttachedInfo onAttach(String objectName);

        //消息插入监听
        void onMessageAttach(WrapperMessage message, boolean isScrollBottom);

        //消息接收到了
        void onReceivedMessage(Message message, ReceivedProfile profile);
    }

    /**
     * 监听频道的相关
     */
    interface OnChannelListener {
        //监听输入状态
        void onEditingStatusChanged(List<UltraGroupTypingStatusInfo> infoList);
    }

    abstract class AttachedInfo {
        /**
         * 根据message的信息返回布局id
         *
         * @param message 消息
         * @return 布局id
         */
        public abstract int onSetLayout(WrapperMessage message);

        public abstract OnConvertListener<WrapperMessage> onSetConvertListener();

        public abstract boolean checked();
    }


}
