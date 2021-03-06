package cn.rc.community.conversion.controller;


import android.text.TextUtils;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.api.core.Dispatcher;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.utils.DateFt;
import com.basis.utils.DateUtil;
import com.basis.utils.GsonUtil;
import com.basis.utils.Logger;
import com.basis.utils.TimeFt;
import com.basis.wapper.IResultBack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.community.CommunityAPI;
import cn.rc.community.conversion.ObjectName;
import cn.rc.community.conversion.controller.interfaces.IManager;
import cn.rc.community.conversion.controller.interfaces.IMessageAdapter;
import cn.rc.community.conversion.sdk.SendMessageCallback;
import cn.rc.community.conversion.sdk.UltraApi;
import cn.rc.community.conversion.sdk.UltraGroupApi;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.helper.UltraGroupCenter;
import cn.rc.community.message.sysmsg.ChannelNoticeMsg;
import cn.rc.community.message.sysmsg.ChannelType;
import cn.rc.community.utils.UltraUnReadMessageManager;
import io.rong.imkit.IMCenter;
import io.rong.imlib.ChannelClient;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.IRongCoreListener;
import io.rong.imlib.RongCommonDefine;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.listener.OnReceiveMessageWrapperListener;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.HistoryMessageOption;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.ReceivedProfile;
import io.rong.imlib.model.UltraGroupTypingStatusInfo;
import io.rong.message.ImageMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.ReferenceMessage;
import io.rong.message.SightMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * ?????????&?????????????????????
 */
public class MessageManager extends OnReceiveMessageWrapperListener implements IManager,
        IRongCoreListener.UltraGroupTypingStatusListener, IRongCoreListener.UltraGroupMessageChangeListener {
    private final static String TAG = "MessageManager";
    private volatile static IManager _manager;
    protected final static Map<String, AttachedInfo> attachedInfoMap = new HashMap<>();
    private MessageAdapter<WrapperMessage> adapter;
    private RecyclerView recyclerView;
    private OnMessageAttachListener attachListener;
    private OnChannelListener onChannelListener;
    private String _targetId;
    private String _channelId;

    public static void registerAttachInfo(BaseMessageAttachedInfo attachedInfo) {
        if (null != attachedInfo && attachedInfo.checked()) {
            String objectName = attachedInfo.onSetObjectName();
            if (!attachedInfoMap.containsKey(objectName)) {
                attachedInfoMap.put(objectName, attachedInfo);
            }
        }
    }

    private MessageManager() {
        UltraGroupCenter.getInstance().addReceiveMessageWrapperListener(this);
        UltraGroupCenter.getInstance().addUltraGroupMessageChangeListener(this);
        UltraGroupCenter.getInstance().addUltraGroupTypingStatusListener(this);
    }

    public static IManager get() {
        if (null == _manager) {
            synchronized (MessageManager.class) {
                if (null == _manager) {
                    _manager = new MessageManager();
                }
            }
        }
        return _manager;
    }

    @Override
    public void setOnMessageAttachListener(OnMessageAttachListener attachListener) {
        this.attachListener = attachListener;
    }

    @Override
    public void setOnChannelListener(OnChannelListener onChannelListener) {
        this.onChannelListener = onChannelListener;
    }

    /**
     * ????????????
     *
     * @param message
     * @param profile
     */
    @Override
    public void onReceivedMessage(Message message, ReceivedProfile profile) {
        if (message != null && TextUtils.equals(message.getTargetId(), _targetId)) {
            //??????????????????
            if (TextUtils.equals(message.getChannelId(), _channelId)) {
                //??????????????????
                if (attachListener != null) attachListener.onReceivedMessage(message, profile);
            } else if (TextUtils.isEmpty(message.getChannelId())) {
                //?????????????????????????????????????????????
                if (attachListener != null) attachListener.onReceivedMessage(message, profile);
            }
        }
    }

    public void attachChannel(String targetId, String channelId) {
        _targetId = targetId;
        _channelId = channelId;
        adapter = new MessageAdapter<>();
    }

    @Override
    public void attachView(RecyclerView recyclerView) {
        if (null != adapter && null != recyclerView) {
            recyclerView.setAdapter(adapter);
            this.recyclerView = recyclerView;
        }
    }

    @Override
    public void setOnMessageLongClick(IMessageAdapter.OnItemLongClickListener<WrapperMessage> listener) {
        if (null != adapter) adapter.setOnItemLongClickListener(listener);
    }

    @Override
    public void setOnMessageClick(IMessageAdapter.OnItemClickListener<WrapperMessage> listener) {
        if (null != adapter) adapter.setOnItemClickListener(listener);
    }

    @Override
    public List<WrapperMessage> getMessages() {
        return null != adapter ? adapter.getMessages() : new ArrayList<>();
    }

    /**
     * ????????????
     *
     * @param message Message
     * @param last    ???????????????
     * @return
     */
    @Override
    public WrapperMessage insertMessage(Message message, boolean last, boolean isScrollBottom) {
        if (message.getContent() instanceof ChannelNoticeMsg && ((ChannelNoticeMsg) message.getContent()).getType() == ChannelType.quit) {
            //????????????????????????
            return null;
        }
        AttachedInfo attachedInfo = getAttachedInfo(message);
        if (attachedInfo == null) return null;
        WrapperMessage msg = WrapperMessage.fromMessage(message, attachedInfo);
        if (null != adapter) adapter.insert(msg, last);
        if (attachListener != null)
            Dispatcher.get().dispatch(new Runnable() {
                @Override
                public void run() {
                    if (null != attachedInfo) attachListener.onMessageAttach(msg, isScrollBottom);
                }
            });
        return msg;
    }

    /**
     * ??????????????????????????? AttachedInfo
     *
     * @param message
     * @return
     */
    private AttachedInfo getAttachedInfo(Message message) {
        if (null == message) return null;
        String objectName = message.getObjectName();
        Logger.d(TAG, "onReceived: objectName = " + objectName);
        AttachedInfo attachedInfo = attachedInfoMap.get(objectName);
        if (null == attachedInfo && null != attachListener) {
            attachedInfo = attachListener.onAttach(objectName);
            if (null != attachedInfo && attachedInfo.checked()) {
                attachedInfoMap.put(objectName, attachedInfo);
            }
        }
        if (null == attachedInfo) {
            Logger.e(TAG, "The Message for ObjName Is " + objectName + " not register Layout or OnConvertListener");
            return null;
        }
        return attachedInfo;
    }


    @Override
    public void sendMessage(MessageContent content, EditText editText, SendMessageCallback callback) {
        UltraGroupApi.getApi().sendMessage(_targetId, _channelId, editText, content, new SendMessageCallback() {

            private WrapperMessage wrapperMessage;

            @Override
            public void onAttached(Message message) {
                if (adapter != null) {
                    wrapperMessage = insertMessage(message, true, true);
                }
                if (callback != null) callback.onAttached(message);
            }

            @Override
            public void onSuccess(Message message) {
                if (wrapperMessage != null) {
                    wrapperMessage.setMessage(message);
                    adapter.update(wrapperMessage);
                }
                if (callback != null) callback.onSuccess(message);
            }

            @Override
            public void onError(Message message, int code, String reason) {
                if (wrapperMessage != null) {
                    wrapperMessage.setMessage(message);
                    adapter.update(wrapperMessage);
                }
                if (callback != null) callback.onError(message, code, reason);
            }
        });
    }

    @Override
    public void deleteMessage(WrapperMessage message) {
        int position = getMessages().indexOf(message);
        if (adapter != null && position > -1) {
            Message msg = message.getMessage();
            RongCoreClient.getInstance().deleteMessages(new int[]{msg.getMessageId()}, new IRongCoreCallback.ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    //?????????????????????
                    adapter.delete(position);
                }

                @Override
                public void onError(IRongCoreEnum.CoreErrorCode e) {

                }
            });
        }
    }

    @Override
    public void editMessage(WrapperMessage wrapperMessage, IResultBack<Boolean> callBack) {
        Message message = wrapperMessage.getMessage();
        UltraGroupApi.getApi().modifyUltraGroupMessage(message, new UltraApi.WrapperResultCallback(new IResultBack<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (adapter != null && result) {
                    adapter.update(wrapperMessage);
                }
                if (callBack != null) callBack.onResult(result);
            }
        }));
    }

    @Override
    public void sendMediaMessage(MessageContent content, SendMessageCallback callback) {
        UltraGroupApi.getApi().sendMediaMessage(_targetId, _channelId, content, new SendMessageCallback() {

            private WrapperMessage wrapperMessage;

            @Override
            public void onSuccess(Message message) {
                if (wrapperMessage != null) {
                    wrapperMessage.setMessage(message);
                    adapter.update(wrapperMessage);
                }
                if (callback != null) callback.onSuccess(message);
            }

            @Override
            public void onError(Message message, int code, String reason) {
                if (wrapperMessage != null) {
                    wrapperMessage.setMessage(message);
                    adapter.update(wrapperMessage);
                }
                if (callback != null) callback.onError(message, code, reason);
            }

            @Override
            public void onAttached(Message message) {
                if (adapter != null) {
                    wrapperMessage = insertMessage(message, true, true);
                }
                if (callback != null) callback.onAttached(message);
            }

            @Override
            public void onProgress(Message message, int progress) {
                wrapperMessage.setProgress(progress);
                adapter.update(wrapperMessage);
                if (callback != null) callback.onProgress(message, progress);
            }
        });
    }

    @Override
    public void sendImageMessage(MessageContent content, SendMessageCallback callback) {
        UltraGroupApi.getApi().sendImageMessage(_targetId, _channelId, content, new SendMessageCallback() {

            private WrapperMessage wrapperMessage;

            @Override
            public void onSuccess(Message message) {
                if (wrapperMessage != null) {
                    wrapperMessage.setMessage(message);
                    adapter.update(wrapperMessage);
                }
                if (callback != null) callback.onSuccess(message);
            }

            @Override
            public void onError(Message message, int code, String reason) {
                if (wrapperMessage != null) {
                    wrapperMessage.setMessage(message);
                    adapter.update(wrapperMessage);
                }
                if (callback != null) callback.onError(message, code, reason);
            }

            @Override
            public void onAttached(Message message) {
                if (adapter != null) {
                    wrapperMessage = insertMessage(message, true, true);
                }
                if (callback != null) callback.onAttached(message);
            }

            @Override
            public void onProgress(Message message, int progress) {
                wrapperMessage.setProgress(progress);
                adapter.update(wrapperMessage);
                if (callback != null) callback.onProgress(message, progress);
            }
        });
    }

    @Override
    public void getFirstUnReadMessage(IResultBack<Message> resultBack) {
        ChannelClient.getInstance().getTheFirstUnreadMessage(Conversation.ConversationType.ULTRA_GROUP, _targetId, _channelId, new IRongCoreCallback.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                if (resultBack != null) resultBack.onResult(message);
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                Logger.e("getFirstUnReadMessage", e);
            }
        });
    }

    @Override
    public void getConversion(IResultBack<Conversation> resultBack) {
        UltraGroupApi.getApi().getConversion(_targetId, _channelId, resultBack);
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
    public void getUltraGroupUnreadCount(IResultBack<Integer> resultBack) {
        UltraGroupApi.getApi().getAllChannelUnreadCount(_targetId, resultBack);
    }

    @Override
    public void getChannelUnreadCount(IResultBack<Integer> resultBack) {
        UltraGroupApi.getApi().getChannelUnreadCount(_targetId, _channelId, resultBack);
    }

    /**
     * ????????????
     *
     * @param iMessage
     */
    @Override
    public void recallMessage(WrapperMessage iMessage) {
        ChannelClient.getInstance().recallUltraGroupMessage(iMessage.getMessage(), new IRongCoreCallback.ResultCallback<RecallNotificationMessage>() {
            @Override
            public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
                //?????????
                Message iMessageMessage = iMessage.getMessage();
                //????????????????????????message
                Message message = Message.obtain(iMessageMessage.getTargetId(), iMessageMessage.getConversationType(), iMessageMessage.getChannelId(), recallNotificationMessage);
                message.setObjectName(ObjectName.RECALL_TAG);
                message.setSentTime(recallNotificationMessage.getRecallTime());
                message.setSenderUserId(iMessageMessage.getSenderUserId());
                WrapperMessage newMessage = WrapperMessage.fromMessage(message, getAttachedInfo(message));
                adapter.replace(iMessage, newMessage);
                //???????????????????????????????????????????????????
                for (WrapperMessage wrapperMessage : getMessages()) {
                    MessageContent content = wrapperMessage.getMessage().getContent();
                    if (content instanceof ReferenceMessage) {
                        String referMsgUid = ((ReferenceMessage) content).getReferMsgUid();
                        if (TextUtils.equals(referMsgUid, iMessageMessage.getUId())) {
                            adapter.update(wrapperMessage);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                Logger.e("recallMessage", e);
            }
        });
    }


    /**
     * ????????????????????????????????????????????????
     *
     * @param sentTime ??????????????????????????????????????????????????????
     * @param callback
     */
    @Override
    public void clearMessagesUnreadStatus(long sentTime, IRongCoreCallback.OperationCallback callback) {
        ChannelClient.getInstance().clearMessagesUnreadStatus(Conversation.ConversationType.ULTRA_GROUP, _targetId, _channelId, sentTime, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onSuccess();
                UltraUnReadMessageManager.getInstance().getiUltraGroupUnreadSyncStatusListener().onClearedUnreadStatus(_targetId, _channelId, sentTime);
                ChannelClient.getInstance().syncUltraGroupReadStatus(_targetId, _channelId, sentTime, null);
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                if (callback != null) callback.onError(coreErrorCode);
            }
        });
    }


    @Override
    public void clearAllMessageUnreadStatus(IRongCoreCallback.OperationCallback callback) {
        ChannelClient.getInstance().clearMessagesUnreadStatus(Conversation.ConversationType.ULTRA_GROUP, _targetId, _channelId, new IRongCoreCallback.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                    UltraUnReadMessageManager.getInstance().getiUltraGroupUnreadSyncStatusListener().onClearedUnreadStatus(_targetId, _channelId, System.currentTimeMillis());
                }
                if (callback != null) callback.onSuccess();
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                if (callback != null) callback.onError(coreErrorCode);
            }
        });
    }

    @Override
    public int containMessage(Message message) {
        for (int i = 0; i < getMessages().size(); i++) {
            WrapperMessage wrapperMessage = getMessages().get(i);
            if (wrapperMessage == null || wrapperMessage.getMessage() == null) continue;
            if (TextUtils.equals(wrapperMessage.getMessage().getUId(), message.getUId())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void getMessage(String uid, IResultBack<Message> resultBack) {
        UltraGroupApi.getApi().getMessage(uid, resultBack);
    }

    private final static String SYSTEM_TARGET = "_SYSTEM_";

    @Override
    public void clearSystemMessagesUnreadStatus() {
        IMCenter.getInstance().clearMessagesUnreadStatus(Conversation.ConversationType.SYSTEM, SYSTEM_TARGET, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });
    }

    @Override
    public void getSystemMessage(int lastMessageId, String objectName, IResultBack<List<Message>> resultBack) {
        RongCoreClient.getInstance().getHistoryMessages(
                Conversation.ConversationType.SYSTEM,
                SYSTEM_TARGET,
                objectName,
                lastMessageId,
                COUNT,
                RongCommonDefine.GetMessageDirection.FRONT,
                new IRongCoreCallback.ResultCallback<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        int count = null == messages ? 0 : messages.size();
                        Logger.e(TAG, "getSystemMessage: count = " + count);
                        if (null != resultBack) resultBack.onResult(messages);
                    }

                    @Override
                    public void onError(IRongCoreEnum.CoreErrorCode e) {
                        if (null != resultBack) resultBack.onResult(null);
                    }
                });
    }

    @Override
    public void getSystemMessage(long time, String[] objectNames, IResultBack<List<Message>> resultBack) {
        Logger.e(TAG, "objectNames = " + objectNames.length);
        RongCoreClient.getInstance().getHistoryMessages(
                Conversation.ConversationType.SYSTEM,
                SYSTEM_TARGET,
                Arrays.asList(objectNames),
                time,
                COUNT,
                RongCommonDefine.GetMessageDirection.FRONT,
                new IRongCoreCallback.ResultCallback<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        int count = null == messages ? 0 : messages.size();
                        Logger.e(TAG, "getSystemMessage: count = " + count);
                        if (null != resultBack) resultBack.onResult(messages);
                    }

                    @Override
                    public void onError(IRongCoreEnum.CoreErrorCode e) {
                        if (null != resultBack) resultBack.onResult(null);

                    }
                });
    }

    @Override
    public String messageToContent(MessageContent message) {
        String content = "";
        if (message instanceof TextMessage) {
            content = ((TextMessage) message).getContent();
        } else if (message instanceof ImageMessage) {
            content = "[??????]";
        } else if (message instanceof VoiceMessage) {
            content = "[??????]";
        } else if (message instanceof ReferenceMessage) {
            content = ((ReferenceMessage) message).getEditSendText();
        } else if (message instanceof SightMessage) {
            content = "[??????]";
        } else if (message instanceof RecallNotificationMessage) {
            content = "??????????????????";
        } else {
            content = GsonUtil.obj2Json(message);
        }
        return content;
    }

    /**
     * ?????????????????????
     * ?????????      ?????? hh:mm
     * ?????????      ?????? hh:mm
     * ???????????????   02???26??? 21???00
     */
    @Override
    public String messageToDate(Message message) {
        Date d = null;
        if (null != message) {
            long send = message.getSentTime();
            long received = message.getReceivedTime();
            // ?????????????????? received = 0  ???????????? receive > send
            long time = Math.max(send, received);
            d = new Date(time);
        }
        if (null == d) {
            return DateUtil.date2String(new Date(), TimeFt.HCm);
        }
        Calendar cal = DateUtil.date2Calendar(d);
        Calendar current = DateUtil.getCurrentCalendar();
        if (cal.get(Calendar.YEAR) == current.get(Calendar.YEAR)) {//??????
            if (cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)) {//??????
                return "?????? " + DateUtil.date2String(d, TimeFt.HCm);
            } else if (Math.abs(cal.get(Calendar.DAY_OF_YEAR) - current.get(Calendar.DAY_OF_YEAR)) == 1) {// ??????
                return "?????? " + DateUtil.date2String(d, TimeFt.HCm);
            }
        }
        return DateUtil.date2String(d, DateFt.MHdH, TimeFt.HCm);
    }


    @Override
    public void getMessages(long dateTime, int pageNum, HistoryMessageOption.PullOrder pullOrder, boolean isScrollBottom, IRongCoreCallback.IGetMessageCallbackEx iGetMessageCallbackEx) {
        HistoryMessageOption historyMessageOption = new HistoryMessageOption();
        historyMessageOption.setDataTime(dateTime);
        historyMessageOption.setCount(pageNum);
        historyMessageOption.setOrder(pullOrder);
        ChannelClient.getInstance().getMessages(Conversation.ConversationType.ULTRA_GROUP
                , _targetId, _channelId, historyMessageOption,
                new IRongCoreCallback.IGetMessageCallbackEx() {
                    @Override
                    public void onComplete(List<Message> messageList, long syncTimestamp, boolean hasMoreMsg, IRongCoreEnum.CoreErrorCode errorCode) {
                        for (Message message : messageList) {
                            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????,???????????????????????????????????????????????????
                            insertMessage(message, pullOrder == HistoryMessageOption.PullOrder.ASCEND ? true : false, isScrollBottom);
                        }
                        //?????????????????????
                        iGetMessageCallbackEx.onComplete(messageList, syncTimestamp, hasMoreMsg, errorCode);
                    }

                    @Override
                    public void onFail(IRongCoreEnum.CoreErrorCode errorCode) {
                        iGetMessageCallbackEx.onFail(errorCode);
                    }
                }
        );
    }

    @Override
    public void getLatestMessages(IRongCoreCallback.ResultCallback<List<Message>> callback) {

    }

    @Override
    public void sendUltraGroupTypingStatus(IRongCoreCallback.OperationCallback callback) {
        ChannelClient.getInstance().sendUltraGroupTypingStatus(_targetId, _channelId, IRongCoreEnum.UltraGroupTypingStatus.ULTRA_GROUP_TYPING_STATUS_TEXT, new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onSuccess();
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                if (callback != null) callback.onError(coreErrorCode);
            }
        });
    }

    /**
     * ???????????????????????????-???????????????????????????
     *
     * @param messages
     */
    @Override
    public void onUltraGroupMessageExpansionUpdated(List<Message> messages) {

    }

    /**
     * ???????????????????????????-????????????????????????
     *
     * @param messages
     */
    @Override
    public void onUltraGroupMessageModified(List<Message> messages) {
        if (adapter == null) return;
        List<WrapperMessage> messageList = getMessages();
        for (WrapperMessage wrapperMessage : messageList) {
            for (Message message : messages) {
                if (TextUtils.equals(message.getUId(), wrapperMessage.getMessage().getUId())) {
                    wrapperMessage.setMessage(message);
                    adapter.update(wrapperMessage);//????????????????????????
                }
            }
        }
    }


    /**
     * ???????????????????????????-????????????-??????UI
     * ????????????????????????????????????????????????????????????????????????????????????
     *
     * @param messages
     */
    @Override
    public void onUltraGroupMessageRecalled(List<Message> messages) {
        for (Message message : messages) {
            int newMessageId = message.getMessageId();
            for (WrapperMessage wrapperMessage : getMessages()) {
                Message oldMessage = wrapperMessage.getMessage();
                if (oldMessage.getContent() instanceof ReferenceMessage) {
                    //????????????????????????????????????????????????????????????????????????
                    String referMsgUid = ((ReferenceMessage) oldMessage.getContent()).getReferMsgUid();
                    if (TextUtils.equals(referMsgUid, message.getUId())) {
                        //?????????????????????????????????
                        adapter.update(wrapperMessage);
                    }
                }
                //??????????????????????????????????????????????????????
                int oldMessageId = oldMessage.getMessageId();
                if (newMessageId == oldMessageId) {
                    //??????????????????message
                    message.setObjectName(ObjectName.RECALL_TAG);
                    WrapperMessage newMessage = WrapperMessage.fromMessage(message, getAttachedInfo(message));
                    adapter.replace(wrapperMessage, newMessage);
                }
            }
        }
    }

    /**
     * ?????????????????????
     *
     * @param infoList
     */
    @Override
    public void onUltraGroupTypingStatusChanged(List<UltraGroupTypingStatusInfo> infoList) {
        if (onChannelListener != null) {
            ArrayList<UltraGroupTypingStatusInfo> infoArrayList = new ArrayList<>();
            for (UltraGroupTypingStatusInfo info : infoList) {
                if (TextUtils.equals(info.getTargetId(), _targetId) && TextUtils.equals(info.getChannelId(), _channelId)) {
                    //????????????????????????
                    infoArrayList.add(info);
                }
            }
            onChannelListener.onEditingStatusChanged(infoArrayList);
        }
    }

}
