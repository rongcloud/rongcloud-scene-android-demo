package com.rc.live.helper;


import android.view.View;

import java.util.List;

import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.liveroom.api.RCHolder;
import cn.rongcloud.liveroom.api.RCParamter;
import cn.rongcloud.liveroom.api.interfaces.RCLiveEventListener;
import cn.rongcloud.liveroom.api.interfaces.RCLiveLinkListener;
import cn.rongcloud.liveroom.api.interfaces.RCLiveSeatListener;
import cn.rongcloud.liveroom.api.model.RCLiveSeatInfo;

/**
 * @author lihao
 * @project RongRTCDemo
 * @date 2021/11/16
 * @time 5:30 下午
 */
public interface LiveRoomListener extends RCLiveEventListener, RCLiveLinkListener, RCLiveSeatListener {

    /**
     * 申请上麦的用户
     *
     * @param requestLives
     */
    void onRequestLiveVideoIds(List<String> requestLives);


    /**
     * 可以被邀请的用户
     */
    void onInviteLiveVideoIds(List<User> roomUsers);

    /**
     * 返回View
     *
     * @param seatInfo
     * @param rcParameter
     * @return
     */
    View inflaterSeatView(RCLiveSeatInfo seatInfo, RCParamter rcParameter);


    /**
     * 绑定数据到view上
     */
    void onBindView(RCHolder holder, RCLiveSeatInfo seat, RCParamter parameter);
}
