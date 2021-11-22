package cn.rong.combusis.sdk;

import com.kit.wapper.IResultBack;

import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;

/**
 * 语聊房SDK api封装接口
 */
public interface Api {
    /**
     * notify room 的name
     */
    String EVENT_ROOM_CLOSE = "VoiceRoomClosed";
    String EVENT_BACKGROUND_CHANGE = "VoiceRoomBackgroundChanged";
    String EVENT_MANAGER_LIST_CHANGE = "VoiceRoomNeedRefreshManagerList";
    String EVENT_REJECT_MANAGE_PICK = "VoiceRoomRejectManagePick"; // 拒绝上麦
    String EVENT_AGREE_MANAGE_PICK = "VoiceRoomAgreeManagePick"; // 同意上麦
    String EVENT_KICK_OUT_OF_SEAT = "EVENT_KICK_OUT_OF_SEAT";
    String EVENT_REQUEST_SEAT_REFUSE = "EVENT_REQUEST_SEAT_REFUSE";
    String EVENT_REQUEST_SEAT_AGREE = "EVENT_REQUEST_SEAT_AGREE";
    String EVENT_REQUEST_SEAT_CANCEL = "EVENT_REQUEST_SEAT_CANCEL";
    String EVENT_USER_LEFT_SEAT = "EVENT_USER_LEFT_SEAT";
    String EVENT_ADD_SHIELD = "EVENT_ADD_SHIELD"; // 添加屏蔽词
    String EVENT_DELETE_SHIELD = "EVENT_DELETE_SHIELD"; // 删除屏蔽词

    String EVENT_KICKED_OUT_OF_ROOM = "EVENT_KICKED_OUT_OF_ROOM";

    /**
     * 获取房间实例 跟新使用同一实例
     *
     * @return
     */
    RCVoiceRoomInfo getRoomInfo();

    /**
     * 通知房间
     *
     * @param name
     * @param content
     */
    void notifyRoom(String name, String content);

    /**
     * 创建并加入房间
     *
     * @param roomId
     * @param roomInfo   getRoomInfo获取的实例
     * @param resultBack
     */
    void createAndJoin(String roomId, RCVoiceRoomInfo roomInfo, IResultBack<Boolean> resultBack);

    /**
     * 加入房间
     *
     * @param roomId
     * @param resultBack
     */
    void joinRoom(String roomId, IResultBack<Boolean> resultBack);

    /**
     * 离开房间
     *
     * @param resultBack
     */
    void leaveRoom(IResultBack<Boolean> resultBack);

    /**
     * 全麦锁定
     */
    void lockAll(boolean locked);

    /**
     * 锁定指定麦位
     *
     * @param index
     * @param locked
     * @param resultBack
     */
    void lockSeat(int index, boolean locked, IResultBack<Boolean> resultBack);

    /**
     * 全麦静音
     */
    void muteAll(boolean mute);


    /**
     * 静音指定麦位
     *
     * @param index
     * @param mute
     * @param resultBack
     */
    void muteSeat(int index, boolean mute, IResultBack<Boolean> resultBack);

    /**
     * 下麦
     *
     * @param resultBack
     */
    void leaveSeat(IResultBack<Boolean> resultBack);

    /**
     * 上麦
     *
     * @param index
     * @param resultBack
     */
    void enterSeat(int index, IResultBack<Boolean> resultBack);

    /**
     * 申请上麦
     *
     * @param resultBack
     */
    void requestSeat(IResultBack<Boolean> resultBack);

    /**
     * 取消上麦申请
     *
     * @param resultBack
     */
    void cancelRequestSeat(IResultBack<Boolean> resultBack);

    /**
     * 同意上麦
     *
     * @param userId
     * @param resultBack
     */
    void acceptRequestSeat(String userId, IResultBack<Boolean> resultBack);

    /**
     * 拒绝上麦
     *
     * @param userId
     * @param resultBack
     */
    void rejectRequestSeat(String userId, IResultBack<Boolean> resultBack);

    /**
     * 跟新麦位extra字段
     *
     * @param seatIndex
     * @param extra
     * @param resultBack
     */
    void updateSeatExtra(int seatIndex, String extra, IResultBack<Boolean> resultBack);

    /**
     * 跟新麦位count
     *
     * @param count
     * @param resultBack
     */
    void updateSeatCount(int count, IResultBack<Boolean> resultBack);

    /**
     * 跟新房间名称
     *
     * @param name
     * @param resultBack
     */
    void updateRoomName(String name, IResultBack<Boolean> resultBack);

    /**
     * 修改麦位上麦模式 setRoomInfo
     *
     * @param isFreeEnterSeat 是否自由上麦：ture 自由上麦，fasle 申请上麦
     * @param resultBack
     */
    void setSeatMode(Boolean isFreeEnterSeat, IResultBack<Boolean> resultBack);

    /**
     * 剔除默认 kickUserFromSeat
     *
     * @param userId     被踢的人id
     * @param resultBack
     */
    void kickSeat(String userId, IResultBack<Boolean> resultBack);

    /**
     * 设置麦位数 setRoomInfo
     *
     * @param seatCount  麦位数
     * @param resultBack
     */
    void setSeatCount(int seatCount, IResultBack<Boolean> resultBack);

//    /**
//     * 发送PK邀请
//     *
//     * @param inviteeRoomId 被邀请用户所在的房间id
//     * @param inviteeUserId 被邀请人的用户id
//     * @param resultBack    结果回调
//     */
//    void sendPKInvitation(String inviteeRoomId, String inviteeUserId, IResultBack<Boolean> resultBack);
//
//    /**
//     * 取消PK邀请
//     *
//     * @param resultBack 结果回调
//     */
//    void cancelPKInvitation(IResultBack<Boolean> resultBack);
//
//    /**
//     * 回复邀请人是否接受邀请
//     *
//     * @param inviterRoomId 邀请人所在的房间id
//     * @param inviterUserId 邀请人的用户id
//     * @param pkState       pk邀请的响应状态
//     * @param resultBack    结果回调
//     */
//    void responsePKInvitation(String inviterRoomId, String inviterUserId, PKResponse pkState, IResultBack<Boolean> resultBack);
//
//    /**
//     * 屏蔽PK对象的语音
//     *
//     * @param isMute     是否静音
//     * @param resultBack 结果回调
//     */
//    void mutePKUser(boolean isMute, IResultBack<Boolean> resultBack);
//
//    /**
//     * 退出PK
//     */
//    void quitPK(IResultBack<Boolean> resultBack);
//
//    /**
//     * 快速加入pk
//     *
//     * @param pkRoomId   pk房间
//     * @param pkUserId   pkId
//     * @param resultBack 结果回调
//     */
//    void resumePk(String pkRoomId, String pkUserId, IResultBack<Boolean> resultBack);
//
//    /**
//     * 释放pk被邀请者
//     */
//    void releasePKInvitee();
//
//    /**
//     * 获取当前被邀请人的信息
//     */
//    IEventHelp.PKInvitee getPKInvitee();
}
