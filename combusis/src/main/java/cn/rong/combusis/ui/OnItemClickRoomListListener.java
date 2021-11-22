package cn.rong.combusis.ui;

import java.util.List;

public interface OnItemClickRoomListListener<T> {
    void clickItem(T item, int position, boolean isCreate, List<T> list);
}