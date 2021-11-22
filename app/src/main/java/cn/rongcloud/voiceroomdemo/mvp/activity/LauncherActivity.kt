/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity

import android.os.Bundle
import cn.rong.combusis.common.base.PermissionActivity
import cn.rongcloud.voiceroomdemo.R
import com.rongcloud.common.extension.setAndroidNativeLightStatusBar
import com.rongcloud.common.extension.showToast
import com.rongcloud.common.utils.AccountStore


class LauncherActivity : PermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndroidNativeLightStatusBar(true)
        setContentView(R.layout.activity_launcher)
    }

    override fun onSetPermissions(): Array<String>? {
        // 移到主界面申请
//        return LAUNCHER_PERMISSIONS
        return null
    }

    override fun onAccept(accept: Boolean) {
        if (accept) {
            turnToActivity()
        } else {
            showToast("请赋予必要权限！")
        }
    }

    private fun turnToActivity() {
        if (AccountStore.getImToken().isNullOrBlank()) {
            LoginActivity.startActivity(this)
            finish()
        } else {
            HomeActivity.startActivity(this)
            finish()
        }
    }
}