package com.ruler.utils;


import com.ruler.bean.TimeSlot;

import java.util.Comparator;

/**
 * Created by Administrator on 2018/12/26 0026.
 */

public class LocalRecordComparator implements Comparator<TimeSlot> {
    @Override
    public int compare(TimeSlot alarm1, TimeSlot alarm2) {
        return alarm2.getStartTime() >= alarm1.getStartTime() ? -1 : 1;
    }
}
