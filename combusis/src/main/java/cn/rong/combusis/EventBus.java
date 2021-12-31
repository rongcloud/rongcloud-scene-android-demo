package cn.rong.combusis;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.rong.combusis.common.utils.UIKit;

public class EventBus {

    /**
     * 触发事件
     *
     * @param tag
     * @param args
     */
    public void emit(String tag, Object... args) {
        UIKit.runOnUiTherad(new Runnable() {
            @Override
            public void run() {
                List<EventCallback> cals = events.get(tag);
                int count = cals == null ? 0 : cals.size();
                for (int i = count - 1; i > -1; i--) {
                    cals.get(i).onEvent(tag, args);
                }
            }
        });
    }

    private final static EventBus _bus = new EventBus();
    private final LinkedHashMap<String, List<EventCallback>> events = new LinkedHashMap<>(16);

    private EventBus() {
    }

    public static EventBus get() {
        return _bus;
    }

    /**
     * 订阅
     *
     * @param tag
     * @param callback
     */
    public void on(String tag, EventCallback callback) {
        if (!events.containsValue(tag)) {
            List<EventCallback> cals = new ArrayList<>(4);
            cals.add(callback);
            events.put(tag, cals);
        } else {
            events.get(tag).add(callback);
        }
    }

    /**
     * 移除订阅
     *
     * @param tag
     * @param callback
     */
    public void off(String tag, @NonNull EventCallback callback) {
        if (null == callback) {
            events.remove(tag);
        } else {
            List temp = events.get(tag);
            if (null != temp) temp.remove(callback);
        }
    }

    public interface EventCallback {
        void onEvent(String tag, Object... args);
    }

    public class TAG {
        public final static String MUSIC_LIST = "music_play_list";
        public final static String PK_STATE = "pk_state";// pk状态
        public final static String PK_GIFT = "pk_gift";// pk礼物
        public final static String PK_RESPONSE = "pk_response"; // pk邀请响应状态
        public final static String PK_INVITE_QUIT = "pk_intivite_or_quit"; // 用户发起邀请和退出pk 修改状态
        public final static String PK_AUTO_MODIFY = "pk_outo_modify"; // 用户主动修改状态
        public final static String UPDATE_SHIELD = "UPDATE_SHIELD"; // 更新屏蔽词
    }
}
