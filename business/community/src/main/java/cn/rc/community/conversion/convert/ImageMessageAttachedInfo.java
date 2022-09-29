package cn.rc.community.conversion.convert;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.basis.adapter.RcyHolder;
import com.basis.ui.UIStack;
import com.basis.utils.DateUtil;
import com.basis.utils.ImageLoader;
import com.basis.wapper.IResultBack;
import com.google.android.material.imageview.ShapeableImageView;

import cn.rc.community.R;
import cn.rc.community.activity.CoolViewActivity;
import cn.rc.community.bean.UltraGroupUserBean;
import cn.rc.community.conversion.ObjectName;
import cn.rc.community.conversion.controller.BaseMessageAttachedInfo;
import cn.rc.community.conversion.controller.MessageManager;
import cn.rc.community.conversion.controller.WrapperMessage;
import cn.rc.community.conversion.sdk.SendMessageCallback;
import cn.rc.community.utils.UltraGroupUserManager;
import cn.rongcloud.config.provider.user.UserProvider;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;

public class ImageMessageAttachedInfo extends BaseMessageAttachedInfo {

    @Override
    public String onSetObjectName() {
        return ObjectName.Image_TAG;
    }

    @Override
    public int onSetLayout(WrapperMessage message) {
        return R.layout.item_chatroom_left_image;
    }

    @Override
    public void onConvert(RcyHolder holder, WrapperMessage item, int position) {
        Message message = item.getMessage();
        String senderUserId = message.getSenderUserId();
        Message.SentStatus sentStatus = message.getSentStatus();
        MessageContent msgContent = message.getContent();
        switch (sentStatus) {
            case FAILED://失败
                holder.setVisible(R.id.progress_id, false);
                holder.setVisible(R.id.rl_sendMessage_fail_id, true);
                break;
            case SENDING:
                //发送中更新进度条
                holder.setVisible(R.id.progress_id, true);
                ((ProgressBar) holder.getView(R.id.progress_id)).setProgress(item.getProgress());
                holder.setVisible(R.id.rl_sendMessage_fail_id, false);
                break;
            default:
                holder.setVisible(R.id.progress_id, false);
                holder.setVisible(R.id.rl_sendMessage_fail_id, false);
                break;
        }
        holder.setOnClickListener(R.id.tv_resend_id, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //重新发送，直接更新
                MessageManager.get().sendImageMessage(msgContent, new SendMessageCallback() {
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
        CircleImageView circleImageView = holder.getView(R.id.cv_id);
        UltraGroupUserManager.getInstance().getAsyn(senderUserId, new IResultBack<UltraGroupUserBean>() {
            @Override
            public void onResult(UltraGroupUserBean ultraGroupUserBean) {
                UserInfo userInfo = message.getContent().getUserInfo();
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
        ImageMessage imageMessage = (ImageMessage) msgContent;
        Uri remoteUri = imageMessage.getRemoteUri();
        ShapeableImageView shapeableImageView = holder.getView(R.id.shape_iv_content_id);
        if (remoteUri != null) {
            shapeableImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        ImageLoader.loadUri(holder.getView(R.id.shape_iv_content_id), remoteUri, R.drawable.svg_send_image_message_fail);
        holder.setOnClickListener(R.id.shape_iv_content_id, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity topActivity = UIStack.getInstance().getTopActivity();
                Intent intent = new Intent(topActivity, CoolViewActivity.class);
                intent.putExtra("message", message);
                topActivity.startActivity(intent);
            }
        });
        //长按图片，直接调用最外层的长按方法 
        holder.setOnLongClickListener(R.id.shape_iv_content_id, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (item.getLongClickListener() != null) {
                    item.getLongClickListener().onItemLongClick(item, position);
                }
                return false;
            }
        });
    }
}