package cn.rongcloud.radio.helper;

import java.util.List;

import cn.rongcloud.radioroom.room.RCRadioEventListener;
import io.rong.imlib.model.Message;

/**
 * @author gyn
 * @date 2021/10/13
 */
public interface RadioRoomListener extends RCRadioEventListener {
    void onLoadMessageHistory(List<Message> messages);
}
