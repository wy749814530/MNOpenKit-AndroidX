package com.ruler.utils;

/**
 * Created by WIN on 2018/6/19.
 * 刻度与时间对应关系帮助类
 */

public class TimeScaleData {


    public static void initScaleData() {
        for (int i = -360; i < 1800; i = i + 60) {
            String hours = "00:00";
            if (i < 0) {
                hours = getLargeHourByTime(24 + i / 60);
            } else if (i == 0 || i == 1440) {
                hours = getLargeHourByTime(0);
            } else if (i > 1440) {
                hours = getLargeHourByTime(i / 60 - 24);
            } else {
                hours = getLargeHourByTime(i / 60);
            }
        }

        for (int i = -60; i < 1560; i = i + 10) {
            String hourMinutes = "00:00";
            if (i < 0) {
                hourMinutes = getSmallHourStr(i);
            } else if (i == 0 || i == 1440) {
                hourMinutes = getSmallHourStr(0);
            } else if (i > 1440) {
                hourMinutes = getSmallHourStr(i - 1440);
            } else {
                hourMinutes = getSmallHourStr(i);
            }
        }
    }

    public static String getLargeHourByIndex(int index){
        String hours = "00:00";
        if (index < 0) {
            hours = getLargeHourByTime(24 + index / 60);
        } else if (index == 0 || index == 1440) {
            hours = getLargeHourByTime(0);
        } else if (index > 1440) {
            hours = getLargeHourByTime(index / 60 - 24);
        } else {
            hours = getLargeHourByTime(index / 60);
        }
        return hours;
    }

    public static String getSmallHourByIndex(int index){
        String hourMinutes = "00:00";
        if (index < 0) {
            hourMinutes = getSmallHourStr(index);
        } else if (index == 0 || index == 1440) {
            hourMinutes = getSmallHourStr(0);
        } else if (index > 1440) {
            hourMinutes = getSmallHourStr(index - 1440);
        } else {
            hourMinutes = getSmallHourStr(index);
        }
        return hourMinutes;
    }

    public static String getLargeHourByTime(int time) {
        return String.format("%02d:00",time);
    }

    public static String getSmallHourStr(int time) {
        if (time < 0) {
            return time == -60 ? "23:00" : "23:" + String.format("%02d",(60 + time));
        } else if (time == 0) {
            return "00:00";
        } else if (time < 1440) {
            int hour = time / 60;
            int minute = time % 60;
            return String.format("%02d:%02d",hour, minute);
        }
        return "00:00";
    }
}
