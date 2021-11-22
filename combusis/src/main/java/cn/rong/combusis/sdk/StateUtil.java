//package cn.rong.combusis.sdk;
//
//import com.kit.utils.KToast;
//import com.kit.utils.Logger;
//
//import cn.rong.combusis.sdk.event.EventHelper;
//import cn.rong.combusis.sdk.event.wrapper.IEventHelp;
//
//public class StateUtil {
//
//    /**
//     * 判断是否可以发起邀请
//     *
//     * @return 否可以发起邀请
//     */
//    public static boolean enableInvite() {
//        IEventHelp.Type type = EventHelper.helper().getPKState();
//        if (IEventHelp.Type.PK_INVITE == type) {
//            KToast.show("您已发出邀请，请耐心等待对方处理");
//            return false;
//        }
//        if (IEventHelp.Type.PK_GOING == type
//                || IEventHelp.Type.PK_PUNISH == type
//                || IEventHelp.Type.PK_START == type) {
////                || IEventHelp.Type.PK_STOP == type) {
//            KToast.show("您当前正在PK中");
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 判断是否可以取消邀请
//     *
//     * @return 是否可以取消
//     */
//    public static boolean enableCancelInvite() {
//        IEventHelp.Type type = EventHelper.helper().getPKState();
//        if (IEventHelp.Type.PK_INVITE != type) {
//            KToast.show("你还未发出PK邀请");
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * @return 是否正在pk中
//     */
//    public static boolean isPking() {
//        IEventHelp.Type type = EventHelper.helper().getPKState();
//        Logger.e("StateUtil", "isPking: type = " + type);
//        return type == IEventHelp.Type.PK_GOING ||
//                type == IEventHelp.Type.PK_START ||
//                type == IEventHelp.Type.PK_PUNISH;
//    }
//}
