package cn.rongcloud.pk.widget;

import android.view.View;

import java.util.List;

import cn.rongcloud.pk.api.PKListener;

/**
 * PK两种状态 pk阶段、惩罚阶段
 * pk阶段
 * 1、开启pk记时
 * 2、记时结束 ->进入惩罚阶段
 * 惩罚阶段
 * 1、惩罚记时
 * 2、记时结束 -> 结束pk流程
 */
public interface IPK {
    /**
     * 状态重置
     */
    void reset(boolean isRoomOwner);

    /**
     * pk 开始
     *
     * @param localId 当前人的id
     * @param pkId    pk对象的UserId
     */
    void setPKUserInfo(String localId, String pkId);

    void setPKListener(PKListener pkListener);


    void pkStart(long timeDiff, OnTimerEndListener listener);

    /**
     * PK 惩罚
     *
     * @param listener 定时结束回调
     */
    void pkPunish(long timeDiff, OnTimerEndListener listener);

    /**
     * pk 流程结束
     */
    void pkStop();

    /**
     * 设置pk 双方总价值
     *
     * @param left  左侧价值
     * @param right 右侧价值
     */
    void setPKScore(int left, int right);

    /**
     * 获取pk结果
     *
     * @return 1：成功 0：平局 -1：失败
     */
    int getPKResult();

    /**
     * 设置pk 双方礼物赠送者排行榜
     *
     * @param lefts  左侧排行榜
     * @param rights 右侧排行榜
     */
    void setGiftSenderRank(List<String> lefts, List<String> rights);

    /**
     * 定时器结束监听
     */
    interface OnTimerEndListener {
        void onTimerEnd();
    }

    /**
     * 设置静音状态
     *
     * @param isMute
     */
    void setMute(boolean isMute);

    /**
     * 设置pk静音按钮的点击事件
     *
     * @param l
     */
    void setClickMuteListener(View.OnClickListener l);
}
