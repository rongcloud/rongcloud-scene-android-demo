package cn.rongcloud.gameroom.model;

/**
 * @author gyn
 * @date 2022/5/13
 */
public class GameCreateBean {
    // 是否是新创建的，true是；false否，之前创建的
    public boolean isCreate;
    public GameRoomBean gameRoomBean;
    public boolean isFastIn;
    public String fastInGameId;

    public GameCreateBean(boolean isCreate, GameRoomBean gameRoomBean, boolean isFastIn) {
        this(isCreate, gameRoomBean, isFastIn, "");
    }

    public GameCreateBean(boolean isCreate, GameRoomBean gameRoomBean, boolean isFastIn, String fastInGameId) {
        this.isCreate = isCreate;
        this.gameRoomBean = gameRoomBean;
        this.isFastIn = isFastIn;
        this.fastInGameId = fastInGameId;
    }
}
