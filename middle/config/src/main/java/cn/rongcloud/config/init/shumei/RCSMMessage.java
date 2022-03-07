package cn.rongcloud.config.init.shumei;

import android.os.Parcel;

import com.basis.utils.GsonUtil;

import java.nio.charset.StandardCharsets;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 数美审核消息
 */
@MessageTag(value = "RCMic:shumeiAuditFreezeMsg")
public class RCSMMessage extends MessageContent {
    private String userId;
    private String message;
    private int status;

    public RCSMMessage(byte[] data) {
        super(data);
        String jsonStr = new String(data, StandardCharsets.UTF_8);
        RCSMMessage temp = GsonUtil.json2Obj(jsonStr, RCSMMessage.class);
        if (null != temp) {
            this.userId = temp.userId;
            this.message = temp.message;
            this.status = temp.status;
        }
    }

    @Override
    public byte[] encode() {
        String json = GsonUtil.obj2Json(this);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.message);
        dest.writeInt(this.status);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readString();
        this.message = source.readString();
        this.status = source.readInt();
    }

    public RCSMMessage() {
    }

    protected RCSMMessage(Parcel in) {
        this.userId = in.readString();
        this.message = in.readString();
        this.status = in.readInt();
    }

    public static final Creator<RCSMMessage> CREATOR = new Creator<RCSMMessage>() {
        @Override
        public RCSMMessage createFromParcel(Parcel source) {
            return new RCSMMessage(source);
        }

        @Override
        public RCSMMessage[] newArray(int size) {
            return new RCSMMessage[size];
        }
    };
}
