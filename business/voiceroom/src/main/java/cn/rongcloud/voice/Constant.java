package cn.rongcloud.voice;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/2/18
 * @time 10:50 上午
 */
public class Constant {
    /**
     * notify room 的name
     */
    public static String EVENT_ROOM_CLOSE = "VoiceRoomClosed";
    public static String EVENT_BACKGROUND_CHANGE = "VoiceRoomBackgroundChanged";
    public static String EVENT_MANAGER_LIST_CHANGE = "VoiceRoomNeedRefreshmanagers";
    public static String EVENT_REJECT_MANAGE_PICK = "VoiceRoomRejectManagePick"; // 拒绝上麦
    public static String EVENT_AGREE_MANAGE_PICK = "VoiceRoomAgreeManagePick"; // 同意上麦
    public static String EVENT_KICK_OUT_OF_SEAT = "EVENT_KICK_OUT_OF_SEAT";
    public static String EVENT_REQUEST_SEAT_REFUSE = "EVENT_REQUEST_SEAT_REFUSE";
    public static String EVENT_REQUEST_SEAT_AGREE = "EVENT_REQUEST_SEAT_AGREE";
    public static String EVENT_REQUEST_SEAT_CANCEL = "EVENT_REQUEST_SEAT_CANCEL";
    public static String EVENT_USER_LEFT_SEAT = "EVENT_USER_LEFT_SEAT";
    public static String EVENT_ADD_SHIELD = "EVENT_ADD_SHIELD"; // 添加屏蔽词
    public static String EVENT_DELETE_SHIELD = "EVENT_DELETE_SHIELD"; // 删除屏蔽词
    public static String EVENT_KICKED_OUT_OF_ROOM = "EVENT_KICKED_OUT_OF_ROOM";
}
