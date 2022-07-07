package cn.rongcloud.gameroom.api;

import cn.rongcloud.config.ApiConfig;

/**
 * @author gyn
 * @date 2022/3/17
 */
public class GameApi {
    // 游戏列表接口
    public static final String GAME_LIST = ApiConfig.HOST + "mic/game/list";

    // 登录游戏获取appCode的接口
    public static final String GAME_LOGIN_URL = ApiConfig.HOST + "mic/game/login";

    // 快速开始游戏的接口
    public static final String GAME_FAST_JOIN = ApiConfig.HOST + "mic/game/join";

    /**
     * 房间列表
     */
    public static final String ROOM_LIST = ApiConfig.HOST + "mic/room/list";
    /**
     * 切换游戏
     */
    public static final String SWITCH_GAME = ApiConfig.HOST + "mic/game/toggle/game_id";
    /**
     * 游戏状态改变
     * gameStatus
     * 游戏状态：1 未开始 2 游戏中
     */
    public static final String GAME_STATE = ApiConfig.HOST + "mic/game/toggle/game_status";
}
