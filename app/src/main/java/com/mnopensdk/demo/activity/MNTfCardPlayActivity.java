package com.mnopensdk.demo.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mnopensdk.demo.Constants;
import com.mnopensdk.demo.utils.DateUtil;
import com.mnopensdk.demo.utils.DensityUtil;
import com.mnopensdk.demo.utils.LocalVariable;
import com.mnopensdk.demo.utils.LogUtil;
import com.mnopensdk.demo.utils.TFCardUtils;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.setting.TFStateBean;
import com.mn.player.MNPlayControl;
import com.mn.player.MNPlayControlLinstener;
import com.ruler.RulerView;
import com.ruler.bean.OnBarMoveListener;
import com.ruler.bean.TimeSectionBean;
import com.ruler.bean.TimeSlot;
import com.ruler.utils.RulerCardCacheUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import MNSDK.MNOpenSDK;
import MNSDK.inface.MNOpenSDKInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import com.mnopensdk.demo.R;

import static MNSDK.MNJni.MNTaskStatusCode.MNTASK_STATUS_CODE_t.MNTS_CANCELING;
import static MNSDK.MNJni.MNTaskStatusCode.MNTASK_STATUS_CODE_t.MNTS_CONNECTING;
import static MNSDK.MNJni.MNTaskStatusCode.MNTASK_STATUS_CODE_t.MNTS_CONNECT_FAILED;
import static MNSDK.MNJni.MNTaskStatusCode.MNTASK_STATUS_CODE_t.MNTS_DESTROYED;
import static MNSDK.MNJni.MNTaskStatusCode.MNTASK_STATUS_CODE_t.MNTS_DEVICE_OFFLINE;
import static MNSDK.MNJni.MNTaskStatusCode.MNTASK_STATUS_CODE_t.MNTS_PAUSED;
import static MNSDK.MNJni.MNTaskStatusCode.MNTASK_STATUS_CODE_t.MNTS_READY;
import static MNSDK.MNJni.MNTaskStatusCode.MNTASK_STATUS_CODE_t.MNTS_RUNNING;
import static MNSDK.MNJni.MNTaskStatusCode.MNTASK_STATUS_CODE_t.MNTS_STOPPED;
import static MNSDK.MNJni.MNTaskType.MNAV_TASK_TYPE_t.MTT_CLOUD_ALARM;
import static MNSDK.MNJni.MNTaskType.MNAV_TASK_TYPE_t.MTT_P2P_REALPLAY;
import static MNSDK.MNJni.MNTaskType.MNAV_TASK_TYPE_t.MTT_P2P_SD_PLAYBACK;

/**
 * Created by Administrator on 2018/8/30 0030.
 */

public class MNTfCardPlayActivity extends Activity implements MNOpenSDKInterface.DeviceLocalVideosCallBack {
    private String TAG = MNTfCardPlayActivity.class.getSimpleName();
    @BindView(R.id.iv_shrink)
    ImageView ivShrink;
    @BindView(R.id.iv_full)
    ImageView ivFull;
    @BindView(R.id.before_day)
    TextView beforeDay;
    @BindView(R.id.backward_day)
    TextView backwardDay;
    @BindView(R.id.rl_main_lay)
    RelativeLayout rlMainLay;
    @BindView(R.id.mn_play_control)
    MNPlayControl mnPlayControl;
    @BindView(R.id.btn_startRecord)
    ImageView btnStartRecord;
    @BindView(R.id.btn_screenshot)
    ImageView btnScreenshot;
    @BindView(R.id.btnVoice)
    ImageView btnVoice;
    @BindView(R.id.btn_download)
    ImageView btnDownload;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.title_lay)
    RelativeLayout titleLay;
    @BindView(R.id.frame_download_lay)
    FrameLayout frameDownloadLay;
    @BindView(R.id.ll_tools_lay)
    LinearLayout llToolsLay;
    @BindView(R.id.ll_card_alarm_lay)
    LinearLayout llCardAlarmLay;
    @BindView(R.id.rulerView)
    RulerView rulerView;

    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private MainHandler mainHandler = new MainHandler(this);
    private static final int GOTO_PLAY_TIME = 1000;

    //本地存储(是否有卡)
    List<TimeSlot> timeSlots = new ArrayList<>();
    private RulerCardCacheUtils cacheUtils;
    private long mCurrentTime = System.currentTimeMillis();

    private int mStream = 0;
    private int defHeight = 0;
    private boolean isFristInto = true, isFinish = false;
    private boolean isGotoPlayTime = true; // 卡条是否跟随时间走动
    private String mRecordJson = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mn_tf_card_play_activity);
        ButterKnife.bind(this);

        initLinstener();
        initData();
        defHeight = DensityUtil.dip2px(this, 220);
    }

    private DevicesBean mDevice;

    private void initData() {
        mDevice = (DevicesBean) getIntent().getSerializableExtra("device_info");
        mnPlayControl.setDeviceInfo(mDevice);
        initRulerView();
    }

    private void initLinstener() {
        //实现卡录像查询类（Implement card video query class）
        mnPlayControl.setPlayControlLinstener(new MNPlayControlLinstener() {
            @Override
            public void OnTaskStatus(int videoType, int taskStatus, float fProgress) {
                LogUtil.i(TAG, "OnTaskStatus videoType : " + videoType + ", fProgress : " + fProgress);
                if (videoType == MTT_P2P_REALPLAY.ordinal()) {
                    // 直播任务
                    if (taskStatus == MNTS_READY.ordinal()) {
                        Log.i(TAG, "OnTaskStatus 任务 videoType : " + videoType + " -- 准备就绪...");
                    } else if (taskStatus == MNTS_RUNNING.ordinal()) {
                        Log.i(TAG, "OnTaskStatus 任务 videoType : " + videoType + "-- 播放中..." + fProgress);
                    } else if (taskStatus == MNTS_DEVICE_OFFLINE.ordinal()) {
                        Log.i(TAG, "OnTaskStatus 任务 videoType : " + videoType + "-- 设备不在线...");
                    } else if (taskStatus == MNTS_CONNECT_FAILED.ordinal()) {
                        Log.i(TAG, "OnTaskStatus 任务 videoType : " + videoType + "-- 连接失败，请重试...");
                    } else if (taskStatus == MNTS_STOPPED.ordinal()) {
                        Log.i(TAG, "OnTaskStatus 任务 videoType : " + videoType + "-- 结束");
                    } else if (taskStatus == MNTS_DESTROYED.ordinal()) {
                        Log.i(TAG, "OnTaskStatus 任务 videoType : " + videoType + "-- 任务已销毁...");
                    } else if (taskStatus == MNTS_CANCELING.ordinal()) {
                        Log.i(TAG, "OnTaskStatus 任务 videoType : " + videoType + " -- 任务正在取消...");
                    } else if (taskStatus == MNTS_CONNECTING.ordinal()) {
                        Log.i(TAG, "OnTaskStatus 任务 videoType : " + videoType + "-- 正在获取视频...");
                    } else if (taskStatus == MNTS_PAUSED.ordinal()) {
                        Log.i(TAG, "OnTaskStatus 任务 videoType : " + videoType + "-- 暂停");
                    } else {
                        Log.i(TAG, "OnTaskStatus 任务 videoType : " + videoType + "-- 其他任务类型");
                    }
                } else if (videoType == MTT_P2P_SD_PLAYBACK.ordinal()) {
                    // 卡回放
                } else if (videoType == MTT_CLOUD_ALARM.ordinal()) {
                    // 云回放
                }
            }

            @Override
            public void onDownloadTaskStatus(boolean taskStatus, int downStatus, float progress, String fileName) {
                runOnUiThread(() -> {
                    if (taskStatus) {
                        if (downStatus == 0) {
                            // 开始任务
                            LogUtil.i(TAG, "开始下载");
                            tvProgress.setText("0");
                            tvProgress.setVisibility(View.VISIBLE);
                        } else if (downStatus == 1) { // 下载中
                            LogUtil.i(TAG, "下载中... " + progress);
                            tvProgress.setText(String.format("%.02f", progress * 100) + "/%");
                        } else if (downStatus == 2) { // 下载失败
                            LogUtil.i(TAG, "下载失败");
                            tvProgress.setText("失败");
                            new Handler().postDelayed(() -> {
                                tvProgress.setVisibility(View.GONE);
                            }, 800);
                        } else if (downStatus == 3 || downStatus == 30) { // 下载成功
                            LogUtil.i(TAG, "下载成功 ： " + fileName);
                            tvProgress.setText("成功");
                            if (downStatus == 30) {
                                tvProgress.setVisibility(View.VISIBLE);
                            }
                            new Handler().postDelayed(() -> {
                                tvProgress.setVisibility(View.GONE);
                            }, 800);
                        }
                    } else {
                        LogUtil.i(TAG, "下载失败");
                        tvProgress.setText("失败");
                        new Handler().postDelayed(() -> {
                            tvProgress.setVisibility(View.GONE);
                        }, 800);
                    }
                });
            }

            @Override
            public void onSetSessionCtrl(boolean result, String pointId) {

            }

            @Override
            public void onDelSessionCtrl(boolean result, String pointIndex) {
                //删除摇头机预置点
            }

            @Override
            public void onGotoSessionCtrl(boolean result, String pointId) {
                // 前往摇头机预置点
            }

            @Override
            public void runSpeed(int nYear, int nMonth, int nDay, int nHour, int nMinute, int nSecond, long lNetworkFlowPerSecond) {
                if (isGotoPlayTime) {
                    String ymd_hms = String.format("%d-%02d-%02d %02d:%02d:%02d", nYear, nMonth, nDay, nHour, nMinute, nSecond);
                    tvTime.setText(ymd_hms);
                    mCurrentTime = DateUtil.transTime(nYear, nMonth, nDay, nHour, nMinute, nSecond);
                    rulerView.setCurrentTimeMillis(mCurrentTime);
                }
            }

            @Override
            public void onAudioSwitchChanged(boolean open) {
                if (open) {
                    btnVoice.setTag("on");
                    btnVoice.setImageResource(R.mipmap.live_list_btn_sound_pre);
                } else {
                    btnVoice.setTag("off");
                    btnVoice.setImageResource(R.mipmap.live_list_btn_sound);
                }
            }

            @Override
            public void onHoldTalkSwitchChanged(boolean open) {

            }

            @Override
            public void onToborder() {

            }

            @Override
            public void onNvrSessionView(String data) {

            }

            @Override
            public void onAudioVolume(int volume) {

            }

            @Override
            public void onAuthorityChanged(int authority) {

            }
        });
    }

    @Override
    public void onDeviceLocalVideos(String data) {
        LogUtil.i(TAG, "==本地卡录像信息 onDeviceLocalVideos == " + data);
        runOnUiThread(() -> {
            // SD卡录像
            timeSlots.clear();
            if (data != null && !"".equals(data)) {
                timeSlots.addAll(cacheUtils.covertTimeSlots(data));
                rulerView.setVedioTimeSlot(timeSlots);
                cacheUtils.setLocalRecord(timeSlots);
                if (timeSlots.size() > 0) {
                    TimeSlot timeSlot = timeSlots.get(timeSlots.size() - 1);
                    LogUtil.i(TAG, "第一个数据：" + new Gson().toJson(timeSlot));
                    String startTime = DateUtil.getStringDateByLong(timeSlot.getStartTime(), DateUtil.DEFAULT_FORMAT);
                    String endTime = DateUtil.getStringDateByLong(timeSlot.getEndTime(), DateUtil.DEFAULT_FORMAT);
                    LogUtil.i(TAG, "play : " + startTime + " - " + endTime);
                    mRecordJson = data;
                    // 开始播放卡录像（Start playing card video）
                    mnPlayControl.playTfCardVideo(data, startTime, endTime);
                } else {
                    ToastUtils.MyToast("没有卡录像");
                }
            } else {
                ToastUtils.MyToast("没有卡录像");
            }
        });
    }

    private void initRulerView() {
        //TODO 获取存储卡信息
        MNOpenSDK.getTFSimpleState(mDevice.getSn(), new MNOpenSDKInterface.GetTFSimpleStateCallBack() {
            @Override
            public void onGetTFSimpleState(TFStateBean bean) {
                if (bean != null && bean.isResult() && bean.getParams() != null) {
                    String state = TFCardUtils.getTFCardState(bean);
                    if ("NULL".equals(state) || "Reinsert".equals(state)) {
                        // 没卡
                        ToastUtils.MyToastBottom(getString(R.string.device_has_no_card));
                    } else if ("Normal".equals(state) || "AutoFormat".equals(state)) {
                        // 有卡 格式正常  并且支持格式化
                    } else if ("NeedFormat".equals(state)) {
                        // 有卡 需要格式化
                        ToastUtils.MyToastBottom(getString(R.string.tf_card_needs_be_formatted));
                    } else if ("UnSupported".equals(state)) {
                        // 有卡 不正常并且不能手动格式化
                        ToastUtils.MyToastBottom(getString(R.string.tf_card_needs_covert_fat32));
                    }
                }
            }
        });

        mCurrentTime = System.currentTimeMillis();
        String timeStr = DateUtil.getStringDateByLong(mCurrentTime, DateUtil.DEFAULT_FORMAT);
        tvTime.setText(timeStr);

        long startSearchTime = LocalVariable.getDateBefore(new Date(), 0).getTime();
        long endSearchTime = LocalVariable.getDateBeforeNight(new Date(), 0).getTime();
        pszStartTime = DateUtil.getDateTime(DateUtil.getTodayStart(startSearchTime));
        pszEndTime = DateUtil.getDateTime(DateUtil.getTodayEnd(endSearchTime));
        llCardAlarmLay.setVisibility(View.VISIBLE);
        rulerView.setCurrentTimeMillis(mCurrentTime);
        cacheUtils = new RulerCardCacheUtils(new RulerCardCacheUtils.OnRulerCacheUtilsLinstener() {
            @Override
            public void onPlayTFCard(String alarmTime, String endTime, int mAlarmIndex, TimeSlot dirInfo) {
                String[] timeSqlit = alarmTime.split(" ");
                if (timeSqlit.length != 2) {
                    return;
                }
                String ymd = timeSqlit[0];
                String finalEndTime = ymd + " 23:59:59";
                mnPlayControl.playTfCardVideo(mRecordJson, alarmTime, finalEndTime);
            }

            @Override
            public void setRulerDataMoreIcon(int local, boolean isShow) {

            }

            @Override
            public void isLastCardPlayFinished() {
                ToastUtils.MyToastBottom("没有啦");
            }
        });
        rulerView.setOnBarMoveListener(new OnBarMoveListener() {

            @Override
            public void onBarMoving(boolean isLeftDrag, long timeMillis) {
                isGotoPlayTime = false;
                mCurrentTime = timeMillis;
                String timeStr = DateUtil.getStringDateByLong(mCurrentTime, DateUtil.DEFAULT_FORMAT);
                tvTime.setText(timeStr);
                mainHandler.removeMessages(GOTO_PLAY_TIME);
            }

            @Override
            public void onBarMoveFinish(long timeMillis) {
                mCurrentTime = timeMillis;
                cacheUtils.playLocalVideo(timeMillis);
                mainHandler.sendEmptyMessageDelayed(GOTO_PLAY_TIME, 1500);
            }

            @Override
            public void onMoveExceedStartTime() {
                // 清空时间卡,情况缓存内容
                rulerView.setVedioTimeSlot(null);
                cacheUtils.setLocalRecord(null);
                cachedThreadPool.execute(() -> {
                    mnPlayControl.stopPlayer();
                });
                mainHandler.sendEmptyMessageDelayed(GOTO_PLAY_TIME, 1500);
                TimeSectionBean searchBean = getSearchTime(mCurrentTime);
                pszStartTime = DateUtil.getDateTime(searchBean.getStartTime());
                pszEndTime = DateUtil.getDateTime(searchBean.getEndTime());
                LogUtil.i(TAG, pszStartTime + " -- " + pszEndTime);
                MNOpenSDK.getDeviceLocalVideos(mDevice.getSn(), 0, pszStartTime, pszEndTime, MNTfCardPlayActivity.this);
            }

            @Override
            public void onMoveExceedEndTime() {
                // 清空时间卡,情况缓存内容
                rulerView.setVedioTimeSlot(null);
                cacheUtils.setLocalRecord(null);
                cachedThreadPool.execute(() -> {
                    mnPlayControl.stopPlayer();
                });
                mainHandler.sendEmptyMessageDelayed(GOTO_PLAY_TIME, 1500);
                TimeSectionBean searchBean = getSearchTime(mCurrentTime);
                pszStartTime = DateUtil.getDateTime(searchBean.getStartTime());
                pszEndTime = DateUtil.getDateTime(searchBean.getEndTime());
                LogUtil.i(TAG, pszStartTime + " -- " + pszEndTime);
                MNOpenSDK.getDeviceLocalVideos(mDevice.getSn(), 0, pszStartTime, pszEndTime, MNTfCardPlayActivity.this);
            }

            @Override
            public void isMaxTime(long timeMillis) {
                mCurrentTime = timeMillis;
                mainHandler.sendEmptyMessageDelayed(GOTO_PLAY_TIME, 1500);
            }
        });
        MNOpenSDK.getDeviceLocalVideos(mDevice.getSn(), 0, pszStartTime, pszEndTime, this);
    }

    public TimeSectionBean getSearchTime(long currentTimeMillis) {
        Date mDate = new Date(currentTimeMillis);
        long startKeyTime = LocalVariable.getDateBefore(mDate, 0).getTime();
        long endKeyTime = LocalVariable.getDateBeforeNight(mDate, 0).getTime();

        String startTime = DateUtil.getStringDateByLong(startKeyTime, DateUtil.DEFAULT_FORMAT);
        String endTime = DateUtil.getStringDateByLong(endKeyTime, DateUtil.DEFAULT_FORMAT);

        LogUtil.i(TAG, startTime + " | " + endTime);
        TimeSectionBean sectionBean = new TimeSectionBean();
        sectionBean.setStartTime(startKeyTime);
        sectionBean.setEndTime(endKeyTime);
        return sectionBean;
    }

    String pszStartTime, pszEndTime;

    public void searchCardBeforeDay() {
        // 获取前一天的卡录像信息（Get the card recording information of the previous day）
        long currentTime = DateUtil.convertString2Date(tvTime.getText().toString(), DateUtil.DEFAULT_DATE_FORMAT).getTime() - 24 * 60 * 60 * 1000;
        pszStartTime = DateUtil.getDateTime(DateUtil.getTodayStart(currentTime));
        pszEndTime = DateUtil.getDateTime(DateUtil.getTodayEnd(currentTime));
        tvTime.setText(DateUtil.getStringDateByLong(currentTime, DateUtil.DEFAULT_DATE_FORMAT));
        LogUtil.i(TAG, pszStartTime + " -- " + pszEndTime);
        MNOpenSDK.getDeviceLocalVideos(mDevice.getSn(), 0, pszStartTime, pszEndTime, this);
    }

    public void searchCardBackwardDay() {
        // 获取下一天的卡录像信息（Get the card recording information for the next day）
        long currentTime = DateUtil.convertString2Date(tvTime.getText().toString(), DateUtil.DEFAULT_DATE_FORMAT).getTime() + 24 * 60 * 60 * 1000;
        pszStartTime = DateUtil.getDateTime(DateUtil.getTodayStart(currentTime));
        pszEndTime = DateUtil.getDateTime(DateUtil.getTodayEnd(currentTime));
        tvTime.setText(DateUtil.getStringDateByLong(currentTime, DateUtil.DEFAULT_DATE_FORMAT));
        LogUtil.i(TAG, pszStartTime + " -- " + pszEndTime);
        MNOpenSDK.getDeviceLocalVideos(mDevice.getSn(), 0, pszStartTime, pszEndTime, this);
    }


    public static class MainHandler extends Handler {
        private final WeakReference<MNTfCardPlayActivity> mActivity;

        public MainHandler(MNTfCardPlayActivity activity) {
            mActivity = new WeakReference<MNTfCardPlayActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MNTfCardPlayActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == GOTO_PLAY_TIME) {
                    activity.isGotoPlayTime = true;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //恢复播放任务
        if (!isFristInto) {
            if (mnPlayControl.getTaskStatus() == MNTS_PAUSED.ordinal()) {
                mnPlayControl.resumePlayer();
            } else {
                // 这里重新开启直播代码
                initData();
            }
        }
        isFristInto = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停播放任务
        if (!isFinish) {
            if (mnPlayControl.getTaskStatus() == MNTS_RUNNING.ordinal()) {
                mnPlayControl.pausePlayer();
            } else {
                mnPlayControl.stopPlayer();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO 注销播放器必须
        mnPlayControl.releasePlayer();
    }

    private void finishActivity() {
        isFinish = true;
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {//横屏
            setLandscapeView();
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {//竖屏
            setPortraitView();
        }
    }

    /**
     * 设置横屏UI
     */
    public void setLandscapeView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏手机顶部导航栏
        titleLay.setVisibility(View.GONE);
        llToolsLay.setVisibility(View.GONE);
        llCardAlarmLay.setVisibility(View.GONE);
        ivShrink.setVisibility(View.VISIBLE);
        ivFull.setVisibility(View.GONE);
        rlMainLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    /**
     * 设置竖屏UI
     */
    public void setPortraitView() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        titleLay.setVisibility(View.VISIBLE);
        llToolsLay.setVisibility(View.VISIBLE);
        ivShrink.setVisibility(View.GONE);
        ivFull.setVisibility(View.VISIBLE);
        if (mDevice != null) {
            llCardAlarmLay.setVisibility(View.VISIBLE);
        } else {
            llCardAlarmLay.setVisibility(View.GONE);
        }
        rlMainLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, defHeight));
    }


    @OnClick({R.id.btn_startRecord, R.id.btnVoice, R.id.before_day, R.id.backward_day,
            R.id.btn_download})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_startRecord:
                if ("off".equals(btnStartRecord.getTag())) {
                    File file = new File(Constants.RecordPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String recordPath = Constants.RecordPath + System.currentTimeMillis() + ".mp4";
                    boolean result = mnPlayControl.startRecord(recordPath);
                    if (result) {
                        btnStartRecord.setTag("on");
                        btnStartRecord.setImageResource(R.mipmap.live_list_btn_video_pre);
                    }
                } else {
                    String fileName = mnPlayControl.stopRecord();
                    if (fileName == null || "".equals(fileName)) {
                        ToastUtils.MyToastCenter("录像失败");
                    } else {
                        ToastUtils.MyToastCenter("录像成功：" + fileName);
                    }
                    btnStartRecord.setTag("off");
                    btnStartRecord.setImageResource(R.mipmap.live_list_btn_video);
                }
                break;
            case R.id.btnVoice:
                if ("off".equals(btnVoice.getTag())) {
                    mnPlayControl.startAudio();
                } else {
                    mnPlayControl.stopAudio();
                }
                break;
            case R.id.before_day:
                searchCardBeforeDay();
                break;
            case R.id.backward_day:
                searchCardBackwardDay();
                break;
            case R.id.btn_download:
                String currentTimeStr = tvTime.getText().toString();
                long currentTime = DateUtil.convertString2Time(currentTimeStr, DateUtil.DEFAULT_FORMAT);
                String startDownloadTime = DateUtil.getStringDateByLong(currentTime - 2 * 60 * 1000, DateUtil.DEFAULT_FORMAT);
                String endDownloadTime = DateUtil.getStringDateByLong(currentTime + 2 * 60 * 1000, DateUtil.DEFAULT_FORMAT);

                File file = new File(Constants.RecordPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String downloadPath = Constants.RecordPath + currentTimeStr + ".mp4";
                mnPlayControl.downloadTfCardVideo(mRecordJson, startDownloadTime, endDownloadTime, downloadPath);
                break;
        }
    }

    @OnTouch({R.id.btn_screenshot})
    public boolean onTouchView(View v, MotionEvent event) {
        if (v.getId() == R.id.btn_screenshot) { // 截图
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                btnScreenshot.setImageResource(R.mipmap.live_list_btn_screenshot_pre);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                String fileName = Constants.ImagePath + System.currentTimeMillis() + ".png";
                boolean result = mnPlayControl.screenShot(fileName);
                if (result) {
                    ToastUtils.MyToast("截图成功：" + fileName);
                }
                btnScreenshot.setImageResource(R.mipmap.live_list_btn_screenshot);
            }
        }
        // 单项对讲
        return true;
    }

    @OnClick({R.id.iv_shrink, R.id.iv_full})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_shrink:
                //强制竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.iv_full:
                //强制横屏 全屏播放
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }
}
