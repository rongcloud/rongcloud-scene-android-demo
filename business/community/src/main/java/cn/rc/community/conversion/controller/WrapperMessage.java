package cn.rc.community.conversion.controller;

import com.basis.adapter.RcyHolder;

import cn.rc.community.OnConvertListener;
import cn.rc.community.conversion.controller.interfaces.IManager;
import cn.rc.community.conversion.controller.interfaces.IMessage;
import cn.rc.community.conversion.controller.interfaces.IMessageAdapter;
import io.rong.imlib.model.Message;

/**
 * 消息的包装类
 */
public class WrapperMessage implements IMessage {
    private Message message;
    private int layout;
    private int progress;
    private String objectName;
    private OnConvertListener<WrapperMessage> listener;
    private IMessageAdapter.OnItemClickListener clickListener;
    private IMessageAdapter.OnItemLongClickListener longClickListener;

    public Message getMessage() {
        return message;
    }

    @Override
    public void setOnItemLongClickListener(IMessageAdapter.OnItemLongClickListener onItemLongClickListener) {
        longClickListener = onItemLongClickListener;
    }

    @Override
    public void setOnItemClickListener(IMessageAdapter.OnItemClickListener listener) {
        clickListener = listener;
    }

    public IMessageAdapter.OnItemClickListener getClickListener() {
        return clickListener;
    }

    public IMessageAdapter.OnItemLongClickListener getLongClickListener() {
        return longClickListener;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    private WrapperMessage() {
    }

    public static WrapperMessage fromMessage(Message message, IManager.AttachedInfo attachedInfo) {
        WrapperMessage msg = new WrapperMessage();
        msg.message = message;
        msg.objectName = message.getObjectName();
        if (null != attachedInfo) {
            msg.layout = attachedInfo.onSetLayout(msg);
            msg.listener = attachedInfo.onSetConvertListener();
        }
        return msg;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String getIdentifier() {
        return objectName;
    }

    @Override
    public int getLayoutId() {
        return layout;
    }

    @Override
    public void convert(RcyHolder holder, int position) {
        if (null != listener) {
            listener.onConvert(holder, this, position);
        }
    }
}
