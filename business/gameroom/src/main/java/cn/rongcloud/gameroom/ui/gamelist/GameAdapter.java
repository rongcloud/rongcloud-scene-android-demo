package cn.rongcloud.gameroom.ui.gamelist;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;

import com.basis.utils.ImageLoader;
import com.basis.utils.UIKit;
import com.basis.utils.UiUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import cn.rongcloud.gamelib.model.RCGameInfo;
import cn.rongcloud.gameroom.R;

/**
 * @author gyn
 * @date 2022/3/17
 */
public class GameAdapter extends BaseQuickAdapter<RCGameInfo, BaseViewHolder> {

    private RCGameInfo mGameInfo;
    private int nameColor;
    private int itemWith;

    public GameAdapter() {
        super(R.layout.item_game);
        nameColor = UIKit.getContext().getResources().getColor(R.color.game_color_text_game_name);
        itemWith = (int) ((UiUtils.getScreenWidth(UIKit.getContext()) - UiUtils.dp2px(14)) / 4.5f);
    }

    public void setGameInfo(RCGameInfo mGameInfo) {
        if (this.mGameInfo != mGameInfo) {
            this.mGameInfo = mGameInfo;
            notifyDataSetChanged();
        }
    }

    public void setNameColor(int nameColor) {
        this.nameColor = nameColor;
        notifyDataSetChanged();
    }

    public RCGameInfo getGameInfo() {
        return mGameInfo;
    }

    @Override
    protected void onItemViewHolderCreated(@NonNull BaseViewHolder viewHolder, int viewType) {
        super.onItemViewHolderCreated(viewHolder, viewType);
        ViewGroup viewGroup = viewHolder.getView(R.id.cl_game_root);
        LayoutParams params = viewGroup.getLayoutParams();
        params.width = itemWith;
        viewGroup.setLayoutParams(params);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, RCGameInfo gameInfo) {
        baseViewHolder.setText(R.id.tv_game_name, gameInfo.getGameName());
        baseViewHolder.setTextColor(R.id.tv_game_name, nameColor);
        ImageLoader.loadUrl(baseViewHolder.getView(R.id.iv_game_icon), gameInfo.getThumbnail(), R.color.game_color_user_num);
        baseViewHolder.setVisible(R.id.iv_select, mGameInfo != null && TextUtils.equals(gameInfo.getGameId(), mGameInfo.getGameId()));
    }

}


