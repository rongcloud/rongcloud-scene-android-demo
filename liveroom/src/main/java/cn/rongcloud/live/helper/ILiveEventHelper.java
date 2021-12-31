package cn.rongcloud.live.helper;

import java.util.List;
import java.util.Map;

import cn.rong.combusis.provider.user.User;
import cn.rong.combusis.provider.voiceroom.CurrentStatusType;
import cn.rong.combusis.sdk.event.listener.LeaveRoomCallBack;
import cn.rong.combusis.ui.room.fragment.ClickCallback;
import cn.rongcloud.liveroom.api.RCHolder;
import cn.rongcloud.liveroom.api.callback.RCLiveCallback;
import io.rong.imlib.model.MessageContent;

/**
 * @author lihao
 * @project RongRTCDemo
 * @date 2021/11/16
 * @time 5:26 下午
 */
public interface ILiveEventHelper {

    /**
     * 注册
     *
     * @param roomId
     */
    void register(String roomId);

    /**
     * 获取hodele
     */
    RCHolder getHold(int index);

    /**
     * 反注册
     */
    void unRegister();

    /**
     * 发送消息
     *
     * @param messageContent
     */
    void sendMessage(MessageContent messageContent, boolean isShowLocation);

    /**
     * 获取当前用户的麦位状态
     */
    CurrentStatusType getCurrentStatus();

    /**
     * 保存当前请求上麦的状态
     */
    void setCurrentStatus(CurrentStatusType currentStatus);

    /**
     * 离开房间
     */
    void leaveRoom(LeaveRoomCallBack callback);

    /**
     * 加入房间
     */
    void joinRoom(String roomId, ClickCallback<Boolean> callback);

    /**
     * 邀请上麦
     */
    void pickUserToSeat(String userId, int index, ClickCallback<Boolean> callback);

    /**
     * 取消上麦邀请
     *
     * @param userId   目标用户id
     * @param callback 结果回调
     */
    void cancelInvitation(String userId, ClickCallback<Boolean> callback);

    /**
     * 同意上麦
     */
    void acceptRequestSeat(String userId, ClickCallback<Boolean> callback);

    /**
     * 拒绝上麦申请
     */
    void rejectRequestSeat(String userId, ClickCallback<Boolean> callback);

    /**
     * 撤销麦位申请
     */
    void cancelRequestSeat(ClickCallback<Boolean> callback);

    /**
     * 锁麦
     */
    void lockSeat(int index, boolean isClose, ClickCallback<Boolean> callback);

    /**
     * 切换麦位
     */
    void swichToSeat(int seatIndex, ClickCallback<Boolean> callback);

    /**
     * 开麦或者静麦
     *
     * @param index
     * @param isMute
     * @param callback
     */
    void muteSeat(int index, boolean isMute, ClickCallback<Boolean> callback);

    /**
     * 切换视频和语音
     */
    void switchVideoOrAudio(int index, boolean isVideo, ClickCallback<Boolean> callback);

    /**
     * 打开或者关闭麦克风
     */
    void MuteSelf(int index, boolean isMute, ClickCallback<Boolean> callback);

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
     * 抱上麦位
     */
    void pickUpSeat(int targetIndex, String userId, RCLiveCallback callback);

    /**
     * 更改所属于房间
     *
     * @param roomId
     */
    void changeUserRoom(String roomId);

    /**
     * 关闭房间
     */
    void finishRoom(ClickCallback<Boolean> callback);

    /**
     * 开始直播
     *
     * @param roomId
     * @param callback
     */
    void begin(String roomId, ClickCallback<Boolean> callback);

    /**
     * 准备直播
     */
    void prepare(ClickCallback<Boolean> callback);

    /**
     * 请求上麦直播
     */
    void requestLiveVideo(int index, ClickCallback<Boolean> callback);

    /**
     * 上麦直播
     */
    void enterSeat(int index, ClickCallback<Boolean> callback);

    /**
     * 下麦
     */
    void leaveSeat(ClickCallback<Boolean> callback);

    /**
     * 更新房间的KV消息
     */
    void updateRoomInfoKv(String key, String vaule, ClickCallback<Boolean> callback);

    /**
     * 更新房间的KV消息
     */
    void getRoomInfoByKey(String key, ClickCallback<Boolean> callback);

    /**
     * 获取房间内的KV消息
     */
    void getRoomInfoByKey(List<String> keys, ClickCallback<Map<String, String>> callback);


    /**
     * 获取申请上麦用户人数
     */
    void getRequestLiveVideoIds(ClickCallback<List<String>> callback);

//    /**
//     * 获取当前邀请上麦的用户人数
//     */
//    void getInvitateLiveVideoIds(ClickCallback<List<String>> callback);


}
