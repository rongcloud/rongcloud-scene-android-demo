package cn.rongcloud.config.provider.user;


import com.basis.wapper.IResultBack;

import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.userinfo.model.GroupUserInfo;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

class UserObserver implements RongUserInfoManager.UserDataObserver {
    private IResultBack<UserInfo> resultBack;

    UserObserver(IResultBack<UserInfo> resultBack) {
        this.resultBack = resultBack;
    }

    @Override
    public void onUserUpdate(UserInfo userInfo) {
        if (null != resultBack) resultBack.onResult(userInfo);
    }

    @Override
    public void onGroupUpdate(Group group) {
    }

    @Override
    public void onGroupUserInfoUpdate(GroupUserInfo groupUserInfo) {
    }
}
