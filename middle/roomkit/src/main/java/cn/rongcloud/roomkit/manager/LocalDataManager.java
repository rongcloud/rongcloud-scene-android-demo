package cn.rongcloud.roomkit.manager;

import com.basis.utils.GsonUtil;
import com.basis.utils.SharedPreferUtil;

import java.util.List;

/**
 * @author gyn
 * @date 2022/2/15
 */
public class LocalDataManager {
    private final static String ROOM_BG = "ROOM_BG";

    public static List<String> getBackGroundUrlList() {
        String json = SharedPreferUtil.get(ROOM_BG);
        return GsonUtil.json2List(json, String.class);
    }

    public static void saveBackGroundUrl(List<String> list) {
        if (list != null) {
            SharedPreferUtil.set(ROOM_BG, GsonUtil.obj2Json(list));
        }
    }

    public static String getBackgroundByIndex(int index) {
        List<String> backGroundUrlList = getBackGroundUrlList();
        if (index < 0 || backGroundUrlList == null || index >= backGroundUrlList.size()) {
            return null;
        } else {
            return backGroundUrlList.get(index);
        }
    }
}
