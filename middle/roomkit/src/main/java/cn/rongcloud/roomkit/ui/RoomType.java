package cn.rongcloud.roomkit.ui;

import cn.rongcloud.config.feedback.RcEvent;

/**
 * @author gyn
 * @date 2021/9/15
 */
public enum RoomType {
    /**
     * 语聊房
     */
    VOICE_ROOM(1),
    /**
     * 电台房
     */
    RADIO_ROOM(2),
    /**
     * 直播房
     */
    LIVE_ROOM(3), //临时写成3，还需要和后台协商
    /**
     * 游戏房
     */
    GAME_ROOM(4);

    int type;

    RoomType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public RcEvent convertToRcEvent() {
        switch (this) {
            case GAME_ROOM:
                return RcEvent.GameRoom;
            case LIVE_ROOM:
                return RcEvent.LiveRoom;
            case RADIO_ROOM:
                return RcEvent.RadioRoom;
            case VOICE_ROOM:
                return RcEvent.VoiceRoom;
        }
        return RcEvent.NONE;
    }
}
