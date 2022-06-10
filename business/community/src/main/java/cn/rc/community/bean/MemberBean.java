package cn.rc.community.bean;

import android.text.TextUtils;

import java.util.List;

import cn.rongcloud.config.ApiConfig;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/7
 * @time 7:01 下午
 */
public class MemberBean {

    private int current;
    private int pages;
    private List<RecordsBean> records;
    private boolean searchCount;
    private int size;
    private int total;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<RecordsBean> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsBean> records) {
        this.records = records;
    }

    public boolean isSearchCount() {
        return searchCount;
    }

    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public static class RecordsBean {
        private boolean creatorFlag;
        private String name;
        private String portrait;
        private String userUid;
        private int shutUp;//是否被禁言0:没有,1:禁言

        public int getShutUp() {
            return shutUp;
        }

        public void setShutUp(int shutUp) {
            this.shutUp = shutUp;
        }

        public boolean isCreatorFlag() {
            return creatorFlag;
        }

        public void setCreatorFlag(boolean creatorFlag) {
            this.creatorFlag = creatorFlag;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
    }
}
