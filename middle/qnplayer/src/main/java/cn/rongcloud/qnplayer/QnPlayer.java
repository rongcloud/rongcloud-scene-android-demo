package cn.rongcloud.qnplayer;


import android.content.Context;
import android.media.AudioManager;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;

import java.io.IOException;


public class QnPlayer implements IPlayer {
    private final static String TAG = "QnPlayer";
    PLMediaPlayer mMediaPlayer;
    private PlayListener listener;
    private final AVOptions mAVOptions;
    private final AudioManager audioManager;
    private final TelephonyManager telephonyManager;
    private String trySource;

    public QnPlayer() {
        audioManager = (AudioManager) UIKit.getContext().getSystemService(Context.AUDIO_SERVICE);
        telephonyManager = (TelephonyManager) UIKit.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        mAVOptions = new AVOptions();
        mAVOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE);
        mAVOptions.setInteger(AVOptions.KEY_START_POSITION, 1000);
        mAVOptions.setInteger(AVOptions.KEY_OPEN_RETRY_TIMES, 3);
        mAVOptions.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        mAVOptions.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 500);
        mAVOptions.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 5000);
    }

    private boolean isPrepare() {
        return null != mMediaPlayer;
    }

    private void prepare() {
        Logger.e(TAG, "prepare");
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
//        switchSpeaker(true);
        if (mMediaPlayer == null) {
            mMediaPlayer = new PLMediaPlayer(UIKit.getContext(), mAVOptions);
            mMediaPlayer.setVolume(1.5f, 1.5f);
            listener = new PlayListener(this);
            mMediaPlayer.setVideoEnabled(false);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.setOnPreparedListener(listener);
            mMediaPlayer.setOnCompletionListener(listener);
            mMediaPlayer.setOnErrorListener(listener);
            mMediaPlayer.setOnInfoListener(listener);
            mMediaPlayer.setOnBufferingUpdateListener(listener);
            mMediaPlayer.setWakeMode(UIKit.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        }
        startTelephonyListener();
    }

    protected void tryStartAgain() {
        if (!TextUtils.isEmpty(trySource)) {
            Logger.e(TAG, "2S After try start again ");
            UIKit.postDelayed(new Runnable() {
                @Override
                public void run() {
                    start(trySource);
                    // 只在尝试一次
                    trySource = null;
                }
            }, 2000);
        }
    }

    @Override
    public void start(String url) {
        Logger.e(TAG, "start");
        if (!isPrepare()) {
            prepare();
            try {
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.prepareAsync();
                trySource = url;
            } catch (IOException e) {
                e.printStackTrace();
                stop();
            }
        } else {
            mMediaPlayer.start();
        }

    }

    @Override
    public void pause() {
        Logger.e(TAG, "pause");
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void resume() {
        Logger.e(TAG, "resume");
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void stop() {
        Logger.e(TAG, "stop");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        mMediaPlayer = null;
    }


    public void release() {
        Logger.e(TAG, "release");
        stop();
        AudioManager audioManager = (AudioManager) UIKit.getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);
        stopTelephonyListener();
    }

    PhoneStateListener mPhoneStateListener;

    // Listen to the telephone
    private void startTelephonyListener() {
        mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Logger.d(TAG, "PhoneStateListener: CALL_STATE_IDLE");
                        if (mMediaPlayer != null) {
                            mMediaPlayer.start();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Logger.d(TAG, "PhoneStateListener: CALL_STATE_OFFHOOK");
                        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                            mMediaPlayer.pause();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        Logger.d(TAG, "PhoneStateListener: CALL_STATE_RINGING: " + incomingNumber);
                        break;
                }
            }
        };

        try {
            telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopTelephonyListener() {
        if (telephonyManager != null && mPhoneStateListener != null) {
            telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
            mPhoneStateListener = null;
        }
    }
}
