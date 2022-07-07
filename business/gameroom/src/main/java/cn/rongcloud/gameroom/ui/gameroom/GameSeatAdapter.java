package cn.rongcloud.gameroom.ui.gameroom;

import androidx.annotation.NonNull;

import com.basis.utils.ImageLoader;
import com.basis.wapper.IResultBack;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.gameroom.R;
import cn.rongcloud.gameroom.model.SeatPlayer;
import cn.rongcloud.roomkit.ui.room.widget.WaveView;
import io.rong.imlib.model.UserInfo;

/**
 * @author gyn
 * @date 2022/5/7
 */
public class GameSeatAdapter extends BaseQuickAdapter<SeatPlayer, BaseViewHolder> {

    public GameSeatAdapter() {
        super(R.layout.game_item_seat);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, SeatPlayer seatPlayer) {
        baseViewHolder.setVisible(R.id.tv_captain, seatPlayer.isCaptain);
        baseViewHolder.setVisible(R.id.iv_mute, seatPlayer.isMute);
        baseViewHolder.setText(R.id.tv_state, seatPlayer.playerState.desc);
        baseViewHolder.setVisible(R.id.tv_state, seatPlayer.playerState == SeatPlayer.PlayerState.PLAY || seatPlayer.playerState == SeatPlayer.PlayerState.READY);
        if (seatPlayer.playerState == SeatPlayer.PlayerState.PLAY) {
            baseViewHolder.setBackgroundResource(R.id.tv_state, R.drawable.game_bg_seat_playing);
        } else if (seatPlayer.playerState == SeatPlayer.PlayerState.READY) {
            baseViewHolder.setBackgroundResource(R.id.tv_state, R.drawable.game_bg_seat_ready);
        }

        if (seatPlayer.playerState == SeatPlayer.PlayerState.EMPTY) {
            if (seatPlayer.isLock) {
                baseViewHolder.setImageResource(R.id.iv_avatar, R.drawable.game_ic_seat_lock);
            } else {
                baseViewHolder.setImageResource(R.id.iv_avatar, R.drawable.game_ic_seat_empty);
            }
            baseViewHolder.setBackgroundResource(R.id.iv_avatar, R.color.transparent);
        } else {
            baseViewHolder.setBackgroundResource(R.id.iv_avatar, R.drawable.game_bg_item_seat);
            UserProvider.provider().getAsyn(seatPlayer.userId, new IResultBack<UserInfo>() {
                @Override
                public void onResult(UserInfo userInfo) {
                    ImageLoader.loadUrl(baseViewHolder.getView(R.id.iv_avatar), userInfo.getPortraitUri().toString(), R.drawable.default_portrait);
                }
            });
        }
        if (seatPlayer.playerState != SeatPlayer.PlayerState.EMPTY && seatPlayer.isSpeaking && !seatPlayer.isMute) {
            ((WaveView) (baseViewHolder.getView(R.id.wv_seat_background))).start();
        } else {
            ((WaveView) (baseViewHolder.getView(R.id.wv_seat_background))).stop();
        }
    }

    public void refreshSpeaking(int seatIndex, boolean isSpeaking) {
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).seatIndex == seatIndex) {
                getData().get(i).isSpeaking = isSpeaking;
                notifyItemChanged(i);
                break;
            }
        }
    }
}
