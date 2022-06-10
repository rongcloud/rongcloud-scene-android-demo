package cn.rc.community.plugins;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import com.basis.utils.KToast;
import com.basis.utils.UIKit;

import cn.rc.community.R;
import io.rong.common.RLog;
import io.rong.imkit.utils.ExecutorHelper;
import io.rong.imlib.model.Conversation;
import io.rong.message.FileMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/11
 * @time 6:00 下午
 * 文件功能
 */
public class FilePlugin implements IPlugin {
    private static final String TAG = "FilePlugin";
    private static final int REQUEST_FILE = 100;
    private static final int TIME_DELAY = 400;
    private Conversation.ConversationType conversationType;
    private String targetId;
    private Context mContext;

    public FilePlugin() {
    }

    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.svg_ic_file);
    }

    public String obtainTitle(Context context) {
        return context.getString(R.string.cmu_str_file);
    }

    public void onClick(Activity activity, int index) {
//        this.conversationType = extension.getConversationType();
//        this.targetId = extension.getTargetId();

        if (activity != null) {
            this.mContext = activity.getApplicationContext();
        }

        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("*/*");
        activity.startActivityForResult(intent, REQUEST_FILE);
    }

    @SuppressLint("WrongConstant")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_FILE == requestCode && data != null) {
            final Uri uri = data.getData();
            int takeFlags = data.getFlags() & 3;
            this.mContext.getContentResolver().takePersistableUriPermission(uri, takeFlags);
            ExecutorHelper.getInstance().diskIO().execute(new Runnable() {
                public void run() {
                    try {
                        FileMessage fileMessage = FileMessage.obtain(UIKit.getContext(), uri);
                        KToast.show("发送文件" + uri + "成功");
//                        if (fileMessage != null) {
//                            Message message = Message.obtain(io.rong.imkit.conversation.extension.component.plugin.FilePlugin.this.targetId, io.rong.imkit.conversation.extension.component.plugin.FilePlugin.this.conversationType, fileMessage);
//                            IMCenter.getInstance().sendMediaMessage(message, (String) null, (String) null, (IRongCallback.ISendMediaMessageCallback) null);
//                        }
                    } catch (Exception var3) {
                        RLog.e("FilePlugin", "select file exception" + var3);
                    }

                }
            });
        }

    }
}
