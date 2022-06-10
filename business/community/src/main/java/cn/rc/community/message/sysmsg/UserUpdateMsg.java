package cn.rc.community.message.sysmsg;

import android.os.Parcel;

import com.basis.utils.GsonUtil;

import java.nio.charset.StandardCharsets;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 用户更新头像或者昵称信息
 */
@MessageTag(value = "RCMic:UserUpdate", flag = MessageTag.ISCOUNTED)
public class UserUpdateMsg extends MessageContent {
    private String portrait;
    private String nickName;
    private int type;//1代表修改头像，2代表修改社区昵称
    private String userId;

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UpdateUserInfoType getType() {
        return UpdateUserInfoType.typeOf(type);
    }

    public UserUpdateMsg(byte[] data) {
        super(data);
        String jsonStr = new String(data, StandardCharsets.UTF_8);
        UserUpdateMsg temp = GsonUtil.json2Obj(jsonStr, UserUpdateMsg.class);
        if (null != temp) {
            this.portrait = temp.portrait;
            this.nickName = temp.nickName;
            this.type = temp.type;
            this.userId = temp.userId;
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
        dest.writeString(this.nickName);
        dest.writeString(this.portrait);
        dest.writeInt(this.type);
        dest.writeString(this.userId);
    }

    public void readFromParcel(Parcel source) {
        this.nickName = source.readString();
        this.portrait = source.readString();
        this.type = source.readInt();
        this.userId = source.readString();
    }

    public UserUpdateMsg() {
    }

    protected UserUpdateMsg(Parcel in) {
        this.nickName = in.readString();
        this.portrait = in.readString();
        this.type = in.readInt();
        this.userId = in.readString();
    }

    public static final Creator<UserUpdateMsg> CREATOR = new Creator<UserUpdateMsg>() {
        @Override
        public UserUpdateMsg createFromParcel(Parcel source) {
            return new UserUpdateMsg(source);
        }

        @Override
        public UserUpdateMsg[] newArray(int size) {
            return new UserUpdateMsg[size];
        }
    };

}
