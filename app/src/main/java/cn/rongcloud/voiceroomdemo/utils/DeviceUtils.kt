/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.utils

import android.os.Build
import cn.rongcloud.voiceroomdemo.MyApp
import cn.rongcloud.voiceroomdemo.common.DataStoreKeys.DEVICE_ID_KEY
import cn.rongcloud.voiceroomdemo.common.getValueSync
import cn.rongcloud.voiceroomdemo.common.putValue
import java.util.*

/**
 * @author gusd
 * @Date 2021/06/07
 */
object DeviceUtils {

    fun getDeviceId(): String {
        var deviceId = MyApp.context.getValueSync(DEVICE_ID_KEY)
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
        MyApp.context.putValue(DEVICE_ID_KEY, deviceId)
        return deviceId

    }
}