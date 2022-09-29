package cn.rc.community.channel.details;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.interfaces.IAdapte;
import com.basis.net.oklib.OkApi;
import com.basis.net.oklib.WrapperCallBack;
import com.basis.net.oklib.wrapper.Wrapper;
import com.basis.net.oklib.wrapper.interfaces.IPage;
import com.basis.utils.DateUtil;
import com.basis.utils.GsonUtil;
import com.basis.utils.ImageLoader;
import com.basis.utils.KToast;
import com.basis.utils.Logger;
import com.basis.utils.ResUtil;
import com.basis.utils.UIKit;
import com.basis.wapper.IResultBack;
import com.basis.widget.loading.LoadTag;
import com.google.android.material.imageview.ShapeableImageView;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.community.CommunityAPI;
import cn.rc.community.Constants;
import cn.rc.community.MediaPlayUtils;
import cn.rc.community.OnConvertListener;
import cn.rc.community.R;
import cn.rc.community.UltraKvKey;
import cn.rc.community.bean.CommunityDetailsBean;
import cn.rc.community.bean.MarkMessage;
import cn.rc.community.bean.UltraGroupUserBean;
import cn.rc.community.conversion.ObjectName;
import cn.rc.community.conversion.controller.MessageManager;
import cn.rc.community.helper.CommunityHelper;
import cn.rc.community.message.AbsMessageFragment;
import cn.rc.community.message.MarkMessageAdapter;
import cn.rc.community.setting.CustomerBottomDialog;
import cn.rc.community.utils.UltraGroupUserManager;
import cn.rongcloud.config.provider.user.UserProvider;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ImageMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.ReferenceMessage;
import io.rong.message.SightMessage;
import io.rong.message.TextMessage;

public class MarkMsgFragment extends AbsMessageFragment<MarkMessage> implements OnConvertListener<MarkMessage> {
    private final static List<String> ITEMS = CommunityHelper.getInstance().isCreator() ? Arrays.asList(
            ResUtil.getString(R.string.cmu_message_jump),
            ResUtil.getString(R.string.cmu_message_remove)) : Arrays.asList(
            ResUtil.getString(R.string.cmu_message_jump));
    private CustomerBottomDialog customerBottomDialog;

    public static MarkMsgFragment newInstance(String channelId) {
        MarkMsgFragment fragment = new MarkMsgFragment();
        Bundle args = new Bundle();
        args.putString(UIKit.KEY_BASE, channelId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public IAdapte<MarkMessage, RcyHolder> onSetAdapter() {
        return new MarkMessageAdapter(activity, this, R.layout.item_chatroom_left
                , R.layout.item_chatroom_left_image, R.layout.item_chatroom_left_sight, R.layout.item_chatroom_left_recall);
    }

    private int pageCount;
    private String channelId;

    @Override
    public void init() {
        Bundle args = getArguments();
        if (null != args) {
            channelId = args.getString(UIKit.KEY_BASE);
        }
        super.init();
    }

    @Override
    public void onRefreshData(boolean wait, boolean refresh, MessageResultBack<MarkMessage> resultBack) {
        pageCount++;
        if (refresh) pageCount = 0;
        Map<String, Object> params = new HashMap<>(4);
        params.put("channelUid", channelId);
        params.put("pageNum", pageCount);
        params.put("pageSize", 10);
        LoadTag finalTag = wait ? new LoadTag(activity, ResUtil.getString(R.string.basis_loading)) : null;
        OkApi.post(CommunityAPI.CHANNEL_MARK_MSG, params, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (null != finalTag) finalTag.dismiss();
                List<MarkMessage> messages = result.getList("records", MarkMessage.class);
                //加载更多数据源
                for (MarkMessage message : messages) {
                    onLoadMessage(message, resultBack);
                }
                IPage page = result.getPage();
                Logger.e(TAG, "page = " + GsonUtil.obj2Json(page));
                int size = messages.size();
                if (null == refreshLayout) return;
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                if (size % 10 > 0 || size == 0) {
                    //肯定没有更多了
                    refreshLayout.setEnableLoadMore(false);
                } else {
                    refreshLayout.setEnableLoadMore(true);
                }
                emptyView.setVisibility(getAdapter().getData().size() > 0 ? View.GONE : View.VISIBLE);
            }
        });
    }

    /**
     * 添加到最底部
     *
     * @param mark
     * @param resultBack
     */
    public void onLoadMessage(MarkMessage mark, MessageResultBack<MarkMessage> resultBack) {
        if (null == mark || null != mark.getMessage()) {
            return;
        }
        if (resultBack != null) resultBack.addData(mark, true);
        MessageManager.get().getMessage(mark.getMessageUid(), new IResultBack<Message>() {
            @Override
            public void onResult(Message message) {
                //不显示撤销消息和远端已经删除的消息
                if (null != message && !(message.getContent() instanceof RecallNotificationMessage)) {
                    mark.setMessage(message);
                    getAdapter().updateItem(mark);
                } else {
                    getAdapter().removeItem(mark);
                }
            }
        });
    }

    @Override
    public void onConvert(RcyHolder holder, MarkMessage item, int position) {
        Message message = item.getMessage();
        if (message == null) return;
        MessageContent messageContent = message.getContent();
        if (messageContent == null) return;
        if (messageContent instanceof TextMessage) {
            textMessageConvert(holder, item, position);
        } else if (messageContent instanceof ReferenceMessage) {
            referenceMessageConvert(holder, item, position);
        } else if (messageContent instanceof ImageMessage) {
            imageMessageConvert(holder, item, position);
        } else if (messageContent instanceof SightMessage) {
            sightMessageConvert(holder, item, position);
        } else if (messageContent instanceof RecallNotificationMessage) {
            recallMessageConvert(holder, item, position);
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (customerBottomDialog == null) {
                    customerBottomDialog = new CustomerBottomDialog(activity, ITEMS)
                            .setOnItemClickListener(new CustomerBottomDialog.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    if (0 == position) {
                                        jumpTo(item);
                                    } else if (1 == position) {
                                        remove(item);
                                    }
                                }
                            });
                }
                CommunityDetailsBean.CommunityUserBean u = CommunityHelper.getInstance().getCommunityUserBean();
                int auditStatus = null != u ? u.getAuditStatus() : Constants.AuditStatus.NOT_JOIN.getCode();
                if (auditStatus != Constants.AuditStatus.NOT_JOIN.getCode()) {
                    customerBottomDialog.show();
                }
                return false;
            }
        });
    }

    private void recallMessageConvert(RcyHolder holder, MarkMessage item, int position) {
        Message message = item.getMessage();
        String senderUserId = message.getSenderUserId();
        CircleImageView circleImageView = holder.getView(R.id.cv_id);
        UltraGroupUserManager.getInstance().getAsyn(senderUserId, new IResultBack<UltraGroupUserBean>() {
            @Override
            public void onResult(UltraGroupUserBean ultraGroupUserBean) {
                holder.setText(R.id.tv_name_id, ultraGroupUserBean.getNickName());
                ImageLoader.loadUrl(circleImageView, ultraGroupUserBean.getPortrait(), R.drawable.rc_default_portrait);
            }
        });
        holder.setText(R.id.tv_time_id, DateUtil.getRecordDate(((RecallNotificationMessage) message.getContent()).getRecallTime()));
    }

    private void sightMessageConvert(RcyHolder holder, MarkMessage item, int position) {
        Message message = item.getMessage();
        String senderUserId = message.getSenderUserId();
        MessageContent msgContent = message.getContent();
        CircleImageView circleImageView = holder.getView(R.id.cv_id);
        UltraGroupUserManager.getInstance().getAsyn(senderUserId, new IResultBack<UltraGroupUserBean>() {
            @Override
            public void onResult(UltraGroupUserBean ultraGroupUserBean) {
                holder.setText(R.id.tv_name_id, ultraGroupUserBean.getNickName());
                ImageLoader.loadUrl(circleImageView, ultraGroupUserBean.getPortrait(), R.drawable.rc_default_portrait);
            }
        });
        holder.setText(R.id.tv_time_id, DateUtil.getRecordDate(message.getSentTime()));
        SightMessage sightMessage = (SightMessage) msgContent;
        Uri remoteUri = sightMessage.getThumbUri();
        ShapeableImageView shapeableImageView = holder.getView(R.id.shape_iv_content_id);
        if (remoteUri != null) {
            shapeableImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        ImageLoader.loadUri(holder.getView(R.id.shape_iv_content_id), remoteUri, R.drawable.svg_send_image_message_fail);

        holder.setOnClickListener(R.id.iv_play_id, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayUtils.playSightMessage(message);
            }
        });
    }

    /**
     * 图片
     *
     * @param holder
     * @param item
     * @param position
     */
    private void imageMessageConvert(RcyHolder holder, MarkMessage item, int position) {
        Message message = item.getMessage();
        String senderUserId = message.getSenderUserId();
        MessageContent msgContent = message.getContent();
        CircleImageView circleImageView = holder.getView(R.id.cv_id);
        UltraGroupUserManager.getInstance().getAsyn(senderUserId, new IResultBack<UltraGroupUserBean>() {
            @Override
            public void onResult(UltraGroupUserBean ultraGroupUserBean) {
                holder.setText(R.id.tv_name_id, ultraGroupUserBean.getNickName());
                ImageLoader.loadUrl(circleImageView, ultraGroupUserBean.getPortrait(), R.drawable.rc_default_portrait);
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
                MediaPlayUtils.showImage(message);
            }
        });
    }


    /**
     * 转换为引用消息
     *
     * @param holder
     * @param item
     * @param position
     */
    private void referenceMessageConvert(RcyHolder holder, MarkMessage item, int position) {
        Message message = item.getMessage();
        String senderUserId = message.getSenderUserId();
        CircleImageView circleImageView = holder.getView(R.id.cv_id);
        ReferenceMessage referenceMessage = ((ReferenceMessage) message.getContent());
        UltraGroupUserManager.getInstance().getAsyn(senderUserId, new IResultBack<UltraGroupUserBean>() {
            @Override
            public void onResult(UltraGroupUserBean ultraGroupUserBean) {
                holder.setText(R.id.tv_name_id, ultraGroupUserBean.getNickName());
                ImageLoader.loadUrl(circleImageView, ultraGroupUserBean.getPortrait(), R.drawable.rc_default_portrait);
            }
        });
        holder.setText(R.id.tv_time_id, DateUtil.getRecordDate(message.getSentTime()));
        String content = referenceMessage.getEditSendText();
        String edit = ResUtil.getString(R.string.cmu_str_has_edit);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(content);
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
        });
    }

    /**
     * 转换为文字消息
     *
     * @param holder
     * @param item
     * @param position
     */
    private void textMessageConvert(RcyHolder holder, MarkMessage item, int position) {
        Message message = item.getMessage();
        MessageContent msgContent = message.getContent();
        String senderUserId = message.getSenderUserId();
        CircleImageView circleImageView = holder.getView(R.id.cv_id);
        UltraGroupUserManager.getInstance().getAsyn(senderUserId, new IResultBack<UltraGroupUserBean>() {
            @Override
            public void onResult(UltraGroupUserBean ultraGroupUserBean) {
                holder.setText(R.id.tv_name_id, ultraGroupUserBean.getNickName());
                ImageLoader.loadUrl(circleImageView, ultraGroupUserBean.getPortrait(), R.drawable.rc_default_portrait);
            }
        });
        holder.setText(R.id.tv_time_id, DateUtil.getRecordDate(message.getSentTime()));
        String content = (msgContent instanceof TextMessage) ? ((TextMessage) msgContent).getContent() : msgContent.toString();
        String edit = ResUtil.getString(R.string.cmu_str_has_edit);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(content);
        if (message.isHasChanged()) {
            spannableStringBuilder.append(edit);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#8F8C8C")), content.length(), spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置后面的字体颜色
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(13, true), content.length(), spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//设置后面的字体大小
        }
        holder.setText(R.id.tv_content_id, spannableStringBuilder);
    }


    private void remove(MarkMessage item) {
        OkApi.post(CommunityAPI.REMOVE_MARK_MSG + item.getMessageUid(), null, new WrapperCallBack() {
            @Override
            public void onResult(Wrapper result) {
                if (result.ok()) {
                    removeItem(item);
                    KToast.show("标记移除成功");
                } else {
                    KToast.show("标记移除失败:" + result.getMessage());
                }
            }
        });
    }

    /**
     * 跳转到定位的位置
     *
     * @param item
     */
    private void jumpTo(MarkMessage item) {
        Intent intent = new Intent();
        intent.putExtra(UltraKvKey.MarkMessageKey, item.getMessageUid());
        getActivity().setResult(Constants.markMessageResultCode, intent);
        getActivity().finish();
    }

}