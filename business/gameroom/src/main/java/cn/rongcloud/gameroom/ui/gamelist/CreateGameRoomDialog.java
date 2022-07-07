package cn.rongcloud.gameroom.ui.gamelist;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.ui.BaseBottomSheetDialog;
import com.basis.utils.KToast;
import com.basis.utils.UiUtils;
import com.basis.widget.ChineseLengthFilter;
import com.basis.widget.decoration.SpaceItemDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.vanniktech.emoji.EmojiEditText;

import java.util.List;

import cn.rongcloud.gamelib.model.RCGameInfo;
import cn.rongcloud.gameroom.R;

/**
 * @author gyn
 * @date 2022/3/18
 */
public class CreateGameRoomDialog extends BaseBottomSheetDialog {

    private TextView tvSelectGame;
    private RecyclerView rvGameList;
    private TextView tvRoomName;
    private EmojiEditText etInput;

    private GameAdapter gameAdapter;
    private FrameLayout flCreateRoom;
    private List<RCGameInfo> gameInfoList;
    private OnCreateGameRoomListener onCreateGameRoomListener;

    public CreateGameRoomDialog(List<RCGameInfo> gameInfoList, OnCreateGameRoomListener onCreateGameRoomListener) {
        super(R.layout.game_dialog_create_room);
        this.gameInfoList = gameInfoList;
        this.onCreateGameRoomListener = onCreateGameRoomListener;
    }

    @Override
    public void initView() {
        tvSelectGame = (TextView) getView().findViewById(R.id.tv_select_game);
        rvGameList = (RecyclerView) getView().findViewById(R.id.rv_game_list);
        tvRoomName = (TextView) getView().findViewById(R.id.tv_room_name);
        etInput = (EmojiEditText) getView().findViewById(R.id.et_input);
        flCreateRoom = (FrameLayout) getView().findViewById(R.id.fl_create_room);
        etInput.setFilters(new InputFilter[]{new ChineseLengthFilter(20)});

        gameAdapter = new GameAdapter();
        rvGameList.setAdapter(gameAdapter);
        int space = UiUtils.dp2px(20);
        rvGameList.addItemDecoration(SpaceItemDecoration.Builder.start().setStartSpace(space).setEndSpace(space).build());
        gameAdapter.setNewInstance(gameInfoList);
        gameAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                gameAdapter.setGameInfo(gameInfoList.get(position));
            }
        });

        flCreateRoom.setOnClickListener(v -> {
            RCGameInfo gameInfo = gameAdapter.getGameInfo();
            if (gameInfo == null) {
                KToast.show(getString(R.string.game_text_please_select_game));
                return;
            }
            String name = etInput.getText() == null ? "" : etInput.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                KToast.show(getString(R.string.game_text_please_input_room_name));
                return;
            }
            if (onCreateGameRoomListener != null)
                onCreateGameRoomListener.onCreateGameRoom(gameInfo, name);
            dismiss();
        });
    }

    public interface OnCreateGameRoomListener {
        void onCreateGameRoom(RCGameInfo gameInfo, String name);
    }
}
