/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.utils

import android.os.Build
import com.rongcloud.common.ModuleManager
import com.rongcloud.common.extension.getValueSync
import com.rongcloud.common.extension.myStringPreferencesKey
import com.rongcloud.common.extension.putValue
import java.util.*

/**
 * @author gusd
 * @Date 2021/06/07
 */
object DeviceUtils {
    val DEVICE_ID_KEY by myStringPreferencesKey("")

    fun getDeviceId(): String {
        var deviceId = ModuleManager.applicationContext.getValueSync(DEVICE_ID_KEY)
        if (deviceId.isNotBlank()) {
            return deviceId
        }
        val deviceIdShort =
            "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10
        var serial = try {
            UUID(
                deviceIdShort.hashCode().toLong(),
                Build::class.java.getField("SERIAL")[null].toString().hashCode().toLong()
            ).toString()
        } catch (exception: Exception) {
            "serial"
        }
        deviceId = UUID(deviceIdShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        ModuleManager.applicationContext.putValue(DEVICE_ID_KEY, deviceId)
        return deviceId

    }
}