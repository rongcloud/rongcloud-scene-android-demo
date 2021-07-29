/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * @author gusd
 * @Date 2021/06/22
 */
class CustomConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var visibilityListener: ((View, Int) -> Unit)? = null

    override fun setVisibility(visibility: Int) {
        visibilityListener?.invoke(this, visibility)
        super.setVisibility(visibility)
    }

    fun setVisibleChangeListener(listener: ((v: View, visibility: Int) -> Unit)?) {
        visibilityListener = listener
    }
}