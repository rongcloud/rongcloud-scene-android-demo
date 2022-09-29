package cn.rc.community.bean;

import java.io.Serializable;

import io.rong.imlib.model.Message;

public class MarkMessage implements Serializable {
    private String channelUid;
    private String messageUid;
    private String uid;
    private Message message;

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public String getChannelUid() {
        return channelUid;
    }

    public String getMessageUid() {
        return messageUid;
    }

    public String getUid() {
        return uid;
    }
}
