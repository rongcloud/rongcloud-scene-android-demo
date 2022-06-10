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
 * @time 6:40 下午
 * 社区相关接口的公共帮助类,保存当前所在 社区的信息,并且作为公共逻辑的处理中转站
 */
public class CommunityHelper implements ICommunityHelper, IUltraGroupUserEventListener,
        IUltraGroupChangeListener, IUltraGroupChannelListener, IUltraGroupUserInfoUpdateListener {


    private Queue<CommunityBean> browseCommunityBeans;
    private List<IUltraGroupUserEventListener> ultraGroupUserEventListeners;
    private List<IUltraGroupChannelListener> ultraGroupChannelListeners;
    private List<IUltraGroupChangeListener> iUltraGroupChangeListeners;
    private List<IUltraGroupUserInfoUpdateListener> ultraGroupUserInfoUpdateListeners;

    /**
     * 当前所在社区的实体类监听
     */
    public static MutableLiveData<CommunityDetailsBean> communityDetailsLiveData = new MutableLiveData<>();

    /**
     * 当前所在频道的实体类监听
     *
     * @return
     */
    public static MutableLiveData<ChannelDetailsBean> channelDetailsLiveData = new MutableLiveData<>();

    /**
     * 标注消息监听类
     */
    public static MutableLiveData<Message> markMessageLiveData = new MutableLiveData<>();


    private static CommunityHelper communityHelper = new CommunityHelper();


    public static CommunityHelper getInstance() {
        return communityHelper;
    }

    static {
        HashMap<Integer, Message> hashMap = new HashMap<>();
        //设置在前台其他页面的时候，是否有声音
        RongConfigCenter.notificationConfig().setForegroundOtherPageAction(NotificationConfig.ForegroundOtherPageAction.Silent);
        RongConfigCenter.notificationConfig().setInterceptor(new DefaultInterceptor() {

            /**
             * 是否拦截此本地通知，一般用于自定义本地通知的显示。
             *
             * @param message 本地通知对应的消息
             * @return 是否拦截。true 拦截本地通知，SDK 不弹出通知，需要用户自己处理。false 不拦截，由 SDK 展示本地通知。
             */
            @Override
            public boolean isNotificationIntercepted(Message message) {
                hashMap.put(message.getMessageId(), message);
                //系统消息不应该有提示音
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
             * 是否为高优先级消息。高优先级消息不受全局静默时间和会话免打扰控制，比如 @ 消息。
             *
             * @param message 接收到的消息
             * @return 是否为高优先级消息
             */
            @Override
            public boolean isHighPriorityMessage(Message message) {
                return false;
            }

            /**
             * 注册默认 channel 之前的回调。可以通过此方法拦截并修改默认 channel 里的配置，将修改后的 channel 返回。
             *
             * @param defaultChannel 默认通知频道
             * @return 修改后的通知频道。
             */
            @Override
            public NotificationChannel onRegisterChannel(NotificationChannel defaultChannel) {
                return defaultChannel;
            }

            /**
             * 设置本地通知 PendingIntent 时的回调。
             * 应用层可通过此方法更改 PendingIntent 里的设置，以便自定义本地通知的点击行为。
             * 点击本地通知时，SDK 默认跳转到对应会话页面。
             *
             * @param pendingIntent SDK 默认 PendingIntent
             * @param intent        pendingIntent 里携带的 intent。
             *                      可通过 intent 获取以下信息:
             *                      intent.getStringExtra(RouteUtils.CONVERSATION_TYPE);
             *                      intent.getStringExtra(RouteUtils.TARGET_ID);
             *                      intent.getIntExtra(RouteUtils.MESSAGE_ID, -1);;
             * @return 本地通知里需配置的 PendingIntent.
             */
            @Override
            public PendingIntent onPendingIntent(PendingIntent pendingIntent, Intent intent) {
                String conversation_type = intent.getStringExtra(RouteUtils.CONVERSATION_TYPE);
                if (conversation_type.equals(Conversation.ConversationType.ULTRA_GROUP.getName().toLowerCase())) {
                    //如果是超级群的话
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
     * 游客身份浏览的
     *
     * @param communityBean
     */
    public void addLastBrowseCommunityBean(CommunityBean communityBean) {
        browseCommunityBeans.clear();
        browseCommunityBeans.offer(communityBean);
    }

    /**
     * 获取最后访问的未加入的社区
     *
     * @return
     */
    public CommunityBean getLastBrowseCommunityBean() {
        return browseCommunityBeans.peek();
    }

    /**
     * 清空未加入社区的浏览记录
     *
     * @return
     */
    public void clearBrowsingHistory() {
        browseCommunityBeans.clear();
    }


    /**
     * 获取社区详情
     *
     * @return
     */
    public CommunityDetailsBean getCommunityDetailsBean() {
        return communityDetailsLiveData.getValue();
    }

    /**
     * 获取当前用户在社区的信息
     */
    public CommunityDetailsBean.CommunityUserBean getCommunityUserBean() {
        CommunityDetailsBean communityDetailsBean = getCommunityDetailsBean();
        if (communityDetailsBean != null) {
            return communityDetailsBean.getCommunityUser();
        }
        return null;
    }

    /**
     * 获取当前社区ID
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
     * 当前用户是否为当前社区的创建者
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
     * 获取在本社区的昵称
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
     * 更新在本社区的昵称
     */
    public void setNickName(String newNickName) {
        CommunityDetailsBean.CommunityUserBean communityUserBean = getCommunityUserBean();
        if (communityUserBean != null) {
            communityUserBean.setNickName(newNickName);
            communityDetailsLiveData.postValue(getCommunityDetailsBean());
        }
    }

    /**
     * 更新禁言状态
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
     * 获取禁言状态
     */
    public int getShutUp() {
        CommunityDetailsBean.CommunityUserBean communityUserBean = getCommunityUserBean();
        if (communityUserBean != null) {
            return getCommunityUserBean().getShutUp();
        }
        return -1;
    }

    /**
     * 获取当前频道ID
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
     * 关于社区用户的修改
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
     * 获取超级群信息
     *
     * @param targetId 社区id
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
                    communityDetailsLiveData.setValue(detailsBean);
                } else {
                    //当前社区已解散
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
     * 获取当前所在频道的详情
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
     * 获取当前频道的最新标注消息
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
                                    //继续往后去找可以显示的标注消息
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
                    //如果当前消息不存在或者当前消息已经被撤回了 ，那么继续循环刷新
                    resultBack.onResult(true);
                } else {
                    //如果当前消息已经存在了，那么直接刷新title
                    markMessageLiveData.postValue(message);
                }
            }
        });
    }


    /**
     * 社区整体保存
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
                    communityDetailsLiveData.postValue(communityDetailsBean);
                } else {
                    KToast.show(result.getMessage());
                }
                if (callback != null)
                    callback.onResult(result);
            }
        });
    }

    /**
     * 修改频道设置
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
                        //结束所有界面回到首页
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
     * 自己加入了某个社区
     *
     * @param targetId   社区 ID
     * @param fromUserId 操作人的ID
     * @param hint       提示
     */
    @Override
    public void onJoined(String targetId, String fromUserId, String hint) {
        //初次加入成功的时候，将默认的系统通知方式设置给自己，后面自己就不再受到默认方式修改的影响了
        UltraGroupNotificationLeaveManager.get().getUltraGroupNotificationLevel(targetId, null);
        CommunityBean lastBrowseCommunityBean = CommunityHelper.getInstance().getLastBrowseCommunityBean();
        if (lastBrowseCommunityBean != null && TextUtils.equals(lastBrowseCommunityBean.getCommunityUid(), targetId)) {
            //如果最后浏览的社区加入成功了，那么就没有最后浏览的社区记录了
            CommunityHelper.getInstance().clearBrowsingHistory();
        }
        for (IUltraGroupUserEventListener ultraGroupUserEventListener : ultraGroupUserEventListeners) {
            ultraGroupUserEventListener.onJoined(targetId, fromUserId, hint);
        }
    }

    /**
     * 被拒绝加入某个社区
     *
     * @param targetId   社区 ID
     * @param fromUserId 操作人的ID
     * @param hint       提示
     */
    @Override
    public void onRejected(String targetId, String fromUserId, String hint) {
        if (TextUtils.equals(targetId, getCommunityUid())) {
            getCommunityUserBean().setAuditStatus(Constants.AuditStatus.AUDIT_FAILED.getCode());
            CommunityHelper.communityDetailsLiveData.postValue(getCommunityDetailsBean());
        }
    }

    /**
     * 自己离开了社区
     *
     * @param targetId   社区ID
     * @param fromUserId 操作人ID
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
     * 收到了请求
     *
     * @param targetId   社区ID
     * @param fromUserId 操作人ID
     * @param hint
     */
    @Override
    public void onRequestJoin(String targetId, String fromUserId, String hint) {

    }

    @Override
    public void onBeForbidden(String targetId, String fromUserId, String toUserId, String hint) {
        if (TextUtils.equals(getCommunityUid(), targetId) && TextUtils.equals(RongCoreClient.getInstance().getCurrentUserId(), toUserId)) {
            //判断本人是否在该社区被禁言,并保持更新
            KToast.show(hint);
            CommunityHelper.getInstance().setShutUp(Integer.parseInt(Constants.SHUT_UP));
        }
    }

    @Override
    public void onCancelForbidden(String targetId, String fromUserId, String toUserId, String hint) {
        if (TextUtils.equals(getCommunityUid(), targetId) && TextUtils.equals(RongCoreClient.getInstance().getCurrentUserId(), toUserId)) {
            //判断本人是否在该社区被禁言,并保持更新
            KToast.show(hint);
            CommunityHelper.getInstance().setShutUp(Integer.parseInt(Constants.NOT_SHUT_UP));
        }
    }

    /**
     * 更新用户信息
     *
     * @param userId
     * @param userName
     * @param portrait
     */
    @Override
    public void onUpdateUserInfo(String userId, String userName, String portrait) {
        //更新缓存信息,保证以及看过的都是最新的 
        if (UltraGroupUserManager.getInstance().contains(userId)) {
            UltraGroupUserBean sync = UltraGroupUserManager.getInstance().getSync(userId);
            sync.setNickName(userName);
            sync.setPortrait(portrait);
            UltraGroupUserManager.getInstance().update(sync);
        }
    }

    /**
     * 社区发生了改变的时候
     *
     * @param targetId 社区ID
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
     * 当社区被解散的时候
     *
     * @param targetId 社区ID
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
                deleteDialog.replaceContent("当前社区已经解散", "", null, ResUtil.getString(R.string.cmu_sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //结束所有界面回到首页
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
     * 当有用户加入了社区
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
