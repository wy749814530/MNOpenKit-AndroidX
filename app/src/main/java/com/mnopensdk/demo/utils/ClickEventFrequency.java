package com.mnopensdk.demo.utils;

/**
 * Created by Administrator on 2019/3/7 0007.
 */

public class ClickEventFrequency {
    private static long lastTime = 0;

    /**
     * 设置点击事件频率
     * @param millis
     * @return 是否响应点击事件
     */
    public static boolean enableClick(long millis){
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastTime > millis){
            lastTime = currentClickTime;
            return true;
        }
        return false;
    }
}
