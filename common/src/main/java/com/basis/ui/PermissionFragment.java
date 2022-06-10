package com.basis.ui;

import android.Manifest;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.basis.utils.PermissionUtil;
import com.basis.wapper.IResultBack;

import pub.devrel.easypermissions.AppSettingsDialog;

/**
 * @author: BaiCQ
 * @createTime: 2017/1/13 11:38
 * @className: PermissionActivity
 * @Description: 权限申请基类
 */
public abstract class PermissionFragment extends BaseFragment {

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
    public void init() {
        // chen permeissions
        String[] permissions = onCheckPermission();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.REQUEST_CODE == requestCode) {
            String[] d = PermissionUtil.getDeniedPermissions(activity, permissions);
            if (d == null || 0 == d.length) {
                handlePermissionResult(true);
            } else {
                new AppSettingsDialog.Builder(this).build().show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //从设置页面返回，判断权限是否申请。
            String[] ps = onCheckPermission();
            if (ps == null) {
                return;
            }
            boolean accept = PermissionUtil.hasPermissions(activity, ps);
            handlePermissionResult(accept);
        }
    }

    /**
     * 处理权限申请结果
     *
     * @param accept true：赋予所有权限  false 未全部授权
     */
    protected void handlePermissionResult(boolean accept) {
        // 优先处理resultBack
        // 1.系统调用 checkAndRequestPermissions() 会执行onAccept回调
        // 2.用户调用，会执行resultBack
        if (null != resultBack) {
            resultBack.onResult(accept);
            resultBack = null;
        } else {
            onAccept(accept);
        }
    }

    /**
     * 设置检测权限的数组
     *
     * @return
     */
    protected String[] onCheckPermission() {
        return new String[0];
    }

    /**
     * 权限检测结果
     *
     * @param accept
     */
    protected abstract void onAccept(boolean accept);
}