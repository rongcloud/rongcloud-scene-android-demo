package cn.rongcloud.pk.api;

import android.app.Activity;
import android.view.View;

import cn.rongcloud.pk.bean.PKInviteInfo;
import cn.rongcloud.pk.bean.PKResponse;
import cn.rongcloud.pk.bean.PKState;
import cn.rongcloud.pk.widget.IPK;
import io.rong.imlib.model.Message;

/**
 * @author gyn
 * @date 2022/1/13
 */
public interface IPKManager {

    /**
     * 初始化
     *  @param roomId   房间id
     * @param roomType
     * @param pkView   PK 视图
     * @param listener pk监听
     */
    void init(String roomId,int roomType, IPK pkView, PKListener listener);

    /**
     * 获取pk状态
     *
     * @return
     */
    PKState getPkState();

    /**
     * 反初始化
     */
    void unInit();

    /**
     * 发送pk邀请
     *
     * @param activity Activity
     */
    void showPkInvitation(Activity activity);

    /**
     * 取消邀请
     *
     * @param activity Activity
     */
    void showCancelPkInvitation(Activity activity);

    /**
     * 手动退出pk
     *
     * @param activity Activity
     */
    void showQuitPK(Activity activity);

    /**
     * 刷新pk礼物排行榜信息
     */
    void refreshPKGiftRank();

    /**
     * 消息监听
     *
     * @param message
     */
    void onMessageReceived(Message message);

    /**
     * PK 运行的回调，如果PK连接成功，或者进入正在进行PK的房间均会触发此回调
     *
     * @param pkInviteInfo 返回pk信息
     */
    void onPKBegin(PKInviteInfo pkInviteInfo);

    /**
     * 结束PK时会触发此回调
     */
    void onPKFinish();

    /**
     * 收到邀请PK邀请回调
     *
     * @param inviterRoomId 邀请者的房间id
     * @param inviterUserId 邀请者的用户id
     */
    void onReceivePKInvitation(String inviterRoomId, String inviterUserId);

    /**
     * pk邀请被邀请者取消回调
     * 邀请和被邀请双方都会触发
     *
     * @param inviterRoomId 邀请者的房间id
     * @param inviterUserId 邀请者的用户id
     */
    void onPKInvitationCanceled(String inviterRoomId, String inviterUserId);

    /**
     * PK邀请被受邀请者拒绝回调
     *
     * @param inviteeRoomId 被邀请者的房间id
     * @param inviteeUserId 被邀请者的用户id
     * @param reason        拒绝原因
     */
    void onPKInvitationRejected(String inviteeRoomId, String inviteeUserId, PKResponse reason);

    /**
     * pk 动画进场
     *
     * @param out
     * @param in
     * @param duration
     */
    void enterPkWithAnimation(View out, View in, long duration);

    /**
     * pk 动画退场
     *
     * @param out
     * @param in
     * @param duration
     */
    void quitPkWithAnimation(View out, View in, long duration);

    /**
     * 刷新pk对方的静音状态
     */
    void mutePkView(boolean isMute);

    /**
     * 从服务端重新刷新pk状态
     */
    void refreshPKFromServer();
}
