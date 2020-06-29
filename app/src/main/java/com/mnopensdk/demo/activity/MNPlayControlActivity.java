package com.mnopensdk.demo.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.kongqw.rockerlibrary.view.RockerView;
import com.mn.bean.restfull.AlarmsBean;
import com.mn.bean.restfull.AuthenticationBean;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.restfull.FavoriteDelteBean;
import com.mn.bean.restfull.FavoritePointBean;
import com.mn.bean.restfull.FavoritesInfoBean;
import com.mn.bean.restfull.UpdateDeviceCoverBean;
import com.mn.player.MNControlAction;
import com.mn.player.MNPlayControl;
import com.mn.player.MNPlayControlLinstener;
import com.mn.tools.AbilityTools;
import com.mn.tools.AuthorityManager;
import com.mnopensdk.demo.Constants;
import com.mnopensdk.demo.R;
import com.mnopensdk.demo.adapter.FavoritesAdapter;
import com.mnopensdk.demo.utils.DensityUtil;
import com.mnopensdk.demo.utils.LogUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mnopensdk.demo.widget.RuleAlertDialog;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

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

public class MNPlayControlActivity extends Activity implements MNKitInterface.AuthenticationUrlCallBack, MNKitInterface.GetFavoritePointsInfoCallBack, FavoritesAdapter.FavoritesItemListener {
    @BindView(R.id.iv_shrink)
    ImageView ivShrink;
    @BindView(R.id.iv_full)
    ImageView ivFull;
    @BindView(R.id.rl_main_lay)
    RelativeLayout rlMainLay;
    @BindView(R.id.rv_rockerView_screen)
    RockerView rvRockerView;
    @BindView(R.id.fl_rockerview_screen_bg)
    FrameLayout rvRockerViewBg;
    @BindView(R.id.tv_live)
    TextView tvLive;
    @BindView(R.id.tv_favorites)
    TextView tvFavorites;
    @BindView(R.id.rl_bottom_main_lay)
    RelativeLayout rlBottomMainLay;
    @BindView(R.id.iv_favorites_btn)
    ImageView ivFavoritesBtn;
    @BindView(R.id.recyclerFavorites)
    RecyclerView recyclerFavorites;
    @BindView(R.id.rl_favorites_lay)
    LinearLayout rlFavoritesLay;
    @BindView(R.id.rl_live_ptz_lay)
    RelativeLayout rlLivePtzLay;
    @BindView(R.id.tv_del_favorites)
    TextView tvDelFavorites;
    @BindView(R.id.tv_cover_favorites)
    TextView tvCoverFavorites;
    @BindView(R.id.tv_cancel_favorites_action)
    TextView tvCancelFavoritesAction;
    @BindView(R.id.ll_favorites_action_lay)
    LinearLayout llFavoritesActionLay;
    @BindView(R.id.ll_bottom_lay)
    LinearLayout llBottomLay;
    @BindView(R.id.tv_reset_angle)
    TextView tvResetAngle;
    @BindView(R.id.av_load)
    AVLoadingIndicatorView avLoad;
    @BindView(R.id.tv_loadTip)
    TextView tvLoadTip;
    private String TAG = MNPlayControlActivity.class.getSimpleName();

    @BindView(R.id.mn_play_control)
    MNPlayControl mnPlayControl;
    @BindView(R.id.btn_startRecord)
    ImageView btnStartRecord;
    @BindView(R.id.btn_screenshot)
    ImageView btnScreenshot;
    @BindView(R.id.btn_SingleTalk)
    ImageView btnSingleTalk;
    @BindView(R.id.btn_CodeStream)
    ImageView btnCodeStream;
    @BindView(R.id.btnVoice)
    ImageView btnVoice;
    @BindView(R.id.btn_download)
    ImageView btnDownload;

    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.title_lay)
    RelativeLayout titleLay;
    @BindView(R.id.frame_download_lay)
    FrameLayout frameDownloadLay;
    @BindView(R.id.ll_tools_lay)
    LinearLayout llToolsLay;

    private FavoritesAdapter mAdapter;
    private int defHeight = 0;
    private boolean isFristInto = true, isFinish = false;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mn_play_control_activity);
        ButterKnife.bind(this);

        mDevice = (DevicesBean) getIntent().getSerializableExtra("device_info");
        mCloudVideo = (AlarmsBean) getIntent().getSerializableExtra("cloudVideo");
        loadingDialog = new LoadingDialog(this);
        initLinstener();
        initData();
        initPTZControl();
        defHeight = DensityUtil.dip2px(this, 220);
    }

    private void initPTZControl() {
        rvRockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
        // 监听摇动方向
        rvRockerView.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {
                mnPlayControl.stopPTZAudio();
            }

            @Override
            public void direction(RockerView.Direction direction) {
                if (direction == RockerView.Direction.DIRECTION_UP) {
                    // 上
                    mnPlayControl.ptzControl(MNControlAction.MNDirection.DIRECTION_UP, 0);
                    rvRockerViewBg.setBackgroundResource(R.mipmap.full_yuntai_rocker_bg_top);
                } else if (direction == RockerView.Direction.DIRECTION_LEFT) {
                    // 左
                    mnPlayControl.ptzControl(MNControlAction.MNDirection.DIRECTION_LEFT, 0);
                    rvRockerViewBg.setBackgroundResource(R.mipmap.full_yuntai_rocker_bg_left);
                } else if (direction == RockerView.Direction.DIRECTION_RIGHT) {
                    // 右
                    mnPlayControl.ptzControl(MNControlAction.MNDirection.DIRECTION_RIGHT, 0);
                    rvRockerViewBg.setBackgroundResource(R.mipmap.full_yuntai_rocker_bg_right);
                } else if (direction == RockerView.Direction.DIRECTION_DOWN) {
                    // 下
                    mnPlayControl.ptzControl(MNControlAction.MNDirection.DIRECTION_DOWN, 0);
                    rvRockerViewBg.setBackgroundResource(R.mipmap.full_yuntai_rocker_bg_under);
                } else if (direction == RockerView.Direction.DIRECTION_CENTER) {
                    // 中，停止
                    mnPlayControl.ptzControl(MNControlAction.MNDirection.DIRECTION_CENTER, 0);
                    rvRockerViewBg.setBackgroundResource(R.mipmap.full_yuntai_rocker_bg);
                } else if (direction == RockerView.Direction.DIRECTION_DOWN_RIGHT) {
                    //右下
                    mnPlayControl.ptzControl(MNControlAction.MNDirection.DIRECTION_DOWN_RIGHT, 0);
                    rvRockerViewBg.setBackgroundResource(R.mipmap.full_yuntai_rocker_bg_rightlower);
                } else if (direction == RockerView.Direction.DIRECTION_UP_RIGHT) {
                    //右上
                    mnPlayControl.ptzControl(MNControlAction.MNDirection.DIRECTION_UP_RIGHT, 0);
                    rvRockerViewBg.setBackgroundResource(R.mipmap.full_yuntai_rocker_bg_rightup);
                } else if (direction == RockerView.Direction.DIRECTION_UP_LEFT) {
                    //左上
                    mnPlayControl.ptzControl(MNControlAction.MNDirection.DIRECTION_UP_LEFT, 0);
                    rvRockerViewBg.setBackgroundResource(R.mipmap.full_yuntai_rocker_bg_upleft);
                } else if (direction == RockerView.Direction.DIRECTION_DOWN_LEFT) {
                    //左下
                    mnPlayControl.ptzControl(MNControlAction.MNDirection.DIRECTION_DOWN_LEFT, 0);
                    rvRockerViewBg.setBackgroundResource(R.mipmap.full_yuntai_rocker_bg_uplower);
                }
            }

            @Override
            public void onFinish() {
                mnPlayControl.ptzControl(MNControlAction.MNDirection.DIRECTION_CENTER, 0);
                rvRockerViewBg.setBackgroundResource(R.mipmap.full_yuntai_rocker_bg);
            }
        });
    }

    private DevicesBean mDevice;
    private AlarmsBean mCloudVideo;

    private void initData() {
        if (mDevice != null) {
            // 直播
            tvTitle.setText(mDevice.getDev_name());
            frameDownloadLay.setVisibility(View.GONE);
            btnCodeStream.setVisibility(View.VISIBLE);
            btnSingleTalk.setVisibility(View.VISIBLE);
            rlBottomMainLay.setVisibility(View.VISIBLE);
            setSelectItem(0);
            if (AbilityTools.isSupportPTZControl(mDevice)) {
                tvResetAngle.setVisibility(View.VISIBLE);
                rvRockerViewBg.setVisibility(View.VISIBLE);
                recyclerFavorites.setLayoutManager(new GridLayoutManager(this, 3));
                mAdapter = new FavoritesAdapter(this, null);
                mAdapter.setFavoritesItemListener(this);
                recyclerFavorites.setAdapter(mAdapter);
                mAdapter.openLoadAnimation(false);

                // TODO 获取收藏的位置信息
                loadingDialog.show();
                MNKit.getFavoritePointsInfo(mDevice.getId(), this);
            } else {
                tvResetAngle.setVisibility(View.GONE);
                rvRockerViewBg.setVisibility(View.GONE);
            }
            tvLoadTip.setVisibility(View.GONE);
            mnPlayControl.setDeviceInfo(mDevice);
            mnPlayControl.startLive(MNControlAction.MNCodeStream.VIDEO_FUL, 0);
        } else if (mCloudVideo != null) {
            // 云视频
            tvTitle.setText("云视频");
            frameDownloadLay.setVisibility(View.VISIBLE);
            btnCodeStream.setVisibility(View.GONE);
            btnSingleTalk.setVisibility(View.GONE);
            rlBottomMainLay.setVisibility(View.GONE);

            // 开始云回放，mCloudVideo为云报警数据（Start cloud playback, mCloudVideo is cloud alarm data）
            mnPlayControl.playCloudVideo(mCloudVideo);
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
        String fileName = Constants.ImagePath + System.currentTimeMillis() + ".png";
        boolean result = mnPlayControl.screenShot(fileName);
        if (result && mDevice != null) {
            // mDevice==null说明是云回放
            MNKit.updateDeviceCover(new File(fileName), mDevice.getSn(), 0, new MNKitInterface.UpdateDeviceCoverCallBack() {
                @Override
                public void onUpdateDeviceCoverFailed(String msg, int id) {
                    Log.i(TAG, "封页上传失败 ........");
                }

                @Override
                public void onUpdateDeviceCoverSuc(UpdateDeviceCoverBean response, int id) {
                    Log.i(TAG, "封页上传成功 ........");
                }
            });
        }
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
        ivShrink.setVisibility(View.VISIBLE);
        ivFull.setVisibility(View.GONE);
        rlMainLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        rlBottomMainLay.setVisibility(View.GONE);
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
        rlBottomMainLay.setVisibility(View.VISIBLE);
        rlMainLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, defHeight));
    }

    private AuthenticationBean.UrlsBean mUrlsBean;

    private void initLinstener() {
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
                        if (avLoad.isShown()) {
                            avLoad.hide();
                        }
                        tvLoadTip.setVisibility(View.VISIBLE);
                        tvLoadTip.setText("设备不在线");
                    } else if (taskStatus == MNTS_CONNECT_FAILED.ordinal()) {
                        Log.i(TAG, "OnTaskStatus 任务 videoType : " + videoType + "-- 连接失败，请重试...");
                        if (avLoad.isShown()) {
                            avLoad.hide();
                        }
                        tvLoadTip.setVisibility(View.VISIBLE);
                        tvLoadTip.setText("连接失败，请重试");
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
                        if (downStatus == 0 || downStatus == 1) {
                            // 开始任务
                            LogUtil.i(TAG, "下载中... " + progress);
                            tvProgress.setVisibility(View.VISIBLE);
                            tvProgress.setText(String.format("%.02f", progress * 100) + "/%");
                        } else if (downStatus == 2) { // 下载失败
                            LogUtil.i(TAG, "下载失败");
                            tvProgress.setText("失败");
                            new Handler().postDelayed(() -> {
                                tvProgress.setVisibility(View.GONE);
                            }, 800);
                        } else if (downStatus == 3) { // 下载成功
                            LogUtil.i(TAG, "下载成功 ： " + fileName);
                            tvProgress.setText("成功");
                            if (downStatus == 30) {
                                tvProgress.setVisibility(View.VISIBLE);
                            }
                            new Handler().postDelayed(() -> {
                                tvProgress.setVisibility(View.GONE);
                            }, 800);
                        } else if (downStatus == 4) {
                            LogUtil.i(TAG, "取消下载 ： ");
                            tvProgress.setVisibility(View.GONE);
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
                // 设置预置点结果回调
                if (result) {
                    if (loadingDialog != null) {
                        loadingDialog.show();
                    }
                    MNKit.getFavoritePointsInfo(mDevice.getId(), MNPlayControlActivity.this);
                } else {
                    if (loadingDialog != null) {
                        loadingDialog.dismiss();
                    }
                    LogUtil.i(TAG, getString(R.string.tv_failed_set_favorites));
                    ToastUtils.MyToastBottom(getString(R.string.tv_failed_set_favorites));
                }
            }

            @Override
            public void onDelSessionCtrl(boolean result, String pointIndex) {
                //删除摇头机预置点
                if (result) {
                    MNKit.getFavoritePointsInfo(mDevice.getId(), MNPlayControlActivity.this);
                } else {
                    if (loadingDialog != null) {
                        loadingDialog.dismiss();
                    }
                    ToastUtils.MyToastBottom(getString(R.string.net_err_and_try));
                }
            }

            @Override
            public void onGotoSessionCtrl(boolean result, String pointIndex) {
                // 前往摇头机预置点
                ToastUtils.MyToastBottom(getString(R.string.goto_preset_point));
            }

            @Override
            public void runSpeed(int nYear, int nMonth, int nDay, int nHour, int nMinute, int nSecond, long lNetworkFlowPerSecond) {
                LogUtil.i(TAG, String.format("%d-%d-%d %d:%d:%d    ↓%.2fkb/s", nYear, nMonth, nDay, nHour, nMinute, nSecond, (float) lNetworkFlowPerSecond / 1024));
                if (avLoad.isShown()) {
                    avLoad.hide();
                }

                if (tvLoadTip.getVisibility() != View.GONE) {
                    tvLoadTip.setVisibility(View.GONE);
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
                if (open) {
                    btnSingleTalk.setImageResource(R.mipmap.live_list_btn_voice_pre);
                } else {
                    btnSingleTalk.setImageResource(R.mipmap.live_list_btn_voice);
                }
            }

            @Override
            public void onToborder() {
                LogUtil.i(TAG, "转动到边界啦");
                ToastUtils.MyToastBottom(getString(R.string.tv_goto_the_border));
            }

            @Override
            public void onNvrSessionView(String data) {

            }

            @Override
            public void onAudioVolume(int volume) {

            }

            @Override
            public void onAuthorityChanged(int authority) {
                ToastUtils.MyToastBottom(getString(R.string.tv_restricted_permission));
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onAuthenticationUrlSuc(AuthenticationBean url) {

    }

    @Override
    public void onAuthenticationUrlFailed(String msg) {
        ToastUtils.MyToast("获取视频授权地址失败（Failed to obtain video authorized address）");
    }

    @OnClick({R.id.btn_startRecord, R.id.btnVoice, R.id.btn_CodeStream, R.id.btn_download})
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
            case R.id.btn_CodeStream:
                if ("0".equals(btnCodeStream.getTag())) {
                    boolean result = mnPlayControl.setCodeStream(MNControlAction.MNCodeStream.VIDEO_FUL);
                    if (result) {
                        btnCodeStream.setTag("1");
                        btnCodeStream.setImageResource(R.mipmap.live_list_btn_flu);
                    }
                } else {
                    boolean result = mnPlayControl.setCodeStream(MNControlAction.MNCodeStream.VIDEO_HD);
                    if (result) {
                        btnCodeStream.setTag("0");
                        btnCodeStream.setImageResource(R.mipmap.live_list_btn_hd);
                    }
                }
                break;
            case R.id.btn_download:
                // @TODO 方法有所改变
                if (mCloudVideo != null && (mCloudVideo.getVideoUrl() != null || mCloudVideo.getRecordUrl() != null)) {
                    File file = new File(Constants.RecordPath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String downloadPath = Constants.RecordPath + mCloudVideo.getAlarmId() + ".mp4";
                    mnPlayControl.downCloudAlarmVideo(downloadPath, mCloudVideo);
                } else {
                    ToastUtils.MyToast("没有获取到授权视频地址");
                }
                break;
        }
    }

    @OnTouch({R.id.btn_SingleTalk, R.id.btn_screenshot})
    public boolean onTouchView(View v, MotionEvent event) {
        if (v.getId() == R.id.btn_SingleTalk) { // 长按对讲
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                LogUtil.i(TAG, "== ACTION_DOWN ==");
                mnPlayControl.startTalk();// 开启长按对讲
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                LogUtil.i(TAG, "== ACTION_UP ==");
                mnPlayControl.stopTalk();// 关闭长按对讲
            }
        } else if (v.getId() == R.id.btn_screenshot) { // 截图
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

    public void setSelectItem(int item) {
        if (item == 0) {
            rlLivePtzLay.setVisibility(View.VISIBLE);
            rlFavoritesLay.setVisibility(View.GONE);
            tvLive.setTextColor(ContextCompat.getColor(this, R.color.color_white));
            tvLive.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_2097fe));

            tvFavorites.setTextColor(ContextCompat.getColor(this, R.color.blue_2097fe));
            tvFavorites.setBackgroundColor(ContextCompat.getColor(this, R.color.color_white));
        } else if (item == 1) {
            if (AbilityTools.isSupportPTZControl(mDevice)) {
                MNKit.getFavoritePointsInfo(mDevice.getId(), this);
                rlLivePtzLay.setVisibility(View.GONE);
                rlFavoritesLay.setVisibility(View.VISIBLE);
                tvLive.setTextColor(ContextCompat.getColor(this, R.color.blue_2097fe));
                tvLive.setBackgroundColor(ContextCompat.getColor(this, R.color.color_white));
                tvFavorites.setTextColor(ContextCompat.getColor(this, R.color.color_white));
                tvFavorites.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_2097fe));
            } else {
                ToastUtils.MyToastBottom(getString(R.string.tv_not_support_favorite));
            }
        }
    }


    @OnClick({R.id.iv_shrink, R.id.iv_full, R.id.tv_live, R.id.tv_favorites, R.id.iv_favorites_btn,
            R.id.tv_del_favorites, R.id.tv_cover_favorites, R.id.tv_cancel_favorites_action, R.id.tv_reset_angle})
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
            case R.id.tv_live:
                // 直播按钮
                setSelectItem(0);
                break;
            case R.id.tv_favorites:
                // 收藏按钮
                setSelectItem(1);
                break;
            case R.id.iv_favorites_btn:
                // 开始收藏位置
                if (userPostion.size() == 0) {
                    ToastUtils.MyToastBottom(getString(R.string.tv_favorites_are_full));
                    return;
                }
                if (!AuthorityManager.isHadPTZControlAuthority(mDevice.getSn())) {
                    ToastUtils.MyToastBottom(getString(R.string.tv_restricted_permission));
                    return;
                }
                if (loadingDialog != null) {
                    loadingDialog.show();
                }
                String mFavoritePoint = userPostion.get(0);
                mnPlayControl.saveFavoritePoint("名字_" + mFavoritePoint, mFavoritePoint);
                break;
            case R.id.tv_del_favorites:
                // 删除预置点
                FavoritePointBean favorite = mAdapter.getSelectFavorite();
                mnPlayControl.delFavoritePoint(favorite.getId(), favorite.getPostion_id());
                break;
            case R.id.tv_cover_favorites:
                // 覆盖预置点
                FavoritePointBean mfavorite = mAdapter.getSelectFavorite();
                mnPlayControl.saveFavoritePoint("名字_" + mfavorite.getPostion_id(), mfavorite.getPostion_id());
                break;
            case R.id.tv_cancel_favorites_action:
                // 取消预置点选择
                llBottomLay.setVisibility(View.VISIBLE);
                llFavoritesActionLay.setVisibility(View.GONE);
                mAdapter.setEditState(false);
                break;
            case R.id.tv_reset_angle:
                mnPlayControl.ptzResetAngle();
                break;
        }
    }

    /**
     * 想要删除收藏点
     *
     * @param item
     */
    //@Override
    public void onDelFavoritesItem(FavoritePointBean item) {
        new RuleAlertDialog(this).builder().setTitle(getString(R.string.tip_title)).
                setMsg(getString(R.string.tv_delete_the_favorite)).
                setOkButton(getString(R.string.label_ok), v -> {
                    ArrayList<String> ids = new ArrayList<>();
                    ids.add(item.getId());
                    MNKit.delteFavoritePoints(ids, new MNKitInterface.DelteFavoritePointsCallBack() {
                        @Override
                        public void onDelteFavoritePointsSuc(FavoriteDelteBean regBean) {
                            // 删除成功之后，刷新预置点
                            MNKit.getFavoritePointsInfo(mDevice.getId(), MNPlayControlActivity.this);
                        }

                        @Override
                        public void onDelteFavoritePointsFailed(String o) {

                        }
                    });
                }).
                setCancelButton(getString(R.string.label_cancel), null).
                setCancelBtnColor(getResources().getColor(R.color.login_int)).show();
    }

    /**
     * 获取到收藏位置的回调信息
     *
     * @param bean
     */
    private List<String> userPostion = new ArrayList<>();// 可用预置点

    @Override
    public void onGetFavoritePointsInfoSuc(FavoritesInfoBean response) {
        mAdapter.setData(response.getList());
        userPostion.clear();
        for (int i = 0; i < 6; i++) {
            boolean had = false;
            for (FavoritePointBean item : response.getList()) {
                if (item.getPostion_id().equals(String.valueOf(i))) {
                    had = true;
                }
            }
            if (!had) {
                userPostion.add(String.valueOf(i));
            }
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 获取到收藏位置信息失败回调
     *
     * @param msg
     */
    @Override
    public void onGetFavoritePointsInfoFailed(String msg) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        ToastUtils.MyToastBottom(msg);
    }

    /**
     * 长按预置点，进行编辑操作
     *
     * @param item
     */
    @Override
    public void onLongClickFavorite(FavoritePointBean item) {
        llBottomLay.setVisibility(View.GONE);
        llFavoritesActionLay.setVisibility(View.VISIBLE);
    }

    /**
     * 点击预置点，前往预置点
     *
     * @param item
     */
    @Override
    public void onnClickFavorite(FavoritePointBean item) {
        ToastUtils.MyToastBottom(getString(R.string.goto_preset_point));
        mnPlayControl.gotoFavoritePoint(item.getPostion_id());
    }
}
