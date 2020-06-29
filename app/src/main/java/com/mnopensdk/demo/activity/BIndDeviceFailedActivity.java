package com.mnopensdk.demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.mnopensdk.demo.BaseApplication;
import com.mnopensdk.demo.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mnopensdk.demo.R;

/**
 * Created by WIN on 2018/1/9.
 * 其他错误情况的显示code
 */

public class BIndDeviceFailedActivity extends Activity {
    @BindView(R.id.receive_top)
    RelativeLayout receiveTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_device_failed);
        ButterKnife.bind(this);
        // StatusBarUtil.darkMode(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, receiveTop);
        BaseApplication.getInstance().mActivityStack.addActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().mActivityStack.removeActivity(this);
    }
}
