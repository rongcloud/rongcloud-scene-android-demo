/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import cn.rongcloud.voiceroomdemo.R;


/**
 * 以解决通话过程中切入后台麦克风不工作
 */
public class RTCNotificationService extends Service {

    private static final String CHANNEL_ID = "RTCNotificationService";
    private final int notifyId = 20200202;
    private NotificationManager manager;

    private void init() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(CHANNEL_ID, "onCreate");
        init();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent("io.rong.intent.action.voice_room")
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);
        Notification.Builder builder =
                new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.app_icon)
                        .setContentTitle("语聊房")
                        .setContentText("正在语聊中...")
                        .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_EVENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        Notification notification = builder.build();
        manager.notify(notifyId, notification);
        startForeground(notifyId, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(CHANNEL_ID, "onBind");
        return null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(CHANNEL_ID, "onDestroy");
        manager.cancel(notifyId);
        stopForeground(true);
    }

}
