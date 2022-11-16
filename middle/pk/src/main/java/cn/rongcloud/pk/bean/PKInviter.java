package cn.rongcloud.pk.bean;

/**
 * pk邀请者信息
 * 1.接收到pk邀请保存
 * 2.邀请被取消释放
 * 3.响应pk邀请时释放（手动）
 */
public class PKInviter {
    public String inviterRoomId;
    public String inviterId;
}

