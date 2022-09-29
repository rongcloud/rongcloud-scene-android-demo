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
@MessageTag(value = "RC:Chatroom:Follow")
public class RCChatroomFollow extends MessageContent {
    private static final String TAG = "RCChatroomFollow";

    private String id;
    private int rank;
    private int level;
    private String extra;

    public RCChatroomFollow(byte[] data) {
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
            if (jsonObj.has("rank")) {
                rank = jsonObj.getInt("rank");
            }
            if (jsonObj.has("level")) {
                level = jsonObj.getInt("level");
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
            jsonObj.put("rank", rank);
            jsonObj.put("level", level);
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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
        dest.writeInt(this.rank);
        dest.writeInt(this.level);
        dest.writeString(this.extra);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.rank = source.readInt();
        this.level = source.readInt();
        this.extra = source.readString();
    }

    public RCChatroomFollow() {
    }

    protected RCChatroomFollow(Parcel in) {
        this.id = in.readString();
        this.rank = in.readInt();
        this.level = in.readInt();
        this.extra = in.readString();
    }

    public static final Creator<RCChatroomFollow> CREATOR = new Creator<RCChatroomFollow>() {
        @Override
        public RCChatroomFollow createFromParcel(Parcel source) {
            return new RCChatroomFollow(source);
        }

        @Override
        public RCChatroomFollow[] newArray(int size) {
            return new RCChatroomFollow[size];
        }
    };
}
