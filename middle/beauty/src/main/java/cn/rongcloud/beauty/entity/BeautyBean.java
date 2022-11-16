package cn.rongcloud.beauty.entity;

import java.io.Serializable;

import cn.rongcloud.beauty.utils.DecimalUtils;

/**
 * @author gyn
 * @date 2022/9/15
 */
public class BeautyBean implements Serializable {

    private String key; // 名称标识
    private int desRes; // 描述
    private int closeRes; // 图片/选择器, 关闭了某项，包含选中未选中
    private int openRes; // 图片/选择器，开启了某项，包含选中未选中
    private float intensity = 0.0f; // 当前强度
    private float max = 1.0f; // 最大强度，当最大=最小，没有进度条
    private float min = 0.0f; // 最小强度
    private float stand = 0.0f; // 标准强度，设置后无变化

    public BeautyBean(String key, int desRes, int closeRes, int openRes, float intensity) {
        this.key = key;
        this.desRes = desRes;
        this.closeRes = closeRes;
        this.openRes = openRes;
        this.intensity = intensity;
    }

    public BeautyBean(String key, int desRes, int closeRes, int openRes, float intensity, float max, float min) {
        this.key = key;
        this.desRes = desRes;
        this.closeRes = closeRes;
        this.openRes = openRes;
        this.intensity = intensity;
        this.max = max;
        this.min = min;
    }

    public BeautyBean(String key, int desRes, int closeRes, int openRes, float intensity, float max, float min, float stand) {
        this.key = key;
        this.desRes = desRes;
        this.closeRes = closeRes;
        this.openRes = openRes;
        this.intensity = intensity;
        this.max = max;
        this.min = min;
        this.stand = stand;
    }

    public BeautyBean(String key, int desRes, int closeRes, int openRes) {
        this.key = key;
        this.desRes = desRes;
        this.closeRes = closeRes;
        this.openRes = openRes;
    }

    public BeautyBean(String key, int desRes, int res, float intensity) {
        this.key = key;
        this.desRes = desRes;
        this.closeRes = res;
        this.openRes = res;
        this.intensity = intensity;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getDesRes() {
        return desRes;
    }

    public void setDesRes(int desRes) {
        this.desRes = desRes;
    }

    public int getCloseRes() {
        return closeRes;
    }

    public void setCloseRes(int closeRes) {
        this.closeRes = closeRes;
    }

    public int getOpenRes() {
        return openRes;
    }

    public void setOpenRes(int openRes) {
        this.openRes = openRes;
    }

    public float getIntensity() {
        return intensity;
    }

    // 设置强度值，同时判断是否开启或关闭了该属性，跟标准值对比
    public boolean setIntensity(float intensity) {
        boolean isOpenChanged = false;
        if (DecimalUtils.doubleEquals(this.intensity, stand) && !DecimalUtils.doubleEquals(intensity, stand)
                || !DecimalUtils.doubleEquals(this.intensity, stand) && DecimalUtils.doubleEquals(intensity, stand)) {
            isOpenChanged = true;
        }
        this.intensity = intensity;
        return isOpenChanged;
    }

    // 当前是否是标准值
    public boolean isStand() {
        return DecimalUtils.doubleEquals(intensity, stand);
    }

    public float getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    // 是否支持设置范围
    public boolean hasIntensity() {
        return !DecimalUtils.doubleEquals(max - min, 0);
    }

    // 是否是双向调节
    public boolean isDoubleDirection() {
        return !DecimalUtils.doubleEquals(min, stand);
    }

    public BeautyBean copy() {
        return new BeautyBean(key, desRes, closeRes, openRes, intensity, max, min, stand);
    }

    @Override
    public String toString() {
        return "BeautyBean{" +
                "key='" + key + '\'' +
                ", desRes=" + desRes +
                ", closeRes=" + closeRes +
                ", openRes=" + openRes +
                ", intensity=" + intensity +
                ", max=" + max +
                ", min=" + min +
                ", stand=" + stand +
                '}';
    }
}
