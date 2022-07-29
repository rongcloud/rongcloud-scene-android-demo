package cn.rongcloud.qnplayer;

/**
 * 自定义播放接口
 */
public interface IPlayer {

    void start(String url);

    void pause();

    void resume();

    void stop();

    void release();
}
