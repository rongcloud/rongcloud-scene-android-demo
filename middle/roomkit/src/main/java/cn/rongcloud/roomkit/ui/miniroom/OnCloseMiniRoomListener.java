package cn.rongcloud.roomkit.ui.miniroom;

public interface OnCloseMiniRoomListener {

    void onCloseMiniRoom(CloseResult closeResult);

    interface CloseResult {
        void onClose();
    }
}