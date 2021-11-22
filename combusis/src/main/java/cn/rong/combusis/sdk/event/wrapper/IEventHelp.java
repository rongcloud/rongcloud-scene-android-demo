package cn.rong.combusis.sdk.event.wrapper;

import com.kit.wapper.IResultBack;

import java.util.List;

import cn.rong.combusis.sdk.event.listener.LeaveRoomCallBack;
import cn.rong.combusis.sdk.event.listener.RoomListener;
import cn.rong.combusis.sdk.event.listener.StatusListener;
import cn.rong.combusis.widget.miniroom.OnCloseMiniRoomListener;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.rong.imlib.model.MessageContent;

public interface IEventHelp extends OnCloseMiniRoomListener {

    /**
     * 是否初始化
     *
     * @return 是否初始化
     */
    boolean isInitlaized();

    /**
     * 注册房间事件 加入房间前调用
     *
     * @param roomId 房间id
     */
    void regeister(String roomId);

    /**
     * 获取room info
     *
     * @return room info
     */
    RCVoiceRoomInfo getRoomInfo();

    /**
     * 根据用户id获取麦位信息
     *
     * @param userId 用户id
     * @return 麦位信息
     */
    RCVoiceSeatInfo getSeatInfo(String userId);

    /**
     * 取消房间事件注册 退出房间后调用
     */
    void unregeister();

    /**
     * 添加房间事件监听
     *
     * @param listener 房间监听
     */
    void addRoomListener(RoomListener listener);

    /**
     * 注册房间外部回调监听
     */
    void setRCVoiceRoomEventListener(RCVoiceRoomEventListener rcVoiceRoomEventListener);

    /**
     * 取消外部监听
     */
    void removeRCVoiceRoomEventListener();

    /**
     * 离开房间
     */
    void leaveRoom(LeaveRoomCallBack callback);

    /**
     * 更改所属于房间
     *
     * @param roomId
     */
    void changeUserRoom(String roomId);

    /**
     * 获取当前状态
     */
    int getCurrentStatus();

    /**
     * 保存当前请求上麦的状态
     */
    void setCurrentStatus(int status);

    /**
     * 添加网络延迟监听
     *
     * @param listener 网络监听
     */
    void addStatusListener(StatusListener listener);

    /**
     * 根据用户index获取麦位信息
     *
     * @param index 索引
     * @return 麦位信息
     */
    RCVoiceSeatInfo getSeatInfo(int index);

    /**
     * 获取房间在线用户id集合
     *
     * @param roomId     房间
     * @param resultBack 回调
     */
    void getOnLineUserIds(String roomId, IResultBack<List<String>> resultBack);

    /**
     * 获取未读消息数据
     *
     * @param roomId     房间
     * @param resultBack 回调
     */
    void getUnReadMegCount(String roomId, IResultBack<Integer> resultBack);

    /**
     * 获取申请麦位用户id
     *
     * @param resultBack 回调
     */
    void getRequestSeatUserIds(IResultBack<List<String>> resultBack);

//    /**
//     * 获取当前PK状态
//     *
//     * @return pk状态
//     */
//    Type getPKState();

    /**
     * 获取当前房间ID
     */
    String getRoomId();

    /**
     * 房间公屏消息
     */
    void addMessage(MessageContent message);

    /**
     * 获取房间公屏消息
     */
    List<MessageContent> getMessageList();

    /**
     * 获取麦位信息
     */
    List<RCVoiceSeatInfo> getRCVoiceSeatInfoList();

    /**
     * 是否静音
     */
    boolean getMuteAllRemoteStreams();

    /**
     * 设置是否静音
     */
    void setMuteAllRemoteStreams(boolean isMute);

    /**
     * 获取可用麦位索引
     *
     * @return 可用麦位索引
     */
    int getAvailableSeatIndex();

    /**
     * 获取当前pk邀请者的信息
     */
//    PKInviter getPKInviter();

    /**
     * 释放PK邀请者
     */
//    void releasePKInviter();

    enum Type {
        PK_NONE,//默认状态
        PK_INVITE,//邀请状态
        // pk 中
        PK_GOING,//sdk pk进行中
        PK_START,//ui pk进行中
        PK_PUNISH,//ui pk惩罚
        // pk 结束
        PK_STOP,//ui pk结束
        PK_FINISH//pk关闭状态
    }


    /**
     * pk邀请者信息
     * 1.接收到pk邀请保存
     * 2.邀请被取消释放
     * 3.响应pk邀请时释放（手动）
     */
    class PKInviter {
        public String inviterRoomId;
        public String inviterId;
    }

    /**
     * pk中 被邀请者信息
     * 1.发起邀请api是保存
     * 2.取消邀请时释放（手动）
     * 3.接收到邀请响应释放
     */
    class PKInvitee {
        public String inviteeRoomId;
        public String inviteeId;
    }
}