package cn.rongcloud.beauty.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author gyn
 * @date 2022/9/16
 */
public class BeautyCategory implements Serializable {
    // 分类名称
    private String name;
    // 选项集合
    private List<BeautyBean> beautyBeanList;
    // 是否可恢复
    private boolean hasRecovery;

    private BeautyBean defaultSelected;

    public BeautyCategory(String name, List<BeautyBean> beautyBeanList, boolean hasRecovery) {
        this.name = name;
        this.beautyBeanList = beautyBeanList;
        this.hasRecovery = hasRecovery;
    }

    public BeautyCategory(String name, List<BeautyBean> beautyBeanList, boolean hasRecovery, BeautyBean defaultSelected) {
        this.name = name;
        this.beautyBeanList = beautyBeanList;
        this.hasRecovery = hasRecovery;
        this.defaultSelected = defaultSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BeautyBean> getBeautyBeanList() {
        return beautyBeanList;
    }

    public void setBeautyBeanList(List<BeautyBean> beautyBeanList) {
        this.beautyBeanList = beautyBeanList;
    }

    public boolean isHasRecovery() {
        return hasRecovery;
    }

    public void setHasRecovery(boolean hasRecovery) {
        this.hasRecovery = hasRecovery;
    }

    public BeautyBean getDefaultSelected() {
        return defaultSelected;
    }

    public void setDefaultSelected(BeautyBean defaultSelected) {
        this.defaultSelected = defaultSelected;
    }
}
