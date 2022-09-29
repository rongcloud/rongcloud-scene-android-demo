package cn.rongcloud.pk.bean;


import java.util.List;

import cn.rongcloud.config.provider.user.User;

public class PKInfo {
    private String userId;
    private String roomId;
    private int score;
    private List<User> userInfoList;

    public String getUserId() {
        return userId;
    }

    public int getScore() {
        return score;
    }

    public String getRoomId() {
        return roomId;
    }

    public List<User> getUserInfoList() {
        return userInfoList;
    }
}
