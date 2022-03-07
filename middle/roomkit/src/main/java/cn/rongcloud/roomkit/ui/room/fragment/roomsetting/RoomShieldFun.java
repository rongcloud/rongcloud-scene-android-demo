package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author gyn
 * @date 2021/9/30
 */
public class RoomShieldFun extends IFun.BaseFun {
    public RoomShieldFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_room_setting_shield;
    }

    @Override
    public String getText() {
        return "屏蔽词";
    }
}
