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
 *
 * @author gusd
 * @Date 2021/06/17
 */
@MessageTag(value = "RC:Chatroom:GiftAll")
public class RCChatroomGiftAll extends MessageContent {
    private static final String TAG = "RCChatroomGiftAll";

    private String userId;
    private String userName;
    private String giftId;
    private String giftName;
    private int number;
    private int price;

    public RCChatroomGiftAll(byte[] data) {
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


            if (jsonObj.has("giftId")) {
                giftId = jsonObj.getString("giftId");
            }
            if (jsonObj.has("giftName")) {
                giftName = jsonObj.getString("giftName");
            }
            if (jsonObj.has("number")) {
                number = jsonObj.getInt("number");
            }
            if (jsonObj.has("price")) {
                price = jsonObj.getInt("price");
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

            if (!TextUtils.isEmpty(giftId)) {
                jsonObj.put("giftId", giftId);
            }
            if (!TextUtils.isEmpty(giftName)) {
                jsonObj.put("giftName", giftName);
            }

            jsonObj.put("number", number);
            jsonObj.put("price", price);
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

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.giftId);
        dest.writeString(this.giftName);
        dest.writeInt(this.number);
        dest.writeInt(this.price);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readString();
        this.userName = source.readString();
        this.giftId = source.readString();
        this.giftName = source.readString();
        this.number = source.readInt();
        this.price = source.readInt();
    }

    public RCChatroomGiftAll() {
    }

    protected RCChatroomGiftAll(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
        this.giftId = in.readString();
        this.giftName = in.readString();
        this.number = in.readInt();
        this.price = in.readInt();
    }

    public static final Creator<RCChatroomGiftAll> CREATOR = new Creator<RCChatroomGiftAll>() {
        @Override
        public RCChatroomGiftAll createFromParcel(Parcel source) {
            return new RCChatroomGiftAll(source);
        }

        @Override
        public RCChatroomGiftAll[] newArray(int size) {
            return new RCChatroomGiftAll[size];
        }
    };
}
