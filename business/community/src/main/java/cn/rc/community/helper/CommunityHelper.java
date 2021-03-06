package cn.rc.community.helper;


import static cn.rc.community.channel.ConversionActivity.CHANNEL_ID;
import static cn.rc.community.channel.ConversionActivity.KET_TYPE;
import static io.rong.common.SystemUtils.getApplicationContext;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.android.arouter.launcher.ARouter;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.core.Dispatcher;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.ui.UIStack;
import com.basis.utils.GsonUtil;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.ResUtil;
import com.basis.wapper.IResultBack;
import com.basis.widget.dialog.VRCenterDialog;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import cn.rc.community.CommunityAPI;
import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.bean.ChannelDetailsBean;
import cn.rc.community.bean.CommunityBean;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.bean.MarkMessage;
import cn.rc.community.bean.UltraGroupUserBean;
import cn.rc.community.channel.ConversionActivity;
import cn.rc.community.conversion.controller.MessageManager;
import cn.rc.community.message.sysmsg.ChannelNoticeMsg;
import cn.rc.community.message.sysmsg.CommunityChangeMsg;
import cn.rc.community.message.sysmsg.CommunityDeleteMsg;
import cn.rc.community.message.sysmsg.CommunitySysNoticeMsg;
import cn.rc.community.message.sysmsg.UserUpdateMsg;
import cn.rc.community.utils.UltraGroupUserManager;
import cn.rongcloud.config.router.RouterPath;
import io.rong.imkit.config.RongConfigCenter;
import io.rong.imkit.notification.DefaultInterceptor;
import io.rong.imkit.notification.NotificationConfig;
import io.rong.imkit.notification.RongNotificationManager;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.ChannelClient;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.MessagePushConfig;
import io.rong.message.RecallNotificationMessage;
import io.rong.push.RongPushClient;
import io.rong.push.notification.RongNotificationHelper;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/30
 * @time 6:40 ??????
 * ????????????????????????????????????,?????????????????? ???????????????,??????????????????????????????????????????
 */
public class CommunityHelper implements ICommunityHelper, IUltraGroupUserEventListener,
        IUltraGroupChangeListener, IUltraGroupChannelListener, IUltraGroupUserInfoUpdateListener {


    private Queue<CommunityBean> browseCommunityBeans;
    private List<IUltraGroupUserEventListener> ultraGroupUserEventListeners;
    private List<IUltraGroupChannelListener> ultraGroupChannelListeners;
    private List<IUltraGroupChangeListener> iUltraGroupChangeListeners;
    private List<IUltraGroupUserInfoUpdateListener> ultraGroupUserInfoUpdateListeners;

    /**
     * ????????????????????????????????????
     */
    public static MutableLiveData<CommunityDetailsBean> communityDetailsLiveData = new MutableLiveData<>();

    /**
     * ????????????????????????????????????
     * communityDetailsLiveData
     *
     * @return
     */
    public static MutableLiveData<ChannelDetailsBean> channelDetailsLiveData = new MutableLiveData<>();

    /**
     * ?????????????????????
     */
    public static MutableLiveData<Message> markMessageLiveData = new MutableLiveData<>();


    private static CommunityHelper communityHelper = new CommunityHelper();


    public static CommunityHelper getInstance() {
        return communityHelper;
    }

    static {
        HashMap<Integer, Message> hashMap = new HashMap<>();
        //??????????????????????????????????????????????????????
        RongConfigCenter.notificationConfig().setForegroundOtherPageAction(NotificationConfig.ForegroundOtherPageAction.Silent);
        RongConfigCenter.notificationConfig().setInterceptor(new DefaultInterceptor() {

            /**
             * ???????????????????????????????????????????????????????????????????????????
             *
             * @param message ???????????????????????????
             * @return ???????????????true ?????????????????????SDK ?????????????????????????????????????????????false ??????????????? SDK ?????????????????????
             */
            @Override
            public boolean isNotificationIntercepted(Message message) {
                hashMap.put(message.getMessageId(), message);
                //?????????????????????????????????
                MessageContent content = message.getContent();
                if (content instanceof ChannelNoticeMsg ||
                        content instanceof CommunityDeleteMsg ||
                        content instanceof UserUpdateMsg ||
                        content instanceof CommunityChangeMsg ||
                        content instanceof CommunitySysNoticeMsg) {
                    return true;
                }
                return false;
            }

            /**
             * ????????????????????????????????????????????????????????????????????????????????????????????????????????? @ ?????????
             *
             * @param message ??????????????????
             * @return ???????????????????????????
             */
            @Override
            public boolean isHighPriorityMessage(Message message) {
                return false;
            }

            /**
             * ???????????? channel ???????????????????????????????????????????????????????????? channel ?????????????????????????????? channel ?????????
             *
             * @param defaultChannel ??????????????????
             * @return ???????????????????????????
             */
            @Override
            public NotificationChannel onRegisterChannel(NotificationChannel defaultChannel) {
                return defaultChannel;
            }

            /**
             * ?????????????????? PendingIntent ???????????????
             * ????????????????????????????????? PendingIntent ????????????????????????????????????????????????????????????
             * ????????????????????????SDK ????????????????????????????????????
             *
             * @param pendingIntent SDK ?????? PendingIntent
             * @param intent        pendingIntent ???????????? intent???
             *                      ????????? intent ??????????????????:
             *                      intent.getStringExtra(RouteUtils.CONVERSATION_TYPE);
             *                      intent.getStringExtra(RouteUtils.TARGET_ID);
             *                      intent.getIntExtra(RouteUtils.MESSAGE_ID, -1);;
             * @return ??????????????????????????? PendingIntent.
             */
            @Override
            public PendingIntent onPendingIntent(PendingIntent pendingIntent, Intent intent) {
                String conversation_type = intent.getStringExtra(RouteUtils.CONVERSATION_TYPE);
                if (conversation_type.equals(Conversation.ConversationType.ULTRA_GROUP.getName().toLowerCase())) {
                    //????????????????????????
                    int messageId = intent.getIntExtra(RouteUtils.MESSAGE_ID, -1);
                    Message message = hashMap.get(messageId);
                    if (message != null) {
                        Intent intentNew = new Intent(getApplicationContext(), ConversionActivity.class);
                        intentNew.putExtra(CHANNEL_ID, message.getChannelId());
                        intentNew.putExtra(KET_TYPE, 0);
                        return PendingIntent.getActivity(getApplicationContext(), 1, intentNew, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                }
                return pendingIntent;
            }
        });
    }

    public CommunityHelper() {
        browseCommunityBeans = new LinkedBlockingQueue<>(1);
        this.ultraGroupUserEventListeners = new ArrayList<>();
        this.ultraGroupChannelListeners = new ArrayList<>();
        this.iUltraGroupChangeListeners = new ArrayList<>();
        this.ultraGroupUserInfoUpdateListeners = new ArrayList<>();

        UltraGroupCenter.getInstance().addIUltraGroupUserEventListener(this);
        UltraGroupCenter.getInstance().addIUltraGroupChannelListener(this);
        UltraGroupCenter.getInstance().addIUltraGroupChangeListener(this);
        UltraGroupCenter.getInstance().addIUltraGroupUserInfoUpdateListener(this);
    }

    public void addIUltraGroupUserEventListener(IUltraGroupUserEventListener listener) {
        ultraGroupUserEventListeners.add(listener);
    }

    public void removeIUltraGroupUserEventListener(IUltraGroupUserEventListener listener) {
        if (ultraGroupUserEventListeners.contains(listener)) {
            ultraGroupUserEventListeners.remove(listener);
        }
    }

    public void addIUltraGroupChannelListener(IUltraGroupChannelListener listener) {
        ultraGroupChannelListeners.add(listener);
    }

    public void removeIUltraGroupChannelListener(IUltraGroupChannelListener listener) {
        if (ultraGroupChannelListeners.contains(listener)) {
            ultraGroupChannelListeners.remove(listener);
        }
    }

    public void addIUltraGroupChangeListener(IUltraGroupChangeListener listener) {
        iUltraGroupChangeListeners.add(listener);
    }

    public void removeIUltraGroupChangeListener(IUltraGroupChangeListener listener) {
        if (iUltraGroupChangeListeners.contains(listener)) {
            iUltraGroupChangeListeners.remove(listener);
        }
    }

    public void addIUltraGroupUserInfoUpdateListener(IUltraGroupUserInfoUpdateListener listener) {
        ultraGroupUserInfoUpdateListeners.add(listener);
    }

    public void removeIUltraGroupUserInfoUpdateListener(IUltraGroupUserInfoUpdateListener listener) {
        if (ultraGroupUserInfoUpdateListeners.contains(listener)) {
            ultraGroupUserInfoUpdateListeners.remove(listener);
        }
    }

    /**
     * ?????????????????????
     *
     * @param communityBean
     */
    public void addLastBrowseCommunityBean(CommunityBean communityBean) {
        browseCommunityBeans.clear();
        browseCommunityBeans.offer(communityBean);
    }

    /**
     * ???????????????????????????????????????
     *
     * @return
     */
    public CommunityBean getLastBrowseCommunityBean() {
        return browseCommunityBeans.peek();
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    public void clearBrowsingHistory() {
        browseCommunityBeans.clear();
    }


    /**
     * ??????????????????
     *
     * @return
     */
    public CommunityDetailsBean getCommunityDetailsBean() {
        return communityDetailsLiveData.getValue();
    }

    /**
     * ????????????????????????????????????
     */
    public CommunityDetailsBean.CommunityUserBean getCommunityUserBean() {
        CommunityDetailsBean communityDetailsBean = getCommunityDetailsBean();
        if (communityDetailsBean != null) {
            return communityDetailsBean.getCommunityUser();
        }
        return null;
    }

    /**
     * ??????????????????ID
     *
     * @return
     */
    public String getCommunityUid() {
        if (communityDetailsLiveData != null && communityDetailsLiveData.getValue() != null) {
            return communityDetailsLiveData.getValue().getUid();
        }
        return null;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @return
     */
    public boolean isCreator() {
        if (communityDetailsLiveData != null && communityDetailsLiveData.getValue() != null) {
            return communityDetailsLiveData.getValue().isCreator();
        }
        return false;
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    public String getNickName() {
        CommunityDetailsBean.CommunityUserBean communityUserBean = getCommunityUserBean();
        if (communityUserBean != null) {
            return getCommunityUserBean().getNickName();
        }
        return "";
    }

    /**
     * ???????????????????????????
     */
    public void setNickName(String newNickName) {
        CommunityDetailsBean.CommunityUserBean communityUserBean = getCommunityUserBean();
        if (communityUserBean != null) {
            communityUserBean.setNickName(newNickName);
            communityDetailsLiveData.postValue(getCommunityDetailsBean());
        }
    }

    /**
     * ??????????????????
     *
     * @param shutUp
     */
    public void setShutUp(int shutUp) {
        CommunityDetailsBean.CommunityUserBean communityUserBean = getCommunityUserBean();
        if (communityUserBean != null) {
            communityUserBean.setShutUp(shutUp);
            communityDetailsLiveData.postValue(getCommunityDetailsBean());
        }
    }

    /**
     * ??????????????????
     */
    public int getShutUp() {
        CommunityDetailsBean.CommunityUserBean communityUserBean = getCommunityUserBean();
        if (communityUserBean != null) {
            return getCommunityUserBean().getShutUp();
        }
        return -1;
    }

    /**
     * ??????????????????ID
     *
     * @return
     */
    public String getChannelUid() {
        if (channelDetailsLiveData != null && channelDetailsLiveData.getValue() != null) {
            return channelDetailsLiveData.getValue().getUid();
        }
        return null;
    }


    /**
     * ???????????????????????????
     *
     * @param value
     */
    public void updateUserSetting(String userId, String key, String value, IResultBack<Wrapper> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityUid", getCommunityDetailsBean().getUid());
        params.put("userUid", userId);
        params.put(key, value);
        OkApi.post(CommunityAPI.Community_update_user_info, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (callback != null)
                    callback.onResult(result);
            }
        });
    }


    /**
     * ?????????????????????
     *
     * @param targetId ??????id
     * @param callback
     */
    public void getCommunityDetails(String targetId, IResultBack<CommunityDetailsBean> callback) {
        if (TextUtils.isEmpty(targetId)) {
            KToast.show(ResUtil.getString(R.string.cmu_community_uid_cant_empty));
            return;
        }

        OkApi.post(CommunityAPI.Community_Details + targetId, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                CommunityDetailsBean detailsBean = null;
                if (result.ok()) {
                    detailsBean = result.get(CommunityDetailsBean.class);
                    Logger.e("NeedAudit ??????", targetId+":"+detailsBean.getNeedAudit());
                    communityDetailsLiveData.setValue(detailsBean);
                } else {
                    //?????????????????????
                    if (callback == null) {
                        if (getLastBrowseCommunityBean() != null && getLastBrowseCommunityBean().getCommunityUid().equals(targetId)) {
                            clearBrowsingHistory();
                        }
                        showDeleteDialog(targetId);
                    }
                }
                if (callback != null)
                    callback.onResult(detailsBean);

            }
        });
    }

    /**
     * ?????????????????????????????????
     */
    public void getChannelDetails(String uid, IResultBack<ChannelDetailsBean> callback) {
        if (TextUtils.isEmpty(uid)) {
            KToast.show(ResUtil.getString(R.string.cmu_channel_uid_cant_empty));
            return;
        }

        OkApi.post(CommunityAPI.Channel_Details + uid, null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                ChannelDetailsBean detailsBean = null;
                if (result.ok()) {
                    detailsBean = result.get(ChannelDetailsBean.class);
                    channelDetailsLiveData.setValue(detailsBean);
                }
                if (callback != null)
                    callback.onResult(detailsBean);

            }
        });
    }

    /**
     * ???????????????????????????????????????
     */
    int i;

    public void getNewsMarkMessages() {
        Map<String, Object> params = new HashMap<>(4);
        params.put("channelUid", getChannelUid());
        params.put("pageNum", 0);
        params.put("pageSize", 100);
        OkApi.post(CommunityAPI.CHANNEL_MARK_MSG, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    i = 0;
                    List<MarkMessage> messages = result.getList("records", MarkMessage.class);
                    if (messages.size() > 0) {
                        IResultBack<Boolean> iResultBack = new IResultBack<Boolean>() {
                            @Override
                            public void onResult(Boolean aBoolean) {
                                if (aBoolean) {
                                    //?????????????????????????????????????????????
                                    i++;
                                    if (messages.size() > i) {
                                        refreshMark(messages.get(i).getMessageUid(), this::onResult);
                                    } else {
                                        markMessageLiveData.postValue(null);
                                    }
                                }
                            }
                        };
                        refreshMark(messages.get(i).getMessageUid(), iResultBack);
                    } else {
                        markMessageLiveData.postValue(null);
                    }
                } else {
                    markMessageLiveData.postValue(null);
                }
            }
        });
    }

    private void refreshMark(String messageId, IResultBack<Boolean> resultBack) {
        MessageManager.get().getMessage(messageId, new IResultBack<Message>() {
            @Override
            public void onResult(Message message) {
                if (message == null || message.getContent() instanceof RecallNotificationMessage) {
                    //??????????????????????????????????????????????????????????????? ???????????????????????????
                    resultBack.onResult(true);
                } else {
                    //??????????????????????????????????????????????????????title
                    markMessageLiveData.postValue(message);
                }
            }
        });
    }


    /**
     * ??????????????????
     */

    public void saveCommunityAll(CommunityDetailsBean communityDetailsBean, IResultBack<Wrapper> callback) {
        String jsonStr = GsonUtil.obj2Json(communityDetailsBean);
        Map<String, Object> params = GsonUtil.json2Map(jsonStr, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        OkApi.post(CommunityAPI.Community_save_all, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    KToast.show(ResUtil.getString(R.string.cmu_save_success));
//                    communityDetailsLiveData.setValue(communityDetailsBean);
                } else {
                    KToast.show(result.getMessage());
                }
                if (callback != null)
                    callback.onResult(result);
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param channelUid
     * @param noticeType
     * @param callback
     */
    public void updateChannelSetting(String channelUid, int noticeType, IResultBack<Wrapper> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("communityUid", getCommunityDetailsBean().getUid());
        params.put("channelUid", channelUid);
        params.put("noticeType", noticeType);
        OkApi.post(CommunityAPI.Community_update_channel_setting, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (callback != null)
                    callback.onResult(result);
            }
        });

    }


    public void registerMessageTypes(Class<? extends MessageContent>... classes) {
        RongCoreClient.registerMessageType(Arrays.asList(classes));
    }


    @Override
    public void getConversationListForAllChannel(IResultBack iResultBack) {

    }


    @Override
    public void deleteUltraGroupMessagesForAllChannel(long timestamp, IRongCoreCallback.ResultCallback<Boolean> callback) {

    }

    @Override
    public void deleteUltraGroupMessages(String targetId, String channelId, long timestamp, IRongCoreCallback.ResultCallback<Boolean> callback) {

    }

    @Override
    public void deleteRemoteUltraGroupMessages(String targetId, String channelId, long timestamp, IRongCoreCallback.OperationCallback callback) {

    }

    @Override
    public void getUltraGroupUnreadMentionedCount(String targetId, IRongCoreCallback.ResultCallback<Integer> callback) {

    }

    @Override
    public void getUltraGroupUnreadMentionedCount(String targetId, String channelId, IRongCoreCallback.ResultCallback<Integer> callback) {

    }

    @Override
    public void onKickOut(String targetId, String fromUserId, String hint) {
        UltraGroupNotificationLeaveManager.get().setUltraGroupNotificationLevel(targetId, IRongCoreEnum.PushNotificationLevel.PUSH_NOTIFICATION_LEVEL_DEFAULT);
        if (!TextUtils.equals(targetId, getCommunityUid())) {
            for (IUltraGroupUserEventListener ultraGroupUserEventListener : ultraGroupUserEventListeners) {
                ultraGroupUserEventListener.onKickOut(targetId, fromUserId, hint);
            }
            return;
        }
        Dispatcher.get().dispatch(new Runnable() {
            @Override
            public void run() {
                VRCenterDialog confirmDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
                confirmDialog.replaceContent(hint, "", null, ResUtil.getString(R.string.cmu_sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //??????????????????????????????
                        ARouter.getInstance().build(RouterPath.ROUTER_MAIN).navigation();
                        for (IUltraGroupUserEventListener ultraGroupUserEventListener : ultraGroupUserEventListeners) {
                            ultraGroupUserEventListener.onKickOut(targetId, fromUserId, hint);
                        }
                    }
                }, null);
                confirmDialog.show();
            }
        });
    }


    /**
     * ???????????????????????????
     *
     * @param targetId   ?????? ID
     * @param fromUserId ????????????ID
     * @param hint       ??????
     */
    @Override
    public void onJoined(String targetId, String fromUserId, String hint) {
        //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        UltraGroupNotificationLeaveManager.get().getUltraGroupNotificationLevel(targetId, null);
        CommunityBean lastBrowseCommunityBean = CommunityHelper.getInstance().getLastBrowseCommunityBean();
        if (lastBrowseCommunityBean != null && TextUtils.equals(lastBrowseCommunityBean.getCommunityUid(), targetId)) {
            //??????????????????????????????????????????????????????????????????????????????????????????
            CommunityHelper.getInstance().clearBrowsingHistory();
        }
        for (IUltraGroupUserEventListener ultraGroupUserEventListener : ultraGroupUserEventListeners) {
            ultraGroupUserEventListener.onJoined(targetId, fromUserId, hint);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param targetId   ?????? ID
     * @param fromUserId ????????????ID
     * @param hint       ??????
     */
    @Override
    public void onRejected(String targetId, String fromUserId, String hint) {
        if (TextUtils.equals(targetId, getCommunityUid())) {
            getCommunityUserBean().setAuditStatus(Constants.AuditStatus.AUDIT_FAILED.getCode());
            CommunityHelper.communityDetailsLiveData.postValue(getCommunityDetailsBean());
        }
    }

    /**
     * ?????????????????????
     *
     * @param targetId   ??????ID
     * @param fromUserId ?????????ID
     * @param hint
     */
    @Override
    public void onLeft(String targetId, String fromUserId, String hint) {
        UltraGroupNotificationLeaveManager.get().setUltraGroupNotificationLevel(targetId, IRongCoreEnum.PushNotificationLevel.PUSH_NOTIFICATION_LEVEL_DEFAULT);
        for (IUltraGroupUserEventListener ultraGroupUserEventListener : ultraGroupUserEventListeners) {
            ultraGroupUserEventListener.onLeft(targetId, fromUserId, hint);
        }
    }

    /**
     * ???????????????
     *
     * @param targetId   ??????ID
     * @param fromUserId ?????????ID
     * @param hint
     */
    @Override
    public void onRequestJoin(String targetId, String fromUserId, String hint) {

    }

    @Override
    public void onBeForbidden(String targetId, String fromUserId, String toUserId, String hint) {
        if (TextUtils.equals(getCommunityUid(), targetId) && TextUtils.equals(RongCoreClient.getInstance().getCurrentUserId(), toUserId)) {
            //???????????????????????????????????????,???????????????
            KToast.show(hint);
            CommunityHelper.getInstance().setShutUp(Integer.parseInt(Constants.SHUT_UP));
        }
    }

    @Override
    public void onCancelForbidden(String targetId, String fromUserId, String toUserId, String hint) {
        if (TextUtils.equals(getCommunityUid(), targetId) && TextUtils.equals(RongCoreClient.getInstance().getCurrentUserId(), toUserId)) {
            //???????????????????????????????????????,???????????????
            KToast.show(hint);
            CommunityHelper.getInstance().setShutUp(Integer.parseInt(Constants.NOT_SHUT_UP));
        }
    }

    /**
     * ??????????????????
     *
     * @param userId
     * @param userName
     * @param portrait
     */
    @Override
    public void onUpdateUserInfo(String userId, String userName, String portrait) {
        //??????????????????,???????????????????????????????????? 
        if (UltraGroupUserManager.getInstance().contains(userId)) {
            UltraGroupUserBean sync = UltraGroupUserManager.getInstance().getSync(userId);
            sync.setNickName(userName);
            sync.setPortrait(portrait);
            UltraGroupUserManager.getInstance().update(sync);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param targetId ??????ID
     */
    @Override
    public void onUltraGroupChanged(String targetId) {
        for (IUltraGroupChangeListener iUltraGroupChangeListener : iUltraGroupChangeListeners) {
            iUltraGroupChangeListener.onUltraGroupChanged(targetId);
        }
    }

    @Override
    public void onChannelDeleted(String[] channelIds) {

    }

    /**
     * ???????????????????????????
     *
     * @param targetId ??????ID
     */
    @Override
    public void onUltraGroupDelete(String targetId) {
        if (!TextUtils.equals(targetId, getCommunityUid())) {
            for (IUltraGroupChangeListener iUltraGroupChangeListener : iUltraGroupChangeListeners) {
                iUltraGroupChangeListener.onUltraGroupDelete(targetId);
            }
            return;
        }
        showDeleteDialog(targetId);
    }


    private void showDeleteDialog(String targetId) {
        Dispatcher.get().dispatch(new Runnable() {
            @Override
            public void run() {
                VRCenterDialog deleteDialog = new VRCenterDialog(UIStack.getInstance().getTopActivity(), null);
                deleteDialog.replaceContent("????????????????????????", "", null, ResUtil.getString(R.string.cmu_sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //??????????????????????????????
                        ARouter.getInstance().build(RouterPath.ROUTER_MAIN).navigation();
                        for (IUltraGroupChangeListener iUltraGroupChangeListener : iUltraGroupChangeListeners) {
                            iUltraGroupChangeListener.onUltraGroupDelete(targetId);
                        }
                    }
                }, null);

                deleteDialog.show();
            }
        });

    }

    @Override
    public void onAddMarkMessage(String targetId, String channelId) {
        if (TextUtils.equals(channelId, getChannelUid())) {
            getNewsMarkMessages();
        }
    }

    @Override
    public void onRemoveMarkMessage(String targetId, String channelId) {
        if (TextUtils.equals(channelId, getChannelUid())) {
            getNewsMarkMessages();
        }
    }

    /**
     * ???????????????????????????
     *
     * @param targetId
     * @param fromUserId
     * @param toUserId
     * @param hint
     */
    @Override
    public void onUserJoined(String targetId, String fromUserId, String toUserId, String hint) {

    }

}
