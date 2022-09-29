package cn.rc.community.channel.TextChannel;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/26
 * @time 11:55
 * 文字频道 P层
 */
public interface ITextChannelPresent {

    /**
     * 获取当前频道未读消息数量
     */
    void getChannelUnreadCount();

}
