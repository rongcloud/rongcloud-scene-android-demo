/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rong.combusis.common.base;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kit.utils.PermissionUtil;
import com.kit.wapper.IResultBack;

import org.jetbrains.annotations.NotNull;

import pub.devrel.easypermissions.AppSettingsDialog;


public abstract class PermissionActivity extends AppCompatActivity {
    // 处理小米上线权限问题
//    protected final static String[] PERMISSIONS = new String[]{
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            android.Manifest.permission.READ_EXTERNAL_STORAGE,
//            android.Manifest.permission.RECORD_AUDIO,
//            //音视需要频权限
//            Manifest.permission.CAMERA,
//            Manifest.permission.INTERNET,
//            Manifest.permission.MODIFY_AUDIO_SETTINGS,
//            Manifest.permission.BLUETOOTH,
//            Manifest.permission.BLUETOOTH_ADMIN,
//            Manifest.permission.READ_PHONE_STATE,
//    };
    // 启动页权限
    protected final static String[] LAUNCHER_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    protected final static String[] VOICE_PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
    };

    protected final static String[] CALL_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // chen permeissions
        String[] permissions = onSetPermissions();
        checkAndRequestPermissions(permissions, null);

    }

    private IResultBack resultBack;


    /**
     * 检查并申请权限
     *
     * @param permissions 权限数组
     * @param resultBack  结果回调,若为null会回调onAccept
     */
    protected void checkAndRequestPermissions(String[] permissions, IResultBack<Boolean> resultBack) {
        this.resultBack = resultBack;
        if (null == permissions || PermissionUtil.checkPermissions(this, permissions)) {
            handlePermissionResult(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.REQUEST_CODE == requestCode) {
            String[] d = PermissionUtil.getDeniedPermissions(this, permissions);
            if (d == null || 0 == d.length) {
                handlePermissionResult(true);
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
            handlePermissionResult(accept);
        }
    }

    /**
     * 处理权限申请结果
     *
     * @param accept true：赋予所有权限  false 未全部授权
     */
    protected void handlePermissionResult(@NonNull boolean accept) {
        // 优先处理resultBack
        // 1.系统调用 checkAndRequestPermissions() 会执行onAccept回调
        // 2.用户调用，会执行resultBack
        if (null != resultBack) {
            resultBack.onResult(accept);
        } else {
            onAccept(accept);
        }
    }

    protected abstract void onAccept(@NonNull boolean accept);

    /**
     * 设置检查权限的数组
     */
    @Nullable
    protected abstract String[] onSetPermissions();
}