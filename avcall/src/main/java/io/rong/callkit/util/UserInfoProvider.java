/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package io.rong.callkit.util;


import com.rongcloud.common.dao.database.DatabaseManager;
import com.rongcloud.common.net.ApiConstant;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.rong.callkit.net.CallKitNetManager;
import io.rong.callkit.net.model.UserInfoModel;

/**
 * @author gusd
 * @Date 2021/08/02
 */
public class UserInfoProvider {
    private static final String TAG = "UserInfoProvider";

    private volatile static UserInfoProvider INSTANCE = null;

    private UserInfoProvider() {

    }

    public static UserInfoProvider getInstance() {
        if (INSTANCE == null) {
            synchronized (UserInfoProvider.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserInfoProvider();
                }
            }
        }
        return INSTANCE;
    }

    public Single<UserInfoModel> getUserInfoByPhoneNumber(String phoneNumber) {
        return CallKitNetManager.getInstance()
                .getCallKitApiService()
                .queryUserInfoByNumber(phoneNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(new Consumer<UserInfoModel>() {
                    @Override
                    public void accept(UserInfoModel userInfoModel) throws Throwable {
                        if (userInfoModel.getCode() == ApiConstant.REQUEST_SUCCESS_CODE) {
                            UserInfoModel.UserInfo userInfo = userInfoModel.getData();
                            if (userInfo != null) {
                                DatabaseManager.INSTANCE.addOrUpdateUserInfo(userInfo.getUid(), userInfo.getName(), ApiConstant.INSTANCE.getFILE_URL() + userInfo.getPortrait(), userInfo.getMobile());
                            }
                        }
                    }
                });
    }


}
