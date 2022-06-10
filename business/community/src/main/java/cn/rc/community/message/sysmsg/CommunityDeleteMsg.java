package cn.rc.community.message.sysmsg;

import android.os.Parcel;

import com.basis.utils.GsonUtil;

import java.nio.charset.StandardCharsets;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 社区删除消息
 */
@MessageTag(value = "RCMic:CommunityDelete",flag = MessageTag.ISCOUNTED)
public class CommunityDeleteMsg extends MessageContent {
    private String communityUid;
    private String fromUserId;
    public CommunityDeleteMsg(byte[] data) {
        super(data);
        String jsonStr = new String(data, StandardCharsets.UTF_8);
        CommunityDeleteMsg temp = GsonUtil.json2Obj(jsonStr, CommunityDeleteMsg.class);
        if (null != temp) {
            this.communityUid = temp.communityUid;
            this.fromUserId = temp.fromUserId;
        }
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getCommunityUid() {
        return communityUid;
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
        dest.writeString(this.fromUserId);
    }

    public void readFromParcel(Parcel source) {
        this.communityUid = source.readString();
        this.fromUserId = source.readString();
    }

    public CommunityDeleteMsg() {
    }

    protected CommunityDeleteMsg(Parcel in) {
        this.communityUid = in.readString();
        this.fromUserId = in.readString();
    }

    public static final Creator<CommunityDeleteMsg> CREATOR = new Creator<CommunityDeleteMsg>() {
        @Override
        public CommunityDeleteMsg createFromParcel(Parcel source) {
            return new CommunityDeleteMsg(source);
        }

        @Override
        public CommunityDeleteMsg[] newArray(int size) {
            return new CommunityDeleteMsg[size];
        }
    };
}
