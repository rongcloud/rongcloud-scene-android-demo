package cn.rongcloud.thirdcdn;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;
import com.pili.pldroid.player.widget.PLVideoView;

import cn.rongcloud.liveroom.api.RCRect;
import cn.rongcloud.liveroom.api.model.RCLiveSeatInfo;
import cn.rongcloud.liveroom.manager.LayoutManager;
import cn.rongcloud.liveroom.manager.SeatManager;
import cn.rongcloud.liveroom.weight.WeightUtil;
import cn.rongcloud.liveroom.weight.interfaces.ICDNPlayer;

/**
 * @author gyn
 * @date 2022/9/7
 * <p>
 * 这里采用七牛播放器，官方地址参见 https://developer.qiniu.com/pili/1210/the-android-client-sdk
 */
public class ThirdCDNPlayer extends FrameLayout implements ICDNPlayer {

    public static final String TAG = ThirdCDNPlayer.class.getSimpleName();

    private String roomId = null;

    private PLVideoView mVideoView;

    private Context mContext;

    public ThirdCDNPlayer(Context context) {
        this(context, null);
    }

    public ThirdCDNPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThirdCDNPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ThirdCDNPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    private void initVideoView() {
        removeAllViews();
        mVideoView = new PLVideoView(mContext);
        addView(mVideoView);
        ProgressBar progressBar = new ProgressBar(mContext);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.WHITE));
        mVideoView.setBufferingIndicator(progressBar);
        // 更多详细配置参见 https://developer.qiniu.com/pili/1210/the-android-client-sdk#5

        AVOptions options = new AVOptions();
        // 是否开启直播优化，1 为开启，0 为关闭。若开启，视频暂停后再次开始播放时会触发追帧机制
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        // 快开模式，启用后会加快该播放器实例再次打开相同协议的视频流的速度
        options.setInteger(AVOptions.KEY_FAST_OPEN, 1);
        // 打开重试次数，设置后若打开流地址失败，则会进行重试
        options.setInteger(AVOptions.KEY_OPEN_RETRY_TIMES, 5);
        // 预设置 SDK 的 log 等级， 0-4 分别为 v/d/i/w/e
        options.setInteger(AVOptions.KEY_LOG_LEVEL, 3);
        // 打开视频时单次 http 请求的超时时间，一次打开过程最多尝试五次
        // 单位为 ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 5 * 1000);
        // 请在开始播放之前配置
        mVideoView.setAVOptions(options);

        addListener();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void start(String roomId) {
        Log.d(TAG, "===================== third cdn start , roomId = " + roomId);
        this.roomId = roomId;
        initVideoView();
        mVideoView.setVideoPath(getVideoPath());
        mVideoView.start();
        Log.d(TAG, "===================== third cdn started");
    }

    private String getVideoPath() {
        return ThirdCDNConstant.getPullUrl(roomId);
    }


    @Override
    public void stop() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView = null;
        }
        roomId = null;
        Log.d(TAG, "===================== third cdn stop");
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        // Log.d(TAG, "===================== dispatchDraw w*h = " + realWidth + "*" + realHeight);

        // 画布裁剪，根据麦位摆放把视频不规则黑色部分裁掉，以漏出背景
        RCRect[] rcRects = LayoutManager.get().getFrames();
        Path path = new Path();
        for (int i = 0; i < rcRects.length; i++) {
            RCLiveSeatInfo rcLiveSeatInfo = SeatManager.get().getSeatByIndex(i);
            if (null == rcLiveSeatInfo || TextUtils.isEmpty(rcLiveSeatInfo.getUserId()) || !rcLiveSeatInfo.isEnableVideo()) {
                continue;
            }
            Rect rect = WeightUtil.getClipRect(rcRects[i], realWidth, realHeight, 0, 0);
            path.moveTo(rect.left, rect.top);
            path.lineTo(rect.right, rect.top);
            path.lineTo(rect.right, rect.bottom);
            path.lineTo(rect.left, rect.bottom);
            path.close();
        }
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }

    private void addListener() {
        mVideoView.setOnPreparedListener(new PLOnPreparedListener() {
            @Override
            public void onPrepared(int i) {
                Log.d(TAG, "===================== onPrepared = " + i);
            }
        });
        // setOnInfoListener(new PLOnInfoListener() {
        //     @Override
        //     public void onInfo(int i, int i1, Object o) {
        //         Log.d(TAG, "===================== onInfo = " + i + " " + i1 + " " + o);
        //     }
        // });
        mVideoView.setOnErrorListener(new PLOnErrorListener() {
            @Override
            public boolean onError(int i, Object o) {
                Log.d(TAG, Thread.currentThread() + " ===================== onError = " + i + " " + o);
                // -1未知错误 -2播放器打开失败 -3网络异常
                if (i == -3 || i == -2 || i == -1) {
                    retry();
                }
                return false;
            }
        });
        mVideoView.setOnVideoSizeChangedListener(new PLOnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(int i, int i1) {
                Log.d(TAG, "===================== onVideoSizeChanged = " + i + " " + i1);
            }
        });
    }

    private void retry() {
        if (!TextUtils.isEmpty(roomId)) {
            Log.d(TAG, "===================== third cdn retry started");
            mVideoView.stopPlayback();
            mVideoView = null;
            start(roomId);
        }

    }
}
