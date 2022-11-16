package cn.rongcloud.beauty.listener;

import java.util.List;

import cn.rongcloud.beauty.entity.StickerBean;
import cn.rongcloud.beauty.entity.StickerCategory;

/**
 * @author gyn
 * @date 2022/10/14
 */
public interface StickerDataSourceListener {

    void getStickerCategories(DataCallback<List<StickerCategory>> dataCallback);

    StickerBean getSelectedSticker();

    void onSelectSticker(StickerBean stickerBean, DataCallback<Boolean> dataCallback);

    void onClearSticker();
}
