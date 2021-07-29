/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.common

import cn.rongcloud.voiceroomdemo.MyApp
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.AccountInfo
import cn.rongcloud.voiceroomdemo.utils.JsonUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

/**
 * @author gusd
 * @Date 2021/06/08
 */

private const val TAG = "AccountStore"

object AccountStore {

    private val EMPTY_ACCOUNT = AccountInfo()
    private val ACCOUNT_INFO by myStringPreferencesKey(EMPTY_ACCOUNT.toJson())

    private var currentInfo: AccountInfo

    init {
        currentInfo = JsonUtils.fromJson(
            MyApp.context.getValueSync(ACCOUNT_INFO),
            AccountInfo::class.java
        ) ?: EMPTY_ACCOUNT
    }


    fun saveAccountInfo(info: AccountInfo?) {
        currentInfo = info ?: EMPTY_ACCOUNT
        MyApp.context.putValue(ACCOUNT_INFO, currentInfo.toJson())
    }

    fun getAccountInfo(): AccountInfo = currentInfo


    fun getImToken() = getAccountInfo().imToken


    fun getUserName() = getAccountInfo().userName


    fun getAuthorization() = getAccountInfo().authorization


    fun getUserPortrait() = getAccountInfo().portrait

    fun getUserId() = getAccountInfo().userId

    // 登出监听
    fun obLogoutSubject(): Observable<Boolean> =
        MyApp.context.obValue(ACCOUNT_INFO)
            .filter {
                it.isNullOrEmpty() || it == ACCOUNT_INFO.defaultValue
            }.map {
                return@map true
            }.observeOn(AndroidSchedulers.mainThread())

    // 登出
    fun logout() {

        saveAccountInfo(EMPTY_ACCOUNT)
    }

    // 监听账号信息发生变化
    fun obAccountInfoChange(): Observable<AccountInfo> =
        MyApp.context.obValue(ACCOUNT_INFO).map {
            return@map JsonUtils.fromJson(it, AccountInfo::class.java) ?: EMPTY_ACCOUNT
        }.observeOn(AndroidSchedulers.mainThread())
}