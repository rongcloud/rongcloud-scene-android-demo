package cn.rc.community.setting.notify;

public enum NotifyType {

    FOLLOW("跟随社区设置", -1),
    All("所有消息都通知", 0),
    ONLY("仅被@时通知", 1),
    NONE("从不通知", 2);

    private final String des;
    private final int noticeCode;

    NotifyType(String des, int noticeCode) {
        this.des = des;
        this.noticeCode = noticeCode;
    }

    public String getDes() {
        return des;
    }

    public int getNoticeCode() {
        return noticeCode;
    }

    public static NotifyType valued(int noticeCode) {
        if (noticeCode == All.noticeCode) {
            return All;
        } else if (noticeCode == ONLY.noticeCode) {
            return ONLY;
        } else if (noticeCode == NONE.noticeCode) {
            return NONE;
        } else {
            return FOLLOW;
        }
    }

    public static NotifyType valued(String noticeDes) {
        if (noticeDes.equals(All.getDes())) {
            return All;
        } else if (noticeDes.equals(ONLY.getDes())) {
            return ONLY;
        } else if (noticeDes.equals(NONE.getDes())) {
            return NONE;
        } else {
            return FOLLOW;
        }
    }
}
