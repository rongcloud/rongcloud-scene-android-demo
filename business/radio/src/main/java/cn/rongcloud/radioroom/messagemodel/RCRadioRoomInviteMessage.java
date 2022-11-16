/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.radioroom.messagemodel;

import android.os.Parcel;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

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
@MessageTag(value = "RC:VRLInviteMsg", flag = MessageTag.NONE)
public class RCRadioRoomInviteMessage extends MessageContent {

    public static final Creator<RCRadioRoomInviteMessage> CREATOR = new Creator<RCRadioRoomInviteMessage>() {
        @Override
        public RCRadioRoomInviteMessage createFromParcel(Parcel source) {
            return new RCRadioRoomInviteMessage(source);
        }

        @Override
        public RCRadioRoomInviteMessage[] newArray(int size) {
            return new RCRadioRoomInviteMessage[size];
        }
    };
    private static final String TAG = "RCVoiceRoomInviteMessage";
    /**
     * 邀请的消息 Id
     */
    private String invitationId;
    /**
     * 发送者 Id
     */
    private String sendUserId;
    /**
     * 目标 Id
     */
    private String targetId;
    /**
     * 消息类型
     */
    private RCInviteCmdType type;
    /**
     * 内容
     */
    private String content;

    public RCRadioRoomInviteMessage() {

    }

    public RCRadioRoomInviteMessage(byte[] data) {
        super(data);
        String jsonStr = null;
        jsonStr = new String(data, StandardCharsets.UTF_8);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.has("invitationId")) {
                invitationId = jsonObj.getString("invitationId");
            }

            if (jsonObj.has("sendUserId")) {
                sendUserId = jsonObj.getString("sendUserId");
            }

            if (jsonObj.has("targetId")) {
                targetId = jsonObj.getString("targetId");
            }

            if (jsonObj.has("type")) {
                int cmd = jsonObj.getInt("type");
                if (cmd < RCInviteCmdType.values().length && cmd >= 0) {
                    this.type = RCInviteCmdType.values()[cmd];
                }
            }

            if (jsonObj.has("content")) {
                content = jsonObj.getString("content");
            }
        } catch (JSONException e) {
            RLog.e(TAG, "JSONException " + e.getMessage());
        }
    }

    protected RCRadioRoomInviteMessage(Parcel in) {
        this.invitationId = in.readString();
        this.sendUserId = in.readString();
        this.targetId = in.readString();
        int tmpCmd = in.readInt();
        this.type = tmpCmd == -1 ? null : RCInviteCmdType.values()[tmpCmd];
        this.content = in.readString();
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public RCInviteCmdType getType() {
        return type;
    }

    public void setType(RCInviteCmdType type) {
        this.type = type;
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
            jsonObj.put("invitationId", invitationId);
            jsonObj.put("sendUserId", sendUserId);
            if (!TextUtils.isEmpty(targetId)) {
                jsonObj.put("targetId", targetId);
            }
            jsonObj.put("type", type.ordinal());
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
        return "RCInviteMessage{" +
                "invitationId='" + invitationId + '\'' +
                ", sendUserId='" + sendUserId + '\'' +
                ", targetId='" + targetId + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.invitationId);
        dest.writeString(this.sendUserId);
        dest.writeString(this.targetId);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.content);
    }

    public void readFromParcel(Parcel source) {
        this.invitationId = source.readString();
        this.sendUserId = source.readString();
        this.targetId = source.readString();
        int tmpCmd = source.readInt();
        this.type = tmpCmd == -1 ? null : RCInviteCmdType.values()[tmpCmd];
        this.content = source.readString();
    }

    /**
     * 消息类型
     */
    public enum RCInviteCmdType {
        /**
         * 邀请请求
         */
        @SerializedName("0")
        RCInviteCmdTypeRequest,
        /**
         * 准许进入
         */
        @SerializedName("1")
        RCInviteCmdTypeAccept,
        /**
         * 拒绝进入
         */
        @SerializedName("2")
        RCInviteCmdTypeReject,
        /**
         * 取消请求
         */
        @SerializedName("3")
        RCInviteCmdTypeCancel,

    }
}
