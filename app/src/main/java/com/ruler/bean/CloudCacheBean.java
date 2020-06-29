package com.ruler.bean;


import com.mn.bean.restfull.AlarmsBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2019/3/5 0005.
 */

public class CloudCacheBean implements Serializable {
    private int size;
    private List<TimeSlot> timeSlots;
    private List<AlarmsBean> alarmsBeans;

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setAlarmsBeans(List<AlarmsBean> alarmsBeans) {
        this.alarmsBeans = alarmsBeans;
    }

    public List<AlarmsBean> getAlarmsBeans() {
        return alarmsBeans;
    }
}
