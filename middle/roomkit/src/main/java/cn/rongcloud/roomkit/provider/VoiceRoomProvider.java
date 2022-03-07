package cn.rongcloud.roomkit.provider;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.GsonUtil;
import com.basis.wapper.IResultBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.config.provider.wrapper.AbsProvider;
import cn.rongcloud.roomkit.manager.LocalDataManager;
import cn.rongcloud.roomkit.ui.RoomOwnerType;
import cn.rongcloud.roomkit.ui.RoomType;
import io.rong.imlib.model.UserInfo;

public class VoiceRoomProvider extends AbsProvider<VoiceRoomBean> implements IListProvider<VoiceRoomBean> {
    private final static String API_ROOM = ApiConfig.HOST + "mic/room/";
    private final static String API_ROOMS = ApiConfig.HOST + "mic/room/list";
    private final static VoiceRoomProvider _provider = new VoiceRoomProvider();
    private List<String> bgImages = new ArrayList<>();
    private int page = 1;

    private VoiceRoomProvider() {
        super(-1);
    }

    public static VoiceRoomProvider provider() {
        return _provider;
    }

    @Override
    public void provideFromService(@NonNull List<String> ids, @Nullable IResultBack<List<VoiceRoomBean>> resultBack) {
        if (null == ids || ids.isEmpty()) {
            if (null != resultBack) resultBack.onResult(new ArrayList<>());
            return;
        }
        // TODO
        String roomId = ids.get(0);
        OkApi.get(API_ROOM + roomId, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                Log.e(TAG, GsonUtil.obj2Json(result));
                List<VoiceRoomBean> rooms = result.getList(VoiceRoomBean.class);
                if (null != resultBack) resultBack.onResult(rooms);

            }

            @Override
            public void onError(int code, String msg) {
                if (null != resultBack) resultBack.onResult(null);
            }
        });
    }

    @Override
    protected void onUpdateComplete(List<VoiceRoomBean> voiceRoomBeans) {
        List<UserInfo> users = new ArrayList<>();
        int count = voiceRoomBeans.size();
        for (int i = 0; i < count; i++) {
            if (voiceRoomBeans.get(i).getCreateUser() != null) {
                users.add(voiceRoomBeans.get(i).getCreateUser().toUserInfo());
            }
        }
        UserProvider.provider().update(users);
    }

    public List<String> getImages() {
        return new ArrayList<>(bgImages);
    }

    @Override
    public void loadPage(boolean isRefresh, RoomType roomType, IResultBack<List<VoiceRoomBean>> resultBack) {
        if (isRefresh) {
            page = 1;
        }
        if (page < 1) page = 1;
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", PAGE_SIZE);
        params.put("type", roomType.getType());//1 聊天室(默认) 2 电台
        OkApi.get(API_ROOMS, params, new WrapperCallBack() {
            @Override
            public void onError(int code, String msg) {
                if (null != resultBack) resultBack.onResult(null);
            }

            @Override
            public void onResult(Wrapper wrapper) {
                List<VoiceRoomBean> rooms = wrapper.getList("rooms", VoiceRoomBean.class);
                List<String> images = wrapper.getList("images", String.class);
                if (null != images && !images.isEmpty()) {
                    bgImages.clear();
                    bgImages.addAll(images);
                    LocalDataManager.saveBackGroundUrl(images);
                }
                Log.e(TAG, "provideFromService: size = " + (null == rooms ? 0 : rooms.size()));
                updateCache(rooms);
                if (rooms != null && !rooms.isEmpty()) {
                    page++;
                }
                if (null != resultBack) resultBack.onResult(rooms);
            }
        });
    }

    public int getPage() {
        return page;
    }

    /**
     * 获取房间类型
     *
     * @param voiceRoomBean 当前房间
     * @return 房间类型
     */
    public RoomOwnerType getRoomOwnerType(VoiceRoomBean voiceRoomBean) {
        if (voiceRoomBean == null || voiceRoomBean.getCreateUser() == null) {
            throw new NullPointerException("VoiceRoomBean is null");
        }
        String userId = UserManager.get().getUserId();
        if (TextUtils.equals(userId, voiceRoomBean.getCreateUser().getUserId())) {
            if (voiceRoomBean.getRoomType() == RoomType.VOICE_ROOM.getType()) {
                return RoomOwnerType.VOICE_OWNER;
            } else if (voiceRoomBean.getRoomType() == RoomType.LIVE_ROOM.getType()) {
                return RoomOwnerType.LIVE_OWNER;
            } else {
                return RoomOwnerType.RADIO_OWNER;
            }
        } else {
            if (voiceRoomBean.getRoomType() == RoomType.VOICE_ROOM.getType()) {
                return RoomOwnerType.VOICE_VIEWER;
            } else if (voiceRoomBean.getRoomType() == RoomType.LIVE_ROOM.getType()) {
                return RoomOwnerType.LIVE_VIEWER;
            } else {
                return RoomOwnerType.RADIO_VIEWER;
            }
        }
    }
}
