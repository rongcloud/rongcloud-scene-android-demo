/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.utils

import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import cn.rong.combusis.R
import com.bumptech.glide.Glide
import com.rongcloud.common.extension.getCompletePortraitUrl
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * @author gusd
 * @Date 2021/06/08
 */
@ExperimentalCoroutinesApi
object ImageLoaderUtil {
    fun loadImage(
        context: Context,
        imageView: ImageView,
        url: String?,
        @DrawableRes resId: Int = 0
    ) {
        if (url.isNullOrEmpty()) {
            Glide.with(context).load(resId).into(imageView)
        } else {
            Glide.with(context).load(url).error(resId).placeholder(resId).centerCrop()
                .into(imageView)
        }
    }

    fun loadPortraitDef(
        context: Context,
        imageView: ImageView,
        url: String
    ) {
        loadPortrait(context, imageView, url)
    }

    fun loadPortrait(
        context: Context,
        imageView: ImageView,
        url: String,
        size: Point = Point(250, 250)
    ) {
        val realUrl = url.getCompletePortraitUrl() ?: ""
        Glide.with(context).load(realUrl).override(size.x, size.y)
            .error(R.drawable.default_portrait)
            .placeholder(R.drawable.default_portrait).centerCrop().into(imageView)
    }

    fun loadImage(context: Context, imageView: ImageView, @DrawableRes drawableId: Int) {
        Glide.with(context).load(drawableId).into(imageView)
    }

    fun loadLocalImage(context: Context, imageView: ImageView, localPath: String) {
        Glide.with(context).load(localPath).centerCrop().into(imageView)
    }

    fun loadLocalImage(context: Context, imageView: ImageView, uri: Uri) {
        Glide.with(context).load(uri).centerCrop().into(imageView)
    }

    fun loadImage(context: Context, imageView: ImageView, uri: Uri, resId: Int) {
        Glide.with(context).load(uri).placeholder(resId).error(resId).centerCrop().into(imageView)
    }


}