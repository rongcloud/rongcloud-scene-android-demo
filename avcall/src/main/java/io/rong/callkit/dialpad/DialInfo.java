package io.rong.callkit.dialpad;

import java.io.Serializable;
import java.util.Objects;

public class DialInfo implements Serializable {
    private String phone;
    private String userId;
    private String head;
    private long date;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DialInfo dialInfo = (DialInfo) o;
        return Objects.equals(phone, dialInfo.phone);
    }
}
