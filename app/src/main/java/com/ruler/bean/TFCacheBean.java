package com.ruler.bean;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/18 0018.
 */

public class TFCacheBean {

    private Map<String, TimeCardBean> storageMap = new HashMap<>();

    public TimeCardBean addStorage(String devKey, String value) {
        TimeCardBean timeCard = stringToTimeCardBean(value);
        if (timeCard == null || timeCard.getFound() == 0) {
            return null;
        }
        String timeCardKey = timeCard.getInfo().get(0).getStarttime().trim().split(" ")[0];
        int sameKeyCount = 0;
        for (int i = 1; i < timeCard.getInfo().size(); i++) {
            String currentKey = timeCard.getInfo().get(i).getStarttime().trim().split(" ")[0];
            if (currentKey.equals(timeCardKey)) {
                sameKeyCount++;
            } else {
                sameKeyCount = 0;
                timeCardKey = currentKey;
            }
            if (sameKeyCount >= 3){
                break;
            }
        }

        timeCardKey = devKey + timeCardKey;
        storageMap.put(timeCardKey, timeCard);
        return timeCard;
    }

    public TimeCardBean getStorage(String devKey, String key) {
        key = devKey + key;
        if (!storageMap.containsKey(key)) {
            return null;
        }

        TimeCardBean cardBean = storageMap.get(key);
        return cardBean;
    }

    public TimeCardBean stringToTimeCardBean(String timeCardInfo) {
        if (timeCardInfo == null || !timeCardInfo.contains("found")) {
            return null;
        }
        TimeCardBean userCopy;
        try {
            JSON usersJs = (JSON) JSON.parse(timeCardInfo);
            userCopy = JSON.toJavaObject(usersJs, TimeCardBean.class);
        } catch (Exception e) {
            userCopy = null;
            e.printStackTrace();
        }
        if (userCopy != null) {
            return userCopy;
        }
        return null;
    }
}
