package com.ruler.utils;

import com.ruler.bean.TimeSlot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具
 * Created by HDL on 2017/7/25.
 */

public class DateUtils {


    /**
     * 默认的日期格式
     */
    public static String DEFAULT_YOURMMDD = "yyyyMMdd";
    public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static String DEFAULT_DATE_TIME_FORMAT = "yyyyMMddHHmmss";
    public static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String DEFAULT_M_D_FORMAT = "MM-dd HH:mm:ss";
    public static String DEFAUL_MM_SS_FORMAT = "mm:ss";
    public static String DEFAULT_Y_M_D_H_M_FORMAT = "yyyy-MM-dd HH:mm";
    public static String DEFAULT_TIMEHM_FORMAT = "HH:mm";

    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm");

    /**
     * 获取当前时间的起点（00:00:00）
     *
     * @param currentTime
     * @return
     */
    public static long getTodayStart(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        return calendar.getTimeInMillis();
    }

    public static String getTodayStart(String currentTimeStr, String dateFormat) {
        long startTime = convertString2Time(currentTimeStr, dateFormat);
        long todayStart = getTodayStart(startTime);
        return convertDate2String(new Date(todayStart), dateFormat);
    }

    /**
     * 获取当前时间的终点（23:59:59）
     *
     * @param currentTime
     * @return
     */
    public static long getTodayEnd(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static String getTodayEnd(String currentTimeStr, String dateFormat) {
        long defTime = convertString2Time(currentTimeStr, dateFormat);
        long todayEnd = getTodayEnd(defTime);
        return convertDate2String(new Date(todayEnd), dateFormat);
    }

    public static long getDateBeforeStart(int beforeData) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - beforeData);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getDateBeforeEnd(int beforeData) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - beforeData);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTimeInMillis();
    }

    /**
     * 通过时间秒毫秒数判断两个时间跨越了多少天
     *
     * @param currentMill
     * @param dateMill
     * @return
     */
    public static int differentDaysByMillisecond(long currentMill, long dateMill) {
        int days = (int) ((getTodayStart(currentMill) - getTodayStart(dateMill)) / (1000 * 3600 * 24));
        return days;
    }

    /**
     * 获取当前时间的小时值
     *
     * @param currentTime
     * @return
     */
    public static int getHour(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前时间的分钟值
     *
     * @param currentTime
     * @return
     */
    public static int getMinute(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取当前时间的分钟值
     *
     * @param currentTime
     * @return
     */
    public static int getSecond(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 获取当前时间的分钟值+毫秒值
     *
     * @param currentTime
     * @return
     */
    public static int getMinuteMillisecond(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        return calendar.get(Calendar.MINUTE) * 60 * 1000 + calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND);
    }

    /**
     * 获取当前时间的毫秒值
     *
     * @param currentTime
     * @return
     */
    public static int getMillisecond(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        return calendar.get(Calendar.MILLISECOND);
    }

    /**
     * 获取指定时间的年月日
     *
     * @param currentTime
     * @return
     */
    public static String getDateByCurrentTiem(long currentTime) {
        return new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
    }

    /**
     * 获取指定时间的年月日
     *
     * @param currentTime
     * @return
     */
    public static String getDateTime(long currentTime) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
    }

    /**
     * 根据下标获取HH:mm格式的时间
     *
     * @param timeIndex
     * @return
     */
    public static String getHourMinute(int timeIndex) {
        return format.format(timeIndex * 60 * 1000 + 16 * 60 * 60 * 1000);
    }

    /**
     * 获取指定日期的时间（如：10:11:12）
     *
     * @param currentTime
     * @return
     */
    public static String getTime(long currentTime) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date(currentTime));
    }

    /**
     * 根据当前的秒数计算时间
     *
     * @param currentSecond
     * @return
     */
    public static String getTimeByCurrentSecond(int currentSecond) {
        currentSecond = currentSecond / 60;
        int minute = currentSecond % 60;
        int hour = currentSecond / 60;
        if (hour >= 24) {
            hour = hour % 24;
        }
        return String.format("%02d:%02d", hour, minute);
    }

    /**
     * 根据当前的秒数计算时间
     *
     * @param currentSecond
     * @return
     */
    public static String getTimeByCurrentHours(int currentSecond) {
        currentSecond = currentSecond * 10;
        currentSecond = currentSecond / 60;
        int minute = currentSecond % 60;
        int hour = currentSecond / 60;
        if (hour >= 24) {
            hour = hour % 24;
        }
        return String.format("%02d:%02d", hour, minute);
    }

    public static long convertString2Time(String stringDate, String format) {
        try {
            //java.text.ParseException: Unparseable date: "yyyy-MM-dd HH:mm:ss" (at offset 0)
            Date dateFormat = getDateFormat(format.toString()).parse(stringDate.trim().toString());
            if (dateFormat != null) {
                return dateFormat.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 将字符串转换成日期
     *
     * @param stringDate - 要转换的字符串格式的日期
     * @param dateFormat - 要转换的字符串对应的日期格式
     * @return Date 字符串对应的日期
     */
    private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();
    private static String currentFormat = "";

    public static SimpleDateFormat getDateFormat(String format) {
        if (!currentFormat.equals(format)) {
            threadLocal.remove();
            currentFormat = format;
        }

        SimpleDateFormat df = threadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(format);
            threadLocal.set(df);
        }
        return df;
    }


    /**
     * 将日期转换成指定格式的字符串
     *
     * @param date       - 要转换的日期
     * @param dateFormat - 日期格式
     * @return String 日期对应的字符串
     */
    public static String convertDate2String(Date date, String dateFormat) {
        SimpleDateFormat sdf;
        if (dateFormat != null && !dateFormat.equals("")) {
            try {
                sdf = new SimpleDateFormat(dateFormat);
            } catch (Exception e) {
                sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            }
        } else {
            sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        }
        return sdf.format(date);
    }

    /**
     * 将时间从毫秒数转化为字符串格式
     *
     * @param time
     * @return String 日期时间的字符串形式
     */
    public static String getStringDateByLong(long time) {
        if (time == 0) {
            return "";
        }
        return convertDate2String(new Date(time), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getStringDateByLong(long time, String dateFormat) {
        if (time == 0)
            return "";
        return convertDate2String(new Date(time), dateFormat);
    }


    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean isCurrentTimeArea(long nowTime, long beginTime, long endTime) {
        return nowTime >= beginTime && nowTime <= endTime;
    }

    /**
     * 判断指定时间段是否包含在某个时间段
     *
     * @return
     */
    public static boolean isContainTime(TimeSlot timeSlot, long beginTime, long endTime) {
        return beginTime >= timeSlot.getStartTimeMillis() && endTime <= timeSlot.getEndTimeMillis();
    }

}
