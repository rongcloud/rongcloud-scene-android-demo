/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.radioroom.messagemodel;

import android.os.Parcel;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * @author gusd
 * @Date 2021/06/02
 */
@MessageTag(value = "RC:VRLRefreshMsg", flag = MessageTag.NONE)
public class RCRadioRoomRefreshMessage extends MessageContent {

    public static final Creator<RCRadioRoomRefreshMessage> CREATOR = new Creator<RCRadioRoomRefreshMessage>() {
        @Override
        public RCRadioRoomRefreshMessage createFromParcel(Parcel source) {
            return new RCRadioRoomRefreshMessage(source);
        }

        @Override
        public RCRadioRoomRefreshMessage[] newArray(int size) {
            return new RCRadioRoomRefreshMessage[size];
        }
    };
    private static final String TAG = "RCVoiceRoomRefreshMessa";
    private String name;
    private String content;

    public RCRadioRoomRefreshMessage(byte[] data) {
        super(data);
        String jsonStr = null;
        jsonStr = new String(data, StandardCharsets.UTF_8);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.has("name")) {
                name = jsonObj.getString("name");
            }
            if (jsonObj.has("content")) {
                content = jsonObj.getString("content");
            }
        } catch (JSONException e) {
            RLog.e(TAG, "JSONException " + e.getMessage());
        }
    }

    public RCRadioRoomRefreshMessage() {
    }

    protected RCRadioRoomRefreshMessage(Parcel in) {
        this.name = in.readString();
        this.content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.content);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.content = source.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            if (!TextUtils.isEmpty(name)) {
                jsonObj.put("name", name);
            }
            if (!TextUtils.isEmpty(content)) {
                jsonObj.put("content", content);
            }
            return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
        } catch (JSONException e) {
            RLog.e(TAG, "JSONException " + e.getMessage());
        }
        return null;
    }

    @Override
    public String toString() {
        return "RCVoiceRoomRefreshMessage{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
