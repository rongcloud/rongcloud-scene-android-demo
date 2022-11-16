package cn.rc.community.message;

import android.content.Context;

import com.basis.adapter.RcyAdapter;
import com.basis.adapter.RcyHolder;

import java.util.ArrayList;
import java.util.List;

import cn.rc.community.OnConvertListener;
import cn.rc.community.R;
import cn.rc.community.bean.MarkMessage;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.ReferenceMessage;
import io.rong.message.SightMessage;
import io.rong.message.TextMessage;

/**
 * 标准消息适配器
 *
 * @param
 */
public class MarkMessageAdapter extends RcyAdapter<MarkMessage, RcyHolder> {
    private OnConvertListener<MarkMessage> convert;


    @Override
    public synchronized void setData(List<MarkMessage> list, boolean refresh) {
        List<MarkMessage> messages = new ArrayList<>();
        for (MarkMessage msg : list) {
            // 过滤非法数据，避免视图显示异常
            if (null != msg && null != msg.getMessage() && null != msg.getMessage().getContent()) {
                messages.add(msg);
            }
        }
        super.setData(messages, refresh);
    }

    @Override
    public int getItemLayoutId(MarkMessage item, int position) {
        Message message = item.getMessage();
        if (message == null || message.getContent() == null) {
            return R.layout.item_chatroom_left;
        }
        Class<? extends MessageContent> aClass = message.getContent().getClass();
        if (ImageMessage.class.equals(aClass)) {
            return R.layout.item_chatroom_left_image;
        } else if (TextMessage.class.equals(aClass) || ReferenceMessage.class.equals(aClass)) {
            return R.layout.item_chatroom_left;
        } else if (SightMessage.class.equals(aClass)) {
            return R.layout.item_chatroom_left_sight;
        } else if (RecallNotificationMessage.class.equals(aClass)) {
            return R.layout.item_chatroom_left_recall;
        }
        return -1;
    }

    @Override
    public void convert(RcyHolder holder, MarkMessage item, int position, int layoutId) {
        if (null != convert) convert.onConvert(holder, item, position);
    }

    public MarkMessageAdapter(Context context, OnConvertListener<MarkMessage> listener, int... layoutId) {
        super(context, layoutId);
        this.convert = listener;
    }


}
