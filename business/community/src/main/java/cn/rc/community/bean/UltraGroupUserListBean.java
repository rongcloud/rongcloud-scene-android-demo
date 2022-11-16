package cn.rc.community.bean;


import java.util.List;

import cn.rongcloud.config.provider.wrapper.Provide;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/18
 * @time 12:04
 * 当前社区的信息集合
 */
public class UltraGroupUserListBean implements Provide {

    private String targetId;
    private List<UltraGroupUserBean> userBeans;


    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public List<UltraGroupUserBean> getUserBeans() {
        return userBeans;
    }

    public void setUserBeans(List<UltraGroupUserBean> userBeans) {
        this.userBeans = userBeans;
    }

    @Override
    public String getKey() {
        return targetId;
    }
}
