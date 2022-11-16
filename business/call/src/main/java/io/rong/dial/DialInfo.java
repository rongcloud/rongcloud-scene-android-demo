package io.rong.dial;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import cn.rongcloud.config.ApiConfig;

public class DialInfo implements Serializable {
    private String phone;
    private String userId;
    private String head;
    private long date;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DialInfo dialInfo = (DialInfo) o;
        return Objects.equals(phone, dialInfo.phone);
    }

    public static DialInfo fromUserInfoModel(UserInfoModel user) {
        DialInfo dial = new DialInfo();
        dial.userId = user.getUid();
        dial.phone = user.getMobile();
        dial.head = TextUtils.isEmpty(user.getPortrait()) ? ApiConfig.DEFAULT_PORTRAIT_ULR
                : ApiConfig.FILE_URL + user.getPortrait();//拼接前缀
        dial.date = new Date().getTime();
        return dial;
    }
}
