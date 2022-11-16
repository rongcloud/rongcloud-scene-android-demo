package cn.rongcloud.pk.api;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.GsonUtil;
import com.basis.utils.Logger;
import com.basis.wapper.IResultBack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.config.AppConfig;
import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.pk.bean.PKResult;

public class PKApi {
    private final static String TAG = "PKApi";
    private final static String HOST = AppConfig.get().getBaseServerAddress();

    /**
     * pk状态上报
     */
    private static final String PK_STATE = HOST + "mic/room/pk";
    private static final String PK_INFO = HOST + "mic/room/pk/detail/";

    /**
     * 获取pk积分排行
     *
     * @param roomId
     * @return
     */
    private static String getPKInfo(String roomId) {
        return PK_INFO + roomId;
    }

    // pk/{roomId}/isPk
    private static String isPkState(String roomId) {
        return HOST + "mic/room/pk/" + roomId + "/isPk";
    }

    /**
     * 在线房主
     */
    public static String ONLINE_CREATER = HOST + "mic/room/online/created/list/v1";

    /**
     * 上报pk开始
     *
     * @param roomId     当前room id
     * @param toRoomId   pk room id
     * @param resultBack 结果回调
     */
    public static void reportPKStart(String roomId, String toRoomId, IResultBack<Boolean> resultBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("roomId", roomId);
        params.put("toRoomId", toRoomId);
        params.put("status", 0);
        OkApi.post(PK_STATE, params, new WrapperCallBack() {
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
    public static void reportPKEnd(String roomId, String toRoomId, IResultBack<Boolean> resultBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("roomId", roomId);
        params.put("toRoomId", toRoomId);
        params.put("status", 2);
        OkApi.post(PK_STATE, params, new WrapperCallBack() {
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
    public static void getPKInfo(String roomId, IResultBack<PKResult> resultBack) {
        OkApi.get(getPKInfo(roomId), null, new WrapperCallBack() {
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
    public static void isPkState(String roomId, IResultBack<PKResult> resultBack) {
        OkApi.get(isPkState(roomId), null, new WrapperCallBack() {
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

    public static void getOnlineCreator(int roomType, IResultBack<List<VoiceRoomBean>> resultBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("roomType", roomType);
        OkApi.get(PKApi.ONLINE_CREATER, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Logger.d(TAG, "requestOwners#onResult");
                List<VoiceRoomBean> rooms = result.getList(VoiceRoomBean.class);
                resultBack.onResult(rooms);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                resultBack.onResult(null);
            }
        });
    }
}
