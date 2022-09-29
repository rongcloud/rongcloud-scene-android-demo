package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

import cn.rongcloud.roomkit.R;

/**
 * @author lihao
 * @project RongRTCDemo
 * @date 2021/11/16
 * @time 6:02 下午
 */
public class RoomVideoSetFun extends IFun.BaseFun {

    public RoomVideoSetFun(int status) {
        setStatus(status);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_room_setting_video_set;
    }

    @Override
    public String getText() {
        return "视频设置";
    }
}
