package cn.rongcloud.radio.helper;

import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2021/10/13
 */
public interface IRadioEventHelper {

    void register(String roomId);

    void unRegister();

    void addRadioEventListener(RadioRoomListener listener);

    void removeRadioEventListener(RadioRoomListener listener);

    boolean isInRoom();

    void sendMessage(MessageContent messageContent);
}
