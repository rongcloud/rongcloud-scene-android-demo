/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.base

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

/**
 * @author gusd
 * @Date 2021/06/04
 */
abstract class BaseLifeCyclePresenter(
    val lifecycleOwner: LifecycleOwner
) : BasePresenter(), LifecycleObserver, BaseLifeCycleObserver {

    @Inject
    public fun initLifecycle() {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate() {

    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }

    override fun onStart() {

    }

    override fun onStop() {

    }

    override fun onResume() {

    }

    fun addDisposable(vararg disposable: Disposable) {
        compositeDisposable.addAll(*disposable)
    }


}