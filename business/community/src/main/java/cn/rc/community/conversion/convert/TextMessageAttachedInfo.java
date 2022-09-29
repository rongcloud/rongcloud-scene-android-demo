package cn.rc.community.conversion.convert;

import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.basis.adapter.RcyHolder;
import com.basis.utils.DateUtil;
import com.basis.utils.GsonUtil;
import com.basis.utils.ImageLoader;
import com.basis.utils.ResUtil;
import com.basis.wapper.IResultBack;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.community.R;
import cn.rc.community.bean.UltraGroupUserBean;
import cn.rc.community.conversion.ObjectName;
import cn.rc.community.conversion.controller.BaseMessageAttachedInfo;
import cn.rc.community.conversion.controller.MessageManager;
import cn.rc.community.conversion.controller.WrapperMessage;
import cn.rc.community.conversion.sdk.SendMessageCallback;
import cn.rc.community.utils.UltraGroupUserManager;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

public class TextMessageAttachedInfo extends BaseMessageAttachedInfo {

    @Override
    public String onSetObjectName() {
        return ObjectName.TEXT_TAG;
    }

    @Override
    public int onSetLayout(WrapperMessage message) {
        return R.layout.item_chatroom_left;
    }

    @Override
    public void onConvert(RcyHolder holder, WrapperMessage item, int position) {
        CircleImageView circleImageView = holder.getView(R.id.cv_id);
        Message message = item.getMessage();
        TextMessage msgContent = (TextMessage) message.getContent();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String content = (msgContent instanceof TextMessage) ? ((TextMessage) msgContent).getContent() : msgContent.toString();
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
        String senderUserId = message.getSenderUserId();
        Message.SentStatus sentStatus = message.getSentStatus();
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
                MessageManager.get().sendMessage(msgContent, null, new SendMessageCallback() {
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
                if (null != ultraGroupUserBean) {
                    UserInfo userInfo = msgContent.getUserInfo();
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
            }
        });

        holder.setText(R.id.tv_time_id, DateUtil.getRecordDate(message.getSentTime()));
        String edit = ResUtil.getString(R.string.cmu_str_has_edit);
        if (message.isHasChanged()) {
            spannableStringBuilder.append(edit);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#8F8C8C")), content.length(), spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置后面的字体颜色
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(13, true), content.length(), spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//设置后面的字体大小
        }
        holder.setText(R.id.tv_content_id, spannableStringBuilder);
    }
}