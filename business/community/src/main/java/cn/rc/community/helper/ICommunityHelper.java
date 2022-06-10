package cn.rc.community.helper;

import com.basis.wapper.IResultBack;

import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.model.MessageContent;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/11
 * @time 6:54 下午
 */
public interface ICommunityHelper {

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
    void getUltraGroupUnreadMentionedCount(String targetId, IRongCoreCallback.ResultCallback<Integer> callback);

    /**
     * 获取指定频道的消息未读数
     */
    void getUltraGroupUnreadMentionedCount(String targetId, String channelId, IRongCoreCallback.ResultCallback<Integer> callback);

    /**
     * 注册自定义消息类型
     *
     * @param classes
     */
    void registerMessageTypes(Class<? extends MessageContent>... classes);
    
}
