/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.radioroom.room;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.basis.utils.Logger;
import com.basis.utils.UIKit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.radioroom.IRCRadioRoomEngine;
import cn.rongcloud.radioroom.callback.RCRadioRoomBaseCallback;
import cn.rongcloud.radioroom.callback.RCRadioRoomCallback;
import cn.rongcloud.radioroom.callback.RCRadioRoomResultCallback;
import cn.rongcloud.radioroom.utils.EncoderUtils;
import cn.rongcloud.radioroom.utils.JsonUtils;
import cn.rongcloud.radioroom.utils.VMLog;
import cn.rongcloud.rtc.api.RCRTCAudioRouteManager;
import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCRemoteUser;
import cn.rongcloud.rtc.api.RCRTCRoom;
import cn.rongcloud.rtc.api.RCRTCRoomConfig;
import cn.rongcloud.rtc.api.callback.IRCRTCResultCallback;
import cn.rongcloud.rtc.api.callback.IRCRTCResultDataCallback;
import cn.rongcloud.rtc.api.callback.IRCRTCRoomEventsListener;
import cn.rongcloud.rtc.api.callback.IRCRTCStatusReportListener;
import cn.rongcloud.rtc.api.report.StatusBean;
import cn.rongcloud.rtc.api.report.StatusReport;
import cn.rongcloud.rtc.api.stream.RCRTCCDNInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCLiveInfo;
import cn.rongcloud.rtc.base.RCRTCLiveRole;
import cn.rongcloud.rtc.base.RCRTCMediaType;
import cn.rongcloud.rtc.base.RCRTCRoomType;
import cn.rongcloud.rtc.base.RTCErrorCode;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.IRongCoreListener;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.chatroom.base.RongChatRoomClient;
import io.rong.imlib.listener.OnReceiveMessageWrapperListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.ReceivedProfile;

/**
 * 电台房的控制
 */
public class RCRadioRoomEngineImpl implements IRCRadioRoomEngine, RongChatRoomClient.KVStatusListener,
        IRongCoreListener.OnReceiveMessageListener {
    private static final String TAG = "RCRadioRoomEngineImpl";
    private static final IRCRadioRoomEngine instance = new RCRadioRoomEngineImpl();
    private final static Handler main = new Handler(Looper.getMainLooper());
    /**
     * 是否在说话
     */
    String isSpeaking = "0";
    private RCRTCRoom mRcrtcRoom;
    private RCRadioRoomInfo mRadioRoom;
    private RCRadioEventListener listener;

    private RCRadioRoomEngineImpl() {
        RongCoreClient.addOnReceiveMessageListener(new OnReceiveMessageWrapperListener() {
            @Override
            public void onReceivedMessage(Message message, ReceivedProfile profile) {
                VMLog.e(TAG, "onReceivedMessage");
                RCRadioRoomEngineImpl.this.onReceived(message, profile.getLeft());
            }
        });
        RongChatRoomClient.getInstance().addKVStatusListener(this);
    }

    public static IRCRadioRoomEngine getInstance() {
        return instance;
    }

    private void onErrorWithCheck(final RCRadioRoomBaseCallback callback, final int code, final String message) {
        VMLog.d(TAG, "onErrorWithCheck:[" + code + "],[" + message + "] callback:" + callback);
        if (null != callback) main.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(code, message);
            }
        });
    }

    private void onSuccessWithCheck(final RCRadioRoomCallback callback) {
        if (checkCallback(callback))
            main.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess();
                }
            });
    }

    private <T> void onSuccessWithCheck(final RCRadioRoomResultCallback<T> callback, final T data) {
        if (checkCallback(callback)) {
            main.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(data);
                }
            });
        }
    }

    private boolean checkCallback(RCRadioRoomBaseCallback callback) {
        return callback != null;
    }


    @Override
    public void setRadioEventListener(RCRadioEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void joinRoom(RCRadioRoomInfo roomInfo, final RCRadioRoomCallback callback) {
        if (null == roomInfo || !roomInfo.check()) {
            onErrorWithCheck(callback, -1, "RoomInfo is Check Null");
            return;
        }
        this.mRadioRoom = roomInfo;
        VMLog.v(TAG, "joinRoom:role = " + mRadioRoom.getRole());
        RongChatRoomClient.getInstance().joinChatRoom(mRadioRoom.getRoomId(), -1, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                if (RCRTCLiveRole.BROADCASTER == mRadioRoom.getRole()) {
                    updateRadioRoomKV(UpdateKey.RC_ROOM_NAME, mRadioRoom.getRoomName(), new RCRadioRoomCallback() {
                        @Override
                        public void onSuccess() {
                            joinRTCRoom(mRadioRoom.getRoomId(), mRadioRoom.getRole(), callback);
                        }

                        @Override
                        public void onError(int code, String message) {
                            onErrorWithCheck(callback, code, message);
                        }
                    });
                } else {
                    joinRTCRoom(mRadioRoom.getRoomId(), mRadioRoom.getRole(), callback);
                }
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode err) {
                VMLog.e(TAG, "joinRoom#joinChatRoom", err);
                onErrorWithCheck(callback, err.code, err.msg);
            }
        });
    }

    @Override
    public void leaveRoom(final RCRadioRoomCallback callback) {
        if (null == mRadioRoom || !mRadioRoom.check() || null == mRcrtcRoom) {
            onErrorWithCheck(callback, 0, "Not Join RadioRoom");
            return;
        }
        VMLog.d(TAG, "leaveRoom#roomId:" + mRadioRoom.getRoomId());
        leaveSeat(new RCRadioRoomCallback() {
            @Override
            public void onSuccess() {
                quitChatRoom(callback);
            }

            @Override
            public void onError(int code, String message) {
                quitChatRoom(callback);
            }
        });
    }

    private void quitChatRoom(RCRadioRoomCallback callback) {
        //离开聊天室 无论成功与否 都执行leaveRTCRoom
        RongChatRoomClient.getInstance().quitChatRoom(mRadioRoom.getRoomId(), new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                VMLog.d(TAG, "quitRoom:onSuccess: ");
                leaveRTCRoom(callback);
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode code) {
                VMLog.e(TAG, "quitRoom:onError", code);
                leaveRTCRoom(null);
                onErrorWithCheck(callback, code.code, code.msg);
            }
        });
    }

    @Override
    public void enterSeat(final RCRadioRoomCallback callback) {
        if (null == mRcrtcRoom || null == mRadioRoom || !mRadioRoom.check()) {
            onErrorWithCheck(callback, -1, "Check RTCRoom Or RadioRoom is Null ");
            return;
        }
        if (mRadioRoom.isInSeat()) {
            onErrorWithCheck(callback, -1, "Is In Seat ");
            return;
        }
        // 取消静音
        muteSelf(false);
        mRcrtcRoom.getLocalUser().publishDefaultLiveStreams(new IRCRTCResultDataCallback<RCRTCLiveInfo>() {
            @Override
            public void onSuccess(RCRTCLiveInfo rcrtcLiveInfo) {
                VMLog.e(TAG, "enterSeat#publishDefaultLiveStreams#onSuccess:");
                addCustomerCDNStream(rcrtcLiveInfo);
                RCRTCEngine.getInstance().registerStatusReportListener(new StateListener());
                // 跟新KV
                updateRadioRoomKV(UpdateKey.RC_SEATING, "1", new RCRadioRoomCallback() {
                    @Override
                    public void onSuccess() {
                        mRadioRoom.setInSeat(true);
                        onSuccessWithCheck(callback);
                    }

                    @Override
                    public void onError(int code, String message) {
                        onErrorWithCheck(callback, code, message);
                    }
                });
            }

            @Override
            public void onFailed(RTCErrorCode error) {
                VMLog.e(TAG, "enterSeat#publishDefaultLiveStreams#onFailed", error);
                onErrorWithCheck(callback, error.getValue(), error.getReason());
            }
        });
    }

    private RCRTCLiveInfo mLiveInfo;

    private void addCustomerCDNStream(RCRTCLiveInfo liveInfo) {
        if (null == liveInfo || null == mRadioRoom
                || StreamType.customer != mRadioRoom.getStreamType()) {
            // 非自定义cdn
            return;
        }
        this.mLiveInfo = liveInfo;
        String pushUrl = EncoderUtils.formatRtmpUrl(mRadioRoom.getRoomId(), true);
        Logger.e(TAG, "pushUrl = " + pushUrl);
        mLiveInfo.addPublishStreamUrl(pushUrl, new IRCRTCResultDataCallback<String[]>() {
            @Override
            public void onSuccess(String[] data) {
                VMLog.e(TAG, "addCDNStream#onSuccess:" + data);
            }

            @Override
            public void onFailed(RTCErrorCode errorCode) {
                VMLog.e(TAG, "addCDNStream#onFailed:" + errorCode);
            }
        });
    }

    // TODO: 2022/7/22 离开房间调用即可
    private void removeCustomerCDNStream() {
        if (null == mLiveInfo || null == mRadioRoom) return;
        String pushUrl = EncoderUtils.formatRtmpUrl(mRadioRoom.getRoomId(), true);
        Logger.e(TAG, "pushUrl = " + pushUrl);
        mLiveInfo.removePublishStreamUrl(pushUrl, new IRCRTCResultDataCallback<String[]>() {
            @Override
            public void onSuccess(String[] data) {
                VMLog.e(TAG, "removeCDNStream#onSuccess:" + data);
            }

            @Override
            public void onFailed(RTCErrorCode errorCode) {
                VMLog.e(TAG, "removeCDNStream#onFailed:" + errorCode);
            }
        });
    }

    @Override
    public void leaveSeat(final RCRadioRoomCallback callback) {
        if (null == mRcrtcRoom || null == mRadioRoom || !mRadioRoom.check()) {
            onErrorWithCheck(callback, -1, "Check RTCRoom Or RadioRoom is Null ");
            return;
        }
        if (!mRadioRoom.isInSeat()) {
            onErrorWithCheck(callback, -1, "Is Not In Seat ");
            return;
        }
        // 静音
        muteSelf(true);
        // 更新KV
        updateRadioRoomKV(UpdateKey.RC_SEATING, "0", new RCRadioRoomCallback() {
            @Override
            public void onSuccess() {
                mRadioRoom.setInSeat(false);
                removeCustomerCDNStream();
                onSuccessWithCheck(callback);
            }

            @Override
            public void onError(int code, String message) {
                onErrorWithCheck(callback, code, message);
            }
        });
    }

    @Override
    public void muteSelf(boolean isMute) {
        RCRTCEngine.getInstance().getDefaultAudioStream().setMicrophoneDisable(isMute);
    }

    @Override
    public void updateRadioRoomKV(final UpdateKey type, final String value, final RCRadioRoomCallback callback) {
        RongChatRoomClient.getInstance().forceSetChatRoomEntry(mRadioRoom.getRoomId(), type.getValue(), value, false, false, "", new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                onSuccessWithCheck(callback);
                if (listener != null) {
                    main.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onRadioRoomKVUpdate(type, value);
                        }
                    });
                }
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode error) {
                VMLog.e(TAG, "updateKv#forceSetChatRoomEntry", error);
                onErrorWithCheck(callback, error.code, error.msg);
            }
        });
    }

    @Override
    public void getRadioRoomValue(UpdateKey key, final RCRadioRoomResultCallback<String> callback) {
        if (mRadioRoom == null) {
            onErrorWithCheck(callback, -1, "radioRoom is null");
            return;
        }
        final String noticeType = key.getValue();
        RongChatRoomClient.getInstance().getChatRoomEntry(mRadioRoom.getRoomId(), noticeType, new IRongCoreCallback.ResultCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> stringStringMap) {
                String value = stringStringMap.get(noticeType);
                if (!TextUtils.isEmpty(value)) {
                    onSuccessWithCheck(callback, value);
                }
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                onErrorWithCheck(callback, coreErrorCode.code, coreErrorCode.msg);
            }
        });
    }

    private IPlayer player;

    @Override
    public void setPlayer(IPlayer player) {
        this.player = player;
    }

    private void listenRadio(boolean listen, final RCRadioRoomCallback callback) {
        if (null != mRadioRoom && StreamType.customer == mRadioRoom.getStreamType()) {
            listenCustomerCDN(listen, callback);
        } else {
            listenRongCDN(listen, callback);
        }
    }

    private void listenCustomerCDN(boolean listen, RCRadioRoomCallback callback) {
        if (null == mRadioRoom) return;
        String roomId = mRadioRoom.getRoomId();
        String pullUrl = EncoderUtils.formatRtmpUrl(roomId, false);
        VMLog.d(TAG, "listenCustomerCDNRadio: " + listen + "  " + pullUrl);
        if (null == player) {
            VMLog.e(TAG, "listenCustomerCDNRadio: No Set IPlayer");
            if (null != callback) callback.onError(-1, "No Set IPlayer");
            return;
        }
        if (listen) {
            player.start(pullUrl);
        } else {
            player.stop();
        }
    }

    private void listenRongCDN(boolean listen, final RCRadioRoomCallback callback) {
        VMLog.d(TAG, "listenRongCDN: " + listen);
        if (null == mRcrtcRoom || null == mRadioRoom || !mRadioRoom.check()) {
            onErrorWithCheck(callback, -1, "Check RTCRoom Or RadioRoom is Null ");
            return;
        }
        RCRTCCDNInputStream stream = mRcrtcRoom.getCDNStream();
        if (null == stream) {
            onErrorWithCheck(callback, -2, "Not Find CDN Stream");
            return;
        }
        if (!listen) return;
        mRcrtcRoom.getLocalUser().subscribeStreams(Arrays.asList(stream), new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                VMLog.d(TAG, "onPublishCDNStream:listenRadio:subscribeStreams:onSuccess");
                onSuccessWithCheck(callback);
            }

            @Override
            public void onFailed(RTCErrorCode code) {
                VMLog.d(TAG, "onPublishCDNStream:listenRadio:" + code.getValue() + "  " + code.getReason());
                onErrorWithCheck(callback, code.getValue(), code.getReason());
            }
        });
    }

    private void release() {
        mRadioRoom = null;
        RCRTCEngine.getInstance().unregisterStatusReportListener();
        if (null != mRcrtcRoom) {
            mRcrtcRoom.unregisterRoomListener();
            mRcrtcRoom = null;
        }
        listener = null;
        if (null != player) {
            player.release();
        }
    }

    private void leaveRTCRoom(final RCRadioRoomCallback callback) {
        RCRTCEngine.getInstance().leaveRoom(new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                release();
                RCRTCEngine.getInstance().unInit();
                RCRTCAudioRouteManager.getInstance().unInit();
                // 兼容1加7T 手机
                postAudioMode(AudioManager.MODE_NORMAL);
                onSuccessWithCheck(callback);
            }

            @Override
            public void onFailed(RTCErrorCode code) {
                VMLog.e(TAG, "leaveRTCRoom#leaveRoom", code);
                onErrorWithCheck(callback, code.getValue(), code.getReason());
            }
        });
    }

    private Handler handler;

    void postAudioMode(final int audioMode) {
        if (null == handler) {
            Looper looper = Looper.myLooper();
            if (null != looper) {
                handler = new Handler();
            }
        }
        if (null != handler) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AudioManager manager = (AudioManager) UIKit.getContext().getSystemService(Context.AUDIO_SERVICE);
                    manager.setMode(audioMode);
                }
            });
        }
    }

    private void joinRTCRoom(String roomId, final RCRTCLiveRole role, final RCRadioRoomCallback callback) {
        initRCRTCEngine();
        RCRTCRoomConfig config = RCRTCRoomConfig
                .Builder
                .create()
                .setRoomType(RCRTCRoomType.LIVE_AUDIO)
                .setLiveRole(role)
                .build();
        RCRTCEngine.getInstance().joinRoom(roomId, config, new IRCRTCResultDataCallback<RCRTCRoom>() {
            @Override
            public void onSuccess(RCRTCRoom rcrtcRoom) {
                mRcrtcRoom = rcrtcRoom;
                mRcrtcRoom.registerRoomListener(new RoomEventsListener());
                if (role == RCRTCLiveRole.AUDIENCE) {
                    listenRadio(true, null);
                }
                VMLog.d(TAG, "joinRTCRoom#joinRoom#onSuccess: role = " + role);
                onSuccessWithCheck(callback);
            }

            @Override
            public void onFailed(RTCErrorCode error) {
                VMLog.e(TAG, "joinRTCRoom#joinRoom#onFailed", error);
                onErrorWithCheck(callback, error.getValue(), error.getReason());
            }
        });
    }

    private void initRCRTCEngine() {
        RCRTCConfig.Builder builder = RCRTCConfig
                .Builder
                .create()
                .enableHardwareDecoder(true)
                .enableHardwareEncoder(true);
        String manufacturer = Build.MANUFACTURER.trim();
        if (manufacturer.contains("vivo")) {
            builder.setAudioSource(MediaRecorder.AudioSource.MIC);
        } else {
            builder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                    .enableLowLatencyRecording(true);
        }
        RCRTCConfig config = builder.build();
        RCRTCEngine.getInstance().init(UIKit.getContext(), config);
        RCRTCAudioRouteManager.getInstance().init(UIKit.getContext());
    }

    @Override
    public void onChatRoomKVSync(String roomId) {
        VMLog.d(TAG, "onChatRoomKVSync: " + roomId);
    }

    @Override
    public void onChatRoomKVUpdate(String roomId, final Map<String, String> chatRoomKvMap) {
        VMLog.d(TAG, "onChatRoomKVUpdate : " + "roomId = " + roomId + " size = " + chatRoomKvMap.size() + " chatRoomKvMap = " + JsonUtils.toJson(chatRoomKvMap));
        if (null == chatRoomKvMap || chatRoomKvMap.isEmpty()) {
            VMLog.d(TAG, "onChatRoomKVUpdate: KV is Empty");
            return;
        }
        if (mRadioRoom == null) {
            VMLog.d(TAG, "onChatRoomKVUpdate: mRadioRoom is null");
            return;
        }
        if (!TextUtils.equals(roomId, mRadioRoom.getRoomId())) {
            VMLog.d(TAG, "onChatRoomKVUpdate: roomId not equal");
            return;
        }
        if (null == listener) {
            VMLog.d(TAG, "onChatRoomKVUpdate: listener is null");
            return;
        }
        final Map<String, String> map = new HashMap<>();
        map.putAll(chatRoomKvMap);
        main.post(new Runnable() {
            @Override
            public void run() {
                UpdateKey[] values = UpdateKey.values();
                String value;
                for (int i = 0; i < values.length; i++) {
                    value = map.get(values[i].getValue());
                    if (value != null) {
                        listener.onRadioRoomKVUpdate(values[i], value);
                    }
                }
            }
        });
    }

    @Override
    public void onChatRoomKVRemove(String s, Map<String, String> map) {
        VMLog.d(TAG, "onChatRoomKVRemove:roomId = " + s);
    }

    @Override
    public boolean onReceived(final Message message, int i) {
        if (null != listener) main.post(new Runnable() {
            @Override
            public void run() {
                listener.onMessageReceived(message);
            }
        });
        return false;
    }

    public class StateListener extends IRCRTCStatusReportListener {

        @Override
        public void onConnectionStats(final StatusReport statusReport) {
            String speaking = "0";
            for (StatusBean statusBean : statusReport.statusAudioSends.values()) {
                if (statusBean.isSend && TextUtils.equals(statusBean.mediaType, RCRTCMediaType.AUDIO.getDescription())) {
                    if (statusBean.audioLevel > 0) {
                        speaking = "1";
                    } else {
                        speaking = "0";
                    }
                }
            }
            if (listener != null) {
                if (TextUtils.equals(isSpeaking, speaking)) {
                    return;
                }
                isSpeaking = speaking;
                updateRadioRoomKV(UpdateKey.RC_SPEAKING, isSpeaking, null);
            }
        }
    }

    public class RoomEventsListener extends IRCRTCRoomEventsListener {
        @Override
        public void onPublishCDNStream(RCRTCCDNInputStream stream) {
            VMLog.d(TAG, "onPublishCDNStream:");
            if (RCRTCLiveRole.BROADCASTER == mRadioRoom.getRole()) {
                return;
            }
            listenRadio(true, new RCRadioRoomCallback() {
                @Override
                public void onSuccess() {
                    VMLog.d(TAG, "onPublishCDNStream:onSuccess");
                }

                @Override
                public void onError(int code, String message) {

                }
            });
        }

        @Override
        public void onUnpublishCDNStream(RCRTCCDNInputStream stream) {
            VMLog.d(TAG, "onUnpublishCDNStream:");
            if (RCRTCLiveRole.BROADCASTER == mRadioRoom.getRole()) {
                return;
            }
            listenRadio(false, new RCRadioRoomCallback() {
                @Override
                public void onSuccess() {
                    VMLog.d(TAG, "onUnpublishCDNStream:onSuccess");
                }

                @Override
                public void onError(int code, String message) {

                }
            });
        }

        @Override
        public void onRemoteUserPublishResource(RCRTCRemoteUser rcrtcRemoteUser, List<RCRTCInputStream> list) {
        }

        @Override
        public void onRemoteUserMuteAudio(RCRTCRemoteUser rcrtcRemoteUser, RCRTCInputStream rcrtcInputStream, boolean b) {
        }

        @Override
        public void onRemoteUserMuteVideo(RCRTCRemoteUser rcrtcRemoteUser, RCRTCInputStream rcrtcInputStream, boolean b) {
        }

        @Override
        public void onRemoteUserUnpublishResource(RCRTCRemoteUser rcrtcRemoteUser, List<RCRTCInputStream> list) {
        }

        @Override
        public void onUserJoined(RCRTCRemoteUser rcrtcRemoteUser) {
        }

        @Override
        public void onUserLeft(RCRTCRemoteUser rcrtcRemoteUser) {
            VMLog.d(TAG, "onUserLeft:");
        }

        @Override
        public void onUserOffline(RCRTCRemoteUser rcrtcRemoteUser) {
            VMLog.d(TAG, "onUserOffline:");
        }

        @Override
        public void onPublishLiveStreams(List<RCRTCInputStream> list) {
        }

        @Override
        public void onUnpublishLiveStreams(List<RCRTCInputStream> list) {
        }
    }
}
