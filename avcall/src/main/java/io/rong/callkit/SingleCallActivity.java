/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package io.rong.callkit;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.kit.UIKit;
import com.kit.wapper.IResultBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.rong.combusis.feedback.FeedbackHelper;
import cn.rong.combusis.provider.user.UserProvider;
import io.rong.callkit.util.BluetoothUtil;
import io.rong.callkit.util.CallKitUtils;
import io.rong.callkit.util.DefaultPushConfig;
import io.rong.callkit.util.HeadsetInfo;
import io.rong.callkit.util.RingingMode;
import io.rong.calllib.CallUserProfile;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.calllib.message.CallSTerminateMessage;
import io.rong.common.RLog;
import io.rong.imkit.IMCenter;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.utils.PermissionCheckUtil;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class SingleCallActivity extends BaseCallActivity implements Handler.Callback {
    private static final String TAG = "VoIPSingleActivity";
    private static final int LOSS_RATE_ALARM = 20;
    int userType;
    SurfaceView remoteVideo;
    String remoteUserId;
    private LayoutInflater inflater;
    private RongCallSession callSession;
    private RelativeLayout mLPreviewContainer;
    private FrameLayout mSPreviewContainer;
    private FrameLayout mButtonContainer;
    private LinearLayout mUserInfoContainer;
    private TextView mConnectionStateTextView;
    private Boolean isInformationShow = false;
    private SurfaceView mLocalVideo = null;
    private boolean muted = false; // 静音
    private boolean handFree = false; // 免提
    private boolean startForCheckPermissions = false;
    private boolean isReceiveLost = false;
    private boolean isSendLost = false;
    private SoundPool mSoundPool = null;
    private int EVENT_FULL_SCREEN = 1;
    private String targetId = null;
    private RongCallCommon.CallMediaType mediaType;
    private RongCallCommon.CallMediaType remoteMediaType;
    private Runnable mCheckConnectionStableTask = new Runnable() {
        @Override
        public void run() {
            boolean isConnectionStable = !isSendLost && !isReceiveLost;
            if (isConnectionStable) {
                mConnectionStateTextView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public final boolean handleMessage(Message msg) {
        if (msg.what == EVENT_FULL_SCREEN) {
            hideVideoCallInformation();
            return true;
        }
        return false;
    }

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rc_voip_activity_single_call);
        getSupportActionBar().hide();
        if (RongCallModule.isIgnoreIncomingCall()) {//忽略来电也要屏蔽呼出
            Toast.makeText(UIKit.getContext(), "忙碌中...", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.i(
                "AudioPlugin",
                "savedInstanceState != null="
                        + (savedInstanceState != null)
                        + ",,,RongCallClient.getInstance() == null"
                        + (RongCallClient.getInstance() == null));
        if (savedInstanceState != null && RongCallClient.getInstance() == null) {
            // 音视频请求权限时，用户在设置页面取消权限，导致应用重启，退出当前activity.
            Log.i("AudioPlugin", "音视频请求权限时，用户在设置页面取消权限，导致应用重启，退出当前activity");
            finish();
            return;
        }
        Intent intent = getIntent();
        mLPreviewContainer = findViewById(R.id.rc_voip_call_large_preview);
        mSPreviewContainer = findViewById(R.id.rc_voip_call_small_preview);
        mButtonContainer = findViewById(R.id.rc_voip_btn);
        mUserInfoContainer = findViewById(R.id.rc_voip_user_info);
        mConnectionStateTextView = findViewById(R.id.rc_tv_connection_state);

        startForCheckPermissions = intent.getBooleanExtra("checkPermissions", false);
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));

        String receivedCallId = "";
        if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }
        } else if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            callSession = intent.getParcelableExtra("callSession");
            if (null == callSession) {// 兼容悬浮框
                callSession = RongCallClient.getInstance().getCallSession();
                CallKitUtils.callConnected = intent.getBooleanExtra("callConnected", false);
                CallKitUtils.isDial = false;
            }
            mediaType = callSession.getMediaType();
            receivedCallId = callSession.getCallId();
        } else {
            callSession = RongCallClient.getInstance().getCallSession();
            if (callSession != null) {
                mediaType = callSession.getMediaType();
                receivedCallId = callSession.getCallId();
            }
        }
        if (!RongCallClient.getInstance().canCallContinued(receivedCallId)) {
            RLog.w(TAG, "Already received hangup message before, finish current activity");
            finish();
            return;
        }
        if (mediaType != null) {
            inflater = LayoutInflater.from(this);
            initView(mediaType, callAction);

            if (requestCallPermissions(mediaType, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
                setupIntent();
            }
        } else {
            RLog.w(TAG, "remote already hangup, finish current activity");
            setShouldShowFloat(false);
            CallFloatBoxView.hideFloatBox();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        startForCheckPermissions = intent.getBooleanExtra("checkPermissions", false);
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));
        if (callAction == null) {
            return;
        }
        if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }
        } else if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            callSession = intent.getParcelableExtra("callSession");
            if (null == callSession) {// 兼容悬浮框
                callSession = RongCallClient.getInstance().getCallSession();
                CallKitUtils.callConnected = intent.getBooleanExtra("callConnected", false);
                CallKitUtils.isDial = false;
            }
            mediaType = callSession.getMediaType();
        } else {
            callSession = RongCallClient.getInstance().getCallSession();
            mediaType = callSession.getMediaType();
        }
        super.onNewIntent(intent);

        if (requestCallPermissions(mediaType, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)) {
            setupIntent();
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                boolean permissionGranted;
                if (mediaType == RongCallCommon.CallMediaType.AUDIO) {
                    permissionGranted = PermissionCheckUtil.checkPermissions(this, AUDIO_CALL_PERMISSIONS);
                } else {
                    permissionGranted = PermissionCheckUtil.checkPermissions(this, VIDEO_CALL_PERMISSIONS);
                }
                if (permissionGranted) {
                    if (startForCheckPermissions) {
                        startForCheckPermissions = false;
                        RongCallClient.getInstance().onPermissionGranted();
                    } else {
                        setupIntent();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.rc_permission_grant_needed), Toast.LENGTH_SHORT).show();
                    if (startForCheckPermissions) {
                        startForCheckPermissions = false;
                        RongCallClient.getInstance().onPermissionDenied();
                    } else {
                        Log.i("AudioPlugin", "--onRequestPermissionsResult--finish");
                        finish();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            String[] permissions;
            if (mediaType == RongCallCommon.CallMediaType.AUDIO) {
                permissions = AUDIO_CALL_PERMISSIONS;
            } else {
                permissions = VIDEO_CALL_PERMISSIONS;
            }
            if (PermissionCheckUtil.checkPermissions(this, permissions)) {
                if (startForCheckPermissions) {
                    RongCallClient.getInstance().onPermissionGranted();
                } else {
                    setupIntent();
                }
            } else {
                if (startForCheckPermissions) {
                    RongCallClient.getInstance().onPermissionDenied();
                } else {
                    Log.i("AudioPlugin", "onActivityResult finish");
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupIntent() {
        RongCallCommon.CallMediaType mediaType;
        Intent intent = getIntent();
        RongCallAction callAction = RongCallAction.valueOf(intent.getStringExtra("callAction"));
        if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            callSession = intent.getParcelableExtra("callSession");
            if (null == callSession) {// 兼容悬浮框
                callSession = RongCallClient.getInstance().getCallSession();
                CallKitUtils.callConnected = intent.getBooleanExtra("callConnected", false);
                CallKitUtils.isDial = false;
            }
            mediaType = callSession.getMediaType();
            targetId = callSession.getInviterUserId();
        } else if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            if (intent.getAction().equals(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO)) {
                mediaType = RongCallCommon.CallMediaType.AUDIO;
            } else {
                mediaType = RongCallCommon.CallMediaType.VIDEO;
            }
            targetId = intent.getStringExtra("targetId");
            starCall(mediaType, true);
        } else { // resume call
            callSession = RongCallClient.getInstance().getCallSession();
            targetId = callSession.getInviterUserId();
            mediaType = callSession.getMediaType();
        }
        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            handFree = false;
            muted = false;

        } else if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
            handFree = true;
            muted = false;
        }
        View handFreeV = findViewById(R.id.rc_voip_handfree);
        View callMuteV = findViewById(R.id.rc_voip_call_mute);
        if (null != handFreeV) handFreeV.setSelected(handFree);
        if (null != callMuteV) handFreeV.setSelected(muted);
        UserProvider.provider().getAsyn(targetId, new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                TextView userName = mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
                userName.setText(CallKitUtils.nickNameRestrict(userInfo.getName()));
                ImageView userPortrait = mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
                if (userPortrait != null && userInfo.getPortraitUri() != null) {
                    Glide.with(SingleCallActivity.this)
                            .load(userInfo.getPortraitUri())
                            .override(200)
                            .placeholder(R.drawable.rc_default_portrait)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(userPortrait);
                }
            }
        });
        createPickupDetector();
    }

    private void starCall(RongCallCommon.CallMediaType mediaType, boolean needCall) {
        String type = getIntent().getStringExtra("conversationType");
        Conversation.ConversationType conversationType = Conversation.ConversationType.PRIVATE;
        if (!TextUtils.isEmpty(type)) {
            conversationType = Conversation.ConversationType.valueOf(type.toUpperCase(Locale.US));
        }
        List<String> userIds = new ArrayList<>();
        userIds.add(targetId);
        RongCallClient.setPushConfig(DefaultPushConfig.getInviteConfig(this, mediaType == RongCallCommon.CallMediaType.AUDIO, true, ""), DefaultPushConfig.getHangupConfig(this, true, ""));
        if (needCall)
            RongCallClient.getInstance().startCall(conversationType, targetId, userIds, null, mediaType, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "---single activity onResume---");
        if (pickupDetector != null && mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            pickupDetector.register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "---single activity onPause---");
        if (pickupDetector != null) {
            pickupDetector.unRegister();
        }
    }

    @Override
    public void onBackPressed() {
        onMinimizeClick(null);
    }

    private void initView(RongCallCommon.CallMediaType mediaType, RongCallAction callAction) {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMinimizeClick(view);
            }
        });
        RelativeLayout userInfoLayout = null;
        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)
                || callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            userInfoLayout = (RelativeLayout)
                    inflater.inflate(R.layout.rc_voip_audio_call_user_info_incoming, null);
        } else {// 单人视频 or 拨打 界面
            userInfoLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_audio_call_user_info, null);
        }
        boolean incomming = callAction.equals(RongCallAction.ACTION_INCOMING_CALL);
        boolean audio = mediaType.equals(RongCallCommon.CallMediaType.AUDIO);
        RelativeLayout buttonLayout = (RelativeLayout) inflater.inflate(
                incomming ? R.layout.rc_voip_call_bottom_incoming_button_layout
                        : audio ? R.layout.rc_voip_call_bottom_connected_button_layout
                        : R.layout.rc_video_call_bottom_connected_button_layout, null);
        if (callAction.equals(RongCallAction.ACTION_RESUME_CALL) && CallKitUtils.isDial) {
            ImageView button = buttonLayout.findViewById(R.id.rc_voip_call_mute_btn);
            if (null != button) button.setEnabled(false); // 呼出 不静音
        }
        if (callAction.equals(RongCallAction.ACTION_OUTGOING_CALL)) {
            RelativeLayout layout = buttonLayout.findViewById(R.id.rc_voip_call_mute);
            layout.setVisibility(View.VISIBLE);
            ImageView button = buttonLayout.findViewById(R.id.rc_voip_call_mute_btn);
            button.setEnabled(false);
            buttonLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.VISIBLE);
            // TODO: 2021/7/26 视频通话，暂时屏蔽 不能切换 mediaType 和语音通话共用一个btn id
//            if (!audio) {
//                buttonLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.GONE);
//            }
        }

        if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            mLPreviewContainer.setVisibility(View.GONE);
            mSPreviewContainer.setVisibility(View.GONE);
            if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
                ImageView iv_answerBtn = buttonLayout.findViewById(R.id.rc_voip_call_answer_btn);
                iv_answerBtn.setImageResource(R.drawable.ic_call_audio_answer);
                TextView callInfo = userInfoLayout.findViewById(R.id.rc_voip_call_remind_info);
                callInfo.setText(R.string.rc_voip_audio_call_inviting);
                onIncomingCallRinging();
            }
        } else if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
            if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
                ImageView iv_answerBtn = buttonLayout.findViewById(R.id.rc_voip_call_answer_btn);
                iv_answerBtn.setImageResource(R.drawable.ic_call_video_answer);
                TextView callInfo = userInfoLayout.findViewById(R.id.rc_voip_call_remind_info);
                callInfo.setText(R.string.rc_voip_video_call_inviting);
                onIncomingCallRinging();
            }
        }
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(buttonLayout);
        mUserInfoContainer.removeAllViews();
        mUserInfoContainer.addView(userInfoLayout);
        if (callAction.equals(RongCallAction.ACTION_INCOMING_CALL)) {
            regisHeadsetPlugReceiver();
            if (BluetoothUtil.hasBluetoothA2dpConnected()
                    || BluetoothUtil.isWiredHeadsetOn(SingleCallActivity.this)) {
                HeadsetInfo headsetInfo =
                        new HeadsetInfo(true, HeadsetInfo.HeadsetType.BluetoothA2dp);
                onHeadsetPlugUpdate(headsetInfo);
            }
        }
    }

    private void resetTextOrIconColor(boolean white) {
        ((ImageView) findViewById(R.id.iv_back)).setImageResource(white ? R.drawable.ic_small_white : R.drawable.ic_small);
        int color = getResources().getColor(white ? android.R.color.white : R.color.color_text_main);

        TextView rc_voip_user_name = findViewById(R.id.rc_voip_user_name);
        TextView rc_voip_call_remind_info = findViewById(R.id.rc_voip_call_remind_info);
        if (null != rc_voip_user_name) rc_voip_user_name.setTextColor(color);
        if (null != rc_voip_call_remind_info) rc_voip_call_remind_info.setTextColor(color);
    }

    @Override
    public void onCallOutgoing(RongCallSession callSession, SurfaceView localVideo) {
        super.onCallOutgoing(callSession, localVideo);
        this.callSession = callSession;
        mediaType = callSession.getMediaType();
        resetTextOrIconColor(mediaType == RongCallCommon.CallMediaType.VIDEO);
        if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)) {
            if (null != mLPreviewContainer) {
                mLPreviewContainer.setVisibility(View.VISIBLE);
                mLPreviewContainer.addView(localVideo, mLargeLayoutParams);
            }
            if (null != localVideo) localVideo.setTag(callSession.getSelfUserId());
        }
        callRinging(RingingMode.Outgoing);
        regisHeadsetPlugReceiver();
        if (BluetoothUtil.hasBluetoothA2dpConnected() || BluetoothUtil.isWiredHeadsetOn(this)) {
            HeadsetInfo headsetInfo = new HeadsetInfo(true, HeadsetInfo.HeadsetType.BluetoothA2dp);
            onHeadsetPlugUpdate(headsetInfo);
        }
    }

    @Override
    public void onCallConnected(RongCallSession callSession, SurfaceView localVideo) {
        super.onCallConnected(callSession, localVideo);
        this.callSession = callSession;
        mediaType = callSession.getMediaType();
        resetTextOrIconColor(mediaType == RongCallCommon.CallMediaType.VIDEO);
        if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
            RelativeLayout btnLayout = (RelativeLayout) inflater.inflate(
                    R.layout.rc_voip_call_bottom_connected_button_layout, null);
            ImageView button = btnLayout.findViewById(R.id.rc_voip_call_mute_btn);
            button.setEnabled(true);

            mButtonContainer.removeAllViews();
            mButtonContainer.addView(btnLayout);
        } else {
            mConnectionStateTextView.setVisibility(View.VISIBLE);
            mConnectionStateTextView.setText(R.string.rc_voip_connecting);
            // 二人视频通话接通后 mUserInfoContainer 中更换为无头像的布局
            mUserInfoContainer.removeAllViews();
            inflater.inflate(R.layout.rc_voip_video_call_user_info, mUserInfoContainer);
            UserProvider.provider().getAsyn(targetId, new IResultBack<UserInfo>() {
                @Override
                public void onResult(UserInfo userInfo) {
                    if (userInfo != null) {
                        TextView userName = mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
                        userName.setText(CallKitUtils.nickNameRestrict(userInfo.getName()));
                    }
                }
            });
            mLocalVideo = localVideo;
            mLocalVideo.setTag(callSession.getSelfUserId());
        }
        TextView tv_rc_voip_call_remind_info = mUserInfoContainer.findViewById(R.id.rc_voip_call_remind_info);
        tv_rc_voip_call_remind_info.setVisibility(View.GONE);
        TextView remindInfo;
        if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
            remindInfo = mUserInfoContainer.findViewById(R.id.tv_setupTime);
        } else {
            remindInfo = mUserInfoContainer.findViewById(R.id.tv_setupTime_video);
        }
        if (remindInfo == null) {
            remindInfo = tv_rc_voip_call_remind_info;
        }
        setupTime(remindInfo);
        RongCallClient.getInstance().setEnableLocalAudio(!muted);
        View muteV = mButtonContainer.findViewById(R.id.rc_voip_call_mute);
        if (muteV != null) muteV.setSelected(muted);
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn() || BluetoothUtil.hasBluetoothA2dpConnected()) {
            handFree = false;
            RongCallClient.getInstance().setEnableSpeakerphone(false);
            ImageView handFreeV = null;
            if (null != mButtonContainer) {
                handFreeV = mButtonContainer.findViewById(R.id.rc_voip_handfree_btn);
            }
            if (handFreeV != null) {
                handFreeV.setSelected(false);
                handFreeV.setEnabled(false);
                handFreeV.setClickable(false);
            }
        } else {
            RongCallClient.getInstance().setEnableSpeakerphone(handFree);
            View handFreeV = mButtonContainer.findViewById(R.id.rc_voip_handfree);
            if (handFreeV != null) {
                handFreeV.setSelected(handFree);
            }
        }
        stopRing();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "---single activity onDestroy---");
        // 统计打分
        if (!CallFloatBoxView.isCallFloatBoxShown()) {
            FeedbackHelper.getHelper().statistics();
        }
        super.onDestroy();
    }

    @Override
    public void onRemoteUserJoined(
            final String userId,
            RongCallCommon.CallMediaType mediaType,
            int userType,
            SurfaceView remoteVideo) {
        super.onRemoteUserJoined(userId, mediaType, userType, remoteVideo);
        Log.v(TAG, "onRemoteUserJoined userID=" + userId + ",mediaType=" + mediaType.name() + " , userType=" + (userType == 1 ? "Normal" : "Observer"));
        this.remoteMediaType = mediaType;
        this.userType = userType;
        this.remoteVideo = remoteVideo;
        this.remoteUserId = userId;
    }

    private void changeToConnectedState(
            final String userId,
            RongCallCommon.CallMediaType mediaType,
            int userType,
            SurfaceView remoteVideo) {
        mConnectionStateTextView.setVisibility(View.GONE);
        if (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) {
            mLPreviewContainer.setVisibility(View.VISIBLE);
            mLPreviewContainer.removeAllViews();
            remoteVideo.setTag(userId);

            Log.v(TAG, "onRemoteUserJoined mLPreviewContainer.addView(remoteVideo)");
            mLPreviewContainer.addView(remoteVideo, mLargeLayoutParams);
            mLPreviewContainer.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isInformationShow) {
                                hideVideoCallInformation();
                            } else {
                                showVideoCallInformation();
                                handler.sendEmptyMessageDelayed(EVENT_FULL_SCREEN, 5 * 1000);
                            }
                        }
                    });
            mSPreviewContainer.setVisibility(View.VISIBLE);
            mSPreviewContainer.removeAllViews();
            Log.d(TAG, "onRemoteUserJoined mLocalVideo != null=" + (mLocalVideo != null));
            if (mLocalVideo != null) {
                mLocalVideo.setZOrderMediaOverlay(true);
                mLocalVideo.setZOrderOnTop(true);
                mSPreviewContainer.addView(mLocalVideo);
            }
            /** 小窗口点击事件 * */
            mSPreviewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SurfaceView fromView =
                                (SurfaceView) mSPreviewContainer.getChildAt(0);
                        SurfaceView toView = (SurfaceView) mLPreviewContainer.getChildAt(0);

                        mLPreviewContainer.removeAllViews();
                        mSPreviewContainer.removeAllViews();
                        fromView.setZOrderOnTop(false);
                        fromView.setZOrderMediaOverlay(false);
                        mLPreviewContainer.addView(fromView, mLargeLayoutParams);
                        toView.setZOrderOnTop(true);
                        toView.setZOrderMediaOverlay(true);
                        mSPreviewContainer.addView(toView);
                        if (null != fromView.getTag()
                                && !TextUtils.isEmpty(fromView.getTag().toString())) {
                            UserProvider.provider().getAsyn(targetId, new IResultBack<UserInfo>() {
                                @Override
                                public void onResult(UserInfo userInfo) {
                                    if (null != userInfo) {
                                        TextView userName = mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
                                        userName.setText(CallKitUtils.nickNameRestrict(userInfo.getName()));
                                    }
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mButtonContainer.setVisibility(View.GONE);
            mUserInfoContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 当通话中的某一个参与者切换通话类型，例如由 audio 切换至 video，回调 onMediaTypeChanged。
     *
     * @param userId    切换者的 userId。
     * @param mediaType 切换者，切换后的媒体类型。
     * @param video     切换着，切换后的 camera 信息，如果由 video 切换至 audio，则为 null。
     */
    @Override
    public void onMediaTypeChanged(
            String userId, RongCallCommon.CallMediaType mediaType, SurfaceView video) {
        boolean audio = mediaType == RongCallCommon.CallMediaType.AUDIO;
        if (callSession.getSelfUserId().equals(userId)) {
            showShortToast(getString(audio ? R.string.rc_voip_switched_to_audio : R.string.rc_voip_switched_to_video));
        } else {
            if (callSession.getMediaType() != RongCallCommon.CallMediaType.AUDIO) {
                RongCallClient.getInstance()
                        .changeCallMediaType(RongCallCommon.CallMediaType.AUDIO);
                callSession.setMediaType(RongCallCommon.CallMediaType.AUDIO);
                showShortToast(getString(audio ? R.string.rc_voip_remote_switched_to_audio : R.string.rc_voip_remote_switched_to_video));
            }
        }
        initAudioCallView();
        handler.removeMessages(EVENT_FULL_SCREEN);
        mButtonContainer.findViewById(R.id.rc_voip_call_mute).setSelected(muted);
    }

    @Override
    public void onNetworkReceiveLost(String userId, int lossRate) {
        isReceiveLost = lossRate > LOSS_RATE_ALARM;
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        refreshConnectionState();
                    }
                });
    }

    @Override
    public void onNetworkSendLost(int lossRate, int delay) {
        isSendLost = lossRate > LOSS_RATE_ALARM;
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        refreshConnectionState();
                    }
                });
    }

    @Override
    public void onFirstRemoteVideoFrame(String userId, int height, int width) {
        Log.d(TAG, "onFirstRemoteVideoFrame for user::" + userId);
        if (userId.equals(remoteUserId)) {
            //            mConnectionStateTextView.setVisibility(View.GONE);
            changeToConnectedState(userId, remoteMediaType, userType, remoteVideo);
        }
    }

    /**
     * 视频转语音 *
     */
    private void initAudioCallView() {
        mLPreviewContainer.removeAllViews();
        mLPreviewContainer.setVisibility(View.GONE);
        mSPreviewContainer.removeAllViews();
        mSPreviewContainer.setVisibility(View.GONE);
        resetTextOrIconColor(false);
        findViewById(R.id.rc_voip_audio_chat).setVisibility(View.GONE); // 隐藏语音聊天按钮
        View userInfoView = inflater.inflate(R.layout.rc_voip_audio_call_user_info_incoming, null);
        TextView tv_rc_voip_call_remind_info = userInfoView.findViewById(R.id.rc_voip_call_remind_info);
        TextView timeView = userInfoView.findViewById(R.id.tv_setupTime);
        if (CallKitUtils.callConnected) {
            tv_rc_voip_call_remind_info.setVisibility(View.GONE);
            setupTime(timeView);
        } else {
            tv_rc_voip_call_remind_info.setVisibility(View.VISIBLE);
            timeView.setVisibility(View.GONE);
        }

        mUserInfoContainer.removeAllViews();
        mUserInfoContainer.addView(userInfoView);
        UserProvider.provider().getAsyn(targetId, new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                if (userInfo != null) {
                    TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
                    userName.setText(CallKitUtils.nickNameRestrict(userInfo.getName()));
                    if (callSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
                        ImageView userPortrait = mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
                        if (userPortrait != null) {
                            Glide.with(SingleCallActivity.this)
                                    .load(userInfo.getPortraitUri())
                                    .placeholder(R.drawable.rc_default_portrait)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(userPortrait);
                        }
                    }
                }
            }
        });
        mUserInfoContainer.setVisibility(View.VISIBLE);
        View button = inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(button);
        mButtonContainer.setVisibility(View.VISIBLE);
        // 视频转音频时默认不开启免提
        handFree = false;
        RongCallClient.getInstance().setEnableSpeakerphone(false);
        View handFreeV = mButtonContainer.findViewById(R.id.rc_voip_handfree);
        if (null != handFreeV) {
            handFreeV.setVisibility(View.VISIBLE);
            handFreeV.setSelected(handFree);
        }
        if (pickupDetector != null) {
            pickupDetector.register(this);
        }
    }

    /**
     * 挂断
     *
     * @param view
     */
    public void onHangupBtnClick(View view) {
        unRegisterHeadsetplugReceiver();
        RongCallSession session = RongCallClient.getInstance().getCallSession();
        if (session == null || isFinishing) {
            finish();
            Log.e(TAG, "hangup call error:  callSession=" + (callSession == null) + ",isFinishing=" + isFinishing);
            return;
        }
        RongCallClient.getInstance().hangUpCall(session.getCallId());
        stopRing();
    }

    /**
     * 接听
     *
     * @param view
     */
    public void onReceiveBtnClick(View view) {
        RongCallSession session = RongCallClient.getInstance().getCallSession();
        if (session == null || isFinishing) {
            Log.e(TAG, "hangup call error:  callSession=" + (callSession == null) + ",isFinishing=" + isFinishing);
            finish();
            return;
        }
        // 处理被叫
        CallKitUtils.callConnected = true;
        RongCallClient.getInstance().acceptCall(session.getCallId());
    }

    public void hideVideoCallInformation() {
        isInformationShow = false;
        mUserInfoContainer.setVisibility(View.GONE);
        mButtonContainer.setVisibility(View.GONE);
        findViewById(R.id.rc_voip_audio_chat).setVisibility(View.GONE);
    }

    public void showVideoCallInformation() {
        isInformationShow = true;
        mUserInfoContainer.setVisibility(View.VISIBLE);
        mButtonContainer.setVisibility(View.VISIBLE);
        RelativeLayout btnLayout =
                (RelativeLayout)
                        inflater.inflate(
                                R.layout.rc_video_call_bottom_connected_button_layout, null);
        btnLayout.findViewById(R.id.rc_voip_call_mute).setSelected(muted);
        btnLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.GONE);
        btnLayout.findViewById(R.id.rc_voip_camera).setVisibility(View.VISIBLE);
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(btnLayout);
        View view = findViewById(R.id.rc_voip_audio_chat);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSwitchButtonClick(v);
                    }
                });
    }

    public void onSwitchCallButtonClick(View view) {
        RongCallClient.getInstance().changeCallMediaType(RongCallCommon.CallMediaType.AUDIO);
        callSession.setMediaType(RongCallCommon.CallMediaType.AUDIO);
        // 收到修改
        RongCallClient.getInstance().getCallSession().setMediaType(RongCallCommon.CallMediaType.AUDIO);
        Log.e(TAG, "onSwitchCallButtonClick: mediaType = " + RongCallClient.getInstance().getCallSession().getMediaType());
    }

    /**
     * 切换语音
     *
     * @param view
     */
    public void onSwitchButtonClick(View view) {
        if (RongIMClient.getInstance().getCurrentConnectionStatus()
                == RongIMClient.ConnectionStatusListener.ConnectionStatus
                .CONNECTED) {
            RongCallClient.getInstance()
                    .changeCallMediaType(RongCallCommon.CallMediaType.AUDIO);
            callSession.setMediaType(RongCallCommon.CallMediaType.AUDIO);
            initAudioCallView();
        } else {
            showShortToast(getString(R.string.rc_voip_im_connection_abnormal));
        }
    }

    /**
     * 免提
     *
     * @param view
     */
    public void onHandFreeButtonClick(View view) {
        CallKitUtils.speakerphoneState = !view.isSelected();
        RongCallClient.getInstance().setEnableSpeakerphone(!view.isSelected()); // true:打开免提 false:关闭免提
        view.setSelected(!view.isSelected());
        handFree = view.isSelected();
    }

    /**
     * 静音
     *
     * @param view
     */
    public void onMuteButtonClick(View view) {
        RongCallClient.getInstance().setEnableLocalAudio(view.isSelected());
        view.setSelected(!view.isSelected());
        muted = view.isSelected();
    }

    @Override
    public void onCallDisconnected(
            RongCallSession callSession, RongCallCommon.CallDisconnectedReason reason) {
        super.onCallDisconnected(callSession, reason);
        String senderId;
        String extra = "";
        isFinishing = true;
        if (callSession == null) {
            RLog.e(TAG, "onCallDisconnected. callSession is null!");
            postRunnableDelay(
                    new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
            return;
        }
        senderId = callSession.getInviterUserId();
        long time = getTime();
        if (time > 0) {
            if (time >= 3600) {
                extra = String.format("%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60));
            } else {
                extra = String.format("%02d:%02d", (time % 3600) / 60, (time % 60));
            }
        }
        cancelTime();
        if (!TextUtils.isEmpty(senderId)) {
            CallSTerminateMessage message = new CallSTerminateMessage();
            message.setReason(reason);
            message.setMediaType(callSession.getMediaType());
            message.setExtra(extra);
            long serverTime =
                    System.currentTimeMillis() - RongIMClient.getInstance().getDeltaTime();
            if (senderId.equals(callSession.getSelfUserId())) {
                message.setDirection("MO");
                IMCenter.getInstance().insertOutgoingMessage(
                        Conversation.ConversationType.PRIVATE,
                        callSession.getTargetId(),
                        io.rong.imlib.model.Message.SentStatus.SENT,
                        message,
                        serverTime,
                        null);
            } else {
                message.setDirection("MT");
                io.rong.imlib.model.Message.ReceivedStatus receivedStatus = new io.rong.imlib.model.Message.ReceivedStatus(0);
                IMCenter.getInstance().insertIncomingMessage(
                        Conversation.ConversationType.PRIVATE,
                        callSession.getTargetId(),
                        senderId,
                        receivedStatus,
                        message,
                        serverTime,
                        null);
            }
        }
        postRunnableDelay(
                new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
    }

    private void refreshConnectionState() {
        if (isSendLost || isReceiveLost) {
            if (mConnectionStateTextView.getVisibility() == View.GONE) {
                mConnectionStateTextView.setText(R.string.rc_voip_unstable_call_connection);
                mConnectionStateTextView.setVisibility(View.VISIBLE);
                if (mSoundPool != null) {
                    mSoundPool.release();
                }
                mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
                mSoundPool.load(this, R.raw.voip_network_error_sound, 0);
                mSoundPool.setOnLoadCompleteListener(
                        new SoundPool.OnLoadCompleteListener() {
                            @Override
                            public void onLoadComplete(
                                    SoundPool soundPool, int sampleId, int status) {
                                soundPool.play(sampleId, 1F, 1F, 0, 0, 1F);
                            }
                        });
            }
            mConnectionStateTextView.removeCallbacks(mCheckConnectionStableTask);
            mConnectionStateTextView.postDelayed(mCheckConnectionStableTask, 3000);
        }
    }

    @Override
    public void onRestoreFloatBox(Bundle bundle) {
        super.onRestoreFloatBox(bundle);
        Log.d(TAG, "---single activity onRestoreFloatBox---");
        if (bundle == null) return;
        muted = bundle.getBoolean("muted");
        handFree = bundle.getBoolean("handFree");
        setShouldShowFloat(true);
        callSession = RongCallClient.getInstance().getCallSession();
        if (callSession == null) {
            setShouldShowFloat(false);
            finish();
            return;
        }
        RongCallCommon.CallMediaType mediaType = callSession.getMediaType();
        // TODO: 2021/7/23 VIDEO-> AUDIO 后 从悬浮框过来 还是视频通话
        Log.d(TAG, "---single activity onRestoreFloatBox---" + mediaType);
        RongCallAction callAction = RongCallAction.valueOf(getIntent().getStringExtra("callAction"));
        Log.d(TAG, "---single activity onRestoreFloatBox--- callAction = " + callAction);
        inflater = LayoutInflater.from(this);
        initView(mediaType, callAction);
        targetId = callSession.getTargetId();
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(targetId);
        if (userInfo != null) {
            TextView userName = mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            userName.setText(CallKitUtils.nickNameRestrict(userInfo.getName()));
//            if (mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) {
            ImageView userPortrait = mUserInfoContainer.findViewById(R.id.rc_voip_user_portrait);
            if (userPortrait != null) {
                Glide.with(this)
                        .load(userInfo.getPortraitUri())
                        .placeholder(R.drawable.rc_default_portrait)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(userPortrait);
            }
        }
        SurfaceView localVideo = null;
        SurfaceView remoteVideo = null;
        String remoteUserId = null;
        for (CallUserProfile profile : callSession.getParticipantProfileList()) {
            if (profile.getUserId().equals(RongIMClient.getInstance().getCurrentUserId())) {
                localVideo = profile.getVideoView();
            } else {
                remoteVideo = profile.getVideoView();
                remoteUserId = profile.getUserId();
            }
        }
        if (localVideo != null && localVideo.getParent() != null) {
            ((ViewGroup) localVideo.getParent()).removeView(localVideo);
        }
        if (CallKitUtils.isDial) {
            onCallOutgoing(callSession, localVideo);
        }
        if (!CallKitUtils.isDial && CallKitUtils.callConnected) {
            onCallConnected(callSession, localVideo);
        }
        if (remoteVideo != null) {
            if (remoteVideo.getParent() != null) {
                ((ViewGroup) remoteVideo.getParent()).removeView(remoteVideo);
            }
            changeToConnectedState(remoteUserId, mediaType, 1, remoteVideo);
        }
    }

    @Override
    public String onSaveFloatBoxState(Bundle bundle) {
        super.onSaveFloatBoxState(bundle);
        callSession = RongCallClient.getInstance().getCallSession();
        if (callSession == null) {
            return null;
        }
        bundle.putBoolean("muted", muted);
        bundle.putBoolean("handFree", handFree);
        bundle.putInt("mediaType", callSession.getMediaType().getValue());
//        bundle.putString(EXTRA_BUNDLE_KEY_USER_TOP_NAME, topUserName);
        String action = getIntent().getAction();
        if (TextUtils.isEmpty(action)) {
            action = callSession.getMediaType() == RongCallCommon.CallMediaType.VIDEO
                    ? RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO
                    : RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO;
        }
        return action;
    }

    public void onMinimizeClick(View view) {
        super.onMinimizeClick(view);
    }

    /**
     * 切换相机
     *
     * @param view
     */
    public void onSwitchCameraClick(View view) {
        RongCallClient.getInstance().switchCamera();
    }

    @Override
    public void onUserUpdate(final UserInfo info) {
        if (isFinishing()) {
            return;
        }
        if (targetId != null && targetId.equals(info.getUserId())) {
            final TextView userName = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            if (info.getName() != null && null != userName)
                userName.post(new Runnable() {
                    @Override
                    public void run() {
                        userName.setText(CallKitUtils.nickNameRestrict(info.getName()));
                    }
                });
        }
    }

    public void onHeadsetPlugUpdate(HeadsetInfo headsetInfo) {
        if (headsetInfo == null || !BluetoothUtil.isForground(SingleCallActivity.this)) {
            Log.v(TAG, "SingleCallActivity 不在前台！");
            return;
        }
        Log.v(TAG, "Insert=" + headsetInfo.isInsert() + ",headsetInfo.getType=" + headsetInfo.getType().getValue());
        try {
            if (headsetInfo.isInsert()) {
                RongCallClient.getInstance().setEnableSpeakerphone(false);
                ImageView handFreeV = null;
                if (null != mButtonContainer) {
                    handFreeV = mButtonContainer.findViewById(R.id.rc_voip_handfree_btn);
                }
                if (handFreeV != null) {
                    handFreeV.setSelected(false);
                    handFreeV.setEnabled(false);
                    handFreeV.setClickable(false);
                }
                if (headsetInfo.getType() == HeadsetInfo.HeadsetType.BluetoothA2dp) {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    am.startBluetoothSco();
                    am.setBluetoothScoOn(true);
                    am.setSpeakerphoneOn(false);
                }
            } else {
                if (headsetInfo.getType() == HeadsetInfo.HeadsetType.WiredHeadset
                        && BluetoothUtil.hasBluetoothA2dpConnected()) {
                    return;
                }
                RongCallClient.getInstance().setEnableSpeakerphone(true);
                ImageView handFreeV = mButtonContainer.findViewById(R.id.rc_voip_handfree_btn);
                if (handFreeV != null) {
                    handFreeV.setSelected(true);
                    handFreeV.setEnabled(true);
                    handFreeV.setClickable(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "SingleCallActivity->onHeadsetPlugUpdate Error=" + e.getMessage());
        }
    }
}
