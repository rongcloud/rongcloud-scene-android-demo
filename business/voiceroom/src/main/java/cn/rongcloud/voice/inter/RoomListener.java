package cn.rongcloud.voice.inter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;

/**
 * 房间监听
 */
public interface RoomListener {
    /**
     * 房间信息
     *
     * @param roomInfo
     */
    void onRoomInfo(@NonNull RCVoiceRoomInfo roomInfo);

    /**
     * 房间麦位列表
     *
     * @param seatInfos
     */
    void onSeatList(@NonNull List<RCVoiceSeatInfo> seatInfos);

    /**
     * notify
     *
     * @param code
     * @param content
     */
    void onNotify(String code, String content);

    /**
     * 在线人ID回调
     *
     * @param userIds
     */
    void onOnLineUserIds(@Nullable List<String> userIds);
}
