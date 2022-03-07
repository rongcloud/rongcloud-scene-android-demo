package cn.rongcloud.roomkit.ui;

import java.util.List;

public interface OnItemClickRoomListListener<T> {
    void clickItem(T item, int position, boolean isCreate, List<T> list);
}