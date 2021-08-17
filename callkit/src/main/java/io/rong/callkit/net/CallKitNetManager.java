/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package io.rong.callkit.net;

import com.rongcloud.common.net.RetrofitManager;

/**
 * @author gusd
 * @Date 2021/08/02
 */
public class CallKitNetManager {
    private static final String TAG = "CallKitNetManager";

    private volatile static CallKitNetManager INSTANCE = null;
    private final CallKitApiService mCallKitApiService;

    private CallKitNetManager() {
        mCallKitApiService = RetrofitManager.INSTANCE.getRetrofit().create(CallKitApiService.class);
    }

    public static CallKitNetManager getInstance() {
        if (INSTANCE == null) {
            synchronized (CallKitNetManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CallKitNetManager();
                }
            }
        }
        return INSTANCE;
    }

    public CallKitApiService getCallKitApiService() {
        return mCallKitApiService;
    }
}
