package cn.rongcloud.config.router;

/**
 * @author gyn
 * @date 2022/2/10
 */
public class RouterPath {

    // 启动页
    public static final String ROUTER_SPLASH = "/app/splash";
    //主页
    public static final String ROUTER_MAIN = "/app/main";

    // 登录页
    public static final String ROUTER_LOGIN = "/profile/login";

    // h5 页面
    public static final String ROUTER_H5 = "/profile/h5";


    // 语聊房相关页面
    /**
     * 房间列表页面
     */
    public static final String ROUTER_VOICE_LIST = "/voiceroom/roomlist";
    /**
     * 语聊房房间页面
     */
    public static final String ROUTER_VOICE_ROOM = "/voiceroom/room";

    // 电台房相关页面
    /**
     * 电台房列表页
     */
    public static final String ROUTER_RADIO_LIST = "/radio/roomlist";
    /**
     * 电台房房间页面
     */
    public static final String ROUTER_RADIO_ROOM = "/radio/room";

    // 直播相关页面
    /**
     * 直播列表
     */
    public static final String ROUTER_LIVE_LIST = "/live/livelist";
    /**
     * 直播房房间页面
     */
    public static final String ROUTER_LIVE_ROOM = "/live/room";

    // 音视频通话相关页面
    /**
     * 音视频通话页面
     */
    public static final String ROUTER_CALL = "/call/callpage";

    // 主页fragment
    public static final String FRAGMENT_COMMUNITY = "/community/fragment_community";
    public static final String FRAGMENT_FIND = "/community/fragment_find";
    public static final String FRAGMENT_MESSAGE = "/community/fragment_message";
    public static final String FRAGMENT_HOME = "/app/fragment_home";
    public static final String FRAGMENT_ME = "/profile/fragment_me";
    public static final String FRAGMENT_ME_COMMUNITY = "/profile/fragment_me_community";


}
