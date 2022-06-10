package cn.rc.community.helper;


import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.wapper.IResultBack;

import io.rong.imlib.ChannelClient;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.model.Conversation;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/26
 * @time 22:37
 * 超级群通知管理类
 */
public class UltraGroupNotificationLeaveManager {

    private volatile static UltraGroupNotificationLeaveManager _manager;

    public static UltraGroupNotificationLeaveManager get() {
        if (null == _manager) {
            synchronized (UltraGroupNotificationLeaveManager.class) {
                if (null == _manager) {
                    _manager = new UltraGroupNotificationLeaveManager();
                }
            }
        }
        return _manager;
    }


    /**
     * 设置指定的超级群的默认通知级别
     * 默认免打扰逻辑对所有群成员生效，由超级群的管理员进行设置
     *
     * @param targetId
     * @param pushNotificationLevel
     */
    public void setUltraGroupConversationDefaultNotificationLevel(String targetId, IRongCoreEnum.PushNotificationLevel pushNotificationLevel, IResultBack<Boolean> resultBack) {
        ChannelClient.getInstance().setUltraGroupConversationDefaultNotificationLevel(targetId, pushNotificationLevel, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                Logger.d("setConversationNotificationLevel：targetId:" + "targetId:" + pushNotificationLevel);
                if (resultBack != null) {
                    resultBack.onResult(true);
                }
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                if (resultBack != null) {
                    resultBack.onResult(false);
                    KToast.show(coreErrorCode.getMessage());
                }
            }
        });
    }

    /**
     * 获取超级群的默认免打扰级别
     * 如果没有设置默认的，那么就是全部消息都接受
     *
     * @param targetId
     */
    public void getUltraGroupConversationDefaultNotificationLevel(String targetId, IResultBack<IRongCoreEnum.PushNotificationLevel> resultBack) {
        ChannelClient.getInstance().getUltraGroupConversationDefaultNotificationLevel(targetId, new IRongCoreCallback.ResultCallback<IRongCoreEnum.PushNotificationLevel>() {
            @Override
            public void onSuccess(IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
                if (pushNotificationLevel == IRongCoreEnum.PushNotificationLevel.PUSH_NOTIFICATION_LEVEL_DEFAULT) {
                    pushNotificationLevel = IRongCoreEnum.PushNotificationLevel.PUSH_NOTIFICATION_LEVEL_ALL_MESSAGE;
                }
                if (resultBack != null) {
                    resultBack.onResult(pushNotificationLevel);
                }
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {

            }
        });
    }


    /**
     * 设置指定的超级群的通知级别
     *
     * @param targetId
     */
    public void setUltraGroupNotificationLevel(String targetId, IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
        ChannelClient.getInstance().setConversationNotificationLevel(Conversation.ConversationType.ULTRA_GROUP, targetId, pushNotificationLevel, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                Logger.d("setConversationNotificationLevel：targetId:" + "targetId:" + pushNotificationLevel);
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {

            }
        });
    }

    /**
     * 获取指定的超级群的通知级别，如果没有设置过，那么以超级群的默认通知级别为准
     * 如果自己没有设置过，此时会接受全部消息，那么将默认的通知级别设置给自己，那么以后自己就不再受默认配置的影响了
     *
     * @param targetId
     */
    public void getUltraGroupNotificationLevel(String targetId, IResultBack<IRongCoreEnum.PushNotificationLevel> resultBack) {
        ChannelClient.getInstance().getConversationNotificationLevel(Conversation.ConversationType.ULTRA_GROUP, targetId, new IRongCoreCallback.ResultCallback<IRongCoreEnum.PushNotificationLevel>() {
            @Override
            public void onSuccess(IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
                if (pushNotificationLevel == IRongCoreEnum.PushNotificationLevel.PUSH_NOTIFICATION_LEVEL_DEFAULT) {
                    getUltraGroupConversationDefaultNotificationLevel(targetId, new IResultBack<IRongCoreEnum.PushNotificationLevel>() {
                        @Override
                        public void onResult(IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
                            IRongCoreEnum.PushNotificationLevel level = pushNotificationLevel;
                            setUltraGroupNotificationLevel(targetId, level);
                            if (resultBack != null) {
                                resultBack.onResult(level);
                            }
                        }
                    });
                } else {
                    if (resultBack != null) {
                        resultBack.onResult(pushNotificationLevel);
                    }
                }
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {

            }
        });
    }

    /**
     * 设置超级群指定频道的推送级别
     *
     * @param targetId
     * @param channelId
     * @param pushNotificationLevel
     */
    public void setChannelNotificationLevel(String targetId, String channelId, IRongCoreEnum.PushNotificationLevel pushNotificationLevel, IResultBack<Boolean> resultBack) {
        ChannelClient.getInstance().setConversationChannelNotificationLevel(Conversation.ConversationType.ULTRA_GROUP, targetId, channelId, pushNotificationLevel, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                Logger.d("setChannelNotificationLevel：targetId:" + "targetId:" + pushNotificationLevel);
                if (resultBack != null) {
                    resultBack.onResult(true);
                }
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                if (resultBack != null) {
                    resultBack.onResult(false);
                    KToast.show(coreErrorCode.getMessage());
                }
            }
        });
    }

    /**
     * 获取超级群指定频道的推送级别
     * 如果没有特定的设置过，那么默认是跟随社区的设置
     *
     * @param targetId
     */
    public void getChannelNotificationLevel(String targetId, String channelId, IResultBack<IRongCoreEnum.PushNotificationLevel> resultBack) {
        ChannelClient.getInstance().getConversationChannelNotificationLevel(Conversation.ConversationType.ULTRA_GROUP, targetId, channelId, new IRongCoreCallback.ResultCallback<IRongCoreEnum.PushNotificationLevel>() {
            @Override
            public void onSuccess(IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
                //代表当前其实没有设置的，其实就是跟随社区
                if (resultBack != null) {
                    resultBack.onResult(pushNotificationLevel);
                }
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {

            }
        });
    }

}
