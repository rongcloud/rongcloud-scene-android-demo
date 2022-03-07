package cn.rongcloud.roomkit.message;

import android.os.Parcel;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2021/10/21
 * 电台房房主关闭房间的消息
 */
@MessageTag(value = "RC:RCRRCloseMsg")
public class RCRRCloseMessage extends MessageContent {
    public static final Creator<RCRRCloseMessage> CREATOR = new Creator<RCRRCloseMessage>() {
        @Override
        public RCRRCloseMessage createFromParcel(Parcel source) {
            return new RCRRCloseMessage(source);
        }

        @Override
        public RCRRCloseMessage[] newArray(int size) {
            return new RCRRCloseMessage[size];
        }
    };

    protected RCRRCloseMessage(Parcel in) {
    }

    public RCRRCloseMessage() {
    }

    public RCRRCloseMessage(byte[] data) {

    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public void readFromParcel(Parcel source) {
    }
}
