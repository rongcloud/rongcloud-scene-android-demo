/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package io.rong.callkit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import io.rong.callkit.util.IncomingCallExtraHandleUtil;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imlib.model.AndroidConfig;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.MessagePushConfig;
import io.rong.imlib.model.UserInfo;
import io.rong.push.common.PushConst;
import io.rong.push.common.RLog;
import io.rong.push.notification.PushNotificationMessage;
import io.rong.push.notification.RongNotificationInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * 为解决在 Android 10 以上版本不再允许后台运行 Activity，音视频的离线推送呼叫消息将由通知栏的形式展示给用户
 * Created by wangw on 2019-12-09.
 */
public class VoIPBroadcastReceiver extends BroadcastReceiver {

    private static final String HANGUP = "RC:VCHangup";
    private static final String INVITE = "RC:VCInvite";
    public static final String ACTION_CALLINVITEMESSAGE = "action.push.CallInviteMessage";
    public final static String ACTION_CALLINVITEMESSAGE_CLICKED = "action.push.CallInviteMessage.CLICKED";//通知pantent发送的广播
    private static final String TAG = "VoIPBroadcastReceiver";
    private static Map<String, Integer> notificationCache = new HashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        RLog.d(TAG, "onReceive.action:" + action);

        PushNotificationMessage message = intent.getParcelableExtra(PushConst.MESSAGE);
        RongCallSession callSession = null;
        boolean checkPermissions = false;
        if (intent.hasExtra("callsession")) {
            callSession = intent.getParcelableExtra("callsession");
            checkPermissions = intent.getBooleanExtra("checkPermissions", false);
        }

        if (TextUtils.equals(ACTION_CALLINVITEMESSAGE, action)) {
            if (callSession == null) {
                RLog.e(TAG, "push:: callsession is null !!");
                return;
            }
            String objName = message.getObjectName();
            if (TextUtils.equals(objName, INVITE)) {
                IncomingCallExtraHandleUtil.cacheCallSession(callSession, checkPermissions);
                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(callSession.getCallerUserId());
                sendNotification(context, message, callSession, checkPermissions, userInfo);
            } else {
                IncomingCallExtraHandleUtil.clear();
                UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(callSession.getCallerUserId());
                sendNotification(context, message, callSession, checkPermissions, userInfo);
            }
        } else if (TextUtils.equals(ACTION_CALLINVITEMESSAGE_CLICKED, action)) {
            IncomingCallExtraHandleUtil.removeNotification(context);
            IncomingCallExtraHandleUtil.clear();
            clearNotificationCache();
            handleNotificationClickEvent(context, message, callSession, checkPermissions);
        }
    }

    private void handleNotificationClickEvent(Context context, PushNotificationMessage message, RongCallSession callSession, boolean checkPermissions) {
        Intent intent;
        //如果进程被杀 RongCallClient.getInstance() 返回Null
        if (RongCallClient.getInstance() != null && RongCallClient.getInstance().getCallSession() != null && callSession != null) {
            intent = RongCallModule.createVoIPIntent(context, callSession, checkPermissions);
            RLog.d(TAG, "handleNotificationClickEvent: start call activity");
        } else {
//            intent = createConversationListIntent(context);
//            RLog.d(TAG, "handleNotificationClickEvent: start conversation activity");
            boolean video = null == callSession ? false : callSession.getMediaType() == RongCallCommon.CallMediaType.VIDEO;
            intent = createDialIntent(context, video);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(context.getPackageName());
        context.startActivity(intent);
    }

    private void sendNotification(Context context, PushNotificationMessage message, RongCallSession callSession, boolean checkPermissions, UserInfo userInfo) {
        String pushContent;
        boolean isAudio = callSession.getMediaType() == RongCallCommon.CallMediaType.AUDIO;
        if (message.getObjectName().equals(HANGUP)) {
            pushContent = context.getResources().getString(R.string.rc_voip_call_terminalted_notify);
            if (callSession.getConversationType().equals(ConversationType.GROUP) && RongCallClient.getInstance().getCallSession() != null) {
                return;//群组消息，getCallSession不为空，说明收到的hangup并不是最后一个人发出的，此时不需要生成通知
            }
        } else {
            if (userInfo == null) {
                pushContent = context.getResources().getString(isAudio ? R.string.rc_voip_notificatio_audio_call_inviting_general : R.string.rc_voip_notificatio_video_call_inviting_general);
            } else {
                pushContent = userInfo.getName() + context.getResources().getString(isAudio ? R.string.rc_voip_notificatio_audio_call_inviting : R.string.rc_voip_notificatio_video_call_inviting);
            }
        }
        message.setPushContent(pushContent);
        if (callSession.getConversationType().equals(ConversationType.PRIVATE)) {
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getName())) {
                message.setPushTitle(userInfo.getName());
            }
        } else if (callSession.getConversationType().equals(ConversationType.GROUP)) {
            Group group = RongUserInfoManager.getInstance().getGroupInfo(callSession.getTargetId());
            if (group != null && !TextUtils.isEmpty(group.getName())) {
                message.setPushTitle(group.getName());
            }
        }
        if (callSession != null && callSession.getPushConfig() != null) {
            MessagePushConfig messagePushConfig = callSession.getPushConfig();
            if (!TextUtils.isEmpty(messagePushConfig.getPushTitle())) {
                message.setPushTitle(messagePushConfig.getPushTitle());
            }
            if (!TextUtils.isEmpty(messagePushConfig.getPushContent()) &&
                    !messagePushConfig.getPushContent().equals("voip")) {
                message.setPushContent(messagePushConfig.getPushContent());
            }
            if (messagePushConfig.isForceShowDetailContent()) {
                message.setShowDetail(messagePushConfig.isForceShowDetailContent());
            }
            AndroidConfig androidConfig = messagePushConfig.getAndroidConfig();
            if (androidConfig != null) {
                message.setChannelIdHW(androidConfig.getChannelIdHW());
                message.setChannelIdMi(androidConfig.getChannelIdMi());
                message.setChannelIdOPPO(androidConfig.getChannelIdOPPO());
                message.setNotificationId(androidConfig.getNotificationId());
            }
        }
        sendNotification(context, message, callSession, checkPermissions);
    }

    private void sendNotification(Context context, PushNotificationMessage message, RongCallSession callSession, boolean checkPermissions) {
        String objName = message.getObjectName();
        if (TextUtils.isEmpty(objName)) {
            return;
        }

        String title;
        String content;
        int notificationId = IncomingCallExtraHandleUtil.VOIP_NOTIFICATION_ID;
        RLog.i(TAG, "sendNotification() messageType: " + message.getConversationType()
                + " messagePushContent: " + message.getPushContent()
                + " messageObjectName: " + message.getObjectName()
                + " notificationId: " + message.getNotificationId());


        if (objName.equals(INVITE) || objName.equals(HANGUP)) {
            content = message.getPushContent();
            title = message.getPushTitle();
        } else {
            return;
        }

        Notification notification = RongNotificationInterface.createNotification(context, title, createPendingIntent(context, message, callSession, checkPermissions, IncomingCallExtraHandleUtil.VOIP_REQUEST_CODE, false), content, RongNotificationInterface.SoundType.VOIP, message.isShowDetail());
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String channelName = context.getResources().getString(context.getResources().getIdentifier("rc_notification_channel_name", "string", context.getPackageName()));
            NotificationChannel notificationChannel = new NotificationChannel("rc_notification_id", channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            if (notification != null && notification.sound != null && !objName.equals(HANGUP)) {//挂断不需要铃声
                notificationChannel.setSound(notification.sound, null);
            }
            nm.createNotificationChannel(notificationChannel);
        }
        if (notification != null) {
            RLog.i(TAG, "sendNotification() real notify! notificationId: " + notificationId +
                    " notification: " + notification.toString());
            if (message.getObjectName().equals(INVITE)) {
                notificationCache.put(callSession.getCallId(), notificationId);
                nm.notify(notificationId, notification);
                IncomingCallExtraHandleUtil.VOIP_NOTIFICATION_ID++;
            } else if (notificationCache.containsKey(callSession.getCallId())) {
                notificationId = notificationCache.get(callSession.getCallId());
                nm.notify(notificationId, notification);
            }
        }
    }

    private static PendingIntent createPendingIntent(Context context, PushNotificationMessage message, RongCallSession callSession, boolean checkPermissions, int requestCode, boolean isMulti) {
        Intent intent = new Intent();
        intent.setAction(ACTION_CALLINVITEMESSAGE_CLICKED);
        intent.putExtra(PushConst.MESSAGE, message);
        intent.putExtra("callsession", callSession);
        intent.putExtra("checkPermissions", checkPermissions);
        intent.putExtra(PushConst.IS_MULTI, isMulti);
        intent.setPackage(context.getPackageName());
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Intent createConversationListIntent(Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse("rong://" + context.getPackageName()).buildUpon()
                .appendPath("conversationlist").build();
        intent.setData(uri);
        intent.setPackage(context.getPackageName());
        return intent;
    }

    private static Intent createDialIntent(Context context, boolean video) {
        RLog.i(TAG, "createDialIntent:video = " + video);
        Intent intent = new Intent("io.rong.intent.action.voip.DIAL");
        intent.putExtra("is_video", video);
        intent.setPackage(context.getPackageName());
        return intent;
    }

    public static void clearNotificationCache() {
        notificationCache.clear();
    }
}
