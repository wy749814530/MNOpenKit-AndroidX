package com.mn;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.mn.bean.restfull.ServerMsgBean;

/**
 * Created by Administrator on 2020/3/9 0009.
 */

public abstract class MNApplication extends Application {
    private static MNApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
    }

    public static MNApplication getApplication() {
        return mApplication;
    }

    public abstract void OnServerMSG(ServerMsgBean data);
}
