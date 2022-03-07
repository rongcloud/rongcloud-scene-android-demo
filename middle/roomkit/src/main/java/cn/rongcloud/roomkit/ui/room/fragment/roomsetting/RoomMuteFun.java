package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author gyn
 * @date 2021/9/30
 */
public class RoomMuteFun extends IFun.BaseFun {
    public RoomMuteFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        if (getStatus() == 1) {
            return R.drawable.ic_room_setting_unmute;
        } else {
            return R.drawable.ic_room_setting_mute;
        }
    }

    @Override
    public String getText() {
        if (getStatus() == 1) {
            return "取消静音";
        } else {
            return "静音";
        }
    }
}
