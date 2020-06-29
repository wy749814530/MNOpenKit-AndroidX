package com.mnopensdk.demo.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mnopensdk.demo.BaseApplication;

/**
 * Created by hjz on 2017/8/31.
 */

public class SharedPreferUtils {

    public static void write(String file, String key, String value) {
        new Thread(() -> {
            SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(file, BaseApplication.MODE_PRIVATE);
            Editor edit = preferences.edit();
            edit.putString(key, value);
            edit.commit();
        }).start();

    }

    public static String read(String file, String key, String defValue) {
        String tem = "";
        try {
            SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(file, BaseApplication.MODE_PRIVATE);
            tem = preferences.getString(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tem;
    }

    public static void write_bool(String file, String key, boolean value) {
        new Thread(() -> {
            SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(file, BaseApplication.MODE_PRIVATE);
            Editor edit = preferences.edit();
            edit.putBoolean(key, value);
            edit.commit();
        }).start();

    }

    public static boolean getBoolean(String file, String key, boolean defaultValue) {
        SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(file, BaseApplication.MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }

    public static boolean read_bool(String file, String key) {
        boolean aBoolean = false;
        try {
            SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(file, BaseApplication.MODE_PRIVATE);
            aBoolean = preferences.getBoolean(key, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aBoolean;
    }

    public static void write_int(String file, String key, int value) {
        new Thread(() -> {
            SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(file, BaseApplication.MODE_PRIVATE);
            Editor edit = preferences.edit();
            edit.putInt(key, value);
            edit.commit();
        }).start();

    }

    public static int read_int(String file, String key, int value) {
        int anInt = 0;
        try {
            SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(file, BaseApplication.MODE_PRIVATE);
            anInt = preferences.getInt(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return anInt;
    }

    public static void write_long(String file, String key, long value) {
        new Thread(() -> {
            SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(file, BaseApplication.MODE_PRIVATE);
            Editor edit = preferences.edit();
            edit.putLong(key, value);
            edit.commit();
        }).start();

    }

    public static long read_Long(String file, String key, long value) {
        long anInt = 0;
        try {
            SharedPreferences preferences = BaseApplication.getInstance().getSharedPreferences(file, BaseApplication.MODE_PRIVATE);
            anInt = preferences.getLong(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return anInt;
    }

}
