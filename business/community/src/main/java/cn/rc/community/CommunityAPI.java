package cn.rc.community;

import cn.rongcloud.config.ApiConfig;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/24
 * @time 3:28 下午
 * 实时社区接口
 */
public class CommunityAPI {

    private final static String HOST = ApiConfig.HOST;

    //创建社区 post
    public final static String Community_Create = HOST + "mic/community/save";

    //查询社区
    public final static String Community_find = HOST + "mic/community/page";

    //社区详情
    public final static String Community_Details = HOST + "mic/community/detail/";

    //频道详情
    public final static String Channel_Details = HOST + "mic/channel/detail/";

    //分页查询用户已加入社区列表
    public final static String Community_list = HOST + "mic/community/user/pageCommunity";

    //创建频道
    public final static String Community_create_channel = HOST + "mic/channel/save";

    //删除频道 channelUid
    public final static String Community_delete_channel = HOST + "mic/channel/delete/";

    //删除分组 groupUid
    public final static String Community_delete_group = HOST + "mic/group/delete/";

    //整体保存
    public final static String Community_save_all = HOST + "mic/community/saveAll";

    //创建分组
    public final static String Community_create_group = HOST + "mic/group/save";

    // /mic/community/user/join/{communityUid}
    public final static String Community_Join = HOST + "/mic/community/user/join/";

    //关于社区用户的修改
    public final static String Community_update_user_info = HOST + "mic/community/user/update";

    //用户修改频道设置
    public final static String Community_update_channel_setting = HOST + "mic/community/user/update/channel";

    //解散群组
    public final static String Community_delete = HOST + "mic/community/delete/";

    //社区用户
    public final static String Community_User = HOST + "mic/community/user/page";

    // 频道标记消息
    public final static String CHANNEL_MARK_MSG = HOST + "mic/channel/message/page";

    // 移除标记 /mic/channel/message/delete/{uid}
    public final static String REMOVE_MARK_MSG = HOST + "mic/channel/message/delete/";

    // 标记
    public final static String MARK_MSG = HOST + "mic/channel/message/save";

    //标记消息详情
    public final static String MARK_MSG_DETAILS = HOST + "mic/channel/message/detail/";

    // 修改频道  /mic/channel/update
    public final static String Channel_Update = HOST + "mic/channel/update";

    //查询社区用户信息
    public final static String Community_User_info = HOST + "mic/community/user/info";


}
