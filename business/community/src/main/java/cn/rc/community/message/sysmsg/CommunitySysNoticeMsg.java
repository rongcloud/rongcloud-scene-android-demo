package cn.rc.community.message.sysmsg;

import android.os.Parcel;

import com.basis.utils.GsonUtil;

import java.nio.charset.StandardCharsets;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 社区通知消息
 */
@MessageTag(value = "RCMic:CommunitySysNotice", flag = MessageTag.ISCOUNTED)
public class CommunitySysNoticeMsg extends MessageContent {
    private String communityUid;
    private String message;
    private int type;
    private String fromUserId;

    public String getCommunityUid() {
        return communityUid;
    }

    public String getMessage() {
        return message;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public CommunityType getType() {
        return CommunityType.typeOf(type);
    }

    public CommunitySysNoticeMsg(byte[] data) {
        super(data);
        String jsonStr = new String(data, StandardCharsets.UTF_8);
        CommunitySysNoticeMsg temp = GsonUtil.json2Obj(jsonStr, CommunitySysNoticeMsg.class);
        if (null != temp) {
            this.communityUid = temp.communityUid;
            this.message = temp.message;
            this.type = temp.type;
            this.fromUserId = temp.fromUserId;
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
    }

    public void readFromParcel(Parcel source) {
        this.communityUid = source.readString();
        this.message = source.readString();
        this.type = source.readInt();
        this.fromUserId = source.readString();
    }

    public CommunitySysNoticeMsg() {
    }

    protected CommunitySysNoticeMsg(Parcel in) {
        this.communityUid = in.readString();
        this.message = in.readString();
        this.type = in.readInt();
        this.fromUserId = in.readString();
    }

    public static final Creator<CommunitySysNoticeMsg> CREATOR = new Creator<CommunitySysNoticeMsg>() {
        @Override
        public CommunitySysNoticeMsg createFromParcel(Parcel source) {
            return new CommunitySysNoticeMsg(source);
        }

        @Override
        public CommunitySysNoticeMsg[] newArray(int size) {
            return new CommunitySysNoticeMsg[size];
        }
    };

}
