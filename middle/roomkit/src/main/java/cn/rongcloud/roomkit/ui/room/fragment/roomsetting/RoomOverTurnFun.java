package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author lihao
 * @project RongRTCDemo
 * @date 2021/11/16
 * @time 6:02 下午
 */
public class RoomOverTurnFun extends IFun.BaseFun {

    public RoomOverTurnFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_room_setting_overturn;
    }

    @Override
    public String getText() {
        return "翻转";
    }
}
