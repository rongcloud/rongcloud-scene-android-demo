package cn.rongcloud.radio.helper;

import cn.rongcloud.qnplayer.QnPlayer;
import cn.rongcloud.radioroom.room.IPlayer;

/**
 * 两个IPlayer的转换包装类
 */
public class Player implements IPlayer {
    private final static IPlayer _instance = new Player();

    private final cn.rongcloud.qnplayer.IPlayer _player;

    private Player() {
        _player = new QnPlayer();
    }

    public static IPlayer getPlayer() {
        return _instance;
    }

    @Override
    public void start(String url) {
        _player.start(url);
    }

    @Override
    public void pause() {
        _player.pause();
    }

    @Override
    public void resume() {
        _player.resume();
    }

    @Override
    public void stop() {
        _player.stop();
    }

    @Override
    public void release() {
        _player.release();
    }
}
