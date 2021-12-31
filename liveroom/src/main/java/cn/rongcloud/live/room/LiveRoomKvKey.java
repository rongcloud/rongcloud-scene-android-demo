package cn.rongcloud.live.room;

/**
 * @author lihao
 * @project RongRTCDemo
 * @date 2021/11/24
 * @time 6:24 下午
 * KV 消息的key
 */
public class LiveRoomKvKey {
    //房间名称
    public final static String LIVE_ROOM_NAME = "name";
    //公告
    public final static String LIVE_ROOM_NOTICE = "notice";
    //屏蔽词
    public final static String LIVE_ROOM_SHIELDS = "shields";
    //房间上麦模式
    public final static String LIVE_ROOM_ENTER_SEAT_MODE = "FreeEnterSeat";
    //分辨率
    public final static String LIVE_ROOM_VIDEO_RESOLUTION = "RCRTCVideoResolution";
    //帧率
    public final static String LIVE_ROOM_VIDEO_FPS = "RCRTCVideoFps";

    public class EnterSeatMode {
        //申请上麦
        public final static String LIVE_ROOM_RequestEnterSeat = "0";
        //自由上麦
        public final static String LIVE_ROOM_FreeEnterSeat = "1";

    }
}
