package cn.rongcloud.roomkit.ui.friend.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Friend implements Serializable {
    @SerializedName("userId")
    private String uid;
    @SerializedName("userName")
    private String name;
    @SerializedName("portrait")
    private String portrait;
    @SerializedName("status")
    private int status;

    private FollowStatus followStatus;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isChecked(int type) {
        switch (getFollowStatus(type)) {
            case FOLLOW_EACH:
            case FOLLOW_DONE:
                return true;
            default:
                return false;
        }
    }

    public String getFollowDesc(int type) {
        return getFollowStatus(type).desc;
    }

    public void setFollowStatus(FollowStatus status) {
        followStatus = status;
    }

    public FollowStatus getFollowStatus(int type) {
        if (followStatus != null) {
            return followStatus;
        }
        switch (type) {
            case 1:
                if (status == 1) {
                    followStatus = FollowStatus.FOLLOW_EACH;
                } else {
                    followStatus = FollowStatus.FOLLOW_DONE;
                }
                break;
            case 2:
                if (status == 1) {
                    followStatus = FollowStatus.FOLLOW_EACH;
                } else {
                    followStatus = FollowStatus.FOLLOW_BACK;
                }
                break;
            default:
                followStatus = FollowStatus.FOLLOW;
        }
        return followStatus;
    }

    public void changeFollowStatus(int type) {
        switch (getFollowStatus(type)) {
            case FOLLOW:
                if (status == 1) {
                    setFollowStatus(FollowStatus.FOLLOW_EACH);
                } else {
                    setFollowStatus(FollowStatus.FOLLOW_DONE);
                }
                break;
            case FOLLOW_BACK:
                setFollowStatus(FollowStatus.FOLLOW_EACH);
                break;
            case FOLLOW_DONE:
                setFollowStatus(FollowStatus.FOLLOW);
                break;
            case FOLLOW_EACH:
                if (type == 1) {
                    setFollowStatus(FollowStatus.FOLLOW);
                } else {
                    setFollowStatus(FollowStatus.FOLLOW_BACK);
                }
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return uid.equals(friend.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    public enum FollowStatus {
        FOLLOW_EACH("互相关注"),
        FOLLOW_BACK("回关"),
        FOLLOW_DONE("已关注"),
        FOLLOW("关注");

        private String desc;

        FollowStatus(String desc) {
            this.desc = desc;
        }
    }
}
