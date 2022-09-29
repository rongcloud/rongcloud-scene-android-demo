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
 * @author gusd
 * @Date 2021/06/17
 */
@MessageTag(value = "RC:Chatroom:KickOut")
public class RCChatroomKickOut extends MessageContent {
    private static final String TAG = "RCChatroomKickOut";

    private String userId;

    private String userName;

    private String targetId;

    private String targetName;

    public RCChatroomKickOut(byte[] data) {
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

            if (jsonObj.has("targetName")) {
                targetName = jsonObj.getString("targetName");
            }
            if (jsonObj.has("targetId")) {
                targetId = jsonObj.getString("targetId");
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

            if (!TextUtils.isEmpty(targetId)) {
                jsonObj.put("targetId", targetId);
            }
            if (!TextUtils.isEmpty(targetName)) {
                jsonObj.put("targetName", targetName);
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

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.targetId);
        dest.writeString(this.targetName);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readString();
        this.userName = source.readString();
        this.targetId = source.readString();
        this.targetName = source.readString();
    }

    public RCChatroomKickOut() {
    }

    protected RCChatroomKickOut(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
        this.targetId = in.readString();
        this.targetName = in.readString();
    }

    public static final Creator<RCChatroomKickOut> CREATOR = new Creator<RCChatroomKickOut>() {
        @Override
        public RCChatroomKickOut createFromParcel(Parcel source) {
            return new RCChatroomKickOut(source);
        }

        @Override
        public RCChatroomKickOut[] newArray(int size) {
            return new RCChatroomKickOut[size];
        }
    };
}
