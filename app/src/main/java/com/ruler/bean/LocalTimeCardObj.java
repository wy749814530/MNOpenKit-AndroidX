package com.ruler.bean;

import com.alibaba.fastjson.JSON;
import com.mnopensdk.demo.utils.LogUtil;
import com.mnopensdk.demo.bean.TFStorageBean;
import com.ruler.utils.DateUtils;

/**
 * Created by Administrator on 2018/11/5 0005.
 */

public class LocalTimeCardObj {
    private final static String TAG = "LocalTimeCardObj";

    private static TFStorageBean mTFStorage = new TFStorageBean();
    /**
     * 缓存本地录像
     * @param loaclAlarms
     * @param keyTime
     * @return
     */
    public static TimeCardBean cacheLocalRecording(String loaclAlarms, long keyTime) {
        TimeCardBean mBean = stringToTimeCardBean(loaclAlarms);
        if (mBean == null || mBean.getFound() == 0){
            String key = DateUtils.getDateByCurrentTiem(keyTime);
            mTFStorage.addStorage(key, loaclAlarms);

        }else {
            String key = mBean.getInfo().get(0).getStarttime().trim().split(" ")[0];
            mTFStorage.addStorage(key, loaclAlarms);
        }
        return mBean;
    }

    public static TimeCardBean getDayLocalVideo(long currentTime) {
        String pszStartTime = DateUtils.getDateByCurrentTiem(DateUtils.getTodayStart(currentTime));
        String jsonData = mTFStorage.getStorage(pszStartTime);
        return stringToTimeCardBean(jsonData);
    }

    public static TimeCardBean stringToTimeCardBean(String timeCardInfo) {
        if (timeCardInfo == null || "".equals(timeCardInfo) || !timeCardInfo.contains("found")) {
            LogUtil.i(TAG, "这个时间点没有告警录像");
            return null;
        }

        JSON usersJs = (JSON) JSON.parse(timeCardInfo);
        TimeCardBean userCopy = JSON.toJavaObject(usersJs, TimeCardBean.class);
        if (userCopy != null) {
            return userCopy;
        }
        return null;
    }
}
