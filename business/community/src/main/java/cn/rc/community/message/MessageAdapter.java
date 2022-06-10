package cn.rc.community.message;

import android.content.Context;

import androidx.annotation.LayoutRes;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.wapper.IResultBack;

import java.util.List;

import cn.rc.community.OnConvertListener;
import cn.rc.community.bean.MarkMessage;
import cn.rc.community.conversion.controller.MessageManager;
import io.rong.imlib.model.Message;

public class MessageAdapter<T> extends RcySAdapter<T, RcyHolder> {
    private OnConvertListener<T> convert;

    @Override
    public synchronized void setData(List<T> list, boolean refresh) {
        super.setData(list, refresh);
        int count = null == list ? 0 : list.size();
        for (int i = 0; i < count; i++) {
            T item = list.get(i);
            if (item instanceof MarkMessage) {
                onLoadMessage(i, (MarkMessage) item);
            }
        }
    }

    public MessageAdapter(Context context, @LayoutRes int layoutId, OnConvertListener<T> listener) {
        super(context, layoutId);
        this.convert = listener;
    }

    @Override
    public void convert(RcyHolder holder, T item, int position) {
        if (null != convert) convert.onConvert(holder, item, position);
    }

    public void onLoadMessage(int position, MarkMessage mark) {
        if (null == mark || null != mark.getMessage()) {
            return;
        }
        MessageManager.get().getMessage(mark.getMessageUid(), new IResultBack<Message>() {
            @Override
            public void onResult(Message message) {
                if (null != message) {
                    mark.setMessage(message);
                    notifyItemChanged(position);
                }
            }
        });
    }
}
