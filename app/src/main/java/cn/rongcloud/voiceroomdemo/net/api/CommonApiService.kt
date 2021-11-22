/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.net.api

import cn.rongcloud.voiceroomdemo.net.api.bean.request.GetVerificationCode
import cn.rongcloud.voiceroomdemo.net.api.bean.request.LoginRequestBean
import cn.rongcloud.voiceroomdemo.net.api.bean.request.UpdateUserInfoRequestBean
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.LoginRespondBean
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.UpdateUserInfoRespond
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VerificationCodeRespondBean
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author gusd
 * @Date 2021/06/07
 */
interface CommonApiService {

    @POST("/user/sendCode")
    fun getVerificationCode(@Body phoneNumber: GetVerificationCode): Single<VerificationCodeRespondBean>

    @POST("/user/login")
    fun login(@Body loginRequestBean: LoginRequestBean): Single<LoginRespondBean>

    @POST("/user/update")
    fun updateUserInfo(@Body updateUserInfoRequestBean: UpdateUserInfoRequestBean): Single<UpdateUserInfoRespond>

}