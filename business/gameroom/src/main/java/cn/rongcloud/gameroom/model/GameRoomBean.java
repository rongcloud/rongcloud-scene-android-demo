package cn.rongcloud.gameroom.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.gamelib.model.RCGameInfo;

/**
 * @author gyn
 * @date 2022/3/17
 */
public class GameRoomBean extends VoiceRoomBean implements Serializable {
    @SerializedName("gameResp")
    private RCGameInfo gameInfo;

    public RCGameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(RCGameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }
}
