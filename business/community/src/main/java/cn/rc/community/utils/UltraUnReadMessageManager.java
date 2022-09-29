package cn.rc.community.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.rc.community.helper.UltraGroupCenter;
import io.rong.common.RLog;
import io.rong.imkit.IMCenter;
import io.rong.imlib.ChannelClient;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.IRongCoreListener;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.listener.OnReceiveMessageWrapperListener;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.ReceivedProfile;
import io.rong.message.RecallNotificationMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/11
 * @time 16:26
 * 超级群社区消息未读数量监听工具类
 */
public class UltraUnReadMessageManager extends OnReceiveMessageWrapperListener implements IRongCoreListener.UltraGroupReadTimeListener, IRongCoreListener.UltraGroupMessageChangeListener {

    private static final String TAG = "UltraUnReadMessageManager";
    private final List<MultiConversationUnreadMsgInfo> mMultiConversationUnreadInfos;

    public UltraUnReadMessageManager() {
        this.mMultiConversationUnreadInfos = new ArrayList();
        RongCoreClient.addOnReceiveMessageListener(this);
        //消息被撤回的时候，同步未读消息数量,将未读消息的数量返回去
        IMCenter.getInstance().addOnRecallMessageListener(new RongIMClient.OnRecallMessageListener() {
            public boolean onMessageRecalled(Message message, RecallNotificationMessage recallNotificationMessage) {
                syncUnreadCount(message.getTargetId(), message.getChannelId());
                return false;
            }
        });
        //监听消息的撤回之类的，
        UltraGroupCenter.getInstance().addUltraGroupMessageChangeListener(this);
        //同步多端消息
        UltraGroupCenter.getInstance().addUltraGroupReadTimeListener(this);

    }


    public static UltraUnReadMessageManager getInstance() {
        return UltraUnReadMessageManager.SingletonHolder.sInstance;
    }


    /**
     * 接收到消息的时候
     *
     * @param message
     * @param profile
     * @return
     */
    @Override
    public void onReceivedMessage(Message message, ReceivedProfile profile) {
        if (profile.getLeft() == 0 || !profile.hasPackage()) {
            this.syncUnreadCount(message.getTargetId(), message.getChannelId());
        }
    }

    /**
     * 同步消息
     */
    private void syncUnreadCount(String targetId, String channelId) {
        Iterator var1 = this.mMultiConversationUnreadInfos.iterator();
        while (var1.hasNext()) {
            final UltraUnReadMessageManager.MultiConversationUnreadMsgInfo msgInfo = (UltraUnReadMessageManager.MultiConversationUnreadMsgInfo) var1.next();
            ChannelClient.getInstance().getUnreadCount(Conversation.ConversationType.ULTRA_GROUP, targetId, channelId, new IRongCoreCallback.ResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    msgInfo.observer.onChannelUnReadChanged(targetId, channelId, integer);
                }

                @Override
                public void onError(IRongCoreEnum.CoreErrorCode e) {

                }
            });
            //所有频道的未读数
            ChannelClient.getInstance().getConversationListForAllChannel(Conversation.ConversationType.ULTRA_GROUP, targetId, new IRongCoreCallback.ResultCallback<List<Conversation>>() {
                @Override
                public void onSuccess(List<Conversation> conversations) {
                    if (conversations == null) return;
                    Integer integer = 0;
                    for (Conversation conversation : conversations) {
                        if (null != conversation) {
                            if (TextUtils.isEmpty(conversation.getChannelId())) {
                                continue;
                            }
                            integer = integer + conversation.getUnreadMessageCount();
                        }
                    }
                    msgInfo.observer.onUltraGroupUnReadChanged(targetId, integer);
                }

                @Override
                public void onError(IRongCoreEnum.CoreErrorCode e) {

                }
            });
        }
    }

    /**
     * 添加监听
     *
     * @param observer
     */
    public void addObserver(final UltraUnReadMessageManager.IUnReadMessageObserver observer) {
        if (observer == null) {
            RLog.e("UnReadMessageManager", "can't add a null observer!");
        } else {
            synchronized (this.mMultiConversationUnreadInfos) {
                final UltraUnReadMessageManager.MultiConversationUnreadMsgInfo msgInfo = new UltraUnReadMessageManager.MultiConversationUnreadMsgInfo();
                msgInfo.observer = observer;
                this.mMultiConversationUnreadInfos.add(msgInfo);
            }
        }
    }

    /**
     * 移除监听
     *
     * @param observer
     */
    public void removeObserver(final UltraUnReadMessageManager.IUnReadMessageObserver observer) {
        if (observer == null) {
            RLog.w("UnReadMessageManager", "removeOnReceiveUnreadCountChangedListener Illegal argument");
        } else {
            synchronized (this.mMultiConversationUnreadInfos) {
                UltraUnReadMessageManager.MultiConversationUnreadMsgInfo result = null;
                Iterator var4 = this.mMultiConversationUnreadInfos.iterator();

                while (var4.hasNext()) {
                    UltraUnReadMessageManager.MultiConversationUnreadMsgInfo msgInfo = (UltraUnReadMessageManager.MultiConversationUnreadMsgInfo) var4.next();
                    if (msgInfo.observer == observer) {
                        result = msgInfo;
                        break;
                    }
                }

                if (result != null) {
                    this.mMultiConversationUnreadInfos.remove(result);
                }

            }
        }
    }

    /**
     * 清除所有监听
     */
    public void clearObserver() {
        synchronized (this.mMultiConversationUnreadInfos) {
            this.mMultiConversationUnreadInfos.clear();
        }
    }


    /**
     * 当未读消息被清除的时候
     *
     * @param target
     * @param channelId
     * @param sentTime
     */
    IUltraGroupUnreadSyncStatusListener iUltraGroupUnreadSyncStatusListener = new IUltraGroupUnreadSyncStatusListener() {

        @Override
        public void onClearedUnreadStatus(String target, String channelId, long sentTime) {
            syncUnreadCount(target, channelId);
        }
    };

    /**
     * 清空消息的时候记得回调一下
     *
     * @return
     */
    public IUltraGroupUnreadSyncStatusListener getiUltraGroupUnreadSyncStatusListener() {
        return iUltraGroupUnreadSyncStatusListener;
    }

    @Override
    public void onUltraGroupReadTimeReceived(String targetId, String channelId, long time) {
        syncUnreadCount(targetId, channelId);
    }

    /**
     * 消息扩展
     *
     * @param messages
     */
    @Override
    public void onUltraGroupMessageExpansionUpdated(List<Message> messages) {

    }

    /**
     * 消息修改
     *
     * @param messages
     */
    @Override
    public void onUltraGroupMessageModified(List<Message> messages) {

    }

    /**
     * 消息撤回
     *
     * @param messages
     */
    @Override
    public void onUltraGroupMessageRecalled(List<Message> messages) {
        for (Message message : messages) {
            syncUnreadCount(message.getTargetId(), message.getChannelId());
        }
    }


    private class MultiConversationUnreadMsgInfo {
        UltraUnReadMessageManager.IUnReadMessageObserver observer;

        private MultiConversationUnreadMsgInfo() {
        }
    }

    private static class SingletonHolder {
        static UltraUnReadMessageManager sInstance = new UltraUnReadMessageManager();

        private SingletonHolder() {
        }
    }

    /**
     * 消息变化监听
     */
    public interface IUnReadMessageObserver {
        /**
         * 社区的某个频道的消息未读数量
         *
         * @param targetId
         * @param channelId
         * @param count
         */
        void onChannelUnReadChanged(String targetId, String channelId, int count);


        /**
         * 社区的消息未读数发生变化 ，所有的未读数，包括了没有channelId的消息
         *
         * @param targetId
         * @param count
         */
        void onUltraGroupUnReadChanged(String targetId, int count);
    }

    /**
     * 超级群未读消息被清除监听
     */
    public interface IUltraGroupUnreadSyncStatusListener {

        /**
         * 未读消息被清除的时候
         *
         * @param target
         * @param channelId
         * @param sentTime
         */
        void onClearedUnreadStatus(String target, String channelId, long sentTime);
    }
}
