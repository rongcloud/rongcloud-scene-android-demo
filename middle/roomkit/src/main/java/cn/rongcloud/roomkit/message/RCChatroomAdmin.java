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
 * 设置或取消管理员时发送
 * @author gusd
 * @Date 2021/06/17
 */
@MessageTag(value = "RC:Chatroom:Admin", flag = MessageTag.NONE)
public class RCChatroomAdmin extends MessageContent {
    private static final String TAG = "RCChatroomAdmin";

    private String userId;
    private String userName;
    private boolean isAdmin;

    public RCChatroomAdmin(byte[] data) {
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
            if (jsonObj.has("isAdmin")) {
                isAdmin = jsonObj.getBoolean("isAdmin");
            } else {
                isAdmin = false;
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
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
            jsonObj.put("isAdmin", isAdmin);
            return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeByte(this.isAdmin ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readString();
        this.userName = source.readString();
        this.isAdmin = source.readByte() != 0;
    }

    public RCChatroomAdmin() {
    }

    protected RCChatroomAdmin(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
        this.isAdmin = in.readByte() != 0;
    }

    public static final Creator<RCChatroomAdmin> CREATOR = new Creator<RCChatroomAdmin>() {
        @Override
        public RCChatroomAdmin createFromParcel(Parcel source) {
            return new RCChatroomAdmin(source);
        }

        @Override
        public RCChatroomAdmin[] newArray(int size) {
            return new RCChatroomAdmin[size];
        }
    };
}
