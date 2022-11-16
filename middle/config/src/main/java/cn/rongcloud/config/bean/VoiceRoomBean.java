package cn.rongcloud.config.bean;


import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.provider.wrapper.Provide;

public class VoiceRoomBean implements Provide {

    private int id;
    private String roomId;
    private String roomName;
    private String themePictureUrl;
    private String backgroundUrl;
    private int isPrivate;
    private String password;
    private String userId;
    private long updateDt;
    private User createUser;
    private int roomType;
    private int userTotal;
    private String stopEndTime;
    private String currentTime;
    private boolean stop;

    @Override
    public String getKey() {
        return roomId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getThemePictureUrl() {
        return themePictureUrl;
    }

    public void setThemePictureUrl(String themePictureUrl) {
        this.themePictureUrl = themePictureUrl;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public boolean isPrivate() {
        return isPrivate == 1;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(long updateDt) {
        this.updateDt = updateDt;
    }

    public User getCreateUser() {
        return createUser;
    }

    public String getCreateUserName() {
        if (createUser != null) {
            return createUser.getUserName();
        } else {
            return "";
        }
    }

    public String getCreateUserPortrait() {
        if (createUser != null) {
            return createUser.getPortraitUrl();
        } else {
            return "";
        }
    }

    public String getCreateUserId() {
        if (createUser != null) {
            return createUser.getUserId();
        } else {
            return "";
        }
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public int getUserTotal() {
        return userTotal;
    }

    public void setUserTotal(int userTotal) {
        this.userTotal = userTotal;
    }

    public Object getStopEndTime() {
        return stopEndTime;
    }

    public void setStopEndTime(String stopEndTime) {
        this.stopEndTime = stopEndTime;
    }

    public Object getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
