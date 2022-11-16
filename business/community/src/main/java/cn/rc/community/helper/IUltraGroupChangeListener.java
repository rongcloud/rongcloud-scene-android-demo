package cn.rc.community.helper;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/12
 * @time 19:56
 */
public interface IUltraGroupChangeListener {

    /**
     * 社区发生了改变
     *
     * @param targetId 社区ID
     */
    void onUltraGroupChanged(String targetId);

    /**
     * 频道被删除
     */
    void onChannelDeleted(String[] channelIds);

    /**
     * 社区被解散了
     *
     * @param targetId 社区ID
     */
    void onUltraGroupDelete(String targetId);
}
