package cn.rongcloud.voice.model;

import com.basis.utils.GsonUtil;

import cn.rongcloud.voiceroom.model.RCVoiceSeatInfo;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class UiSeatModel {
    private UiMemberModel member;
    private int giftCount;
    private int index;
    private RCVoiceSeatInfo seatModel;
    private BehaviorSubject<UiSeatModel> subject;

    public UiSeatModel(int index, RCVoiceSeatInfo seatInfo, BehaviorSubject<UiSeatModel> subject) {
        this.index = index;
        this.seatModel = seatInfo;
        this.subject = subject;
    }

    public int getIndex() {
        return index;
    }

    public UiMemberModel getMember() {
        return member;
    }

    public void setMember(UiMemberModel member) {
        // giftCount处理被重置
        int temp = getGiftCount();
        if (member.getGiftCount() < 1) {
            member.setGiftCount(temp);
        }
        this.member = member;
        if (null != subject) subject.onNext(this);
    }

    public String getUserId() {
        return seatModel != null ? seatModel.getUserId() : "";
    }


    public RCVoiceSeatInfo.RCSeatStatus getSeatStatus() {
        return seatModel != null ? seatModel.getStatus() : RCVoiceSeatInfo.RCSeatStatus.RCSeatStatusEmpty;
    }


    public boolean isMute() {
        return seatModel != null ? seatModel.isMute() : false;
    }

    public void setMute(boolean mute) {
        if (null != seatModel) seatModel.setMute(mute);
        if (null != subject) subject.onNext(this);
    }

    public boolean isSpeaking() {
        return seatModel != null ? seatModel.isSpeaking() : false;
    }

    public void setSpeaking(boolean speaking) {
        if (null != seatModel) seatModel.setSpeaking(speaking);
        if (null != subject) subject.onNext(this);
    }

    public UiSeatModelExtra getExtra() {
        UiSeatModelExtra extra = GsonUtil.json2Obj(seatModel.getExtra(), UiSeatModelExtra.class);
        return null != extra ? extra : new UiSeatModelExtra();
    }

    public void setExtra(UiSeatModelExtra extra) {
//        Logger.e("seatModel：setExtra extra= "+GsonUtil.obj2Json(extra));
        if (null != seatModel) {
            seatModel.setExtra(GsonUtil.obj2Json(extra));
        }
        if (null != subject) subject.onNext(this);
    }

    public String getPortrait() {
        return member != null ? member.getPortrait() : "";
    }


    public int getGiftCount() {
        if (null != member && member.getGiftCount() > 0) {
            giftCount = member.getGiftCount();
        }
//        Logger.e("UiSeatModel#getGiftCount: = " + giftCount + " index = " + index);
        return giftCount;
    }

    public void setGiftCount(int giftCount) {
//        Logger.e("UiSeatModel#setGiftCount: = " + giftCount + " index = " + index);
        this.giftCount = giftCount;
        if (null != member) member.setGiftCount(giftCount);
        if (null != subject) subject.onNext(this);
    }

    public static class UiSeatModelExtra {
        private boolean disableRecording = false;

        public boolean isDisableRecording() {
            return disableRecording;
        }

        public void setDisableRecording(boolean disableRecording) {
            this.disableRecording = disableRecording;
        }
    }
}
