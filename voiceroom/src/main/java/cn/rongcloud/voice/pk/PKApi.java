package cn.rongcloud.voice.pk;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.kit.cache.GsonUtil;
import com.kit.utils.Logger;
import com.kit.wapper.IResultBack;

import java.util.HashMap;
import java.util.Map;

import cn.rong.combusis.api.VRApi;
import cn.rongcloud.voice.pk.domain.PKResult;

public class PKApi {
    private final static String TAG = "PKApi";

    /**
     * 上报pk开始
     *
     * @param roomId     当前room id
     * @param toRoomId   pk room id
     * @param resultBack 结果回调
     */
    static void reportPKStart(String roomId, String toRoomId, IResultBack<Boolean> resultBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("roomId", roomId);
        params.put("toRoomId", toRoomId);
        params.put("status", 0);
        OkApi.post(VRApi.PK_STATE, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Logger.e(TAG, "reportPKState:" + GsonUtil.obj2Json(result));
                if (null != resultBack && result.ok()) {
                    resultBack.onResult(true);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                resultBack.onResult(false);
            }
        });
    }

    /**
     * 上报pk结束
     *
     * @param roomId     当前room id
     * @param toRoomId   pk room id
     * @param resultBack 结果回调
     */
    static void reportPKEnd(String roomId, String toRoomId, IResultBack<Boolean> resultBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("roomId", roomId);
        params.put("toRoomId", toRoomId);
        params.put("status", 2);
        OkApi.post(VRApi.PK_STATE, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (null != resultBack) {
                    resultBack.onResult(result.ok());
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(false);
            }
        });
    }

    /**
     * 获取pk信息
     *
     * @param roomId     房间id
     * @param resultBack 结果回调
     */
    static void getPKInfo(String roomId, IResultBack<PKResult> resultBack) {
        OkApi.get(VRApi.getPKInfo(roomId), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Logger.e(TAG, "result:" + GsonUtil.obj2Json(result));
                if (null != result && result.ok()) {
                    PKResult pkResult = result.get(PKResult.class);
                    if (null != resultBack) resultBack.onResult(pkResult);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(null);
            }
        });
    }

    /**
     * 获取PK 状态
     *
     * @param roomId     房间id
     * @param resultBack 结果回调
     */
    static void isPkState(String roomId, IResultBack<PKResult> resultBack) {
        OkApi.get(VRApi.isPkState(roomId), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Logger.e(TAG, "result:" + GsonUtil.obj2Json(result));
                if (null != result && result.ok()) {
                    PKResult pkResult = result.get(PKResult.class);
                    if (null != resultBack) resultBack.onResult(pkResult);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult(null);
            }
        });
    }
}
