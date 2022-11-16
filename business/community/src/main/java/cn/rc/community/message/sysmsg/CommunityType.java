package cn.rc.community.message.sysmsg;

public enum CommunityType {
    /**
     * 0: 代表申请加入社区
     */
    request(0),

    /**
     * 1: 代表加入社区后通知消息
     */
    joined(1),

    /**
     * 2: 代表退出社区的通知消息
     */
    left(2),
    /**
     * 3: 被踢出社区的通知消息
     */
    kick(3),

    /**
     * 4: 禁言
     */
    disabled(4),

    /**
     * 5: 解除禁言
     */
    enabled(5),

    /**
     * 6: 被拒绝
     */
    rejected(6),
    /**
     * 7: 社区被删除
     */
    deleted(7);

    private final int type;

    CommunityType(int v) {
        this.type = v;
    }

    public int getType() {
        return type;
    }

    public static CommunityType typeOf(int type) {
        if (0 == type) {
            return request;
        } else if (1 == type) {
            return joined;
        } else if (2 == type) {
            return left;
        } else if (3 == type) {
            return kick;
        } else if (4 == type) {
            return disabled;
        } else if (5 == type) {
            return enabled;
        } else if (6 == type) {
            return rejected;
        } else if (7 == type) {
            return deleted;
        } else {
            return joined;
        }
    }

}