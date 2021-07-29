/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.fragment.voiceroom.roomsetting.setting.functionmodel

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * @author gusd
 * @Date 2021/06/22
 */
abstract class BaseFunctionModel {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val dataChangeSubject = BehaviorSubject.create<FunctionDataModel>()
    abstract fun onCreate()

    fun addDisposable(vararg disposable: Disposable) {
        compositeDisposable.addAll(*disposable)
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }

    protected fun onDataChange(image: Int, text: String, clickListener: () -> Unit) {
        dataChangeSubject.onNext(FunctionDataModel(image, text, clickListener))
    }

    fun setChangeListener(listener: (image: Int, text: String, clickListener: () -> Unit) -> Unit) {
        addDisposable(dataChangeSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listener.invoke(it.image, it.text, it.clickListener)
            })

    }

    internal class FunctionDataModel(
        val image: Int,
        val text: String,
        val clickListener: () -> Unit
    )
}

