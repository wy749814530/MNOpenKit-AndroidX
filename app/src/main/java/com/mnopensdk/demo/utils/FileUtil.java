package com.mnopensdk.demo.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.mnopensdk.demo.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.mnopensdk.demo.R;

/**
 * Created by hjz on 2017/9/5.
 */

public class FileUtil {

    //删除文件 夹下面的文件
    public static void deleteFile(String fileName) {
        //先清空之前的文件
        File baseFile = new File(fileName);
        if (baseFile != null && baseFile.exists()) {
            if (baseFile.isFile()) {
                deleteSeletect(fileName);
            } else if (baseFile.isDirectory()) {
                File[] f = baseFile.listFiles();
                if (f != null) {
                    for (int i = f.length - 1; i > -1; --i) {
                        if (f[i].isFile() && f[i].length() > 0) {
                            f[i].delete();
                        }
                    }
                }
            }
        }
    }

    //public static Map<String, SoftReference<Bitmap>> imageCaches = new HashMap<String, SoftReference<Bitmap>>();//本地图片软存

    //删除单个文件
    public static boolean deleteSeletect(String path) {
        if (path == "") {
            return false;
        }
        File file = new File(path);
        try {
            if (file.isFile() && file.exists()) {
                file.delete();
            }
            return true;
        } catch (Exception e) {
          e.printStackTrace();
        }
        return false;
    }



    //时间排序
    public static List<String> sortList(List<String> list) {
        List<String> lstTemp = new ArrayList<String>();
        int nCount1 = list.size();
        for (int n = 0; n < nCount1; n++) {
            String strMaxItem = Collections.max(list);
            if (!lstTemp.contains(strMaxItem)) {
                lstTemp.add(strMaxItem);
            }
            list.remove(strMaxItem);
        }
        list.clear();
        for (String strNewItem : lstTemp) {
            list.add(strNewItem);
        }
        return list;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getJpgName() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String strDate = sdf.format(date);
        String fileName = strDate + ".jpg";
        return fileName;
    }

    /**
     * 获取bitmap存储到本地的路径
     *
     * @return
     */
    public static String getJpgPathName(Context context) {
        String path = Constants.ImagePath + context.getString(R.string.app_name) + File.separator;
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
        String fileName = path + System.currentTimeMillis() + ".bmp";
        return fileName;
    }

}
