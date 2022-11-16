/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.roomkit.message;

import android.os.Parcel;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * @author gusd
 * @Date 2021/06/27
 */
@MessageTag(value = "RC:LocationMessage")
public class RCChatroomLocationMessage extends MessageContent {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
    }

    public void readFromParcel(Parcel source) {
        this.content = source.readString();
    }

    public RCChatroomLocationMessage() {
    }

    protected RCChatroomLocationMessage(Parcel in) {
        this.content = in.readString();
    }

    public static final Creator<RCChatroomLocationMessage> CREATOR = new Creator<RCChatroomLocationMessage>() {
        @Override
        public RCChatroomLocationMessage createFromParcel(Parcel source) {
            return new RCChatroomLocationMessage(source);
        }

        @Override
        public RCChatroomLocationMessage[] newArray(int size) {
            return new RCChatroomLocationMessage[size];
        }
    };
}
