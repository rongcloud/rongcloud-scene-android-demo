package com.rc.live.room;

import android.content.Context;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

import com.basis.ui.mvp.IBaseView;

import java.util.List;

import cn.rongcloud.config.bean.VoiceRoomBean;
import cn.rongcloud.liveroom.api.model.RCLiveSeatInfo;
import cn.rongcloud.liveroom.weight.RCLiveView;
import cn.rongcloud.roomkit.ui.room.fragment.roomsetting.IFun;
import cn.rongcloud.roomkit.ui.room.model.Member;
import io.rong.imlib.model.MessageContent;

/**
 * 直播房
 */
public interface LiveRoomView extends IBaseView {
    /**
     * 当前直播已结束
     */
    void showFinishView();

    /**
     * 设置房间数据
     */
    void setRoomData(VoiceRoomBean voiceRoomBean);

    /**
     * 设置布局的顶部关注按钮的文字状态
     */
    void setTitleFollow(boolean isFollow);

    /**
     * 添加单条公屏消息
     */
    void addMessageContent(MessageContent messageContent, boolean isReset);

    /**
     * 添加多条公屏消息
     */
    void addMessageList(List<MessageContent> messageContentList, boolean isReset);

    /**
     * 设置喜欢的动画
     */
    void showLikeAnimation();

    /**
     * 当前用户的麦位状态
     */
    void changeStatus();

    /**
     * 当前的连麦状态
     */
    void changeSeatOrder();

    /**
     * 获取fragment管理器
     *
     * @return
     */
    FragmentManager getLiveFragmentManager();

    /**
     * 关闭当前页面
     */
    void finish();

    /**
     * 显示直播view
     */
    void showRCLiveVideoView(RCLiveView rcLiveView);

    /**
     * 显示消息延迟
     *
     * @param delayMs
     */
    void showNetWorkStatus(long delayMs);

    /**
     * 显示在线人数
     *
     * @param onLineCount
     */
    void setOnlineCount(int onLineCount);

    /**
     * 显示房主的礼物数量
     */
    void setCreateUserGift(String giftCount);

    /**
     * 显示发送礼物弹窗
     *
     * @param voiceRoomBean
     * @param selectUserId
     * @param members
     */
    void showSendGiftDialog(VoiceRoomBean voiceRoomBean, String selectUserId, List<Member> members);

    /**
     * 显示音乐弹窗
     */
    void showMusicDialog();

    /**
     * 底部设置弹窗
     *
     * @param funList
     */
    void showRoomSettingFragment(List<MutableLiveData<IFun.BaseFun>> funList);

    /**
     * 获取上下文
     *
     * @return
     */
    Context getLiveActivity();

    /**
     * 设置公告的内容
     *
     * @param notice
     */
    void setNotice(String notice);

    /**
     * 展示申请人数
     */
    void showUnReadRequestNumber(int requestNumber);

    /**
     * 获取连麦视图距离顶部的距离
     *
     * @return
     */
    int getMarginTop();

    /**
     * 显示人员设置弹窗
     *
     * @param userId
     */
    void showMemberSettingFragment(String userId);

    /**
     * 主播点击自己的麦位
     *
     * @param rcLiveSeatInfo
     */
    void showCreatorSettingFragment(RCLiveSeatInfo rcLiveSeatInfo);

    /**
     * 刷新消息集合
     */
    void refreshMessageList();

    /**
     * 切换到其他房间
     *
     * @param roomId
     */
    void switchOtherRoom(String roomId);

    /**
     * 默认连麦模式下，连麦状态中，房主点击弹出挂断连接弹窗
     */
    void showHangUpFragment(String userId);

    /**
     * 展示撤销连麦邀请弹窗
     */
    void showUninviteVideoFragment(String userId);

    /**
     * 动态修改 messageList的高度
     */
    void changeMessageContainerHeight();

    /**
     * 刷新音乐播放小框
     *
     * @param show 是否显示
     * @param name musicName-author
     * @param url  封面链接
     */
    void refreshMusicView(boolean show, String name, String url);
}
