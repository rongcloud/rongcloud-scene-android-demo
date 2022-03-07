package cn.rongcloud.roomkit.ui.miniroom;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.basis.utils.ImageLoader;
import com.basis.utils.Logger;
import com.basis.utils.UiUtils;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.room.widget.WaveView;

/**
 * 语聊房的最小窗口管理器
 */
public class MiniRoomManager implements OnMiniRoomListener {

    public static final String TAG = "MiniRoomManager";
    private View miniWindows;
    private WaveView waveView;
    private ImageView bgView;
    private String roomId;
    private OnCloseMiniRoomListener onCloseMiniRoomListener;
    /**
     * 滑动状态监听
     */
    private ViewStateListener viewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {

        }

        @Override
        public void onShow() {

        }

        @Override
        public void onHide() {

        }

        @Override
        public void onDismiss() {

        }

        @Override
        public void onMoveAnimStart() {

        }

        @Override
        public void onMoveAnimEnd() {

        }

        @Override
        public void onBackToDesktop() {

        }
    };


    public static MiniRoomManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 展示视频小窗口
     *
     * @param context
     * @param roomId
     * @param intent
     * @param
     * @param onCloseMiniRoomListener
     */
    public void show(Context context, String roomId, Intent intent, View miniWindows, OnCloseMiniRoomListener onCloseMiniRoomListener) {
        this.roomId = roomId;
        this.onCloseMiniRoomListener = onCloseMiniRoomListener;
        this.miniWindows = miniWindows;
        FloatWindow.with(context.getApplicationContext())
                .setTag(TAG)
                .setView(miniWindows)
                .setWidth(UiUtils.getScreenWidth(context) / 3)
                .setHeight(UiUtils.getScreenHeight(context) / 3)
                .setX(Screen.width, 0.65f)
                .setY(Screen.height, 0.55f)
                .setMoveType(MoveType.slide)
                .setDesktopShow(true)
                .build();

        miniWindows.setOnClickListener(v -> {
            close();
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        IFloatWindow iFloatWindow = FloatWindow.get(TAG);
        if (iFloatWindow != null && !iFloatWindow.isShowing()) {
            iFloatWindow.show();
        }
    }


    /**
     * 语聊房 电台房小窗口
     *
     * @param context
     * @param roomId
     * @param background
     * @param intent
     * @param onCloseMiniRoomListener
     */
    public void show(Context context, String roomId, String background, Intent intent, OnCloseMiniRoomListener onCloseMiniRoomListener) {
        this.roomId = roomId;
        this.onCloseMiniRoomListener = onCloseMiniRoomListener;
        miniWindows = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.view_voice_room_mini, null);
        waveView = miniWindows.findViewById(R.id.wv_creator_background);
        bgView = miniWindows.findViewById(R.id.iv_room_creator_portrait);
        FloatWindow.with(context.getApplicationContext())
                .setTag(TAG)
                .setView(miniWindows)
                .setWidth(UiUtils.dp2px(120))
                .setHeight(UiUtils.dp2px(120))
                .setX(Screen.width, 0.7f)
                .setY(Screen.height, 0.75f)
                .setMoveType(MoveType.active)
                .setDesktopShow(true)
                .setViewStateListener(viewStateListener)
                .build();

        miniWindows.setOnClickListener(v -> {
            close();
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ImageLoader.loadUrl(bgView, background, R.drawable.img_default_room_cover);
        IFloatWindow iFloatWindow = FloatWindow.get(TAG);
        if (iFloatWindow != null && !iFloatWindow.isShowing()) {
            iFloatWindow.show();
        }
    }


    public void close() {
        FloatWindow.destroy(TAG);
        onCloseMiniRoomListener = null;
    }

    /**
     * 是否和悬浮窗房间是同一房间
     *
     * @param targetRoomId
     * @return
     */
    public boolean isSameRoom(String targetRoomId) {
        if (TextUtils.isEmpty(targetRoomId)) {
            return false;
        } else {
            return TextUtils.equals(targetRoomId, this.roomId);
        }
    }

    public void finish(String targetRoomId, OnCloseMiniRoomListener.CloseResult closeResult) {
        FloatWindow.destroy(TAG);
        if (!isSameRoom(targetRoomId) && onCloseMiniRoomListener != null) {
            onCloseMiniRoomListener.onCloseMiniRoom(closeResult);
        } else if (closeResult != null) {
            closeResult.onClose();
        }
        this.roomId = "";
        this.onCloseMiniRoomListener = null;
    }

    public boolean isShowing() {
        IFloatWindow iFloatWindow = FloatWindow.get(TAG);
        if (iFloatWindow == null) {
            return false;
        }
        return iFloatWindow.isShowing();
    }

    @Override
    public void onSpeak(boolean isSpeaking) {
        if (waveView == null) {
            return;
        }
        waveView.post(new Runnable() {
            @Override
            public void run() {
                if (isSpeaking) {
                    waveView.start();
                } else {
                    waveView.stop();
                }
            }
        });

    }

    private static class Holder {
        private final static MiniRoomManager INSTANCE = new MiniRoomManager();
    }

}
