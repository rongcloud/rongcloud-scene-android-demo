package cn.rong.combusis.sdk.event.wrapper;

public enum TipType {
    InvitedSeat("上麦邀请"),
    RequestSeat("上麦申请"),
    InvitedPK("PK邀请");
    private String value;

    TipType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
