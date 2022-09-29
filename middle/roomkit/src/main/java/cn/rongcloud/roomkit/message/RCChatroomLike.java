/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.roomkit.message;

import android.os.Parcel;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * @author gusd
 * @Date 2021/06/17
 */
@MessageTag(value = "RC:Chatroom:Like")
public class RCChatroomLike extends MessageContent {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public void readFromParcel(Parcel source) {
    }

    public RCChatroomLike() {
    }

    public RCChatroomLike(byte[] data){

    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    protected RCChatroomLike(Parcel in) {
    }

    public static final Creator<RCChatroomLike> CREATOR = new Creator<RCChatroomLike>() {
        @Override
        public RCChatroomLike createFromParcel(Parcel source) {
            return new RCChatroomLike(source);
        }

        @Override
        public RCChatroomLike[] newArray(int size) {
            return new RCChatroomLike[size];
        }
    };
}
