package cn.rongcloud.config.feedback;

import com.basis.utils.UIKit;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import cn.rongcloud.config.UserManager;

public class UmengHelper {
    private final static UmengHelper helper = new UmengHelper();

    private UmengHelper() {
    }

    public static UmengHelper get() {
        return helper;
    }


    public void event(RcUmEvent event) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("userid", UserManager.get().getUserId());
        MobclickAgent.onEventObject(UIKit.getContext(), event.name(), params);
    }

}