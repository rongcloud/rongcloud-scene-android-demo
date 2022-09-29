package cn.rongcloud.config.init;

import android.text.TextUtils;

import com.basis.net.oklib.wrapper.OkHelper;
import com.basis.net.oklib.wrapper.interfaces.IHeader;

import java.util.HashMap;
import java.util.Map;

import cn.rongcloud.config.AppConfig;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.User;
import okhttp3.Headers;

public class OKModule implements IModule {
    protected OKModule() {
    }

    @Override
    public void onInit() {
        OkHelper.get().setHeadCacher(new IHeader() {
            @Override
            public Map<String, String> onAddHeader() {
                Map<String, String> map = new HashMap<String, String>();
                User user = UserManager.get();
                if (null != user && !TextUtils.isEmpty(user.getAuthorization())) {
                    map.put("Authorization", user.getAuthorization());
                }
                if (!TextUtils.isEmpty(AppConfig.get().getBusinessToken())) {
                    map.put("BusinessToken", AppConfig.get().getBusinessToken());
                }
                return map;
            }

            @Override
            public void onCacheHeader(Headers headers) {
            }

        });
    }

    @Override
    public void onUnInit() {
    }

    @Override
    public void onRegisterMessageType() {

    }
}
