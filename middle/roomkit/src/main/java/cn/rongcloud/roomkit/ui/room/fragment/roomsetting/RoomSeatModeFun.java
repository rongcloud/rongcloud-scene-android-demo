package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author gyn
 * @date 2021/9/30
 */
public class RoomSeatModeFun extends IFun.BaseFun {
    public RoomSeatModeFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        if (getStatus() == 1) {
            return R.drawable.ic_room_setting_request_seat_model;
        } else {
            return R.drawable.ic_room_setting_free_enter_model;
        }
    }

    @Override
    public String getText() {
        if (getStatus() == 1) {
            return "申请上麦";
        } else {
            return "自由上麦";
        }
    }
}
