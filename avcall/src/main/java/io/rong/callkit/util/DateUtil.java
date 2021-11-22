package io.rong.callkit.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil extends cn.rong.combusis.common.utils.DateUtil {

    /**
     * 当天，显示时间；hh:mm
     * 早于今日 0 时，不早于昨天 0 时，显示“昨天”
     * 早于昨日 0 时，未超过 7 天，显示星期
     * 超过 7 天显示日期，yy/mm/dd
     *
     * @param lo 毫秒值
     * @return
     */
    public static String getRecordDate(Long lo) {
        Date d = new Date(lo);
        Calendar cal = date2Calendar(d);
        Calendar current = getCurrentCalendar();
        if (cal.get(Calendar.YEAR) == current.get(Calendar.YEAR)) {//同年
            if (cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)) {//当天
                return date2String(d, TimeFt.HCm);
            } else if (Math.abs(cal.get(Calendar.DAY_OF_YEAR) - current.get(Calendar.DAY_OF_YEAR)) == 1) {// 昨天
                return "昨天";
            } else if (Math.abs(cal.get(Calendar.DAY_OF_YEAR) - current.get(Calendar.DAY_OF_YEAR)) < 8) {//七天内
                return getWeekOfDate(d);
            }
        }
        return date2String(d, DateFt.ySMSd);
    }
}