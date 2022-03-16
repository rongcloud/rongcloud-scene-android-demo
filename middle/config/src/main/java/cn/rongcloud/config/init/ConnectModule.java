package cn.rongcloud.config.init;

import android.app.Application;
import android.text.TextUtils;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;

import java.util.Arrays;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.AppConfig;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.init.shumei.RCDeviceMessage;
import cn.rongcloud.config.init.shumei.RCSMMessage;
import cn.rongcloud.config.provider.user.User;
import io.rong.imkit.IMCenter;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.listener.OnReceiveMessageWrapperListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.ReceivedProfile;

/**
 * im连接模块，默认注册
 */
public class ConnectModule implements IModule {
    private final static String TAG = "ConnectModule";
    private OnRegisterMessageTypeListener listener;
    private final static String DEVICE = ApiConfig.HOST + "/user/login/device/mobile";

    protected ConnectModule(OnRegisterMessageTypeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onInit() {
        // 连接监听
        IMCenter.getInstance().addConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
            @Override
            public void onChanged(ConnectionStatus status) {
                Logger.d(TAG, "onInit: ConnectionStatusListener");
                if (status == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
                    Logger.d(TAG, "onInit: KICKED_OFFLINE_BY_OTHER_CLIENT");
                    KToast.show("当前账号已在其他设备登录，请重新登录");
                    UserManager.logout();
                }
            }
        });

        // 监听通用业务消息
        // 1. 数美审核消息
        // 2. 设备登录消息
        RongCoreClient.addOnReceiveMessageListener(new OnReceiveMessageWrapperListener() {
            @Override
            public void onReceivedMessage(Message message, ReceivedProfile profile) {
                if (null != message) {
                    MessageContent content = message.getContent();
                    Logger.e(TAG, "message objectName = " + message.getObjectName() + "content = " + GsonUtil.obj2Json(content));
                    //如果status级别：1: 弹窗提示  2: 则为用户跳到登录页面
                    if (content instanceof RCSMMessage) {
                        RCSMMessage rcs = (RCSMMessage) content;
                        if (2 == rcs.getStatus()) {
                            UserManager.logout();
                        }
                        KToast.show(rcs.getMessage());
                    } else if (content instanceof RCDeviceMessage) {
                        RCDeviceMessage rds = (RCDeviceMessage) content;
                        // mobile web 和 desktop
                        // 注意 android 和 ios 可以通过连接状态监听实现互踢，此处主要针对和web互踢功能
                        String platform = rds.getPlatform();
                        if (!TextUtils.equals(platform, "mobile")) {
                            UserManager.logout();
                            KToast.show(platform + " 端已登录");
                        }
                    }
                }
            }
        });

        String appkey = AppConfig.get().getAppKey();
        if (TextUtils.isEmpty(appkey)) {
            return;
        }
        // 初始化im
        RongIM.init((Application) UIKit.getContext(), appkey);
        // 添加自定义消息
        if (null != listener) listener.onRegisterMessageType();

        User user = UserManager.get();
        String imToken = null == user ? "" : user.getImToken();
        if (TextUtils.isEmpty(imToken)) {
            return;
        }
        //兼容踢web端，需要先上报状态，才能连接 否则web端无法接收到服务分发的设备消息
        reportDevice(new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                if (!aBoolean) return;
                UIKit.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 用户已登录则连接im
                        RongIM.connect(imToken, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onSuccess(String t) {
                                Logger.e(TAG, "connect#onSuccess:" + t);
                            }

                            @Override
                            public void onError(RongIMClient.ConnectionErrorCode e) {
                                Logger.e(TAG, "connect#onError:" + GsonUtil.obj2Json(e));
                            }

                            @Override
                            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus code) {
                                Logger.e(TAG, "connect#onDatabaseOpened:code = " + code);
                            }
                        });
                    }
                }, 200);
            }
        });

    }

    @Override
    public void onUnInit() {
    }

    @Override
    public void onRegisterMessageType() {
        RongIMClient.registerMessageType(Arrays.asList(RCDeviceMessage.class, RCSMMessage.class));
    }

    /**
     * 上报设备状态，处理自定登录踢除web端
     */
    private static void reportDevice(IResultBack<Boolean> resultBack) {
        OkApi.post(DEVICE, null, new WrapperCallBack() {
            @Override
            public void onError(int code, String msg) {
                Logger.e(TAG, "reportDevice#onError code  = " + code + " message = " + msg);
                if (null != resultBack) resultBack.onResult(false);
            }

            @Override
            public void onResult(Wrapper result) {
                Logger.e(TAG, "reportDevice#onResult code  = " + result.getCode() + " message = " + result.getMessage());
                if (null != resultBack) resultBack.onResult(null != result && result.ok());
            }
        });
    }
}
