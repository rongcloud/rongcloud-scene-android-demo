package cn.rongcloud.voice.room;


import com.basis.ui.mvp.IBasePresent;

import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.voice.model.UiSeatModel;

interface IVoiceRoomPresent extends IBasePresent {

    void onNetworkStatus(int i);

    void setCurrentRoom(VoiceRoomBean mVoiceRoomBean);

    VoiceRoomBean getmVoiceRoomBean();

    /**
     * 监听时间
     *
     * @param roomId
     */
    void initListener(String roomId);

    /**
     * 空座位被点击 观众
     */
    void enterSeatViewer(int position);

    /**
     * 空座位被点击 房主
     *
     * @param seatStatus
     * @param
     */
    void enterSeatOwner(UiSeatModel seatStatus);
}
