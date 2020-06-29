package com.mnopensdk.demo.activity.setting;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.restfull.FirmwareVerBean;
import com.mn.bean.setting.AlarmAsossiatedBean;
import com.mn.bean.setting.BLCConfigBean;
import com.mn.bean.setting.BaseResult;
import com.mn.bean.setting.DevBaseInfoBean;
import com.mn.bean.setting.DevStandardBean;
import com.mn.bean.setting.LanguageBean;
import com.mn.bean.setting.LocalesConfigBean;
import com.mn.bean.setting.NetLightCallBean;
import com.mn.bean.setting.TimeZoneBean;
import com.mn.bean.setting.VideoInOptBean;
import com.mn.bean.setting.VideoOptionsBean;
import com.mn.tools.AbilityTools;
import com.mn.tools.SettingSupportTools;
import com.mnopensdk.demo.BaseApplication;
import com.mnopensdk.demo.R;
import com.mnopensdk.demo.activity.HomeActivity;
import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.utils.LogUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mnopensdk.demo.widget.ReNameDialog;
import com.mnopensdk.demo.widget.RuleAlertDialog;

import MNSDK.MNKit;
import MNSDK.MNOpenSDK;
import MNSDK.inface.MNKitInterface;
import MNSDK.inface.MNOpenSDKInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/12/12 0012.
 */

public class DeviceSettingActivity extends BaseActivity implements MNOpenSDKInterface.SetSummerTimeConfigCallBack, MNOpenSDKInterface.SetBLCConfigCallBack, MNOpenSDKInterface.SetVideoInOptionsCallBack, MNOpenSDKInterface.SetAlarmAsossiatedConfigCallBack, MNOpenSDKInterface.SetNetLightCallBack {

    DevicesBean mDevice;
    @BindView(R.id.tv_serialNum)
    TextView tvSerialNum;
    @BindView(R.id.tv_model)
    TextView tvModel;
    @BindView(R.id.tv_firmware_version)
    TextView tvFirmwareVersion;
    @BindView(R.id.rl_message_pushe_lay)
    RelativeLayout rlMessagePusheLay;
    @BindView(R.id.tv_time_zone)
    TextView tvTimeZone;
    @BindView(R.id.rl_time_zone_lay)
    RelativeLayout rlTimeZoneLay;
    @BindView(R.id.iv_summer_time)
    ImageView ivSummerTime;
    @BindView(R.id.rl_summer_time_lay)
    RelativeLayout rlSummerTimeLay;
    @BindView(R.id.iv_dev_blc)
    ImageView ivDevBlc;
    @BindView(R.id.rl_dev_blc_lay)
    RelativeLayout rlDevBlcLay;
    @BindView(R.id.iv_camera_picture)
    ImageView ivCameraPicture;
    @BindView(R.id.rl_camera_picture_lay)
    RelativeLayout rlCameraPictureLay;
    @BindView(R.id.tv_lighting_mode)
    TextView tvLightingMode;
    @BindView(R.id.rl_lighting_mode_lay)
    RelativeLayout rlLightingModeLay;
    @BindView(R.id.iv_alert_tone)
    ImageView ivAlertTone;
    @BindView(R.id.rl_alert_tone_lay)
    RelativeLayout rlAlertToneLay;
    @BindView(R.id.tv_audio_settings)
    TextView tvAudioSettings;
    @BindView(R.id.rl_audio_settings_lay)
    RelativeLayout rlAudioSettingsLay;
    @BindView(R.id.rl_reboot_the_device_lay)
    RelativeLayout rlRebootTheDeviceLay;
    @BindView(R.id.iv_night_vision_mode)
    ImageView ivNightVisionMode;
    @BindView(R.id.rl_infrared_light_mode_lay)
    RelativeLayout rlInfraredLightModeLay;
    @BindView(R.id.tv_dev_name)
    TextView tvDevName;
    @BindView(R.id.rl_redev_name_lay)
    RelativeLayout rlRedevNameLay;
    @BindView(R.id.rl_storage_card_lay)
    RelativeLayout rlStorageCardLay;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.tv_standard)
    TextView tvStandard;
    @BindView(R.id.iv_breathing_light_switch)
    ImageView ivBreathingLightSwitch;
    @BindView(R.id.rl_breathing_light_lay)
    RelativeLayout rlBreathingLightLay;
    @BindView(R.id.dev_item_update)
    Button devItemUpdate;

    private LoadingDialog loadingDialog;
    private ReNameDialog reNameDialog;
    int lightSensitive = 50;
    // 画面翻转与红外灯模式
    VideoInOptBean mVideoInfo;

    // 灯光模式
    int mLightType;
    boolean mAudioEnable;

    private String mPal;
    private String mLang;
    private FirmwareVerBean mVerBean;

    @Override
    protected int setViewLayout() {
        return R.layout.activity_device_setting;
    }

    @Override
    protected void initData() {
        mDevice = (DevicesBean) getIntent().getSerializableExtra("device_info");

        tvDevName.setText(mDevice.getDev_name());
        //下边代码比较重要
        MNOpenSDK.linkToDevice(mDevice.getSn(), mDevice.getAuthority() != 0);
    }

    @Override
    protected void initViews() {
        setTitle(getString(R.string.dev_setting));
    }

    @Override
    protected void initEvents() {
        tvSerialNum.setText(mDevice.getSn());
        tvModel.setText(mDevice.getModel());
        tvFirmwareVersion.setText(mDevice.getVer());
        loadingDialog = new LoadingDialog(this);

        // 获取设备基础信息
        MNOpenSDK.getDeviceBaseInfo(mDevice.getSn(), new MNOpenSDKInterface.GetDeviceBaseInfoCallBack() {
            @Override
            public void onGetDeviceBaseInfo(DevBaseInfoBean bean) {
                LogUtil.i("DeviceSettingActivity", "onGetDeviceBaseInfo : " + new Gson().toJson(bean));
            }
        });

        // 获取语言
        MNOpenSDK.getLanguageConfig(mDevice.getSn(), new MNOpenSDKInterface.LanguageConfigCallBack() {
            @Override
            public void onLanguageConfig(LanguageBean data) {
                if (data != null && data.isResult() && data.getParams() != null) {
                    tvLanguage.setText(data.getParams().getLanguage());
                    mLang = data.getParams().getLanguage();
                    if (mPal != null) {
                        checkFirmwareVer();
                    }
                }
            }
        });

        // 获取视频制式
        MNOpenSDK.getVideoStandard(mDevice.getSn(), new MNOpenSDKInterface.VideoStandardConfigCallBack() {
            @Override
            public void onVideoStandardConfig(DevStandardBean data) {
                if (data != null && data.isResult() && data.getParams() != null) {
                    tvStandard.setText(data.getParams().getVideoStandard());
                    mPal = data.getParams().getVideoStandard();
                    if (mLang != null) {
                        checkFirmwareVer();
                    }
                }
            }
        });

        // 时区配置
        MNOpenSDK.getTimeZoneConfig(mDevice.getSn(), new MNOpenSDKInterface.GetTimeZoneConfigCallBack() {
            @Override
            public void onGetTimeZoneConfig(TimeZoneBean bean) {
                Resources res = BaseApplication.getInstance().getResources();
                String[] city = res.getStringArray(R.array.timezones_array);
                if (bean != null && bean.isResult() && bean.getParams() != null) {
                    int zoneIndex = bean.getParams().getTimeZone();
                    tvTimeZone.setText(city[zoneIndex]);
                }
            }
        });

        // 获取夏令时配置
        MNOpenSDK.getSummerTimeConfig(mDevice.getSn(), new MNOpenSDKInterface.GetSummerTimeConfigCallBack() {

            @Override
            public void onGetSummerTimeConfig(LocalesConfigBean bean) {
                boolean dstEnable = false;
                if (bean != null && bean.isResult() && bean.getParams() != null) {
                    dstEnable = bean.getParams().isDSTEnable();
                }
                if (dstEnable) {
                    ivSummerTime.setImageResource(R.mipmap.st_switch_on);
                    ivSummerTime.setTag("on");
                } else {
                    ivSummerTime.setImageResource(R.mipmap.st_switch_off);
                    ivSummerTime.setTag("off");
                }
            }
        });

        // 获取呼吸灯开关状态
        if (SettingSupportTools.isSupportBreathingLight(mDevice)) {
            rlBreathingLightLay.setVisibility(View.VISIBLE);
            MNOpenSDK.getNetLightConfig(mDevice.getSn(), new MNOpenSDKInterface.GetNetLightCallBack() {
                @Override
                public void onGetNetLight(NetLightCallBean bean) {
                    boolean isNetLight = false;
                    if (bean != null && bean.isResult() && bean.getParams() != null) {
                        isNetLight = bean.getParams().isNetLight();

                    }
                    if (isNetLight) {
                        ivBreathingLightSwitch.setImageResource(R.mipmap.st_switch_on);
                        ivBreathingLightSwitch.setTag("on");
                    } else {
                        ivBreathingLightSwitch.setImageResource(R.mipmap.st_switch_off);
                        ivBreathingLightSwitch.setTag("off");
                    }
                }
            });
        } else {
            rlBreathingLightLay.setVisibility(View.GONE);
        }

        // 背光补偿
        if (SettingSupportTools.isSupportBLC(mDevice)) {
            rlDevBlcLay.setVisibility(View.VISIBLE);
            MNOpenSDK.getBLCConfig(mDevice.getSn(), new MNOpenSDKInterface.GetBLCConfigCallBack() {
                @Override
                public void onGetBLCConfig(BLCConfigBean bean) {
                    boolean lightEnable = false;
                    if (bean != null && bean.isResult() && bean.getParams() != null) {
                        lightEnable = bean.getParams().isLightEnable();
                        lightSensitive = bean.getParams().getLightSensitive();
                    }
                    if (lightEnable) {
                        ivDevBlc.setImageResource(R.mipmap.st_switch_on);
                        ivDevBlc.setTag("on");
                    } else {
                        ivDevBlc.setImageResource(R.mipmap.st_switch_off);
                        ivDevBlc.setTag("off");
                    }
                }
            });
        } else {
            rlDevBlcLay.setVisibility(View.GONE);
        }

        // 画面翻转
        MNOpenSDK.getVideoInOptions(mDevice.getSn(), new MNOpenSDKInterface.GetVideoInOptionsCallBack() {
            @Override
            public void onGetVideoInOptions(VideoOptionsBean bean) {
                if (bean != null && bean.isResult() && bean.getParams() != null) {
                    mVideoInfo = bean.getParams();

                    if (mVideoInfo.isMirror()) {
                        // 倒着放
                        ivCameraPicture.setTag("down");
                        ivCameraPicture.setImageResource(R.mipmap.camera_picture_down);
                    } else {
                        // 正着放
                        ivCameraPicture.setTag("up");
                        ivCameraPicture.setImageResource(R.mipmap.camera_picture_up);
                    }

                    if (mVideoInfo.getDayNightColor() == 0) {
                        ivNightVisionMode.setTag("off");
                        ivNightVisionMode.setImageResource(R.mipmap.set_icon_night_off);
                    } else if (mVideoInfo.getDayNightColor() == 1) {
                        ivNightVisionMode.setTag("auto");
                        ivNightVisionMode.setImageResource(R.mipmap.set_icon_night_auto);
                    } else if (mVideoInfo.getDayNightColor() == 3) {
                        ivNightVisionMode.setTag("on");
                        ivNightVisionMode.setImageResource(R.mipmap.set_icon_night_on);
                    }
                }
            }
        });

        // 声光报警模式
        if (SettingSupportTools.isSupportAlertTone(mDevice)) {
            //530WIFI枪机才有 声光报警模式
            rlAlertToneLay.setVisibility(View.VISIBLE);
            rlLightingModeLay.setVisibility(View.VISIBLE);
            MNOpenSDK.getAlarmAsossiatedConfig(mDevice.getSn(), new MNOpenSDKInterface.GetAlarmAsossiatedConfigCallBack() {
                @Override
                public void onGetAlarmAsossiatedConfig(AlarmAsossiatedBean bean) {
                    if (bean != null && bean.isResult() && bean.getParams() != null) {
                        mLightType = bean.getParams().getLightType();
                        mAudioEnable = bean.getParams().isAudioEnable();
                        // 0 红外夜视;1 星光全彩2; 双光警戒;（之前是     // 0 星光全彩;1红外夜视 2; 双光警戒）
                        if (mLightType == 0) {
                            tvLightingMode.setText(getString(R.string.set_sw_0));
                        } else if (mLightType == 1) {
                            tvLightingMode.setText(getString(R.string.set_sw_1));
                        } else if (mLightType == 2) {
                            tvLightingMode.setText(getString(R.string.set_sw_2));
                        }

                        if (mAudioEnable) {
                            ivAlertTone.setImageResource(R.mipmap.st_switch_on);
                            ivAlertTone.setTag("on");
                        } else {
                            ivAlertTone.setImageResource(R.mipmap.st_switch_off);
                            ivAlertTone.setTag("off");
                        }
                    }
                }
            });
        } else {
            rlAlertToneLay.setVisibility(View.GONE);
            rlLightingModeLay.setVisibility(View.GONE);
        }

    }

    private void checkFirmwareVer() {
        MNKit.getFirmwareVer(mDevice.getSn(), mPal, mLang, 10, new MNKitInterface.GetFirmwareVerCallBack() {
            @Override
            public void onGetFirmwareVerSuc(FirmwareVerBean verBean) {
                Log.i("DeviceSettingActivity", "" + new Gson().toJson(verBean));
                if (verBean.getFirmwares() != null && verBean.getFirmwares().size() != 0) {
                    devItemUpdate.setVisibility(View.VISIBLE);
                    mVerBean = verBean;
                }
            }

            @Override
            public void onGetFirmwareVerFailed(String msg) {
                ToastUtils.MyToastBottom(getString(R.string.net_err_and_try));
            }
        });
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

    @OnClick({R.id.rl_message_pushe_lay, R.id.rl_time_zone_lay, R.id.rl_summer_time_lay, R.id.rl_dev_blc_lay,
            R.id.rl_camera_picture_lay, R.id.rl_infrared_light_mode_lay, R.id.rl_lighting_mode_lay, R.id.rl_alert_tone_lay,
            R.id.rl_audio_settings_lay, R.id.rl_reboot_the_device_lay, R.id.rl_storage_card_lay, R.id.rl_redev_name_lay,
            R.id.rl_breathing_light_lay, R.id.dev_item_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dev_item_update:
                // 固件更新
                Intent intent = new Intent(this, DevSetUpFirmActivity.class);
                intent.putExtra("firmwareVerBean", mVerBean);
                intent.putExtra("dev_name", mDevice.getDev_name());
                intent.putExtra("uuid", mDevice.getSn());
                startActivity(intent);
                break;
            case R.id.rl_redev_name_lay:
                if (reNameDialog == null) {
                    reNameDialog = new ReNameDialog(this).setOnDismissListener(new ReNameDialog.OnDismissListener() {
                        @Override
                        public void onDismiss(String name) {
                            tvDevName.setText(name);
                            mDevice.setDev_name(name);
                            if (HomeActivity.instance != null) {
                                HomeActivity.instance.onRefresh();
                            }
                        }
                    });
                }
                reNameDialog.setContent(mDevice.getSn(), mDevice.getDev_name()).show();
                break;
            case R.id.rl_message_pushe_lay:
                // 消息推送设置
                Intent pushIntent = new Intent(this, MessagePusheActivity.class);
                pushIntent.putExtra("device_info", mDevice);
                startActivity(pushIntent);
                break;
            case R.id.rl_time_zone_lay:
                // 时区
                Intent zoneIntent = new Intent(this, TimeZoneActivity.class);
                zoneIntent.putExtra("device_info", mDevice);
                startActivityForResult(zoneIntent, 2000);
                break;
            case R.id.rl_summer_time_lay:
                // 夏令时
                loadingDialog.show();
                if ("off".equals(ivSummerTime.getTag())) {
                    MNOpenSDK.setSummerTimeConfig(mDevice.getSn(), true, this);
                } else {
                    MNOpenSDK.setSummerTimeConfig(mDevice.getSn(), false, this);
                }
                break;
            case R.id.rl_dev_blc_lay:
                // 背光补偿
                if ("off".equals(ivDevBlc.getTag())) {
                    MNOpenSDK.setBLCConfig(mDevice.getSn(), true, lightSensitive, this);
                } else {
                    MNOpenSDK.setBLCConfig(mDevice.getSn(), false, lightSensitive, this);
                }
                break;
            case R.id.rl_camera_picture_lay:
                if (mVideoInfo == null) {
                    return;
                }
                // 画面翻转
                if ("down".equals(ivCameraPicture.getTag())) {
                    // 当前倒着放，设置为正着放
                    mVideoInfo.setFlip(false);
                    mVideoInfo.setMirror(false);
                } else {
                    // 当前正着放，设置为倒着放
                    mVideoInfo.setFlip(true);
                    mVideoInfo.setMirror(true);
                }
                loadingDialog.show();
                MNOpenSDK.setVideoInOptions(mDevice.getSn(), mVideoInfo, this);
                break;
            case R.id.rl_infrared_light_mode_lay:
                if (mVideoInfo == null) {
                    return;
                }
                // 红外灯模式
                if ("off".equals(ivNightVisionMode.getTag())) {
                    // 关闭-->开启
                    mVideoInfo.setDayNightColor(3);
                } else if ("on".equals(ivNightVisionMode.getTag())) {
                    // 开启-->自动
                    mVideoInfo.setDayNightColor(1);
                } else {
                    // 自动-->关闭
                    mVideoInfo.setDayNightColor(0);
                }
                loadingDialog.show();
                MNOpenSDK.setVideoInOptions(mDevice.getSn(), mVideoInfo, this);
                break;
            case R.id.rl_lighting_mode_lay:
                // 灯光模式
                Intent lightingIntent = new Intent(this, DevAlartModeActivity.class);
                lightingIntent.putExtra("devSn", mDevice.getSn());
                lightingIntent.putExtra("lightType", mLightType);
                startActivityForResult(lightingIntent, 3000);
                break;
            case R.id.rl_breathing_light_lay:
                // 呼吸灯设置
                loadingDialog.show();
                if ("off".equals(ivBreathingLightSwitch.getTag())) {
                    MNOpenSDK.setNetLightConfig(mDevice.getSn(), true, this);
                } else {
                    MNOpenSDK.setNetLightConfig(mDevice.getSn(), false, this);
                }
                break;
            case R.id.rl_alert_tone_lay:
                //警戒音
                loadingDialog.show();
                if ("off".equals(ivAlertTone.getTag())) {
                    MNOpenSDK.setAlarmAsossiatedConfig(mDevice.getSn(), mLightType, true, this);
                } else {
                    MNOpenSDK.setAlarmAsossiatedConfig(mDevice.getSn(), mLightType, false, this);
                }
                break;
            case R.id.rl_audio_settings_lay:
                //音频设置
                Intent audioIntent = new Intent(this, DevSetVolumeActivity.class);
                audioIntent.putExtra("device", mDevice);
                startActivity(audioIntent);
                break;
            case R.id.rl_reboot_the_device_lay:
                // 重启摄像机
                new RuleAlertDialog(this).builder().setTitle(getString(R.string.sure_restart_cam)).
                        setOkButton(getString(R.string.label_ok), v -> {
                            MNOpenSDK.rebootDevice(mDevice.getSn());
                            finish();
                        }).
                        setCancelButton(getString(R.string.label_cancel), null).
                        setCancelBtnColor(getResources().getColor(R.color.login_int)).show();

                break;
            case R.id.rl_storage_card_lay:
                Intent tfIntent = new Intent(this, DevSetTFActivity.class);
                tfIntent.putExtra("device", mDevice);
                startActivity(tfIntent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2000 && resultCode == 200 && data != null) {
            //data.getIntExtra("zoneIndex", 0);
            String zoneTime = data.getStringExtra("zoneTime");
            tvTimeZone.setText(zoneTime);
        } else if (requestCode == 3000 && resultCode == 200 && data != null) {
            mLightType = data.getIntExtra("lightType", -1);
            if (mLightType == 0) {
                tvLightingMode.setText(getString(R.string.set_sw_0));
            } else if (mLightType == 1) {
                tvLightingMode.setText(getString(R.string.set_sw_1));
            } else if (mLightType == 2) {
                tvLightingMode.setText(getString(R.string.set_sw_2));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSetSummerTimeConfig(BaseResult bean) {
        //TODO 设置夏令时开关
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (bean != null && bean.isResult()) {
            if ("off".equals(ivSummerTime.getTag())) {
                ivSummerTime.setImageResource(R.mipmap.st_switch_on);
                ivSummerTime.setTag("on");
            } else {
                ivSummerTime.setImageResource(R.mipmap.st_switch_off);
                ivSummerTime.setTag("off");
            }
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_successfully));
        } else {
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_failed));
        }
    }

    @Override
    public void onSetBLCConfig(BaseResult bean) {
        //TODO 设置背光补偿成功
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (bean != null && bean.isResult()) {
            if ("off".equals(ivDevBlc.getTag())) {
                ivDevBlc.setImageResource(R.mipmap.st_switch_on);
                ivDevBlc.setTag("on");
            } else {
                ivDevBlc.setImageResource(R.mipmap.st_switch_off);
                ivDevBlc.setTag("off");
            }
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_successfully));
        } else {
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_failed));
        }
    }

    @Override
    public void onSetVideoInOptions(BaseResult bean) {
        //TODO 设置设备红外灯、镜像、翻转配置
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (bean != null && bean.isResult()) {

            if ("down".equals(ivCameraPicture.getTag()) && !mVideoInfo.isFlip()) {
                // 设置为正着放成功
                ivCameraPicture.setTag("up");
                ivCameraPicture.setImageResource(R.mipmap.camera_picture_up);
            } else if ("up".equals(ivCameraPicture.getTag()) && mVideoInfo.isFlip()) {
                // 设置为倒着放成功
                ivCameraPicture.setTag("down");
                ivCameraPicture.setImageResource(R.mipmap.camera_picture_down);
            } else if ("off".equals(ivNightVisionMode.getTag()) && mVideoInfo.getDayNightColor() == 3) {
                // 关闭-->开启 设置成功
                ivNightVisionMode.setTag("on");
                ivNightVisionMode.setImageResource(R.mipmap.set_icon_night_on);
            } else if ("on".equals(ivNightVisionMode.getTag()) && mVideoInfo.getDayNightColor() == 1) {
                // 开启-->自动 设置成功
                ivNightVisionMode.setTag("auto");
                ivNightVisionMode.setImageResource(R.mipmap.set_icon_night_auto);
            } else {
                // 自动-->关闭 设置成功
                ivNightVisionMode.setTag("off");
                ivNightVisionMode.setImageResource(R.mipmap.set_icon_night_off);
            }
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_successfully));
        } else {
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_failed));
        }
    }

    @Override
    public void onSetAlarmAsossiatedConfig(BaseResult bean) {
        //TODO 设置枪机声光模式
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (bean != null && bean.isResult()) {
            if ("off".equals(ivAlertTone.getTag())) {
                ivAlertTone.setImageResource(R.mipmap.st_switch_on);
                ivAlertTone.setTag("on");
            } else {
                ivAlertTone.setImageResource(R.mipmap.st_switch_off);
                ivAlertTone.setTag("off");
            }
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_successfully));
        } else {
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_failed));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onSetNetLight(BaseResult bean) {
        // TODO 设置呼吸灯开关成功
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (bean != null && bean.isResult()) {
            if ("off".equals(ivBreathingLightSwitch.getTag())) {
                ivBreathingLightSwitch.setImageResource(R.mipmap.st_switch_on);
                ivBreathingLightSwitch.setTag("on");
            } else {
                ivBreathingLightSwitch.setImageResource(R.mipmap.st_switch_off);
                ivBreathingLightSwitch.setTag("off");
            }
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_successfully));
        } else {
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_failed));
        }
    }
}
