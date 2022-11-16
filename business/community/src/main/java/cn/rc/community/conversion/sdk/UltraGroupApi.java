package cn.rc.community.conversion.sdk;

import android.text.TextUtils;
import android.widget.EditText;

import com.basis.utils.GsonUtil;
import com.basis.wapper.IResultBack;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.community.conversion.controller.MessageManager;
import cn.rc.community.helper.CommunityHelper;
import cn.rongcloud.config.UserManager;
import io.rong.imkit.feature.mention.MentionBlock;
import io.rong.imkit.feature.mention.RongMentionManager;
import io.rong.imlib.ChannelClient;
import io.rong.imlib.IRongCoreCallback;
import io.rong.imlib.IRongCoreEnum;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.MessagePushConfig;
import io.rong.imlib.model.UserInfo;

/**
 * 超级群api
 */
public class UltraGroupApi implements UltraApi {

    private final static UltraApi _api = new UltraGroupApi();

    public static UltraApi getApi() {
        return _api;
    }

    /**
     * 获取会话信息
     *
     * @param targetId
     * @param channelId
     * @param resultBack
     */
    @Override
    public void getConversion(String targetId, String channelId, IResultBack<Conversation> resultBack) {
        ChannelClient.getInstance().getConversation(ULTRA_GROUP,
                targetId,
                channelId,
                new WrapperResultCallback<>(resultBack));
    }


    /**
     * 获取社区下所有频道的未读数
     *
     * @param targetId   会话id
     * @param resultBack callback
     */
    @Override
    public void getAllChannelUnreadCount(String targetId, IResultBack<Integer> resultBack) {
        ChannelClient.getInstance().getUltraGroupUnreadMentionedCount(targetId, new WrapperResultCallback<>(resultBack));

    }

    /**
     * 获取单个频道的未读数
     *
     * @param targetId
     * @param channelId
     * @param resultBack
     */
    @Override
    public void getChannelUnreadCount(String targetId, String channelId, IResultBack<Integer> resultBack) {
        ChannelClient.getInstance().getUnreadCount(ULTRA_GROUP, targetId, channelId, new IRongCoreCallback.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer unreadMessageCount) {
                if (resultBack != null) resultBack.onResult(unreadMessageCount);
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode e) {
                if (resultBack != null) resultBack.onResult(0);
            }
        });
    }

    @Override
    public void getLastMessages(String targetId, String channelId, IResultBack<List<Message>> resultBack) {
        ChannelClient.getInstance().getLatestMessages(ULTRA_GROUP,
                targetId,
                channelId,
                MSG_COUNT,
                new WrapperResultCallback<>(resultBack));
    }

    @Override
    public void getMessage(String uid, IResultBack<Message> resultBack) {
        ChannelClient.getInstance().getMessageByUid(uid,
                new WrapperResultCallback<>(resultBack));
    }

    @Override
    public void getHistoryMessages(Message lastMessage, String filterMsgTag, IResultBack<List<Message>> result) {
        ChannelClient.getInstance().getHistoryMessages(
                ULTRA_GROUP,
                lastMessage.getTargetId(),
                lastMessage.getMessageId(),// last msg id
                MSG_COUNT,
                lastMessage.getChannelId(),
                new WrapperResultCallback<>(result));
    }

    @Override
    public void sendMessage(String targetId, String channelId, EditText editText, MessageContent content, SendMessageCallback callback) {
        Message message = createMessage(targetId, channelId, content);
        Map<String, String> expansion = message.getExpansion();
        String mentionBlockInfo = RongMentionManager.getInstance().getMentionBlockInfo();
        if (!TextUtils.isEmpty(mentionBlockInfo)) {
            List<MentionBlock> mentionBlocks = GsonUtil.json2List(mentionBlockInfo, MentionBlock.class);
            JsonObject jsonObject = new JsonObject();
            for (MentionBlock mentionBlock : mentionBlocks) {
                jsonObject.addProperty(mentionBlock.userId, mentionBlock.name);
            }
            expansion.put("mentionedContent", jsonObject.toString());
        }
        //添加当前的输入框中的艾特信息
        if (editText != null) {
            RongMentionManager.getInstance().onSendToggleClick(message, editText);
        }

        RongCoreClient.getInstance().sendMessage(message, "", "", new IRongCoreCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
                if (callback != null) callback.onAttached(message);
            }

            @Override
            public void onSuccess(Message message) {
                if (null != callback) callback.onSuccess(message);
            }

            @Override
            public void onError(Message message, IRongCoreEnum.CoreErrorCode coreErrorCode) {
                if (null != callback)
                    callback.onError(message, coreErrorCode.code, coreErrorCode.msg);
            }
        });
    }

    @Override
    public void sendMediaMessage(String targetId, String channelId, MessageContent content, SendMessageCallback callback) {
        Message message = createMessage(targetId, channelId, content);
        RongCoreClient.getInstance().sendMediaMessage(message, "", "", new IRongCoreCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message message, int progress) {
                if (null != callback) callback.onProgress(message, progress);
            }

            @Override
            public void onCanceled(Message message) {

            }

            @Override
            public void onAttached(Message message) {
                if (callback != null) callback.onAttached(message);
            }

            @Override
            public void onSuccess(Message message) {
                if (null != callback) callback.onSuccess(message);
            }

            @Override
            public void onError(Message message, IRongCoreEnum.CoreErrorCode code) {
                if (null != callback) callback.onError(message, code.code, code.msg);
            }
        });
    }

    @Override
    public void sendImageMessage(String targetId, String channelId, MessageContent content, SendMessageCallback callback) {
        Message message = createMessage(targetId, channelId, content);
        RongCoreClient.getInstance().sendImageMessage(message, "", "", new IRongCoreCallback.SendImageMessageCallback() {
            @Override
            public void onAttached(Message message) {
                if (callback != null) callback.onAttached(message);
            }

            @Override
            public void onError(Message message, IRongCoreEnum.CoreErrorCode code) {
                if (null != callback) callback.onError(message, code.code, code.msg);
            }

            @Override
            public void onSuccess(Message message) {
                if (null != callback) callback.onSuccess(message);
            }

            @Override
            public void onProgress(Message message, int progress) {
                if (null != callback) callback.onProgress(message, progress);
            }
        });
    }

    /**
     * 构建发送的消息对象
     *
     * @param targetId
     * @param channelId
     * @param messageContent
     * @return
     */
    private Message createMessage(String targetId, String channelId, MessageContent messageContent) {
        UserInfo userInfo = new UserInfo(UserManager.get().getUserId(), UserManager.get().getUserName(), UserManager.get().getPortraitUri());
        messageContent.setUserInfo(userInfo);
        Message message = Message.obtain(targetId, ULTRA_GROUP, channelId, messageContent);
        message.setCanIncludeExpansion(true);
        HashMap<String, String> expansion = new HashMap<>();
        message.setExpansion(expansion);
        String title = CommunityHelper.getInstance().getCommunityDetailsBean().getName() + "#"
                + CommunityHelper.channelDetailsLiveData.getValue().getName();
        String content = CommunityHelper.getInstance().getNickName() + ": " + MessageManager.get().messageToContent(messageContent);
        MessagePushConfig messagePushConfig = new MessagePushConfig.Builder().setPushTitle(title)
                .setPushContent(content).build();
        message.setMessagePushConfig(messagePushConfig);
        return message;
    }

    /**
     * 编辑已经发送的消息
     *
     * @param message
     * @param wrapperResultCallback
     */
    @Override
    public void modifyUltraGroupMessage(Message message, WrapperResultCallback wrapperResultCallback) {
        message.setHasChanged(true);
        ChannelClient.getInstance().modifyUltraGroupMessage(message.getUId(), message.getContent(), new IRongCoreCallback.OperationCallback() {
            @Override
            public void onSuccess() {
                if (wrapperResultCallback != null) wrapperResultCallback.onSuccess(true);
            }

            @Override
            public void onError(IRongCoreEnum.CoreErrorCode coreErrorCode) {
                if (wrapperResultCallback != null) wrapperResultCallback.onError(coreErrorCode);
            }
        });
    }
}
