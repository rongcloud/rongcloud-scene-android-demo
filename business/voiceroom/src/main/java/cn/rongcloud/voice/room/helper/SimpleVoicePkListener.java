package cn.rongcloud.voice.room.helper;

import com.basis.utils.Logger;
import com.basis.wapper.IResultBack;

import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.pk.api.PKListener;
import cn.rongcloud.pk.bean.PKResponse;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * @author gyn
 * @date 2022/1/17
 */
public interface SimpleVoicePkListener extends PKListener {

    @Override
    default void lockAllAndKickOut() {
        RCVoiceRoomInfo roomInfo = VoiceEventHelper.helper().getRoomInfo();
        if (null != roomInfo) {
            int seatCount = roomInfo.getSeatCount();
            for (int i = 1; i < seatCount; i++) {
                RCVoiceRoomEngine.getInstance().lockSeat(i, true, null);
            }
        }
    }

    @Override
    default void quitPKIfPKing() {
    }

    @Override
    default void unLockAll() {
        RCVoiceRoomEngine.getInstance().lockOtherSeats(false, null);
    }

    @Override
    default void resumePk(String pkRoomId, String pkUserId, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().resumePk(pkRoomId, pkUserId, new VoiceResultCallback(resultBack));
    }

    @Override
    default void responsePKInvitation(String inviterRoomId, String inviterUserId, PKResponse pkResponse, IResultBack<Boolean> resultBack) {
        cn.rongcloud.voiceroom.model.PKResponse response = cn.rongcloud.voiceroom.model.PKResponse.valueOf(pkResponse.name());
        RCVoiceRoomEngine.getInstance().responsePKInvitation(
                inviterRoomId,
                inviterUserId,
                response,
                new VoiceResultCallback(resultBack));
    }

    @Override
    default void onSendPKInvitation(String inviteeRoomId, String inviteeId, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().sendPKInvitation(inviteeRoomId, inviteeId, new VoiceResultCallback(resultBack));
    }

    @Override
    default void onCancelPkInvitation(String inviteeRoomId, String inviteeId, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().cancelPKInvitation(inviteeRoomId, inviteeId, new VoiceResultCallback(resultBack));
    }

    @Override
    default void onQuitPK(IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().quitPK(
                new VoiceResultCallback(resultBack));
    }

    @Override
    default void onSendPKMessage(String content) {
        VoiceEventHelper.helper().sendMessage(TextMessage.obtain(content));
    }

    @Override
    default void onSendPKStartMessage(String pkUserId) {
        UserProvider.provider().getAsyn(pkUserId, new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                String name = null != userInfo ? userInfo.getName() : pkUserId;
                onSendPKMessage("与" + name + "的PK即将开始，PK过程中，麦上观众将被抱下麦");
            }
        });
    }

    @Override
    default void onMutePKUser(boolean isMute, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().mutePKUser(
                isMute, new VoiceResultCallback(resultBack));
    }

    class VoiceResultCallback implements RCVoiceRoomCallback {
        private final static String TAG = VoiceResultCallback.class.getSimpleName();
        private IResultBack<Boolean> resultBack;

        public VoiceResultCallback(IResultBack<Boolean> resultBack) {
            this.resultBack = resultBack;
        }

        @Override
        public void onSuccess() {
            if (resultBack != null) resultBack.onResult(true);
        }

        @Override
        public void onError(int code, String message) {
            if (resultBack != null) resultBack.onResult(false);
            Logger.e(TAG, code + ":" + message);
        }
    }
}
