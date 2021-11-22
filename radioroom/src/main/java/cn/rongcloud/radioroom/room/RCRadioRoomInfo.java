package cn.rongcloud.radioroom.room;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;

import cn.rongcloud.radioroom.utils.JsonUtils;
import cn.rongcloud.radioroom.utils.VMLog;
import cn.rongcloud.rtc.base.RCRTCLiveRole;

public class RCRadioRoomInfo implements Serializable {
    private String roomName;
    private String roomId;
    private boolean inSeat = false;
    private int role;

    public RCRadioRoomInfo(RCRTCLiveRole role) {
        this.role = null == role ? RCRTCLiveRole.AUDIENCE.getType() : role.getType();
    }

    public static RCRadioRoomInfo fromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return new RCRadioRoomInfo(null);
        }
        return JsonUtils.fromJson(json, RCRadioRoomInfo.class);
    }

    public boolean check() {
        return !TextUtils.isEmpty(roomId) && !TextUtils.isEmpty(roomName);
    }

    protected boolean isInSeat() {
        return inSeat;
    }

    protected void setInSeat(boolean inSeat) {
        this.inSeat = inSeat;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    protected RCRTCLiveRole getRole() {
        return 1 == role ? RCRTCLiveRole.BROADCASTER : RCRTCLiveRole.AUDIENCE;
    }

    public void setRole(RCRTCLiveRole role) {
        this.role = role.getType();
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String toJson() {
        return JsonUtils.toJson(this);
    }

    public String toKv() {
        String json = JsonUtils.toJson(this);
        //移除role roomId
        JsonObject jObj = JsonParser.parseString(json).getAsJsonObject();
        jObj.remove("role");
        jObj.remove("roomId");
        jObj.remove("inSeat");
        String result = jObj.toString();
        VMLog.d("toKv", "toKv:" + result);
        return result;
    }
}
