package cn.rong.combusis.sdk.event.wrapper;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.kit.utils.Logger;
import com.kit.wapper.IResultBack;

import java.lang.ref.WeakReference;

import cn.rong.combusis.VRCenterDialog;
import cn.rong.combusis.sdk.VoiceRoomApi;
import cn.rongcloud.voiceroom.model.PKResponse;
import io.rong.imlib.model.UserInfo;


public class EventDialogHelper {

    private final static EventDialogHelper helper = new EventDialogHelper();
    private VRCenterDialog dialog;
    private Timer pkTimer;

    public static EventDialogHelper helper() {
        return helper;
    }

    public void dismissDialog() {
        if (null != dialog) {
            dialog.dismiss();
        }
        if (null != pkTimer) {
            pkTimer.cancel();
        }
        dialog = null;
    }

    public void showTipDialog(Activity activity, String title, String message, IResultBack<Boolean> resultBack) {
        if (null == dialog || !dialog.enable()) {
            dialog = new VRCenterDialog(activity,
                    new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            EventDialogHelper.this.dialog = null;
                        }
                    });
        }
        TextView textView = new TextView(dialog.getContext());
        textView.setText(message);
        textView.setTextSize(18);
        textView.setTextColor(Color.parseColor("#343434"));
        dialog.replaceContent(title,
                "拒绝",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                        if (null != resultBack) resultBack.onResult(false);
                    }
                },
                "同意",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                        if (null != resultBack) resultBack.onResult(true);
                    }
                },
                textView);
        dialog.show();
    }

    public void showPKDialog(Activity activity, String title, String roomId, UserInfo userInfo, IResultBack<Boolean> resultBack) {
        if (null == dialog || !dialog.enable()) {
            dialog = new VRCenterDialog(activity,
                    new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            EventDialogHelper.this.dialog = null;
                        }
                    });
        }
        TextView textView = new TextView(dialog.getContext());
        textView.setTextSize(14);
        textView.setTextColor(Color.parseColor("#343434"));
        if (null != pkTimer) {
            pkTimer.cancel();
        }
        pkTimer = new Timer(textView, roomId, userInfo);
        pkTimer.start();
        dialog.replaceContent(title,
                "拒绝",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                        if (null != resultBack) resultBack.onResult(false);
                    }
                },
                "同意",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                        if (null != resultBack) resultBack.onResult(true);
                    }
                },
                textView);
        dialog.show();
    }

    public class Timer extends CountDownTimer {
        private WeakReference<TextView> reference;
        private String preInfo;
        private String roomId;
        private String userId;

        Timer(TextView textView, String roomId, UserInfo userInfo) {
            super(11 * 1000, 1000);
            this.preInfo = "房主 " + userInfo.getName() + " 邀请您进行PK，是否同意？";
            this.reference = new WeakReference<>(textView);
            this.roomId = roomId;
            this.userId = userInfo.getUserId();
        }

        @Override
        public void onTick(long l) {
            Logger.e("Timer", "L = " + l);
            if (null != reference && reference.get() != null) {
                reference.get().setText(preInfo + " (" + (l / 1000) + "秒)");
            }
            if (l < 1000) {
                dismissDialog();
                VoiceRoomApi.getApi().responsePKInvitation(roomId, userId, PKResponse.ignore, null);
            }
        }

        @Override
        public void onFinish() {

        }
    }
}
