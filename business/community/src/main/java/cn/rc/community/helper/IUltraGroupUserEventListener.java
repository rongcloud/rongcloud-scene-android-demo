package cn.rc.community.helper;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/12
 * @time 18:24
 * 监听在社区中的行为动作
 */
public interface IUltraGroupUserEventListener {

    /**
     * 被踢出社区
     *
     * @param targetId   社区 ID
     * @param fromUserId 操作人的ID
     * @param hint       踢人的提示
     */
    void onKickOut(String targetId, String fromUserId, String hint);

    /**
     * 加入了某个社区
     *
     * @param targetId   社区 ID
     * @param fromUserId 操作人的ID
     * @param hint       提示
     */
    void onJoined(String targetId, String fromUserId, String hint);

    /**
     * 被拒绝加入了某个社区
     *
     * @param targetId   社区 ID
     * @param fromUserId 操作人的ID
     * @param hint       提示
     */
    void onRejected(String targetId, String fromUserId, String hint);

    /**
     * 离开了某个社区
     *
     * @param targetId   社区ID
     * @param fromUserId 操作人ID
     * @param hint
     */
    void onLeft(String targetId, String fromUserId, String hint);


    /**
     * 申请加入某个社区
     *
     * @param targetId   社区ID
     * @param fromUserId 操作人ID
     * @param hint
     */
    void onRequestJoin(String targetId, String fromUserId, String hint);

    /**
     * 被某个社区禁言
     *
     * @param targetId   社区 ID
     * @param fromUserId 操作人的ID
     * @param toUserId   被操作人的ID
     * @param hint       提示
     */
    void onBeForbidden(String targetId, String fromUserId, String toUserId, String hint);

    /**
     * 被某个社区取消禁言
     *
     * @param targetId   社区 ID
     * @param fromUserId 操作人的ID
     * @param toUserId   被操作人的ID
     * @param hint       提示
     */
    void onCancelForbidden(String targetId, String fromUserId, String toUserId, String hint);


}
