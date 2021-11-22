package cn.rong.combusis.widget.miniroom;

public interface OnCloseMiniRoomListener {

    void onCloseMiniRoom(CloseResult closeResult);

    interface CloseResult {
        void onClose();
    }
}