package cn.rc.community.bean;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/25
 * @time 4:37 下午
 * 分组和频道的父类
 */
public class ListBean {

    public String name;
    public int sort;
    public String uid;

    public ListBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
