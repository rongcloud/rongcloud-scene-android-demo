/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.net

import cn.rongcloud.voiceroomdemo.common.AccountStore
import cn.rongcloud.voiceroomdemo.net.api.ApiConstant
import cn.rongcloud.voiceroomdemo.net.api.CommonApiService
import cn.rongcloud.voiceroomdemo.net.api.DownloadFileApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author gusd
 * @Date 2021/06/07
 */
private const val TAG = "RetrofitManager"

object RetrofitManager {
    val commonService: CommonApiService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        getRetrofit().create(CommonApiService::class.java)
    }

    val downloadService: DownloadFileApiService by lazy {
        getDownloadRetrofit().create(DownloadFileApiService::class.java)
    }

    private fun getDownloadRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("http://127.0.0.1").client(getOkHttpClient())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()).build()
    }

    private fun getRetrofit(): Retrofit {
        // 获取retrofit的实例

        return Retrofit.Builder()
            .baseUrl(ApiConstant.BASE_URL)  //自己配置
            .client(getOkHttpClient())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    private fun getOkHttpClient(): OkHttpClient {
        //添加一个log拦截器,打印所有的log
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        //可以设置请求过滤的水平,body,basic,headers
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(addHeaderInterceptor()) // token过滤
            .addInterceptor(httpLoggingInterceptor) //日志,所有的请求响应度看到
            .connectTimeout(10L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(60L, TimeUnit.SECONDS)
            .build()
    }


    /**
     * 设置头
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .method(originalRequest.method(), originalRequest.body())
            var authorization = AccountStore.getAuthorization()
            if (!authorization.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", authorization)
            }
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }


}