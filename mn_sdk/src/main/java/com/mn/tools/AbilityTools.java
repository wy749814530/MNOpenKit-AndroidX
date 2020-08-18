package com.mn.tools;


import com.mn.bean.restfull.DevicesBean;

import MNSDK.MNJni;

/**
 * Created by Administrator on 2019/10/29 0029.
 */

public class AbilityTools {

    /**
     * 是否是4G设备
     *
     * @param device
     * @return
     */
    public static boolean isFourGEnable(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getFourgAbility() != null) {
            if (device.getSupport_ability().getFourgAbility().getFourgEnable() == 1) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 是否是4G设备(剔除低功耗)
     * 支持5分钟暂停
     *
     * @param device
     * @return
     */
    public static boolean isFourGEnableSupportFivePouse(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getFourgAbility() != null) {
            if (device.getSupport_ability().getFourgAbility().getFourgEnable() == 1 && device.getType() != 17) {//17暂时没安排做低功耗，暂时过滤
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 是否是电池设备
     *
     * @param device
     * @return
     */
    public static boolean isBatteryDev(DevicesBean device) {
        if (device != null && (device.getType() == 2 || device.getType() == 17)) {
            return true;
        }
        return false;
    }

    /**
     * 是否需要统计流量
     *
     * @param device
     * @return
     */
    public static boolean isNeetTrafficStatistics(DevicesBean device) {
        if (device != null && (device.getType() == 5 || device.getType() == 13 || device.getType() == 17)) {
            return true;
        }
        return false;
    }


    /**
     * 是否支持云台控制
     *
     * @param device
     * @return
     */
    public static boolean isSupportPTZControl(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getPtzAbility() != null && device.getSupport_ability().getPtzAbility().getDirection() == 1) {
            // 支持云台控制的
            if (device.getAuthority() == 0 || AuthorityManager.hadPTZControlAuthority(device.getAuthority())) {
                // 自己的和分享的
                return true;
            }
        }
        if (device != null && (device.getType() == 7 || device.getType() == 10 || device.getType() == 13)) {
            if (device.getAuthority() == 0 || AuthorityManager.hadPTZControlAuthority(device.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否支持24小时云录像
     *
     * @param mDevice
     * @return
     */
    public static boolean isSupport24HourCloudRecording(DevicesBean mDevice) {
        if (mDevice != null && mDevice.getSupport_ability() != null && mDevice.getSupport_ability().getH24recordAbility() == 1) {
            return true;
        }
        return false;
    }

    /**
     * 是否支持时光机
     *
     * @param mDevice
     * @return
     */
    public static boolean isSupportTimeMachine(DevicesBean mDevice) {
        if (mDevice != null && mDevice.getSupport_ability() != null && mDevice.getSupport_ability().getTimingCaptureAbility() == 1) {
            return true;
        }
        return false;
    }

    /**
     * 是否支持人脸识别
     *
     * @param mDevice
     * @return
     */
    public static boolean isSupportFaceRecognition(DevicesBean mDevice) {
        if (mDevice != null && mDevice.getSupport_ability() != null && mDevice.getSupport_ability().getAlarmAbility() != null) {
            for (DevicesBean.SupportAbilityBean.AlarmAbilityBean alarmAbilityBean : mDevice.getSupport_ability().getAlarmAbility()) {
                if (alarmAbilityBean.getAlarmType() == 3 && (alarmAbilityBean.getSubAlarmType().contains(3) || alarmAbilityBean.getSubAlarmType().contains(4))) {
                    return true;
                }
            }
        }

        if (mDevice != null && (mDevice.getType() == 1 || mDevice.getType() == 7 || mDevice.getType() == 8)) {
            return true;
        }

        return false;
    }

    /**
     * 是否支持移动侦测
     *
     * @param mDevice
     * @return
     */
    public static boolean isSupportMotionDetection(DevicesBean mDevice) {
        if (mDevice != null && mDevice.getSupport_ability() != null && mDevice.getSupport_ability().getAlarmAbility() != null) {
            for (DevicesBean.SupportAbilityBean.AlarmAbilityBean alarmAbilityBean : mDevice.getSupport_ability().getAlarmAbility()) {
                if (alarmAbilityBean.getAlarmType() == 8) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否支持来电提醒
     *
     * @param device
     * @return
     */
    public static boolean isSupportIncomingCall(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getAlarmAbility() != null) {
            for (DevicesBean.SupportAbilityBean.AlarmAbilityBean alarmAbilityBean : device.getSupport_ability().getAlarmAbility()) {
                if (alarmAbilityBean.getAlarmType() == 10) {
                    return true;
                }
            }
        }

        if (device != null && device.getType() == 2) {
            return true;
        }
        return false;
    }

    /**
     * 是否支持人形检测
     *
     * @param device
     * @return
     */
    public static boolean isSupportHumanDetection(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getAlarmAbility() != null) {
            for (DevicesBean.SupportAbilityBean.AlarmAbilityBean alarmAbilityBean : device.getSupport_ability().getAlarmAbility()) {
                if (alarmAbilityBean.getAlarmType() == 11) {
                    return true;
                }
            }
        }

        if (device != null && (device.getType() == 6 || device.getType() == 16)) {
            return true;
        }
        return false;
    }

    /**
     * 是否支持哭声检测
     *
     * @param device
     * @return
     */
    public static boolean isSupportCryDetection(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getAlarmAbility() != null) {
            for (DevicesBean.SupportAbilityBean.AlarmAbilityBean alarmAbilityBean : device.getSupport_ability().getAlarmAbility()) {
                if (alarmAbilityBean.getAlarmType() == 12 && alarmAbilityBean.getSubAlarmType().contains(1)) {
                    return true;
                }
            }
        }

        if (device != null && (device.getType() == 6)) {
            return true;
        }
        return false;
    }


    /**
     * 是否支持箱体报警
     *
     * @param device
     * @return
     */
    public static boolean isSupportBoxAlarm(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getAlarmAbility() != null) {
            for (DevicesBean.SupportAbilityBean.AlarmAbilityBean alarmAbilityBean : device.getSupport_ability().getAlarmAbility()) {
                if (alarmAbilityBean.getAlarmType() == 256) {
                    return true;
                }
            }
        }

        if (device != null && (device.getType() == 9)) {
            return true;
        }
        return false;
    }

    /**
     * 是否支持遮挡报警
     *
     * @param mDevice
     * @return
     */
    public static boolean isSupportBlineDetect(DevicesBean mDevice) {
        if (mDevice != null && mDevice.getSupport_ability() != null && mDevice.getSupport_ability().getAlarmAbility() != null) {
            for (DevicesBean.SupportAbilityBean.AlarmAbilityBean alarmAbilityBean : mDevice.getSupport_ability().getAlarmAbility()) {
                if (alarmAbilityBean.getAlarmType() == 13 && alarmAbilityBean.getSubAlarmType().contains(1)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否支持考勤报警
     *
     * @param device
     * @return
     */
    public static boolean isSupportAttendance(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getAlarmAbility() != null) {
            for (DevicesBean.SupportAbilityBean.AlarmAbilityBean alarmAbilityBean : device.getSupport_ability().getAlarmAbility()) {
                if (alarmAbilityBean.getAlarmType() == 3 && alarmAbilityBean.getSubAlarmType().contains(5)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否是低功耗设备
     *
     * @param mDevice
     * @return
     */
    public static boolean isLowPowerDev(DevicesBean mDevice) {
        //  device.getType() == 17
        if (mDevice != null && mDevice.getSupport_ability() != null && mDevice.getSupport_ability().getOtherAbility() != null) {
            if (mDevice.getSupport_ability().getOtherAbility().getLowPower() == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否支持通用外部IO报警
     *
     * @param mDevice
     * @return
     */
    public static boolean isSupportIoAlarm(DevicesBean mDevice) {
        if (mDevice != null && mDevice.getSupport_ability() != null && mDevice.getSupport_ability().getAlarmAbility() != null) {
            for (DevicesBean.SupportAbilityBean.AlarmAbilityBean alarmAbilityBean : mDevice.getSupport_ability().getAlarmAbility()) {
                if (alarmAbilityBean.getAlarmType() == 1 && alarmAbilityBean.getSubAlarmType().contains(1)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 是否支持PIR红外侦测报警
     *
     * @param mDevice
     * @return
     */
    public static boolean isSupportPIRAlarm(DevicesBean mDevice) {
        if (mDevice != null && mDevice.getSupport_ability() != null && mDevice.getSupport_ability().getAlarmAbility() != null) {
            for (DevicesBean.SupportAbilityBean.AlarmAbilityBean alarmAbilityBean : mDevice.getSupport_ability().getAlarmAbility()) {
                if (alarmAbilityBean.getAlarmType() == 14 && alarmAbilityBean.getSubAlarmType().contains(1)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否支持人形框
     *
     * @param device
     * @return
     */
    public static boolean isSupportHumanShapeBox(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getOtherAbility() != null && device.getSupport_ability().getOtherAbility().getHumanShapeBox() == 1) {
            return true;
        }
        return false;
    }


    /**
     * 是否支持设置报警区域
     *
     * @param device
     * @return
     */
    public static boolean isSupportAlarmArea(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getOtherAbility() != null && device.getSupport_ability().getOtherAbility().getAlarmAreaSet() == 1) {
            return true;
        }
        return false;
    }

    /**
     * 是否支持设置警戒音
     *
     * @param device
     * @return
     */
    public static boolean isSupportSetAlarmTone(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getOtherAbility() != null && device.getSupport_ability().getOtherAbility().getAlarmAudioSet() == 1) {
            return true;
        }
        return false;
    }

    /**
     * 是否支持移动追踪
     *
     * @param device
     * @return
     */
    public static boolean isSupportDirectionTrack(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getPtzAbility() != null && device.getSupport_ability().getPtzAbility().getTrack() == 1) {
            return true;
        }
        return false;
    }

    /**
     * 是否是新协议
     *
     * @param device
     * @return
     */
    public static boolean isNewProtocol(DevicesBean device) {
        if (device != null && device.getSupport_ability() != null && device.getSupport_ability().getOtherAbility() != null && device.getSupport_ability().getOtherAbility().getNewProtocol() == 1) {
            MNJni.SetDeviceVersionType(device.getSn(), false);
            return true;
        }
        MNJni.SetDeviceVersionType(device.getSn(), true);
        return false;
    }

    /**
     * 根据设备类型判断是否是新协议
     *
     * @param deviceType
     * @param devSn
     * @return
     */
    public static boolean isNewProtocolByType(int deviceType, String devSn) {
        if (deviceType == 10 || deviceType == 16 || deviceType == 21 || deviceType == 22 || deviceType == 23) {
            MNJni.SetDeviceVersionType(devSn, false);
            return true;
        } else {
            MNJni.SetDeviceVersionType(devSn, true);
            if (deviceType == 4 || deviceType == 11) {
                return true;
            } else {
                return false;
            }
        }
    }
}
