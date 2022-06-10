package cn.rc.community.bean;

import android.text.TextUtils;

import java.util.List;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.User;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/29
 * @time 7:31 下午
 */
public class CommunityDetailsBean implements Cloneable {


    private List<ChannelBean> channelList;//根目录的频道
    private CommunityUserBean communityUser;
    private String coverUrl;
    private List<GroupBean> groupList;
    private int id;
    private String joinChannelUid;//用户默认进入的频道
    private String name;
    private int needAudit;//是否需要审核 0 不需要 1需要
    private String msgChannelUid;
    private int noticeType;//创建者设置的通知类型
    private int personCount;//人数
    private String portrait;
    private String remark;//简介
    private String uid;
    private int updateType;//更新类型 1为分组 2为频道 3为整体
    private String creator;

    public boolean isCreator() {
        User user = UserManager.get();
        return null != user && TextUtils.equals(creator, user.getUserId());
    }

    public CommunityDetailsBean clone() {
        try {
            return (CommunityDetailsBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ChannelBean> getChannelList() {
        return channelList;
    }

    public List<GroupBean> getGroupList() {
        return groupList;
    }

    public void setChannelList(List<ChannelBean> channelList) {
        this.channelList = channelList;
    }

    public void setGroupList(List<GroupBean> groupList) {
        this.groupList = groupList;
    }

    public CommunityUserBean getCommunityUser() {
        return communityUser;
    }

    public void setCommunityUser(CommunityUserBean communityUser) {
        this.communityUser = communityUser;
    }

    public String getCoverUrl() {
        return TextUtils.isEmpty(coverUrl) ? ApiConfig.DEFAULT_PORTRAIT_ULR
                : ApiConfig.FILE_URL + coverUrl;//拼接前缀
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJoinChannelUid() {
        return joinChannelUid;
    }

    public void setJoinChannelUid(String joinChannelUid) {
        this.joinChannelUid = joinChannelUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNeedAudit() {
        return needAudit;
    }

    public void setNeedAudit(int needAudit) {
        this.needAudit = needAudit;
    }

    public String getMsgChannelUid() {
        return msgChannelUid;
    }

    public void setMsgChannelUid(String msgChannelUid) {
        this.msgChannelUid = msgChannelUid;
    }

    public int getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(int noticeType) {
        this.noticeType = noticeType;
    }

    public int getPersonCount() {
        return personCount;
    }

    public void setPersonCount(int personCount) {
        this.personCount = personCount;
    }

    public String getPortrait() {
        return TextUtils.isEmpty(portrait) ? ApiConfig.DEFAULT_PORTRAIT_ULR
                : ApiConfig.FILE_URL + portrait;//拼接前缀
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public static class CommunityUserBean {
        private int auditStatus;
        private int noticeType;//用户设置的社区通知类型
        private int shutUp;//是否被禁言0:没有,1:禁言
        private String nickName;//我在本社区的昵称

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getShutUp() {
            return shutUp;
        }

        public void setShutUp(int shutUp) {
            this.shutUp = shutUp;
        }

        public int getAuditStatus() {
            return auditStatus;
        }

        public void setAuditStatus(int auditStatus) {
            this.auditStatus = auditStatus;
        }

        public int getNoticeType() {
            return noticeType;
        }

        public void setNoticeType(int noticeType) {
            this.noticeType = noticeType;
        }
    }

}
