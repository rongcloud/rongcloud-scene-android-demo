/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.utils

import android.content.Context

/**
 * @author gusd
 * @Date 2021/06/09
 */
object UiUtils {
    fun dp2Px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density //当前屏幕密度因子
        return (dp * scale + 0.5f).toInt()
    }

    fun px2Dp(context: Context, px: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (px / scale + 0.5f).toInt()
    }
}