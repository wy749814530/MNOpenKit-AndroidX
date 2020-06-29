package com.ruler.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/8/29 0029.
 */

public class RecordJsonBean {
    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static class RecordBean {
        private String recordJson;
        private List<TimeSlot> slots;

        public void setRecordJson(String recordJson) {
            this.recordJson = recordJson;
        }

        public String getRecordJson() {
            return recordJson;
        }

        public void setSlots(List<TimeSlot> slots) {
            this.slots = slots;
        }

        public List<TimeSlot> getSlots() {
            return slots;
        }
    }
}
