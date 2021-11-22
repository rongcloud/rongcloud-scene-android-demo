/*
 * Coyright © 2021 RongCloud. All rights reserved.
 */

package cn.rong.combusis.music;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.basis.net.oklib.wrapper.interfaces.ILoadTag;
import com.kit.utils.KToast;
import com.kit.utils.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import cn.rong.combusis.common.net.IResultBack;
import cn.rong.combusis.common.utils.RealPathFromUriUtils;
import cn.rong.combusis.common.utils.UIKit;
import cn.rong.combusis.music.domain.MusicBean;
import cn.rongcloud.rtc.api.RCRTCAudioMixer;
import cn.rongcloud.rtc.api.callback.RCRTCAudioMixingStateChangeListener;

/**
 * music 管理类
 */
public class MusicManager implements IMusic {
    public final static int MUSIC_TYPE_SYS = 0;// 系统内置
    public final static int MUSIC_TYPE_USER = 1;
    public final static int MUSIC_LOCAL_ADD = -1;//最后一个添加条目
    public final static int WHAT_NEXT = 1000;
    public final static int WHAT_STOP = 1001;
    public final static int WHAT_PLAY = 1002;
    private final static String TAG = "MusicManager";
    private final static MusicManager manager = new MusicManager();
    private final static HandlerThread playJob;
    private final static Handler handler;

    static {
        playJob = new HandlerThread("VOICE_MUSIC");
        playJob.start();
        handler = new Handler(playJob.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (WHAT_NEXT == msg.what) {
                    manager.playNextMusic();
                } else if (WHAT_STOP == msg.what) {
                    manager.stopPlayMusic();
                } else if (WHAT_PLAY == msg.what) {
                    Object obj = msg.obj;
                    if (null != obj && obj instanceof MusicBean) {
                        manager.playMusic((MusicBean) obj);
                    }
                }
            }
        };
    }

    // 用户添加music 可能包含sys
    private final List<MusicBean> userMusics = new ArrayList<>(8);
    // 系统内置music
    private final List<MusicBean> sysMusics = new ArrayList<>(8);
    private String roomId;
    private String currentPlayMusicUrl;
    private RCRTCAudioMixer.MixingState currentMusicState = RCRTCAudioMixer.MixingState.STOPPED;
    private List<VRMusicListener> listeners = new ArrayList<>(4);
    // 停止播放标识
    private boolean musicStopFlag = false;

    private MusicManager() {
    }

    public static IMusic get() {
        return manager;
    }

    @Override
    public void unInit() {
        RCRTCAudioMixer.getInstance().setAudioMixingStateChangeListener(null);
        userMusics.clear();
        listeners.clear();
    }

    @Override
    public void init(String roomId) {
        this.roomId = roomId;
        loadMusic(null);
        RCRTCAudioMixer.getInstance().setAudioMixingStateChangeListener(new RCRTCAudioMixingStateChangeListener() {
            @Override
            public void onMixEnd() {
                Logger.e(TAG, "onMixEnd: ");
                if (!musicStopFlag) {
                    handler.sendEmptyMessage(WHAT_NEXT);
                }
            }

            @Override
            public void onStateChanged(RCRTCAudioMixer.MixingState mixingState, RCRTCAudioMixer.MixingStateReason mixingStateReason) {
                currentMusicState = mixingState;
                Logger.e(TAG, "onStateChanged: currentMusicState = " + currentMusicState);
                int count = null != userMusics ? userMusics.size() : 0;
                if (RCRTCAudioMixer.MixingState.PLAY == mixingState) {
                    for (int i = 0; i < count; i++) {
                        MusicBean bean = userMusics.get(i);
                        bean.setPlaying(bean.getUrl().equals(currentPlayMusicUrl));
                    }
                } else if (RCRTCAudioMixer.MixingState.PAUSED == mixingState || RCRTCAudioMixer.MixingState.STOPPED == mixingState) {
                    for (int i = 0; i < count; i++) {
                        userMusics.get(i).setPlaying(false);
                    }
                }
            }

            @Override
            public void onReportPlayingProgress(float v) {
            }
        });

    }

    /**
     * load music
     *
     * @param resultBack load more 完成回调
     */
    private void loadMusic(IResultBack resultBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final CountDownLatch latch = new CountDownLatch(2);
                // load sys music
                MusicApi.loadMusics(roomId, MUSIC_TYPE_SYS, new IResultBack<List<MusicBean>>() {
                    @Override
                    public void onResult(List<MusicBean> musicBeans) {
                        sysMusics.clear();
                        if (null != musicBeans) sysMusics.addAll(musicBeans);
                        latch.countDown();
                    }
                });
                // load user music
                MusicApi.loadMusics(roomId, MUSIC_TYPE_USER, new IResultBack<List<MusicBean>>() {
                    @Override
                    public void onResult(List<MusicBean> musicBeans) {
                        userMusics.clear();
                        if (null != musicBeans) userMusics.addAll(musicBeans);
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (Exception e) {
                }
                //handle state：playing addAlready
                for (MusicBean sys : sysMusics) {
                    sys.setAddAlready(userMusics.contains(sys));
                }
                if (currentMusicState == RCRTCAudioMixer.MixingState.PLAY && !TextUtils.isEmpty(currentPlayMusicUrl)) {
                    for (MusicBean use : userMusics) {
                        use.setPlaying(use.getUrl().equals(currentPlayMusicUrl));
                    }
                }
                if (null != resultBack) resultBack.onResult(null);
                UIKit.runOnUiTherad(new Runnable() {
                    @Override
                    public void run() {
                        for (VRMusicListener l : listeners) {
                            l.onMusics(sysMusics, userMusics);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void stopPlayMusic() {
        musicStopFlag = true;
        RCRTCAudioMixer.getInstance().stop();
    }

    /**
     * 按播放列表播放
     */
    private void playNextMusic() {
        if (null == userMusics || userMusics.isEmpty()) {
            return;
        }
        MusicBean next = null;
        int count = userMusics.size();
        if (TextUtils.isEmpty(currentPlayMusicUrl)) {
            next = userMusics.get(0);
        } else {
            for (int i = 0; i < count; i++) {
                if (currentPlayMusicUrl.equals(userMusics.get(i).getUrl())) {
                    int nextIndex = i + 1;
                    if (nextIndex >= count) nextIndex = 0;
                    Logger.e(TAG, "next index = " + nextIndex);
                    next = userMusics.get(nextIndex);
                    break;
                }
            }
        }
        if (null != next) {
            playMusic(next);
        }
    }

    /**
     * 1.download
     * 2.play
     *
     * @param music 音乐对象
     */
    private void playMusic(MusicBean music) {
        musicStopFlag = false;
        MusicApi.downloadMusic(music.getUrl(), music.getName(), new IResultBack<File>() {
            @Override
            public void onResult(File file) {
                if (null == file || !file.exists()) return;
                currentPlayMusicUrl = music.getUrl();
                RCRTCAudioMixer.getInstance()
                        .startMix(file.getAbsolutePath(), RCRTCAudioMixer.Mode.MIX, true, 1);
                UIKit.runOnUiTherad(new Runnable() {
                    @Override
                    public void run() {
                        for (VRMusicListener l : listeners) {
                            l.onPlayState(currentPlayMusicUrl);
                        }
                    }
                });
            }
        });
    }

    /**
     * 切换播放状态
     *
     * @param music 音乐对象
     */
    @Override
    public void switchMusicPlayState(MusicBean music) {
        if (!TextUtils.isEmpty(currentPlayMusicUrl) && currentPlayMusicUrl.equals(music.getUrl())
                && (currentMusicState == RCRTCAudioMixer.MixingState.PAUSED ||
                currentMusicState == RCRTCAudioMixer.MixingState.PLAY)) {
            if (currentMusicState == RCRTCAudioMixer.MixingState.PAUSED) {
                RCRTCAudioMixer.getInstance().resume();
                music.setPlaying(true);
            } else if (currentMusicState == RCRTCAudioMixer.MixingState.PLAY) {
                RCRTCAudioMixer.getInstance().pause();
                music.setPlaying(false);
            }
            UIKit.runOnUiTherad(new Runnable() {
                @Override
                public void run() {
                    for (VRMusicListener l : listeners) {
                        l.onPlayState(currentPlayMusicUrl);
                    }
                }
            });
        } else {
            Logger.e(TAG, "switchMusicPlayState 2");
            // 暂停旧的
            if (!TextUtils.isEmpty(currentPlayMusicUrl)) {
                stopPlayMusic();
            }
            // 播放新的
            Message msg = Message.obtain();
            msg.what = WHAT_PLAY;
            msg.obj = music;
            handler.sendMessage(msg);
        }
    }

    /**
     * 添加 只有一个音乐 直接播放
     *
     * @param music 音乐对象
     */
    public void addMusic(MusicBean music, IResultBack<Boolean> resultBack) {
        MusicApi.addMusic(roomId, music, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                KToast.show("添加音乐" + (aBoolean ? "成功" : "失败"));
                if (aBoolean) loadMusic(new IResultBack() {
                    @Override
                    public void onResult(Object o) {
                        if (userMusics.size() == 1) {
                            Logger.d(TAG, "addMusic: list only one music,start play");
                            Message msg = Message.obtain();
                            msg.what = WHAT_PLAY;
                            msg.obj = userMusics.get(0);
                            handler.sendMessage(msg);
                        }
                    }
                });
                if (null == resultBack) resultBack.onResult(aBoolean);
            }
        });
    }

    /**
     * 删除
     *
     * @param music      音乐对象
     * @param resultBack 回调
     */
    public void deleteMusic(MusicBean music, IResultBack<Boolean> resultBack) {
        MusicApi.deleteMusic(roomId, music.getId(), new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                KToast.show("删除音乐" + (aBoolean ? "成功" : "失败"));
                // 删除的是正在播放，暂停
                if (!TextUtils.isEmpty(currentPlayMusicUrl) && currentPlayMusicUrl.equals(music.getUrl())) {
                    stopPlayMusic();
                }
                if (aBoolean) loadMusic(null);
                if (null == resultBack) resultBack.onResult(aBoolean);
            }
        });
    }

    /**
     * 置顶
     *
     * @param music      音乐
     * @param resultBack 回调
     */
    public void moveTop(MusicBean music, IResultBack<Boolean> resultBack) {
        if (TextUtils.isEmpty(music.getUrl())) {
            if (null != resultBack) resultBack.onResult(false);
            return;
        }
        // play
        if (TextUtils.isEmpty(currentPlayMusicUrl) || currentMusicState == RCRTCAudioMixer.MixingState.PAUSED) {
            stopPlayMusic();
            Message msg = Message.obtain();
            msg.what = WHAT_PLAY;
            msg.obj = music;//播放当前
            handler.sendMessage(msg);
        }
        int toId = 0;
        if (!TextUtils.isEmpty(currentPlayMusicUrl)) {
            for (MusicBean m : userMusics) {
                if (currentPlayMusicUrl.equals(m.getUrl())) {
                    toId = m.getId();
                    break;
                }
            }
        }
        MusicApi.moveMusic(roomId, music.getId(), toId, new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                KToast.show("置顶" + (aBoolean ? "成功" : "失败"));
                if (aBoolean) loadMusic(null);
                if (null != resultBack) resultBack.onResult(aBoolean);
            }
        });
    }

    /**
     * 添加本地音乐
     *
     * @param uri        本地文件uri
     * @param loadTag    wait dialog
     * @param resultBack 回调
     */
    public void addMusicByUri(Uri uri, ILoadTag loadTag, IResultBack<Boolean> resultBack) {
        String realPath = RealPathFromUriUtils.getRealPathFromUri(UIKit.getContext(), uri);
        Logger.e(TAG, "addMusicByUri: realPath = " + realPath);
        if (TextUtils.isEmpty(realPath)) {
            KToast.show("文件选择异常");
            if (null != resultBack) resultBack.onResult(false);
            return;
        }
        if (!realPath.endsWith("mp3")
                && !realPath.endsWith("aac")
                && !realPath.endsWith("m4a")
                && !realPath.endsWith("wav")
                && !realPath.endsWith("ogg")
                && !realPath.endsWith("amr")
        ) {
            KToast.show("仅支持 MP3、AAC、M4A、WAV、OGG、AMR 格式文件");
            if (null != resultBack) resultBack.onResult(false);
            return;
        }
        if (null != loadTag) loadTag.show();
        MusicApi.uploadMusicFile(realPath, new IResultBack<String>() {
            @Override
            public void onResult(String url) {
                if (null != loadTag) loadTag.dismiss();
                if (TextUtils.isEmpty(url)) {
                    KToast.show("文件上传失败");
                    if (null != resultBack) resultBack.onResult(false);
                    return;
                }
                // 查询信息
                String author = "无";
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try {
                    retriever.setDataSource(realPath);
                    author = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                } catch (Exception e) {
                    Logger.e(TAG, "e = " + e);
                } finally {
                    retriever.release();
                }
                File file = new File(realPath);
                String name = file.getName();
                name = name.substring(0, name.lastIndexOf("."));
                MusicBean bean = new MusicBean();
                bean.setType(MUSIC_TYPE_USER);
                bean.setName(name);
                bean.setUrl(url);
                bean.setAuthor(author);
                bean.setSize((int) (file.length() / 1024 / 10.24) / 100.0);// 转换单位：2.15M
                // add music
                addMusic(bean, resultBack);
            }
        });
    }

    @Override
    public boolean isPlaying() {
        return currentMusicState == RCRTCAudioMixer.MixingState.PLAY;
    }

    public void addMusicListenre(VRMusicListener listener) {
        if (null != listeners && !listeners.contains(listener)) {
            listeners.add(listener);
        }
        // 首次执行
        listener.onPlayState(currentPlayMusicUrl);
        listener.onMusics(sysMusics, userMusics);
    }

}