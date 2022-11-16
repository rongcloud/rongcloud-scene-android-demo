package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author gyn
 * @date 2021/9/30
 */
public class RoomMuteAllFun extends IFun.BaseFun {
    public RoomMuteAllFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        if (getStatus() == 1) {
            return R.drawable.ic_room_setting_unmute_all;
        } else {
            return R.drawable.ic_room_setting_mute_all;
        }
    }

    @Override
    public String getText() {
        if (getStatus() == 1) {
            return "解锁全麦";
        } else {
            return "全麦锁麦";
        }
    }
}
