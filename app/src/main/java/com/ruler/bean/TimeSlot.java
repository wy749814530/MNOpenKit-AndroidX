package com.ruler.bean;


import com.ruler.utils.DateUtils;


/**
 * 时间段
 * Created by HDL on 2017/9/4.
 */

public class TimeSlot {
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 报警类型
     */
    private String id;
    private int type;
    private int subType;
    private int index;
    private String videoUrl;
    private int number;
    private String sn;
    private boolean is24Record;
    /**
     * 当前天数的开始时间（凌晨00:00:00）毫秒值
     */
    private long currentDayStartTimeMillis;

    public TimeSlot() {
    }

    public TimeSlot(long currentDayStartTimeMillis, long startTime, long endTime, int type) {
        this.currentDayStartTimeMillis = currentDayStartTimeMillis;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    /**
     * 获取开始时间.
     * 当天持续秒数---->减去了当前开始时间的毫秒值（eg  00:01:00---->60）
     *
     * @return
     */
    public long getStartTimeMillis() {
        if (currentDayStartTimeMillis > startTime) {
            return 0;
        }
        return (startTime - DateUtils.getTodayStart(startTime));
    }

    /**
     * 获取结束时间
     * 当天持续秒数---->减去了当前开始时间的毫秒值（eg  00:01:00---->60）
     *
     * @return
     */
    public long getEndTimeMillis() {
        if (currentDayStartTimeMillis + 24 * 60 * 60 * 1000 <= endTime) {
            return (24 * 60 * 60 - 1) * 1000;
        }
        return (endTime - DateUtils.getTodayStart(endTime));
    }

    public void setCurrentDayStartTimeMillis(long currentDayStartTimeMillis) {
        this.currentDayStartTimeMillis = currentDayStartTimeMillis;
    }

    public long getCurrentDayStartTimeMillis() {
        return currentDayStartTimeMillis;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "startTime=" + DateUtils.getStringDateByLong(startTime, DateUtils.DEFAULT_FORMAT) + ",endTime=" + DateUtils.getStringDateByLong(endTime, DateUtils.DEFAULT_FORMAT) +
                ", id =" + getId() + ",type=" + getType() +
                ", subType =" + getSubType() +
                '}';
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public int getSubType() {
        return subType;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSn() {
        return sn;
    }

    public void setIs24Record(boolean is24Record) {
        this.is24Record = is24Record;
    }

    public boolean isIs24Record() {
        return is24Record;
    }
}
