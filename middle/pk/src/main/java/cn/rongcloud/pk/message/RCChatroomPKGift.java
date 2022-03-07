/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.pk.message;

import android.os.Parcel;


import com.basis.utils.GsonUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

import cn.rongcloud.config.provider.user.User;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 服务端分发pk礼物消息
 */
@MessageTag(value = "RCMic:chrmPkMsg")
public class RCChatroomPKGift extends MessageContent {
    public static final Creator<RCChatroomPKGift> CREATOR = new Creator<RCChatroomPKGift>() {
        @Override
        public RCChatroomPKGift createFromParcel(Parcel source) {
            return new RCChatroomPKGift(source);
        }

        @Override
        public RCChatroomPKGift[] newArray(int size) {
            return new RCChatroomPKGift[size];
        }
    };
    private static final String TAG = "RCChatroomPKGift";
    private long score;
    private String roomId;
    private List<User> userList;
    private long pkTime;

    public RCChatroomPKGift(byte[] data) {
        super(data);
        String jsonStr = null;
        jsonStr = new String(data, StandardCharsets.UTF_8);
        RCChatroomPKGift temp = GsonUtil.json2Obj(jsonStr, RCChatroomPKGift.class);
        if (null != temp) {
            this.score = temp.score;
            this.roomId = temp.roomId;
            this.userList = temp.userList;
            this.pkTime = temp.pkTime;
        }
    }

    public RCChatroomPKGift() {
    }

    protected RCChatroomPKGift(Parcel in) {
        this.score = in.readLong();
        this.roomId = in.readString();
        this.userList = GsonUtil.json2List(in.readString(), User.class);
        this.pkTime = in.readLong();

    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public long getPkTime() {
        return pkTime;
    }

    public void setPkTime(long pkTime) {
        this.pkTime = pkTime;
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
        dest.writeLong(this.score);
        dest.writeString(this.roomId);
        dest.writeString(GsonUtil.obj2Json(userList));
        dest.writeLong(pkTime);
    }

    public void readFromParcel(Parcel source) {
        this.score = source.readLong();
        this.roomId = source.readString();
        this.userList = GsonUtil.json2List(source.readString(), User.class);
        this.pkTime = source.readLong();
    }
}
