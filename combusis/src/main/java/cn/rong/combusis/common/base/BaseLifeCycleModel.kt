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
 * @Date 2021/07/28
 */
abstract class BaseLifeCycleModel(val lifecycleOwner: LifecycleOwner) : BaseModel,
    BaseLifeCycleObserver, LifecycleObserver {
    val TAG: String = javaClass.simpleName
    @Inject
    public fun initLifecycle() {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun addDisposable(vararg disposable: Disposable) {
        compositeDisposable.addAll(*disposable)
    }

    override fun onCreate() {
        Log.d(TAG, "LifeCycle:onCreate")
    }

    override fun onDestroy() {
        Log.d(TAG, "LifeCycle:onDestroy")
        compositeDisposable.dispose()
    }

    override fun onStart() {
        Log.d(TAG, "LifeCycle:onStart")
    }

    override fun onStop() {
        Log.d(TAG, "LifeCycle:onStop")
    }

    override fun onResume() {
        Log.d(TAG, "LifeCycle:onResume")
    }

}