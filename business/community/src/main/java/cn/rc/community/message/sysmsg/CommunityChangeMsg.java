package cn.rc.community.message.sysmsg;

import android.os.Parcel;
import android.util.Log;

import com.basis.utils.GsonUtil;

import java.nio.charset.StandardCharsets;

import cn.rongcloud.config.init.shumei.RCDeviceMessage;
import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 社区变化通知消息
 */
@MessageTag(value = "RCMic:CommunityChange", flag = MessageTag.ISCOUNTED)
public class CommunityChangeMsg extends MessageContent {
    private String communityUid;
    private String fromUserId;
    private String message;
    private String[] channelUids;

    public CommunityChangeMsg(byte[] data) {
        super(data);
        String jsonStr = new String(data, StandardCharsets.UTF_8);
        CommunityChangeMsg temp = GsonUtil.json2Obj(jsonStr, CommunityChangeMsg.class);
        if (null != temp) {
            this.communityUid = temp.communityUid;
            this.fromUserId = temp.fromUserId;
            this.message = temp.message;
            this.channelUids = temp.channelUids;
        }
    }

    public String getCommunityUid() {
        return communityUid;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getMessage() {
        return message;
    }

    public String[] getChannelUids() {
        return channelUids;
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
        dest.writeString(this.message);
        dest.writeStringArray(this.channelUids);
    }

    public void readFromParcel(Parcel source) {
        this.communityUid = source.readString();
        this.fromUserId = source.readString();
        this.message = source.readString();
        this.channelUids = source.createStringArray();
    }

    public CommunityChangeMsg() {
    }

    protected CommunityChangeMsg(Parcel in) {
        this.communityUid = in.readString();
        this.fromUserId = in.readString();
        this.message = in.readString();
        this.channelUids = in.createStringArray();
        Log.e("TAG", "CommunityChangeMsg: ");
    }

    public static final Creator<CommunityChangeMsg> CREATOR = new Creator<CommunityChangeMsg>() {
        @Override
        public CommunityChangeMsg createFromParcel(Parcel source) {
            return new CommunityChangeMsg(source);
        }

        @Override
        public CommunityChangeMsg[] newArray(int size) {
            return new CommunityChangeMsg[size];
        }
    };
}
