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
@MessageTag(value = "RC:Chatroom:User:Ban")
public class RCChatroomUserBan extends MessageContent {
    private static final String TAG = "RCChatroomUserBan";
    private String id;
    private long duration;
    private String extra;

    public RCChatroomUserBan(byte[] data) {
        super(data);
        String jsonStr = null;
        jsonStr = new String(data, StandardCharsets.UTF_8);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.has("id")) {
                id = jsonObj.getString("id");
            }
            if (jsonObj.has("extra")) {
                extra = jsonObj.getString("extra");
            }
            if (jsonObj.has("duration")) {
                duration = jsonObj.getLong("duration");
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            if (!TextUtils.isEmpty(id)) {
                jsonObj.put("id", id);
            }
            if (!TextUtils.isEmpty(extra)) {
                jsonObj.put("extra", extra);
            }

            jsonObj.put("duration", duration);
            return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeLong(this.duration);
        dest.writeString(this.extra);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.duration = source.readInt();
        this.extra = source.readString();
    }

    public RCChatroomUserBan() {
    }


    protected RCChatroomUserBan(Parcel in) {
        this.id = in.readString();
        this.duration = in.readInt();
        this.extra = in.readString();
    }

    public static final Creator<RCChatroomUserBan> CREATOR = new Creator<RCChatroomUserBan>() {
        @Override
        public RCChatroomUserBan createFromParcel(Parcel source) {
            return new RCChatroomUserBan(source);
        }

        @Override
        public RCChatroomUserBan[] newArray(int size) {
            return new RCChatroomUserBan[size];
        }
    };
}
