package cn.rong.combusis.widget.miniroom;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rongcloud.common.utils.ImageLoaderUtil;
import com.rongcloud.common.utils.UiUtils;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import cn.rong.combusis.R;
import cn.rong.combusis.common.ui.widget.WaveView;
import cn.rongcloud.liveroom.api.RCLiveEngine;
import cn.rongcloud.liveroom.weight.RCLiveView;

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

    /**
     * 展示视频小窗口
     *
     * @param context
     * @param roomId
     * @param intent
     * @param
     * @param onCloseMiniRoomListener
     */
    public void show(Context context, String roomId, Intent intent, OnCloseMiniRoomListener onCloseMiniRoomListener) {
        this.roomId = roomId;
        this.onCloseMiniRoomListener = onCloseMiniRoomListener;
        miniWindows = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.view_live_room_mini, null);
        RelativeLayout relativeLayout = miniWindows.findViewById(R.id.fl_content_id);
        View view_close = miniWindows.findViewById(R.id.iv_close_id);
        view_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish("", null);
            }
        });
        // 这里改成DP值
        RCLiveView rcLiveView = RCLiveEngine.getInstance().preview();
        ViewParent parent = rcLiveView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeAllViews();
        }

        rcLiveView.setDevTop(0);
        relativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                relativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                rcLiveView.attachParent(relativeLayout, null);
            }
        });
        FloatWindow.with(context.getApplicationContext())
                .setTag(TAG)
                .setView(miniWindows)
                .setWidth(UiUtils.INSTANCE.getScreenWidth(context) / 3)
                .setHeight(UiUtils.INSTANCE.getScreenHeight(context) / 3)
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
