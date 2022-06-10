package cn.rc.community.dialog;

import cn.rc.community.conversion.controller.WrapperMessage;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/3/14
 * @time 5:56 下午
 * 消息操作相关的接口
 */
public interface IMessageOperation {

    /**
     * 编辑
     *
     * @param iMessage
     */
    void edit(WrapperMessage iMessage);

    /**
     * 引用
     */
    void quote(WrapperMessage iMessage);

    /**
     * 标注
     */
    void annotation(WrapperMessage iMessage);

    /**
     * 取消标注
     */
    void cancelAnnotation(WrapperMessage iMessage);

    /**
     * 复制
     */
    void copy(WrapperMessage iMessage);

    /**
     * 删除
     *
     * @param iMessage
     */
    void delete(WrapperMessage iMessage);

    /**
     * 撤销
     *
     * @param iMessage
     */
    void reCall(WrapperMessage iMessage);
}
