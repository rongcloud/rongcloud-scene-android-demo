package cn.rc.community.plugins;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.basis.utils.GlideEngine;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.PermissionUtil;
import com.basis.utils.ResUtil;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import cn.rc.community.R;
import cn.rc.community.conversion.controller.MessageManager;
import cn.rc.community.conversion.sdk.SendMessageCallback;
import io.rong.common.FileUtils;
import io.rong.imkit.feature.destruct.DestructManager;
import io.rong.imkit.picture.config.PictureMimeType;
import io.rong.imkit.utils.PermissionCheckUtil;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.GIFMessage;
import io.rong.message.ImageMessage;
import io.rong.message.SightMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/11
 * @time 5:50 下午
 * 图片功能
 */
public class ImagePlugin implements IPlugin, IPluginRequestPermissionResultCallback {

    private int mRequestCode = -1;
    private Activity activity;
    private Drawable icon;
    private String title;

    public ImagePlugin() {
        icon = ResUtil.getDrawable(R.drawable.svg_ic_pic);
        title = ResUtil.getString(R.string.cmu_str_pic);
    }

    public ImagePlugin(Drawable icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public Drawable obtainDrawable(Context context) {
        return icon;
    }

    public String obtainTitle(Context context) {
        return title;
    }

    /**
     * 点击按钮
     *
     * @param activity
     * @param index
     */
    public void onClick(Activity activity, int index) {
        this.activity = activity;
        this.mRequestCode = (index + 1 << 8) + 188;
        String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        if (PermissionUtil.hasPermissions(activity, permissions)) {
            this.openPictureSelector(activity);
        } else {
            requestPermissionForPluginResult(permissions, mRequestCode);
        }

    }

    /**
     * 得到返回结果去处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == mRequestCode) {
            List<LocalMedia> selectList = PictureSelector.obtainSelectorList(data);
            if (selectList != null && selectList.size() > 0) {
                boolean sendOrigin = ((LocalMedia) selectList.get(0)).isOriginal();
                Iterator var6 = selectList.iterator();

                while (var6.hasNext()) {
                    LocalMedia item = (LocalMedia) var6.next();
                    String mimeType = item.getMimeType();
                    if (mimeType.startsWith("image")) {
                        //发送
                        sendImage(item, sendOrigin);
//                        SendImageManager.getInstance().sendImage(this.conversationType, this.targetId, item, sendOrigin);
//                        if (this.conversationType.equals(Conversation.ConversationType.PRIVATE)) {
//                            RongIMClient.getInstance().sendTypingStatus(this.conversationType, this.targetId, "RC:ImgMsg");
//                        }
                    } else if (mimeType.startsWith("video")) {
                        if (item.getPath().startsWith("content")) {
                            item.setPath(getFilePathFromContentUri(Uri.parse(item.getPath()), activity.getContentResolver()));
                        }
                        Uri path = Uri.parse(item.getPath());
                        if (TextUtils.isEmpty(path.getScheme())) {
                            path = Uri.parse("file://" + item.getPath());
                        }
                        sendVideo(path, item.getDuration());
//                        SendMediaManager.getInstance().sendMedia(IMCenter.getInstance().getContext(), this.conversationType, this.targetId, path, item.getDuration());
//                        if (this.conversationType.equals(Conversation.ConversationType.PRIVATE)) {
//                            RongIMClient.getInstance().sendTypingStatus(this.conversationType, this.targetId, "RC:SightMsg");
//                        }
                    }
                }
            }
        }

    }

    /**
     * 发送视频消息
     *
     * @param mediaUri
     * @param duration
     */
    private void sendVideo(Uri mediaUri, long duration) {
        if (!TextUtils.isEmpty(mediaUri.toString())) {
            if (!FileUtils.isFileExistsWithUri(activity, mediaUri)) {
                return;
            }
            SightMessage sightMessage = SightMessage.obtain(activity, mediaUri, (int) duration / 1000);
            if (DestructManager.isActive()) {
                sightMessage.setDestruct(true);
                sightMessage.setDestructTime((long) DestructManager.SIGHT_DESTRUCT_TIME);
            }
            File file = new File(mediaUri.getPath());
            if (FileUtils.getFileSize(file) > 10 * 1024 * 1024) {
                KToast.show("图片文件最大支持10M");
                return;
            }
            MessageManager.get().sendMediaMessage(sightMessage, new SendMessageCallback() {
                @Override
                public void onSuccess(Message message) {

                }

                @Override
                public void onError(Message message, int code, String reason) {

                }
            });
        }
    }

    public static String getFilePathFromContentUri(Uri selectedVideoUri,

                                                   ContentResolver contentResolver) {

        String filePath = "";

        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);

// 也可用下面的方法拿到cursor

// Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);
        if (null != cursor) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            filePath = cursor.getString(columnIndex);

            cursor.close();

        }
        return filePath;

    }

    /**
     * 发送图片消息
     *
     * @param image
     * @param sendOrigin
     */
    private void sendImage(LocalMedia image, boolean sendOrigin) {
        if (image.getPath() != null) {
            String mimeType = image.getMimeType();
            String path = image.getPath();
            if (!path.startsWith("content://") && !path.startsWith("file://")) {
                path = "file://" + path;
            }

            Uri uri = Uri.parse(path);
            MessageContent content;
            if (PictureMimeType.isGif(mimeType)) {
                content = GIFMessage.obtain(uri);
            } else {
                content = ImageMessage.obtain(uri, uri, sendOrigin);
            }

            if (DestructManager.isActive() && content != null) {
                ((MessageContent) content).setDestruct(true);
                ((MessageContent) content).setDestructTime((long) DestructManager.IMAGE_DESTRUCT_TIME);
            }
            File file = new File(getFilePathFromContentUri(uri, activity.getContentResolver()));
            if (FileUtils.getFileSize(file) > 2 * 1024 * 1024) {
                KToast.show("图片文件最大支持2M");
                return;
            }
            MessageManager.get().sendImageMessage(content, new SendMessageCallback() {
                @Override
                public void onSuccess(Message message) {
                    Logger.d("ImagePlugin", "上传图片" + message.getContent() + "成功");
                }

                @Override
                public void onError(Message message, int code, String reason) {
                    Logger.d("ImagePlugin", "上传图片" + message.getContent() + "失败");
                }
            });
        }

    }

    /**
     * 得到权限
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return
     */
    public boolean onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionCheckUtil.checkPermissions(activity, permissions)) {
            if (requestCode != -1) {
                this.openPictureSelector(activity);
            }
        } else if (activity != null) {
            PermissionCheckUtil.showRequestPermissionFailedAlter(activity, permissions, grantResults);
        }

        return true;
    }

    /**
     * 打开相册
     *
     * @param activity
     */
    private void openPictureSelector(Activity activity) {
        PictureSelector.create(activity)
                .openGallery(TextUtils.equals(ResUtil.getString(R.string.cmu_str_pic), title) ?
                        PictureMimeType.ofImage() : PictureMimeType.ofVideo())
                .setImageEngine(GlideEngine.createGlideEngine())
                .isGif(true)
                .isDisplayCamera(true)
                .isDisplayTimeAxis(true)
                .setMaxSelectNum(9)
                .setImageSpanCount(3)
                .setFilterMaxFileSize(TextUtils.equals(ResUtil.getString(R.string.cmu_str_pic), title) ?
                        2 * 1024 * 1024 : 10 * 1024 * 1024)
                .forResult(this.mRequestCode);

    }

    /**
     * 申请权限
     *
     * @param permissions
     * @param requestCode
     * @param
     */
    public void requestPermissionForPluginResult(String[] permissions, int requestCode) {
        if ((requestCode & -256) != 0) {
            throw new IllegalArgumentException("requestCode must less than 256");
        } else {
            PermissionUtil.checkPermissions(activity, permissions, requestCode);
        }
    }
}
