package cn.rongcloud.gameroom.ui.gameroom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.List;

import cn.rongcloud.gamelib.model.RCGameInfo;
import cn.rongcloud.gameroom.R;
import cn.rongcloud.gameroom.api.GameApi;
import cn.rongcloud.gameroom.ui.gamelist.GameAdapter;


/**
 * @author gyn
 * @date 2021/9/24
 */
public class SwitchGamePopupWindow extends PopupWindow {
    private View mRootView;

    public SwitchGamePopupWindow(Context context, OnGameClickListener onGameClickListener) {
        super(context);
        mRootView = LayoutInflater.from(context).inflate(R.layout.game_popup_switch_game, null, false);
        setContentView(mRootView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setClippingEnabled(false);
        RecyclerView rvGameList = mRootView.findViewById(R.id.rv_game_list);
        GameAdapter gameAdapter = new GameAdapter();
        rvGameList.setAdapter(gameAdapter);
        gameAdapter.setNameColor(Color.WHITE);
        gameAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (onGameClickListener != null) {
                    onGameClickListener.switchGame(gameAdapter.getItem(position));
                    dismiss();
                }
            }
        });
        OkApi.get(GameApi.GAME_LIST, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                List<RCGameInfo> gameInfoList = result.getList(RCGameInfo.class);
                if (gameInfoList != null) {
                    gameAdapter.setNewInstance(gameInfoList);
                }
            }
        });
    }

    public interface OnGameClickListener {
        void switchGame(RCGameInfo gameInfo);
    }
}
