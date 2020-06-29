package com.mnopensdk.demo.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mnopensdk.demo.BaseApplication;
import com.mnopensdk.demo.Constants;
import com.mnopensdk.demo.bean.BindDevBeean;
import com.mnopensdk.demo.utils.CountDownTimerUtils;
import com.mnopensdk.demo.utils.LogUtil;
import com.mnopensdk.demo.utils.MTimerTask;
import com.mnopensdk.demo.utils.StatusBarUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.CustomDialog;
import com.mnopensdk.demo.widget.RuleAlertDialog;
import com.mn.bean.restfull.BaseBean;
import com.mn.tools.SoundWaveManager;
import com.mn.tools.WiredBindingManager;

import java.lang.ref.WeakReference;

import MNSDK.MNBindDevProcessor;
import MNSDK.MNKit;
import MNSDK.inface.IMNBindDeviceFace;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mnopensdk.demo.R;
import com.mn.encoder.VoicePlayer;
import com.mn.encoder.VoicePlayerListener;

public class BindDevSendSoundWifiActivity extends Activity implements VoicePlayerListener, IMNBindDeviceFace, MNKitInterface.DeviceBindViewCallBack {
    @BindView(R.id.receive_back)
    ImageView receiveBack;
    @BindView(R.id.receive_title)
    LinearLayout receiveTitle;
    @BindView(R.id.receive_top)
    RelativeLayout receiveTop;
    @BindView(R.id.next_ok)
    Button nextOk;
    @BindView(R.id.iv_volume)
    ImageView ivVolume;
    @BindView(R.id.send_before)
    LinearLayout sendBefore;
    @BindView(R.id.sending)
    TextView sending;
    @BindView(R.id.send_end)
    LinearLayout sendEnd;
    @BindView(R.id.iv_volume_gif)
    ImageView ivVolumeGif;
    @BindView(R.id.loading_lo)
    TextView tv;
    @BindView(R.id.iv_whait)
    ImageView ivWhait;
    @BindView(R.id.id_wiat)
    LinearLayout llWiat;
    @BindView(R.id.tvCount)
    TextView tvConnect;
    private static BindDevSendSoundWifiActivity mActivity;

    protected MyHandler mViewHandler = new MyHandler(this);
    boolean isEnd = true;
    private RuleAlertDialog ruleAlertDialog;
    int duration;
    String ssid;
    String password;
    MTimerTask mTimerTask;
    String deviceSn = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifi_receive_new);
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, receiveTop);
        BaseApplication.getInstance().mActivityStack.addActivity(this);

        MNBindDevProcessor.getInstance().register(this);

        SoundWaveManager.getIntance().setListener(this);

        mActivity = this;
        ssid = getIntent().getStringExtra("ssid");
        password = getIntent().getStringExtra("password");

        ivVolume.setImageResource(R.mipmap.home_add_send_acoustic_click);
        audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

        // 有线设备绑定获取设备SN(Under wired binding, it will pass SN to enter)
        deviceSn = getIntent().getStringExtra("qrsn");

        if (!TextUtils.isEmpty(deviceSn)) {
            // 已经获取到SN，尝试有线绑定（Already obtained SN, try to wire bind）
            WiredBindingManager.getIntance().startWiredBinding(deviceSn, new WiredBindingManager.WiredBindingCallBack() {
                @Override
                public void onWiredBindingSuc(String sn) {
                    // 设备有连接上网络，开始绑定设备到账号（The device is connected to the network and begins to bind the device to the account）
                    MNKit.bindDeviceBySnAndVn(deviceSn, Constants.VN, 2,BindDevSendSoundWifiActivity.this);
                }

                @Override
                public void onWiredBindingFailed() {
                    // 设备没有有连接上网络，有线绑定失败（The device is not connected to the network, and the wire binding fails）
                    gotoFailedActivity();
                }
            });
        }
    }

    boolean isC = false;

    @OnClick({R.id.receive_back, R.id.next_ok, R.id.iv_volume})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.receive_back:
                initDialog();
                break;
            case R.id.next_ok:
                if (ruleAlertDialog == null) {
                    ruleAlertDialog = new RuleAlertDialog(this)
                            .builder()
                            .setCancelable(false)
                            .setTitle(getString(R.string.add_wifi_tip))
                            .setMsg(getString(R.string.add_wifi_listen))
                            .setOkButton(getString(R.string.add_widi_ok), v -> {
                                isC = true;
                                ivVolume.setVisibility(View.GONE);
                                nextOk.setVisibility(View.GONE);
                                sendEnd.setVisibility(View.GONE);
                                llWiat.setVisibility(View.VISIBLE);
                                tvConnect.setVisibility(View.VISIBLE);
                                if ("N2".equals(BindDevStep1Activity.ADD_DEV_TYPE) ||
                                        "N2s".equals(BindDevStep1Activity.ADD_DEV_TYPE)) {
                                    Glide.with(this).load(R.drawable.wifi_newconnect).into(ivWhait);
                                } else if ("DoorBell".equals(BindDevStep1Activity.ADD_DEV_TYPE)) {
                                    Glide.with(this).load(R.drawable.doorbell_wifi_wait).into(ivWhait);
                                } else if ("cloudHead".equals(BindDevStep1Activity.ADD_DEV_TYPE)) {
                                    Glide.with(this).load(R.drawable.yuntai).into(ivWhait);
                                } else if ("StrongBox".equals(BindDevStep1Activity.ADD_DEV_TYPE)) {
                                    Glide.with(this).load(R.drawable.add_box).into(ivWhait);
                                }
                                countDownTimerUtils.start();
                                if (!TextUtils.isEmpty(deviceSn)) {
                                    MNKit.bindDeviceBySnAndVn(deviceSn, Constants.VN, 2,this);
                                    mTimerTask.postDelayed(5000, 0);
                                }
                            }).setCancelButton(getString(R.string.label_cancel), null);
                }
                ruleAlertDialog.show();
                break;
            case R.id.iv_volume:
                if (isEnd) {
                    isEnd = false;
                    isonPlayEnd = true;
                    ivVolume.setVisibility(View.GONE);
                    // 发送声波
                    SoundWaveManager.getIntance().sendMsg(ssid, password);
                }
                break;
        }
    }

    boolean isOk = false;

    /**
     * 开始发送声波
     *
     * @param _player
     */
    @Override
    public void onPlayStart(VoicePlayer _player) {
        mViewHandler.post(() -> {
            duration = 0;
            if (ivVolumeGif.getVisibility() != View.VISIBLE) {
                ivVolumeGif.setVisibility(View.VISIBLE);
                sendBefore.setVisibility(View.GONE);
                sending.setVisibility(View.VISIBLE);
                sendEnd.setVisibility(View.GONE);
                nextOk.setBackgroundResource(R.mipmap.add_btn_no);
            }
            Glide.with(this).asGif().load(R.drawable.volume).into(ivVolumeGif);
        });

    }

    boolean isonPlayEnd = true;
    AudioManager audio;

    /**
     * 声波发送完成
     *
     * @param _player
     */
    @Override
    public void onPlayEnd(VoicePlayer _player) {
        if (isonPlayEnd) {
            isonPlayEnd = false;
            return;
        }
        runOnUiThread(() -> {
            isEnd = true;
            if (!isC) {
                sendEnd.setVisibility(View.VISIBLE);
            }
            nextOk.setBackgroundResource(R.mipmap.add_btn_yes);
            sending.setVisibility(View.GONE);
            ivVolume.setImageResource(R.mipmap.home_add_send_acoustic_click);
            nextOk.setEnabled(true);
            ivVolumeGif.setVisibility(View.GONE);
            ivVolume.setVisibility(View.VISIBLE);
        });
    }

    boolean isCome = true;

    /**
     * 绑定设备监听回调方法,当设备配合网络成功并且上线之后，会回调此方法
     *
     * @param pszJson
     * @param nlen
     */
    @Override
    public void OnRequestToBindDevice(String pszJson, int nlen) {
        LogUtil.i("", "OnRequestToBindDevice : " + pszJson);
        //{"deviceSn":	"MDAhAQEAbGUwNjFiMjIzOWMxNgAA","vn":"ABCDEF","bindState":3002}
        if (pszJson != null && !"".equals(pszJson)) {
            mViewHandler.sendEmptyMessage(1);
        } else {
            mViewHandler.sendEmptyMessage(0);
        }
        LogUtil.i("BindDevice", pszJson);
        Gson gson = new Gson();
        BindDevBeean bindDevBeean = gson.fromJson(pszJson, BindDevBeean.class);
        if (bindDevBeean == null) return;
        deviceSn = bindDevBeean.getDeviceSn();
        if (deviceSn == null || "".equals(deviceSn)) return;
        int bindState = bindDevBeean.getBindState();
        if (bindState == 0) {
            goRestHttp(bindDevBeean);
        } else {
            switch (bindState) {
                case 3000://没有被绑定
                    goRestHttp(bindDevBeean);
                    break;
                case 3001://设备已被其他用户绑定
                    mViewHandler.sendEmptyMessage(2);
                    break;
                case 3002://设备已经被自己绑定
                    mViewHandler.sendEmptyMessage(3);
                    break;
            }
        }
    }

    /**
     * 绑定设备
     *
     * @param bindDevBeean
     */
    private void goRestHttp(BindDevBeean bindDevBeean) {
        Constants.sn = deviceSn;
        String vn = bindDevBeean.getVn();
        if (isCome) {
            isCome = false;
            MNKit.bindDeviceBySnAndVn(deviceSn, vn, 2,this);
        }
    }

    /**
     * 绑定设备成功回调
     *
     * @param result
     */
    @Override
    public void onBindDeviceSuc(BaseBean result) {
        if (result != null) {
            int code = result.getCode();
            if (code == 2000) {
                Constants.sn = deviceSn;
                ToastUtils.MyToastCenter("绑定设备成功");
                if (HomeActivity.instance != null) {
                    HomeActivity.instance.onRefresh();
                }
                finish();
            } else if (code == 5001) {//已经被绑其他用户定
                ToastUtils.MyToastCenter("绑定已经被其他用户绑定");
                finish();
            } else if (code == 3000) {
                ToastUtils.MyToastCenter("invalid access_token");
            } else {//绑定失败操作
                ToastUtils.MyToastCenter("绑定设备失败");
                finish();
            }
        }
    }

    /**
     * 绑定设备失败回调
     *
     * @param msg
     */
    @Override
    public void onBindDeviceFailed(String msg) {
        ToastUtils.MyToast(getString(R.string.add_addfaiue));
        gotoFailedActivity();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<BindDevSendSoundWifiActivity> mConText;

        public MyHandler(BindDevSendSoundWifiActivity activity) {
            mConText = new WeakReference<BindDevSendSoundWifiActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mConText.get() != null) {
                if (msg.what == 1) {
                    if (mConText.get().ruleAlertDialog != null) {
                        mConText.get().ruleAlertDialog.dismiss();
                    }
                    ToastUtils.MyToastCenter(mConText.get().getString(R.string.bing_dev));
                } else if (msg.what == 0) {
                    if (mConText.get().ruleAlertDialog != null) {
                        mConText.get().ruleAlertDialog.dismiss();
                    }
                    ToastUtils.MyToastCenter("......");
                } else if (msg.what == 2) {//已经被绑其他用户定
                    ToastUtils.MyToastCenter("绑定已经被其他用户绑定");
                    mConText.get().finish();
                } else if (msg.what == 3) {//已经被自己绑定
                    Constants.sn = mConText.get().deviceSn;
                    ToastUtils.MyToastCenter("绑定设备成功");
                    mConText.get().finish();
                }
            }
        }
    }


    public static BindDevSendSoundWifiActivity getInstance() {
        return mActivity;
    }

    @Override
    public void finish() {
        super.finish();
        SoundWaveManager.getIntance().destroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MNBindDevProcessor.getInstance().unregister(this);
        BaseApplication.getInstance().mActivityStack.removeActivity(this);
        if (mTimerTask != null) {
            mTimerTask.stopPostDelay();
        }
    }

    CustomDialog customDialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                initDialog();
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, // 增加音量
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,//减少音量
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initDialog() {
        if (customDialog == null) {
            customDialog = new CustomDialog(this, CustomDialog.DIALOG_EXIT_ADD, () -> {
                customDialog.dismiss();
                finish();
            });
            if (!isFinishing()) {
                customDialog.show();
            }
        } else {
            if (!isFinishing()) {
                customDialog.show();
            }
        }
    }


    private final CountDownTimerUtils countDownTimerUtils = new CountDownTimerUtils(120000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            //为了优化所作的判断，防止界面不在了还在倒计时
            if (!isFinishing()) {
                String s = String.valueOf(millisUntilFinished / 1000) + "s";
                tv.setText(s);
            }
            if (isFinishing()) {
                cancel();
            }
        }

        @Override
        public void onFinish() {
            ToastUtils.MyToastCenter("绑定设备失败");
            gotoFailedActivity();
            finish();
        }
    };

    private void gotoFailedActivity() {
        runOnUiThread(() -> {
            Intent intent = new Intent(this, BIndDeviceFailedActivity.class);
            startActivity(intent);
        });
    }
}
