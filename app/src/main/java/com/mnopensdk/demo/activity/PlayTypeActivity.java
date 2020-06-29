package com.mnopensdk.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.mnopensdk.demo.activity.setting.DeviceSettingActivity;
import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mnopensdk.demo.widget.RuleAlertDialog;
import com.mn.bean.restfull.BaseBean;
import com.mn.bean.restfull.DevicesBean;
import com.mn.tools.AbilityTools;

import MNSDK.MNKit;
import MNSDK.MNOpenSDK;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2019/12/9 0009.
 */

public class PlayTypeActivity extends BaseActivity {

    @BindView(R.id.btn_cloud_alarm)
    Button btnCloudAlarm;
    @BindView(R.id.btn_24_cloud_record)
    Button btn24CloudRecord;
    @BindView(R.id.btn_local_record)
    Button btnLocalRecord;
    DevicesBean mDevice;
    @BindView(R.id.btn_live_lay)
    Button btnLiveLay;

    LoadingDialog loadingDialog;
    @BindView(R.id.btn_setting)
    Button btnSetting;
    @BindView(R.id.btn_share)
    Button btnShare;
    @BindView(R.id.btn_del_dev)
    Button btnDelDev;
    @BindView(R.id.btn_my_share)
    Button btnMyShare;

    private String mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        setContentView(R.layout.activity_play_type);
        ButterKnife.bind(this);
    }

    @Override
    protected int setViewLayout() {
        return R.layout.activity_play_type;
    }

    @Override
    protected void initData() {
        setTitle("");
        loadingDialog = new LoadingDialog(this);
        mDevice = (DevicesBean) getIntent().getSerializableExtra("device_info");
        //下边代码很重要，与设备建立连接，为了快速打开视频，可以在请求道设备数据之后，立即调用此方法
        MNOpenSDK.linkToDevice(mDevice.getSn(), mDevice.getAuthority() != 0);

        // 是否是分享设备
        if (TextUtils.isEmpty(mDevice.getFrom())) {
            btnShare.setVisibility(View.GONE);
            btnMyShare.setVisibility(View.GONE);
        } else {
            btnShare.setVisibility(View.VISIBLE);
            btnMyShare.setVisibility(View.VISIBLE);
        }

        // 是否支持24小时云录像
        if (AbilityTools.isSupport24HourCloudRecording(mDevice)) {
            btn24CloudRecord.setVisibility(View.VISIBLE);
        } else {
            btn24CloudRecord.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void onBackKeyDown(boolean b) {
        finish();
    }

    @Override
    protected void onRightTvClick() {

    }

    @Override
    protected void onLeftTvClick() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.btn_live_lay, R.id.btn_cloud_alarm, R.id.btn_24_cloud_record, R.id.btn_local_record, R.id.btn_setting, R.id.btn_del_dev,
            R.id.btn_share, R.id.btn_my_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_live_lay:
                Intent intent = new Intent(this, MNPlayControlActivity.class);
                intent.putExtra("device_info", mDevice);
                startActivity(intent);
                break;
            case R.id.btn_cloud_alarm:
                Intent cloudIntent = new Intent(this, CloudAlarmActivity.class);
                cloudIntent.putExtra("device_info", mDevice);
                startActivity(cloudIntent);
                break;
            case R.id.btn_24_cloud_record:
                Intent cloud24Intent = new Intent(this, MN24CloudRecordActivity.class);
                cloud24Intent.putExtra("device_info", mDevice);
                startActivity(cloud24Intent);
                break;
            case R.id.btn_local_record:
                Intent tfCardIntent = new Intent(this, MNTfCardPlayActivity.class);
                tfCardIntent.putExtra("device_info", mDevice);
                startActivity(tfCardIntent);
                break;
            case R.id.btn_setting:
                Intent settingIntent = new Intent(this, DeviceSettingActivity.class);
                settingIntent.putExtra("device_info", mDevice);
                startActivity(settingIntent);
                break;
            case R.id.btn_share:
                Intent shareIntent = new Intent(this, ShareMeToFriendsActivity.class);
                shareIntent.putExtra("devicesBean", mDevice);
                startActivity(shareIntent);
                break;
            case R.id.btn_my_share:
                Intent myShareIntent = new Intent(this, MyShareActivity.class);
                startActivity(myShareIntent);
                break;
            case R.id.btn_del_dev:
                new RuleAlertDialog(this).builder().
                        setTitle(getString(R.string.add_wifi_tip)).
                        setMsg(getString(R.string.sure_delete_camera)).
                        setOkButton(getString(R.string.label_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                delDevice();
                            }
                        }).setCancelButton(getString(R.string.label_cancel), null).show();
                break;
        }
    }


    private void delDevice() {
        if (mDevice.getAuthority() != 0) {//共享设备
            delShareDevice();
        } else {
            //自己设备解绑
            delSelfDevice();
        }
    }

    /**
     * 删除分享的设备
     */

    private void delShareDevice() {
        if (loadingDialog != null) {
            loadingDialog.show();
        }
        MNKit.unbindSharedDevice(mDevice.getId(), new MNKitInterface.UnBindShareDeviceCallBack() {
            @Override
            public void onUnBindShareDeviceFailed(String localizedMessage) {
                ToastUtils.MyToastBottom(localizedMessage);
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onUnBindShareDeviceSuc(BaseBean response) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                if (response == null) {
                    ToastUtils.MyToastBottom(getString(R.string.net_err_and_try));
                } else {
                    if (response.getCode() == 2000) {
                        setResult(200);
                        finish();
                    } else {
                        ToastUtils.MyToastBottom(response.getMsg());
                    }
                }
            }
        });
    }

    /**
     * 删除自己账号下的设备
     */

    private void delSelfDevice() {
        if (loadingDialog != null) {
            loadingDialog.show();
        }
        MNKit.unbindDevice(mDevice.getSn(), new MNKitInterface.UnbindDeviceCallBack() {
            @Override
            public void onUnbindDeviceFailed(String message) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                ToastUtils.MyToastBottom(message);
            }

            @Override
            public void onUnbindDeviceSuc(BaseBean response) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                if (response == null) {
                    ToastUtils.MyToastBottom(getString(R.string.net_err_and_try));
                } else {
                    if (response.getCode() == 2000) {
                        setResult(200);
                        finish();
                    } else {
                        ToastUtils.MyToastBottom(response.getMsg());
                    }
                }
            }
        });
    }
}
