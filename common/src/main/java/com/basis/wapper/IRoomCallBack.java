package com.basis.wapper;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/2/16
 * @time 10:42 上午
 */
public interface IRoomCallBack {
    void onSuccess();

    void onError(int code, String message);
}
