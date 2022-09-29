package cn.rongcloud.pk.bean;

/**
 * @author gyn
 * @date 2022/1/18
 */
public class PKInviteInfo {
    //邀请人ID
    private String inviterUserId;
    //邀请人房间ID
    private String inviterRoomId;
    //被邀请人ID
    private String inviteeUserId;
    //被邀请人房间ID
    private String inviteeRoomId;

    public PKInviteInfo(String inviterUserId, String inviterRoomId, String inviteeUserId, String inviteeRoomId) {
        this.inviterUserId = inviterUserId;
        this.inviterRoomId = inviterRoomId;
        this.inviteeUserId = inviteeUserId;
        this.inviteeRoomId = inviteeRoomId;
    }

    public String getInviterUserId() {
        return inviterUserId;
    }

    public String getInviterRoomId() {
        return inviterRoomId;
    }

    public String getInviteeUserId() {
        return inviteeUserId;
    }

    public String getInviteeRoomId() {
        return inviteeRoomId;
    }
}
