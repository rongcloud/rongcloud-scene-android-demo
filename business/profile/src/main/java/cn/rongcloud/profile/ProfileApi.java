package cn.rongcloud.profile;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.body.FileBody;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.RealPathFromUriUtils;
import com.basis.utils.SharedPreferUtil;
import com.basis.wapper.IResultBack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.Sex;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.provider.user.UserProvider;

public class ProfileApi {
    private final static String TAG = "ProfileApi";
    private final static String SEND_CODE = ApiConfig.HOST + "/user/sendCode";
    private final static String LOGIN = ApiConfig.HOST + "/user/login";
    private final static String UPDATE_INFO = ApiConfig.HOST + "/user/update";
    // 注册或者注销，注销不传任何参数
    public final static String RESIGN = ApiConfig.HOST + "/user/resign";

    public static void sendVerificationCode(String region, String phone, IResultBack<Boolean> back) {
        Map<String, Object> params = new HashMap<>();
        if (!region.startsWith("+")) {
            region = "+" + region;
        }
        params.put("mobile", phone);
        params.put("region", region);
        OkApi.post(SEND_CODE, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    if (null != back) back.onResult(true);
                } else {
                    KToast.show(result.getMessage());
                    if (null != back) back.onResult(false);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                KToast.show(msg);
                if (null != back) back.onResult(false);
            }
        });
    }

    public static void uploadFile(String path, IResultBack<String> resultBack) {
        Logger.e(TAG, "path = " + path);
        FileBody body = new FileBody("multipart/form-data", new File(path));
        OkApi.file(ApiConfig.FILE_UPLOAD, "file", body, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                String url = result.getBody().getAsString();
                if (null != resultBack) resultBack.onResult(url);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                if (null != resultBack) resultBack.onResult("");
            }
        });
    }

    public static void updateUserInfo(String userName, String portraitUrl, Sex sex, IResultBack<Boolean> back) {
        Logger.e(TAG, "userName = " + userName);
        Logger.e(TAG, "portraitUrl = " + portraitUrl);
        Logger.e(TAG, "sex = " + sex);
        Map<String, Object> params = new HashMap<>();
        User user = UserManager.get();
        params.put("userName", userName);
        params.put("portrait", portraitUrl);
        params.put("sex", sex.getSex());
        OkApi.post(UPDATE_INFO, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    User user = UserManager.get();
                    if (null != user) {
                        user.setUserName(userName);
                        user.setSex(sex);
                        if (!TextUtils.isEmpty(portraitUrl)) {
                            user.setPortrait(portraitUrl);
                        }
                        UserManager.save(user);
                    }
                    if (null != back) back.onResult(true);
                } else {
                    KToast.show(result.getMessage());
                    if (null != back) back.onResult(false);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                KToast.show(msg);
                if (null != back) back.onResult(false);
            }
        });
    }

    public static void login(String region, String phone, String code, IResultBack<User> back) {
        Map<String, Object> params = new HashMap<>();
        if (!region.startsWith("+")) {
            region = "+" + region;
        }
        params.put("mobile", phone);
        params.put("verifyCode", code);
        params.put("deviceId", getDeviceId());
        params.put("region", region);
        params.put("platform", "mobile");
        OkApi.post(LOGIN, params, new WrapperCallBack() {

            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    User user = result.get(User.class);
                    user.setPhone(phone);
                    UserManager.save(user);
                    UserProvider.provider().update(user.toUserInfo());
                    if (null != back) back.onResult(user);
                } else {
                    KToast.show(result.getMessage());
                    if (null != back) back.onResult(null);
                }
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                KToast.show(msg);
                if (null != back) back.onResult(null);
            }
        });
    }

    private final static String KEY_DEVICE_ID = "cn.rc.demo.device_id";

    private static String getDeviceId() {
        String deviceId = SharedPreferUtil.get(KEY_DEVICE_ID);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        String deviceShort =
                "35" + Build.BOARD.length() % 10
                        + Build.BRAND.length() % 10
                        + Build.CPU_ABI.length() % 10
                        + Build.DEVICE.length() % 10
                        + Build.MANUFACTURER.length() % 10
                        + Build.MODEL.length() % 10
                        + Build.PRODUCT.length() % 10;


        String serial = new UUID((long) deviceShort.hashCode(), (long) Build.SERIAL.hashCode()).toString();
        deviceId = new UUID((long) deviceShort.hashCode(), (long) serial.hashCode()).toString();
        SharedPreferUtil.set(KEY_DEVICE_ID, deviceId);
        return deviceId;
    }
}
