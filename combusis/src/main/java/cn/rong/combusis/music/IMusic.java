package cn.rong.combusis.music;

import android.net.Uri;

import com.basis.net.oklib.wrapper.interfaces.ILoadTag;

import java.util.List;

import cn.rong.combusis.common.net.IResultBack;
import cn.rong.combusis.music.domain.MusicBean;

public interface IMusic {
    /**
     * 初始化
     *
     * @param roomId 房间id
     */
    void init(String roomId);

    /**
     * 反初始化
     */
    void unInit();

    /**
     * 添加音乐监听
     *
     * @param listener
     */
    void addMusicListenre(VRMusicListener listener);

    /**
     * 停止播放
     */
    void stopPlayMusic();

    /**
     * 切换播放状态
     *
     * @param music
     */
    void switchMusicPlayState(MusicBean music);

    /**
     * 添加音乐
     *
     * @param music      音乐对象
     * @param resultBack 回调
     */
    void addMusic(MusicBean music, IResultBack<Boolean> resultBack);

    /**
     * 删除音乐
     *
     * @param music      音乐对象
     * @param resultBack 回调
     */
    void deleteMusic(MusicBean music, IResultBack<Boolean> resultBack);

    /**
     * 音乐置顶
     *
     * @param music      音乐
     * @param resultBack 回调
     */
    void moveTop(MusicBean music, IResultBack<Boolean> resultBack);

    /**
     * 添加本地音
     *
     * @param uri        本地文件uri
     * @param loadTag    wait dialog
     * @param resultBack 回调
     */
    void addMusicByUri(Uri uri, ILoadTag loadTag, IResultBack<Boolean> resultBack);

    /**
     * 是否播放
     *
     * @return
     */
    boolean isPlaying();

    interface VRMusicListener {
        /**
         * 音乐集合监听
         *
         * @param userMusics 用户添加
         * @param sysMusics  系统内置
         */
        void onMusics(List<MusicBean> sysMusics, List<MusicBean> userMusics);

        /**
         * 播放状态回调
         *
         * @param url
         */
        void onPlayState(String url);
    }
}
