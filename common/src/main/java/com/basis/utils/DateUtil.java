package com.basis.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: BaiCQ
 * @ClassName: DateUtil
 * @Description: 日期转换工具类
 */
public class DateUtil {
    public final static String TAG = "DateUtil";
    public final static String DEF_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
    public final static long N_DAY = 1000 * 24 * 60 * 60;
    public final static long N_HOUR = 1000 * 60 * 60;
    public final static long N_MIN = 1000 * 60;
    public final static long N_SECOND = 1000;

    /**
     * 格式拼接
     *
     * @param dft
     * @param tft
     * @return
     */
    public static String format(DateFt dft, TimeFt tft) {
        if (null == dft && null == tft) {
            return DEF_FORMAT;
        }
        if (null == dft) {
            return tft.getValue();
        }
        if (null == tft) {
            return dft.getValue();
        }
        return dft.getValue() + " " + tft.getValue();
    }

    public static String date2String(Date date, TimeFt tft) {
        return date2String(date, format(null, tft));
    }

    public static String date2String(Date date, DateFt dft) {
        return date2String(date, format(dft, null));
    }

    public static String date2String(Date date, DateFt dft, TimeFt tFt) {
        return date2String(date, format(dft, tFt));
    }

    /**
     * 日期格式化输出
     *
     * @param date   日期对象
     * @param format 格式
     * @return
     */
    public static String date2String(Date date, String format) {
        String resultTimeStr = "";
        if (date != null) {
            try {
                SimpleDateFormat formatPattern = new SimpleDateFormat(format);
                resultTimeStr = formatPattern.format(date);
            } catch (Exception e) {
                Logger.e(TAG, "date2String error:e = " + e.toString());
            }
        }
        return resultTimeStr;
    }

    public static Date string2Date(String dateStr, DateFt dft) {
        return string2Date(dateStr, format(dft, null));
    }

    public static Date string2Date(String dateStr, TimeFt tFt) {
        return string2Date(dateStr, format(null, tFt));
    }

    public static Date string2Date(String dateStr, DateFt dft, TimeFt tFt) {
        return string2Date(dateStr, format(dft, tFt));
    }

    /**
     * 日期格式的字符串 解析成日期对象
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static Date string2Date(String dateStr, String format) {
        Date date = null;
        if (!TextUtils.isEmpty(dateStr)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                date = sdf.parse(dateStr);
            } catch (Exception e) {
            }
        }
        return date;
    }

    public static Calendar date2Calendar(Date date) {
        if (null == date) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Date calendar2Date(Calendar calendar) {
        if (null == calendar) return null;
        return calendar.getTime();
    }

    public static boolean sameDay(Calendar c1, Calendar c2) {
        if (c1 != null && c2 != null) {
            return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                    && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                    && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
        }
        return false;
    }

    public static boolean sameYear(Calendar c1, Calendar c2) {
        if (c1 != null && c2 != null) {
            return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
        }
        return false;
    }

    public static boolean sameMonth(Calendar c1, Calendar c2) {
        if (c1 != null && c2 != null) {
            return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                    && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH));
        }
        return false;
    }

    public static Calendar getCurrentCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    public static Date formatDate(int year, int month, int day, int hour, int min, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }

    public static Date startOfTodDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date endOfTodDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取指定日期所在月份的起始和结束时间：2018-09-08 16:40:00 获取到的结果：["2018-09-01 00:00:00"，"2018-09-30 23:59:59"]
     *
     * @param date 指定日期
     * @return 起始时间点的数组
     */
    public static Date[] getMonthStartAndEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //定义数组用于存放起始时间[0]和结束时间[1]
        Date[] startAndEndDate = new Date[2];
        //设置当月的起始时间
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                1,
                0,
                0,
                0);
        startAndEndDate[0] = calendar.getTime();
        //设置当月结束天为当月的最大天，如：9月份最大天为30，此时设置天为30
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        //设置结束时间
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE),
                23,
                59,
                59);//设置当月的结束时间
        startAndEndDate[1] = calendar.getTime();//存放到数组中
        return startAndEndDate;
    }

    /**
     * call 记录的日期格式
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
                return "今天 " + date2String(d, TimeFt.HCm);
            } else if (Math.abs(cal.get(Calendar.DAY_OF_YEAR) - current.get(Calendar.DAY_OF_YEAR)) == 1) {// 昨天
                return "昨天 " + date2String(d, TimeFt.HCm);
            } else if (Math.abs(cal.get(Calendar.DAY_OF_YEAR) - current.get(Calendar.DAY_OF_YEAR)) < 8) {//七天内
                return getWeekOfDate(d) + " " + date2String(d, TimeFt.HCm);
            }
        }
        return date2String(d, DateFt.ySMSd);
    }

    public final static String[] WEEK_DAY = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public static String getWeekOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) w = 0;
        return WEEK_DAY[w];
    }
}
