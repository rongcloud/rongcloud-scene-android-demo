/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.roomkit.message;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * @author gusd
 * @Date 2021/06/17
 */
@MessageTag(value = "RC:Chatroom:Seats")
public class RCChatroomSeats extends MessageContent {
    private static final String TAG = "RCChatroomSeats";

    private int count;


    public RCChatroomSeats(byte[] data) {
        super(data);
        String jsonStr = null;
        jsonStr = new String(data, StandardCharsets.UTF_8);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.has("count")) {
                count = jsonObj.getInt("count");
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("count", count);
            return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }
        return null;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
    }

    public void readFromParcel(Parcel source) {
        this.count = source.readInt();
    }

    public RCChatroomSeats() {
    }

    protected RCChatroomSeats(Parcel in) {
        this.count = in.readInt();
    }

    public static final Creator<RCChatroomSeats> CREATOR = new Creator<RCChatroomSeats>() {
        @Override
        public RCChatroomSeats createFromParcel(Parcel source) {
            return new RCChatroomSeats(source);
        }

        @Override
        public RCChatroomSeats[] newArray(int size) {
            return new RCChatroomSeats[size];
        }
    };
}
