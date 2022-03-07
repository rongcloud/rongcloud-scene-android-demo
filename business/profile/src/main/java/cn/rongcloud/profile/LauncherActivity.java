/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.profile;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.basis.ui.BaseActivity;
import com.basis.utils.GsonUtil;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import java.util.Arrays;
import java.util.List;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.config.router.RouterPath;

public class LauncherActivity extends BaseActivity {
    final static int DELAY = 500;

    @Override
    public int setLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    public void init() {
        long start = System.currentTimeMillis();
        getWrapBar().setHide(true).work();
        User user = UserManager.get();
        if (null != user && !TextUtils.isEmpty(user.getImToken())) {
            ((UserProvider) UserProvider.provider())
                    .getFromService(Arrays.asList(user.getUserId()), new IResultBack<List<User>>() {
                        @Override
                        public void onResult(List<User> users) {
                            if (null != users && !users.isEmpty()) {
                                User u = users.get(0);
                                Logger.e("LauncherActivity", "update user info: = " + GsonUtil.obj2Json(u));
                                user.setUserName(u.getUserName());
                                user.setPortrait(u.getPortrait());
                                UserManager.save(user);
                            }
                            jump(RouterPath.ROUTER_MAIN, start);
                        }
                    });
        } else {
            jump(RouterPath.ROUTER_LOGIN, start);
        }
    }

    void jump(String routerPath, long start) {
        long dv = DELAY - (System.currentTimeMillis() - start);
        if (dv < 0) dv = 0;
        UIKit.postDelayed(new Runnable() {
            @Override
            public void run() {
                ARouter.getInstance().build(routerPath).navigation();
                finish();
            }
        }, dv);
    }
}
