package com.mnopensdk.demo.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.mnopensdk.demo.BaseApplication;


/**
 * Created by hz on 2017/8/29.
 */

public class ToastUtils {
    public static Toast mToast;

    public static void MyToast(String msg) {
        if (mToast == null) {
            //防止内存泄露所使用的context和（通过判断是否为空）防止多次new对象增加堆内存和多次弹出显示问题
            mToast = Toast.makeText(BaseApplication.getInstance(), "", Toast.LENGTH_SHORT);
            mToast.setText(msg);//解决小米手机toast带应用名称问题
        } else {
            mToast.setText(msg);
        }
        mToast.setGravity(Gravity.BOTTOM, 0, 175);
        mToast.show();
    }

    public static void MyToastCenter(String msg) {
        if (mToast == null) {
            //防止内存泄露所使用的context和（通过判断是否为空）防止多次new对象增加堆内存和多次弹出显示问题
            mToast = Toast.makeText(BaseApplication.getInstance(), "", Toast.LENGTH_SHORT);
            mToast.setText(msg);
        } else {
            mToast.setText(msg);
        }
        mToast.setGravity(Gravity.BOTTOM, 0, 175);
        mToast.show();
    }

    public static void MyToastBottom(String msg) {
        if (mToast == null) {
            //防止内存泄露所使用的context和（通过判断是否为空）防止多次new对象增加堆内存和多次弹出显示问题
            mToast = Toast.makeText(BaseApplication.getInstance(), "", Toast.LENGTH_SHORT);
            mToast.setText(msg);
        } else {
            mToast.setText(msg);
        }
        mToast.setGravity(Gravity.BOTTOM, 0, 175);
        mToast.show();
    }

    public static void onDestory() {
        mToast = null;
    }
}
