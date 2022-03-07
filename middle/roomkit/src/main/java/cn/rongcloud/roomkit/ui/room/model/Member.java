package cn.rongcloud.roomkit.ui.room.model;

import android.net.Uri;
import android.text.TextUtils;

import java.io.Serializable;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.provider.user.User;
import io.rong.imlib.model.UserInfo;

/**
 * @author gyn
 * @date 2021/10/9
 */
public class Member implements Serializable {
    private String userId;
    private String userName;
    private String portrait;
    // 是否是管理
    private int isAdmin = 0;
    // 是否已关注
    private int status = 0;
    // 麦位
    private int seatIndex = -1;

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
                ApiConfig.DEFAULT_PORTRAIT_ULR
                : ApiConfig.FILE_URL + portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public boolean isAdmin() {
        return isAdmin == 1;
    }

    public boolean isFollow() {
        return status == 1;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSeatIndex() {
        return seatIndex;
    }

    public void setSeatIndex(int seatIndex) {
        this.seatIndex = seatIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return TextUtils.isEmpty(userId) && userId.equals(user.getUserId());
    }

    public UserInfo toUserInfo() {
        return new UserInfo(userId, userName, Uri.parse(getPortraitUrl()));
    }

    public User toUser() {
        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setPortrait(portrait);
        return user;
    }

    public static Member fromUser(User user) {
        Member member = new Member();
        member.setUserId(user.getUserId());
        member.setUserName(user.getUserName());
        member.setPortrait(user.getPortrait());
        return member;
    }
}
