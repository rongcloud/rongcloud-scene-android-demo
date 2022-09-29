package cn.rc.community.bean;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/8
 * @time 6:42 下午
 * 频道详情实体类
 */
public class ChannelDetailsBean {

    private String name;
    private String remark;
    private String uid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
