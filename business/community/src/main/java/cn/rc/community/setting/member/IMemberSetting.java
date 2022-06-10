package cn.rc.community.setting.member;

import cn.rc.community.bean.MemberBean;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/4/8
 * @time 4:50 下午
 * 成员列表操作接口
 */
public interface IMemberSetting {

    /**
     * 变更身份组
     *
     * @param member
     */
    void ExchangedGroup(MemberBean.RecordsBean member);

    /**
     * 修改社区昵称
     *
     * @param member
     */
    void ChangeNickName(MemberBean.RecordsBean member);

    /**
     * 禁止/取消发言
     *
     * @param member
     */
    void ChangeSpeakStatus(MemberBean.RecordsBean member, String shutUp);

    /**
     * 永久封禁
     *
     * @param member
     */
    void Blocked(MemberBean.RecordsBean member);

    /**
     * 踢出
     *
     * @param member
     */
    void KickOut(MemberBean.RecordsBean member);

}
