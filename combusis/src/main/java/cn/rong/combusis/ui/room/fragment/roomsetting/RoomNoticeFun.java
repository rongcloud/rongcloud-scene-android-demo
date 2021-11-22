package cn.rong.combusis.ui.room.fragment.roomsetting;

import cn.rong.combusis.R;

/**
 * @author gyn
 * @date 2021/9/30
 */
public class RoomNoticeFun extends IFun.BaseFun {
    public RoomNoticeFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_room_setting_notice;
    }

    @Override
    public String getText() {
        return "房间公告";
    }
}
