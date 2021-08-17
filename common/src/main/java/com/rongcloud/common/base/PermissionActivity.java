/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.base;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rongcloud.common.utils.PermissionUtil;

import org.jetbrains.annotations.NotNull;

import pub.devrel.easypermissions.AppSettingsDialog;


public abstract class PermissionActivity extends AppCompatActivity {

    protected final static String[] PERMISSIONS = new String[]{
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO,
            //音视需要频权限
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_PHONE_STATE,
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // chen permeissions
        String[] permissions = onSetPermissions();
        if (null != permissions && PermissionUtil.checkPermissions(this, permissions)) {
            onAccept(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.REQUEST_CODE == requestCode) {
            String[] d = PermissionUtil.getDeniedPermissions(this, permissions);
            if (d == null || 0 == d.length) {
                onAccept(true);
            } else {
                new AppSettingsDialog.Builder(this).build().show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //从设置页面返回，判断权限是否申请。
            String[] ps = onSetPermissions();
            if (ps == null) {
                return;
            }
            boolean accept = PermissionUtil.hasPermissions(this, ps);
            onAccept(accept);
        }
    }

    /**
     * 权限申请结果回调
     *
     * @param accept true：赋予所有权限  false 未全部授权
     */
    protected abstract void onAccept(@NonNull boolean accept);

    /**
     * 设置检查权限的数组
     */
    @Nullable
    protected abstract String[] onSetPermissions();
}