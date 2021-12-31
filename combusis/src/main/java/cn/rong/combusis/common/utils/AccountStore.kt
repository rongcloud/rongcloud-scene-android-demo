/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package com.rongcloud.common.utils

import android.annotation.SuppressLint
import android.content.Context
import cn.rong.combusis.common.utils.JsonUtils
import cn.rong.combusis.provider.user.User
import com.rongcloud.common.ModuleManager
import com.rongcloud.common.extension.*
import com.rongcloud.common.model.AccountInfo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.rong.imkit.RongIM

/**
 * @author gusd
 * @Date 2021/06/08
 */

private const val TAG = "AccountStore"

@SuppressLint("StaticFieldLeak")
object AccountStore {

    private val EMPTY_ACCOUNT = AccountInfo()
    private val ACCOUNT_INFO by myStringPreferencesKey(EMPTY_ACCOUNT.toJson())

    private var currentInfo: AccountInfo

    private var context: Context = ModuleManager.applicationContext

    init {
        currentInfo = JsonUtils.fromJson(
            context.getValueSync(ACCOUNT_INFO),
            AccountInfo::class.java
        ) ?: EMPTY_ACCOUNT
    }

    fun saveAccountInfo(info: AccountInfo?) {
        currentInfo = info ?: EMPTY_ACCOUNT
        context.putValue(ACCOUNT_INFO, currentInfo.toJson())
    }

    fun getAccountInfo(): AccountInfo = currentInfo

    fun getPhone(): String = currentInfo.phone ?: ""


    fun getImToken() = getAccountInfo().imToken


    fun getUserName() = getAccountInfo().userName


    fun getAuthorization() = getAccountInfo().authorization


    fun getUserPortrait() = getAccountInfo().portrait

    fun getUserId() = getAccountInfo().userId

    // 登出监听
    fun obLogoutSubject(): Observable<Boolean> =
        context.obValue(ACCOUNT_INFO)
            .filter {
                it.isNullOrEmpty() || it == ACCOUNT_INFO.defaultValue
            }.map {
                return@map true
            }.observeOn(AndroidSchedulers.mainThread())

    // 登出
    fun logout() {
        saveAccountInfo(EMPTY_ACCOUNT)
        RongIM.getInstance().disconnect()
    }

    // 监听账号信息发生变化
    fun obAccountInfoChange(): Observable<AccountInfo> =
        context.obValue(ACCOUNT_INFO).map {
            return@map JsonUtils.fromJson(it, AccountInfo::class.java) ?: EMPTY_ACCOUNT
        }.observeOn(AndroidSchedulers.mainThread())

    fun toUser(): User {
        return User().apply {
            this.userId = getAccountInfo().userId
            this.userName = getAccountInfo().userName
            this.portrait = getAccountInfo().portrait
        }
    }
}