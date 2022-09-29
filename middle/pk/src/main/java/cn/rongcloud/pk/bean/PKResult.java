package cn.rongcloud.pk.bean;

import java.util.List;

public class PKResult {
    private long timeDiff;
    private int statusMsg;
    private List<PKInfo> roomScores;

    public List<PKInfo> getRoomScores() {
        return roomScores;
    }

    public long getTimeDiff() {
        return timeDiff;
    }

    public int getStatusMsg() {
        return statusMsg;
    }
}