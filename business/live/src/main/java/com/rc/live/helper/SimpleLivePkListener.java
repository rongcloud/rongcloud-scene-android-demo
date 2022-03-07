package com.rc.live.helper;


import com.basis.utils.Logger;
import com.basis.wapper.IResultBack;

import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.liveroom.api.RCLiveEngine;
import cn.rongcloud.liveroom.api.callback.RCLiveCallback;
import cn.rongcloud.liveroom.api.error.RCLiveError;
import cn.rongcloud.liveroom.manager.RCDataManager;
import cn.rongcloud.pk.api.PKListener;
import cn.rongcloud.pk.bean.PKResponse;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * @author gyn
 * @date 2022/1/17
 */
public interface SimpleLivePkListener extends PKListener {

    @Override
    default void lockAllAndKickOut() {
    }

    @Override
    default void unLockAll() {
    }

    @Override
    default void quitPKIfPKing() {
        if (RCDataManager.get().getRcLiveVideoPK() != null) {
            RCLiveEngine.getInstance().quitPK(new RCLiveCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(int code, RCLiveError error) {
                }
            });
        }
    }

    /**
     * @param pkRoomId   pk PK对象的房间ID
     * @param pkUserId   pk的用户id PK对象的用户ID
     * @param resultBack 回调
     */
    @Override
    default void resumePk(String pkRoomId, String pkUserId, IResultBack<Boolean> resultBack) {
        RCLiveEngine.getInstance().resumePk(new LiveResultCallback(resultBack));
    }

    @Override
    default void responsePKInvitation(String inviterRoomId, String inviterUserId, PKResponse pkResponse, IResultBack<Boolean> resultBack) {
        switch (pkResponse) {
            case reject:
            case ignore:
                RCLiveEngine.getInstance().rejectPKInvitation(inviterRoomId, inviterUserId, pkResponse.name(), new LiveResultCallback(resultBack));
                break;
            case accept:
                RCLiveEngine.getInstance().acceptPKInvitation(inviterRoomId, inviterUserId, new LiveResultCallback(resultBack));
                break;
        }
    }

    @Override
    default void onSendPKInvitation(String inviteeRoomId, String inviteeId, IResultBack<Boolean> resultBack) {
        RCLiveEngine.getInstance().sendPKInvitation(inviteeRoomId, inviteeId, new LiveResultCallback(resultBack));
    }

    @Override
    default void onCancelPkInvitation(String inviteeRoomId, String inviteeId, IResultBack<Boolean> resultBack) {
        RCLiveEngine.getInstance().cancelPKInvitation(inviteeRoomId, inviteeId, new LiveResultCallback(resultBack));
    }

    @Override
    default void onQuitPK(IResultBack<Boolean> resultBack) {
        RCLiveEngine.getInstance().quitPK(new LiveResultCallback(resultBack));
    }

    @Override
    default void onSendPKMessage(String content) {
        LiveEventHelper.getInstance().sendMessage(TextMessage.obtain(content), true);
    }

    @Override
    default void onSendPKStartMessage(String pkUserId) {
        UserProvider.provider().getAsyn(pkUserId, new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                String name = null != userInfo ? userInfo.getName() : pkUserId;
                onSendPKMessage("与" + name + "的PK即将开始");
            }
        });
    }

    @Override
    default void onMutePKUser(boolean isMute, IResultBack<Boolean> resultBack) {
        RCLiveEngine.getInstance().mutePKUser(isMute, new LiveResultCallback(resultBack));
    }

    class LiveResultCallback implements RCLiveCallback {
        private static String TAG = "PKManager";
        private IResultBack<Boolean> resultBack;

        public LiveResultCallback(IResultBack<Boolean> resultBack) {
            this.resultBack = resultBack;
        }

        @Override
        public void onSuccess() {
            if (resultBack != null) resultBack.onResult(true);
        }

        @Override
        public void onError(int code, RCLiveError error) {
            if (resultBack != null) resultBack.onResult(false);
            Logger.e(TAG, code + ":" + error);
        }
    }
}
