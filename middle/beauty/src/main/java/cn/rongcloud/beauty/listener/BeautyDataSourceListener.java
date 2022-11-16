package cn.rongcloud.beauty.listener;

import java.util.List;

import cn.rongcloud.beauty.entity.BeautyBean;
import cn.rongcloud.beauty.entity.BeautyCategory;

/**
 * @author gyn
 * @date 2022/9/16
 */
public interface BeautyDataSourceListener {
    List<BeautyCategory> getDefaultBeautyData();

    List<BeautyCategory> getCurrentBeautyData();

    void onEnableBeauty(boolean enable);

    void onSetBeauty(BeautyBean beauty);
}
