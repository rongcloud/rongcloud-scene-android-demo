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
 * 普通语音消息
 * @author gusd
 * @Date 2021/06/17
 */
@MessageTag(value = "RC:VRVoiceMsg")
public class RCChatroomVoice extends MessageContent {
    private static final String TAG = "RCChatroomVoice";

    private String userId;
    private String userName;
    private String path;
    private String duration;

    public RCChatroomVoice(byte[] data) {
        super(data);
        String jsonStr = null;
        jsonStr = new String(data, StandardCharsets.UTF_8);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.has("_userId")) {
                userId = jsonObj.getString("_userId");
            }

            if (jsonObj.has("_userName")) {
                userName = jsonObj.getString("_userName");
            }
            if (jsonObj.has("_path")) {
                path = jsonObj.getString("_path");
            }
            if (jsonObj.has("_duration")) {
                duration = jsonObj.getString("_duration");
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
                jsonObj.put("_userId", userId);
            }
            if (!TextUtils.isEmpty(userName)) {
                jsonObj.put("_userName", userName);
            }
            if (!TextUtils.isEmpty(path)) {
                jsonObj.put("_path", path);
            }
            if (!TextUtils.isEmpty(duration)) {
                jsonObj.put("_duration", duration);
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.path);
        dest.writeString(this.duration);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readString();
        this.userName = source.readString();
        this.path = source.readString();
        this.duration = source.readString();
    }

    public RCChatroomVoice() {
    }

    protected RCChatroomVoice(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
        this.path = in.readString();
        this.duration = in.readString();
    }

    public static final Creator<RCChatroomVoice> CREATOR = new Creator<RCChatroomVoice>() {
        @Override
        public RCChatroomVoice createFromParcel(Parcel source) {
            return new RCChatroomVoice(source);
        }

        @Override
        public RCChatroomVoice[] newArray(int size) {
            return new RCChatroomVoice[size];
        }
    };
}
