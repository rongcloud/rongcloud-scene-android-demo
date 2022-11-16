package cn.rc.community.conversion.controller;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.net.oklib.api.core.Dispatcher;
import com.basis.ui.UIStack;
import com.basis.utils.UIKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rc.community.R;
import cn.rc.community.conversion.controller.interfaces.IMessage;
import cn.rc.community.conversion.controller.interfaces.IMessageAdapter;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.RongCoreClient;
import io.rong.imlib.model.Conversation;

/**
 * 消息适配器
 */
class MessageAdapter<T extends IMessage> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IMessageAdapter<T> {
    // itemType和IMessage的映射关系
    private Map<Integer, IMessage> messageTypes;
    private List<T> messages;
    private OnItemClickListener<T> clickListener;
    private OnItemLongClickListener<T> longClickListener;

    public MessageAdapter() {
        messageTypes = new HashMap<>(8);
        messages = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return null == messages ? 0 : messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        IMessage m = getMessage(position);
        int itemType = -1;
        if (null != m) {
            for (Map.Entry<Integer, IMessage> entry : messageTypes.entrySet()) {
                if (TextUtils.equals(entry.getValue().getIdentifier(), m.getIdentifier())) {
                    itemType = entry.getKey();
                }
            }
        }
        if (itemType == -1) {
            throw new IllegalArgumentException("No ViewType Set for position =" + position);
        }
        return itemType;
    }

    @NonNull
    @Override
    public RcyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IMessage message = messageTypes.get(viewType);
        if (null == message) {
            throw new IllegalArgumentException("No ViewHolder Setted for ViewType =" + viewType);
        }
        return new RcyHolder(LayoutInflater.from(UIKit.getContext()).inflate(message.getLayoutId(), parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        T msg = getMessage(position);
        //需要将外部的监听事件传递给每个 attachedInfo对象
        if (null != msg) {
            msg.setOnItemClickListener(clickListener);
            msg.setOnItemLongClickListener(longClickListener);
            msg.convert((RcyHolder) holder, position);
        }
        if (null != clickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(msg, position);
                }
            });
        }
        if (null != longClickListener) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longClickListener.onItemLongClick(msg, position);
                    return false;
                }
            });
        }
        if (!TextUtils.equals(msg.getMessage().getSenderUserId(), RongCoreClient.getInstance().getCurrentUserId())
                && !TextUtils.equals(msg.getMessage().getSenderUserId(), "_SYSTEM_")) {
            ((RcyHolder) holder).setOnClickListener(R.id.cv_id, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RouteUtils.routeToConversationActivity(
                            UIStack.getInstance().getTopActivity(),
                            Conversation.ConversationType.PRIVATE,
                            msg.getMessage().getSenderUserId()
                    );
                }
            });
        } else {
            ((RcyHolder) holder).setOnClickListener(R.id.cv_id, null);
        }
    }


    @Override
    public boolean isRegistered(@NonNull IMessage message) {
        for (IMessage msg : messageTypes.values()) {
            if (TextUtils.equals(msg.getIdentifier(), message.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void registerMessage(@NonNull IMessage message) {
        if (!isRegistered(message)) {
            int itemType = messageTypes.size();
            messageTypes.put(itemType, message);
        }
    }

    @NonNull
    @Override
    public List<T> getMessages() {
        return messages;
    }

    @Override
    public synchronized void addMessages(List<T> messages, boolean clear) {
        if (clear) {
            this.messages.clear();
        }
        if (null != messages) {
            this.messages.addAll(messages);
        }
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public T getMessage(int position) {
        int count = getItemCount();
        if (position < 0 || count == 0 || position >= count) return null;
        return messages.get(position);
    }

    @Override
    public void insert(@NonNull T message, boolean last) {
        Dispatcher.get().dispatch(new Runnable() {
            @Override
            public void run() {
                registerMessage(message);
                if (last) {
                    messages.add(message);
                    notifyItemRangeInserted(messages.size() - 1, 1);
                } else {
                    messages.add(0, message);
                    notifyItemRangeInserted(0, 1);
                }
            }
        });

    }

    @Override
    public void delete(int position) {
        Dispatcher.get().dispatch(new Runnable() {
            @Override
            public void run() {
                T t = messages.remove(position);
                if (null != t) notifyItemRangeRemoved(position, 1);
            }
        });
    }

    @Override
    public void update(T message) {
        Dispatcher.get().dispatch(new Runnable() {
            @Override
            public void run() {
                int i = messages.indexOf(message);
                if (i > -1) notifyItemRangeChanged(i, 1);
            }
        });

    }

    @Override
    public void replace(T oldMessage, T newMessage) {
        Dispatcher.get().dispatch(new Runnable() {
            @Override
            public void run() {
                registerMessage(newMessage);
                int i = messages.indexOf(oldMessage);
                if (i > -1) {
                    messages.set(i, newMessage);
                    notifyItemRangeChanged(i, 1);
                }
            }
        });
    }


    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
        this.longClickListener = listener;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

}
