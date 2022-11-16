package cn.rongcloud.beauty.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author gyn
 * @date 2022/10/14
 */
public class StickerCategory implements Serializable {
    private String name;
    @SerializedName("data")
    private List<StickerBean> stickerBeanList;
    // 0 普通，1中级，2高级
    private int category;

    public StickerCategory() {
    }

    public StickerCategory(String name, List<StickerBean> stickerBeanList) {
        this.name = name;
        this.stickerBeanList = stickerBeanList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StickerBean> getStickerBeanList() {
        return stickerBeanList;
    }

    public void setStickerBeanList(List<StickerBean> stickerBeanList) {
        this.stickerBeanList = stickerBeanList;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
