package cn.rongcloud.qnplayer;

import android.util.Log;

import com.basis.utils.Logger;
import com.pili.pldroid.player.PLOnBufferingUpdateListener;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnPreparedListener;

public class PlayListener implements PLOnBufferingUpdateListener, PLOnPreparedListener, PLOnCompletionListener, PLOnInfoListener, PLOnErrorListener {
    String TAG = "QnPlayer_PlayListener";

    private final QnPlayer player;

    public PlayListener(QnPlayer player) {
        this.player = player;
    }

    @Override
    public void onBufferingUpdate(int i) {
    }

    @Override
    public void onCompletion() {
    }

    @Override
    public boolean onError(int i, Object o) {
        Logger.e(TAG, "onError: [" + i + "] msg = " + o);
        if (PLOnErrorListener.ERROR_CODE_OPEN_FAILED == i) {
            player.stop();
            player.tryStartAgain();
        }
        return false;
    }

    @Override
    public void onInfo(int i, int i1, Object o) {

    }

    @Override
    public void onPrepared(int preparedTime) {
        Log.i(TAG, "On Prepared  preparedTime = " + preparedTime);
        player.resume();
    }
}
