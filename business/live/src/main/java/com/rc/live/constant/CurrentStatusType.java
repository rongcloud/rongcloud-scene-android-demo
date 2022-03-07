package com.rc.live.constant;

/**
 * 当前用户的麦位状态
 */
public enum CurrentStatusType {
    /**
     * 不在麦位上
     */
    STATUS_NOT_ON_SEAT(0),
    /**
     * 申请了麦位，等待申请审批
     */
    STATUS_WAIT_FOR_SEAT(1),
    /**
     * 在麦位上
     */
    STATUS_ON_SEAT(2);


    int type;

    CurrentStatusType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}