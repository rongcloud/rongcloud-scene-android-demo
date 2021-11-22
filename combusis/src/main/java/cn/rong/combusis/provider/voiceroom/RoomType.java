package cn.rong.combusis.provider.voiceroom;

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
    RADIO_ROOM(2);

    int type;

    RoomType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
