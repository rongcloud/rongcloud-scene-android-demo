package cn.rongcloud.gameroom.ui.gamelist;

import androidx.annotation.NonNull;

import com.basis.utils.ImageLoader;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import cn.rongcloud.config.provider.user.Sex;
import cn.rongcloud.gameroom.R;
import cn.rongcloud.gameroom.model.GameRoomBean;

/**
 * @author gyn
 * @date 2022/3/17
 */
public class GameRoomAdapter extends BaseQuickAdapter<GameRoomBean, BaseViewHolder> implements LoadMoreModule {
    public GameRoomAdapter() {
        super(R.layout.item_game_room);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, GameRoomBean gameRoomBean) {
        baseViewHolder.setText(R.id.tv_room_name, gameRoomBean.getRoomName());
        ImageLoader.loadUrl(baseViewHolder.getView(R.id.iv_avatar), gameRoomBean.getCreateUserPortrait(), R.drawable.default_portrait);
        baseViewHolder.setText(R.id.tv_count, gameRoomBean.getUserTotal() + "");
        baseViewHolder.setVisible(R.id.iv_lock, gameRoomBean.isPrivate());
        baseViewHolder.getView(R.id.iv_gender).setSelected(gameRoomBean.getCreateUser().getSex() == Sex.woman);
        if (gameRoomBean.getGameInfo() != null) {
            ImageLoader.loadUrl(baseViewHolder.getView(R.id.iv_game_icon), gameRoomBean.getGameInfo().getThumbnail(), R.color.game_bg_page);
            baseViewHolder.setText(R.id.tv_game_name, gameRoomBean.getGameInfo().getGameName());
        }
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }
}
