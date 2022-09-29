package cn.rc.community.helper;


import java.util.ArrayList;
import java.util.List;

import cn.rc.community.Version;
import cn.rc.community.conversion.ObjectName;
import cn.rc.community.message.sysmsg.ChannelNoticeMsg;
import cn.rc.community.message.sysmsg.ChannelType;
import cn.rc.community.message.sysmsg.CommunityChangeMsg;
import cn.rc.community.message.sysmsg.CommunityDeleteMsg;
import cn.rc.community.message.sysmsg.CommunitySysNoticeMsg;
import cn.rc.community.message.sysmsg.CommunityType;
import cn.rc.community.message.sysmsg.UpdateUserInfoType;
import cn.rc.community.message.sysmsg.UserUpdateMsg;
import io.rong.imkit.notification.NotificationUtil;
import io.rong.imlib.ChannelClient;
import io.rong.imlib.IRongCoreListener;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.listener.OnReceiveMessageWrapperListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.ReceivedProfile;
import io.rong.imlib.model.UltraGroupTypingStatusInfo;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/11
 * @time 17:43
 * 超级群消息监听的类，用于做消息监听的扩展，不处理任何逻辑
 */
public class UltraGroupCenter extends OnReceiveMessageWrapperListener implements IRongCoreListener.UltraGroupMessageChangeListener, IRongCoreListener.UltraGroupReadTimeListener,
        IRongCoreListener.UltraGroupTypingStatusListener {

    private List<IRongCoreListener.UltraGroupMessageChangeListener> ultraGroupMessageChangeListeners;
    private List<IRongCoreListener.UltraGroupReadTimeListener> ultraGroupReadTimeListeners;
    private List<IRongCoreListener.UltraGroupTypingStatusListener> ultraGroupTypingStatusListeners;
    private List<OnReceiveMessageWrapperListener> onReceiveMessageWrapperListeners;
    private List<IUltraGroupUserEventListener> ultraGroupUserEventListeners;
    private List<IUltraGroupChannelListener> ultraGroupChannelListeners;
    private List<IUltraGroupChangeListener> iUltraGroupChangeListeners;
    private List<IUltraGroupUserInfoUpdateListener> ultraGroupUserInfoUpdateListeners;


    // imsdk 版本号
    public static final String SDK_VERSION = Version.SDK_VERSION;

    public static String getVersion() {
        return SDK_VERSION;
    }

    public static UltraGroupCenter getInstance() {
        return UltraGroupCenter.SingletonHolder.sInstance;
    }

    @Override
    public void onUltraGroupMessageExpansionUpdated(List<Message> messages) {
        for (IRongCoreListener.UltraGroupMessageChangeListener ultraGroupMessageChangeListener : ultraGroupMessageChangeListeners) {
            ultraGroupMessageChangeListener.onUltraGroupMessageExpansionUpdated(messages);
        }
    }

    @Override
    public void onUltraGroupMessageModified(List<Message> messages) {
        for (IRongCoreListener.UltraGroupMessageChangeListener ultraGroupMessageChangeListener : ultraGroupMessageChangeListeners) {
            ultraGroupMessageChangeListener.onUltraGroupMessageModified(messages);
        }
    }

    @Override
    public void onUltraGroupMessageRecalled(List<Message> messages) {
        for (IRongCoreListener.UltraGroupMessageChangeListener ultraGroupMessageChangeListener : ultraGroupMessageChangeListeners) {
            ultraGroupMessageChangeListener.onUltraGroupMessageRecalled(messages);
        }
    }

    @Override
    public void onUltraGroupReadTimeReceived(String targetId, String channelId, long time) {
        for (IRongCoreListener.UltraGroupReadTimeListener ultraGroupReadTimeListener : ultraGroupReadTimeListeners) {
            ultraGroupReadTimeListener.onUltraGroupReadTimeReceived(targetId, channelId, time);
        }
    }

    @Override
    public void onUltraGroupTypingStatusChanged(List<UltraGroupTypingStatusInfo> infoList) {
        for (IRongCoreListener.UltraGroupTypingStatusListener ultraGroupTypingStatusListener : ultraGroupTypingStatusListeners) {
            ultraGroupTypingStatusListener.onUltraGroupTypingStatusChanged(infoList);
        }
    }


    private static class SingletonHolder {
        static UltraGroupCenter sInstance = new UltraGroupCenter();

        private SingletonHolder() {
        }
    }

    public UltraGroupCenter() {
        this.ultraGroupMessageChangeListeners = new ArrayList<>();
        this.ultraGroupReadTimeListeners = new ArrayList<>();
        this.ultraGroupTypingStatusListeners = new ArrayList<>();
        this.onReceiveMessageWrapperListeners = new ArrayList<>();
        this.ultraGroupUserEventListeners = new ArrayList<>();
        this.ultraGroupChannelListeners = new ArrayList<>();
        this.iUltraGroupChangeListeners = new ArrayList<>();
        this.ultraGroupUserInfoUpdateListeners = new ArrayList<>();

        ChannelClient.getInstance().setUltraGroupTypingStatusListener(this);
        ChannelClient.getInstance().setUltraGroupReadTimeListener(this);
        ChannelClient.getInstance().setUltraGroupMessageChangeListener(this);
        RongCoreClient.addOnReceiveMessageListener(this);
    }

    @Override
    public void onReceivedMessage(Message message, ReceivedProfile profile) {
        switch (message.getObjectName()) {
            case ObjectName.COMMUNITY_SYSTEM_NOTICE:
                covertSysNoticeMsg(((CommunitySysNoticeMsg) message.getContent()));
                break;
            case ObjectName.CHANNEL_NOTICE:
                covertChannelNoticeMsg(((ChannelNoticeMsg) message.getContent()));
                break;
            case ObjectName.COMMUNITY_CHANGE:
                //社区发生了改变的消息
                covertCommunityChange(((CommunityChangeMsg) message.getContent()));
                break;
            case ObjectName.COMMUNITY_DELETE:
                //社区解散的消息
                covertCommunityDelete(((CommunityDeleteMsg) message.getContent()));
                break;
            case ObjectName.COMMUNITY_UPDATE_USERINFO:
                covertUpdateUserInfo(((UserUpdateMsg) message.getContent()));
                break;
        }
        for (OnReceiveMessageWrapperListener onReceiveMessageWrapperListener : onReceiveMessageWrapperListeners) {
            onReceiveMessageWrapperListener.onReceivedMessage(message, profile);
        }
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateMsg
     */
    private void covertUpdateUserInfo(UserUpdateMsg userUpdateMsg) {
        UpdateUserInfoType type = userUpdateMsg.getType();
        switch (type) {
            case nickName:
            case portrait:
                for (IUltraGroupUserInfoUpdateListener ultraGroupUserInfoUpdateListener : ultraGroupUserInfoUpdateListeners) {
                    ultraGroupUserInfoUpdateListener.onUpdateUserInfo(userUpdateMsg.getUserId(), userUpdateMsg.getNickName(), userUpdateMsg.getPortrait());

                }
                break;
        }
    }

    /**
     * 社区被解散了
     *
     * @param communityDeleteMsg
     */
    private void covertCommunityDelete(CommunityDeleteMsg communityDeleteMsg) {
        for (IUltraGroupChangeListener listener : iUltraGroupChangeListeners) {
            listener.onUltraGroupDelete(communityDeleteMsg.getCommunityUid());
        }
    }

    /**
     * 社区发生了改变，这里暂且没表现具体的
     * 1.分组被删除和频道被删除需要弹窗提示
     *
     * @param communityChangeMsg
     */
    private void covertCommunityChange(CommunityChangeMsg communityChangeMsg) {
        for (IUltraGroupChangeListener listener : iUltraGroupChangeListeners) {
            listener.onUltraGroupChanged(communityChangeMsg.getCommunityUid());
            listener.onChannelDeleted(communityChangeMsg.getChannelUids());
        }
    }

    /**
     * 频道消息，社区的每一个人都能收到
     *
     * @param channelNoticeMsg
     */
    private void covertChannelNoticeMsg(ChannelNoticeMsg channelNoticeMsg) {
        ChannelType type = channelNoticeMsg.getType();
        switch (type) {
            case disabled:
                //被禁言
                for (IUltraGroupUserEventListener ultraGroupUserEventListener : ultraGroupUserEventListeners) {
                    ultraGroupUserEventListener.onBeForbidden(channelNoticeMsg.getCommunityUid(),
                            channelNoticeMsg.getFromUserId(), channelNoticeMsg.getToUserId(),
                            channelNoticeMsg.getMessage());
                }
                break;
            case enabled:
                //被取消禁言
                for (IUltraGroupUserEventListener ultraGroupUserEventListener : ultraGroupUserEventListeners) {
                    ultraGroupUserEventListener.onCancelForbidden(channelNoticeMsg.getCommunityUid(),
                            channelNoticeMsg.getFromUserId(), channelNoticeMsg.getToUserId(),
                            channelNoticeMsg.getMessage());
                }
                break;
            case marked:
                for (IUltraGroupChannelListener ultraGroupChannelListener : ultraGroupChannelListeners) {
                    ultraGroupChannelListener.onAddMarkMessage(channelNoticeMsg.getCommunityUid(), channelNoticeMsg.getChannelUid());
                }
                break;
            case removedMarked:
                for (IUltraGroupChannelListener ultraGroupChannelListener : ultraGroupChannelListeners) {
                    ultraGroupChannelListener.onRemoveMarkMessage(channelNoticeMsg.getCommunityUid(), channelNoticeMsg.getChannelUid());
                }
                break;
            case none:
                break;
            case joined:
                //xx加入了社区，插入数据库做提示
                for (IUltraGroupChannelListener ultraGroupChannelListener : ultraGroupChannelListeners) {
                    ultraGroupChannelListener.onUserJoined(channelNoticeMsg.getCommunityUid(),
                            channelNoticeMsg.getFromUserId(), channelNoticeMsg.getToUserId(),
                            channelNoticeMsg.getMessage());
                }
                break;
        }
    }

    /**
     * 系统提示消息，指定的某个人能收到
     *
     * @param communitySysNoticeMsg
     */
    private void covertSysNoticeMsg(CommunitySysNoticeMsg communitySysNoticeMsg) {
        //系统提示
        CommunityType type = communitySysNoticeMsg.getType();
        switch (type) {
            case kick:
                //被踢出
                for (IUltraGroupUserEventListener listener : ultraGroupUserEventListeners) {
                    listener.onKickOut(communitySysNoticeMsg.getCommunityUid(), communitySysNoticeMsg.getFromUserId(), communitySysNoticeMsg.getMessage());
                }
                break;
            case joined:
                //当自己加入社区
                for (IUltraGroupUserEventListener listener : ultraGroupUserEventListeners) {
                    listener.onJoined(communitySysNoticeMsg.getCommunityUid(), communitySysNoticeMsg.getFromUserId(), communitySysNoticeMsg.getMessage());
                }
                break;
            case rejected:
                //当申请被拒绝
                for (IUltraGroupUserEventListener listener : ultraGroupUserEventListeners) {
                    listener.onRejected(communitySysNoticeMsg.getCommunityUid(), communitySysNoticeMsg.getFromUserId(), communitySysNoticeMsg.getMessage());
                }
                break;
            case left:
                //当自己离开社区
                for (IUltraGroupUserEventListener listener : ultraGroupUserEventListeners) {
                    listener.onLeft(communitySysNoticeMsg.getCommunityUid(), communitySysNoticeMsg.getFromUserId(), communitySysNoticeMsg.getMessage());
                }
                break;
            case request:
                //xx申请加入社区
                for (IUltraGroupUserEventListener listener : ultraGroupUserEventListeners) {
                    listener.onRequestJoin(communitySysNoticeMsg.getCommunityUid(), communitySysNoticeMsg.getFromUserId(), communitySysNoticeMsg.getMessage());
                }
                break;
            case enabled:
                //取消禁言

                break;
            case disabled:
                //被禁言
                break;
        }
    }


    /**
     * 添加监听事件，统一处理超级群相关的业务
     *
     * @param listener
     */
    public void addReceiveMessageWrapperListener(OnReceiveMessageWrapperListener listener) {
        this.onReceiveMessageWrapperListeners.add(listener);
    }

    public void removeReceiveMessageWrapperListener(OnReceiveMessageWrapperListener listener) {
        if (onReceiveMessageWrapperListeners.contains(listener)) {
            onReceiveMessageWrapperListeners.remove(listener);
        }
    }

    /**
     * 超级群的消息改变监听
     *
     * @param listener
     */
    public void addUltraGroupMessageChangeListener(IRongCoreListener.UltraGroupMessageChangeListener listener) {
        this.ultraGroupMessageChangeListeners.add(listener);
    }


    public void removeUltraGroupMessageChangeListener(IRongCoreListener.UltraGroupMessageChangeListener listener) {
        if (ultraGroupMessageChangeListeners.contains(listener)) {
            ultraGroupMessageChangeListeners.remove(listener);
        }
    }

    public void addUltraGroupReadTimeListener(IRongCoreListener.UltraGroupReadTimeListener listener) {
        this.ultraGroupReadTimeListeners.add(listener);
    }

    public void removeUltraGroupReadTimeListener(IRongCoreListener.UltraGroupReadTimeListener listener) {
        if (ultraGroupReadTimeListeners.contains(listener)) {
            ultraGroupReadTimeListeners.remove(listener);
        }
    }

    public void addUltraGroupTypingStatusListener(IRongCoreListener.UltraGroupTypingStatusListener listener) {
        this.ultraGroupTypingStatusListeners.add(listener);
    }

    public void removeUltraGroupTypingStatusListener(IRongCoreListener.UltraGroupTypingStatusListener listener) {
        if (ultraGroupTypingStatusListeners.contains(listener)) {
            this.ultraGroupTypingStatusListeners.remove(listener);
        }
    }

    public void addIUltraGroupUserEventListener(IUltraGroupUserEventListener listener) {
        ultraGroupUserEventListeners.add(listener);
    }

    public void removeIUltraGroupUserEventListener(IUltraGroupUserEventListener listener) {
        if (ultraGroupUserEventListeners.contains(listener)) {
            ultraGroupUserEventListeners.remove(listener);
        }
    }

    public void addIUltraGroupChannelListener(IUltraGroupChannelListener listener) {
        ultraGroupChannelListeners.add(listener);
    }

    public void removeIUltraGroupChannelListener(IUltraGroupChannelListener listener) {
        if (ultraGroupChannelListeners.contains(listener)) {
            ultraGroupChannelListeners.remove(listener);
        }
    }

    public void addIUltraGroupChangeListener(IUltraGroupChangeListener listener) {
        iUltraGroupChangeListeners.add(listener);
    }

    public void removeIUltraGroupChangeListener(IUltraGroupChangeListener listener) {
        if (iUltraGroupChangeListeners.contains(listener)) {
            iUltraGroupChangeListeners.remove(listener);
        }
    }

    public void addIUltraGroupUserInfoUpdateListener(IUltraGroupUserInfoUpdateListener listener) {
        ultraGroupUserInfoUpdateListeners.add(listener);
    }

    public void removeIUltraGroupUserInfoUpdateListener(IUltraGroupUserInfoUpdateListener listener) {
        if (ultraGroupUserInfoUpdateListeners.contains(listener)) {
            ultraGroupUserInfoUpdateListeners.remove(listener);
        }
    }
}
