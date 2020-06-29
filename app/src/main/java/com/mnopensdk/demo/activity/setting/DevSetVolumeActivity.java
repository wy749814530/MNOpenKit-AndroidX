package com.mnopensdk.demo.activity.setting;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.setting.AudioOutputBean;
import com.mn.bean.setting.AudioOutputSetBean;
import com.mn.bean.setting.BaseResult;
import com.mn.bean.setting.DevSoundBean;

import MNSDK.MNOpenSDK;
import MNSDK.inface.MNOpenSDKInterface;
import butterknife.BindView;
import butterknife.OnClick;
import com.mnopensdk.demo.R;

/**
 * Created by hjz on 2019/1/23.
 */

public class DevSetVolumeActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, MNOpenSDKInterface.GetAudioOutputCallBack, MNOpenSDKInterface.SetAudioOutputCallBack, MNOpenSDKInterface.GetSoundModeConfigCallBack, MNOpenSDKInterface.SetSoundModeConfigCallBack {
    private final String TAG = DevSetVolumeActivity.class.getSimpleName();
    @BindView(R.id.sb_volume)
    SeekBar volumeSeekBar;
    @BindView(R.id.volume_ig)
    ImageView volumeIg;
    @BindView(R.id.volume_tv)
    TextView volumeTv;
    @BindView(R.id.iv_voice_reminder)
    ImageView ivVoiceReminder;
    @BindView(R.id.rl_voice_reminder_lay)
    RelativeLayout rlVoiceReminderLay;
    @BindView(R.id.iv_dev_offline)
    ImageView ivDevOffline;
    @BindView(R.id.rl_dev_offline_lay)
    RelativeLayout rlDevOfflineLay;
    private LoadingDialog loadingDialog;


    boolean offlineVoiceEnable = false, silentModeSwitch = true;
    boolean getVolumeFinished = false, getSoundFinished = false;
    private DevicesBean mDevice;

    @Override
    protected int setViewLayout() {
        return R.layout.activity_devset_volume;
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.set_volume));
        mDevice = (DevicesBean) getIntent().getSerializableExtra("device");
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
        volumeSeekBar.setOnSeekBarChangeListener(this);
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        //TODO 获取设备音频大小
        MNOpenSDK.getAudioOutputVolume(mDevice.getSn(), this);
        //TODO 获取设备静音和离线语音配置
        MNOpenSDK.getSoundModeConfig(mDevice.getSn(), this);
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

    public AudioOutputSetBean getAudioOutputCfg(int volume) {
        AudioOutputSetBean bean = new AudioOutputSetBean();
        bean.setChannel(0);
        bean.setAudioOutputVolume(volume);
        return bean;
    }

    public void setVolumeConfig(int volume) {
        loadingDialog.show();
        MNOpenSDK.setAudioOutputVolume(mDevice.getSn(), getAudioOutputCfg(volume), this);
    }

    public void setDevSound() {
        loadingDialog.show();

        MNOpenSDK.setSoundModeConfig(mDevice.getSn(), silentModeSwitch, offlineVoiceEnable, this);
    }

    public void dismissDlg() {
        if (loadingDialog != null && getSoundFinished && getVolumeFinished) {
            loadingDialog.dismiss();
        }
    }

    public void setDismissDlg() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        volumeTv.setText(progress + "%");
        if (progress == 0) {
            volumeIg.setImageResource(R.mipmap.set_sound_icon_s);
        } else {
            volumeIg.setImageResource(R.mipmap.set_sound_icon_big);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        setVolumeConfig(Math.round(volumeSeekBar.getProgress()));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick({R.id.rl_voice_reminder_lay, R.id.rl_dev_offline_lay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_voice_reminder_lay:
                String tag = (String) ivVoiceReminder.getTag();
                if ("off".equals(tag)) {
                    silentModeSwitch = false;
                    ivVoiceReminder.setTag("on");
                    ivVoiceReminder.setImageResource(R.mipmap.st_switch_on);
                    setDevSound();
                } else {
                    silentModeSwitch = true;
                    ivVoiceReminder.setTag("off");
                    ivVoiceReminder.setImageResource(R.mipmap.st_switch_off);
                    setDevSound();
                }
                break;
            case R.id.rl_dev_offline_lay:
                String offTag = (String) ivDevOffline.getTag();
                if ("off".equals(offTag)) {
                    offlineVoiceEnable = true;
                    ivDevOffline.setTag("on");
                    ivDevOffline.setImageResource(R.mipmap.st_switch_on);
                    setDevSound();
                } else {
                    offlineVoiceEnable = false;
                    ivDevOffline.setTag("off");
                    ivDevOffline.setImageResource(R.mipmap.st_switch_off);
                    setDevSound();
                }
                break;
        }
    }

    @Override
    public void onGetAudioOutput(AudioOutputBean bean) {
        //TODO 获取设备音频大小成功
        int volume = 50;
        if (bean.isResult() && bean.getParams() != null) {
            volume = bean.getParams().getAudioOutputVolume();
        }
        getVolumeFinished = true;
        dismissDlg();
        volumeSeekBar.setProgress(volume);
    }

    @Override
    public void onSetAudioOutput(BaseResult bean) {
        //TODO 设置设备音频大小成功
        if (bean.isResult()) {
            setDismissDlg();
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_successfully));
        } else {
            ToastUtils.MyToastBottom(getString(R.string.dev_set_up_failed));
        }
    }

    @Override
    public void onGetSoundModeConfig(DevSoundBean bean) {
        //TODO 获取设备静音和离线语音配置
        if (bean.isResult() && bean.getParams() != null) {
            silentModeSwitch = bean.getParams().isSilentMode();// 语音提醒
            offlineVoiceEnable = bean.getParams().isVoiceEnable();// 离线语音提醒
        }

        if (silentModeSwitch) {
            ivVoiceReminder.setTag("off");
            ivVoiceReminder.setImageResource(R.mipmap.st_switch_off);
        } else {
            ivVoiceReminder.setTag("on");
            ivVoiceReminder.setImageResource(R.mipmap.st_switch_on);
        }

        if (offlineVoiceEnable) {
            ivDevOffline.setTag("on");
            ivDevOffline.setImageResource(R.mipmap.st_switch_on);
        } else {
            ivDevOffline.setTag("off");
            ivDevOffline.setImageResource(R.mipmap.st_switch_off);
        }
        getSoundFinished = true;
        dismissDlg();
    }

    @Override
    public void onSetSoundModeConfig(BaseResult bean) {
        //TODO 设置设备静音和离线语音配置
        setDismissDlg();
    }
}
