package cn.rc.community.push;

import android.content.Context;
import android.util.Log;

import io.rong.push.PushType;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/26
 * @time 18:20
 */
public class UltraGroupPushMessageReceiver extends PushMessageReceiver {


    /**
     * 收到消息通知
     *
     * @param context
     * @param pushType
     * @param notificationMessage
     * @return
     */
    @Override
    public boolean onNotificationMessageArrived(Context context, PushType pushType, PushNotificationMessage notificationMessage) {
        Log.e("TAG", "onNotificationMessageArrived: " + notificationMessage);
        if (pushType == PushType.RONG) {
            return true;
        }
        return true;
    }

    /**
     * 被点击
     *
     * @param context
     * @param pushType
     * @param notificationMessage
     * @return
     */
    @Override
    public boolean onNotificationMessageClicked(Context context, PushType pushType, PushNotificationMessage notificationMessage) {
        return super.onNotificationMessageClicked(context, pushType, notificationMessage);
    }
}
