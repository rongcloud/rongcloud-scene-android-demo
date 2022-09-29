package cn.rongcloud.roomkit.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gyn
 * @date 2022/1/11
 */
public class RoomListIdsCache {
    private final List<String> cacheRoomIds = new ArrayList<>();

    private static class Holder {
        private final static RoomListIdsCache INSTANCE = new RoomListIdsCache();
    }

    public static RoomListIdsCache get() {
        return Holder.INSTANCE;
    }

    public void update(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        cacheRoomIds.clear();
        cacheRoomIds.addAll(ids);
    }

    public boolean contains(String roomId) {
        return cacheRoomIds.contains(roomId);
    }
}
