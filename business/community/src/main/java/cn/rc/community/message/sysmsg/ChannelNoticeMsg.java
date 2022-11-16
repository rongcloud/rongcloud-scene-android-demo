package cn.rc.community.message.sysmsg;

import android.os.Parcel;

import com.basis.utils.GsonUtil;

import java.nio.charset.StandardCharsets;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 社区通知消息
 */
@MessageTag(value = "RCMic:ChannelNotice", flag = MessageTag.ISCOUNTED)
public class ChannelNoticeMsg extends MessageContent {
    private String communityUid;
    private String message;
    private int type;
    private String fromUserId;
    private String channelUid;
    private String toUserId;

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getChannelUid() {
        return channelUid;
    }

    public void setChannelUid(String channelUid) {
        this.channelUid = channelUid;
    }

    public String getCommunityUid() {
        return communityUid;
    }

    public String getMessage() {
        return message;
    }

    public ChannelType getType() {
        return ChannelType.typeOf(type);
    }

    public ChannelNoticeMsg(byte[] data) {
        super(data);
        String jsonStr = new String(data, StandardCharsets.UTF_8);
        ChannelNoticeMsg temp = GsonUtil.json2Obj(jsonStr, ChannelNoticeMsg.class);
        if (null != temp) {
            this.communityUid = temp.communityUid;
            this.message = temp.message;
            this.type = temp.type;
            this.fromUserId = temp.fromUserId;
            this.channelUid = temp.channelUid;
            this.toUserId = temp.toUserId;
        }
    }

    @Override
    public byte[] encode() {
        String json = GsonUtil.obj2Json(this);
        return json.getBytes(StandardCharsets.UTF_8);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.communityUid);
        dest.writeString(this.message);
        dest.writeInt(this.type);
        dest.writeString(this.fromUserId);
        dest.writeString(this.channelUid);
        dest.writeString(this.toUserId);
    }

    public void readFromParcel(Parcel source) {
        this.communityUid = source.readString();
        this.message = source.readString();
        this.type = source.readInt();
        this.fromUserId = source.readString();
        this.channelUid = source.readString();
        this.toUserId = source.readString();
    }

    public ChannelNoticeMsg() {
    }

    protected ChannelNoticeMsg(Parcel in) {
        this.communityUid = in.readString();
        this.message = in.readString();
        this.type = in.readInt();
        this.fromUserId = in.readString();
        this.channelUid = in.readString();
        this.toUserId = in.readString();
    }

    public static final Creator<ChannelNoticeMsg> CREATOR = new Creator<ChannelNoticeMsg>() {
        @Override
        public ChannelNoticeMsg createFromParcel(Parcel source) {
            return new ChannelNoticeMsg(source);
        }

        @Override
        public ChannelNoticeMsg[] newArray(int size) {
            return new ChannelNoticeMsg[size];
        }
    };

}
