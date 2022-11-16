package cn.rc.community.helper;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/12
 * @time 19:28
 * 频道通知消息
 */
public interface IUltraGroupChannelListener {

    /**
     * 某条消息被标注
     *
     * @param targetId  社区ID
     * @param channelId 群组ID
     */
    void onAddMarkMessage(String targetId, String channelId);

    /**
     * 某条消息被移除标记
     *
     * @param targetId  社区ID
     * @param channelId 群组ID
     */
    void onRemoveMarkMessage(String targetId, String channelId);

    /**
     * 当有社区用户加入了社区
     * @param targetId
     * @param fromUserId
     * @param toUserId
     * @param hint
     */
    void onUserJoined(String targetId, String fromUserId, String toUserId, String hint);
}
