package com.mnopensdk.demo.utils;

import java.util.Locale;

/**
 * Created by Administrator on 2019/3/14 0014.
 */

public class LanguageManager {

    public static String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        LogUtil.i("LanguageManager", "language : " + language);
        return language;
    }
}
