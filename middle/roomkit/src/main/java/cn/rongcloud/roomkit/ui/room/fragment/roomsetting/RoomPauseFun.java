package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author gyn
 * @date 2021/9/30
 */
public class RoomPauseFun extends IFun.BaseFun {
    public RoomPauseFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        if (getStatus() == 1) {
            return R.drawable.ic_room_setting_pause;
        } else {
            return R.drawable.ic_room_setting_pause;
        }
    }

    @Override
    public String getText() {
        if (getStatus() == 1) {
            return "恢复直播";
        } else {
            return "暂停直播";
        }
    }
}
