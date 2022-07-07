package cn.rongcloud.gameroom.ui.gamelist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.KToast;
import com.basis.utils.UIKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.gamelib.model.RCGameInfo;
import cn.rongcloud.gameroom.R;
import cn.rongcloud.gameroom.api.GameApi;
import cn.rongcloud.gameroom.model.GameCreateBean;
import cn.rongcloud.gameroom.model.GameRoomBean;
import cn.rongcloud.gameroom.model.GameRoomListBean;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.ui.RoomType;
import io.rong.imkit.picture.tools.ToastUtils;

/**
 * @author gyn
 * @date 2022/5/13
 */
public class GameRoomListViewModel extends ViewModel {
    private static final int PAGE_SIZE = 10;
    private int page = 1;
    public MutableLiveData<GameRoomListBean> gameRoomList = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    public MutableLiveData<GameCreateBean> gameCreateBean = new MutableLiveData<>();
    // public MutableLiveData<GameCreateBean> launchRoom = new MutableLiveData<>();


    public void loadRoomList(boolean isRefresh, String gender, String gameId) {
        if (isRefresh) {
            page = 1;
        }
        if (page < 1) page = 1;
        Map<String, Object> params = OkParams.Builder()
                .add("page", page)
                .add("size", PAGE_SIZE)
                .add("type", RoomType.GAME_ROOM.getType())
                .add("sex", gender)
                .add("gameId", gameId)
                .build();
        OkApi.get(GameApi.ROOM_LIST, params, new WrapperCallBack() {
            @Override
            public void onError(int code, String msg) {
                gameRoomList.setValue(new GameRoomListBean(page, null));
            }

            @Override
            public void onResult(Wrapper wrapper) {
                List<GameRoomBean> rooms = wrapper.getList("rooms", GameRoomBean.class);
                int oldPage = page;
                if (rooms != null && !rooms.isEmpty()) {
                    page++;
                }
                gameRoomList.setValue(new GameRoomListBean(oldPage, rooms));
            }
        });
    }

    /**
     * 快速匹配
     *
     * @param gameInfo
     */
    public void fastJoin(RCGameInfo gameInfo) {
        OkApi.get(GameApi.GAME_FAST_JOIN, OkParams.Builder().add("gameId", gameInfo.getGameId()).build(), new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    GameRoomBean gameRoomBean = result.get(GameRoomBean.class);
                    if (gameRoomBean == null) {
                        createGameRoom(gameInfo, UIKit.getContext().getString(R.string.game_text_default_room_title), true);
                    } else {
                        gameCreateBean.setValue(new GameCreateBean(false, gameRoomBean, true));
                    }
                } else if (result.getCode() == 30001) {
                    createGameRoom(gameInfo, UIKit.getContext().getString(R.string.game_text_default_room_title), true);
                } else {
                    KToast.show(result.getMessage());
                }
            }
        });
    }

    public void createRoom() {
        loading.setValue(true);
        // 创建之前检查是否已有创建的房间
        OkApi.put(
                VRApi.ROOM_CREATE_CHECK,
                OkParams.Builder().add("roomType", RoomType.GAME_ROOM.getType()).build(),
                new WrapperCallBack() {
                    @Override
                    public void onResult(Wrapper result) {
                        loading.setValue(false);
                        if (result.ok()) {
                            gameCreateBean.setValue(null);
                        } else if (result.getCode() == 30016) {
                            GameRoomBean gameRoomBean = result.get(GameRoomBean.class);
                            if (gameRoomBean != null) {
                                gameCreateBean.setValue(new GameCreateBean(false, gameRoomBean, false));
                            } else {
                                gameCreateBean.setValue(null);
                            }
                        }
                    }
                });
    }

    public void createGameRoom(RCGameInfo gameInfo, String name, boolean isFastIn) {
        loading.setValue(true);
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("themePictureUrl", "");
        params.put("isPrivate", 0);
        params.put("password", "");
        params.put("backgroundUrl", gameInfo.getLoadingPic());
        params.put("kv", new ArrayList());
        params.put("gameId", gameInfo.getGameId());
        params.put("roomType", RoomType.GAME_ROOM.getType());
        OkApi.post(VRApi.ROOM_CREATE, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                GameRoomBean gameRoomBean = result.get(GameRoomBean.class);
                if (result.ok() && gameRoomBean != null) {
                    // 创建成功
                    gameCreateBean.setValue(new GameCreateBean(true, gameRoomBean, isFastIn, gameInfo.getGameId()));
                } else if (30016 == result.getCode() && gameRoomBean != null) {
                    // 已经创建过房间
                    gameCreateBean.setValue(new GameCreateBean(false, gameRoomBean, isFastIn, gameInfo.getGameId()));
                } else {
                    ToastUtils.s(UIKit.getContext(), result.getMessage());
                }
                loading.setValue(false);
            }

            @Override
            public void onError(int code, String msg) {
                super.onError(code, msg);
                ToastUtils.s(UIKit.getContext(), msg);
                loading.setValue(false);
            }
        });
    }
}
