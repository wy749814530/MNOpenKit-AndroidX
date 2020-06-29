package com.ruler.bean;

/**
 * Created by Administrator on 2018/12/21 0021.
 */

public class TimeSectionBean {
    private long startTime;
    private long endTime;

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }
}
