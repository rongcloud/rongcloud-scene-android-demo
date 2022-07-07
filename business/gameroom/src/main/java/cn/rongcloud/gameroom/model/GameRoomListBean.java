package cn.rongcloud.gameroom.model;

import java.util.List;

/**
 * @author gyn
 * @date 2022/5/13
 */
public class GameRoomListBean {
    public int page;
    public List<GameRoomBean> gameRoomList;

    public GameRoomListBean(int page, List<GameRoomBean> gameRoomList) {
        this.page = page;
        this.gameRoomList = gameRoomList;
    }
}
