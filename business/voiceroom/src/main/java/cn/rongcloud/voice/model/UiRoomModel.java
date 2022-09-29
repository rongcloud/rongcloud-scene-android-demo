package cn.rongcloud.voice.model;

import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.voiceroom.model.RCVoiceRoomInfo;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class UiRoomModel {
    private RCVoiceRoomInfo rcRoomInfo;
    private VoiceRoomBean roomBean;
    // 静音标识
    private boolean isMute;

    private BehaviorSubject<UiRoomModel> subject;

    public UiRoomModel(BehaviorSubject<UiRoomModel> subject) {
        this.subject = subject;
    }

    public RCVoiceRoomInfo getRcRoomInfo() {
        return rcRoomInfo;
    }

    public void setRcRoomInfo(RCVoiceRoomInfo rcRoomInfo) {
        this.rcRoomInfo = rcRoomInfo;
        if (null != subject) subject.onNext(this);
    }

    public VoiceRoomBean getRoomBean() {
        return roomBean;
    }

    public void setRoomBean(VoiceRoomBean roomBean) {
        this.roomBean = roomBean;
        if (null != subject) subject.onNext(this);
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
        if (null != subject) subject.onNext(this);
    }


    public void setSeatCount(int count) {
        if (null != rcRoomInfo)rcRoomInfo.setSeatCount(count);
        if (null != subject) subject.onNext(this);
    }
    public int getSeatCount() {
        return null != rcRoomInfo ? rcRoomInfo.getSeatCount() : 9;
    }

    public void setFreeEnterSeat(boolean freeEnterSeat) {
       if (null != rcRoomInfo)rcRoomInfo.setFreeEnterSeat(freeEnterSeat);
        if (null != subject) subject.onNext(this);
    }

    public boolean isFreeEnterSeat() {
        return null != rcRoomInfo ? rcRoomInfo.isFreeEnterSeat() : false;
    }

    public boolean isLockAll() {
        return null != rcRoomInfo ? rcRoomInfo.isLockAll() : false;
    }


    public boolean isMuteAll() {
        return null != rcRoomInfo ? rcRoomInfo.isMuteAll() : false;
    }
}

