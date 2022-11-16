/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.roomkit.message;

import android.os.Parcel;


import com.basis.utils.GsonUtil;

import java.nio.charset.StandardCharsets;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 服务端分发麦位移除消息，处理异常退出
 */
@MessageTag(value = "RCMic:chrmSeatRemoveMsg")
public class RCChatSeatRemove extends MessageContent {
    public static final Creator<RCChatSeatRemove> CREATOR = new Creator<RCChatSeatRemove>() {
        @Override
        public RCChatSeatRemove createFromParcel(Parcel source) {
            return new RCChatSeatRemove(source);
        }

        @Override
        public RCChatSeatRemove[] newArray(int size) {
            return new RCChatSeatRemove[size];
        }
    };
    private static final String TAG = "RCChatSeatRemove";
    private String userId;
    private String roomId;
    private String timestamp;

    public RCChatSeatRemove(byte[] data) {
        super(data);
        String jsonStr = null;
        jsonStr = new String(data, StandardCharsets.UTF_8);
        RCChatSeatRemove temp = GsonUtil.json2Obj(jsonStr, RCChatSeatRemove.class);
        if (null != temp) {
            this.userId = temp.userId;
            this.roomId = temp.roomId;
            this.timestamp = temp.timestamp;
        }
    }

    public RCChatSeatRemove() {
    }

    protected RCChatSeatRemove(Parcel in) {
        this.userId = in.readString();
        this.roomId = in.readString();
        this.timestamp = in.readString();
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
        dest.writeString(this.userId);
        dest.writeString(this.roomId);
        dest.writeString(this.timestamp);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readString();
        this.roomId = source.readString();
        this.timestamp = source.readString();
    }
}
