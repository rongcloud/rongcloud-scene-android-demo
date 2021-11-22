package cn.rong.combusis.sdk.event;

import android.text.TextUtils;
import android.util.Log;

import com.basis.UIStack;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.kit.UIKit;
import com.kit.cache.GsonUtil;
import com.kit.utils.Logger;
import com.kit.wapper.IResultBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.rong.combusis.api.VRApi;
import cn.rong.combusis.music.MusicManager;
import cn.rong.combusis.provider.user.UserProvider;
import cn.rong.combusis.sdk.event.listener.LeaveRoomCallBack;
import cn.rong.combusis.sdk.event.listener.RoomListener;
import cn.rong.combusis.sdk.event.listener.StatusListener;
import cn.rong.combusis.sdk.event.wrapper.AbsEvenHelper;
import cn.rong.combusis.sdk.event.wrapper.IEventHelp;
import cn.rong.combusis.sdk.event.wrapper.TipType;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomResultCallback;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.ChatRoomInfo;
import io.rong.imlib.model.ChatRoomMemberInfo;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

public class EventHelper extends AbsEvenHelper {

    private final static IEventHelp _helper = new EventHelper();

    private EventHelper() {
    }

    public static IEventHelp helper() {
        return _helper;
    }

    @Override
    public boolean isInitlaized() {
        return !TextUtils.isEmpty(roomId);
    }


    public void regeister(String roomId) {
        init(roomId);
    }

    @Override
    public String getRoomId() {
        if (TextUtils.isEmpty(roomId)) {
            return "";
        }
        return roomId;
    }

    @Override
    public void addMessage(MessageContent message) {
        messageList.add(message);
    }

    @Override
    public List<MessageContent> getMessageList() {
        return messageList;
    }

    @Override
    public List<RCVoiceSeatInfo> getRCVoiceSeatInfoList() {
        return mSeatInfos;
    }

    @Override
    public boolean getMuteAllRemoteStreams() {
        return isMute;
    }

    @Override
    public void setMuteAllRemoteStreams(boolean isMute) {
        this.isMute = isMute;
    }

    @Override
    public void unregeister() {
        unInit();
    }

    @Override
    public void addRoomListener(RoomListener listener) {
        if (null == listeners) listeners = new ArrayList<>();
        listeners.add(listener);
    }

    @Override
    public void setRCVoiceRoomEventListener(RCVoiceRoomEventListener rcVoiceRoomEventListener) {
        this.rcVoiceRoomEventListener = rcVoiceRoomEventListener;
    }

    @Override
    public void removeRCVoiceRoomEventListener() {
        this.rcVoiceRoomEventListener = null;
    }

    /**
     * 离开当前房间
     *
     * @param callback
     */
    @Override
    public void leaveRoom(LeaveRoomCallBack callback) {
        RCVoiceRoomEngine.getInstance().leaveRoom(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                MusicManager.get().stopPlayMusic();
                unregeister();
                changeUserRoom("");
                if (callback != null)
                    callback.onSuccess();
            }

            @Override
            public void onError(int i, String s) {
                if (callback != null)
                    callback.onError(i, s);
            }
        });
    }

    //更改所属房间
    @Override
    public void changeUserRoom(String roomId) {
        HashMap<String, Object> params = new OkParams()
                .add("roomId", roomId)
                .build();
        OkApi.get(VRApi.USER_ROOM_CHANGE, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    Log.e(TAG, "onResult: " + result.getMessage());
                }
            }
        });
    }

    @Override
    public int getCurrentStatus() {
        return currentStatus;
    }

    @Override
    public void setCurrentStatus(int status) {
        currentStatus = status;
    }

    @Override
    public void addStatusListener(StatusListener listener) {
        if (null == statusListeners) statusListeners = new ArrayList<>();
        statusListeners.add(listener);
    }

    /**
     * 根据用户id获取麦位信息
     *
     * @param userId
     * @return 麦位信息
     */
    public RCVoiceSeatInfo getSeatInfo(String userId) {
        synchronized (obj) {
            if (mSeatInfos == null) return null;
            int count = mSeatInfos.size();
            for (int i = 0; i < count; i++) {
                RCVoiceSeatInfo s = mSeatInfos.get(i);
                if (userId.equals(s.getUserId())) {
                    return s;
                }
            }
            return null;
        }
    }

    /**
     * @param index 索引
     * @return 麦位信息
     */
    public RCVoiceSeatInfo getSeatInfo(int index) {
        int count = null != mSeatInfos ? mSeatInfos.size() : 0;
        if (index < count) {
            synchronized (obj) {
                mSeatInfos.get(index);
            }
        }
        return null;
    }

    @Override
    public void getOnLineUserIds(String roomId, IResultBack<List<String>> resultBack) {
        RongIMClient.getInstance()
                .getChatRoomInfo(roomId,
                        20,
                        ChatRoomInfo.ChatRoomMemberOrder.RC_CHAT_ROOM_MEMBER_ASC,
                        new RongIMClient.ResultCallback<ChatRoomInfo>() {
                            @Override
                            public void onSuccess(ChatRoomInfo chatRoomInfo) {
                                Logger.d("=======" + chatRoomInfo.getTotalMemberCount());
                                if (null != resultBack && null != chatRoomInfo) {
                                    List<ChatRoomMemberInfo> cs = chatRoomInfo.getMemberInfo();
                                    int count = null == cs ? 0 : cs.size();
                                    List<String> result = new ArrayList<>();
                                    for (int i = 0; i < count; i++) {
                                        result.add(cs.get(i).getUserId());
                                    }
                                    resultBack.onResult(result);
                                }
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode err) {
                                Logger.e(TAG, "getOnLineUserIds#onError code = " + err.code + " msg = " + err.getMessage());
                                if (null != resultBack) resultBack.onResult(new ArrayList<>());
                            }
                        }
                );


    }

    @Override
    public void getUnReadMegCount(String roomId, IResultBack<Integer> resultBack) {
        RongIMClient.getInstance().getUnreadCount(new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                resultBack.onResult(integer);
            }

            @Override
            public void onError(RongIMClient.ErrorCode err) {
                Logger.e(TAG, "getUnReadMegCount#onError code = " + err.code + " msg = " + err.getMessage());
                if (null != resultBack) resultBack.onResult(0);
            }
        });
    }

    @Override
    public void getRequestSeatUserIds(IResultBack<List<String>> resultBack) {
        RCVoiceRoomEngine.getInstance().getRequestSeatUserIds(new RCVoiceRoomResultCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                if (null != resultBack) {
                    List<String> requestIds = new ArrayList<>();
                    for (String id : strings) {
                        if (null == getSeatInfo(id)) {//筛选 不再麦位上
                            requestIds.add(id);
                        }
                    }
                    resultBack.onResult(requestIds);
                }
            }

            @Override
            public void onError(int i, String s) {
                Logger.e(TAG, "getRequestSeatUserIds#onError code = " + i + " msg = " + s);
            }
        });
    }

    /**
     * 获取可用麦位索引
     *
     * @return 可用麦位索引
     */
    public int getAvailableSeatIndex() {
        synchronized (obj) {
            int availableIndex = -1;
            for (int i = 0; i < mSeatInfos.size(); i++) {
                RCVoiceSeatInfo seat = mSeatInfos.get(i);
                if (RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty == seat.getStatus()) {
                    availableIndex = i;
                    break;
                }
            }
            return availableIndex;
        }
    }

//    @Override
//    public PKInviter getPKInviter() {
//        return pkInviter;
//    }
//
//    @Override
//    public void releasePKInviter() {
//        pkInviter = null;
//    }

    @Override
    protected void onShowTipDialog(String roomId, String userId, TipType type, IResultBack<Boolean> resultBack) {
        // 根据userId获取用户信息
        if (null == UIStack.getInstance().getTopActivity()) {
            Logger.e(TAG, "onShowTipDialog:  topActivity is null");
            return;
        }
        UserProvider.provider().getAsyn(userId, new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                Logger.e(TAG, "onShowTipDialog: " + GsonUtil.obj2Json(userInfo));
                if (null != userInfo) {
                    UIKit.runOnUiTherad(new Runnable() {// fix：子线程弹不出弹框
                        @Override
                        public void run() {
                            String message = "";
//                            if (TipType.InvitedSeat == type) {
//                                message = userInfo.getName() + "邀请您上麦，是否同意？";
//                                EventDialogHelper.helper().showTipDialog(UIStack.getInstance().getTopActivity(), type.getValue(), message, resultBack);
//                            } else if (TipType.RequestSeat == type) {
//                                message = userInfo.getName() + "申请上麦，是否同意？";
//                                EventDialogHelper.helper().showTipDialog(UIStack.getInstance().getTopActivity(), type.getValue(), message, resultBack);
//                            } else {
//                                EventDialogHelper.helper().showPKDialog(UIStack.getInstance().getTopActivity(), type.getValue(), roomId, userInfo, resultBack);
//                            }
                        }
                    });
                } else {
                    if (null != resultBack) resultBack.onResult(false);
                }
            }
        });

    }

    @Override
    public void onCloseMiniRoom(CloseResult closeResult) {
        leaveRoom(new LeaveRoomCallBack() {
            @Override
            public void onSuccess() {
                if (closeResult != null) {
                    closeResult.onClose();
                }
            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }
}