/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.extension

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.rongcloud.common.ModuleManager
import com.rongcloud.common.R
import com.rongcloud.common.net.ApiConstant
import com.rongcloud.common.utils.ImageLoaderUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * @author gusd
 * @Date 2021/06/04
 */
fun getString(@StringRes stringId: Int): String {
    return ModuleManager.applicationContext.getString(stringId)
}


fun Context.showToast(message: String?) {
    ui {
        message?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}


fun Context.showToast(@StringRes messageRes: Int) {
    showToast(getString(messageRes))
}

fun AppCompatActivity.setAndroidNativeLightStatusBar(isDark: Boolean) {
    val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
    windowInsetsController?.isAppearanceLightStatusBars = isDark
}


fun ImageView.loadImageView(url: String, @DrawableRes defaultImage: Int = 0) {
    ImageLoaderUtil.loadImage(context, this, url, defaultImage)
}

fun ImageView.loadImageView(uri: Uri, @DrawableRes defaultImage: Int = 0) {
    ImageLoaderUtil.loadImage(context, this, uri, defaultImage)
}

fun ImageView.loadPortrait(url: String?) {
    if (url.isNullOrEmpty()) {
        this.setImageResource(R.drawable.default_portrait)
    } else {
        ImageLoaderUtil.loadPortrait(context, this, url)
    }
}

fun ImageView.loadLocalPortrait(url: String?) {
    if (url.isNullOrEmpty()) {
        this.setImageResource(R.drawable.default_portrait)
    } else {
        ImageLoaderUtil.loadLocalImage(context, this, url)
    }
}

fun ImageView.loadLocalPortrait(uri: Uri?) {
    if (uri == null) {
        this.setImageResource(R.drawable.default_portrait)
    } else {
        ImageLoaderUtil.loadLocalImage(context, this, uri)
    }
}

fun ImageView.loadPortrait(@DrawableRes drawableId: Int) {
    ImageLoaderUtil.loadImage(context, this, drawableId)
}


fun <T> T.ui(action: () -> Unit) {

    // Fragment
    if (this is Fragment) {
        val fragment = this
        if (!fragment.isAdded) return

        val activity = fragment.activity ?: return
        if (activity.isFinishing) return

        activity.runOnUiThread(action)
        return
    }

    // Activity
    if (this is Activity) {
        if (this.isFinishing) return
        this.runOnUiThread(action)
        return
    }

    // 主线程
    if (Looper.getMainLooper() === Looper.myLooper()) {
        action()
        return
    }

    // 子线程，使用handler
    KitUtil.handler.post { action() }
}

object KitUtil {
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
}


@OptIn(ExperimentalContracts::class)
fun CharSequence?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }
    return this != null && this.isNotEmpty()
}

@OptIn(ExperimentalContracts::class)
fun CharSequence?.isNotNullOrBlank(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrBlank != null)
    }
    return this != null && this.isNotBlank()
}

fun View.setAvoidFastClickListener(
    windowDuration: Long = 500,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    listener: (view: View) -> Unit
): Disposable {
    return Observable.create<View> { emitter ->
        setOnClickListener {
            if (!emitter.isDisposed) {
                emitter.onNext(it)
            }
        }
    }.throttleFirst(windowDuration, unit).subscribe { listener(it) }
}

fun String.getCompletePortraitUrl(): String? {
    if (this.isNotNullOrEmpty()) {
        if (!this.startsWith("http") && !this.startsWith("file", true)) {
            return "${ApiConstant.FILE_URL}$this"
        }
    }
    return null
}
