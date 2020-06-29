package com.mnopensdk.demo.activity.setting;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mn.bean.restfull.FirmwareVerBean;
import com.mn.bean.setting.UpgradeStateBean;
import com.mnopensdk.demo.R;
import com.mnopensdk.demo.utils.CountDownTimerUtils;
import com.mnopensdk.demo.utils.LogUtil;
import com.mnopensdk.demo.utils.StatusBarUtil;
import com.mnopensdk.demo.utils.ToastUtils;

import MNSDK.MNOpenSDK;
import MNSDK.inface.MNOpenSDKInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WIN on 2017/9/25.
 */

public class DevSetUpFirmActivity extends AppCompatActivity {
    @BindView(R.id.start_updata)
    Button startUpdata;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.activity_base_title_rl)
    RelativeLayout activityBaseTitleRl;
    @BindView(R.id.tv_newestVersion)
    TextView tvNewestVersion;
    private String TAG = DevSetUpFirmActivity.class.getSimpleName();
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_ver)
    TextView tvCurrentVer;   //当前版本
    @BindView(R.id.tv_upver)
    TextView tvLatestVer; // 最新版本
    @BindView(R.id.tv_desc)
    TextView tvDesc; // 最新版本

    private FirmwareVerBean mVerBean;
    private ProgressDialog pDialog;
    private String _devName;
    private String _devCurrentVer;
    private String _devlatestVer;
    private String _devDesc;

    private int downloadProgress = 0;
    private boolean upFinished = false;
    private boolean updataState = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_firmware);
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, activityBaseTitleRl);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    private long lPkgSize;//转化为long型
    private String pPkgUrl;
    private String pszPkgMD5;
    private String sn;

    private void initView() {
        if (getIntent() != null) {
            Intent intent = getIntent();
            mVerBean = (FirmwareVerBean) getIntent().getSerializableExtra("firmwareVerBean");
            //可以换成序列号对象
            _devName = intent.getStringExtra("dev_name");
            _devCurrentVer = mVerBean.getCurrentVersion();
            if (mVerBean.getFirmwares() != null && mVerBean.getFirmwares().size() != 0) {
                _devlatestVer = mVerBean.getFirmwares().get(0).getMax_version();
                _devDesc = mVerBean.getFirmwares().get(0).getDesc();
                pPkgUrl = mVerBean.getFirmwares().get(0).getUrl();
                pszPkgMD5 = mVerBean.getFirmwares().get(0).getMd5();
                lPkgSize = (long) mVerBean.getFirmwares().get(0).getSize();
            }
            sn = intent.getStringExtra("uuid");

            LogUtil.i("dddddd", sn + "," + pPkgUrl + "," + pszPkgMD5 + "," + lPkgSize);
            if (_devDesc != null && _devDesc.contains("\\r\\n")) {
                tvDesc.setText(_devDesc.replace("\\r\\n", "\n"));
            } else {
                tvDesc.setText(_devDesc);
            }
            tvName.setText(_devName);
            tvCurrentVer.setText(_devCurrentVer);
            tvLatestVer.setText(_devlatestVer);
        }
    }

    public void onBackClick() {
        if (updataState) {
            ToastUtils.MyToast(getString(R.string.update_fire));
            return;
        }
        this.finish();
    }

    @OnClick({R.id.iv_back, R.id.start_updata})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackClick();
                break;
            case R.id.start_updata:
                update();
                break;
        }
    }


    private void initGosucc() {
        upFinished = true;
        updataState = false;
        pDialog.setProgress(100);
        pDialog.dismiss();
        ToastUtils.MyToast(getResources().getString(R.string.update_success));

        new Handler().postDelayed(() -> {
            destroyActivitys();
        }, 1500);
    }

    private void destroyActivitys() {
        finish();
    }

    public void update() {
        try {
            // 点击升级弹出是否升级的对话框
            Builder builder = new Builder(DevSetUpFirmActivity.this);
            builder.setTitle(getString(R.string.fire_go));
            builder.setMessage(getString(R.string.fireto));
            builder.setPositiveButton(getString(R.string.label_ok), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        // 确认升级，关闭提示，显示进度提示条，后台下载数据
                        dialog.dismiss();
                        updataState = true;
                        MNOpenSDK.upgradeFirmware(sn, pPkgUrl, lPkgSize, pszPkgMD5);
                        getUpgradeProgress(sn);
                        showProgress();
                        LogUtil.i("Update", "result：" + sn + ",,," + pPkgUrl + ",,," + lPkgSize + ",,,," + pszPkgMD5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.label_cancel), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    public void showProgress() {
        try {
            pDialog = new ProgressDialog(DevSetUpFirmActivity.this);
            pDialog.setTitle(getResources().getString(R.string.st_download_data));
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.show();
            countDownTimerUtils.start();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    private final CountDownTimerUtils countDownTimerUtils = new CountDownTimerUtils(350 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (!isFinishing()) {
                String s = String.valueOf(millisUntilFinished / 1000) + "s";
                Log.i(TAG, s);
                //setRight(s);
            }
            if (isFinishing()) {
                cancel();
            }
        }

        @Override
        public void onFinish() {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (downloadProgress < 100) {
                LogUtil.i("Connect", "没有升级成功");
                ToastUtils.MyToast(getString(R.string.st_device_update_prompt_fail));
            }
            destroyActivitys();
        }
    };

    /**
     * 升级是否失败
     *
     * @param status 状态
     * @return true 失败
     */
    private boolean failStatus(String status) {
        String[] failStr = {"Invalid", "Failed", "Cancelled", "NotEnoughMemory", "FileUnmatch"};
        for (int i = 0; i < failStr.length; i++) {
            if (status.equals(failStr[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: // 升级时阻止退出
                if (updataState) {
                    ToastUtils.MyToast(getString(R.string.update_fire));
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }


    public void getUpgradeProgress(String sn) {
        MNOpenSDK.getUpgradeState(sn, new MNOpenSDKInterface.GetUpgradeStateCallBack() {
            @Override
            public void onGetUpgradeState(String sn, UpgradeStateBean bean) {
                if (bean.isResult() && bean.getParams() != null) {
                    try {
                        int progress = bean.getParams().getProgress();
                        String state = bean.getParams().getState();
                        LogUtil.i(TAG, "getUpgradeState : " + state + " , " + progress);

                        if (downloadProgress != progress) {
                            downloadProgress = progress;
                        }
                        if (pDialog == null) return;
                        if (progress > 0) {
                            pDialog.setIndeterminate(false);
                        }
                        pDialog.setProgress(progress);
                        if ("Preparing".equals(state) || "".equals(state)) {
                            //准备下载
                            pDialog.setTitle(getResources().getString(R.string.downding_devFirm));
                            getUpgradeProgress(sn);
                        } else if ("Downloading".equals(state)) {
                            // 正在下载， progress下载进度
                            pDialog.setTitle(getResources().getString(R.string.downding_devFirm));
                            getUpgradeProgress(sn);
                        } else if ("Upgrading".equals(state)) {
                            // 正在升级， progress升级进度
                            pDialog.setTitle(getResources().getString(R.string.up_devFirm));
                            getUpgradeProgress(sn);
                            //todo 有些p2会出现这种组合
                            if (progress == 100) {
                                // 成功
                                pDialog.setTitle(getResources().getString(R.string.update_success));
                                initGosucc();
                            }
                        } else if ("Succeeded".equals(state)) {
                            // 成功
                            pDialog.setTitle(getResources().getString(R.string.update_success));
                            initGosucc();
                        } else if (failStatus(state)) {
                            // 失败
                            upFinished = true;
                            updataState = false;
                            LogUtil.i(TAG, "st_device_update_prompt_fail refreshUi2 failStatus(status) 升级失败" + getString(R.string.st_device_update_prompt_fail));
                            ToastUtils.MyToast(getResources().getString(R.string.st_device_update_prompt_fail));
                            destroyActivitys();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getUpgradeProgress(sn);
                }
            }
        });
    }

}
