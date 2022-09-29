/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package cn.rongcloud.gameroom.model;

import java.util.Objects;

public class SeatPlayer {

    public String userId;

    public boolean isCaptain;

    public boolean isMute;

    public boolean isLock;

    public int seatIndex;

    public boolean isAdmin;

    public PlayerState playerState = PlayerState.IDLE;

    public boolean isSpeaking;

    public enum PlayerState {
        // 座位没人
        EMPTY(""),
        // 空闲状态
        IDLE(""),
        // 已准备状态
        READY("已准备"),
        // 游戏中状态
        PLAY("游戏中");
        public String desc;

        PlayerState(String desc) {
            this.desc = desc;
        }
    }

    public static SeatPlayer getEmptySeatPlayer() {
        return new SeatPlayer();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatPlayer that = (SeatPlayer) o;
        return isCaptain == that.isCaptain
                && isMute == that.isMute
                && isLock == that.isLock
                && isAdmin == that.isAdmin
                && seatIndex == that.seatIndex
                && Objects.equals(userId, that.userId)
                && playerState == that.playerState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, isCaptain, isMute, seatIndex, isAdmin, playerState);
    }
}
