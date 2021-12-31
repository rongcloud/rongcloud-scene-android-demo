package cn.rong.combusis.sdk.event.wrapper;

import androidx.annotation.NonNull;

import com.kit.cache.GsonUtil;
import com.kit.utils.Logger;
import com.kit.wapper.IResultBack;

import cn.rong.combusis.EventBus;
import cn.rong.combusis.message.RCChatroomPK;
import cn.rong.combusis.message.RCChatroomPKGift;
import cn.rong.combusis.sdk.VoiceRoomApi;
import cn.rongcloud.voiceroom.model.PKResponse;
import cn.rongcloud.voiceroom.model.RCPKInfo;
import io.rong.imlib.model.Message;

/**
 * 实现PK相关回调
 */
public abstract class AbsPKHelper extends AbsEvenHelper {
    //pk 邀请者
    protected PKInviter pkInviter;

    protected Type current = Type.PK_NONE;

    @Override
    protected void init(String roomId) {
        super.init(roomId);
        current = Type.PK_NONE;
        //由于手动调quitPK 不会回调onPkFinish，因此需要在调用api成功后修改current状态
        EventBus.get().on(EventBus.TAG.PK_INVITE_QUIT, new EventBus.EventCallback() {
            @Override
            public void onEvent(String tag, Object... args) {
                if (null != args && args.length == 1) {
                    Logger.e(TAG, "on: current = " + current);
                    current = (Type) args[0];
                    dispatchPKState(null);
                }
            }
        });
        EventBus.get().on(EventBus.TAG.PK_AUTO_MODIFY, new EventBus.EventCallback() {
            @Override
            public void onEvent(String tag, Object... args) {
                if (null != args && args.length == 1) {
                    current = (Type) args[0];
                    Logger.e(TAG, "on: current = " + current);
                }
            }
        });
    }

    @Override
    protected void unInit() {
        super.unInit();
        current = Type.PK_NONE;
        EventBus.get().off(EventBus.TAG.PK_INVITE_QUIT, null);
        EventBus.get().off(EventBus.TAG.PK_AUTO_MODIFY, null);
    }

    @Override
    public Type getPKState() {
        return current;
    }

    @Override
    public void onMessageReceived(Message message) {
        super.onMessageReceived(message);
        handleBusinessPKState(message);
    }

    /**
     * 处理业务pk流在的状态
     *
     * @param message 消息
     */
    void handleBusinessPKState(Message message) {
        if (message.getContent() instanceof RCChatroomPK) {
            RCChatroomPK chatroomPK = (RCChatroomPK) message.getContent();
            String state = chatroomPK.getStatusMsg();
            Logger.e(TAG, "state = " + state);
            Logger.e(TAG, "chatroomPK = " + GsonUtil.obj2Json(chatroomPK));
            if ("0".equals(state)) {// 开始
                current = Type.PK_START;
                dispatchPKState(chatroomPK);
            } else if ("1".equals(state)) {//惩罚
                current = Type.PK_PUNISH;
                dispatchPKState(chatroomPK);
            } else {// pk finish
                current = Type.PK_STOP;
                dispatchPKState(chatroomPK);
            }
        } else if (message.getContent() instanceof RCChatroomPKGift) {
            EventBus.get().emit(EventBus.TAG.PK_GIFT);
        }
    }

    /**
     * PK开启成功
     *
     * @param rcpkInfo
     */
    @Override
    public void onPKGoing(@NonNull RCPKInfo rcpkInfo) {
        Logger.e(TAG, "onPKConnect");
        //邀请同意 开始PK 释放被邀请信息
        VoiceRoomApi.getApi().releasePKInvitee();
        // 开启pk 统一有服务端分发消息
        current = Type.PK_GOING;
        dispatchPKState(rcpkInfo);
    }

    /**
     * PK结束回调
     */
    @Override
    public void onPKFinish() {
        Logger.e(TAG, "onPKFinish");
        // 开启pk 统一有服务端分发消息
        current = Type.PK_FINISH;
        dispatchPKState(null);
    }

    /**
     * 接收到PK邀请回调
     *
     * @param inviterRoomId 发起邀请人的房间Id
     * @param inviterUserId 发起邀请人的Id
     */
    @Override
    public void onReveivePKInvitation(String inviterRoomId, String inviterUserId) {
        Logger.e(TAG, "onReveivePKInvitation");
        //保存邀请者信息
        pkInviter = new PKInviter();
        pkInviter.inviterRoomId = inviterRoomId;
        pkInviter.inviterId = inviterUserId;
        current = Type.PK_INVITE;
        dispatchPKState(null);
        onShowTipDialog(inviterRoomId, inviterUserId, TipType.InvitedPK, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {// true:同意  false：拒绝
                VoiceRoomApi.getApi().responsePKInvitation(inviterRoomId, inviterUserId, result ? PKResponse.accept : PKResponse.reject, null);
            }
        });
    }


    /**
     * PK邀请被取消
     *
     * @param roomId 发起邀请人的房间Id
     * @param userId 发起邀请人的Id
     */
    @Override
    public void onPKInvitationCanceled(String roomId, String userId) {
        Logger.e(TAG, "onPKInvitationCanceled");
        EventDialogHelper.helper().dismissDialog();
        if (null != pkInviter) {
            // 调用忽略是 邀请双方都会执行这个回调，但是主动调用忽略 会将pkInviter释放，可以判断
            EToast.showToast("邀请已被取消");
        }
        // 释放邀请者信息
        pkInviter = null;
        current = Type.PK_NONE;
        dispatchPKState(null);
    }

    /**
     * PK邀请被拒绝
     *
     * @param roomId 发起邀请人的房间Id
     * @param userId 发起邀请人的Id
     */
    @Override
    public void onPKInvitationRejected(String roomId, String userId) {
        Logger.e(TAG, "onPKInvitationRejected");
        IEventHelp.PKInvitee invitee = VoiceRoomApi.getApi().getPKInvitee();
        //判断是否是当前正在邀请的信息
        if (invitee.inviteeRoomId.equals(roomId) && invitee.inviteeId.equals(userId)) {
            dispatchPKResponse(PKResponse.reject);
            //邀请被忽略 该邀请流程结束 释放被邀请信息
            VoiceRoomApi.getApi().releasePKInvitee();
        }
        current = Type.PK_NONE;
        dispatchPKState(null);
    }

    @Override
    public void onPKInvitationIgnored(String roomId, String userId) {
        Logger.e(TAG, "onPKInvitationIgnored");
        IEventHelp.PKInvitee invitee = VoiceRoomApi.getApi().getPKInvitee();
        //判断是否是当前正在邀请的信息
        if (invitee.inviteeRoomId.equals(roomId) && invitee.inviteeId.equals(userId)) {
            dispatchPKResponse(PKResponse.ignore);
            //邀请被忽略 该邀请流程结束 释放被邀请信息
            VoiceRoomApi.getApi().releasePKInvitee();
        }
        current = Type.PK_NONE;
        dispatchPKState(null);
    }

    private void dispatchPKState(Object extra) {
        EventBus.get().emit(EventBus.TAG.PK_STATE, current, extra);
    }

    private void dispatchPKResponse(PKResponse pkState) {
        EventBus.get().emit(EventBus.TAG.PK_RESPONSE, pkState);
    }
}