package cn.rong.combusis.umeng;

import com.rongcloud.common.utils.AccountStore;
import com.rongcloud.common.utils.UIKit;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

public class UmengHelper {
    private final static UmengHelper helper = new UmengHelper();

    private UmengHelper() {
    }

    public static UmengHelper get() {
        return helper;
    }


    public void event(RcUmEvent event) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("userid", AccountStore.INSTANCE.getUserId());
        MobclickAgent.onEventObject(UIKit.getContext(), event.name(), params);
    }

}