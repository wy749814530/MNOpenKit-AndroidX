package com.mnopensdk.demo.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mnopensdk.demo.base.BaseFragment;
import com.mnopensdk.demo.event.RefreshNvrIpcExistEvent;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.setting.AlarmTimeRecordBean;
import com.mn.bean.setting.AlarmTimeRecordNvrBean;
import com.mn.bean.setting.BaseResult;
import com.mn.bean.setting.DevSetMoreBaseBean;
import com.mn.bean.setting.SetAlarmRecordBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import MNSDK.MNOpenSDK;
import MNSDK.inface.MNOpenSDKInterface;
import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2019/7/11 0011.
 */

public class DevNvrTfRecordSetFragment extends BaseFragment implements View.OnClickListener, MNOpenSDKInterface.SetAlarmRecordCallBack, MNOpenSDKInterface.SetNvrAlarmRecordCallBack {

    TextView tvViright;
    RelativeLayout tfStorgeConfig;
    TextView tvEventRecord;
    TextView tvRefreshDevExist;
    ImageView ivEventAlarm;
    RelativeLayout rlEventAlarmLay;
    TextView tv24;
    TextView tv24hourRecord;
    ImageView ivContinuous;
    RelativeLayout rlContinuousAlarmLay;
    RelativeLayout rlNoDevLay;
    LinearLayout llRecordLay;

    private DevicesBean mDevicesBean;
    private int mChannelId = 0;
    private boolean isAllDayRecord = false, isNullRecord = true;
    private boolean mIpcIsExist = true;
    private LoadingDialog loadingDialog;
    private RefreshNvrIpcExistEvent existEvent;

    public static DevNvrTfRecordSetFragment newInstance(DevicesBean devicesBean, int channelId) {
        Bundle args = new Bundle();
        args.putSerializable("devicesBean", devicesBean);
        args.putInt("channelId", channelId);
        DevNvrTfRecordSetFragment fragment = new DevNvrTfRecordSetFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getViewLayoutId() {
        return R.layout.fragment_dev_nvr_tf_record_set;
    }

    @Override
    protected void initView() {
        mDevicesBean = (DevicesBean) getArguments().getSerializable("devicesBean");
        mChannelId = getArguments().getInt("channelId");

        tvViright = thisView.findViewById(R.id.tv_Viright);
        tfStorgeConfig = thisView.findViewById(R.id.tf_storge_config);
        tvEventRecord = thisView.findViewById(R.id.tv_event_record);
        ivEventAlarm = thisView.findViewById(R.id.iv_event_alarm);
        rlEventAlarmLay = thisView.findViewById(R.id.rl_event_alarm_lay);
        tv24 = thisView.findViewById(R.id.tv_24);
        tv24hourRecord = thisView.findViewById(R.id.tv_24hour_record);
        ivContinuous = thisView.findViewById(R.id.iv_continuous);
        rlContinuousAlarmLay = thisView.findViewById(R.id.rl_continuous_alarm_lay);
        rlNoDevLay = thisView.findViewById(R.id.rl_no_dev_lay);
        llRecordLay = thisView.findViewById(R.id.ll_record_lay);
        tvRefreshDevExist = thisView.findViewById(R.id.tv_refresh_devExist);

        tvEventRecord.setText(getString(R.string.recording_when_saving_alarm_hark));
        tv24hourRecord.setText(getString(R.string.full_day_continuous_recording_hark));

        loadingDialog = new LoadingDialog(getContext()).setTimeOut(15 * 1000);

        rlEventAlarmLay.setOnClickListener(this);
        rlContinuousAlarmLay.setOnClickListener(this);
        tvRefreshDevExist.setOnClickListener(this);
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void initLoadDate() {
        if (mDevicesBean != null) {
            loadingDialog.show();
            onRefresh();
        }
    }

    @Override
    protected void onViewResume() {

    }

    public void onRefresh() {
        if (mDevicesBean.getType() == 4) {
            if (mIpcIsExist) {
                int[] channels = new int[]{mChannelId};
                llRecordLay.setVisibility(View.VISIBLE);
                rlNoDevLay.setVisibility(View.GONE);
                /**
                 *获取NVR设备TF卡存储类型和时间配置
                 */
                MNOpenSDK.getNvrAlarmRecord(mDevicesBean.getSn(), channels, new MNOpenSDKInterface.GetNvrAlarmRecordCallBack() {
                    @Override
                    public void onGetNvrAlarmRecord(AlarmTimeRecordNvrBean recordBean) {
                        AlarmTimeRecordBean lNvrBean = null;
                        if (recordBean == null || !recordBean.isResult() || recordBean.getParams() == null || recordBean.getParams().size() == 0) {
                            isNullRecord = true;
                            isAllDayRecord = false;
                            ToastUtils.MyToastBottom(getString(R.string.settings_failed));
                        } else {
                            lNvrBean = null;
                            for (AlarmTimeRecordBean nvrBean : recordBean.getParams()) {
                                if (nvrBean.isResult() && nvrBean.getParams() != null) {
                                    if (lNvrBean == null) {
                                        lNvrBean = nvrBean;
                                        isNullRecord = false;
                                        isAllDayRecord = nvrBean.getParams().isAllDayRecord();
                                    } else {
                                        if (lNvrBean.getParams().isAllDayRecord() != nvrBean.getParams().isAllDayRecord()) {
                                            isNullRecord = true;
                                        }
                                    }
                                }
                            }
                            if (lNvrBean == null) {
                                isNullRecord = true;
                            }
                        }
                        setTfCardVideoState();
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                    }
                });
            } else {
                llRecordLay.setVisibility(View.GONE);
                rlNoDevLay.setVisibility(View.VISIBLE);
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }
        } else {
            /**
             *获取设备TF卡存储类型和时间配置
             */
            MNOpenSDK.getAlarmRecord(mDevicesBean.getSn(), new MNOpenSDKInterface.GetAlarmRecordCallBack() {
                @Override
                public void onGetAlarmRecord(AlarmTimeRecordBean recordBean) {
                    if (recordBean != null) {
                        if (recordBean.isResult() && recordBean.getParams() != null) {
                            isAllDayRecord = recordBean.getParams().isAllDayRecord();
                            isNullRecord = false;
                        } else {
                            isNullRecord = true;
                        }
                        setTfCardVideoState();
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setDeviceIsExist() {
        mIpcIsExist = true;
        if (llRecordLay != null && mDevicesBean != null && mDevicesBean.getType() == 4) {
            llRecordLay.setVisibility(View.VISIBLE);
            rlNoDevLay.setVisibility(View.GONE);
        }
    }

    public void setDeviceNotExistOrUnsupport() {
        mIpcIsExist = false;
        if (llRecordLay != null && mDevicesBean != null && mDevicesBean.getType() == 4) {
            llRecordLay.setVisibility(View.GONE);
            rlNoDevLay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSetAlarmRecord(BaseResult bean) {
        // TODO 设置录像时间成功
        if (bean != null && bean.isResult()) {
            setTfCardVideoState();
        } else {
            ToastUtils.MyToastBottom(getString(R.string.settings_failed));
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }

        if (isFormatTfCard) {
            isFormatTfCard = false;
            EventBus.getDefault().post("format_event");
        }
    }

    @Override
    public void onSetNvrAlarmRecord(DevSetMoreBaseBean bean) {
        if (bean != null && bean.isResult()) {
            setTfCardVideoState();
        } else {
            ToastUtils.MyToastBottom(getString(R.string.settings_failed));
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    private void setTfCardVideoState() {
        if (isAllDayRecord) {
            // 全天录像
            tvViright.setText(getString(R.string.full_day_recording));
            tvViright.setVisibility(View.GONE);
            ivEventAlarm.setVisibility(View.INVISIBLE);
            ivContinuous.setVisibility(View.VISIBLE);
        } else {
            if (isNullRecord == false) {
                // 移动侦测
                ivEventAlarm.setVisibility(View.VISIBLE);
                ivContinuous.setVisibility(View.INVISIBLE);
                tvViright.setText(getString(R.string.event_recording));
                tvViright.setVisibility(View.GONE);
            } else {
                tvViright.setText(getString(R.string.recording_mode_not_set));
                tvViright.setVisibility(View.VISIBLE);
                ivEventAlarm.setVisibility(View.INVISIBLE);
                ivContinuous.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_event_alarm_lay:
                isNullRecord = false;
                isAllDayRecord = false;
                tfStorgeConfig.setTag("event_alarm");
                setAlarmRecord();
                break;
            case R.id.rl_continuous_alarm_lay:
                isNullRecord = false;
                isAllDayRecord = true;
                tfStorgeConfig.setTag("continuous_alarm");
                setAlarmRecord();
                break;
            case R.id.tv_refresh_devExist:
                if (existEvent == null) {
                    existEvent = new RefreshNvrIpcExistEvent();
                }
                EventBus.getDefault().post(existEvent);
                break;
        }
    }

    public void setAlarmRecord() {
        loadingDialog.setTimeOut(15 * 1000);
        loadingDialog.show();
        if (mDevicesBean.getType() == 4) {
            // @TODO NVR 配置设置
            ArrayList<SetAlarmRecordBean> setAlarmRecordBeans = new ArrayList<>();
            SetAlarmRecordBean recordBean = new SetAlarmRecordBean();
            recordBean.setChannel(mChannelId);
            recordBean.setAllDayRecord(isAllDayRecord);
            setAlarmRecordBeans.add(recordBean);

            MNOpenSDK.setNvrAlarmRecord(mDevicesBean.getSn(), setAlarmRecordBeans, this);
        } else {
            MNOpenSDK.setAlarmRecord(mDevicesBean.getSn(), isAllDayRecord, this);
        }
    }

    private boolean isFormatTfCard = false;

    public void setAlarmRecordAndFormat() {
        isFormatTfCard = true;
        isNullRecord = false;
        isAllDayRecord = true;
        tfStorgeConfig.setTag("continuous_alarm");
        setAlarmRecord();
    }
}
