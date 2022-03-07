package cn.rongcloud.config.provider.user;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.wapper.IResultBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.provider.wrapper.IProvider;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.userinfo.UserDataProvider;
import io.rong.imlib.model.UserInfo;

/**
 * 用户信息提供者
 */
public class UserProvider implements IProvider<UserInfo> {
    private final static String TAG = "UserProvider";
    private final static String API_BATCH = ApiConfig.HOST + "/user/batch";
    private final static IProvider _provider = new UserProvider();
    private final UserObserver datObserver;
    private final Map<String, IResultBack> observers = new HashMap<>(4);
    private final Map<String, IResultBack> onceObservers = new HashMap<>(4);//只监听一次

    private UserProvider() {
        datObserver = new UserObserver(new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                String userId = null == userInfo ? null : userInfo.getUserId();
                if (TextUtils.isEmpty(userId)) {
                    return;
                }
                IResultBack<UserInfo> onceBack = onceObservers.remove(userInfo.getUserId());
                if (null != onceBack) onceBack.onResult(userInfo);
                IResultBack<UserInfo> observer = observers.get(userInfo.getUserId());
                if (null != observer) observer.onResult(userInfo);
            }
        });
        RongUserInfoManager.getInstance().addUserDataObserver(datObserver);
        //设置IM user provider 使用Im 提供的缓存机制
        RongUserInfoManager.getInstance().setUserInfoProvider(new UserDataProvider.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                provideFromService(Collections.singletonList(s), null);
                return null;
            }
        }, true);
    }

    public static IProvider<UserInfo> provider() {
        return _provider;
    }


    public void release() {
        RongUserInfoManager.getInstance().removeUserDataObserver(datObserver);
    }

    private UserInfo get(@NonNull String userId) {
        return RongUserInfoManager.getInstance().getUserInfo(userId);
    }

    @Override
    public void update(UserInfo userInfo) {
        RongUserInfoManager.getInstance().refreshUserInfoCache(userInfo);
    }

    @Override
    public void update(List<UserInfo> updates) {
        for (UserInfo u : updates) {
            RongUserInfoManager.getInstance().refreshUserInfoCache(u);
        }
    }

    @Override
    public void getAsyn(@NonNull String userId, @NonNull IResultBack<UserInfo> resultBack) {
        UserInfo info = RongUserInfoManager.getInstance().getUserInfo(userId);
        if (null != info) {
            resultBack.onResult(info);
            return;
        }
        //执行provider
        onceObservers.put(userId, resultBack);
    }


    @Override
    public void batchGetAsyn(@NonNull List<String> keys, @NonNull IResultBack<List<UserInfo>> resultBack) {
        Log.e(TAG, "NO implements: batchGetAsyn");
        int count = null != keys ? keys.size() : 0;
        List<UserInfo> userInfoList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String id = keys.get(i);
            UserInfo user = RongUserInfoManager.getInstance().getUserInfo(id);
            if (null != user) {
                userInfoList.size();
            }
        }
    }

    @Override
    public void observeSingle(@NonNull String userId, @NonNull IResultBack<UserInfo> resultBack) {
        if (!observers.containsKey(userId)) observers.put(userId, resultBack);
        UserInfo info = RongUserInfoManager.getInstance().getUserInfo(userId);
        if (null != info) {
            resultBack.onResult(info);
        }
    }

    @Override
    public void removeSingleObserver(String key) {
        if (null != key) observers.remove(key);
    }

    /**
     * @param ids        参数:params.put("userIds", keys);
     * @param resultBack
     */
    @Override
    public void provideFromService(List<String> ids, @Nullable IResultBack<List<UserInfo>> resultBack) {
        if (null == ids || ids.isEmpty()) {
            if (null != resultBack) resultBack.onResult(null);
            return;
        }
        Map<String, Object> params = new HashMap<>(2);
        params.put("userIds", ids);
        OkApi.post(API_BATCH, params, new WrapperCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "provideFromService#onError code  = " + code + " message = " + msg);
                if (null != resultBack) resultBack.onResult(null);
            }

            @Override
            public void onResult(Wrapper result) {
                List<User> users = result.getList(User.class);
                Log.e(TAG, "provideFromService: size = " + (null == users ? 0 : users.size()));
                //返回集
                List<UserInfo> userInfos = new ArrayList<>();
                if (null != users && !users.isEmpty()) {
                    for (User user : users) {
                        UserInfo info = user.toUserInfo();
                        //跟新cache UserObserver执行回调 ->observer
                        RongUserInfoManager.getInstance().refreshUserInfoCache(user.toUserInfo());
                        userInfos.add(info);
                    }
                }
                if (null != resultBack) resultBack.onResult(userInfos);
            }
        });
    }

    public void getFromService(List<String> ids, @Nullable IResultBack<List<User>> resultBack) {
        if (null == ids || ids.isEmpty()) {
            if (null != resultBack) resultBack.onResult(null);
            return;
        }
        Map<String, Object> params = new HashMap<>(2);
        params.put("userIds", ids);
        OkApi.post(API_BATCH, params, new WrapperCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "provideFromService#onError code  = " + code + " message = " + msg);
                if (null != resultBack) resultBack.onResult(null);
            }

            @Override
            public void onResult(Wrapper result) {
                List<User> users = result.getList(User.class);
                Log.e(TAG, "provideFromService: size = " + (null == users ? 0 : users.size()));
                if (null != resultBack) resultBack.onResult(users);
            }
        });
    }
}
