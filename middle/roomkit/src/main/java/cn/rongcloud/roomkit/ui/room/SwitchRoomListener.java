package cn.rongcloud.roomkit.ui.room;

/**
 * @author gyn
 * @date 2021/10/15
 */
public interface SwitchRoomListener {
    void preJoinRoom();

    void joinRoom();

    void destroyRoom();

    void onBackPressed();

    void addSwitchRoomListener();

    void removeSwitchRoomListener();
}
