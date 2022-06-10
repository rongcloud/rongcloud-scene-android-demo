package cn.rc.community.bean;

import android.text.TextUtils;

import cn.rc.community.helper.CommunityHelper;
import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.provider.wrapper.Provide;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/13
 * @time 12:10
 * 用户社区信息
 */
public class UltraGroupUserBean implements Provide {

    private String nickName;
    private String portrait;
    private String userUid;
    private int status;//用户审核状态

    public int getStatus() {
        return status;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPortrait() {
        return TextUtils.isEmpty(portrait) ?
                ApiConfig.DEFAULT_PORTRAIT_ULR :
                portrait.startsWith("http")
                        ? portrait
                        : ApiConfig.FILE_URL + portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    @Override
    public String getKey() {
        return userUid + "-%-" + CommunityHelper.getInstance().getCommunityUid();
    }
}
