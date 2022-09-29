package cn.rc.community.conversion.convert;

import android.app.Activity;
import android.view.View;

import com.basis.adapter.RcyHolder;
import com.basis.ui.UIStack;
import com.basis.utils.DateUtil;

import cn.rc.community.Constants;
import cn.rc.community.R;
import cn.rc.community.channel.details.MarkMessageActivity;
import cn.rc.community.conversion.ObjectName;
import cn.rc.community.conversion.controller.BaseMessageAttachedInfo;
import cn.rc.community.conversion.controller.WrapperMessage;
import cn.rc.community.message.sysmsg.ChannelNoticeMsg;
import cn.rc.community.message.sysmsg.ChannelType;
import io.rong.imlib.model.Message;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/20
 * @time 15:29
 * 系统消息
 */
public class ChannelNoticeMessageAttachedInfo extends BaseMessageAttachedInfo {
    @Override
    public void onConvert(RcyHolder holder, WrapperMessage item, int position) {
        Message message = item.getMessage();
        ChannelNoticeMsg channelNoticeMsg = (ChannelNoticeMsg) message.getContent();
        holder.setText(R.id.tv_content_id, channelNoticeMsg.getMessage());
        holder.setText(R.id.tv_time_id, DateUtil.getRecordDate(message.getSentTime()));
        ChannelType type = channelNoticeMsg.getType();
        switch (type) {
            case marked:
            case removedMarked:
                holder.setVisible(R.id.tv_jump_to_id, true);
                break;
            case joined:
            case enabled:
            case disabled:
            case quit:
                holder.setVisible(R.id.tv_jump_to_id, false);
                break;
        }
        holder.setOnClickListener(R.id.tv_jump_to_id, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity topActivity = UIStack.getInstance().getTopActivity();
                MarkMessageActivity.openMarkMessage(topActivity, channelNoticeMsg.getChannelUid(), Constants.markMessageRequestCode);
            }
        });
    }

    @Override
    public int onSetLayout(WrapperMessage message) {
        return R.layout.item_chatroom_left_channel_notice;
    }

    @Override
    public String onSetObjectName() {
        return ObjectName.CHANNEL_NOTICE;
    }
}
