/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity;


import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.BaseActivity;
import com.kit.utils.KToast;
import com.rongcloud.common.utils.AccountStore;

import cn.rong.combusis.api.VRApi;
import cn.rongcloud.voiceroomdemo.R;
import cn.rongcloud.voiceroomdemo.ui.dialog.UnregisterDialog;
import cn.rongcloud.voiceroomdemo.ui.dialog.UserInfoDialog;
import cn.rongcloud.voiceroomdemo.webview.ActCommentWeb;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    private UserInfoDialog dialog;
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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_item_first:
                ActCommentWeb.openCommentWeb(activity, "file:///android_asset/agreement_zh.html", "注册条款");
                break;
            case R.id.ll_item_second:
                ActCommentWeb.openCommentWeb(activity, "file:///android_asset/privacy_zh.html", "隐私政策");
                break;
            case R.id.ll_item_third:
                showUnregisterDialog();
                break;
            case R.id.ll_item_fourth:
                AccountStore.INSTANCE.logout();
                break;
        }
    }

    public void showUnregisterDialog() {
        unregisterDialog = new UnregisterDialog(this, v -> {
            unregisterDialog.dismiss();
            OkApi.post(VRApi.RESIGN, null, new WrapperCallBack() {
                @Override
                public void onResult(Wrapper result) {
                    if (null != result && result.ok()) {
                        KToast.show("注销成功");
                        AccountStore.INSTANCE.logout();
                    }
                }
            });

        });
        unregisterDialog.show();

    }
}