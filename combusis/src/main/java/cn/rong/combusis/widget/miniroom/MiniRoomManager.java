package cn.rong.combusis.widget.miniroom;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.rongcloud.common.utils.ImageLoaderUtil;
import com.rongcloud.common.utils.UiUtils;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import cn.rong.combusis.R;
import cn.rong.combusis.common.ui.widget.WaveView;

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
        return MiniRoomManager.Holder.INSTANCE;
    }

    public void show(Context context, String roomId, String background, Intent intent, OnCloseMiniRoomListener onCloseMiniRoomListener) {
        this.roomId = roomId;
        this.onCloseMiniRoomListener = onCloseMiniRoomListener;
        miniWindows = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.view_voice_room_mini, null);
        waveView = miniWindows.findViewById(R.id.wv_creator_background);
        bgView = miniWindows.findViewById(R.id.iv_room_creator_portrait);
        FloatWindow.with(context.getApplicationContext())
                .setTag(TAG)
                .setView(miniWindows)
                .setWidth(UiUtils.INSTANCE.dp2Px(context, 120))
                .setHeight(UiUtils.INSTANCE.dp2Px(context, 120))
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
        ImageLoaderUtil.INSTANCE.loadImage(bgView.getContext(), bgView, background, R.drawable.img_default_room_cover);
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
