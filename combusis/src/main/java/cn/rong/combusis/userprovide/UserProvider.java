package cn.rong.combusis.userprovide;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.rongcloud.common.net.IResultBack;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rong.combusis.oklib.Core;
import cn.rong.combusis.oklib.GsonUtil;
import cn.rong.combusis.oklib.Wrapper;
import cn.rong.combusis.oklib.WrapperCallBack;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.userinfo.UserDataProvider;
import io.rong.imlib.model.UserInfo;

public class UserProvider implements IUserProvider, IResultBack<UserInfo>, UserDataProvider.UserInfoProvider {
    private final static String TAG = "UserProvider";
    private final static IUserProvider _provider = new UserProvider();
    private final UserObserver observer;
    private final Map<String, IResultBack> resultBackMap;

    @Override
    public UserInfo getUserInfo(String s) {
        List<String> ids = new ArrayList<>(2);
        ids.add(s);
        getUserInfoByIdsFromNet(ids);
        return null;
    }

    /**
     * UserObserver回调
     *
     * @param userInfo
     */
    @Override
    public void onResult(UserInfo userInfo) {
        IResultBack<UserInfo> resultBack = null;
        if (null != userInfo && !TextUtils.isEmpty(userInfo.getUserId())) {
            resultBack = resultBackMap.remove(userInfo.getUserId());
        }
        if (null != resultBack) resultBack.onResult(userInfo);
    }

    private UserProvider() {
        resultBackMap = new HashMap<>(4);
        RongUserInfoManager.getInstance().setUserInfoProvider(this, true);
        observer = new UserObserver(this);
        RongUserInfoManager.getInstance().addUserDataObserver(observer);
    }

    public static IUserProvider provider() {
        return _provider;
    }

    public void release() {
        RongUserInfoManager.getInstance().removeUserDataObserver(observer);
    }

    @Nullable
    @Override
    public UserInfo getUser(@NonNull String userId) {
        return RongUserInfoManager.getInstance().getUserInfo(userId);
    }

    @Override
    public void getUserAsyn(@NonNull String userId, @NonNull IResultBack<UserInfo> resultBack) {
        if (null == resultBack || TextUtils.isEmpty(userId)) return;
        UserInfo info = RongUserInfoManager.getInstance().getUserInfo(userId);
        if (null != info) {
            resultBack.onResult(info);
            return;
        }
        resultBackMap.put(userId, resultBack);
    }

    private void getUserInfoByIdsFromNet(List<String> userIds) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("userIds", userIds);
        Core.core().post(null, API_BATCH, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Log.e(TAG, GsonUtil.obj2Json(result));
                List<User> users = result.getList(User.class);
                if (null != users && !users.isEmpty()) {
                    //UserObserver执行回调
                    RongUserInfoManager.getInstance().refreshUserInfoCache(users.get(0).toUserInfo());
                }
            }
        });
    }
}
