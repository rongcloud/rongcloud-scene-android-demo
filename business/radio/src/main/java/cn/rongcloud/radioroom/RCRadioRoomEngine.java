/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.radioroom;

import cn.rongcloud.radioroom.room.RCRadioRoomEngineImpl;

/**
 * 语聊房引擎接口
 */
public abstract class RCRadioRoomEngine{

    public static final String RC_KICK_USER_OUT_ROOM_CONTENT = "RCKickUserOutRoomContent";

    public static IRCRadioRoomEngine getInstance() {
        return RCRadioRoomEngineImpl.getInstance();
    }
}
