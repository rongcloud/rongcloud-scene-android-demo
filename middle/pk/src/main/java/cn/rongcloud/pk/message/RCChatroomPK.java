/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.pk.message;

import android.os.Parcel;


import com.basis.utils.GsonUtil;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

import cn.rongcloud.config.provider.user.User;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 服务端分发pk状态消息
 */
@MessageTag(value = "RCMic:chrmPkStatusMsg")
public class RCChatroomPK extends MessageContent {
    public static final Creator<RCChatroomPK> CREATOR = new Creator<RCChatroomPK>() {
        @Override
        public RCChatroomPK createFromParcel(Parcel source) {
            return new RCChatroomPK(source);
        }

        @Override
        public RCChatroomPK[] newArray(int size) {
            return new RCChatroomPK[size];
        }
    };
    private static final String TAG = "RCChatroomPK";
    private String stopPkRoomId;
    private String statusMsg;
    private String timeDiff;
    private List<RoomScore> roomScores;

    public RCChatroomPK(byte[] data) {
        super(data);
        String jsonStr = null;
        jsonStr = new String(data, StandardCharsets.UTF_8);
        RCChatroomPK temp = GsonUtil.json2Obj(jsonStr, RCChatroomPK.class);
        if (null != temp) {
            this.stopPkRoomId = temp.stopPkRoomId;
            this.statusMsg = temp.statusMsg;
            this.timeDiff = temp.timeDiff;
            this.roomScores = temp.roomScores;
        }
    }

    public RCChatroomPK() {
    }

    protected RCChatroomPK(Parcel in) {
        this.stopPkRoomId = in.readString();
        this.statusMsg = in.readString();
        this.timeDiff = in.readString();
        this.roomScores = GsonUtil.json2List(in.readString(), RoomScore.class);

    }

    public String getStopPkRoomId() {
        return stopPkRoomId;
    }

    public void setStopPkRoomId(String stopPkRoomId) {
        this.stopPkRoomId = stopPkRoomId;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        this.timeDiff = timeDiff;
    }

    public List<RoomScore> getRoomScores() {
        return roomScores;
    }

    public void setRoomScores(List<RoomScore> roomScores) {
        this.roomScores = roomScores;
    }

    public byte[] encode() {
        String json = GsonUtil.obj2Json(this);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.stopPkRoomId);
        dest.writeString(this.statusMsg);
        dest.writeString(this.timeDiff);
        dest.writeString(GsonUtil.obj2Json(this.roomScores));
    }

    public void readFromParcel(Parcel source) {
        this.stopPkRoomId = source.readString();
        this.statusMsg = source.readString();
        this.timeDiff = source.readString();
        this.roomScores = GsonUtil.json2List(source.readString(), RoomScore.class);
    }

    public static class RoomScore implements Serializable {
        private String roomId;
        private int score;
        private List<User> userInfoList;

        public int getScore() {
            return score;
        }

        public String getRoomId() {
            return roomId;
        }

        public List<User> getUserInfoList() {
            return userInfoList;
        }
    }
}
