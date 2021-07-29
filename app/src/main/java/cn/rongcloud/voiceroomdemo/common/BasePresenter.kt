/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.common

/**
 * @author gusd
 * @Date 2021/06/04
 */
abstract class BasePresenter<T:IBaseView> {

    abstract fun onCreate()
    abstract fun onDestroy()
}