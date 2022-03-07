package cn.rongcloud.voice.room.helper;

import com.basis.wapper.IResultBack;
import com.basis.wapper.IRoomCallBack;

import java.util.List;

import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.pk.bean.PKState;
import cn.rongcloud.roomkit.ui.miniroom.OnCloseMiniRoomListener;
import cn.rongcloud.roomkit.ui.room.dialog.shield.Shield;
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import cn.rongcloud.voice.inter.RoomListener;
import cn.rongcloud.voice.inter.StatusListener;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.rong.imlib.model.MessageContent;

public interface IVoiceRoomHelp extends OnCloseMiniRoomListener {

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
    void register(String roomId);

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
     *
     * @param rcVoiceRoomEventListener
     */
    void removeRCVoiceRoomEventListener(RCVoiceRoomEventListener rcVoiceRoomEventListener);

    /**
     * 保存当前请求上麦的状态
     */
    void setCurrentStatus(int status);

    /**
     * 离开房间
     */
    void leaveRoom(IRoomCallBack callback);

    /**
     * 邀请上麦
     */
    void pickUserToSeat(String userId, ClickCallback<Boolean> callback);

    /**
     * 同意上麦
     */
    void acceptRequestSeat(String userId, ClickCallback<Boolean> callback);

    /**
     * 撤销麦位申请
     */
    void cancelRequestSeat(ClickCallback<Boolean> callback);

    /**
     * 锁麦
     */
    void lockSeat(int index, boolean isClose, ClickCallback<Boolean> callback);

    /**
     * 开麦或者静麦
     *
     * @param index
     * @param isMute
     * @param callback
     */
    void muteSeat(int index, boolean isMute, ClickCallback<Boolean> callback);

    /**
     * 踢出房间
     */
    void kickUserFromRoom(User user, ClickCallback<Boolean> callback);

    /**
     * 抱下麦位
     *
     * @param user
     * @param callback
     */
    void kickUserFromSeat(User user, ClickCallback<Boolean> callback);

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

    /**
     * 获取当前PK状态
     *
     * @return pk状态
     */
    PKState getPKState();

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
     * 设置是否静音
     */
    void setMuteAllRemoteStreams(boolean isMute);

    /**
     * 是否静音
     */
    boolean getMuteAllRemoteStreams();

    /**
     * 获取可用麦位索引
     *
     * @return 可用麦位索引
     */
    int getAvailableSeatIndex();

    List<Shield> getShield();

    void sendMessage(MessageContent messageContent);

    boolean isShowingMessage(MessageContent content);
}