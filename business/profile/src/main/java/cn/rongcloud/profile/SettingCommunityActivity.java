/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.profile;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.basis.ui.BaseActivity;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.RealPathFromUriUtils;
import com.basis.utils.ResUtil;
import com.basis.utils.SoftBoardUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.interfaces.IWrapBar;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.feedback.RcUmEvent;
import cn.rongcloud.config.feedback.UmengHelper;
import cn.rongcloud.config.provider.user.Sex;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.profile.dialog.UserInfoDialog;
import cn.rongcloud.profile.webview.ActCommentWeb;


public class SettingCommunityActivity extends BaseActivity implements View.OnClickListener {
    private boolean needModefy = false;

    public static void startActivity(Activity activity, int code) {
        if (code > 0) {
            activity.startActivityForResult(new Intent(activity, SettingCommunityActivity.class), code);
        } else {
            activity.startActivity(new Intent(activity, SettingCommunityActivity.class));
        }
    }

    @Override
    public void onBackCode() {
        if (needModefy) setResult(Activity.RESULT_OK);
        super.onBackCode();
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_setting_commuity;
    }

    private ImageView iv_portrait;
    private EditText et_name;
    private View vMan, vWoman;

    @Override
    public void init() {
        getWrapBar().setElevation(IWrapBar.DEFAULT_ELEVATION).setTitle(R.string.profile_me_setting).addOptionMenu(ResUtil.getString(R.string.profile_save))
                .setOnMenuSelectedListener(new IWrapBar.OnMenuSelectedListener() {
                    @Override
                    public void onItemSelected(int position) {
                        if (0 == position) {
                            updateInfo();
                        }
                    }
                }).work();
        iv_portrait = findViewById(R.id.iv_portrait);
        et_name = findViewById(R.id.et_name);
        et_name.setEnabled(false);
        vMan = findViewById(R.id.cb_man);
        vWoman = findViewById(R.id.cb_woman);
        findViewById(R.id.btn_portrait).setOnClickListener(this);
        findViewById(R.id.iv_editor).setOnClickListener(this);
        vWoman.setOnClickListener(this);
        vMan.setOnClickListener(this);
        initData();
    }

    public void initData() {
        User user = UserManager.get();
        if (null != user) {
            et_name.setText(user.getUserName());
            ImageLoader.loadUrl(iv_portrait, user.getPortraitUrl(), R.drawable.default_portrait, ImageLoader.Size.S_250);
            refreshSelectSex(Sex.man == user.getSex());
        }
    }

    private boolean isMan = true;

    private void refreshSelectSex(boolean man) {
        isMan = man;
        vMan.setSelected(isMan);
        vWoman.setSelected(!isMan);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_portrait == id) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 10000);
        } else if (R.id.iv_editor == id) {
            et_name.setEnabled(!et_name.isEnabled());
            if (et_name.isEnabled()) {
                SoftBoardUtil.showKeyboard(et_name);
                et_name.setSelection(et_name.getText().length());
            } else {
                SoftBoardUtil.hideKeyboard(et_name);
            }
        } else if (R.id.cb_man == id) {
            refreshSelectSex(true);
        } else if (R.id.cb_woman == id) {
            refreshSelectSex(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10000 && resultCode == Activity.RESULT_OK) {
            selectImageUrl = data.getData();
            if (null != selectImageUrl && null != iv_portrait) {
                ImageLoader.loadUri(iv_portrait, selectImageUrl, R.drawable.default_portrait, ImageLoader.Size.S_250);
            }
        }
    }

    private Uri selectImageUrl;

    void updateInfo() {
        String userName = et_name.getText().toString().trim();
        if (null != selectImageUrl) {
            String path = RealPathFromUriUtils.getRealPathFromUri(activity, selectImageUrl);
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

    void modifyUserInfo(String userName, String portraitUrl) {
        Sex sex = isMan ? Sex.man : Sex.woman;
        User user = UserManager.get();
        if (null != user &&
                TextUtils.isEmpty(portraitUrl)
                && TextUtils.equals(userName, user.getUserName())
                && sex == user.getSex()) {
            // 头像 昵称 性别都没有修改
            onBackCode();
            return;
        }
        ProfileApi.updateUserInfo(userName, portraitUrl, sex, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                if (aBoolean) {
                    KToast.show(R.string.profile_profile_update_success);
                    needModefy = true;
                    onBackCode();
                }
            }
        });
    }
}