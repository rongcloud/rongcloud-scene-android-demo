package cn.rong.combusis.sdk.event.listener;

public interface LeaveRoomCallBack {
    void onSuccess();

    void onError(int code, String message);
}
