package cn.rongcloud.roomkit.manager;

import com.basis.utils.UIKit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.rongcloud.roomkit.message.RCAllBroadcastMessage;


/**
 * @author gyn
 * @date 2021/10/18
 * 全服广播控制类
 * 维护一个消息集合，有新消息就添加进去，每隔5秒钟抛出一个消息并触发一次监听
 */
public class AllBroadcastManager {
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private List<RCAllBroadcastMessage> messageList = new ArrayList<>();
    private OnObtainMessage onObtainMessage;

    public static AllBroadcastManager getInstance() {
        return Holder.INSTANCE;
    }

    public void addMessage(RCAllBroadcastMessage message) {
        messageList.add(message);
        if (!isRunning.get()) {
            start(0);
        }
    }

    public void setListener(OnObtainMessage onObtainMessage) {
        this.onObtainMessage = onObtainMessage;
        if (!isRunning.get()) {
            start(0);
        }
    }

    private void start(long delay) {
        isRunning.set(true);
        UIKit.postDelayed(() -> {
            if (messageList.isEmpty()) {
                isRunning.set(false);
                if (onObtainMessage != null) {
                    onObtainMessage.onMessage(null);
                }
            } else {
                RCAllBroadcastMessage message = messageList.remove(0);
                if (onObtainMessage != null) {
                    onObtainMessage.onMessage(message);
                }
                start(5000);
            }
        }, delay);
    }

    public void removeListener(OnObtainMessage onObtainMessage) {
        if (onObtainMessage == this.onObtainMessage) {
            this.onObtainMessage = null;
        }
    }

    public interface OnObtainMessage {
        void onMessage(RCAllBroadcastMessage message);
    }

    private static class Holder {
        private final static AllBroadcastManager INSTANCE = new AllBroadcastManager();
    }
}
