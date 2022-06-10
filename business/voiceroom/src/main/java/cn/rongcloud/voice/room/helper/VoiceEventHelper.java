package cn.rongcloud.voice.room.helper;


import static cn.rongcloud.voice.Constant.EVENT_AGREE_MANAGE_PICK;
import static cn.rongcloud.voice.Constant.EVENT_MANAGER_LIST_CHANGE;
import static cn.rongcloud.voice.Constant.EVENT_REJECT_MANAGE_PICK;
import static cn.rongcloud.voice.Constant.EVENT_ROOM_CLOSE;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.OkParams;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.UIStack;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.wapper.IResultBack;
import com.basis.wapper.IRoomCallBack;
import com.basis.widget.dialog.VRCenterDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.music.MusicControlManager;
import cn.rongcloud.pk.PKManager;
import cn.rongcloud.pk.bean.PKInviteInfo;
import cn.rongcloud.pk.bean.PKResponse;
import cn.rongcloud.pk.bean.PKState;
import cn.rongcloud.roomkit.api.VRApi;
import cn.rongcloud.roomkit.manager.RCChatRoomMessageManager;
import cn.rongcloud.roomkit.message.RCChatroomAdmin;
import cn.rongcloud.roomkit.message.RCChatroomBarrage;
import cn.rongcloud.roomkit.message.RCChatroomEnter;
import cn.rongcloud.roomkit.message.RCChatroomGift;
import cn.rongcloud.roomkit.message.RCChatroomGiftAll;
import cn.rongcloud.roomkit.message.RCChatroomKickOut;
import cn.rongcloud.roomkit.message.RCChatroomLocationMessage;
import cn.rongcloud.roomkit.message.RCChatroomSeats;
import cn.rongcloud.roomkit.message.RCChatroomVoice;
import cn.rongcloud.roomkit.message.RCFollowMsg;
import cn.rongcloud.roomkit.ui.miniroom.MiniRoomManager;
import cn.rongcloud.roomkit.ui.room.dialog.shield.Shield;
import cn.rongcloud.roomkit.ui.room.fragment.ClickCallback;
import cn.rongcloud.roomkit.ui.room.model.MemberCache;
import cn.rongcloud.voice.Constant;
import cn.rongcloud.voice.inter.RoomListener;
import cn.rongcloud.voice.inter.StatusListener;
import cn.rongcloud.voiceroom.api.RCVoiceRoomEngine;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomCallback;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomEventListener;
import cn.rongcloud.voiceroom.api.callback.RCVoiceRoomResultCallback;
import cn.rongcloud.voiceroom.model.RCPKInfo;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.ChatRoomInfo;
import io.rong.imlib.model.ChatRoomMemberInfo;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author gyn
 * @date 2022/1/17
 */
public class VoiceEventHelper implements IVoiceRoomHelp, RCVoiceRoomEventListener {
    private static final String TAG = VoiceEventHelper.class.getSimpleName();
    private final static VoiceEventHelper _helper = new VoiceEventHelper();

    protected final static Object obj = new Object();
    protected List<RoomListener> listeners;//房间监听
    protected List<StatusListener> statusListeners;//网络状态监听
    protected List<RCVoiceSeatInfo> mSeatInfos;//当前麦序
    protected RCVoiceRoomInfo roomInfo;//房间信息
    protected RCVoiceRoomEventListener rcVoiceRoomEventListener;
    protected List<MessageContent> messageList = new LinkedList<>();
    protected boolean isMute = false;
    public static final int STATUS_ON_SEAT = 0;
    public static final int STATUS_NOT_ON_SEAT = 1;
    public static final int STATUS_WAIT_FOR_SEAT = 2;
    protected int currentStatus;
    private VRCenterDialog inviteDialog;
    protected String roomId;
    private List<Shield> shields = new ArrayList<>();

    private VoiceEventHelper() {
    }

    public static IVoiceRoomHelp helper() {
        return _helper;
    }

    @Override
    public List<Shield> getShield() {
        return shields;
    }

    protected void init(String roomId) {
        this.roomId = roomId;
        RCVoiceRoomEngine.getInstance().setVoiceRoomEventListener(this);
        if (null == mSeatInfos) mSeatInfos = new ArrayList<>();
        if (null == listeners) listeners = new ArrayList<>();
        if (null == statusListeners) statusListeners = new ArrayList<>();
    }

    protected void unInit() {
        Logger.e(TAG,"unInit");
        RCVoiceRoomEngine.getInstance().setVoiceRoomEventListener(null);
        MusicControlManager.getInstance().release();
        PKManager.get().unInit();
        if (null != mSeatInfos) mSeatInfos.clear();
        if (null != listeners) listeners.clear();
        if (null != statusListeners) statusListeners.clear();
        messageList.clear();
        isMute = false;
        roomInfo = null;
        roomId = null;
    }


    @Override
    public void onRoomKVReady() {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onRoomKVReady();
        }
        Log.d(TAG, "onRoomKVReady");
    }

    public RCVoiceRoomInfo getRoomInfo() {
        return roomInfo;
    }

    /**
     * 房间信息跟新回调
     *
     * @param room
     */
    @Override
    public void onRoomInfoUpdate(RCVoiceRoomInfo room) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onRoomInfoUpdate(room);
        }
        this.roomInfo = room;
        Log.d(TAG, "onRoomInfoUpdate:" + GsonUtil.obj2Json(roomInfo));
        if (null != listeners) {
            for (RoomListener l : listeners) {
                l.onRoomInfo(roomInfo);
            }
        }
    }

    /**
     * 麦位列表跟新回调
     *
     * @param list
     */
    @Override
    public void onSeatInfoUpdate(List<RCVoiceSeatInfo> list) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onSeatInfoUpdate(list);
        }
        int count = list.size();
        Log.d(TAG, "onSeatInfoUpdate: count = " + count);
        for (int i = 0; i < count; i++) {
            RCVoiceSeatInfo info = list.get(i);
            Log.d(TAG, "index = " + i + "  " + GsonUtil.obj2Json(info));
        }
        synchronized (obj) {
            mSeatInfos.clear();
            mSeatInfos.addAll(list);
        }
        refreshSeatInfos();
    }

    private void refreshSeatInfos() {
        if (null != listeners) {
            for (RoomListener l : listeners) {
                l.onSeatList(mSeatInfos);
            }
        }
    }

    //同步回调 onSeatInfoUpdate 此处无特殊需求可不处理
    @Override
    public void onUserEnterSeat(int index, String userId) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onUserEnterSeat(index, userId);
        }
        Log.d(TAG, "onUserEnterSeat: index = " + index + " userId = " + userId);
        RCVoiceSeatInfo info = getSeatInfo(index);
        if (null != info) {
            info.setUserId(userId);
            info.setStatus(RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusUsing);
        }
        refreshSeatInfos();
    }

    //同步回调 onSeatInfoUpdate 此处无特殊需求可不处理
    @Override
    public void onUserLeaveSeat(int index, String userId) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onUserLeaveSeat(index, userId);
        }
        Log.d(TAG, "onUserLeaveSeat: index = " + index + " userId = " + userId);
        RCVoiceSeatInfo info = getSeatInfo(index);
        if (null != info) {
            info.setUserId(null);
            // 可能是锁定导致的
            // info.setStatus(RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty);
        }
        refreshSeatInfos();
    }

    //同步回调 onSeatInfoUpdate 此处无特殊需求可不处理
    @Override
    public void onSeatMute(int index, boolean mute) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onSeatMute(index, mute);
        }
        Log.d(TAG, "onSeatMute: index = " + index + " mute = " + mute);
    }

    //同步回调 onSeatInfoUpdate 此处无特殊需求可不处理
    @Override
    public void onSeatLock(int index, boolean locked) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onSeatLock(index, locked);
        }
        Log.d(TAG, "onSeatLock: index = " + index + " locked = " + locked);
    }

    /**
     * 观众进入
     *
     * @param userId
     */
    @Override
    public void onAudienceEnter(String userId) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onAudienceEnter(userId);
        }
        Log.d(TAG, "onAudienceEnter: userId = " + userId);
        if (null != listeners) {
            getOnLineUserIds(roomId, new IResultBack<List<String>>() {
                @Override
                public void onResult(List<String> strings) {
                    for (RoomListener l : listeners) {
                        l.onOnLineUserIds(strings);
                    }
                }
            });
        }
    }

    /**
     * 观众离开房间
     *
     * @param userId
     */
    @Override
    public void onAudienceExit(String userId) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onAudienceExit(userId);
        }
        Log.d(TAG, "onAudienceExit: userId = " + userId);
        if (null != listeners) {
            getOnLineUserIds(roomId, new IResultBack<List<String>>() {
                @Override
                public void onResult(List<String> strings) {
                    for (RoomListener l : listeners) {
                        l.onOnLineUserIds(strings);
                    }
                }
            });
        }
    }

    /**
     * 说话状态回调 比较频繁
     *
     * @param index      麦位索引
     * @param audioLevel 是否正在语音
     */
    @Override
    public void onSpeakingStateChanged(int index, int audioLevel) {
        boolean speaking = audioLevel > 0;
//        Log.v(TAG, "onSpeakingStateChanged: index = " + index + " speaking = " + speaking);
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onSpeakingStateChanged(index, audioLevel);
        }
        if (MiniRoomManager.getInstance().isShowing()) {
            MiniRoomManager.getInstance().onSpeak(speaking);
        }
//        Log.v(TAG, "onSpeakingStateChanged: index = " + index + " speaking = " + speaking);
        if (null != statusListeners) {
            for (StatusListener l : statusListeners) {
                l.onSpeaking(index, speaking);
            }
        }
    }

    @Override
    public void onMessageReceived(Message message) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onMessageReceived(message);
        }
        PKManager.get().onMessageReceived(message);
        if (message.getConversationType() == Conversation.ConversationType.CHATROOM) {
            addMessage(message.getContent());
        }
        if (message.getConversationType() == Conversation.ConversationType.PRIVATE) {
            if (null != statusListeners) {
                if (!TextUtils.isEmpty(roomId)) {
                    getUnReadMegCount(roomId, new IResultBack<Integer>() {
                        @Override
                        public void onResult(Integer integer) {
                            for (StatusListener l : statusListeners) {
                                l.onReceive(integer);
                            }
                        }
                    });
                }
            }
        }
        Log.v(TAG, "onMessageReceived: " + GsonUtil.obj2Json(message.getContent()));
    }

    /**
     * 是否显示在消息列表中的消息
     *
     * @param content
     * @return
     */
    public boolean isShowingMessage(MessageContent content) {
        if (content instanceof RCChatroomBarrage || content instanceof RCChatroomEnter
                || content instanceof RCChatroomKickOut || content instanceof RCChatroomGiftAll
                || content instanceof RCChatroomGift || content instanceof RCChatroomAdmin
                || content instanceof RCChatroomLocationMessage || content instanceof RCFollowMsg
                || content instanceof RCChatroomVoice || content instanceof TextMessage
                || content instanceof RCChatroomSeats) {
            return true;
        }
        return false;
    }

    /**
     * 房间通知回调
     *
     * @param name
     * @param content
     */
    @Override
    public void onRoomNotificationReceived(String name, String content) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onRoomNotificationReceived(name, content);
        }
        if (MiniRoomManager.getInstance().isShowing()) {
            if (TextUtils.equals(name, EVENT_ROOM_CLOSE)) {
                VRCenterDialog confirmDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
                confirmDialog.replaceContent("当前直播已结束", "", null, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leaveRoom(null);
                        unregeister();
                    }
                }, null);
                confirmDialog.setCancelable(false);
                confirmDialog.show();
            } else if (TextUtils.equals(name, EVENT_AGREE_MANAGE_PICK)) {
                if (TextUtils.equals(content, UserManager.get().getUserId()))
                    KToast.show("用户连线成功");
            } else if (TextUtils.equals(name, EVENT_REJECT_MANAGE_PICK)) {
                if (TextUtils.equals(content, UserManager.get().getUserId()))
                    KToast.show("用户拒绝邀请");
            }
        }
        Log.v(TAG, "onRoomNotificationReceived: name = " + name + " content = " + content);
        if (null != listeners) {
            for (RoomListener l : listeners) {
                l.onNotify(name, content);
            }
        }
    }

    @Override
    public void onRoomDestroy() {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onRoomDestroy();
        }
    }

    /**
     * 当前用户被抱上麦回调
     *
     * @param userId 发起邀请的用户id
     */
    @Override
    public void onPickSeatReceivedFrom(String userId) {
        Log.d(TAG, "onPickSeatReceivedFrom: userId = " + userId);
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onPickSeatReceivedFrom(userId);
        }
        if (MiniRoomManager.getInstance().isShowing()) {
            showPickReceivedDialog(true, userId);
        }
    }

    /**
     * 弹窗收到上麦邀请弹窗
     *
     * @param isCreate 是否是房主
     * @param userId   邀请人的ID
     */
    public void showPickReceivedDialog(boolean isCreate, String userId) {
        String pickName = isCreate ? "房主" : "管理员";
        inviteDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
        inviteDialog.replaceContent("您被" + pickName + "邀请上麦，是否同意?", "拒绝", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拒绝
                inviteDialog.dismiss();
                notifyRoom(EVENT_REJECT_MANAGE_PICK, userId);
            }
        }, "同意", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //同意
                notifyRoom(Constant.EVENT_AGREE_MANAGE_PICK, UserManager.get().getUserId());
                //获取可用麦位索引
                int availableIndex = getAvailableSeatIndex();
                if (availableIndex > -1) {
                    enterSeat(availableIndex, null);
                } else {
                    KToast.show("当前没有空余的麦位");
                }
                inviteDialog.dismiss();
                if (currentStatus == STATUS_WAIT_FOR_SEAT) {
                    //被邀请上麦了，并且同意了，如果该用户已经申请了上麦，那么主动撤销掉申请
                    cancelRequestSeat(new ClickCallback<Boolean>() {
                        @Override
                        public void onResult(Boolean result, String msg) {
                            if (result) {
                                currentStatus = STATUS_ON_SEAT;
                            }
                        }
                    });
                }
            }
        }, null);
        inviteDialog.show();
    }

    private void enterSeat(int index, IResultBack<Boolean> resultBack) {
        RCVoiceRoomEngine.getInstance().enterSeat(
                index, new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        KToast.show("上麦成功");
                    }

                    @Override
                    public void onError(int code, String message) {
                        KToast.show("上麦失败");
                    }
                });
    }

    /**
     * 被踢下麦回调
     *
     * @param index 麦位索引
     */
    @Override
    public void onKickSeatReceived(int index) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onKickSeatReceived(index);
        }
        if (MiniRoomManager.getInstance().isShowing()) {
            KToast.show("您已被抱下麦位");
        }
        Log.d(TAG, "onPickSeatReceivedFrom: index = " + index);
    }

    public void notifyRoom(String name, String content) {
        RCVoiceRoomEngine.getInstance().notifyVoiceRoom(name, content, null);
    }

    /**
     * 房主或管理员同意当前用户的排麦申请的回调
     * 1. enterSeat
     */
    @Override
    public void onRequestSeatAccepted() {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onRequestSeatAccepted();
        }
        if (MiniRoomManager.getInstance().isShowing()) {
            currentStatus = STATUS_ON_SEAT;
            //加入麦位
            int availableIndex = getAvailableSeatIndex();
            if (availableIndex > -1) {
                enterSeat(availableIndex, null);
            } else {
                KToast.show("当前没有空余的麦位");
            }
        }
        Log.d(TAG, "onRequestSeatAccepted: ");
    }

    /**
     * 发送的排麦请求被房主或管理员拒绝
     */
    @Override
    public void onRequestSeatRejected() {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onRequestSeatRejected();
        }
        if (MiniRoomManager.getInstance().isShowing()) {
            currentStatus = STATUS_NOT_ON_SEAT;
        }
        Log.d(TAG, "onRequestSeatRejected: ");
        KToast.show("您的上麦申请被拒绝啦");
    }

    /**
     * 排麦列表发生变化
     * 1、获取申请排麦id列表
     * 2、过滤已经在房间的用户
     * 3、弹框提 同意、拒绝
     */
    @Override
    public void onRequestSeatListChanged() {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onRequestSeatListChanged();
        }
        Log.d(TAG, "onRequestSeatListChanged: ");
//        RCVoiceRoomEngine.getInstance().getRequestSeatUserIds(new RCVoiceRoomResultCallback<List<String>>() {
//            @Override
//            public void onSuccess(List<String> strings) {
//                Log.e(TAG, "getRequestSeatUserIds: ids = " + GsonUtil.obj2Json(strings));
//                List<String> requestIds = new ArrayList<>();
//                for (String id : strings) {
//                    if (null == getSeatInfo(id)) {//过滤 不再麦位上
//                        requestIds.add(id);
//                    }
//                }
//                if (!requestIds.isEmpty()) {
//                    String userId = requestIds.get(0);
//                    onShowTipDialog("", userId, TipType.RequestSeat, new IResultBack<Boolean>() {//申请上麦
//                        @Override
//                        public void onResult(Boolean result) {
//                            if (result) {
//                                //同意
//                                int index = getAvailableSeatIndex();
//                                if (index > -1) {
//                                    RCVoiceRoomEngine.getInstance().acceptRequestSeat(userId, null);
//                                } else {
//                                    KToast.show("当前没有空余的麦位");
//                                }
//                            } else {//拒绝
//                                RCVoiceRoomEngine.getInstance().rejectRequestSeat(userId, null);
//                            }
//                        }
//                    });
//                } else {//申请被取消
//                    EventDialogHelper.helper().dismissDialog();
//                }
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                Log.e(TAG, "onError: code:" + i + " ,message = " + s);
//            }
//        });

    }

    /**
     * 收到邀请
     *
     * @param invitationId 邀请标识 Id
     * @param userId       发送邀请用户的标识
     * @param content      邀请内容 （用户可以自定义）
     */
    @Override
    public void onInvitationReceived(String invitationId, String userId, String content) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onInvitationReceived(invitationId, userId, content);
        }
        Log.d(TAG, "onInvitationReceived: invitationId = " + invitationId + " userId = " + userId + " content = " + content);
    }

    /**
     * 邀请被接受回调
     *
     * @param invitationId 邀请标识 Id
     */
    @Override
    public void onInvitationAccepted(String invitationId) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onInvitationAccepted(invitationId);
        }
        KToast.show("用户连线成功");
        Log.d(TAG, "onInvitationAccepted: invitationId = " + invitationId);
    }

    /**
     * 邀请被拒绝回调
     *
     * @param invitationId 邀请标识 Id
     */
    @Override
    public void onInvitationRejected(String invitationId) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onInvitationRejected(invitationId);
        }
        KToast.show("用户拒绝邀请");
        Log.d(TAG, "onInvitationRejected: invitationId = " + invitationId);
    }

    /**
     * 邀请被取消回调
     *
     * @param invitationId 邀请标识 Id
     */
    @Override
    public void onInvitationCancelled(String invitationId) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onInvitationCancelled(invitationId);
        }
        Log.d(TAG, "onInvitationCancelled: invitationId = " + invitationId);
    }

    /**
     * 被踢出房间回调
     *
     * @param targetId 被踢用户的标识
     * @param userId   发起踢人用户的标识
     */
    @Override
    public void onUserReceiveKickOutRoom(String targetId, String userId) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onUserReceiveKickOutRoom(targetId, userId);
        }
        if (MiniRoomManager.getInstance().isShowing()) {
            if (targetId.equals(UserManager.get().getUserId())) {
                KToast.show("你已被踢出房间");
                MiniRoomManager.getInstance().close();
                leaveRoom(null);
            }
        }
        Log.d(TAG, "onUserReceiveKickOutRoom: targetId = " + targetId);
    }

    /**
     * 网络状态
     *
     * @param i 网络延迟 ms
     */
    @Override
    public void onNetworkStatus(int i) {
        if (rcVoiceRoomEventListener != null) {
            rcVoiceRoomEventListener.onNetworkStatus(i);
        }
//        Log.d(TAG, "onNetworkStatus: rtt = " + i);
        if (null != statusListeners) {
            for (StatusListener l : statusListeners) {
                l.onStatus(i);
            }
        }
    }

    @Override
    public void onPKGoing(RCPKInfo rcpkInfo) {
        PKInviteInfo pkInviteInfo = new PKInviteInfo(rcpkInfo.getInviterId(), rcpkInfo.getInviterRoomId(), rcpkInfo.getInviteeId(), rcpkInfo.getInviteeRoomId());
        PKManager.get().onPKBegin(pkInviteInfo);
    }

    @Override
    public void onPKFinish() {
        PKManager.get().onPKFinish();
    }

    @Override
    public void onReceivePKInvitation(String inviterRoomId, String inviterUserId) {
        Logger.d(TAG, "onReceivePKInvitation");
        UserProvider.provider().getAsyn(inviterUserId, new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                PKManager.get().onReceivePKInvitation(inviterRoomId, inviterUserId);
            }
        });
    }

    @Override
    public void onPKInvitationCanceled(String inviterRoomId, String inviterUserId) {
        Logger.d(TAG, "onPKInvitationCanceled");
        PKManager.get().onPKInvitationCanceled(inviterRoomId, inviterUserId);
    }

    @Override
    public void onPKInvitationRejected(String s, String s1) {
        Logger.d(TAG, "onPKInvitationRejected");
        PKManager.get().onPKInvitationRejected(s, s1, PKResponse.reject);
    }

    @Override
    public void onPKInvitationIgnored(String s, String s1) {
        Logger.d(TAG, "onPKInvitationIgnored");
        PKManager.get().onPKInvitationRejected(s, s1, PKResponse.ignore);
    }

    @Override
    public boolean isInitlaized() {
        return !TextUtils.isEmpty(roomId);
    }


    public void register(String roomId) {
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
        if (isShowingMessage(message)) {
            messageList.add(message);
        }
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
    public void removeRCVoiceRoomEventListener(RCVoiceRoomEventListener rcVoiceRoomEventListener) {
        if (rcVoiceRoomEventListener == this.rcVoiceRoomEventListener) {
            this.rcVoiceRoomEventListener = null;
        }
    }

    /**
     * 离开当前房间
     *
     * @param callback
     */
    @Override
    public void leaveRoom(IRoomCallBack callback) {
        RCVoiceRoomEngine.getInstance().leaveRoom(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
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

    @Override
    public void pickUserToSeat(String userId, ClickCallback<Boolean> callback) {
        if (getAvailableSeatIndex() < 0) {
            callback.onResult(false, "麦位已满");
            return;
        }
        RCVoiceRoomEngine.getInstance().pickUserToSeat(userId, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //邀请成功,集合会跟着变化
                callback.onResult(true, "邀请成功");
            }

            @Override
            public void onError(int i, String s) {
                callback.onResult(false, s);
            }
        });
    }

    @Override
    public void acceptRequestSeat(String userId, ClickCallback<Boolean> callback) {
        if (getAvailableSeatIndex() < 0) {
            KToast.show("房间麦位已满");
            return;
        }
        RCVoiceRoomEngine.getInstance()
                .acceptRequestSeat(userId, new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onResult(true, "");
                    }

                    @Override
                    public void onError(int i, String s) {
                        callback.onResult(false, s);
                    }
                });
    }

    @Override
    public void cancelRequestSeat(ClickCallback<Boolean> callback) {
        RCVoiceRoomEngine.getInstance().cancelRequestSeat(new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //取消成功
                callback.onResult(true, "");
            }

            @Override
            public void onError(int i, String s) {
                //取消失败
                callback.onResult(false, s);
            }
        });
    }

    @Override
    public void lockSeat(int index, boolean isClose, ClickCallback<Boolean> callback) {
        RCVoiceRoomEngine.getInstance().lockSeat(index, isClose, new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //锁座位成功
                callback.onResult(true, isClose ? "座位已关闭" : "座位已开启");
            }

            @Override
            public void onError(int i, String s) {
                //锁座位失败
                callback.onResult(false, s);
            }
        });
    }

    @Override
    public void muteSeat(int index, boolean isMute, ClickCallback<Boolean> callback) {
        RCVoiceRoomEngine.getInstance()
                .muteSeat(index, isMute, new RCVoiceRoomCallback() {
                    @Override
                    public void onSuccess() {
                        //座位禁麦成功
                        callback.onResult(true, "");
                        if (isMute) {
                            KToast.show("此麦位已闭麦");
                        } else {
                            KToast.show("已取消闭麦");
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        //座位禁麦失败
                        callback.onResult(false, s);
                    }
                });
    }

    @Override
    public void kickUserFromRoom(User user, ClickCallback<Boolean> callback) {
        RCVoiceRoomEngine.getInstance().kickUserFromRoom(user.getUserId(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                //踢出房间成功以后，要发送消息给被踢出的人
                RCChatroomKickOut kickOut = new RCChatroomKickOut();
                kickOut.setUserId(UserManager.get().getUserId());
                kickOut.setUserName(UserManager.get().getUserName());
                kickOut.setTargetId(user.getUserId());
                kickOut.setTargetName(user.getUserName());
                RCChatRoomMessageManager.sendChatMessage(getRoomId(), kickOut, true, null, null);
                callback.onResult(true, "踢出成功");
            }

            @Override
            public void onError(int i, String s) {
                callback.onResult(false, s);
            }
        });
    }

    @Override
    public void kickUserFromSeat(User user, ClickCallback<Boolean> callback) {
        RCVoiceRoomEngine.getInstance().kickUserFromSeat(user.getUserId(), new RCVoiceRoomCallback() {
            @Override
            public void onSuccess() {
                callback.onResult(true, "");
            }

            @Override
            public void onError(int i, String s) {
                callback.onResult(false, s);
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

    @Override
    public PKState getPKState() {
        return PKManager.get().getPkState();
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

    @Override
    public void onCloseMiniRoom(CloseResult closeResult) {
        leaveRoom(new IRoomCallBack() {
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

    /**
     * 发送消息
     *
     * @param messageContent
     */
    @Override
    public void sendMessage(MessageContent messageContent) {
        //先判断是否包含了屏蔽词
        boolean isContains = false;
        if (shields != null) {
            for (Shield shield : shields) {
                if (messageContent instanceof RCChatroomBarrage) {
                    if (((RCChatroomBarrage) messageContent).getContent().contains(shield.getName())) {
                        isContains = true;
                        break;
                    }
                }
            }
            if (isContains) {
                //如果是包含了敏感词'
                Message message = new Message();
                message.setConversationType(Conversation.ConversationType.CHATROOM);
                message.setContent(messageContent);
                onMessageReceived(message);
                return;
            }
        }
        RCChatRoomMessageManager.sendChatMessage(roomId, messageContent, true
                , new Function1<Integer, Unit>() {
                    @Override
                    public Unit invoke(Integer integer) {
                        addMessage(messageContent);
                        if (messageContent instanceof RCChatroomAdmin) {
                            //发送成功，回调给接收的地方，统一去处理，避免多个地方处理 通知刷新管理员信息
                            RCVoiceRoomEngine.getInstance().notifyVoiceRoom(EVENT_MANAGER_LIST_CHANGE, "", null);
                            MemberCache.getInstance().refreshAdminData(roomId);
                        }
                        return null;
                    }
                }, new Function2<IRongCoreEnum.CoreErrorCode, Integer, Unit>() {
                    @Override
                    public Unit invoke(IRongCoreEnum.CoreErrorCode coreErrorCode, Integer integer) {
                        KToast.show("发送失败");
                        return null;
                    }
                });
    }
}
