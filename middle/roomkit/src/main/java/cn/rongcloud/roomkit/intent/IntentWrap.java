package cn.rongcloud.roomkit.intent;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;

import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.roomkit.ui.RoomType;


/**
 * @author gyn
 * @date 2021/10/21
 */
public class IntentWrap {
    // keys
    public static final String KEY_ROOM_IDS = "KEY_ROOM_IDS";
    public static final String KEY_IS_CREATE = "KEY_IS_CREATE";
    public static final String KEY_ROOM_POSITION = "KEY_ROOM_POSITION";
    // actions
    private static final String ACTION_RADIO_ROOM = ".RadioRoomActivity";
    private static final String ACTION_VOICE_ROOM = ".VoiceRoomActivity";
    private static final String ACTION_LIVE_ROOM = ".LiveRoomActivity";

    public static String getRadioRoomAction(Context context) {
        return context.getPackageName() + ACTION_RADIO_ROOM;
    }

    public static String getVoiceRoomAction(Context context) {
        return context.getPackageName() + ACTION_VOICE_ROOM;
    }

    public static String getLiveRoomAction(Context context) {
        return context.getPackageName() + ACTION_LIVE_ROOM;
    }

    /**
     * 打开电台房间
     *
     * @param context  context
     * @param roomIds  房间列表id
     * @param position 打开的房间的位置
     */
    public static void launchRadioRoom(Context context, ArrayList<String> roomIds, int position) {
        ARouter.getInstance().build(RouterPath.ROUTER_RADIO_ROOM)
                .withStringArrayList(KEY_ROOM_IDS, roomIds)
                .withInt(KEY_ROOM_POSITION, position)
                .navigation();
    }

    /**
     * 打开语聊房
     *
     * @param context  context
     * @param roomIds  房间列表id
     * @param position 打开的房间的位置
     * @param isCreate 是否是创建
     */
    public static void launchVoiceRoom(Context context, ArrayList<String> roomIds, int position, boolean isCreate) {
        ARouter.getInstance().build(RouterPath.ROUTER_VOICE_ROOM)
                .withStringArrayList(KEY_ROOM_IDS, roomIds)
                .withInt(KEY_ROOM_POSITION, position)
                .withBoolean(KEY_IS_CREATE, isCreate)
                .navigation();
    }

    /**
     * 打开直播房
     */
    public static void launchLiveRoom(Context context, ArrayList<String> roomIds, int position, boolean isCreate) {
        ARouter.getInstance().build(RouterPath.ROUTER_LIVE_ROOM)
                .withStringArrayList(KEY_ROOM_IDS, roomIds)
                .withInt(KEY_ROOM_POSITION, position)
                .withBoolean(KEY_IS_CREATE, isCreate)
                .navigation();
    }


    /**
     * 根据房间类型，id，跳转到相应的房间
     *
     * @param context
     * @param roomType
     * @param roomId
     */
    public static void launchRoom(Context context, int roomType, String roomId) {
        if (roomType == RoomType.RADIO_ROOM.getType()) {
            ArrayList<String> ids = new ArrayList<>();
            ids.add(roomId);
            launchRadioRoom(context, ids, 0);
        } else if (roomType == RoomType.VOICE_ROOM.getType()) {
            ArrayList<String> ids = new ArrayList<>();
            ids.add(roomId);
            launchVoiceRoom(context, ids, 0, false);
        } else if (roomType == RoomType.LIVE_ROOM.getType()) {
            ArrayList<String> ids = new ArrayList<>();
            ids.add(roomId);
            launchLiveRoom(context, ids, 0, false);
        }
    }

    /**
     * 根据房间类型，id，跳转到相应的房间
     *
     * @param context
     * @param roomType
     * @param roomIds
     * @param position
     * @param isCreate
     */
    public static void launchRoom(Context context, RoomType roomType, ArrayList<String> roomIds, int position, boolean isCreate) {
        if (roomType == RoomType.RADIO_ROOM) {
            launchRadioRoom(context, roomIds, position);
        } else if (roomType == RoomType.VOICE_ROOM) {
            launchVoiceRoom(context, roomIds, position, isCreate);
        } else if (roomType == RoomType.LIVE_ROOM) {
            launchLiveRoom(context, roomIds, position, isCreate);
        }
    }
}
