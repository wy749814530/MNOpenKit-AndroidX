package com.mnopensdk.demo.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by WIN on 2017/10/20.
 */

public class LocalVariable {
    public static String mStartYMD = "2017-09-01";
    public static String mStartHMS = "00:00:00";
    public static String mEndYMD = "2017-09-28";
    public static String mEndHMS = "00:00:00";


    public static void resetSearchTime() {
        String startYMD = getTime(getDateBefore(new Date(), 3));
        String endYMD = getTime(new Date());
        String endHMS = getTimeHM(new Date());
//		String startYMD = EventSearchActivity.getTime(getDateBefore(new Date(), 0));
        mStartYMD = startYMD;
        mStartHMS = "00:00:00";
        mEndYMD = endYMD;
        mEndHMS = endHMS;
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getTimeHM(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    // 获取几天前时间
    public static long getTodayNightDate() {
        String ymd = getTime(new Date());
        String timeStr = ymd + " 23:59:59";
        Log.i("LocalVariable", "getTodayNightDate timeStr : " + timeStr);
        return DateUtil.parseTime2Long(timeStr);
    }

    // 获取几天前时间
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        String ymd = getTime(now.getTime());
        String timeStr = ymd + " 00:00:01";
        Log.i("LocalVariable", "getDateBefore timeStr : " + timeStr);
        return String2Date(timeStr);
    }

    // 获取几天前23.59.59时间
    public static Date getDateBeforeNight(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        String ymd = getTime(now.getTime());
        String timeStr = ymd + " 23:59:59";
        Log.i("LocalVariable", "getDateBeforesd timeStr : " + timeStr);
        return String2Date(timeStr);
    }

    // 获取几天后时间
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    public static Date String2Date(String dataStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(dataStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
