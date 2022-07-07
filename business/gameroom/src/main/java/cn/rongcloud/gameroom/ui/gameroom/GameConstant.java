package cn.rongcloud.gameroom.ui.gameroom;

/**
 * @author gyn
 * @date 2022/5/17
 */
public class GameConstant {
    // 同意邀请上麦的事件，content=发起邀请者的userId
    public static final String EVENT_AGREE_MANAGE_PICK = "EVENT_AGREE_MANAGE_PICK";
    // 拒绝邀请上麦的事件，content=发起邀请者的userId
    public static final String EVENT_REJECT_MANAGE_PICK = "EVENT_REJECT_MANAGE_PICK";
    // 发起邀请加入游戏的事件，content=被邀请者的userId
    public static final String EVENT_INVITED_JOIN_GAME = "EVENT_INVITED_JOIN_GAME";
    // 添加屏蔽词,content=屏蔽词的名称
    public static final String EVENT_ADD_SHIELD = "EVENT_ADD_SHIELD";
    // 删除屏蔽词,content=屏蔽词的名称
    public static final String EVENT_DELETE_SHIELD = "EVENT_DELETE_SHIELD";
    // 切换游戏，content=RCGameInfo的json字符串
    public static final String EVENT_SWITCH_GAME = "EVENT_SWITCH_GAME";
    // 关闭麦克风后，发消息通知其他人，因为目前sdk内部关闭mic后状态不回调，先自定义消息解决一下
    public static final String EVENT_CLOSE_MIC = "EVENT_CLOSE_MIC";
}
