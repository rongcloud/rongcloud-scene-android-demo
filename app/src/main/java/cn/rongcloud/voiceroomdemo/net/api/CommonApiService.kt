/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.net.api

import cn.rongcloud.mvoiceroom.net.bean.request.*
import cn.rongcloud.mvoiceroom.net.bean.respond.*
import cn.rongcloud.voiceroomdemo.net.api.bean.request.*
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.*
import com.rongcloud.common.net.bean.SimpleRespondBean
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * @author gusd
 * @Date 2021/06/07
 */
interface CommonApiService {

    @POST("/user/sendCode")
    fun getVerificationCode(@Body phoneNumber: GetVerificationCode): Single<VerificationCodeRespondBean>

    @POST("/user/login")
    fun login(@Body loginRequestBean: LoginRequestBean): Single<LoginRespondBean>


    @POST("/file/upload")
    @Multipart
    fun fileUpload(@Part body: MultipartBody.Part): Single<FileUploadRespond>

    @POST("/user/update")
    fun updateUserInfo(@Body updateUserInfoRequestBean: UpdateUserInfoRequestBean): Single<UpdateUserInfoRespond>

}