package cn.rongcloud.roomkit.ui.room;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyAdapter;
import com.basis.adapter.RcyHolder;
import com.basis.utils.Logger;
import com.basis.utils.UiUtils;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.manager.IAudioPlayListener;
import cn.rongcloud.roomkit.manager.RCAudioPlayManager;
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
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.room.model.MemberCache;
import cn.rongcloud.roomkit.widget.CenterAlignImageSpan;
import io.rong.imkit.manager.AudioPlayManager;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * @author gyn
 * @date 2021/9/23
 */
public class RoomMessageAdapter extends RcyAdapter<MessageContent, RcyHolder> {
    OnClickMessageUserListener mOnClickMessageUserListener;
    private String mRoomCreateId = "";
    private int iconSize = 0;
    private RoomType roomType;

    public RoomMessageAdapter(Context context, OnClickMessageUserListener onClickMessageUserListener, RoomType roomType) {
        this(context, R.layout.item_message_system, R.layout.item_message_normal, R.layout.item_message_voice);
        this.mOnClickMessageUserListener = onClickMessageUserListener;
        iconSize = UiUtils.dp2px(11);
        this.roomType = roomType;
    }

    public RoomMessageAdapter(Context context, int... itemLayoutId) {
        super(context, itemLayoutId);
    }

    public void setRoomCreateId(String roomCreateId) {
        this.mRoomCreateId = roomCreateId;
    }

    /**
     * 向recyclerview添加数据 ，如果在底部或自己发送的消息自动滚动，否则不滚动
     *
     * @param list
     * @param refresh
     * @param recyclerView
     */
    public synchronized void setData(List<MessageContent> list, boolean refresh, RecyclerView recyclerView) {
        // 当前是否在列表最下面
        boolean inBottom = !recyclerView.canScrollVertically(1);
        // 设置数据
        super.setData(list, refresh);
        // 是否是自己主动发的消息
        boolean isMyselfMessage = false;
        if (list != null && list.size() > 0) {
            MessageContent messageContent = list.get(list.size() - 1);
            if (messageContent instanceof RCChatroomVoice) {
                isMyselfMessage = TextUtils.equals(((RCChatroomVoice) messageContent).getUserId(), UserManager.get().getUserId());
            }
            if (messageContent instanceof RCChatroomBarrage) {
                isMyselfMessage = TextUtils.equals(((RCChatroomBarrage) messageContent).getUserId(), UserManager.get().getUserId());
            }
        }
        if (refresh || inBottom || isMyselfMessage) {
            int count = getItemCount();
            if (count > 0) {
                recyclerView.smoothScrollToPosition(count - 1);
            }
        }
    }

    @Override
    public int getItemLayoutId(MessageContent item, int position) {
        if (item instanceof RCChatroomLocationMessage || item instanceof TextMessage) {
            return R.layout.item_message_system;
        } else if (item instanceof RCChatroomVoice) {
            return R.layout.item_message_voice;
        } else {
            return R.layout.item_message_normal;
        }
    }

    @Override
    public void convert(RcyHolder holder, MessageContent messageContent, int position, int layoutId) {
        if (messageContent instanceof RCChatroomLocationMessage || messageContent instanceof TextMessage) {
            setSystemMessage(holder, messageContent);
        } else if (messageContent instanceof RCChatroomVoice) {
            setVoiceMessage(holder, (RCChatroomVoice) messageContent);
        } else {
            setNormalMessage(holder, messageContent);
        }
    }

    /**
     * 设置系统消息数据
     *
     * @param holder         holder
     * @param messageContent messageContent
     */
    private void setSystemMessage(RcyHolder holder, MessageContent messageContent) {
        if (messageContent instanceof RCChatroomLocationMessage) {
            holder.setTextColor(R.id.tv_message_system, Color.parseColor("#6A9FFF"));
            holder.setText(R.id.tv_message_system, ((RCChatroomLocationMessage) messageContent).getContent());
        } else if (messageContent instanceof TextMessage) {
            if (!TextUtils.isEmpty(messageContent.getExtra()) && messageContent.getExtra().equals("mixTypeChange")) {
                holder.setTextColor(R.id.tv_message_system, Color.parseColor("#EF499A"));
            } else {
                holder.setTextColor(R.id.tv_message_system, Color.parseColor("#6A9FFF"));
            }
            holder.setText(R.id.tv_message_system, ((TextMessage) messageContent).getContent());
        }

    }

    /**
     * 设置语音消息数据
     *
     * @param holder  holder
     * @param message messageContent
     */
    private void setVoiceMessage(RcyHolder holder, RCChatroomVoice message) {
        EmojiTextView messageTextView = holder.getView(R.id.tv_message_content);
        List<MsgInfo> list = new ArrayList<>(4);
        list.add(new MsgInfo(String.format("%s: ", message.getUserName()), message.getUserId(), true, 0, 0));
        messageTextView.setText(styleBuilder(list));
        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.setText(R.id.tv_voice_duration, String.format("%s''", message.getDuration()));
        AnimationDrawable animationDrawable = (AnimationDrawable) ((ImageView) holder.getView(R.id.iv_paly_voice_id)).getDrawable();
        animationDrawable.stop();
        holder.setOnClickListener(R.id.ll_paly_voice_id, v -> {
            if (RCAudioPlayManager.getInstance().isPlaying() && TextUtils.equals(message.getPath(), AudioPlayManager.getInstance().getPlayingUri().toString())) {
                //当前正在播放,并且点击的就是当前的
                AudioPlayManager.getInstance().stopPlay();
                return;
            }
            RCAudioPlayManager.getInstance().startPlay(context, Uri.parse(message.getPath()), new IAudioPlayListener() {

                @Override
                public void onStart(Uri uri) {
                    //开始动画
                    animationDrawable.start();
                }

                @Override
                public void onStop(Uri uri) {
                    //音频被停止,停止动画
                    animationDrawable.stop();
                    animationDrawable.selectDrawable(0);
                }

                @Override
                public void onComplete(Uri uri) {
                    //停止动画
                    animationDrawable.stop();
                    animationDrawable.selectDrawable(0);
                }
            });
        });
    }

    /**
     * 设置普通消息数据
     *
     * @param holder  holder
     * @param message messageContent
     */
    private void setNormalMessage(RcyHolder holder, MessageContent message) {
        EmojiTextView messageTextView = holder.getView(R.id.tv_message_normal);
        List<MsgInfo> list = new ArrayList<>(4);
        messageTextView.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                0,
                0
        );
        messageTextView.setCompoundDrawablePadding(0);
        messageTextView.setBackgroundResource(R.drawable.bg_voice_room_message_item);
        if (message instanceof RCChatroomBarrage) {
            list.add(new MsgInfo(String.format("%s: ", ((RCChatroomBarrage) message).getUserName()), ((RCChatroomBarrage) message).getUserId(), true, 0, 0));
            list.add(new MsgInfo(((RCChatroomBarrage) message).getContent(), "", false, 0, 0));
        } else if (message instanceof RCChatroomEnter) {
            list.add(new MsgInfo(String.format("%s ", ((RCChatroomEnter) message).getUserName()), ((RCChatroomEnter) message).getUserId(), true, 0, 0));
            list.add(new MsgInfo("进来了", "", false, 0, 0));
        } else if (message instanceof RCChatroomKickOut) {
            list.add(new MsgInfo(String.format("%s 被 ", ((RCChatroomKickOut) message).getTargetName()), "", false, 0, 0));
            list.add(new MsgInfo(String.format("%s ", ((RCChatroomKickOut) message).getUserName()), ((RCChatroomKickOut) message).getUserId(), true, 0, 0));
            list.add(new MsgInfo(" 踢出去了", "", false, 0, 0));
        } else if (message instanceof RCChatroomGiftAll) {
            list.add(new MsgInfo(String.format("%s ", ((RCChatroomGiftAll) message).getUserName()), ((RCChatroomGiftAll) message).getUserId(), true, 0, 0));
            list.add(new MsgInfo(String.format("全麦打赏 %s x%s", ((RCChatroomGiftAll) message).getGiftName(), ((RCChatroomGiftAll) message).getNumber()), "", false, 0, 0));
            if (roomType != RoomType.LIVE_ROOM)
                messageTextView.setBackgroundResource(R.drawable.bg_voice_room_gift_message_item);
        } else if (message instanceof RCChatroomGift) {
            list.add(new MsgInfo(String.format("%s ", ((RCChatroomGift) message).getUserName()), ((RCChatroomGift) message).getUserId(), true, 0, 0));
            list.add(new MsgInfo(" 送给 ", "", false, 0, 0));
            list.add(new MsgInfo(String.format("%s ", ((RCChatroomGift) message).getTargetName()), ((RCChatroomGift) message).getTargetId(), true, 0, 0));
            list.add(new MsgInfo(String.format(" %s x%s", ((RCChatroomGift) message).getGiftName(), ((RCChatroomGift) message).getNumber()), "", false, 0, 0));
            if (roomType != RoomType.LIVE_ROOM)
                messageTextView.setBackgroundResource(R.drawable.bg_voice_room_gift_message_item);
        } else if (message instanceof RCChatroomAdmin) {
            list.add(new MsgInfo(String.format("%s ", ((RCChatroomAdmin) message).getUserName()), ((RCChatroomAdmin) message).getUserId(), true, 0, 0));
            if (((RCChatroomAdmin) message).isAdmin()) {
                list.add(new MsgInfo(" 成为管理员", "", false, 0, 0));
            } else {
                list.add(new MsgInfo(" 被撤回管理员", "", false, 0, 0));
            }
        } else if (message instanceof RCChatroomSeats) {
            list.add(new MsgInfo(String.format("房间更换为 %s 座模式，请重新上麦", ((RCChatroomSeats) message).getCount()), "", false, 0, 0));
        } else if (message instanceof RCChatroomLocationMessage) {
            list.add(new MsgInfo(((RCChatroomLocationMessage) message).getContent(), "", false, 0, 0));
        } else if (message instanceof RCFollowMsg) {
            User user = ((RCFollowMsg) message).getUser();
            User targetUser = ((RCFollowMsg) message).getTargetUser();
            list.add(new MsgInfo(TextUtils.equals(user.getUserId(), UserManager.get().getUserId()) ? "你" : user.getUserName(), user.getUserId(), true, 0, 0));
            list.add(new MsgInfo(" 关注了 ", "", false, 0, 0));
            list.add(new MsgInfo(TextUtils.equals(targetUser.getUserId(), UserManager.get().getUserId()) ? "你" : targetUser.getUserName(), targetUser.getUserId(), true, 0, 0));
        }
        messageTextView.setText(styleBuilder(list));
        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private SpannableStringBuilder styleBuilder(List<MsgInfo> infos) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        int start = 0;
        MsgInfo info;
        for (int i = 0; i < infos.size(); i++) {
            info = infos.get(i);
            if (!TextUtils.isEmpty(info.getClickId())) {
                if (TextUtils.equals(info.getClickId(), mRoomCreateId)) {
                    SpannableString icon = new SpannableString(" ");
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_creator);
                    drawable.setBounds(0, 0, iconSize, iconSize);
                    icon.setSpan(new CenterAlignImageSpan(drawable), 0, icon.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    style.append(icon).append(" ");
                    info.start = start;
                    start += 2;
                    info.end = start;
                } else if (MemberCache.getInstance().isAdmin(info.getClickId())) {
                    SpannableString icon = new SpannableString(" ");
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_is_admin);
                    drawable.setBounds(0, 0, iconSize, iconSize);
                    icon.setSpan(new CenterAlignImageSpan(drawable), 0, icon.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    style.append(icon).append(" ");
                    info.start = start;
                    start += 2;
                    info.end = start;
                }
            }
            info.start = start;
            start += info.getContent().length();
            info.end = start;
            style.append(info.getContent());
            if (info.isClicked()) {
                MsgInfo finalInfo = info;
                style.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        mOnClickMessageUserListener.clickMessageUser(finalInfo.getClickId());
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(Color.parseColor("#78FFFFFF"));
                    }
                }, info.start, info.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return style;
    }

    public interface OnClickMessageUserListener {
        void clickMessageUser(String userId);
    }

    class MsgInfo {
        private String content = "";
        private String clickId = "";
        private boolean clicked = false;
        private int start = 0;
        private int end = 0;

        public MsgInfo(String content, String clickId, boolean clicked, int start, int end) {
            this.content = content;
            this.clickId = clickId;
            this.clicked = clicked;
            this.start = start;
            this.end = end;
        }

        public String getContent() {
            return content;
        }

        public String getClickId() {
            return clickId;
        }

        public boolean isClicked() {
            return clicked;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
}
