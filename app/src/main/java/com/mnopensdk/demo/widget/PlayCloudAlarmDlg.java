package com.mnopensdk.demo.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;


import com.mnopensdk.demo.utils.ToastUtils;
import com.mn.bean.restfull.AlarmsBean;
import com.mn.bean.restfull.AuthenticationBean;
import com.mn.player.audio.AudioRecorder;
import com.mn.player.audio.AudioRunable;
import com.mn.player.audio.OnAudioObserver;
import com.mn.player.audio.OnVolumeListener;
import com.mn.player.opengl.GLFrameRenderer;
import com.mn.player.video.OnVideoObserver;
import com.mn.player.video.VideoBean;
import com.mn.player.video.VideoRunable;
import com.mnopensdk.demo.utils.LogUtil;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import MNSDK.MNJni;
import MNSDK.MNKit;
import MNSDK.MNVideoProcessor;
import MNSDK.inface.IMNVideoFace;
import MNSDK.inface.MNKitInterface;
import com.mnopensdk.demo.R;

/**
 * Created by WIN on 2017/12/1.
 */

public class PlayCloudAlarmDlg implements OnClickListener, OnVolumeListener, OnAudioObserver, OnVideoObserver, IMNVideoFace, DialogInterface.OnDismissListener, MNKitInterface.AuthenticationUrlCallBack {
    private String TAG = PlayCloudAlarmDlg.class.getSimpleName();
    private Context context;
    private Dialog dialog;
    private Display display;

    private AlarmsBean mAlarm;
    private GLSurfaceView mGlsfView;
    private GLFrameRenderer renderer;
    private AudioRecorder _talkRecorder = null;      // 音频采集
    private AudioTrack _audioPlayer;
    private AudioRunable mAudioRunable;
    private VideoRunable mVideoRunable;
    private long lVideoTaskContext = 0;             // 云报警 直播 回放
    private long lVoiceTalkTaskContext = 0;         // 对讲context
    private long lCloudVideoDownTaskContext = 0;   // 云报警视频下载
    private Lock mVideoTaskLock = new ReentrantLock();
    private Handler myHandler = new Handler(Looper.getMainLooper());

    public PlayCloudAlarmDlg(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        builder();
    }

    private void builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_cloud_alarm, null);
        // 获取自定义Dialog布局中的控件
        mGlsfView = view.findViewById(R.id.glsf_view);
        renderer = new GLFrameRenderer();
        mGlsfView.setEGLContextClientVersion(2);
        mGlsfView.setRenderer(renderer);
        mGlsfView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);  // RENDERMODE_WHEN_DIRTY方式，这样不会让CPU一直处于高速运转状态

        mAudioRunable = new AudioRunable(this);
        mVideoRunable = new VideoRunable(this);
        _talkRecorder = new AudioRecorder();
        _talkRecorder.setOnVolumeListener(this);
        MNVideoProcessor.getInstance().register(this);
        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.RuleAlertDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.9);
//        lp.height = (int) (lp.width/3*2);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(this);
        setScreenBgDarken();
    }

    // 设置屏幕背景变暗
    private void setScreenBgDarken() {
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.alpha = 0.5f;
        lp.dimAmount = 0.5f;
        dialog.getWindow().setAttributes(lp);
    }

    // 设置屏幕背景变亮
    private void setScreenBgLight() {
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.alpha = 1.0f;
        lp.dimAmount = 1.0f;
        dialog.getWindow().setAttributes(lp);
    }

    public void show(AlarmsBean alarm) {
        mAlarm = alarm;
        if (mAlarm != null) {
            // 云视频
            MNKit.authenticationUrl(mAlarm.getVideoUrl(), this);
        }
        dialog.show();
    }

    @Override
    public void onAuthenticationUrlFailed(String msg) {
        ToastUtils.MyToastBottom(context.getString(R.string.net_err_and_try));
    }


    @Override
    public void onAuthenticationUrlSuc(AuthenticationBean data) {
        mnts_Peady();
        if (data.getUrls() == null || data.getUrls().size() == 0) {
            mnts_Connect_failed();
            return;
        }
        final AuthenticationBean.UrlsBean urlsBean = data.getUrls().get(0);
        if (urlsBean.getPresignedurl() == null || "".equals(urlsBean.getPresignedurl()) || urlsBean.getFile_size() == 0) {
            mnts_Connect_failed();
            return;
        }

        String mVideoUrl = urlsBean.getPresignedurl().replace("https", "http");
        mnts_Connecting();
        new Thread(new Runnable() {
            @Override
            public void run() {
                releaseTask();
                LogUtil.i(TAG, mVideoUrl);
                try {
                    boolean bGetLock = mVideoTaskLock.tryLock(100, TimeUnit.MILLISECONDS);
                    if (bGetLock) {
                        if (0 == lVideoTaskContext) {
                            MNJni.MNTaskType taskType = new MNJni.MNTaskType(MNJni.MNTaskType.MNAV_TASK_TYPE_t.MTT_CLOUD_ALARM.ordinal());
                            MNJni.MNOutputDataType dataType = new MNJni.MNOutputDataType(MNJni.MNOutputDataType.MN_OUTPUT_DATA_TYPE.MODT_FFMPEG_YUV420.ordinal());
                            MNJni.MNCloudTaskType cloudTaskType = new MNJni.MNCloudTaskType(MNJni.MNCloudTaskType.MN_CLOUD_TASK_TYPE.MCTT_PLAY.ordinal());
                            lVideoTaskContext = MNJni.PrepareTask(taskType, 0);
                            MNJni.ConfigCloudAlarmTask(lVideoTaskContext, mVideoUrl, 0, urlsBean.getFile_size(), dataType, cloudTaskType);
                            MNJni.StartTask(lVideoTaskContext);
                            mVideoRunable.startRun();
                            initAudio();
                            mAudioRunable.startRun();
                        }
                        mVideoTaskLock.unlock();
                    } else {


                        myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mnts_Connect_failed();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void initAudio() {
        if (_audioPlayer == null) {
            LogUtil.i(TAG, "OnAudioData playAudio onCreate : -------------------1-----1");
            int nBufSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            _audioPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, nBufSize, AudioTrack.MODE_STREAM);
            _audioPlayer.play();
        }
    }

    public void releaseTask() {
        mVideoRunable.stopRun();
        mAudioRunable.stopRun();
        mVideoTaskLock.lock();
        if (0 == MNJni.DestroyTask(lVideoTaskContext)) {
            lVideoTaskContext = 0;
        }
        mVideoTaskLock.unlock();

        if (0 == MNJni.DestroyTask(lVoiceTalkTaskContext)) {
            lVoiceTalkTaskContext = 0;
        }
        if (_talkRecorder != null) {
            _talkRecorder.Stop();
        }
    }

    private void mnts_Connecting() {

    }

    private void mnts_Connect_failed() {
    }

    private void mnts_Peady() {
    }

    @Override
    public void onVolumeBack(int value) {

    }

    @Override
    public void onRunableAudioData(long lTaskContext, int nChannelId, long userdata, byte[] InData, int nDataLen, int nEncodeType) {
        if (ByteBuffer.wrap(InData).remaining() > 0) {
            if (_audioPlayer != null && _audioPlayer.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                _audioPlayer.write(InData, 0, InData.length);
            }
        }
    }

    private int position = 0;

    @Override
    public void onRunableVideoData(VideoBean videoBean) {
        if (renderer == null || videoBean == null) return;
        try {
            renderer.update(videoBean.getnWidth(), videoBean.getnHeight());//可能会产出内存溢出问题以及空指针
            renderer.update(videoBean.getY(), videoBean.getU(), videoBean.getV());
            mGlsfView.requestRender();
            position++;
            myHandler.post(new Runnable() {
                @Override
                public void run() {
//                    if ((pbProgress.getVisibility() != GONE || tvTipMsg.getVisibility() != GONE) && position > 5){
//                        pbProgress.setVisibility(GONE);
//                        tvTipMsg.setVisibility(GONE);
//                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnVideoDataByte(long lTaskContext, int nChannelId, long userdata, int dataType, byte[] data, int nDataLen, byte[] y, byte[] u, byte[] v, int nWidth, int nHeight, int nYStride, int nUStride, int nVStride, int nFps, int nSliceType, int nYear, int nMonth, int nDay, int nHour, int nMinute, int nSecond, long lNetworkFlowPerSecond, long lTotalFlow) {
        if (lTaskContext != lVideoTaskContext || mVideoRunable == null) {
            return;
        }
        mVideoRunable.writeVideo(lTaskContext, nChannelId, userdata, dataType, data, nDataLen, ByteBuffer.wrap(y), ByteBuffer.wrap(u), ByteBuffer.wrap(v),
                nWidth, nHeight, nYStride, nUStride, nVStride, nFps, nSliceType,
                nYear, nMonth, nDay, nHour, nMinute, nSecond, lNetworkFlowPerSecond, lTotalFlow);
    }

    @Override
    public void OnAudioDataByte(long lTaskContext, int nChannelId, long userdata, byte[] InData, int nDataLen, int nEncodeType) {
        if (lTaskContext != lVideoTaskContext || mAudioRunable == null) {
            return;
        }
        mAudioRunable.writeAudio(lTaskContext, nChannelId, userdata, InData, nDataLen, nEncodeType);
    }

    @Override
    public void OnTaskStatus(long lTaskContext, long userdata, int eTaskStatus, float fProgress) {

    }

    @Override
    public void OnSessionCtrl(long lTaskContext, int nChannelId, int eSessionCtrlType, String data, int length) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        releaseTask();
    }


    public interface OnMoreSetClickLinstener {

    }

    private OnMoreSetClickLinstener mListener;

    public void setMoreSetClickLinstener(OnMoreSetClickLinstener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
        if (mListener == null) {
            return;
        }
        switch (v.getId()) {

        }
    }


}
