package com.mn;

import android.util.Log;

import com.mn.bean.restfull.AlarmTypeBean;
import com.mn.bean.restfull.PushconfigBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/3/10 0010.
 */

public class MnKitAlarmType {

    /**
     * 全部报警
     */
    public static final int AllAlarm_Detection = getAllAlarm_Detection();

    /**
     * 动检报警
     */
    public static final int Motion_Detection = 1 << 1;
    /**
     * 人形报警
     */
    public static final int Humanoid_Detection = 1 << 2;
    /**
     * 遮挡报警
     */
    public static final int Occlusion_Detection = 1 << 3;
    /**
     * 人脸识别报警
     */
    public static final int Face_Detection = 1 << 4;

    /**
     * 哭声报警
     */
    public static final int Cry_Detection = 1 << 5;
    /**
     * 箱体报警
     */
    public static final int Box_Detection = 1 << 6;
    /**
     * 门铃来电提醒报警
     */
    public static final int CallReminder_Detection = 1 << 7;
    /**
     * PIR 报警
     */
    public static final int Infrared_detection = 1 << 8;
    /**
     * 外部IO报警
     */
    public static final int IO_detection = 1 << 9;
    /**
     * 人脸考勤
     */
    public static final int IO_Attendance_detection = 1 << 10;

    public static int getAllAlarm_Detection() {
        int all = 0;
        all = all | Motion_Detection;
        all = all | Humanoid_Detection;
        all = all | Occlusion_Detection;
        all = all | Face_Detection;
        all = all | Cry_Detection;
        all = all | Box_Detection;
        all = all | Infrared_detection;
        all = all | IO_detection;
        all = all | IO_Attendance_detection;
        return all;
    }

    public static List<PushconfigBean.PushListBean> getAlarmTypeList(int alarmOptions) {
        List<PushconfigBean.PushListBean> pushList = new ArrayList<>();
        if ((alarmOptions & MnKitAlarmType.AllAlarm_Detection) == MnKitAlarmType.AllAlarm_Detection) {
            Log.i("setPushConfig", "所以报警");
            //人脸识别
            PushconfigBean.PushListBean alarm = new PushconfigBean.PushListBean();
            List<Integer> subAlarm = new ArrayList<>();
            subAlarm.add(3);
            subAlarm.add(4);
            subAlarm.add(5);
            alarm.setAlarmType(3);
            alarm.setSubAlarmType(subAlarm);
            pushList.add(alarm);

            //移动侦测
            PushconfigBean.PushListBean alarm1 = new PushconfigBean.PushListBean();
            List<Integer> subAlarm1 = new ArrayList<>();
            subAlarm1.add(0);
            alarm1.setAlarmType(8);
            alarm1.setSubAlarmType(subAlarm1);
            pushList.add(alarm1);

            // 人形检测
            PushconfigBean.PushListBean alarm2 = new PushconfigBean.PushListBean();
            List<Integer> subAlarm2 = new ArrayList<>();
            subAlarm2.add(1);
            alarm2.setAlarmType(11);
            alarm2.setSubAlarmType(subAlarm2);
            pushList.add(alarm2);

            // 遮挡报警
            PushconfigBean.PushListBean alarm3 = new PushconfigBean.PushListBean();
            List<Integer> subAlarm3 = new ArrayList<>();
            subAlarm3.add(1);
            alarm3.setAlarmType(13);
            alarm3.setSubAlarmType(subAlarm3);
            pushList.add(alarm3);
            // 哭声检测
            PushconfigBean.PushListBean alarm4 = new PushconfigBean.PushListBean();
            List<Integer> subAlarm4 = new ArrayList<>();
            subAlarm4.add(1);
            alarm4.setAlarmType(12);
            alarm4.setSubAlarmType(subAlarm4);
            pushList.add(alarm4);
            //箱体报警
            PushconfigBean.PushListBean alarm5 = new PushconfigBean.PushListBean();
            List<Integer> subAlarm5 = new ArrayList<>();
            subAlarm5.add(1);
            subAlarm5.add(2);
            subAlarm5.add(3);
            subAlarm5.add(4);
            subAlarm5.add(5);
            alarm5.setAlarmType(256);
            alarm5.setSubAlarmType(subAlarm5);
            pushList.add(alarm5);
            //PIR报警
            PushconfigBean.PushListBean alarm6 = new PushconfigBean.PushListBean();
            List<Integer> subAlarm6 = new ArrayList<>();
            alarm6.setAlarmType(14);
            subAlarm6.add(1);
            alarm6.setSubAlarmType(subAlarm6);
            pushList.add(alarm6);
            //外部IO报警
            PushconfigBean.PushListBean alarm7 = new PushconfigBean.PushListBean();
            List<Integer> subAlarm7 = new ArrayList<>();
            subAlarm7.add(1);
            alarm7.setAlarmType(1);
            alarm7.setSubAlarmType(subAlarm7);
            pushList.add(alarm7);
        } else {
            if ((alarmOptions & MnKitAlarmType.Face_Detection) == MnKitAlarmType.Face_Detection || (alarmOptions & MnKitAlarmType.IO_Attendance_detection) == MnKitAlarmType.IO_Attendance_detection) {
                Log.i("setPushConfig", "人脸识别");
                PushconfigBean.PushListBean alarm3 = new PushconfigBean.PushListBean();
                List<Integer> subAlarm = new ArrayList<>();
                if ((alarmOptions & MnKitAlarmType.Face_Detection) == MnKitAlarmType.Face_Detection) {
                    subAlarm.add(3);
                    subAlarm.add(4);
                }
                if ((alarmOptions & MnKitAlarmType.IO_Attendance_detection) == MnKitAlarmType.IO_Attendance_detection) {
                    subAlarm.add(5);
                }
                alarm3.setAlarmType(3);
                alarm3.setSubAlarmType(subAlarm);
                pushList.add(alarm3);
            }

            if ((alarmOptions & MnKitAlarmType.Motion_Detection) == MnKitAlarmType.Motion_Detection) {
                Log.i("setPushConfig", "移动侦测");
                List<Integer> subAlarm = new ArrayList<>();
                PushconfigBean.PushListBean alarm8 = new PushconfigBean.PushListBean();
                alarm8.setAlarmType(8);
                subAlarm.add(0);
                alarm8.setSubAlarmType(subAlarm);
                pushList.add(alarm8);
            }

            if ((alarmOptions & MnKitAlarmType.Humanoid_Detection) == MnKitAlarmType.Humanoid_Detection) {
                Log.i("setPushConfig", "人形检测");
                List<Integer> subAlarm = new ArrayList<>();
                PushconfigBean.PushListBean alarm11 = new PushconfigBean.PushListBean();
                alarm11.setAlarmType(11);
                subAlarm.add(1);
                alarm11.setSubAlarmType(subAlarm);
                pushList.add(alarm11);
            }

            if ((alarmOptions & MnKitAlarmType.Occlusion_Detection) == MnKitAlarmType.Occlusion_Detection) {
                Log.i("setPushConfig", "遮挡报警");
                List<Integer> subAlarm = new ArrayList<>();
                PushconfigBean.PushListBean alarm11 = new PushconfigBean.PushListBean();
                alarm11.setAlarmType(13);
                subAlarm.add(1);
                alarm11.setSubAlarmType(subAlarm);
                pushList.add(alarm11);
            }

            if ((alarmOptions & MnKitAlarmType.Cry_Detection) == MnKitAlarmType.Cry_Detection) {
                Log.i("setPushConfig", "哭声报警");
                List<Integer> subAlarm = new ArrayList<>();
                PushconfigBean.PushListBean alarm12 = new PushconfigBean.PushListBean();
                alarm12.setAlarmType(12);
                subAlarm.add(1);
                alarm12.setSubAlarmType(subAlarm);
                pushList.add(alarm12);
            }

            if ((alarmOptions & MnKitAlarmType.Box_Detection) == MnKitAlarmType.Box_Detection) {
                Log.i("setPushConfig", "箱体报警");
                List<Integer> subAlarm = new ArrayList<>();
                PushconfigBean.PushListBean alarm256 = new PushconfigBean.PushListBean();
                subAlarm.add(1);
                subAlarm.add(2);
                subAlarm.add(3);
                subAlarm.add(4);
                subAlarm.add(5);
                alarm256.setAlarmType(256);
                alarm256.setSubAlarmType(subAlarm);
                pushList.add(alarm256);
            }

            if ((alarmOptions & MnKitAlarmType.Infrared_detection) == MnKitAlarmType.Infrared_detection) {
                Log.i("setPushConfig", "PIR 人体红外报警");
                List<Integer> subAlarm = new ArrayList<>();
                PushconfigBean.PushListBean alarm12 = new PushconfigBean.PushListBean();
                alarm12.setAlarmType(14);
                subAlarm.add(1);
                alarm12.setSubAlarmType(subAlarm);
                pushList.add(alarm12);
            }

            if ((alarmOptions & MnKitAlarmType.IO_detection) == MnKitAlarmType.IO_detection) {
                Log.i("setPushConfig", "外部IO报警");
                List<Integer> subAlarm = new ArrayList<>();
                PushconfigBean.PushListBean alarm16 = new PushconfigBean.PushListBean();
                subAlarm.add(1);
                alarm16.setAlarmType(1);
                alarm16.setSubAlarmType(subAlarm);
                pushList.add(alarm16);
            }
        }

        return pushList;
    }
    
}
