package cn.rong.combusis.userprovide;

import android.net.Uri;
import android.text.TextUtils;

import com.rongcloud.common.net.ApiConstant;

import java.io.Serializable;

import io.rong.imlib.model.UserInfo;

public class User implements Serializable {
    private String userId;
    private String userName;
    private String portrait;

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
        return new UserInfo(userId, userName, Uri.parse(getPortrait()));
    }
}
