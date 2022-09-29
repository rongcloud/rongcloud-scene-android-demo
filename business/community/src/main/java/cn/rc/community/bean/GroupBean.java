package cn.rc.community.bean;

import java.util.List;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/24
 * @time 6:39 下午
 * 分组实体类
 */
public class GroupBean extends ListBean {

    private boolean isExpansion = true;//默认为全展开

    public boolean isExpansion() {
        return isExpansion;
    }

    public void setExpansion(boolean expansion) {
        isExpansion = expansion;
    }

    private List<ChannelBean> channelList;

    public GroupBean(String name) {
        super(name);
    }

    public List<ChannelBean> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<ChannelBean> channelList) {
        this.channelList = channelList;
    }
}
