package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author gyn
 * @date 2021/9/30
 */
public class RoomNameFun extends IFun.BaseFun {
    public RoomNameFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_room_setting_name;
    }

    @Override
    public String getText() {
        return "房间标题";
    }
}
