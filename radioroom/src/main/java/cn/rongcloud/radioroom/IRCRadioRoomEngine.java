/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.radioroom;

import cn.rongcloud.radioroom.callback.RCRadioRoomCallback;
import cn.rongcloud.radioroom.callback.RCRadioRoomResultCallback;
import cn.rongcloud.radioroom.room.RCRadioEventListener;
import cn.rongcloud.radioroom.room.RCRadioRoomInfo;

/**
 * 语聊房引擎接口
 */
public interface IRCRadioRoomEngine {

    void setRadioEventListener(RCRadioEventListener listener);

    void joinRoom(RCRadioRoomInfo roomInfo, RCRadioRoomCallback callback);

    void leaveRoom(final RCRadioRoomCallback callback);

    void enterSeat(final RCRadioRoomCallback callback);

    void leaveSeat(final RCRadioRoomCallback callback);

    void muteSelf(boolean isMute);

    void updateRadioRoomKV(UpdateKey key, String value, RCRadioRoomCallback callback);

    void getRadioRoomValue(UpdateKey key, RCRadioRoomResultCallback<String> callback);

    enum UpdateKey {
        RC_ROOM_NAME("RCRadioRoomKVRoomNameKey"),
        RC_SEATING("RCRadioRoomKVSeatingKey"),
        RC_SILENT("RCRadioRoomKVSilentKey"),
        RC_SPEAKING("RCRadioRoomKVSpeakingKey"),
        RC_NOTICE("RCRadioRoomKVNoticeKey"),
        RC_BGNAME("RCRadioRoomKVBGNameKey"),
        RC_SUSPEND("RCRadioRoomKVSuspendKey");

        private String value;

        UpdateKey(java.lang.String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
