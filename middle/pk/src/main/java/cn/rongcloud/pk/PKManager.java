package cn.rongcloud.pk;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.basis.ui.UIStack;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.dialog.BottomDialog;
import com.basis.widget.dialog.VRCenterDialog;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.pk.api.IPKManager;
import cn.rongcloud.pk.api.PKApi;
import cn.rongcloud.pk.api.PKListener;
import cn.rongcloud.pk.bean.PKInfo;
import cn.rongcloud.pk.bean.PKInviteInfo;
import cn.rongcloud.pk.bean.PKInvitee;
import cn.rongcloud.pk.bean.PKInviter;
import cn.rongcloud.pk.bean.PKResponse;
import cn.rongcloud.pk.bean.PKResult;
import cn.rongcloud.pk.bean.PKState;
import cn.rongcloud.pk.dialog.CancelPKDialog;
import cn.rongcloud.pk.dialog.OnlineCreatorDialog;
import cn.rongcloud.pk.dialog.RequestPKDialog;
import cn.rongcloud.pk.message.RCChatroomPK;
import cn.rongcloud.pk.message.RCChatroomPKGift;
import cn.rongcloud.pk.widget.IPK;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 * @author gyn
 * @date 2022/1/12
 * <p>
 * PK主播端
 * // PK邀请
 * 1、显示在线房主列表，选则发起PK邀请 进入已邀请状态 该状态不可再次邀请
 * 2、对方拒绝PK或忽略PK邀请，恢复原状 可再次邀请
 * 3、对方同意PK,SDK进入PK状态，接收到SDK PKGong状态暂不处理，暂约定pk邀请者上报pk状态：开始，pk双方等待服务端分发pk开始状态消息
 * // 开始PK
 * 1、pk双发接收到服务端分发的pk消息，解析pk状态：0，pk开始
 * 2、PKView开启PK记时
 * 3、PKView记时结束，暂停，等待服务端分发pk惩罚状态消息。
 * // 惩罚
 * 1、pk双发接收到服务端分发的pk惩罚状态消息，解析pk状态：1，惩罚开始，
 * 2、PKView开启惩罚记时
 * 3、PKView记时结束。暂停，等待服务端分发pk结束状态消息。
 * // pk结束
 * 1、pk双发接收到服务端分发的pk消息，解析pk状态：2，pk结束
 * 2、暂约定pk邀请者执行sdk的quitPk,结束pk
 * // 主动结束pk
 * 1、pk双方均可主动结束pk流程
 * 2、pk结束方，主动调sdk的quitPK,结束pk，并上报pk状态结束 等待服务端分发消息
 * 3、pk双方接收到服务端分发的pk消息，结束pk流程
 * 观众端：
 * 1、检查当前房间的状态，若果在pk中 需返回pk双方房间和房主信息，-1：不在pk 0：pk 阶段  2：惩罚阶段
 * 2、根据pk的状态，ui调转到不同的PK阶段
 */
public class PKManager implements IPKManager, DialogInterface.OnDismissListener {
    private static final String TAG = PKManager.class.getSimpleName();
    private String roomId, pkRoomId;
    private int roomType;
    private IPK pkView;
    private PKState pkState = PKState.PK_NONE;
    private PKListener pkListener;
    private BottomDialog dialog;
    private RequestPKDialog requestPKDialog;
    private PKInviteInfo pkInviteInfo;
    private PKInviter pkInviter;
    private PKInvitee pkInvitee;
    private static PKManager pkManager;
    private boolean isMute = false;

    public static IPKManager get() {
        if (null == pkManager) {
            synchronized (IPKManager.class) {
                if (null == pkManager) {
                    pkManager = new PKManager();
                }
            }
        }
        return pkManager;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        dialog = null;
    }

    @Override
    public void init(String roomId, int roomType, IPK pkView, PKListener listener) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.pkView = pkView;
        this.pkListener = listener;
        pkView.setPKListener(listener);
        pkView.setClickMuteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPKer()) {
                    return;
                }
                isMute = !isMute;
                if (null != pkListener) {
                    pkListener.onMutePKUser(isMute, new IResultBack<Boolean>() {
                        @Override
                        public void onResult(Boolean aBoolean) {
                            pkView.setMute(isMute);
                            if (aBoolean) {
                                KToast.show(isMute ? "屏蔽音频成功" : "取消屏蔽音频成功");
                            } else {
                                KToast.show(isMute ? "屏蔽音频失败" : "取消屏蔽音频失败");
                            }
                        }
                    });
                }
            }
        });
        // 观众端 检查pk状态
        PKApi.getPKInfo(roomId, new IResultBack<PKResult>() {
            @Override
            public void onResult(PKResult pkResult) {
                Logger.d(TAG, GsonUtil.obj2Json(pkResult));
                if (null == pkResult || pkResult.getStatusMsg() == -1 || pkResult.getStatusMsg() == 2) {
                    Logger.e(TAG, "init: Not In PK");
                    pkState = PKState.PK_NONE;
                    if (pkListener != null) pkListener.quitPKIfPKing();
                    return;
                }

                // 观众端需要获取pk双方房间和房主信息
                PKInfo[] pkInfos = formatPKInfo(pkResult);
                if (null == pkInfos || 2 != pkInfos.length) {
                    Logger.e(TAG, "pk info list is fault");
                    pkState = PKState.PK_NONE;
                    return;
                }
                String currentId = UserManager.get().getUserId();
                //当前房间主播 可能pk主播退出进入pk方的房间会引起问题
                boolean isRoomOwner = TextUtils.equals(currentId, pkInfos[0].getUserId());
                Logger.d(TAG, "current user is room owner = " + isRoomOwner);
                if (isRoomOwner) {
                    pkListener.lockAllAndKickOut();
//                    lockAllAndKickOut();
                    // 走到这里 说明主播是退出后 又进自己房间
                    pkListener.resumePk(pkInfos[1].getRoomId(), pkInfos[1].getUserId(), new IResultBack<Boolean>() {
                        @Override
                        public void onResult(Boolean aBoolean) {
                            if (aBoolean) {
                                pkView.reset(true);
                                handleAudienceJoinPk(pkResult);
                            } else {
                                Logger.e(TAG, "resume Pk Fail");
                            }
                        }
                    });
                } else {
                    pkView.reset(false);
                    handleAudienceJoinPk(pkResult);
                    pkState = pkResult.getStatusMsg() == 0 ? PKState.PK_START : PKState.PK_PUNISH;
                    onPkStateChanged(pkState);
                }
            }
        });
    }

    @Override
    public PKState getPkState() {
        return pkState;
    }

    void handleAudienceJoinPk(PKResult pkResult) {
        Logger.e(TAG, "handleAudienceJoinPk");
        if (null != pkListener) pkListener.onPkStart();
        // 观众端需要获取pk双方房间和房主信息
        int state = pkResult.getStatusMsg();
        if (null != pkView) {
            refreshPKInfo(pkResult);
            long timeDiff = null == pkResult ? -1 : pkResult.getTimeDiff();
            if (0 == state) {// pk 阶段
                pkView.pkStart(timeDiff, new IPK.OnTimerEndListener() {
                    @Override
                    public void onTimerEnd() {
                        // 等待服务端分发pk惩罚消息
                    }
                });
            } else if (1 == state) {// pk 惩罚阶段
                pkView.pkPunish(timeDiff, new IPK.OnTimerEndListener() {
                    @Override
                    public void onTimerEnd() {
                        // 等待服务端分发pk结束消息
                    }
                });
            }
        }
    }

    PKInfo[] refreshPKInfo(PKResult pkResult) {
        PKInfo[] pkInfos = formatPKInfo(pkResult);
        if (null != pkInfos) {
            pkView.setPKUserInfo(pkInfos[0].getUserId(), pkInfos[1].getUserId());
            // set score
            pkView.setPKScore(pkInfos[0].getScore(), pkInfos[1].getScore());
            // left
            List<String> lefts = new ArrayList<>();
            List<User> lusers = pkInfos[0].getUserInfoList();
            int ls = null == lusers ? 0 : lusers.size();
            for (int i = 0; i < ls; i++) {
                lefts.add(lusers.get(i).getPortraitUrl());
            }
            // right
            List<String> rights = new ArrayList<>();
            List<User> rusers = pkInfos[1].getUserInfoList();
            int rs = null == rusers ? 0 : rusers.size();
            for (int i = 0; i < rs; i++) {
                rights.add(rusers.get(i).getPortraitUrl());
            }
            pkView.setGiftSenderRank(lefts, rights);
        }
        return pkInfos;
    }

    /**
     * 格式化pk信息
     *
     * @return PKInfo[2]
     * left：index = 0
     * right：index = 1
     */
    private PKInfo[] formatPKInfo(PKResult pkResult) {
        if (null == pkResult) {
            return null;
        }
        List<PKInfo> pkInfos = pkResult.getRoomScores();
        if (null == pkInfos || 2 != pkInfos.size()) {
            return null;
        }
        PKInfo[] result = new PKInfo[2];
        PKInfo first = pkInfos.get(0);
        if (TextUtils.equals(roomId, first.getRoomId())) {
            result[0] = first;
            result[1] = pkInfos.get(1);
        } else {
            result[0] = pkInfos.get(1);
            result[1] = first;
        }
        return result;
    }

    @Override
    public void unInit() {
        pkManager = null;
    }

    @Override
    public void showPkInvitation(Activity activity) {
        if (pkState.enableInvite()) {
            if (dialog != null) dialog.dismiss();
            dialog = new OnlineCreatorDialog(activity, roomType, new OnlineCreatorDialog.OnSendPkCallback() {
                @Override
                public void sendPkInvitation(String inviteeRoomId, String inviteeUserId) {
                    pkInvitee = new PKInvitee();
                    pkInvitee.inviteeRoomId = inviteeRoomId;
                    pkInvitee.inviteeId = inviteeUserId;
                    if (null != pkListener)
                        pkListener.onSendPKInvitation(inviteeRoomId, inviteeUserId, new IResultBack<Boolean>() {
                            @Override
                            public void onResult(Boolean aBoolean) {
                                if (aBoolean) {
                                    KToast.show("发送PK邀请成功");
                                    // 邀请成功 修改pk状态
                                    onPkStateChanged(PKState.PK_INVITE);
                                } else {
                                    KToast.show("发送PK邀请失败");
                                }
                            }
                        });
                }
            }).setOnCancelListener(this);
            dialog.show();
        }
    }

    @Override
    public void showCancelPkInvitation(Activity activity) {
        if (pkState.enableCancelInvite()) {
            if (dialog != null) dialog.dismiss();
            dialog = new CancelPKDialog(activity, new IResultBack<Boolean>() {
                @Override
                public void onResult(Boolean aBoolean) {
                    if (aBoolean) {
                        if (null == pkInvitee) {
                            KToast.show("您还未发出PK邀请");
                            return;
                        }
                        if (null != pkListener)
                            pkListener.onCancelPkInvitation(pkInvitee.inviteeRoomId, pkInvitee.inviteeId, new IResultBack<Boolean>() {
                                @Override
                                public void onResult(Boolean aBoolean) {
                                    if (aBoolean) {
                                        KToast.show("取消PK邀请成功");
                                        onPkStateChanged(PKState.PK_NONE);
                                    } else {
                                        KToast.show("取消PK邀请失败");
                                    }
                                }
                            });
                    }
                }
            }).setOnCancelListener(this);
            dialog.show();
        }
    }

    @Override
    public void showQuitPK(Activity activity) {
        if (!pkState.isInPk()) {
            KToast.show("请先发起PK");
            return;
        }
        VRCenterDialog dialog = new VRCenterDialog(activity, null);
        dialog.replaceContent(activity.getString(R.string.quit_pk_dialog_tip), activity.getString(R.string.dialog_cancle), null, activity.getString(R.string.dialog_agree), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PKApi.reportPKEnd(roomId, pkRoomId, new IResultBack<Boolean>() {
                    @Override
                    public void onResult(Boolean s) {
                        if (s) {
                            if (pkListener != null)
                                pkListener.onQuitPK(new IResultBack<Boolean>() {
                                    @Override
                                    public void onResult(Boolean aBoolean) {
                                        if (aBoolean) {
                                            onPkStateChanged(PKState.PK_NONE);
                                        } else {
                                            KToast.show("PK结束失败");
                                        }
                                    }
                                });
                        } else {
                            KToast.show("PK结束失败");
                        }
                    }
                });
            }
        }, null);
        dialog.show();
    }

    @Override
    public void refreshPKGiftRank() {
        PKApi.getPKInfo(roomId, new IResultBack<PKResult>() {
            @Override
            public void onResult(PKResult pkResult) {
                refreshPKInfo(pkResult);
            }
        });
    }

    @Override
    public void onMessageReceived(Message message) {
        if (message.getContent() instanceof RCChatroomPK) {
            RCChatroomPK chatroomPK = (RCChatroomPK) message.getContent();
            String state = chatroomPK.getStatusMsg();
            Logger.d(TAG, "state = " + state);
            Logger.d(TAG, "chatroomPK = " + GsonUtil.obj2Json(chatroomPK));
            if ("0".equals(state)) {// 开始
                onPkStateChanged(PKState.PK_START);
                handlePKStart();
            } else if ("1".equals(state)) {//惩罚
                onPkStateChanged(PKState.PK_PUNISH);
                handlePkPunish();
            } else {// pk finish
                onPkStateChanged(PKState.PK_STOP);
                handlePKStop(chatroomPK);
                // pk结束者房间id
                String stopId = chatroomPK.getStopPkRoomId();
                if (TextUtils.isEmpty(stopId)) {
                    KToast.show("本轮PK结束");
                } else {
                    if (TextUtils.equals(stopId, roomId)) {// 当前房主结束
                        KToast.show("我方挂断，本轮PK结束");
                    } else {
                        KToast.show("对方挂断，本轮PK结束");
                    }
                }
            }
        } else if (message.getContent() instanceof RCChatroomPKGift) {
            Logger.e(TAG, "礼物消息");
            refreshPKGiftRank();
        }
    }

    /**
     * 接收到服务下发的pk开启消息 执行
     * 处理pk开始阶段
     * <p>
     * 注意：pkResult 不为空说明是init check inPK 传过来的 不需要再获取pkinfo
     */
    void handlePKStart() {
        Logger.e(TAG, "PK Start");
        if (null != pkListener) pkListener.onPkStart();
        // pk 开始后隐藏邀请界面
        if (dialog != null) dialog.dismiss();
        // 首次刷新pk信息
        PKApi.getPKInfo(roomId, new IResultBack<PKResult>() {
            @Override
            public void onResult(PKResult pkResult) {
                if (null == pkView) return;
                pkView.reset(isPKer());
                PKInfo[] pkInfos = refreshPKInfo(pkResult);
                if (null != pkInfos && isPKer()) {
                    String pkId = pkInfos[1].getUserId();
                    if (pkListener != null) pkListener.onSendPKStartMessage(pkId);
                }
                if (isPKer()) {//锁麦所有 ->
                    if (null != pkListener)
                        pkListener.lockAllAndKickOut();
                }
                long timeDiff = null == pkResult ? -1 : pkResult.getTimeDiff();
                pkView.pkStart(timeDiff, new IPK.OnTimerEndListener() {
                    @Override
                    public void onTimerEnd() {
                        // 等待服务端分发pk惩罚消息
                    }
                });
            }
        });
    }

    /**
     * 处理pk惩罚阶段
     * 2、惩罚记时结束：邀请者 quitPK,接收者：记录标识
     */
    void handlePkPunish() {
        Logger.e(TAG, "PK Punish");
        //惩罚记时
        if (null != pkView) {
            int pk = pkView.getPKResult();
            if (null != pkListener && isPKer()) {
                pkListener.onSendPKMessage(pk == 0 ? "平局" : (pk > 0 ? "我方 PK 胜利" : "我方 PK 失败"));
            }
            pkView.pkPunish(-1, new IPK.OnTimerEndListener() {
                @Override
                public void onTimerEnd() {
                    // 等待服务端分发pk结束消息
                }
            });
        }
    }

    /**
     * 处理pk结束
     * 接收到服务下发的pk结束消息 执行
     * 1、邀请者调用SDK quitPk 结束pk
     * 2、被邀请者 暂不处理
     */
    void handlePKStop(RCChatroomPK chatroomPK) {
        Logger.e(TAG, "PK Stop");
        if (null != pkListener) pkListener.onPkStop();
        if (null != pkView) pkView.pkStop();
        if (null != pkListener && isPKer()) {
            KToast.show("本轮PK结束");
        }
        if (isPKer()) {//取消锁麦
            if (null != pkListener) pkListener.unLockAll();
        }
        if (null != chatroomPK && TextUtils.isEmpty(chatroomPK.getStopPkRoomId())) {
            //倒计时 自动挂断
            if (isInviter()) {
                //约定邀请者 quitpk
                if (pkListener != null)
                    pkListener.onQuitPK(new IResultBack<Boolean>() {
                        @Override
                        public void onResult(Boolean aBoolean) {
                            if (aBoolean) {
                                onPkStateChanged(PKState.PK_NONE);
                            } else {
                                KToast.show("PK结束失败");
                            }
                        }
                    });
            } else {
                // 受邀者 修改惩罚结束标识 等待pkFinish回调
            }
        } else {
            //主动挂断 不需要处理
        }
    }

    @Override
    public void onPKBegin(PKInviteInfo pkInviteInfo) {
        this.pkInviteInfo = pkInviteInfo;
        onPkStateChanged(PKState.PK_GOING);
        if (pkInviteInfo != null) {
//            pkRoomId = TextUtils.equals(pkInviteInfo.getInviteeRoomId(), roomId) ? pkInviteInfo.getInviterRoomId() : pkInviteInfo.getInviteeRoomId();
            pkRoomId = TextUtils.equals(pkInviteInfo.getInviteeUserId(), UserManager.get().getUserId()) ? pkInviteInfo.getInviterRoomId() : pkInviteInfo.getInviteeRoomId();
            //  pk信息中的自己房间
            String plcRoomId = TextUtils.equals(pkInviteInfo.getInviteeUserId(), UserManager.get().getUserId()) ? pkInviteInfo.getInviteeRoomId() : pkInviteInfo.getInviterRoomId();
            // 1、约定 邀请者开启pk 上报pk开始状态
            // 2、被邀请者 暂不处理，等待服务端pk开启消息
            Logger.e(TAG,"plcRoomId = "+plcRoomId);
            Logger.e(TAG,"roomId = "+roomId);
            if (isInviter() && TextUtils.equals(plcRoomId,roomId)) {
                PKApi.reportPKStart(roomId, pkRoomId, new IResultBack<Boolean>() {
                    @Override
                    public void onResult(Boolean success) {
                        // 等待 PK_START
                    }
                });
            }
        }
    }

    /**
     * @return 是否是邀请者
     */
    private boolean isInviter() {
        return null != pkInviteInfo && UserManager.get().getUserId().equals(pkInviteInfo.getInviterUserId());
    }

    /**
     * @return 是否是PKer
     */
    boolean isPKer() {
        String currentId = UserManager.get().getUserId();
        return null != pkInviteInfo && ((TextUtils.equals(roomId, pkInviteInfo.getInviteeRoomId()) && TextUtils.equals(currentId, pkInviteInfo.getInviteeUserId())) ||
                (TextUtils.equals(roomId, pkInviteInfo.getInviterRoomId()) && TextUtils.equals(currentId, pkInviteInfo.getInviterUserId())));
    }

    @Override
    public void onPKFinish() {
        Logger.d(TAG, "onPKFinish");
        onPkStateChanged(PKState.PK_FINISH);
        handlePKStop(null);
    }

    @Override
    public void onReceivePKInvitation(String inviterRoomId, String inviterUserId) {
        Logger.d(TAG, "onReceivePKInvitation");
        //保存邀请者信息
        pkInviter = new PKInviter();
        pkInviter.inviterRoomId = inviterRoomId;
        pkInviter.inviterId = inviterUserId;
        onPkStateChanged(PKState.PK_INVITE);
        requestPKDialog = new RequestPKDialog(UIStack.getInstance().getTopActivity(), new RequestPKDialog.OnClickAction() {
            @Override
            public void onAction(PKResponse pkResponse) {
                if (pkListener != null)
                    pkListener.responsePKInvitation(inviterRoomId, inviterUserId, pkResponse, new IResultBack<Boolean>() {
                        @Override
                        public void onResult(Boolean aBoolean) {
                            if (aBoolean) {
                                if (PKResponse.accept == pkResponse) {
                                    KToast.show("同意邀请");
                                } else if (PKResponse.reject == pkResponse) {
                                    KToast.show("已拒绝PK邀请");
                                } else {
                                    KToast.show("邀请被取消");
                                }
                                //判断是否是当前正在邀请的信息
                                if (pkInviter != null && TextUtils.equals(pkInviter.inviterId, inviterUserId) && TextUtils.equals(pkInviter.inviterRoomId, inviterRoomId)) {
                                    //处理成功后 该邀请流程结束 释放被邀请信息
                                    pkInviter = null;
                                }
                                //修改当前pk状态:拒绝和忽略需重置状态none
                                if (PKResponse.ignore == pkResponse || PKResponse.reject == pkResponse) {
                                    onPkStateChanged(PKState.PK_NONE);
                                }
                            } else {
                                KToast.show("响应PK邀请失败");
                            }
                        }
                    });
            }
        });
        UserProvider.provider().getAsyn(inviterUserId, new IResultBack<UserInfo>() {
            @Override
            public void onResult(UserInfo userInfo) {
                String name = null != userInfo ? userInfo.getName() : inviterUserId;
                String tips = "房主 " + name + " 邀请您进行PK，是否同意？";
                requestPKDialog.show(tips);
            }
        });

    }

    @Override
    public void onPKInvitationCanceled(String inviterRoomId, String inviterUserId) {
        Logger.d(TAG, "onPKInvitationCanceled");
        if (requestPKDialog != null) requestPKDialog.dismiss();
        if (null != pkInviter) {
            // 调用忽略是 邀请双方都会执行这个回调，但是主动调用忽略 会将pkInviter释放，可以判断
            KToast.show("邀请已被取消");
        }
        // 释放邀请者信息
        pkInviter = null;
        onPkStateChanged(PKState.PK_NONE);
    }

    @Override
    public void onPKInvitationRejected(String inviteeRoomId, String inviteeUserId, PKResponse reason) {
        Logger.d(TAG, "onPKInvitationRejected");

        //判断是否是当前正在邀请的信息
        if (pkInvitee != null && TextUtils.equals(inviteeRoomId, pkInvitee.inviteeRoomId) && TextUtils.equals(inviteeUserId, pkInvitee.inviteeId)) {
            switch (reason) {
                case reject:
                    KToast.show("对方拒绝了PK邀请");
                    break;
                case ignore:
                    KToast.show("对方无回应，PK发起失败");
                    break;
                case busy:
                    KToast.show("对方正忙");
                    break;
            }

            //邀请被忽略 该邀请流程结束 释放被邀请信息
            pkInvitee = null;
        }
        onPkStateChanged(PKState.PK_NONE);
    }

    @Override
    public void enterPkWithAnimation(View out, View in, long duration) {
        startAnimation(out, R.anim.anim_left_out, duration, true);
        startAnimation(in, R.anim.anim_right_in, duration, false);
    }

    @Override
    public void quitPkWithAnimation(View out, View in, long duration) {
        startAnimation(out, R.anim.anim_right_out, duration, true);
        startAnimation(in, R.anim.anim_left_in, duration, false);
    }

    @Override
    public void mutePkView(boolean isMute) {
        if (pkState.isInPk()) {
            this.isMute = isMute;
            pkView.setMute(isMute);
        }
    }

    @Override
    public void refreshPKFromServer() {
        PKApi.getPKInfo(roomId, new IResultBack<PKResult>() {
            @Override
            public void onResult(PKResult pkResult) {
                Logger.d(TAG, GsonUtil.obj2Json(pkResult));
                if (null == pkResult || pkResult.getStatusMsg() == -1 || pkResult.getStatusMsg() == 2) {
                    Logger.e(TAG, "init: Not In PK");
                    pkState = PKState.PK_NONE;
                    if (pkListener != null) pkListener.quitPKIfPKing();
                    return;
                }
                long timeDiff = null == pkResult ? -1 : pkResult.getTimeDiff();
                int state = pkResult.getStatusMsg();
                if (0 == state) {// pk 阶段
                    pkView.pkStart(timeDiff, new IPK.OnTimerEndListener() {
                        @Override
                        public void onTimerEnd() {
                            // 等待服务端分发pk惩罚消息
                        }
                    });
                } else if (1 == state) {// pk 惩罚阶段
                    pkView.pkPunish(timeDiff, new IPK.OnTimerEndListener() {
                        @Override
                        public void onTimerEnd() {
                            // 等待服务端分发pk结束消息
                        }
                    });
                }
            }
        });
    }

    private void onPkStateChanged(PKState pkState) {
        Logger.d(TAG, "onPkStateChanged:" + pkState.name());
        this.pkState = pkState;
        if (null != pkListener) pkListener.onPkStateChanged(pkState);
    }

    /**
     * 开启动画
     *
     * @param view     视图
     * @param animalId anim资源id
     * @param duration 动画时长
     * @param out      是否离屏
     */
    private void startAnimation(View view, int animalId, long duration, boolean out) {
        if (null == view) return;
        Animation animation = AnimationUtils.loadAnimation(UIKit.getContext(), animalId);
        animation.setDuration(duration);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (!out) view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (out) view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(animation);
    }
}
