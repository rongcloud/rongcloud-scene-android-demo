package cn.rongcloud.roomkit.message;

import android.os.Parcel;

import com.basis.utils.GsonUtil;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2021/10/11
 */

@MessageTag(value = "RC:VRFollowMsg")
public class RCFollowMsg extends MessageContent {
    public static final Creator<RCFollowMsg> CREATOR = new Creator<RCFollowMsg>() {
        @Override
        public RCFollowMsg createFromParcel(Parcel source) {
            return new RCFollowMsg(source);
        }

        @Override
        public RCFollowMsg[] newArray(int size) {
            return new RCFollowMsg[size];
        }
    };
    private static final String TAG = "RCFollowMsg";
    @SerializedName("_userInfo")
    private User userInfoSelf;
    @SerializedName("_targetUserInfo")
    private User targetUserInfo;

    public RCFollowMsg(byte[] data) {
        super(data);
        String jsonStr = new String(data, StandardCharsets.UTF_8);
        RCFollowMsg msg = GsonUtil.json2Obj(jsonStr, RCFollowMsg.class);
        if (msg != null) {
            userInfoSelf = msg.userInfoSelf;
            targetUserInfo = msg.targetUserInfo;
        }
    }

    public RCFollowMsg() {
    }

    protected RCFollowMsg(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public byte[] encode() {
        return GsonUtil.obj2Json(this).getBytes(StandardCharsets.UTF_8);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(GsonUtil.obj2Json(userInfoSelf));
        dest.writeString(GsonUtil.obj2Json(targetUserInfo));
    }

    public void readFromParcel(Parcel source) {
        this.userInfoSelf = GsonUtil.json2Obj(source.readString(), User.class);
        this.targetUserInfo = GsonUtil.json2Obj(source.readString(), User.class);
    }

    public User getUserInfoSelf() {
        return userInfoSelf;
    }

    public void setUserInfoSelf(User userInfoSelf) {
        this.userInfoSelf = userInfoSelf;
    }

    public cn.rongcloud.config.provider.user.User getUser() {
        return userInfoSelf.toUser();
    }

    public void setUser(cn.rongcloud.config.provider.user.User user) {
        this.userInfoSelf = new User();
        userInfoSelf.setId(user.getUserId());
        userInfoSelf.setName(user.getUserName());
        userInfoSelf.setPortrait(user.getPortraitUrl());
    }

    public cn.rongcloud.config.provider.user.User getTargetUser() {
        return targetUserInfo.toUser();
    }

    public void setTargetUser(cn.rongcloud.config.provider.user.User user) {
        this.targetUserInfo = new User();
        targetUserInfo.setId(user.getUserId());
        targetUserInfo.setName(user.getUserName());
        targetUserInfo.setPortrait(user.getPortraitUrl());
    }

    public User getTargetUserInfo() {
        return targetUserInfo;
    }

    public void setTargetUserInfo(User targetUserInfo) {
        this.targetUserInfo = targetUserInfo;
    }

    class User implements Serializable {
        private String id;
        private String name;
        private String portrait;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        public cn.rongcloud.config.provider.user.User toUser() {
            cn.rongcloud.config.provider.user.User user = new cn.rongcloud.config.provider.user.User();
            user.setUserId(id);
            user.setUserName(name);
            user.setPortrait(portrait);
            return user;
        }
    }
}
