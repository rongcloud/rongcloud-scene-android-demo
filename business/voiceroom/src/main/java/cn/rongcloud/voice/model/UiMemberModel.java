package cn.rongcloud.voice.model;

import cn.rongcloud.roomkit.ui.room.model.Member;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class UiMemberModel {
    private Member member;
    private boolean isAdmin;
    private int giftCount;
    private boolean isRequestSeat;
    private boolean isInvitedInfoSeat;
    private boolean selected;


    private BehaviorSubject<UiMemberModel> subject;

    public UiMemberModel(BehaviorSubject<UiMemberModel> subject) {
        this.subject = subject;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
        if (null != subject) subject.onNext(this);
    }

    public String getPortrait() {
        return null != member?member.getPortrait():"";
    }

    public void setPortrait(String portrait) {
        if (null != member)member.setPortrait(portrait);
    }

    public String getUserId() {
        return null != member?member.getUserId():"";
    }

    public String getUserName() {
        return null != member?member.getUserName():"";
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
        if (null != subject) subject.onNext(this);
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
        if (null != subject) subject.onNext(this);
    }

    public boolean isRequestSeat() {
        return isRequestSeat;
    }

    public void setRequestSeat(boolean requestSeat) {
        isRequestSeat = requestSeat;
        if (null != subject) subject.onNext(this);
    }

    public boolean isInvitedInfoSeat() {
        return isInvitedInfoSeat;
    }

    public void setInvitedInfoSeat(boolean invitedInfoSeat) {
        isInvitedInfoSeat = invitedInfoSeat;
        if (null != subject) subject.onNext(this);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (null != subject) subject.onNext(this);
    }

}
