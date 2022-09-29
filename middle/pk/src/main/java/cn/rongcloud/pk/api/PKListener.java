package cn.rongcloud.pk.api;

import com.basis.wapper.IResultBack;

import cn.rongcloud.pk.bean.PKResponse;
import cn.rongcloud.pk.bean.PKState;

/**
 * @author gyn
 * @date 2022/1/12
 */
public interface PKListener {
    /**
     * pk开始
     */
    void onPkStart();

    /**
     * pk结束：1、惩罚记时结束，2、手动退出PK
     */
    void onPkStop();

    /**
     * 锁定并踢出麦位上的人
     */
    void lockAllAndKickOut();

    /**
     * 解锁麦位
     */
    void unLockAll();

    /**
     * 如果服务端已经结束PK，SDk还是pk状态，则结束pk
     */
    void quitPKIfPKing();


    /**
     * 恢复pk
     *
     * @param pkRoomId   pk
     * @param pkUserId   pk的用户id
     * @param resultBack 回调
     */
    void resumePk(String pkRoomId, String pkUserId, IResultBack<Boolean> resultBack);

    /**
     * 收到邀请弹框后，处理邀请信息
     *
     * @param inviterRoomId
     * @param inviterUserId
     * @param pkResponse
     * @param resultBack
     */
    void responsePKInvitation(String inviterRoomId, String inviterUserId, PKResponse pkResponse, IResultBack<Boolean> resultBack);

    /**
     * 发起pk邀请
     *
     * @param inviteeRoomId
     * @param inviteeId
     * @param resultBack
     */
    void onSendPKInvitation(String inviteeRoomId, String inviteeId, IResultBack<Boolean> resultBack);

    /**
     * 取消pk邀请
     *
     * @param inviteeRoomId
     * @param inviteeId
     * @param resultBack
     */
    void onCancelPkInvitation(String inviteeRoomId, String inviteeId, IResultBack<Boolean> resultBack);

    /**
     * 结束pk
     */
    void onQuitPK(IResultBack<Boolean> resultBack);

    /**
     * pk 状态改变
     *
     * @param pkState pk状态
     */
    void onPkStateChanged(PKState pkState);

    void onSendPKStartMessage(String pkUserId);

    void onSendPKMessage(String content);

    void onMutePKUser(boolean isMute, IResultBack<Boolean> resultBack);
}
