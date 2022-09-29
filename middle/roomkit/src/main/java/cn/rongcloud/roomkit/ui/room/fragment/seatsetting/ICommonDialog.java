package cn.rongcloud.roomkit.ui.room.fragment.seatsetting;

/**
 * @author lihao
 * @project RongRTCDemo
 * @date 2021/11/18
 * @time 4:56 下午
 * 用来展示公共的弹窗
 */
public interface ICommonDialog {

    /**
     * 展示邀请和申请弹窗
     *
     * @param index 滑动到第index fragment
     * @param seatIndex  邀请的位置
     */
    void showSeatOperationViewPagerFragment(int index ,int seatIndex);

    /**
     * 展示撤销弹窗
     */
    void showRevokeSeatRequestFragment();
}
