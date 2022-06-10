package cn.rc.community.bean;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/24
 * @time 6:35 下午
 * 频道实体类
 */
public class ChannelBean extends ListBean {

    private String groupUid;
    private int noticeType;

    public ChannelBean(String name) {
        super(name);
    }

    public String getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(String groupUid) {
        this.groupUid = groupUid;
    }

    public int getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(int noticeType) {
        this.noticeType = noticeType;
    }
}
