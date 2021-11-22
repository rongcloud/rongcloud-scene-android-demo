package com.basis.ui;

import androidx.annotation.NonNull;

import com.kit.cache.GsonUtil;
import com.kit.utils.Logger;
import com.kit.utils.PermissionUtil;

/**
 * @author: BaiCQ
 * @createTime: 2017/1/13 11:38
 * @className: AbsPermissionActivity
 * @Description: 权限申请基类
 */
public abstract class AbsPermissionActivity extends BaseActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.REQUEST_CODE == requestCode) {
            String[] arr = PermissionUtil.getDeniedPermissions(this, permissions);
            Logger.e(TAG, "arr = " + GsonUtil.obj2Json(arr));
            boolean accept = null == arr || 0 == arr.length;
            onPermissionAccept(accept);
        }
    }

    @Override
    public final void init() {
        if (PermissionUtil.checkPermissions(this, onCheckPermission())) {
            onPermissionAccept(true);
        }
    }

    /**
     * 设置检测权限的数组
     *
     * @return
     */
    protected abstract String[] onCheckPermission();

    /**
     * 权限检测结果
     *
     * @param accept
     */
    protected abstract void onPermissionAccept(boolean accept);
}