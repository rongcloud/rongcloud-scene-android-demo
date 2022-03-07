package cn.rongcloud.config.init.shumei;

import android.os.Parcel;

import com.basis.utils.GsonUtil;

import java.nio.charset.StandardCharsets;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 多端登录消息
 */
@MessageTag(value = "RCMic:loginDeviceMsg")
public class RCDeviceMessage extends MessageContent {
    private String userId;
    private String platform;

    public RCDeviceMessage(byte[] data) {
        super(data);
        String jsonStr = new String(data, StandardCharsets.UTF_8);
        RCDeviceMessage temp = GsonUtil.json2Obj(jsonStr, RCDeviceMessage.class);
        if (null != temp) {
            this.userId = temp.userId;
            this.platform = temp.platform;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.platform);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readString();
        this.platform = source.readString();
    }

    public RCDeviceMessage() {
    }

    protected RCDeviceMessage(Parcel in) {
        this.userId = in.readString();
        this.platform = in.readString();
    }

    public static final Creator<RCDeviceMessage> CREATOR = new Creator<RCDeviceMessage>() {
        @Override
        public RCDeviceMessage createFromParcel(Parcel source) {
            return new RCDeviceMessage(source);
        }

        @Override
        public RCDeviceMessage[] newArray(int size) {
            return new RCDeviceMessage[size];
        }
    };
}
