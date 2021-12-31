/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.basis.ui.BaseActivity;
import com.basis.widget.interfaces.IWrapBar;
import com.rongcloud.common.utils.AccountStore;
import com.rongcloud.common.utils.ImageLoaderUtil;

import cn.rong.combusis.umeng.RcUmEvent;
import cn.rong.combusis.umeng.UmengHelper;
import cn.rongcloud.voiceroomdemo.R;
import cn.rongcloud.voiceroomdemo.mvp.activity.iview.ISettingView;
import cn.rongcloud.voiceroomdemo.mvp.presenter.SettingPresenter;
import cn.rongcloud.voiceroomdemo.ui.dialog.UnregisterDialog;
import cn.rongcloud.voiceroomdemo.ui.dialog.UserInfoDialog;
import cn.rongcloud.voiceroomdemo.webview.ActCommentWeb;
import kotlin.jvm.functions.Function0;


public class SettingActivity extends BaseActivity implements View.OnClickListener, ISettingView {
    private UserInfoDialog dialog;
    private boolean needModefy = false;
    private UnregisterDialog unregisterDialog;
    private SettingPresenter presenter;

    public static void startActivity(Activity activity, int code) {
        if (code > 0) {
            activity.startActivityForResult(new Intent(activity, SettingActivity.class), code);
        } else {
            activity.startActivity(new Intent(activity, SettingActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (needModefy) setResult(Activity.RESULT_OK);
        if (null != dialog) dialog.dismiss();
        super.onBackPressed();
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_setting;
    }

    private ImageView iv_portrait;
    private TextView tv_name;

    @Override
    public void init() {
        getWrapBar()
                .addOptionMenu("", R.drawable.ic_setting)
                .setOnMenuSelectedListener(new IWrapBar.OnMenuSelectedListener() {
                    @Override
                    public void onItemSelected(int position) {
                        if (0 == position) {
                            ProfileActivity.startActivity(activity, -1);
                        }
                    }
                }).work();
        iv_portrait = findViewById(R.id.iv_portrait);
        tv_name = findViewById(R.id.tv_name);
        iv_portrait.setOnClickListener(this);
        findViewById(R.id.ad_first).setOnClickListener(this);
        findViewById(R.id.ad_second).setOnClickListener(this);
        findViewById(R.id.ad_third).setOnClickListener(this);
        findViewById(R.id.ad_fourth).setOnClickListener(this);
        findViewById(R.id.ad_fivth).setOnClickListener(this);
        findViewById(R.id.customer_dial).setOnClickListener(this);
        initData();
    }

    public void initData() {
        presenter = new SettingPresenter(this, this, this);
        tv_name.setText(AccountStore.INSTANCE.getUserName());
        String url = AccountStore.INSTANCE.getUserPortrait();
        if (TextUtils.isEmpty(url)) {
            iv_portrait.setImageResource(R.drawable.default_portrait);
        } else {
            ImageLoaderUtil.INSTANCE.loadPortraitDef(SettingActivity.this, iv_portrait, url);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_portrait:
                showEditeInfoDialog();
                break;
            case R.id.ad_first:
                UmengHelper.get().event(RcUmEvent.SettingBanner);
                ActCommentWeb.openCommentWeb(this, "https://m.rongcloud.cn/activity/rtc20", "套餐方案");//banner
                break;
            case R.id.ad_second:
                UmengHelper.get().event(RcUmEvent.SettingPackage);
                ActCommentWeb.openCommentWeb(this, "https://m.rongcloud.cn/activity/rtc20", "套餐方案");
                break;
            case R.id.ad_third:
                UmengHelper.get().event(RcUmEvent.SettingDemoDownload);
                ActCommentWeb.openCommentWeb(this, "https://m.rongcloud.cn/downloads/demo", "Demo 下载");
                break;
            case R.id.ad_fourth:
                UmengHelper.get().event(RcUmEvent.SettingCS);
                ActCommentWeb.openCommentWeb(this, "https://m.rongcloud.cn/cs", "在线客服");
                break;
            case R.id.ad_fivth:
                UmengHelper.get().event(RcUmEvent.SettingAboutUs);
                ActCommentWeb.openCommentWeb(this, "https://m.rongcloud.cn/about", "关于我们");
                break;
            case R.id.customer_dial:
                UmengHelper.get().event(RcUmEvent.SettingCallCM);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:13161856839");
                intent.setData(data);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10000 && resultCode == Activity.RESULT_OK) {
            Uri selectImageUrl = data.getData();
            if (null != selectImageUrl && null != dialog) {
                dialog.setUserPortrait(selectImageUrl);
            }
        }
    }

    private void showEditeInfoDialog() {
        dialog = new UserInfoDialog(this, new Function0() {
            @Override
            public Object invoke() {
                dialog.dismiss();
                presenter.logout();
                return null;
            }
        }, (userName, uri) -> {
            dialog.dismiss();
            presenter.modifyUserInfo(userName, uri);
            return null;
        }, () -> {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 10000);
            return null;
        }, () -> {
            showUnregisterDialog();
            return null;
        });
        dialog.show();
    }

    public void showUnregisterDialog() {
        unregisterDialog = new UnregisterDialog(this, v -> {
            unregisterDialog.dismiss();
            presenter.unregister();
        });
        unregisterDialog.show();
    }

    @Override
    public void modifyInfoSuccess() {
        needModefy = true;
        if (null != dialog) {
            runOnUiThread(() -> {
                dialog.dismiss();
                tv_name.setText(AccountStore.INSTANCE.getUserName());
                String url = AccountStore.INSTANCE.getUserPortrait();
                ImageLoaderUtil.INSTANCE.loadPortraitDef(SettingActivity.this, iv_portrait, url);
            });
        }
    }
}