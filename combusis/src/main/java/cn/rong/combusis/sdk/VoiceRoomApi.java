package cn.rong.combusis.sdk;

import android.text.TextUtils;

import com.kit.utils.KToast;
import com.kit.utils.Logger;
import com.kit.wapper.IResultBack;

import cn.rong.combusis.EventBus;
import cn.rong.combusis.sdk.event.EventHelper;
import cn.rong.combusis.sdk.event.wrapper.IEventHelp;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.model.PKResponse;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;

public class VoiceRoomApi implements Api {
    private final static String TAG = "VoiceRoomApi";
    private final static Api api = new VoiceRoomApi();
    private final RCVoiceRoomInfo roomInfo = new RCVoiceRoomInfo();
    private IEventHelp.PKInvitee pkInvitee;

    private VoiceRoomApi() {
    }

    public static Api getApi() {
        return api;
    }

    @Override
    public RCVoiceRoomInfo getRoomInfo() {
        return roomInfo;
    }

    /**
     * 邀请同意 上麦
     *
     * @param userId
     * @param resultBack
     */
    public void invitedEnterSeat(String userId, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().pickUserToSeat(
                userId, new DefaultRoomCallback("invitedIntoSeat", "邀请上麦", resultBack));
    }


    /**
     * 房间通知
     *
     * @param name
     * @param content
     */
    @Override
    public void notifyRoom(String name, String content) {
        RCVoiceRoomEngine.getInstance().notifyVoiceRoom(name, content, null);
    }

    /**
     * 创建并加入房间
     *
     * @param roomId     房间id
     * @param roomInfo   房间实体 建议使用getRoomInfo获取的对象 否则需要维护RCVoiceRoomInfo，跟新房间设置时
     * @param resultBack 回调
     */
    @Override
    public void createAndJoin(String roomId, RCVoiceRoomInfo roomInfo, IResultBack<Boolean> resultBack) {
        if (TextUtils.isEmpty(roomId)) {
            if (null != resultBack) resultBack.onResult(false);
            return;
        }
        if (null == roomInfo || TextUtils.isEmpty(roomInfo.getRoomName()) || roomInfo.getSeatCount() < 1) {
            if (null != resultBack) resultBack.onResult(false);
            return;
        }
        RCVoiceRoomEngine.getInstance().createAndJoinRoom(roomId, roomInfo,
                new DefaultRoomCallback(
                        "createAndJoin",
                        "创建并加入房间",
                        resultBack));
    }

    @Override
    public void joinRoom(String roomId, IResultBack<Boolean> resultBack) {
        if (TextUtils.isEmpty(roomId)) {
            if (null != resultBack) resultBack.onResult(false);
            return;
        }
        RCVoiceRoomEngine.getInstance().joinRoom(roomId,
                new DefaultRoomCallback(
                        "joinRoom",
                        "加入房间",
                        resultBack));
    }

    @Override
    public void leaveRoom(IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().leaveRoom(
                new DefaultRoomCallback(
                        "leaveRoom",
                        "离开房间",
                        resultBack));
    }

    @Override
    public void lockAll(boolean locked) {
        RCVoiceRoomEngine.getInstance().lockOtherSeats(locked, null);
//        KToast.show(locked ? "全麦锁定成功" : "全麦解锁成功");
    }

    @Override
    public void muteAll(boolean mute) {
        RCVoiceRoomEngine.getInstance().muteOtherSeats(mute, null);
//        KToast.show(mute ? "全麦静音成功" : "全麦取消静音成功");
    }

    /**
     * 锁麦
     *
     * @param index
     * @param locked
     * @param resultBack
     */
    @Override
    public void lockSeat(int index, boolean locked, IResultBack<Boolean> resultBack) {
        String action = locked ? "麦位锁定" : "取消麦位解锁";
        RCVoiceRoomEngine.getInstance().lockSeat(
                index,
                locked,
                new DefaultRoomCallback(
                        "muteSeat",
                        "",
                        resultBack));
    }

    /**
     * 静麦
     *
     * @param index
     * @param mute
     * @param resultBack
     */
    @Override
    public void muteSeat(int index, boolean mute, IResultBack<Boolean> resultBack) {
        String action = mute ? "麦位静音" : "取消麦位静音";
        RCVoiceRoomEngine.getInstance().muteSeat(index, mute,
                new DefaultRoomCallback(
                        "muteSeat",
                        action,
                        resultBack));
    }

    /**
     * 下麦
     *
     * @param resultBack
     */
    @Override
    public void leaveSeat(IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().leaveSeat(
                new DefaultRoomCallback(
                        "leaveSeat",
                        "下麦",
                        resultBack));
    }

    /**
     * 上麦
     *
     * @param index
     * @param resultBack
     */
    @Override
    public void enterSeat(int index, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().enterSeat(
                index,
                new DefaultRoomCallback(
                        "enterSeat",
                        "上麦",
                        resultBack));
    }

    /**
     * 申请上麦
     *
     * @param resultBack
     */
    @Override
    public void requestSeat(IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().requestSeat(
                new DefaultRoomCallback(
                        "requestSeat",
                        "申请上麦",
                        resultBack));
    }

    /**
     * 撤销上麦申请
     *
     * @param resultBack
     */
    @Override
    public void cancelRequestSeat(IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().cancelRequestSeat(
                new DefaultRoomCallback("cancelRequestSeat", "取消排麦", resultBack));
    }

    /**
     * 管理员/房主：同意上麦申请
     *
     * @param userId
     * @param resultBack
     */
    @Override
    public void acceptRequestSeat(String userId, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().acceptRequestSeat(userId,
                new DefaultRoomCallback("acceptRequestSeat", "同意排麦请求", resultBack));
    }

    @Override
    public void rejectRequestSeat(String userId, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().rejectRequestSeat(userId,
                new DefaultRoomCallback("rejectRequestSeat", "拒绝排麦请求", resultBack));
    }

    @Override
    public void updateSeatExtra(int seatIndex, String extra, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().updateSeatInfo(seatIndex, extra,
                new DefaultRoomCallback("updateSeatExtra", "更新扩展属性", resultBack));
    }

    @Override
    public void updateSeatCount(int count, IResultBack<Boolean> resultBack) {
        roomInfo.setSeatCount(count);
        updateRoomInfo(roomInfo, resultBack);
    }

    @Override
    public void updateRoomName(String name, IResultBack<Boolean> resultBack) {
        roomInfo.setRoomName(name);
        updateRoomInfo(roomInfo, resultBack);
    }

    @Override
    public void setSeatMode(Boolean isFreeEnterSeat, IResultBack<Boolean> resultBack) {
        Logger.e(TAG, "setSeatMode:" + isFreeEnterSeat);
        roomInfo.setFreeEnterSeat(isFreeEnterSeat);
        updateRoomInfo(roomInfo, resultBack);
    }

    @Override
    public void kickSeat(String userId, IResultBack<Boolean> resultBack) {
        Logger.e(TAG, "kickSeat:" + userId);
        RCVoiceRoomEngine.getInstance().kickUserFromSeat(userId,
                new DefaultRoomCallback(
                        "kickSeat",
                        "剔除用户",
                        resultBack));
    }

    @Override
    public void setSeatCount(int seatCount, IResultBack<Boolean> resultBack) {
        Logger.e(TAG, "setSeatCount:" + seatCount);
        roomInfo.setSeatCount(seatCount);
        updateRoomInfo(roomInfo, resultBack);
    }

    /**
     * 为避免重置未修改属性，建议跟新和创建时传入相同的对象，
     *
     * @param roomInfo
     * @param resultBack
     */
    private void updateRoomInfo(RCVoiceRoomInfo roomInfo, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().setRoomInfo(roomInfo,
                new DefaultRoomCallback(
                        "updateRoomInfo",
                        "修改房间信息",
                        resultBack));
    }

    @Override
    public void sendPKInvitation(String inviteeRoomId, String inviteeId, IResultBack<Boolean> resultBack) {
        //保存pk信息
        pkInvitee = new IEventHelp.PKInvitee();
        pkInvitee.inviteeRoomId = inviteeRoomId;
        pkInvitee.inviteeId = inviteeId;
        //邀请
        RCVoiceRoomEngine.getInstance().sendPKInvitation(
                inviteeRoomId,
                inviteeId,
                new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        KToast.show("发送PK邀请成功");
                        // 邀请成功 修改pk状态
                        EventBus.get().emit(EventBus.TAG.PK_INVITE_QUIT, IEventHelp.Type.PK_INVITE);
                        if (null != resultBack) resultBack.onResult(true);
                    }

                    @Override
                    public void onError(int i, String s) {
                        KToast.show("发送PK邀请失败");
                        Logger.e(TAG, "sendPKInvitation#onError [" + i + "]:" + s);
                        if (null != resultBack) resultBack.onResult(false);
                    }
                });
    }

    @Override
    public void cancelPKInvitation(IResultBack<Boolean> resultBack) {
        if (null == pkInvitee) {
            KToast.show("您还未发出PK邀请");
            if (null != resultBack) resultBack.onResult(false);
            return;
        }
        RCVoiceRoomEngine.getInstance().cancelPKInvitation(
                pkInvitee.inviteeRoomId,
                pkInvitee.inviteeId,
                new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        KToast.show("取消PK邀请成功");
                        // 邀请成功 修改pk状态
                        EventBus.get().emit(EventBus.TAG.PK_INVITE_QUIT, IEventHelp.Type.PK_NONE);
                        if (null != resultBack) resultBack.onResult(true);
                    }

                    @Override
                    public void onError(int i, String s) {
                        KToast.show("取消PK邀请失败");
                        Logger.e(TAG, "cancelPKInvitation#onError [" + i + "]:" + s);
                        if (null != resultBack) resultBack.onResult(false);
                    }
                });
    }

    @Override
    public void responsePKInvitation(String inviterRoomId, String inviterUserId, PKResponse pkState, IResultBack<Boolean> resultBack) {
//        String action = (pkState == PKResponse.accept ? "PK同意" : pkState == PKResponse.reject ? "拒绝" : "忽略") + "PK邀请";
        RCVoiceRoomEngine.getInstance().responsePKInvitation(
                inviterRoomId,
                inviterUserId,
                pkState,
                new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        if (PKResponse.accept == pkState) {
                            KToast.show("同意邀请");
                        } else if (PKResponse.reject == pkState) {
                            KToast.show("已拒绝PK邀请");
                        } else {
                            KToast.show("邀请被取消");
                        }
                        if (null != resultBack) resultBack.onResult(true);
                        // 当前邀请人信息
                        IEventHelp.PKInviter pkInviter = EventHelper.helper().getPKInviter();
                        //判断是否是当前正在邀请的信息
                        if (pkInviter.inviterId.equals(inviterUserId) && pkInviter.inviterRoomId.equals(inviterRoomId)) {
                            //处理成功后 该邀请流程结束 释放被邀请信息
                            EventHelper.helper().releasePKInviter();
                        }
                        //修改当前pk状态:拒绝和忽略需重置状态none
                        if (PKResponse.ignore == pkState || PKResponse.reject == pkState) {
                            EventBus.get().emit(EventBus.TAG.PK_INVITE_QUIT, IEventHelp.Type.PK_NONE);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        KToast.show("响应PK邀请失败");
                        Logger.e(TAG, "responsePKInvitation#onError [" + i + "]:" + s);
                        if (null != resultBack) resultBack.onResult(false);
                    }
                });
    }

    @Override
    public void mutePKUser(boolean isMute, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().mutePKUser(
                isMute,
                new DefaultRoomCallback("mutePKUser", isMute ? "屏蔽音频" : "取消屏蔽音频", resultBack));
    }

    @Override
    public void quitPK(IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().quitPK(
                new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        Logger.e(TAG, "quitPK#onSuccess ");
                        //修改pk状态
                        EventBus.get().emit(EventBus.TAG.PK_INVITE_QUIT, IEventHelp.Type.PK_NONE);
                        if (null != resultBack) resultBack.onResult(true);
                    }

                    @Override
                    public void onError(int i, String s) {
                        Logger.e(TAG, "quitPK#onError [" + i + "]:" + s);
                        if (null != resultBack) resultBack.onResult(false);
                    }
                });

    }

    public void resumePk(String pkRoomId, String pkUserId, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().resumePk(pkRoomId, pkUserId, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                if (null != resultBack) resultBack.onResult(true);
            }

            @Override
            public void onError(int i, String s) {
                Logger.e(TAG, "quickStartPk#onError [" + i + "]:" + s);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    @Override
    public void releasePKInvitee() {
        pkInvitee = null;
    }

    @Override
    public IEventHelp.PKInvitee getPKInvitee() {
        return pkInvitee;
    }
}
