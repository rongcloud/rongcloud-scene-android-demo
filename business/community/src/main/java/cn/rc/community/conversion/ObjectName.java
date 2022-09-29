package cn.rc.community.conversion;

/**
 * 消息的ObjectName常量
 */
public class ObjectName {
    public final static String TEXT_TAG = "RC:TxtMsg";

    public final static String Image_TAG = "RC:ImgMsg";

    public final static String Sight_TAG = "RC:SightMsg";

    public final static String REFERENCE_TAG = "RC:ReferenceMsg";

    public final static String RECALL_TAG = "RC:RcNtf";

    //频道通知消息 1代表加入消息，2代表标记消息，3代表被禁言，4代表解除禁言,5删除标记消息
    public final static String CHANNEL_NOTICE = "RCMic:ChannelNotice";

    //解散社区消息
    public final static String COMMUNITY_DELETE = "RCMic:CommunityDelete";

    //1.修改社区、.修改分组、.修改频道、新增分组、新增频道、删除分组、删除频道 时都会发送消息，端上收到改刷新消息要刷新社区详情。
    public final static String COMMUNITY_CHANGE = "RCMic:CommunityChange";

    //申请加入社区消息、加入社区、退出社区、被踢出
    public final static String COMMUNITY_SYSTEM_NOTICE = "RCMic:CommunitySysNotice";

    //更新用户信息
    public final static String COMMUNITY_UPDATE_USERINFO = "RCMic:UserUpdate";


}
