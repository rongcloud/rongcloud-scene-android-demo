package cn.rc.community.helper;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/19
 * @time 23:19
 * 用户信息更新监听
 */
public interface IUltraGroupUserInfoUpdateListener {

    /**
     * 社区的某个人的个人信息发生了变化
     *
     * @param userId
     * @param userName
     * @param portrait
     */
    void onUpdateUserInfo(String userId, String userName, String portrait);
}
