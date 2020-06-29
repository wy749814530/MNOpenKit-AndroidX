package com.mnopensdk.demo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mn.bean.restfull.DevStateInfoBean;
import com.mnopensdk.demo.BaseApplication;
import com.mnopensdk.demo.R;
import com.mnopensdk.demo.utils.StatusBarUtil;
import com.mnopensdk.demo.utils.ToastUtils;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WIN on 2018/1/9.
 */

public class AddDeviceExclamationActivity extends AppCompatActivity implements MNKitInterface.DeviceStateInfoCallBack {

    @BindView(R.id.receive_top)
    RelativeLayout receiveTop;
    String devSn = "", bindUser = "";
    @BindView(R.id.tv_binduser)
    TextView tvBinduser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_hasbind);
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, receiveTop);
        BaseApplication.getInstance().mActivityStack.addActivity(this);
        devSn = getIntent().getStringExtra("devSn");
        bindUser = getIntent().getStringExtra("bindUser");
        if (!"".equals(bindUser) && bindUser != null) {
            tvBinduser.setText(getString(R.string.add_usered_one) + bindUser + getString(R.string.add_usered_two));
        } else {
            getDevInfo(devSn);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().mActivityStack.removeActivity(this);
    }


    public void getDevInfo(String sn) {
        MNKit.getDeviceStateInfoWithSn(sn, this);
    }

    @Override
    public void onGetDeviceStateFailed(String message) {
        ToastUtils.MyToastBottom(getString(R.string.net_err_and_try));
    }

    @Override
    public void onGetDeviceStateSuc(DevStateInfoBean response) {
        try {
            if (response != null && response.getCode() == 2000 && response.getBind_state() == 2) {
                DevStateInfoBean.BindUserBean bind_user = response.getBind_user();
                if (bind_user.getPhone() != null || !"".equals(bind_user.getPhone())) {
                    tvBinduser.setText(getString(R.string.add_usered_one) + bind_user.getPhone() + getString(R.string.add_usered_two));
                } else if (!TextUtils.isEmpty(bind_user.getEmail())) {
                    tvBinduser.setText(getString(R.string.add_usered_one) + bind_user.getEmail() + getString(R.string.add_usered_two));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
