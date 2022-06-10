package cn.rc.community.message.sysmsg;

public enum ChannelType {
    none(0),
    /**
     * 1: 加入频道
     */
    joined(1),

    /**
     * 2: 标记消息
     */
    marked(2),

    /**
     * 3: 禁言
     */
    disabled(3),

    /**
     * 4: 解除禁言
     */
    enabled(4),

    /**
     * 被移除的标记消息
     */
    removedMarked(5);

    private final int type;

    ChannelType(int v) {
        this.type = v;
    }

    public int getType() {
        return type;
    }

    public static ChannelType typeOf(int type) {
        if (1 == type) {
            return joined;
        } else if (2 == type) {
            return marked;
        } else if (3 == type) {
            return disabled;
        } else if (4 == type) {
            return enabled;
        } else if (5 == type) {
            return removedMarked;
        } else {
            return none;
        }
    }

}