/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.profile;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.basis.ui.BaseActivity;
import com.basis.utils.ImageLoader;
import com.basis.utils.Logger;
import com.basis.utils.RealPathFromUriUtils;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.interfaces.IWrapBar;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.feedback.RcUmEvent;
import cn.rongcloud.config.feedback.UmengHelper;
import cn.rongcloud.config.provider.user.Sex;
import cn.rongcloud.profile.dialog.UserInfoDialog;
import cn.rongcloud.profile.webview.ActCommentWeb;


public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private UserInfoDialog dialog;
    private boolean needModefy = false;

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
        getWrapBar().addOptionMenu("", R.drawable.ic_setting)
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
        tv_name.setText(UserManager.get().getUserName());
        String url = UserManager.get().getPortraitUrl();
        ImageLoader.loadUrl(iv_portrait, url, R.drawable.default_portrait, ImageLoader.Size.S_250);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_portrait == id) {
            showEditeInfoDialog();
        } else if (R.id.ad_first == id) {
            UmengHelper.get().event(RcUmEvent.SettingBanner);
            ActCommentWeb.openCommentWeb(this, "https://m.rongcloud.cn/activity/rtc20", "套餐方案");//banner
        } else if (R.id.ad_second == id) {
            UmengHelper.get().event(RcUmEvent.SettingPackage);
            ActCommentWeb.openCommentWeb(this, "https://m.rongcloud.cn/activity/rtc20", "套餐方案");
        } else if (R.id.ad_third == id) {
            UmengHelper.get().event(RcUmEvent.SettingDemoDownload);
            ActCommentWeb.openCommentWeb(this, "https://m.rongcloud.cn/downloads/demo", "Demo 下载");
        } else if (R.id.ad_fourth == id) {
            UmengHelper.get().event(RcUmEvent.SettingCS);
            ActCommentWeb.openCommentWeb(this, "https://m.rongcloud.cn/cs", "在线客服");
        } else if (R.id.ad_fivth == id) {
            UmengHelper.get().event(RcUmEvent.SettingAboutUs);
            ActCommentWeb.openCommentWeb(this, "https://m.rongcloud.cn/about", "关于我们");
        } else if (R.id.customer_dial == id) {
            UmengHelper.get().event(RcUmEvent.SettingCallCM);
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + ApiConfig.CUSTOMER_PHONE);
            intent.setData(data);
            startActivity(intent);
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
        dialog = new UserInfoDialog(activity, new UserInfoDialog.OnUserListener() {
            @Override
            public void onSave(String userName, Uri portrait) {
                dialog.dismiss();
                Logger.e("userName = " + userName);
                Logger.e("portrait = " + portrait);
                if (null != portrait) {
                    String path = RealPathFromUriUtils.getRealPathFromUri(activity, portrait);
                    ProfileApi.uploadFile(path, new IResultBack<String>() {

                        @Override
                        public void onResult(String url) {
                            modifyUserInfo(userName, url);
                        }
                    });
                } else {
                    modifyUserInfo(userName, "");
                }
            }

            @Override
            public void onSelected() {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 10000);
            }
        });
        dialog.show();
    }


    void modifyUserInfo(String userName, String portraitUrl) {
        ProfileApi.updateUserInfo(userName, portraitUrl, Sex.unknown, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                if (aBoolean) {
                    UIKit.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != dialog) dialog.dismiss();
                            tv_name.setText(UserManager.get().getUserName());
                            String url = UserManager.get().getPortraitUrl();
                            ImageLoader.loadUrl(iv_portrait, url, R.drawable.default_portrait, ImageLoader.Size.S_250);
                        }
                    });
                }
            }
        });
    }
}