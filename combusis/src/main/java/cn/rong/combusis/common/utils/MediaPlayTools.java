/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package cn.rong.combusis.common.utils;

import static android.content.Context.AUDIO_SERVICE;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

/**
 * <p>Title: MediaPlayTools.java</p>
 * <p>Description:
 * case R.id.start:
 * File file = new File(Environment.getExternalStorageDirectory(),"voiceDemo/fn_2013100916_99e9ba8bf0924f269e4e25ab1df6c726_1142.amr");
 * MediaPlayTools.getInstance().playVoice(file.getAbsolutePath(), false);
 * break;
 * case R.id.puse:
 * <p>
 * MediaPlayTools.getInstance().pause();
 * break;
 * case R.id.resume:
 * <p>
 * MediaPlayTools.getInstance().resume();
 * break;
 * case R.id.stop:
 * <p>
 * MediaPlayTools.getInstance().stop();
 * break;</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: http://www.cloopen.com</p>
 *
 * @author Jorstin Chan
 * @version 3.5
 * @date 2013-10-16
 */
public class MediaPlayTools {

    private static final String TAG = "MediaPlayTools";
    /**
     * The definition of the state of play
     * Play error
     */
    private static final int STATUS_ERROR = -1;
    /**
     * Stop playing
     */
    private static final int STATUS_STOP = 0;
    /**
     * Voice playing
     */
    private static final int STATUS_PLAYING = 1;
    /**
     * Pause playback
     */
    private static final int STATUS_PAUSE = 2;
    private static MediaPlayTools mInstance = null;
    private static Context context;
    private final boolean calling = false;
    /**
     * 当前语音播放的Item
     */
    public int mVoicePosition = -1;
    public String mVoiceMsgid = "";
    public boolean isCompelete = false;
    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause
                Log.i("AudioFocusChange", "AUDIOFOCUS_LOSS_TRANSIENT");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume
                Log.i("AudioFocusChange", "AUDIOFOCUS_GAIN");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Stop
                Log.i("AudioFocusChange", "AUDIOFOCUS_LOSS");
            }
        }
    };
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private OnVoicePlayCompletionListener mListener;
    /**
     * The local path voice file
     */
    private String urlPath = "";
    private int status = 0;
    private AudioManager mAudioManager;

    public MediaPlayTools() {

        setOnCompletionListener();
        setOnErrorListener();
    }

    synchronized public static MediaPlayTools getInstance(Context applicationContext) {
        if (null == mInstance) {
            mInstance = new MediaPlayTools();
        }
        context = applicationContext;
        return mInstance;
    }

    /**
     * 通过uri获取视频长度
     *
     * @param mUri
     * @return
     */
    public static String getRingDuring(String mUri) {
        String duration = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                }
                mmr.setDataSource(mUri, headers);
            }

            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
            //异常
            return duration;
        } finally {
            mmr.release();
        }
        if (!TextUtils.isEmpty(duration)) {
            return formatTime(Long.parseLong(duration) / 1000);
        }
        return duration;
    }

    /**
     * get Local video duration
     * 获取本地视频时长
     *
     * @return
     */
    public static int getLocalVideoDuration(String videoPath) {
        int duration;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return duration;
    }

    public static String formatTime(long time) {
        return time >= 3600 ? String.format(Locale.US, "%d:%02d:%02d", (time / 3600), (time % 3600 / 60), (time % 60)) :
                String.format(Locale.US, "%d:%02d", (time / 60), (time % 60));
    }

    /**
     * <p>Title: play</p>
     * <p>Description: Speech interface, you can set the start position to play,
     * and to select the output stream (Earpiece or Speaker)</p>
     *
     * @param isEarpiece
     * @param seek
     */
    private void play(boolean isEarpiece, int seek) {

        int streamType = AudioManager.STREAM_MUSIC;
        if (TextUtils.isEmpty(urlPath) || !new File(urlPath).exists()) {
            return;
        }

        if (isEarpiece) {
            streamType = AudioManager.STREAM_VOICE_CALL;
        }
        requestAudioFocus();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            setOnCompletionListener();
            setOnErrorListener();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(streamType);
            mediaPlayer.setDataSource(urlPath);
            mediaPlayer.prepare();
            if (seek > 0) {
                mediaPlayer.seekTo(seek);
            }
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "[MediaPlayTools - play ] playImp : fail, exception = " + e.getMessage());
        }
    }

    /**
     * <p>Title: play</p>
     * <p>Description: </p>
     *
     * @param urlPath
     * @param isEarpiece
     * @param seek
     * @return
     * @see #play(boolean, int)
     */
    public boolean play(String urlPath, boolean isEarpiece, int seek) {

        if (status != STATUS_STOP) {
            Log.e(TAG, "[MediaPlayTools - play ] startPlay error status:" + status);
            return false;
        }

        this.urlPath = urlPath;

        boolean result = false;
        try {
            play(isEarpiece, seek);
            this.status = STATUS_PLAYING;
            result = true;
        } catch (Exception e) {
            e.printStackTrace();

            try {
                play(true, seek);
                result = true;
            } catch (Exception e1) {
                e1.printStackTrace();
                result = false;
                Log.v(TAG, "[MediaPlayTools - play ] startPlay File[" + this.urlPath + "] failed");
            }

        }

        return result;

    }

    /**
     * Using the speaker model play audio files
     * <p>Title: playVoice</p>
     * <p>Description: </p>
     *
     * @param urlPath
     * @param isEarpiece
     * @return
     */
    public boolean playVoice(String urlPath, boolean isEarpiece) {

        return play(urlPath, isEarpiece, 0);
    }

    /**
     * <p>Title: resume</p>
     * <p>Description: Recovery pause language file, from the last to suspend the position to start playing</p>
     *
     * @return
     */
    public boolean resume() {

        if (this.status != STATUS_PAUSE) {

            Log.e(TAG, "[MediaPlayTools - resume ] resume not STATUS_PAUSE error status:" + this.status);
            return false;
        }

        boolean result = false;

        try {
            mediaPlayer.start();
            this.status = STATUS_PLAYING;
            result = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.e(TAG, "[MediaPlayTools - resume ] resume File[" + this.urlPath + "] ErrMsg[" + e.getStackTrace() + "]");
            this.status = STATUS_ERROR;
            result = false;
        }

        return result;

    }

    public boolean isPause() {
        return this.status == STATUS_PAUSE;
    }

    /**
     * <p>Title: stop</p>
     * <p>Description: Stop playing audio files
     * If you need to play, you will need to call
     *
     * @return
     * @see MediaPlayTools
     * @see MediaPlayTools#playVoice(String, boolean)
     */
    public boolean stop() {

        if (status != STATUS_PLAYING && status != STATUS_PAUSE) {

            Log.e(TAG, "[MediaPlayTools - stop] stop not STATUS_PLAYING or STATUS_PAUSE error status:" + this.status);
            return false;
        }

        boolean result = false;
        try {
            if (mediaPlayer != null) {
                this.mediaPlayer.stop();
                this.mediaPlayer.release();
                this.mediaPlayer = null;
            }
            this.status = STATUS_STOP;
            result = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.v(TAG, "[MediaPlayTools - stop]stop File[" + this.urlPath + "] ErrMsg[" + e.getStackTrace() + "]");
            this.status = STATUS_ERROR;
            result = false;

        }

        return result;
    }

    /**
     * <p>Title: setSpeakerOn</p>
     * <p>Description: Set the output device mode (the Earpiece or Speaker) to play voice
     * </p>
     *
     * @param speakerOn
     */
    public void setSpeakerOn(boolean speakerOn) {

        Log.v(TAG, "[MediaPlayTools - setSpeakerOn] setSpeakerOn=" + speakerOn);

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        if (calling) {
            // 这里需要判断当前的状态是否是正在系统电话振铃或者接听中

        } else {
            int currentPosition = mediaPlayer.getCurrentPosition();

            stop();

//			setOnCompletionListener();
//			setOnErrorListener();

            play(urlPath, !speakerOn, currentPosition);
        }
    }

    public boolean pause() {
        if (this.status != STATUS_PLAYING) {
            Log.e(TAG, "[MediaPlayTools - pause]pause not STATUS_PLAYING error status:" + this.status);
            return false;
        }

        boolean result = false;

        try {

            mediaPlayer.pause();
            this.status = STATUS_PAUSE;
            result = true;

        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "[MediaPlayTools - pause] pause File[" + this.urlPath + "] ErrMsg[" + e.getStackTrace() + "]");
            result = false;
            status = STATUS_ERROR;
        }

        return result;
    }

    public int getStatus() {
        return status;
    }

    public boolean isPlaying() {

        return this.status == STATUS_PLAYING;
    }

    /**
     * <p>Title: setOnCompletionListener</p>
     * <p>Description: Set the playback end of speech monitoring,
     * again will play the status is set to an initial state</p>
     *
     * @see MediaPlayTools#status
     * {@link MediaPlayTools#STATUS_ERROR}
     * {@link MediaPlayTools#STATUS_PLAYING}
     * {@link MediaPlayTools#STATUS_PAUSE}
     * {@link MediaPlayTools#STATUS_STOP}
     */
    private void setOnCompletionListener() {

        //
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "[MediaPlayTools - setOnCompletionListener] Play file[" + urlPath + "] com");
                status = STATUS_STOP;
                releaseAudioFocus();
                if (mListener != null) {
                    mListener.OnVoicePlayCompletion();
                }
            }
        });
    }

    /**
     * <p>Title: setOnErrorListener</p>
     * <p>Description: Set the language player initialization error correction</p>
     */
    private void setOnErrorListener() {

        //
        mediaPlayer.setOnErrorListener(null);
    }

    public void setOnVoicePlayCompletionListener(OnVoicePlayCompletionListener l) {
        mListener = l;
    }

    public int getPlayPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 获取音频焦点
     *
     * @param
     * @return
     */
    public boolean requestAudioFocus() {
        if (null != context.getApplicationContext()) {
            mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(AUDIO_SERVICE);
            int ret = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            return ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        return false;
    }

    /**
     * 释放音频焦点
     *
     * @return
     */
    public boolean releaseAudioFocus() {
        if (mAudioManager == null) {
            return false;
        }
        int ret = mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        return ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public interface OnVoicePlayCompletionListener {
        void OnVoicePlayCompletion();
    }
}
