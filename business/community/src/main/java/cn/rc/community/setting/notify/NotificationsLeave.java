package cn.rc.community.setting.notify;

import io.rong.imlib.IRongCoreEnum;

public enum NotificationsLeave {

    FOLLOW("跟随社区设置", IRongCoreEnum.PushNotificationLevel.PUSH_NOTIFICATION_LEVEL_DEFAULT),
    All("所有消息都通知", IRongCoreEnum.PushNotificationLevel.PUSH_NOTIFICATION_LEVEL_ALL_MESSAGE),
    ONLY("仅被@时通知", IRongCoreEnum.PushNotificationLevel.PUSH_NOTIFICATION_LEVEL_MENTION),
    NONE("从不通知", IRongCoreEnum.PushNotificationLevel.PUSH_NOTIFICATION_LEVEL_BLOCKED);

    private final String des;
    private final IRongCoreEnum.PushNotificationLevel level;

    NotificationsLeave(String des, IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
        this.des = des;
        this.level = pushNotificationLevel;
    }

    public String getDes() {
        return des;
    }

    public IRongCoreEnum.PushNotificationLevel getLevel() {
        return level;
    }

    public static NotificationsLeave valued(IRongCoreEnum.PushNotificationLevel pushNotificationLevel) {
        if (pushNotificationLevel == All.level) {
            return All;
        } else if (pushNotificationLevel == ONLY.level) {
            return ONLY;
        } else if (pushNotificationLevel == NONE.level) {
            return NONE;
        }
        return FOLLOW;
    }

    public static NotificationsLeave valued(String noticeDes) {
        if (noticeDes.equals(All.getDes())) {
            return All;
        } else if (noticeDes.equals(ONLY.getDes())) {
            return ONLY;
        } else if (noticeDes.equals(NONE.getDes())) {
            return NONE;
        }
        return FOLLOW;
    }
}
