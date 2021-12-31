package cn.rongcloud.live.room;


import androidx.lifecycle.MutableLiveData;

import com.basis.mvp.IBasePresent;

import cn.rong.combusis.provider.voiceroom.RoomOwnerType;
import cn.rong.combusis.provider.voiceroom.VoiceRoomBean;
import cn.rong.combusis.ui.room.fragment.ClickCallback;
import cn.rong.combusis.ui.room.fragment.roomsetting.IFun;
import io.rong.imlib.model.MessageContent;


/**
 * 直播房Present接口
 */
interface ILiveRoomPresent extends IBasePresent {

    /**
     * 发送消息
     *
     * @param messageContent
     */
    void sendMessage(MessageContent messageContent);

    /**
     * 直播房发送消息
     *
     * @param messageContent 消息体
     * @param isShowLocation 是否显示在本地
     */
    void sendMessage(MessageContent messageContent, boolean isShowLocation);

    /**
     * 申请上麦
     */
    void requestSeat(int position);

    /**
     * 房间上锁或者取消上锁
     */
    void setRoomPassword(boolean isPrivate, String password, MutableLiveData<IFun.BaseFun> item, String mRoomId);

    /**
     * 设置房间名称
     *
     * @param name
     */
    void setRoomName(String name, String roomId);

    /**
     * 切换房间上麦模式
     */
    void setSeatMode(boolean isFreeEnterSeat);

    /**
     * 当前用户的身份类型
     *
     * @return
     */
    RoomOwnerType getRoomOwnerType();

    /**
     * 设置当前房间
     */
    void setCurrentRoom(VoiceRoomBean mVoiceRoomBean, boolean isCreate);

    /**
     * 设置直播房的各种消息监听
     *
     * @param roomId
     */
    void initLiveRoomListener(String roomId);

    /**
     * 取消直播房的各种监听
     */
    void unInitLiveRoomListener();

    /**
     * 获取礼物
     *
     * @param roomId
     */
    void getGiftCount(String roomId);

    /**
     * 确定修改公告
     *
     * @param notice
     */
    void modifyNotice(String notice);

    /**
     * 发送礼物
     */
    void sendGift();

    /**
     * 准备直播
     */
    void prepare(String roomId, boolean isCreate);

    /**
     * 开始直播
     */
    void begin(String roomId, boolean isCreate);

    /**
     * 关闭直播间
     */
    void finishLiveRoom();

    /**
     * 离开直播间
     */
    void leaveLiveRoom(ClickCallback callBack);
}
