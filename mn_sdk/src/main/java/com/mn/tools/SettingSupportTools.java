package com.mn.tools;


import com.mn.bean.restfull.DevicesBean;

/**
 * Created by Administrator on 2020/5/7 0007.
 */

public class SettingSupportTools {

    // 是否支持呼吸灯
    public static boolean isSupportBreathingLight(DevicesBean device) {
        if (device != null && (device.getType() == 1 || device.getType() == 2 || device.getType() == 3 || device.getType() == 6 || device.getType() == 9 || device.getType() == 14)) {
            return true;
        }
        return false;
    }

    /**
     * 是否支持背光补偿
     *
     * @param device
     * @return
     */
    public static boolean isSupportBLC(DevicesBean device) {
        if (device != null && (device.getType() == 10 || device.getType() == 16 || device.getType() == 21 || device.getType() == 22 || device.getType() == 23)) {
            return true;
        }
        return false;
    }

    /**
     * 是否支持警戒音
     *
     * @param device
     * @return
     */
    public static boolean isSupportAlertTone(DevicesBean device) {
        if (device != null && (device.getType() == 16 || device.getType() == 23)) {
            return true;
        }
        return false;
    }

    /**
     * 夜视模式-->灯光模式
     *
     * @param device
     * @return
     */
    public static boolean isSupportLightMode(DevicesBean device) {
        if (device != null && (device.getType() == 16 || device.getType() == 23)) {
            return true;
        }
        return false;
    }

    /**
     * 是否支持画面翻转
     *
     * @param device
     * @return
     */
    public static boolean isSupportScreenFlip(DevicesBean device) {
        if (device != null && (device.getType() == 4 || device.getType() == 11 || device.getType() == 23)) {
            return false;
        }
        return true;
    }

    /**
     * 是否支持通话
     */
    public static boolean isSupportCall(DevicesBean device) {
        if (device != null && (device.getType() == 4 || device.getType() == 11)) {
            return false;
        }
        return true;
    }

    /**
     * 是否支持8方向云台
     *
     * @param device
     * @return
     */
    public static boolean isSupport8directions(DevicesBean device) {
        if (device != null && (device.getType() == 13)) {
            return true;
        }
        return false;
    }

}
