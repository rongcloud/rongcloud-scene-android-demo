/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroom.utils

import android.net.Uri
import android.util.LruCache
import cn.rongcloud.voiceroom.net.VoiceRoomNetManager
import cn.rongcloud.voiceroom.net.bean.respond.Member
import cn.rongcloud.voiceroom.net.bean.respond.UserIdList
import com.rongcloud.common.extension.getCompletePortraitUrl
import com.rongcloud.common.net.ApiConstant
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.disposables.DisposableHelper
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.rong.imkit.userinfo.RongUserInfoManager
import io.rong.imlib.model.UserInfo
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * @author gusd
 * @Date 2021/07/02
 */
object LocalUserInfoManager : AtomicReference<Disposable>(), Disposable {

    private val cache: LruCache<String, Member> by lazy {
        LruCache(2000)
    }

    private val memberIdInfoSubject = PublishSubject.create<MutableList<String>>()

    private val scheduler = Schedulers.io()

    private var mDisposable: Disposable? = null

    private val userList: MutableList<String> = arrayListOf()

    init {
        RongUserInfoManager.getInstance()
            .setUserInfoProvider({ userId ->
                val member = getMemberByUserId(userId)
                if (member != null) {
                    return@setUserInfoProvider memberToUserInfo(member)
                } else {
                    refreshUserInfo(userId)
                    return@setUserInfoProvider null
                }
            }, true)

        memberIdInfoSubject
            .observeOn(scheduler)
            .debounce(5, TimeUnit.MILLISECONDS)
            .flatMapSingle {
                val list = ArrayList<String>(it)
                it.clear()
                return@flatMapSingle VoiceRoomNetManager.aRoomApi.getUserInfoList(UserIdList(list))
            }
            .subscribe { bean ->
                bean.data?.forEach {
                    cache.put(it.userId, it)
                    refreshIMUserInfo(it)
                }
            }
    }

    fun getMemberByUserId(userId: String?): Member? {
        if (userId.isNullOrEmpty()) {
            return null
        }
        return cache.get(userId)
    }

    fun addUserInfoToCache(member: Member) {
        cache.put(member.userId, member)
    }

    private fun refreshIMUserInfo(member: Member) {
        RongUserInfoManager.getInstance().refreshUserInfoCache(
            memberToUserInfo(member)
        )
    }

    private fun memberToUserInfo(member: Member): UserInfo = UserInfo(
        member.userId,
        member.userName,
        Uri.parse(
            member.portrait?.getCompletePortraitUrl() ?: ApiConstant.DEFAULT_PORTRAIT_ULR
        )
    )

    private fun refreshUserInfo(userId: String) {
        cache[userId]?.let {
            refreshIMUserInfo(it)
        } ?: run {
            DisposableHelper.replace(this, scheduler.scheduleDirect {
                userList.add(userId)
                memberIdInfoSubject.onNext(userList)
            }.apply {
                mDisposable = this
            })
        }
    }

    override fun dispose() {
        mDisposable?.dispose()
    }

    override fun isDisposed(): Boolean {
        return mDisposable?.isDisposed ?: true
    }


}