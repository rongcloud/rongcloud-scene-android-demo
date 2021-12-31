package cn.rong.combusis.provider.user;

import android.net.Uri;
import android.text.TextUtils;

import com.rongcloud.common.net.ApiConstant;

import java.io.Serializable;

import cn.rong.combusis.provider.wrapper.Provide;
import io.rong.imlib.model.UserInfo;

public class User implements Serializable, Provide {
    private String userId;
    private String userName;
    private String portrait;

    @Override
    public String getKey() {
        return userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPortrait() {
        return portrait;
    }

    public String getPortraitUrl() {
        return TextUtils.isEmpty(portrait) ?
                ApiConstant.INSTANCE.getDEFAULT_PORTRAIT_ULR()
                : ApiConstant.INSTANCE.getFILE_URL() + portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return TextUtils.isEmpty(userId) && userId.equals(user.userId);
    }

    public UserInfo toUserInfo() {
        return new UserInfo(userId, userName, Uri.parse(getPortraitUrl()));
    }
}
