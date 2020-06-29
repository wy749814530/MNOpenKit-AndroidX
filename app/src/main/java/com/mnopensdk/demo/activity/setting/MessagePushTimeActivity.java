package com.mnopensdk.demo.activity.setting;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mn.MnKitAlarmType;
import com.mn.bean.restfull.PushconfigBean;
import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.utils.DateUtil;
import com.mnopensdk.demo.utils.LogUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.utils.UTCTimeUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mnopensdk.demo.widget.TimePushPopWindow;
import com.mn.bean.restfull.BaseBean;
import com.mn.bean.restfull.PushconfigBean.PushListBean;
import com.mn.bean.restfull.PushconfigBean.SleepTimeRangeBean;


import java.util.ArrayList;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.OnClick;

import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2019/12/24 0024.
 */

public class MessagePushTimeActivity extends BaseActivity implements TimePushPopWindow.OnSelectedListener, MNKitInterface.GetDevPushconfigCallBack, MNKitInterface.SetDevPushconfigCallBack {
    private String TAG = MessagePushTimeActivity.class.getSimpleName();
    @BindView(R.id.iv_left_icon)
    ImageView ivLeftIcon;
    @BindView(R.id.iv_select_1)
    ImageView ivSelect1;
    @BindView(R.id.rlLay1)
    RelativeLayout rlLay1;
    @BindView(R.id.iv_left_icon2)
    ImageView ivLeftIcon2;
    @BindView(R.id.iv_select_2)
    ImageView ivSelect2;
    @BindView(R.id.rlLay2)
    RelativeLayout rlLay2;
    @BindView(R.id.iv_left_icon4)
    ImageView ivLeftIcon4;
    @BindView(R.id.iv_select_4)
    ImageView ivSelect4;
    @BindView(R.id.rlLay4)
    RelativeLayout rlLay4;
    @BindView(R.id.iv_left_icon3)
    ImageView ivLeftIcon3;
    @BindView(R.id.tv3_content)
    TextView tv3Content;
    @BindView(R.id.iv_select_3)
    ImageView ivSelect3;
    @BindView(R.id.rlLay3)
    RelativeLayout rlLay3;

    TimePushPopWindow timePopWindow;
    LoadingDialog progressHUD;
    private String mSn;
    private int mSensitivity = 1, mSleepenable = 0;
    private int alarmTypeOptions = 0;
    private ArrayList<SleepTimeRangeBean> mSleepTimeRange = new ArrayList<>();
    SleepTimeRangeBean sleepTime1;
    SleepTimeRangeBean sleepTime2;

    private int startHIndex, startMIndex, endHIndex, endMIndex;
    private boolean hadChanged = false;

    @Override
    protected int setViewLayout() {
        return R.layout.activity_message_push_time;
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.push_time_title));
        mSn = getIntent().getStringExtra("sn");

        progressHUD = new LoadingDialog(this);
        progressHUD.show();

        MNKit.getPushConfigWithSN(mSn, 0, this);
    }

    @Override
    protected void initViews() {
        ivSelect1.setVisibility(View.INVISIBLE);
        ivSelect2.setVisibility(View.INVISIBLE);
        ivSelect3.setVisibility(View.INVISIBLE);

        timePopWindow = new TimePushPopWindow(this);
        timePopWindow.setOnSelectedListener(this);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void onBackKeyDown(boolean b) {
        if (hadChanged) {
            setResult(200);
        }
        finish();
    }

    @Override
    protected void onRightTvClick() {

    }

    @Override
    protected void onLeftTvClick() {

    }

    private int action = 0;

    @OnClick({R.id.rlLay1, R.id.rlLay2, R.id.rlLay3, R.id.rlLay4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlLay1:
                action = 1;
                mSleepenable = 0;
                mSleepTimeRange.clear();
                progressHUD.show();
                MNKit.setPushConfigerationWithSN(mSn, 0, alarmTypeOptions, mSleepenable, mSleepTimeRange, this);
                setPopWindowIndex("00:00:00", "00:00:00");
                break;
            case R.id.rlLay2:
                action = 2;
                setPushTimeConfig("08:00:00", "19:59:59");// 白天推送
                break;
            case R.id.rlLay3:
                action = 3;
                timePopWindow.showPopupWindow(startHIndex, startMIndex, endHIndex, endMIndex);
                break;
            case R.id.rlLay4:
                action = 4;
                setPushTimeConfig("20:00:00", "07:59:59");// 夜间推送
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setPushTimeConfig(String startLocalTime, String endLoaclTime) {
        mSleepenable = 1;
        sleepTime1 = null;
        sleepTime2 = null;
        mSleepTimeRange.clear();
        String utc_startETime = UTCTimeUtils.getUTCTimeByLocalTime(startLocalTime, DateUtil.DEFAULT_TIME_FORMAT);
        String[] start_hms = utc_startETime.split(":");
        long utc_startEndTime = 0;
        if (start_hms.length == 3) {
            utc_startEndTime = Integer.valueOf(start_hms[0]) * 60 + Integer.valueOf(start_hms[1]);
        }

        String utc_endBTime = UTCTimeUtils.getUTCTimeByLocalTime(endLoaclTime, DateUtil.DEFAULT_TIME_FORMAT);
        String[] end_hms = utc_endBTime.split(":");
        long utc_EndBTime = 0;
        if (end_hms.length == 3) {
            utc_EndBTime = Integer.valueOf(end_hms[0]) * 60 + Integer.valueOf(end_hms[1]);
        }

        LogUtil.i(TAG, "utc_startETime = " + utc_startETime);
        LogUtil.i(TAG, "utc_endBTime = " + utc_endBTime);
        sleepTime1 = new SleepTimeRangeBean();
        if (utc_EndBTime < utc_startEndTime) {
            sleepTime1.setBsleeptime(utc_endBTime);
            sleepTime1.setEsleeptime(utc_startETime);
            mSleepTimeRange.add(sleepTime1);
        } else {
            String utc_startBTime = "00:00:00";
            sleepTime1.setBsleeptime(utc_startBTime);
            sleepTime1.setEsleeptime(utc_startETime);
            mSleepTimeRange.add(sleepTime1);

            sleepTime2 = new SleepTimeRangeBean();
            String utc_endETime = "23:59:59";
            sleepTime2.setBsleeptime(utc_endBTime);
            sleepTime2.setEsleeptime(utc_endETime);
            mSleepTimeRange.add(sleepTime2);
        }
        setPopWindowIndex(startLocalTime, endLoaclTime);
        setPushconfig();
    }

    public void setPushconfig() {
        hadChanged = true;
        progressHUD.show();
        MNKit.setPushConfigerationWithSN(mSn, 0, alarmTypeOptions, mSleepenable, mSleepTimeRange, this);
    }

    private void setSelectView() {
        if (action == 0) {
            ToastUtils.MyToastCenter(getString(R.string.net_noperfect));
        } else if (action == 1) {
            ivSelect1.setVisibility(View.VISIBLE);
            ivSelect2.setVisibility(View.INVISIBLE);
            ivSelect3.setVisibility(View.INVISIBLE);
            ivSelect4.setVisibility(View.INVISIBLE);
            tv3Content.setText(getString(R.string.choose_the_time_detection));
        } else if (action == 2) {
            ivSelect1.setVisibility(View.INVISIBLE);
            ivSelect2.setVisibility(View.VISIBLE);
            ivSelect3.setVisibility(View.INVISIBLE);
            ivSelect4.setVisibility(View.INVISIBLE);
            tv3Content.setText(getString(R.string.choose_the_time_detection));
        } else if (action == 3) {

            if (mSleepenable == 0) {
                ivSelect1.setVisibility(View.VISIBLE);
                ivSelect2.setVisibility(View.INVISIBLE);
                ivSelect3.setVisibility(View.INVISIBLE);
                ivSelect4.setVisibility(View.INVISIBLE);
                tv3Content.setText(getString(R.string.choose_the_time_detection));
            } else {
                if (sleepTime2 == null) {
                    String sleep1Etime = UTCTimeUtils.getLocalZoneTimeString(sleepTime1.getEsleeptime(), DateUtil.DEFAULT_TIME_FORMAT);
                    String sleep1Btime = UTCTimeUtils.getLocalZoneTimeString(sleepTime1.getBsleeptime(), DateUtil.DEFAULT_TIME_FORMAT);
                    setPushTimeView(sleep1Etime, sleep1Btime);
                } else {
                    String sleep1Etime = UTCTimeUtils.getLocalZoneTimeString(sleepTime1.getEsleeptime(), DateUtil.DEFAULT_TIME_FORMAT);
                    String sleep2Btime = UTCTimeUtils.getLocalZoneTimeString(sleepTime2.getBsleeptime(), DateUtil.DEFAULT_TIME_FORMAT);
                    setPushTimeView(sleep1Etime, sleep2Btime);
                }
            }
        } else if (action == 4) {
            ivSelect1.setVisibility(View.INVISIBLE);
            ivSelect2.setVisibility(View.INVISIBLE);
            ivSelect3.setVisibility(View.INVISIBLE);
            ivSelect4.setVisibility(View.VISIBLE);
            tv3Content.setText(getString(R.string.choose_the_time_detection));
        }
    }

    @Override
    public void OnSelected(String startHour, String startMinute, String endHour, String endMinute) {
        setPushTimeConfig(startHour + ":" + startMinute + ":00", endHour + ":" + endMinute + ":59");
    }

    @Override
    public void onSetDevPushconfigSuc(BaseBean response) {
        if (progressHUD != null) {
            progressHUD.dismiss();
        }
        if (response.getCode() == 2000) {
            setSelectView();
        } else if (response.getCode() == 5005) {
            ToastUtils.MyToastBottom(getString(R.string.tv_restricted_permission));
        }
    }

    @Override
    public void onSetDevPushconfigFailed(String msg) {
        if (progressHUD != null) {
            progressHUD.dismiss();
        }
        ToastUtils.MyToastCenter(getString(R.string.net_noperfect));
    }

    @Override
    public void onGetDevPushconfigFailed(String msg) {
        if (progressHUD != null) {
            progressHUD.dismiss();
        }
        ToastUtils.MyToastCenter(getString(R.string.net_noperfect));
    }

    @Override
    public void onGetDevPushconfigSuc(PushconfigBean response) {
        if (progressHUD != null) {
            progressHUD.dismiss();
        }

        alarmTypeOptions = response.getAlarmTypeOptions();
        mSensitivity = response.getLevel();
        mSleepenable = response.getSleepenable();
        mSleepTimeRange.clear();
        mSleepTimeRange.addAll(response.getSleep_time_range());

        mSleepenable = response.getSleepenable();
        mSleepTimeRange.clear();
        mSleepTimeRange.addAll(response.getSleep_time_range());

        if (mSleepenable == 0 || mSleepTimeRange.size() == 0) {
            ivSelect1.setVisibility(View.VISIBLE);
            ivSelect2.setVisibility(View.INVISIBLE);
            ivSelect3.setVisibility(View.INVISIBLE);
            startHIndex = 0;
            startMIndex = 0;
            endHIndex = 0;
            endMIndex = 0;
            return;
        }
        if (mSleepTimeRange.size() == 1) {
            SleepTimeRangeBean sleepTimeStart = mSleepTimeRange.get(0);
            String sleep1Etime = UTCTimeUtils.getLocalZoneTimeString(sleepTimeStart.getEsleeptime(), DateUtil.DEFAULT_TIME_FORMAT);
            String sleep1Btime = UTCTimeUtils.getLocalZoneTimeString(sleepTimeStart.getBsleeptime(), DateUtil.DEFAULT_TIME_FORMAT);
            setPushTimeView(sleep1Etime, sleep1Btime);
        } else if (mSleepTimeRange.size() == 2) {
            SleepTimeRangeBean timeBean0 = mSleepTimeRange.get(0);
            SleepTimeRangeBean timeBean1 = mSleepTimeRange.get(1);
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
    }

    private void setPushTimeView(String startLocalTime, String endLoaclTime) {
        if ("08:00:00".equals(startLocalTime) && "19:59:59".equals(endLoaclTime)) {
            LogUtil.i(TAG, "sleep1Etime : " + startLocalTime);
            LogUtil.i(TAG, "sleep2Btime : " + endLoaclTime);
            ivSelect1.setVisibility(View.INVISIBLE);
            ivSelect2.setVisibility(View.VISIBLE);
            ivSelect3.setVisibility(View.INVISIBLE);
            ivSelect4.setVisibility(View.INVISIBLE);
            tv3Content.setText(getString(R.string.choose_the_time_detection));
        } else if ("20:00:00".equals(startLocalTime) && "07:59:59".equals(endLoaclTime)) {
            ivSelect1.setVisibility(View.INVISIBLE);
            ivSelect2.setVisibility(View.INVISIBLE);
            ivSelect3.setVisibility(View.INVISIBLE);
            ivSelect4.setVisibility(View.VISIBLE);
            tv3Content.setText(getString(R.string.choose_the_time_detection));
        } else {
            ivSelect1.setVisibility(View.INVISIBLE);
            ivSelect2.setVisibility(View.INVISIBLE);
            ivSelect3.setVisibility(View.VISIBLE);
            ivSelect4.setVisibility(View.INVISIBLE);

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
            tv3Content.setText(getString(R.string.custom_time_period) + strTime);
        }

        setPopWindowIndex(startLocalTime, endLoaclTime);
    }


    public void setPopWindowIndex(String startLocalTime, String endLoaclTime) {
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
        LogUtil.i("TimePushPopWindow", "setPushTimeView :" + startHIndex + " : " + startMIndex + " -- " + endHIndex + " : " + endMIndex);
    }
}
