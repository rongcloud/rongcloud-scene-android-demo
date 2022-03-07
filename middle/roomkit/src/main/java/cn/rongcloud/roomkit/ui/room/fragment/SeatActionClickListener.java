package cn.rongcloud.roomkit.ui.room.fragment;


import cn.rongcloud.config.provider.user.User;

/**
 * @author lihao
 * @project RongRTCDemo
 * @date 2021/11/18
 * @time 10:44 上午
 * 麦位操作接口
 */
public interface SeatActionClickListener {

    /**
     * 邀请上麦
     *
     * @param seatIndex
     * @param user
     * @param callback
     */
    void clickInviteSeat(int seatIndex, User user, ClickCallback<Boolean> callback);

    /**
     * 同意上麦
     *
     * @param userId
     * @param callback
     */
    void acceptRequestSeat(String userId, ClickCallback<Boolean> callback);

    /**
     * 拒绝上麦
     *
     * @param userId
     * @param callback
     */
    void rejectRequestSeat(String userId, ClickCallback<Boolean> callback);

    /**
     * 撤销麦位申请
     */
    void cancelRequestSeat(ClickCallback<Boolean> callback);

    /**
     * 取消上麦邀请
     *
     * @param userId   目标用户id
     * @param callback 结果回调
     */
    void cancelInvitation(String userId, ClickCallback<Boolean> callback);

    /**
     * 下麦
     *
     * @param user
     * @param callback
     */
    void clickKickSeat(User user, ClickCallback<Boolean> callback);

    /**
     * 开麦或者禁麦
     *
     * @param seatIndex
     * @param isMute
     * @param callback
     */
    void clickMuteSeat(int seatIndex, boolean isMute, ClickCallback<Boolean> callback);

    /**
     * 关闭座位或者打开座位
     *
     * @param seatIndex
     * @param isLock
     * @param callback
     */
    void clickCloseSeat(int seatIndex, boolean isLock, ClickCallback<Boolean> callback);

    /**
     * 切换麦位
     */
    void switchToSeat(int seatIndex, ClickCallback<Boolean> callback);
}
