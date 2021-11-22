/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package io.rong.callkit.net;

import io.reactivex.rxjava3.core.Single;
import io.rong.callkit.net.model.UserInfoModel;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author gusd
 * @Date 2021/08/02
 */
public interface CallKitApiService {

    @GET("/user/get/{phoneNumber}")
    public Single<UserInfoModel> queryUserInfoByNumber(@Path("phoneNumber") String phoneNumber);
}
