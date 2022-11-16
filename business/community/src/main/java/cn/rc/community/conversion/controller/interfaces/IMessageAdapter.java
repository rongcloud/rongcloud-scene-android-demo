package cn.rc.community.conversion.controller.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * 消息适配接口
 */
public interface IMessageAdapter<T extends IMessage> {
    /**
     * 是否已注册
     */
    boolean isRegistered(@NonNull T message);

    /**
     * 注册
     */
    void registerMessage(@NonNull IMessage message);

    List<T> getMessages();

    /**
     * 添加消息
     *
     * @param messages 消息集
     * @param clear    是否清空原集合
     */
    void addMessages(List<T> messages, boolean clear);

    /**
     * 获取指定位置的消息
     *
     * @param position 指定位置
     * @return 指定位置的消息
     */
    @Nullable
    T getMessage(int position);

    /**
     * 插入一条消息
     *
     * @param message 消息
     * @param last    是否追加到最后
     */
    void insert(@NonNull T message, boolean last);

    /**
     * 移除指定位置的消息
     *
     * @param position 位置
     */
    @Nullable
    void delete(int position);

    /**
     * 更新指定位置的消息
     */
    void update(T message);

    /**
     * 替换指定位置的消息
     *
     * @param oldMessage
     * @param newMessage
     */
    void replace(T oldMessage, T newMessage);


    /**
     * 设置长按监听
     *
     * @param listener 监听
     */
    void setOnItemLongClickListener(OnItemLongClickListener<T> listener);

    /**
     * 设置点击监听
     *
     * @param listener 监听
     */
    void setOnItemClickListener(OnItemClickListener<T> listener);

    interface OnItemLongClickListener<T extends IMessage> {
        void onItemLongClick(T msg, int position);
    }

    interface OnItemClickListener<T extends IMessage> {
        void onItemClick(T t, int position);
    }
}
