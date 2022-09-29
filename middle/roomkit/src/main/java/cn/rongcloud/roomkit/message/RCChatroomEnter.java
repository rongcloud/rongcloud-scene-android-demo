/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.roomkit.message;

import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 进入房间消息，自己发，并添加到列表
 * @author gusd
 * @Date 2021/06/17
 */
@MessageTag(value = "RC:Chatroom:Enter")
public class RCChatroomEnter extends MessageContent {
    private static final String TAG = "RCChatroomEnter";
    private String userId;
    private String userName;

    public RCChatroomEnter(byte[] data){
        super(data);
        String jsonStr = null;
        jsonStr = new String(data, StandardCharsets.UTF_8);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.has("userId")) {
                userId = jsonObj.getString("userId");
            }

            if (jsonObj.has("userName")) {
                userName = jsonObj.getString("userName");
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            if (!TextUtils.isEmpty(userId)) {
                jsonObj.put("userId", userId);
            }
            if (!TextUtils.isEmpty(userName)) {
                jsonObj.put("userName", userName);
            }
            return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }
        return null;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userName);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readString();
        this.userName = source.readString();
    }

    public RCChatroomEnter() {
    }

    protected RCChatroomEnter(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
    }

    public static final Creator<RCChatroomEnter> CREATOR = new Creator<RCChatroomEnter>() {
        @Override
        public RCChatroomEnter createFromParcel(Parcel source) {
            return new RCChatroomEnter(source);
        }

        @Override
        public RCChatroomEnter[] newArray(int size) {
            return new RCChatroomEnter[size];
        }
    };
}
