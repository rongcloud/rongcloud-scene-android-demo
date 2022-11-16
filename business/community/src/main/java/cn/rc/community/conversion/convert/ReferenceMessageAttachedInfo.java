package cn.rc.community.conversion.convert;

import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import com.basis.adapter.RcyHolder;
import com.basis.utils.DateUtil;
import com.basis.utils.GsonUtil;
import com.basis.utils.ImageLoader;
import com.basis.utils.ResUtil;
import com.basis.wapper.IResultBack;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.rc.community.MediaPlayUtils;
import cn.rc.community.R;
import cn.rc.community.bean.UltraGroupUserBean;
import cn.rc.community.conversion.ObjectName;
import cn.rc.community.conversion.controller.BaseMessageAttachedInfo;
import cn.rc.community.conversion.controller.MessageManager;
import cn.rc.community.conversion.controller.WrapperMessage;
import cn.rc.community.conversion.sdk.SendMessageCallback;
import cn.rc.community.utils.UltraGroupUserManager;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.ReferenceMessage;
import io.rong.message.SightMessage;
import io.rong.message.TextMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/14
 * @time 6:09 下午
 * 引用消息
 */
public class ReferenceMessageAttachedInfo extends BaseMessageAttachedInfo {

    private String content;

    @Override
    public void onConvert(RcyHolder holder, WrapperMessage item, int position) {
        Message message = item.getMessage();
        String senderUserId = message.getSenderUserId();
        CircleImageView circleImageView = holder.getView(R.id.cv_id);
        Message.SentStatus sentStatus = message.getSentStatus();
        ReferenceMessage referenceMessage = ((ReferenceMessage) message.getContent());
        switch (sentStatus) {
            case FAILED:
                holder.setVisible(R.id.rl_sendMessage_fail_id, true);
                break;
            default:
                holder.setVisible(R.id.rl_sendMessage_fail_id, false);
                break;
        }
        holder.setOnClickListener(R.id.tv_resend_id, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //重新发送，直接更新
                MessageManager.get().sendMessage(referenceMessage, null, new SendMessageCallback() {
                    @Override
                    public void onSuccess(Message message) {

                    }

                    @Override
                    public void onError(Message message, int code, String reason) {

                    }

                    @Override
                    public void onAttached(Message message) {
                        //移除掉当前的，重新发送一条同样的新消息
                        MessageManager.get().deleteMessage(item);
                        SendMessageCallback.super.onAttached(message);
                    }
                });
            }
        });
        UltraGroupUserManager.getInstance().getAsyn(senderUserId, new IResultBack<UltraGroupUserBean>() {
            @Override
            public void onResult(UltraGroupUserBean ultraGroupUserBean) {
                UserInfo userInfo = referenceMessage.getUserInfo();
                if (userInfo != null) {
                    //将用户带的信息更新到缓存中
                    if (TextUtils.isEmpty(ultraGroupUserBean.getNickName()))
                        ultraGroupUserBean.setNickName(userInfo.getName());
                    if (TextUtils.isEmpty(ultraGroupUserBean.getPortrait()))
                        ultraGroupUserBean.setPortrait(userInfo.getPortraitUri().toString());
                }
                holder.setText(R.id.tv_name_id, ultraGroupUserBean.getNickName());
                ImageLoader.loadUri(circleImageView, Uri.parse(ultraGroupUserBean.getPortrait()), R.drawable.rc_default_portrait);
            }
        });
        holder.setText(R.id.tv_time_id, DateUtil.getRecordDate(message.getSentTime()));
        content = referenceMessage.getEditSendText();
        if (content == null) content = "";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(content);
        Map<String, String> expansion = message.getExpansion();
        if (expansion != null) {
            String mentionedContent = expansion.get("mentionedContent");
            if (!TextUtils.isEmpty(mentionedContent)) {
                HashMap<String, String> mentionMap = GsonUtil.json2Map(mentionedContent, new TypeToken<HashMap<String, Object>>() {
                }.getType());
                for (String userId : mentionMap.keySet()) {
                    String name = mentionMap.get(userId);
                    int i = content.indexOf("@" + name);
                    if (i > -1) {
                        //说明存在，那么这一部分应该变色
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#0099FF")),
                                i,
                                i + name.length() + 1,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置后面的字体颜色
                    }
                }
            }
        }
        String edit = ResUtil.getString(R.string.cmu_str_has_edit);
        if (message.isHasChanged()) {
            spannableStringBuilder.append(edit);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#8F8C8C")), content.length(), spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置后面的字体颜色
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(13, true), content.length(), spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//设置后面的字体大小
        }
        holder.setText(R.id.tv_content_id, spannableStringBuilder);
        holder.setVisible(R.id.iv_quote_icon, true);
        holder.setVisible(R.id.tv_quote_content, true);
        StringBuffer stringBuffer = new StringBuffer();
        //被引用人的名字
        UltraGroupUserManager.getInstance().getAsyn(referenceMessage.getUserId(), new IResultBack<UltraGroupUserBean>() {
            @Override
            public void onResult(UltraGroupUserBean ultraGroupUserBean) {
                if (null != ultraGroupUserBean) {
                    stringBuffer.append(ultraGroupUserBean.getNickName() + " : ");
                    String referMsgUid = referenceMessage.getReferMsgUid();
                    MessageManager.get().getMessage(referMsgUid, new IResultBack<Message>() {
                        @Override
                        public void onResult(Message message) {
                            if (message != null && message.getContent() instanceof RecallNotificationMessage) {
                                holder.setVisible(R.id.iv_play_id, false);
                                holder.setVisible(R.id.iv_quote_content, false);
                                stringBuffer.append(MessageManager.get().messageToContent(message.getContent()));
                            } else {
                                //根据被引用消息的类型
                                Class<? extends ReferenceMessage> referenceMessageClass = referenceMessage.getClass();
                                try {
                                    Method declaredMethod = referenceMessageClass.getDeclaredMethod("getObjName");
                                    declaredMethod.setAccessible(true);
                                    String objectName = (String) declaredMethod.invoke(referenceMessage);

                                    //判断引用的消息是否已经被撤回了
                                    Log.e("TAG", "onConvert: ");
                                    switch (objectName) {
                                        case ObjectName.REFERENCE_TAG:
                                            //被引用的消息
                                            ReferenceMessage referenceContent = (ReferenceMessage) referenceMessage.getReferenceContent();
                                            holder.setVisible(R.id.iv_quote_content, false);
                                            stringBuffer.append(referenceContent.getEditSendText());
                                            break;
                                        case ObjectName.TEXT_TAG:
                                            //被引用的是文字的话
                                            MessageContent textMessage = referenceMessage.getReferenceContent();
                                            holder.setVisible(R.id.iv_quote_content, false);
                                            stringBuffer.append(((TextMessage) textMessage).getContent());
                                            break;
                                        case ObjectName.Image_TAG:
                                            //被引用的是图片的话
                                            ImageMessage imageMessage = (ImageMessage) referenceMessage.getReferenceContent();
                                            holder.setVisible(R.id.iv_quote_content, true);
                                            Uri remoteUri = imageMessage.getRemoteUri();
                                            if (!TextUtils.isEmpty(remoteUri.getPath())) {
                                                ImageLoader.loadUri(holder.getView(R.id.iv_quote_content), remoteUri, R.drawable.rc_picture_icon_data_error);
                                            }
                                            holder.setOnClickListener(R.id.iv_quote_content, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    MediaPlayUtils.showImage(message);
                                                }
                                            });
                                            break;
                                        case ObjectName.Sight_TAG:
                                            //被引用的是图片的话
                                            SightMessage sightMessage = (SightMessage) referenceMessage.getReferenceContent();
                                            holder.setVisible(R.id.iv_quote_content, true);
                                            holder.setVisible(R.id.iv_play_id, true);
                                            Uri thumbUri = sightMessage.getThumbUri();
                                            if (!TextUtils.isEmpty(thumbUri.getPath())) {
                                                ImageLoader.loadUri(holder.getView(R.id.iv_quote_content), thumbUri, R.drawable.rc_picture_icon_data_error);
                                            }
                                            holder.setOnClickListener(R.id.iv_play_id, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //播放视频
                                                    MediaPlayUtils.playSightMessage(message);
                                                }
                                            });
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            holder.setText(R.id.tv_quote_content, stringBuffer);
                        }
                    });
                }
            }
        });
    }

    @Override
    public int onSetLayout(WrapperMessage message) {
        return R.layout.item_chatroom_left;
    }

    @Override
    public String onSetObjectName() {
        return ObjectName.REFERENCE_TAG;
    }
}
