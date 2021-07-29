/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.ui.widget

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.core.view.setPadding
import com.google.android.material.snackbar.BaseTransientBottomBar

/**
 * @author gusd
 * @Date 2021/07/05
 */
class ActionSnackBar private constructor(
    parentViewGroup: ViewGroup, content: View,
    contentViewCallback: com.google.android.material.snackbar.ContentViewCallback
) : BaseTransientBottomBar<ActionSnackBar>(parentViewGroup, content, contentViewCallback) {


    companion object {
        @SuppressLint("ResourceAsColor")
        fun make(parentViewGroup: ViewGroup, view: View): ActionSnackBar {
            val actionSnackBar = ActionSnackBar(parentViewGroup, view, CallbackImpl(view))
            with(view) {
                actionSnackBar.getView().setPadding(0)
                actionSnackBar.duration = LENGTH_INDEFINITE
            }
            return actionSnackBar
        }

        fun make(parentViewGroup: ViewGroup, @LayoutRes layoutId: Int): ActionSnackBar {
            return make(
                parentViewGroup,
                LayoutInflater.from(parentViewGroup.context)
                    .inflate(layoutId, parentViewGroup, false)
            )
        }
    }



    class CallbackImpl(val content: View) :
        com.google.android.material.snackbar.ContentViewCallback {

        override fun animateContentOut(delay: Int, duration: Int) {
            content.scaleY = 1f
            ViewCompat.animate(content)
                .scaleY(0f)
                .setDuration(duration.toLong())
                .startDelay = delay.toLong()
        }

        override fun animateContentIn(delay: Int, duration: Int) {
            content.scaleY = 0f
            ViewCompat.animate(content)
                .scaleY(1f)
                .setDuration(duration.toLong())
                .startDelay = delay.toLong()
        }

    }
}