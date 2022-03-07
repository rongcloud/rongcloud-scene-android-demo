package cn.rongcloud.pk.bean;

import com.basis.utils.KToast;

/**
 * @author gyn
 * @date 2022/1/13
 */
public enum PKState {
    PK_NONE,//默认状态
    PK_INVITE,//邀请状态
    // pk 中
    PK_GOING,//sdk pk进行中
    PK_START,//ui pk进行中
    PK_PUNISH,//ui pk惩罚
    // pk 结束
    PK_STOP,//ui pk结束
    PK_FINISH;//pk关闭状态

    /**
     * 是否在PK过程中
     *
     * @return
     */
    public boolean isInPk() {
        return this == PK_GOING || this == PK_START || this == PK_PUNISH;
    }

    /**
     * 不在PK过程中
     *
     * @return
     */
    public boolean isNotInPk() {
        return this == PK_NONE ||
                this == PK_FINISH ||
                this == PK_STOP;
    }

    /**
     * 是否在邀请中
     *
     * @return
     */
    public boolean isInInviting() {
        return this == PK_INVITE;
    }

    /**
     * 判断是否可以发起邀请
     *
     * @return 否可以发起邀请
     */
    public boolean enableInvite() {
        if (isInInviting()) {
            KToast.show("您已发出邀请，请耐心等待对方处理");
            return false;
        }
        if (isInPk()) {
            KToast.show("您当前正在PK中");
            return false;
        }
        return true;
    }

    /**
     * 判断是否可以取消邀请
     *
     * @return 是否可以取消
     */
    public boolean enableCancelInvite() {
        if (PK_INVITE != this) {
            KToast.show("你还未发出PK邀请");
            return false;
        }
        return true;
    }

    /**
     * 不在PK中可以做下一步操作
     *
     * @return
     */
    public boolean enableAction() {
        boolean inPk = isInPk();
        if (inPk) {
            KToast.show("当前PK中，无法进行该操作");
        }
        return !inPk;
    }
}
