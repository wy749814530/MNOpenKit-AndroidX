package com.mnopensdk.demo.activity.setting;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mn.MnKitAlarmType;
import com.mn.bean.restfull.BaseBean;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.restfull.PushconfigBean;
import com.mn.bean.restfull.PushconfigBean.SleepTimeRangeBean;
import com.mn.bean.setting.BaseResult;
import com.mn.bean.setting.FaceDetectBean;
import com.mn.bean.setting.MotionDetectBean;
import com.mnopensdk.demo.R;
import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.utils.DateUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.utils.UTCTimeUtils;
import com.mnopensdk.demo.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import MNSDK.MNKit;
import MNSDK.MNOpenSDK;
import MNSDK.inface.MNKitInterface;
import MNSDK.inface.MNOpenSDKInterface;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/12/14 0014.
 */

public class MessagePusheActivity extends BaseActivity implements MNKitInterface.GetDevPushconfigCallBack, MNKitInterface.SetDevPushconfigCallBack, MNOpenSDKInterface.SetMotionDetectCallBack, MNOpenSDKInterface.SetFaceDetectCallBack {
    @BindView(R.id.iv_motion_switch)
    ImageView ivMotionSwitch;
    @BindView(R.id.rl_motion_btn_lay)
    RelativeLayout rlMotionBtnLay;
    @BindView(R.id.tv_detection_tip)
    TextView tvDetectionTip;
    @BindView(R.id.ll_motion_level_btn_lay)
    RelativeLayout llMotionLevelBtnLay;
    @BindView(R.id.iv_face_switch)
    ImageView ivFaceSwitch;
    @BindView(R.id.rl_face_btn_lay)
    RelativeLayout rlFaceBtnLay;
    @BindView(R.id.iv_human_body_switch)
    ImageView ivHumanBodySwitch;
    @BindView(R.id.ll_human_body_btn_lay)
    RelativeLayout llHumanBodyBtnLay;
    @BindView(R.id.iv_cry_switch)
    ImageView ivCrySwitch;
    @BindView(R.id.rl_cry_btn_lay)
    RelativeLayout rlCryBtnLay;
    @BindView(R.id.iv_occlusion_switch)
    ImageView ivOcclusionSwitch;
    @BindView(R.id.rl_occlusion_btn_lay)
    RelativeLayout rlOcclusionBtnLay;
    @BindView(R.id.tv_pushtime)
    TextView tvPushtime;
    @BindView(R.id.rl_setpushtimeLay)
    RelativeLayout rlSetpushtimeLay;


    private DevicesBean mDevice;

    private boolean motionDetectSwitch = false;
    private int motionSensitivity = 0;
    private boolean isFaceDetection = false;
    private LoadingDialog loadingDialog;
    private int alarmOptions;

    @Override
    protected int setViewLayout() {
        return R.layout.activity_message_pushe;
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.alarm_push_settings));
        rlMotionBtnLay.setVisibility(View.GONE);
        llMotionLevelBtnLay.setVisibility(View.GONE); // 移动侦测
        rlFaceBtnLay.setVisibility(View.GONE);         // 人脸识别
        llHumanBodyBtnLay.setVisibility(View.GONE);   //人形检测
        rlCryBtnLay.setVisibility(View.GONE);          // 哭声检测
        rlOcclusionBtnLay.setVisibility(View.GONE);   // 遮挡报警

        loadingDialog = new LoadingDialog(this);
        mDevice = (DevicesBean) getIntent().getSerializableExtra("device_info");
        if (mDevice != null && mDevice.getSupport_ability() != null && mDevice.getSupport_ability().getAlarmAbility() != null) {
            List<DevicesBean.SupportAbilityBean.AlarmAbilityBean> abilityBeanList = mDevice.getSupport_ability().getAlarmAbility();
            for (DevicesBean.SupportAbilityBean.AlarmAbilityBean abilityBean : abilityBeanList) {
                if (abilityBean.getAlarmType() == 3) {
                    // 人脸识别
                    rlFaceBtnLay.setVisibility(View.VISIBLE);         // 人脸识别
                } else if (abilityBean.getAlarmType() == 8) { // 移动侦测
                    rlMotionBtnLay.setVisibility(View.VISIBLE);
                    llMotionLevelBtnLay.setVisibility(View.VISIBLE);
                } else if (abilityBean.getAlarmType() == 11) { // 人形检测
                    llHumanBodyBtnLay.setVisibility(View.VISIBLE);
                } else if (abilityBean.getAlarmType() == 12) { // 哭声检测
                    llHumanBodyBtnLay.setVisibility(View.VISIBLE);
                } else if (abilityBean.getAlarmType() == 13) {// 遮挡报警
                    rlOcclusionBtnLay.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
        if (rlMotionBtnLay.getVisibility() == View.VISIBLE) {
            //移动侦测
            MNOpenSDK.getMotionDetectConfig(mDevice.getSn(), new MNOpenSDKInterface.GetMotionDetectCallBack() {
                @Override
                public void onGetMotionDetect(MotionDetectBean bean) {
                    if (bean != null && bean.isResult() && bean.getParams() != null) {
                        motionSensitivity = bean.getParams().getSensitivity();
                        motionDetectSwitch = bean.getParams().isMotionDetect();
                    }

                    if (motionDetectSwitch) {
                        ivMotionSwitch.setImageResource(R.mipmap.st_switch_on);
                        ivMotionSwitch.setTag("on");
                    } else {
                        ivMotionSwitch.setImageResource(R.mipmap.st_switch_off);
                        ivMotionSwitch.setTag("off");
                    }

                    if (motionSensitivity > 66) {
                        tvDetectionTip.setText(getString(R.string.tv_High_sensitivity));
                        tvDetectionTip.setTag("100");
                    } else if (motionSensitivity > 33) {
                        tvDetectionTip.setText(getString(R.string.tv_Medium_sensitivity));
                        tvDetectionTip.setTag("50");
                    } else {
                        tvDetectionTip.setText(getString(R.string.tv_Low_sensitivity));
                        tvDetectionTip.setTag("1");
                    }
                }
            });
        }

        if (rlFaceBtnLay.getVisibility() == View.VISIBLE) {
            //人脸识别
            MNOpenSDK.getFaceDetectConfig(mDevice.getSn(), new MNOpenSDKInterface.GetFaceDetectCallBack() {
                @Override
                public void onGetFaceDetect(FaceDetectBean bean) {
                    if (bean != null && bean.isResult() && bean.getParams() != null) {
                        isFaceDetection = bean.getParams().isFaceDetection();
                    }

                    if (isFaceDetection) {
                        ivFaceSwitch.setImageResource(R.mipmap.st_switch_on);
                        ivFaceSwitch.setTag("on");
                    } else {
                        ivFaceSwitch.setImageResource(R.mipmap.st_switch_off);
                        ivFaceSwitch.setTag("off");
                    }
                }
            });
        }

        MNKit.getPushConfigWithSN(mDevice.getSn(), 0, this);
        loadingDialog.show();
    }

    @Override
    public void onSetMotionDetect(BaseResult bean) {
        // TODO 移动侦测设置成功
        if (bean != null) {
            if (bean.isResult()) {
                if ("switch".equals(llMotionLevelBtnLay.getTag())) {
                    if ("off".equals(ivMotionSwitch.getTag())) {
                        ivMotionSwitch.setImageResource(R.mipmap.st_switch_on);
                        ivMotionSwitch.setTag("on");
                        motionDetectSwitch = true;
                    } else {
                        ivMotionSwitch.setImageResource(R.mipmap.st_switch_off);
                        ivMotionSwitch.setTag("off");
                        motionDetectSwitch = false;
                    }
                } else {
                    if ("100".equals(tvDetectionTip.getTag())) {
                        tvDetectionTip.setText(getString(R.string.tv_Low_sensitivity));
                        tvDetectionTip.setTag("1");
                        motionSensitivity = 1;
                    } else if ("50".equals(tvDetectionTip.getTag())) {
                        tvDetectionTip.setText(getString(R.string.tv_High_sensitivity));
                        tvDetectionTip.setTag("100");
                        motionSensitivity = 100;
                    } else {
                        tvDetectionTip.setText(getString(R.string.tv_Medium_sensitivity));
                        tvDetectionTip.setTag("50");
                        motionSensitivity = 50;
                    }
                }
            } else {
                if (bean.getCode() == 5005) {
                    ToastUtils.MyToastBottom(getString(R.string.tv_no_alarm_data));
                }
            }
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onSetFaceDetect(BaseResult bean) {
        //TODO 设置人脸识别配置开关成功
        if (bean != null && bean.isResult()) {
            if ("off".equals(ivFaceSwitch.getTag())) {
                ivFaceSwitch.setImageResource(R.mipmap.st_switch_on);
                ivFaceSwitch.setTag("on");
            } else {
                ivFaceSwitch.setImageResource(R.mipmap.st_switch_off);
                ivFaceSwitch.setTag("off");
            }
        } else if (bean != null && bean.getCode() == 5005) {
            ToastUtils.MyToastBottom(getString(R.string.tv_no_alarm_data));
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
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

    @OnClick({R.id.rl_motion_btn_lay, R.id.ll_motion_level_btn_lay, R.id.rl_face_btn_lay, R.id.ll_human_body_btn_lay, R.id.rl_cry_btn_lay, R.id.rl_occlusion_btn_lay, R.id.rl_setpushtimeLay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_motion_btn_lay:
                llMotionLevelBtnLay.setTag("switch");
                loadingDialog.show();
                if ("off".equals(ivMotionSwitch.getTag())) {
                    alarmOptions = alarmOptions | MnKitAlarmType.Motion_Detection;
                    MNOpenSDK.setMotionDetectConfig(mDevice.getSn(), motionSensitivity, true, this);
                } else {
                    alarmOptions = alarmOptions & (~MnKitAlarmType.Motion_Detection);
                    MNOpenSDK.setMotionDetectConfig(mDevice.getSn(), motionSensitivity, false, this);
                }
                if ((alarmOptions & MnKitAlarmType.Motion_Detection) == MnKitAlarmType.Motion_Detection) {
                    Log.i("MessagePush", "移动侦测开");
                } else {
                    Log.i("MessagePush", "移动侦测关");
                }
                MNKit.setPushConfigerationWithSN(mDevice.getSn(), 0, alarmOptions, mSleepenable, mSleepTimeRange, this);
                break;
            case R.id.ll_motion_level_btn_lay:
                loadingDialog.show();
                llMotionLevelBtnLay.setTag("level");
                if (motionSensitivity > 66) {
                    MNOpenSDK.setMotionDetectConfig(mDevice.getSn(), 1, motionDetectSwitch, this);
                } else if (motionSensitivity > 33) {
                    MNOpenSDK.setMotionDetectConfig(mDevice.getSn(), 100, motionDetectSwitch, this);
                } else {
                    MNOpenSDK.setMotionDetectConfig(mDevice.getSn(), 50, motionDetectSwitch, this);
                }
                break;
            case R.id.rl_face_btn_lay:
                loadingDialog.show();
                if ("off".equals(ivFaceSwitch.getTag())) {
                    alarmOptions = alarmOptions | MnKitAlarmType.Face_Detection;
                    MNOpenSDK.setFaceDetectConfig(mDevice.getSn(), true, this);
                } else {
                    alarmOptions = alarmOptions & (~MnKitAlarmType.Face_Detection);
                    MNOpenSDK.setFaceDetectConfig(mDevice.getSn(), false, this);
                }
                MNKit.setPushConfigerationWithSN(mDevice.getSn(), 0, alarmOptions, mSleepenable, mSleepTimeRange, this);
                break;
            case R.id.ll_human_body_btn_lay:
                loadingDialog.show();
                if ("off".equals(ivHumanBodySwitch.getTag())) {
                    alarmOptions = alarmOptions | MnKitAlarmType.Humanoid_Detection;
                    ivHumanBodySwitch.setTag("on");
                    ivHumanBodySwitch.setImageResource(R.mipmap.st_switch_on);
                } else {
                    alarmOptions = alarmOptions & (~MnKitAlarmType.Humanoid_Detection);
                    ivHumanBodySwitch.setTag("off");
                    ivHumanBodySwitch.setImageResource(R.mipmap.st_switch_off);
                }
                MNKit.setPushConfigerationWithSN(mDevice.getSn(), 0, alarmOptions, mSleepenable, mSleepTimeRange, this);
                break;
            case R.id.rl_cry_btn_lay:
                loadingDialog.show();
                if ("off".equals(ivCrySwitch.getTag())) {
                    alarmOptions = alarmOptions | MnKitAlarmType.Cry_Detection;
                    ivCrySwitch.setTag("on");
                    ivCrySwitch.setImageResource(R.mipmap.st_switch_on);
                } else {
                    alarmOptions = alarmOptions & (~MnKitAlarmType.Cry_Detection);
                    ivCrySwitch.setTag("off");
                    ivCrySwitch.setImageResource(R.mipmap.st_switch_off);
                }
                MNKit.setPushConfigerationWithSN(mDevice.getSn(), 0, alarmOptions, mSleepenable, mSleepTimeRange, this);
                break;
            case R.id.rl_occlusion_btn_lay:
                loadingDialog.show();
                if ("off".equals(ivOcclusionSwitch.getTag())) {
                    alarmOptions = alarmOptions | MnKitAlarmType.Occlusion_Detection;
                    ivOcclusionSwitch.setTag("on");
                    ivOcclusionSwitch.setImageResource(R.mipmap.st_switch_on);
                } else {
                    alarmOptions = alarmOptions & (~MnKitAlarmType.Occlusion_Detection);
                    ivOcclusionSwitch.setTag("off");
                    ivOcclusionSwitch.setImageResource(R.mipmap.st_switch_off);
                }
                MNKit.setPushConfigerationWithSN(mDevice.getSn(), 0, alarmOptions, mSleepenable, mSleepTimeRange, this);
                break;
            case R.id.rl_setpushtimeLay:
                Intent intent = new Intent(this, MessagePushTimeActivity.class);
                intent.putExtra("sn", mDevice.getSn());
                startActivityForResult(intent, 1000);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == 200) {
            MNKit.getPushConfigWithSN(mDevice.getSn(), 0, this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSetDevPushconfigSuc(BaseBean response) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (response.getCode() == 5005) {
            ToastUtils.MyToastBottom(getString(R.string.tv_restricted_permission));
        }
    }

    @Override
    public void onSetDevPushconfigFailed(String msg) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        ToastUtils.MyToastBottom(getString(R.string.net_err_and_try));
    }

    @Override
    public void onGetDevPushconfigFailed(String msg) {
        ToastUtils.MyToastBottom(getString(R.string.net_err_and_try));
    }

    @Override
    public void onGetDevPushconfigSuc(PushconfigBean response) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        setGetDevCfgV3Suc(response);
    }

    private int mLevel = 1, mSleepenable = 0;
    private ArrayList<SleepTimeRangeBean> mSleepTimeRange = new ArrayList<>();

    public void setGetDevCfgV3Suc(PushconfigBean response) {

        alarmOptions = response.getAlarmTypeOptions();
        mLevel = response.getLevel();
        mSleepenable = response.getSleepenable();
        mSleepTimeRange.clear();
        mSleepTimeRange.addAll(response.getSleep_time_range());

        if (mSleepenable == 0) {
            tvPushtime.setText(getString(R.string.all_day_detection));
        } else if (mSleepTimeRange.size() == 1) {
            PushconfigBean.SleepTimeRangeBean sleepTimeStart = mSleepTimeRange.get(0);
            String sleep1Etime = UTCTimeUtils.getLocalZoneTimeString(sleepTimeStart.getEsleeptime(), DateUtil.DEFAULT_TIME_FORMAT);
            String sleep1Btime = UTCTimeUtils.getLocalZoneTimeString(sleepTimeStart.getBsleeptime(), DateUtil.DEFAULT_TIME_FORMAT);
            setPushTimeView(sleep1Etime, sleep1Btime);
        } else if (mSleepTimeRange.size() == 2) {

            PushconfigBean.SleepTimeRangeBean timeBean0 = mSleepTimeRange.get(0);
            PushconfigBean.SleepTimeRangeBean timeBean1 = mSleepTimeRange.get(1);
            String bsleeptime = "";
            String esleeptime = "";
            if ("00:00:00".equals(timeBean0.getBsleeptime()) && "23:59:59".equals(timeBean1.getEsleeptime())) {
                bsleeptime = timeBean0.getEsleeptime();
                esleeptime = timeBean1.getBsleeptime();
            } else {
                bsleeptime = timeBean1.getEsleeptime();
                esleeptime = timeBean0.getBsleeptime();
            }
            String localStartTime = UTCTimeUtils.getLocalZoneTimeString(bsleeptime, DateUtil.DEFAULT_TIME_FORMAT);
            String localEndTime = UTCTimeUtils.getLocalZoneTimeString(esleeptime, DateUtil.DEFAULT_TIME_FORMAT);
            setPushTimeView(localStartTime, localEndTime);
        }

        if ((alarmOptions | MnKitAlarmType.Humanoid_Detection) == MnKitAlarmType.Humanoid_Detection) {
            ivHumanBodySwitch.setTag("on");
            ivHumanBodySwitch.setImageResource(R.mipmap.st_switch_on);
        } else {
            ivHumanBodySwitch.setTag("off");
            ivHumanBodySwitch.setImageResource(R.mipmap.st_switch_off);
        }

        if ((alarmOptions | MnKitAlarmType.Cry_Detection) == MnKitAlarmType.Cry_Detection) {
            ivCrySwitch.setTag("on");
            ivCrySwitch.setImageResource(R.mipmap.st_switch_on);
        } else {
            ivCrySwitch.setTag("off");
            ivCrySwitch.setImageResource(R.mipmap.st_switch_off);
        }

        if ((alarmOptions | MnKitAlarmType.Occlusion_Detection) == MnKitAlarmType.Occlusion_Detection) {
            ivOcclusionSwitch.setTag("on");
            ivOcclusionSwitch.setImageResource(R.mipmap.st_switch_on);
        } else {
            ivOcclusionSwitch.setTag("off");
            ivOcclusionSwitch.setImageResource(R.mipmap.st_switch_off);
        }
    }

    private void setPushTimeView(String startLocalTime, String endLoaclTime) {
        if ("08:00:00".equals(startLocalTime) && "19:59:59".equals(endLoaclTime)) {
            tvPushtime.setText(getString(R.string.daytime_detection_only));
        } else if ("20:00:00".equals(startLocalTime) && "07:59:59".equals(endLoaclTime)) {
            tvPushtime.setText(getString(R.string.push_at_night));
        } else {
            int startHIndex = 0, startMIndex = 0, endHIndex = 0, endMIndex = 0;
            String[] startTime = startLocalTime.split(":");
            if (startTime.length == 3) {
                startHIndex = Integer.valueOf(startTime[0]);
                startMIndex = Integer.valueOf(startTime[1]);
            }
            String[] endTime = endLoaclTime.split(":");
            if (endTime.length == 3) {
                endHIndex = Integer.valueOf(endTime[0]);
                endMIndex = Integer.valueOf(endTime[1]);
            }
            String strTime = String.format("%02d:%02d - %02d:%02d", startHIndex, startMIndex, endHIndex, endMIndex);
            tvPushtime.setText(strTime);
        }
    }
}
