package cn.rong.combusis.sdk;

import android.text.TextUtils;
import android.util.Log;

import com.kit.utils.KToast;
import com.kit.wapper.IResultBack;

import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;

public class DefaultRoomCallback implements RCVoiceRoomCallback {
    private final static String TAG = "DefaultRoomCallback";
    private IResultBack<Boolean> resultBack;
    private String methodName;
    private String action;

    protected DefaultRoomCallback(String methodName, IResultBack<Boolean> resultBack) {
        this(methodName, "", resultBack);
    }

    protected DefaultRoomCallback(String methodName, String action, IResultBack<Boolean> resultBack) {
        this.resultBack = resultBack;
        this.action = action;
        this.methodName = TextUtils.isEmpty(methodName) ? "DefauRoomCallback" : methodName;
    }

    @Override
    public void onSuccess() {
        if (null != resultBack) resultBack.onResult(true);
        if (!TextUtils.isEmpty(action)) KToast.show(action + "成功");
    }

    @Override
    public void onError(int i, String s) {
        if (!TextUtils.isEmpty(action)) KToast.show(action + "失败");
        Log.e(TAG, methodName + "#onError [" + i + "]:" + s);
        if (null != resultBack) resultBack.onResult(false);
    }
}