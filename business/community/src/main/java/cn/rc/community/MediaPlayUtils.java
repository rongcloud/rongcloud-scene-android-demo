package cn.rc.community;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.basis.ui.UIStack;
import com.basis.utils.KToast;

import cn.rc.community.activity.CoolViewActivity;
import io.rong.imkit.utils.RongOperationPermissionUtils;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ReferenceMessage;
import io.rong.message.SightMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/25
 * @time 20:28
 * 显示大图预览或者视频
 */
public class MediaPlayUtils {

    public static Context activity = UIStack.getInstance().getTopActivity();

    /**
     * 播放小视频
     *
     * @param message
     */
    public static void playSightMessage(Message message) {
        MessageContent content = message.getContent();
        SightMessage sightMessage;
        if (content instanceof ReferenceMessage) {
            sightMessage = (SightMessage) ((ReferenceMessage) content).getReferenceContent();
        } else {
            sightMessage = (SightMessage) message.getContent();
        }
        if (sightMessage != null) {
            if (!RongOperationPermissionUtils.isMediaOperationPermit(activity)) {
                return;
            } else {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("rong").authority(activity.getPackageName()).appendPath("sight").appendPath("player");
                String intentUrl = builder.build().toString();
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(intentUrl));
                intent.setPackage(activity.getPackageName());
                intent.putExtra("SightMessage", sightMessage);
                intent.putExtra("Message", message);
                intent.putExtra("Progress", 0);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(intent);
                } else {
                    KToast.show("Sight Module does not exist.");
                }
            }
        }

    }

    /**
     * 大图预览图片消息
     *
     * @param message
     */
    public static void showImage(Message message) {
        Intent intent = new Intent(activity, CoolViewActivity.class);
        intent.putExtra("message", message);
        activity.startActivity(intent);
    }
}
