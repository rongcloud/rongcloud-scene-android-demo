package cn.rc.community.bean;

import android.text.TextUtils;

import cn.rongcloud.config.ApiConfig;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/24
 * @time 4:41 下午
 * 社区列表实体类
 */
public class CommunityBean {

    public CommunityBean(String name, String communityUid) {
        this.name = name;
        this.communityUid = communityUid;
    }

    //社区名称
    private String name;
    //社区自定义的唯一标识
    private String communityUid;
    //社区icon
    private String portrait;
    // 已加入人数
    private int personCount;
    // 社区描述
    private String remark;
    // 业务数据 当前未读消息数
    private int unReadCount;
    // 业务数据 上次切换时的未读数
    private int lastUnReadCount;

    private String coverUrl;

    public String getCoverUrl() {
        return TextUtils.isEmpty(coverUrl) ?
                ApiConfig.DEFAULT_PORTRAIT_ULR
                : coverUrl.startsWith("http") ? coverUrl : ApiConfig.FILE_URL + coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public boolean showRedPoint() {
        //切走前和当前相比，有新增则显示，没新增则不显示
        return unReadCount > lastUnReadCount;
    }

    public void refreshLastUnReadCount() {
        // 将当前未读数赋值给last
        lastUnReadCount = unReadCount;
    }

    public void setUnread(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getRemark() {
        return remark;
    }

    public String getPortrait() {
        return TextUtils.isEmpty(portrait) ?
                ApiConfig.DEFAULT_PORTRAIT_ULR
                : portrait.startsWith("http") ? portrait : ApiConfig.FILE_URL + portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCommunityUid(String communityUid) {
        this.communityUid = communityUid;
    }

    public String getCommunityUid() {
        return communityUid;
    }

    public int getPersonCount() {
        return personCount;
    }
}
