package com.rc.live.constant;

/**
 * 邀请用户状态
 */
public enum InviteStatusType {
    /**
     * 可以邀请
     */
    STATUS_NOT_INVITRED(0),
    /**
     * 正在邀请中，等待同意
     */
    STATUS_UNDER_INVITATION(1),
    /**
     * 连接中
     */
    STATUS_CONNECTTING(2);


    int type;

    InviteStatusType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}