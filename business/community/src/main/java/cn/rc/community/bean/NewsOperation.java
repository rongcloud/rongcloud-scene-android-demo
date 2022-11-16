package cn.rc.community.bean;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/9
 * @time 6:32 下午
 */
public class NewsOperation {
    private int icon;
    private String name;

    public NewsOperation(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
