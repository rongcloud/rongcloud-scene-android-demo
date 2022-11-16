package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author lihao
 * @project RongRTCDemo
 * @date 2021/11/16
 * @time 6:02 下午
 */
public class RoomBeautyMakeUpFun extends IFun.BaseFun {

    public RoomBeautyMakeUpFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_room_setting_beauty_makeup;
    }

    @Override
    public String getText() {
        return "美妆";
    }
}
