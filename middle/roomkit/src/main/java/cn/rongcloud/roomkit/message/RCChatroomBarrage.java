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

import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 普通文本消息
 * @author gusd
 * @Date 2021/06/17
 */
@MessageTag(value = "RC:Chatroom:Barrage")
public class RCChatroomBarrage extends MessageContent {
    private static final String TAG = "RCChatroomBarrage";

    private String userId;
    private String userName;
    private String content;

    public RCChatroomBarrage(byte[] data) {
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
            if (jsonObj.has("content")) {
                content = jsonObj.getString("content");
            }
        } catch (JSONException e) {
            RLog.e(TAG, "JSONException " + e.getMessage());
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
            if (!TextUtils.isEmpty(content)) {
                jsonObj.put("content", content);
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.content);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readString();
        this.userName = source.readString();
        this.content = source.readString();
    }

    public RCChatroomBarrage() {
    }

    protected RCChatroomBarrage(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
        this.content = in.readString();
    }

    public static final Creator<RCChatroomBarrage> CREATOR = new Creator<RCChatroomBarrage>() {
        @Override
        public RCChatroomBarrage createFromParcel(Parcel source) {
            return new RCChatroomBarrage(source);
        }

        @Override
        public RCChatroomBarrage[] newArray(int size) {
            return new RCChatroomBarrage[size];
        }
    };
}
