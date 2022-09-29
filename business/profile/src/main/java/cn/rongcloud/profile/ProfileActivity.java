/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.profile;


import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseActivity;
import com.basis.utils.KToast;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.profile.dialog.UnregisterDialog;
import cn.rongcloud.profile.webview.ActCommentWeb;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    private UnregisterDialog unregisterDialog;

    public static void startActivity(Activity activity, int code) {
        if (code > 0) {
            activity.startActivityForResult(new Intent(activity, ProfileActivity.class), code);
        } else {
            activity.startActivity(new Intent(activity, ProfileActivity.class));
        }
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    public void init() {
        getWrapBar().setTitle("设置").work();
        getView(R.id.ll_item_first).setOnClickListener(this);
        getView(R.id.ll_item_second).setOnClickListener(this);
        getView(R.id.ll_item_third).setOnClickListener(this);
        getView(R.id.ll_item_fourth).setOnClickListener(this);
        getView(R.id.ll_item_fivth).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.ll_item_first == id) {
            ActCommentWeb.openCommentWeb(activity, ApiConfig.REGISTER, "注册条款");
        } else if (R.id.ll_item_second == id) {
            ActCommentWeb.openCommentWeb(activity, ApiConfig.PRIVACY, "隐私政策");
        } else if (R.id.ll_item_third == id) {
            showUnregisterDialog();
        } else if (R.id.ll_item_fourth == id) {
            VersionHelper.checkVersion(activity, true);
        } else if (R.id.ll_item_fivth == id) {
            UserManager.logout();
        }
    }

    public void showUnregisterDialog() {
        unregisterDialog = new UnregisterDialog(this, v -> {
            unregisterDialog.dismiss();
            OkApi.post(ProfileApi.RESIGN, null, new WrapperCallBack() {
                @Override
                public void onResult(Wrapper result) {
                    if (null != result && result.ok()) {
                        KToast.show("注销成功");
                        UserManager.logout();
                    }
                }
            });

        });
        unregisterDialog.show();
    }
}