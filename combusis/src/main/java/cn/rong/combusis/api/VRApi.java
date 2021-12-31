package cn.rong.combusis.api;

import com.rongcloud.common.net.ApiConstant;

public class VRApi {
    public final static String HOST = ApiConstant.INSTANCE.getBASE_URL();
    /**
     * 粉丝或关注列表
     */
    public final static String FOLLOW_LIST = HOST + "user/follow/list";
    /**
     * 创建房间
     */
    public final static String ROOM_CREATE = HOST + "mic/room/create";
    /**
     * 操作管理员
     */
    public static final String ADMIN_MANAGE = HOST + "mic/room/manage";
    /**
     * 房间上锁解锁
     */
    public static final String ROOM_PASSWORD = HOST + "mic/room/private";
    /**
     * 修改房间名称
     */
    public static final String ROOM_NAME = HOST + "mic/room/name";
    /**
     * 修改房间背景
     */
    public static final String ROOM_BACKGROUND = HOST + "mic/room/background";
    /**
     * 添加屏蔽词
     */
    public static final String ADD_SHIELD = HOST + "mic/room/sensitive/add";
    /**
     * 发送礼物
     */
    public static final String SEND_GIFT = HOST + "mic/room/gift/add";
    /**
     * 批量获取用户信息
     */
    public static final String GET_USER = HOST + "user/batch";
    /**
     * 文件上传
     */
    public final static String FILE_UPLOAD = HOST + "file/upload";
    /**
     * 上传文件后，文件的前缀
     */
    public static final String FILE_PATH = HOST + "file/show?path=";

    /**
     * pk状态上报
     */
    public static final String PK_STATE = HOST + "mic/room/pk";

    //    private static final String PK_INFO = HOST + "mic/room/pk/info/";
    private static final String PK_INFO = HOST + "mic/room/pk/detail/";

    // pk/{roomId}/isPk
    public static String isPkState(String roomId) {
        return HOST + "mic/room/pk/" + roomId + "/isPk";
    }

    /**
     * 获取pk积分排行
     *
     * @param roomId
     * @return
     */
    public static String getPKInfo(String roomId) {
        return PK_INFO + roomId;
    }

    /**
     * 发送全服广播的礼物
     */
    public static final String GIFT_BROADCAST = HOST + "mic/room/message/broadcast";

    /**
     * 获取房间内成员列表
     *
     * @param roomId
     * @return
     */
    public static String getMembers(String roomId) {
        return HOST + "mic/room/" + roomId + "/members";
    }

    /**
     * 获取房间内管理员列表
     *
     * @param roomId
     * @return
     */
    public static String getAdminMembers(String roomId) {
        return HOST + "mic/room/" + roomId + "/manage/list";
    }

    /**
     * 检查是否创建了房间
     */
    public static final String ROOM_CREATE_CHECK = HOST + "mic/room/create/check";
    /**
     * 注册或者注销，注销不传任何参数
     */
    public static final String RESIGN = HOST + "user/resign";
    /**
     * 房间列表
     */
    public static String ROOM_LIST = HOST + "mic/room/list";
    /**
     * 在线房主
     */
    public static String ONLINE_CREATER = HOST + "mic/room/online/created/list";

    /**
     * 获取房间信息
     *
     * @param roomId
     * @return
     */
    public static String getRoomInfo(String roomId) {
        return HOST + "mic/room/" + roomId;
    }

    /**
     * 获取屏蔽词列表
     *
     * @param roomId
     * @return
     */
    public static String getShield(String roomId) {
        return HOST + "mic/room/sensitive/" + roomId + "/list";
    }

    /**
     * 删除敏感词
     *
     * @param id
     * @return
     */
    public static String deleteShield(int id) {
        return HOST + "mic/room/sensitive/del/" + id;
    }

    /**
     * 更改用户所属房间
     */
    public static String USER_ROOM_CHANGE = HOST + "user/change";
    /**
     * 检查当前用户所属房间
     */
    public static String USER_ROOM_CHECK = HOST + "user/check";

    /**
     * 获取房间内礼物列表
     *
     * @param roomId
     * @return
     */
    public static String getGiftList(String roomId) {
        return HOST + "mic/room/" + roomId + "/gift/list";
    }

    /**
     * 关注或取消关注
     */
    private static String FOLLOW = HOST + "user/follow/";

    public static String followUrl(String userId) {
        return FOLLOW + userId;
    }

    /**
     * 删除房间
     *
     * @return link
     */
    public static String deleteRoom(String roomId) {
        return HOST + "mic/room/" + roomId + "/delete";
    }

    /**
     * 暂停房间
     *
     * @param roomId
     * @return
     */
    public static String stopRoom(String roomId) {
        return HOST + "mic/room/" + roomId + "/stop";
    }
}
