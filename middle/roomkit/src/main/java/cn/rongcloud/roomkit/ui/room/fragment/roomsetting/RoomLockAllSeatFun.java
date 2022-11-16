package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author gyn
 * @date 2021/9/30
 */
public class RoomLockAllSeatFun extends IFun.BaseFun {
    public RoomLockAllSeatFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        if (getStatus() == 1) {
            return R.drawable.ic_room_setting_unlock_all;
        } else {
            return R.drawable.ic_room_setting_lock_all;
        }
    }

    @Override
    public String getText() {
        if (getStatus() == 1) {
            return "解锁全座";
        } else {
            return "全麦锁座";
        }
    }
}
