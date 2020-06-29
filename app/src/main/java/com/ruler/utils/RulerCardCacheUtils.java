package com.ruler.utils;

import com.mnopensdk.demo.utils.DateUtil;
import com.mnopensdk.demo.utils.LogUtil;
import com.ruler.bean.LocalTimeCardObj;
import com.ruler.bean.TimeCardBean;
import com.ruler.bean.TimeSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/12/18 0018.
 */

public class RulerCardCacheUtils {
    private String TAG = RulerCardCacheUtils.class.getSimpleName();
    private List<TimeSlot> mCurrentDirSlot = new ArrayList<>();

    public RulerCardCacheUtils(OnRulerCacheUtilsLinstener listener) {
        mListener = listener;
    }

    public void clear() {
        mCurrentDirSlot.clear();
        mListener = null;
    }

    public void setMoreIvVisible(long currentTime) {
        if (mListener == null) return;
        if (mCurrentDirSlot != null && mCurrentDirSlot.size() > 0) {
            TimeSlot infoBeanLeft = mCurrentDirSlot.get(0);
            TimeSlot infoBeanRight = mCurrentDirSlot.get(mCurrentDirSlot.size() - 1);

            long leftDateTime = infoBeanLeft.getStartTime();
            long rightDateTime = infoBeanRight.getEndTime();

            if (leftDateTime < currentTime) {
                mListener.setRulerDataMoreIcon(1, true);
            } else {
                mListener.setRulerDataMoreIcon(1, false);
            }
            if (rightDateTime > currentTime) {
                mListener.setRulerDataMoreIcon(3, true);
            } else {
                mListener.setRulerDataMoreIcon(3, false);
            }
        } else {
            mListener.setRulerDataMoreIcon(2, false);
        }
    }


    private int currentSelectIndex = 0;

    public void playLocalVideo(long currentTime) {
        setMoreIvVisible(currentTime);
        if (mCurrentDirSlot == null || mCurrentDirSlot.size() == 0) {
            if (mListener != null) {
                mListener.isLastCardPlayFinished();
                return;
            }
        }
        TimeSlot lDirInfo = null;
        for (int i = 0; i < mCurrentDirSlot.size(); i++) {
            currentSelectIndex = i;
            TimeSlot dirInfo = mCurrentDirSlot.get(i);
            long start = dirInfo.getStartTime();
            long end = dirInfo.getEndTime();
            if (currentTime >= start && currentTime < end) { // 刻线在视频段中间
                lDirInfo = dirInfo;
                String alarmTime = DateUtil.getDateFormat(DateUtil.DEFAULT_FORMAT).format(currentTime); // 从当前刻线开始播放
                String endTime = DateUtil.getDateFormat(DateUtil.DEFAULT_FORMAT).format(mCurrentDirSlot.get(currentSelectIndex).getEndTime());
                if (mListener != null) {
                    mListener.onPlayTFCard(alarmTime, endTime, dirInfo.getIndex(), dirInfo);
                }
                return;
            } else if (start > currentTime) { // 当前刻度在视频段的前边,并且刻度线距离视频开始位置小于1分钟
                lDirInfo = dirInfo;
                String alarmTime = DateUtil.getDateFormat(DateUtil.DEFAULT_FORMAT).format(start); // 从当前刻线所在报警的头开始播放
                String endTime = DateUtil.getDateFormat(DateUtil.DEFAULT_FORMAT).format(mCurrentDirSlot.get(currentSelectIndex).getEndTime());
                if (mListener != null) {
                    mListener.onPlayTFCard(alarmTime, endTime, dirInfo.getIndex(), dirInfo);
                }
                return;
            }
        }

        if (mListener != null && lDirInfo == null) {
            mListener.isLastCardPlayFinished();
        }
    }


    private OnRulerCacheUtilsLinstener mListener;

    public void setLocalRecord(List<TimeSlot> localRecord) {
        mCurrentDirSlot.clear();
        if (localRecord != null) {
            mCurrentDirSlot.addAll(localRecord);
        }
    }

    public List<TimeSlot> covertTimeSlots(String recordJson) {
        List<TimeSlot> localDataSlot = new ArrayList<>();
        TimeCardBean userCopy = LocalTimeCardObj.stringToTimeCardBean(recordJson);
        if (userCopy == null || userCopy.getFound() == 0) {
            LogUtil.i(TAG, "covertTimeSlots userCopy is null");
            return localDataSlot;
        }

        for (TimeCardBean.InfoBean infoBean : userCopy.getInfo()) {
            String startT = infoBean.getStarttime();
            String endT = infoBean.getEndtime();
            int type = infoBean.getVideotype();
            /**
             * -1 普通录像
             *  1 来电报警
             *  3 人脸识别
             *  8 动检
             */
            if (startT != null && endT != null && !startT.equals(endT) && endT.compareTo(startT) > 0) {
                Date startDate = DateUtil.convertString2Date(startT, "yyyy-MM-dd HH:mm:ss");
                Date endDate = DateUtil.convertString2Date(endT, "yyyy-MM-dd HH:mm:ss");
                if ((endDate.getTime() - startDate.getTime()) > 24 * 60 * 60 * 1000) {
                    continue;
                }
                TimeSlot slot = new TimeSlot();
                slot.setType(type);
                slot.setEndTime(endDate.getTime());
                slot.setStartTime(startDate.getTime());
                slot.setCurrentDayStartTimeMillis(DateUtils.getTodayStart(startDate.getTime()));
                localDataSlot.add(slot);
            }
        }

        if (localDataSlot.size() > 0) {
            Collections.sort(localDataSlot, new LocalRecordComparator());
        }
        return localDataSlot;
    }

    public interface OnRulerCacheUtilsLinstener {
        void onPlayTFCard(String alarmTime, String endTime, int mAlarmIndex, TimeSlot dirInfo);

        void setRulerDataMoreIcon(int local, boolean isShow);

        void isLastCardPlayFinished();
    }
}
