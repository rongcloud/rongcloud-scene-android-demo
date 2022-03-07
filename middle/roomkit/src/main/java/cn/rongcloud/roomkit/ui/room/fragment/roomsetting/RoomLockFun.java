package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author gyn
 * @date 2021/9/30
 */
public class RoomLockFun extends IFun.BaseFun {
    public RoomLockFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        if (getStatus() == 1) {
            return R.drawable.ic_room_setting_unlock;
        } else {
            return R.drawable.ic_room_setting_lock;
        }
    }

    @Override
    public String getText() {
        if (getStatus() == 1) {
            return "房间解锁";
        } else {
            return "房间上锁";
        }
    }
}
