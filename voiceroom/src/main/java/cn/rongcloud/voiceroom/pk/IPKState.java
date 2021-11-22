//package cn.rongcloud.voiceroom.pk;
//
//import android.app.Activity;
//import android.view.View;
//
//import com.kit.wapper.IResultBack;
//
//import cn.rongcloud.voiceroom.pk.widget.IPK;
//
//public interface IPKState {
//    /**
//     * 初始化
//     *
//     * @param roomId   房间id
//     * @param pkView   PK 视图
//     * @param listener pk监听
//     */
//    void init(String roomId, IPK pkView, VRStateListener listener);
//
//    /**
//     * 反初始化
//     */
//    void unInit();
//
//    /**
//     * 发送pk邀请
//     *
//     * @param activity   Activity
//     * @param resultBack 邀请成功回调
//     */
//    void sendPkInvitation(Activity activity, IResultBack<Boolean> resultBack);
//
//    /**
//     * 取消邀请
//     *
//     * @param activity   Activity
//     * @param resultBack 取消邀请会状态回调
//     */
//    void cancelPkInvitation(Activity activity, IResultBack<Boolean> resultBack);
//
//    /**
//     * 手动退出pk
//     *
//     * @param activity Activity
//     */
//    void quitPK(Activity activity);
//
//    /**
//     * 刷新pk礼物排行榜信息
//     */
//    void refreshPKGiftRank();
//
//    /**
//     * pk 动画进场
//     *
//     * @param left
//     * @param in
//     * @param duration
//     */
//    void enterPkWithAnimation(View left, View in, long duration);
//
//    /**
//     * pk 动画退场
//     *
//     * @param left
//     * @param in
//     * @param duration
//     */
//    void quitPkWithAnimation(View left, View in, long duration);
//
//    interface VRStateListener {
//        /**
//         * pk开始
//         */
//        void onPkStart();
//
//        /**
//         * pk结束：1、惩罚记时结束，2、手动退出PK
//         */
//        void onPkStop();
//
//        /**
//         * pk状态变化
//         */
//        void onPkState();
//
//        void onSendPKMessage(String content);
//    }
//}
