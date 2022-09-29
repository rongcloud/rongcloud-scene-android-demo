package cn.rongcloud.roomkit.ui.room.fragment.roomsetting;

/**
 * @author lihao
 * @project RC RTC
 * @date 2022/5/9
 * @time 18:14
 */
public class RoomFunIdUitls {

    public static String convert(IFun fun) {
        Class<? extends IFun> aClass = fun.getClass();
        if (RoomLockFun.class.equals(aClass)) {
            return "1";
        } else if (RoomPauseFun.class.equals(aClass)) {
            return "2";
        } else if (RoomNameFun.class.equals(aClass)) {
            return "3";
        } else if (RoomNoticeFun.class.equals(aClass)) {
            return "4";
        } else if (RoomBackgroundFun.class.equals(aClass)) {
            return "5";
        } else if (RoomMusicFun.class.equals(aClass)) {
            return "6";
        } else if (RoomShieldFun.class.equals(aClass)) {
            return "7";
        } else if (RoomMuteAllFun.class.equals(aClass)) {
            return "8";
        } else if (RoomLockAllSeatFun.class.equals(aClass)) {
            return "9";
        } else if (RoomSeatModeFun.class.equals(aClass)) {
            return "10";
        } else if (RoomSeatSizeFun.class.equals(aClass)) {
            return "11";
        } else if (RoomMuteFun.class.equals(aClass)) {
            return "12";
        } else if (RoomVideoSetFun.class.equals(aClass)) {
            return "13";
        } else if (RoomOverTurnFun.class.equals(aClass)) {
            return "14";
        } else if (RoomBeautyFun.class.equals(aClass)) {
            return "15";
        } else if (RoomTagsFun.class.equals(aClass)) {
            return "16";
        } else if (RoomBeautyMakeUpFun.class.equals(aClass)) {
            return "17";
        } else if (RoomSpecialEffectsFun.class.equals(aClass)) {
            return "18";
        }
        return "";
    }
}
