package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author gyn
 * @date 2021/9/30
 */
public class RoomSeatSizeFun extends IFun.BaseFun {
    public RoomSeatSizeFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        if (getStatus() == 1) {
            return R.drawable.ic_room_setting_8_seat;
        } else {
            return R.drawable.ic_room_setting_4_seat;
        }
    }

    @Override
    public String getText() {
        if (getStatus() == 1) {
            return "设置 8 个座位";
        } else {
            return "设置 4 个座位";
        }
    }
}
