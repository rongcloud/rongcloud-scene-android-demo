package cn.rongcloud.config.provider.user;

import android.net.Uri;
import android.text.TextUtils;

import java.io.Serializable;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.provider.wrapper.Provide;
import io.rong.imlib.model.UserInfo;

/**
 * 通用用户信息
 */
public class User implements Serializable, Provide {
    private String userId;
    private String userName;
    private String portrait;
    private int type;
    private String authorization;
    private String imToken;
    private String phone;
    private String sex;// 兼容 sex：1 和sex："男"

    public void setSex(Sex sex) {
        this.sex = sex.getSex() + "";
    }

    public Sex getSex() {
        return Sex.sexOf(this.sex);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getType() {
        return type;
    }

    public String getAuthorization() {
        return authorization;
    }

    public String getImToken() {
        return imToken;
    }

    @Override
    public String getKey() {
        return userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Deprecated
    public String getPortrait() {
        return portrait;
    }

    public String getPortraitUrl() {
        return TextUtils.isEmpty(portrait) ?
                ApiConfig.DEFAULT_PORTRAIT_ULR :
                portrait.startsWith("http")
                        ? portrait
                        : ApiConfig.FILE_URL + portrait;
    }

    public Uri getPortraitUri() {
        return Uri.parse(getPortraitUrl());
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return !TextUtils.isEmpty(userId) && userId.equals(user.userId);
    }

    public UserInfo toUserInfo() {
        return new UserInfo(userId, userName, getPortraitUri());
    }

    public static User fromUserInfo(UserInfo userInfo) {
        User user = new User();
        if (null != userInfo) {
            user.userId = userInfo.getUserId();
            user.userName = userInfo.getName();
            user.portrait = userInfo.getPortraitUri().toString();
        }
        return user;
    }


}
