package cn.rongcloud.pk.bean;

/**
 * pk中 被邀请者信息
 * 1.发起邀请api是保存
 * 2.取消邀请时释放（手动）
 * 3.接收到邀请响应释放
 */
public class PKInvitee {
    public String inviteeRoomId;
    public String inviteeId;
}