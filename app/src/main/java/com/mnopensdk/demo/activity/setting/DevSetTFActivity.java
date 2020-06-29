package com.mnopensdk.demo.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.mnopensdk.demo.event.RefreshNvrIpcExistEvent;
import com.mnopensdk.demo.fragment.DevNvrTfRecordSetFragment;
import com.mnopensdk.demo.utils.LogUtil;
import com.mnopensdk.demo.utils.MTimer;
import com.mnopensdk.demo.utils.StatusBarUtil;
import com.mnopensdk.demo.utils.TFCardUtils;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mnopensdk.demo.widget.RuleAlertDialog;
import com.mnopensdk.demo.widget.TFFormatDlg;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.setting.BaseResult;
import com.mn.bean.setting.TFStateConfigBean;
import com.mnopensdk.demo.bean.NvrIpcStateBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import MNSDK.MNEtsTtunelProcessor;
import MNSDK.MNOpenSDK;
import MNSDK.inface.IMNEtsTunnelFace;
import MNSDK.inface.MNOpenSDKInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.mnopensdk.demo.R;
import com.google.android.material.tabs.TabLayout;

public class DevSetTFActivity extends AppCompatActivity implements MTimer.OnMTimerListener, TFFormatDlg.OnFormatLinstener, IMNEtsTunnelFace, MNOpenSDKInterface.TFStorageFormatCallBack {
    private final String TAG = DevSetTFActivity.class.getSimpleName();
    @BindView(R.id.hv_sd_size)
    SeekBar hvSdSize;
    @BindView(R.id.tv_sd_size)
    TextView tvSdSize;
    @BindView(R.id.tf_size_lay)
    LinearLayout tfSizeLay;
    @BindView(R.id.btn_format)
    Button btnFormat;
    @BindView(R.id.format_lay)
    RelativeLayout formatLay;
    @BindView(R.id.progress_format)
    ProgressBar progressFormat;
    @BindView(R.id.tv_should_format)
    TextView tvShouldFormat;
    @BindView(R.id.tv_total_title_1)
    TextView tvTotalTitle1;
    @BindView(R.id.rl_total_title_lay_1)
    RelativeLayout rlTotalTitleLay1;
    @BindView(R.id.tv_total_title_2)
    TextView tvTotalTitle2;
    @BindView(R.id.tv_sd_size_2)
    TextView tvSdSize2;
    @BindView(R.id.hv_sd_size_2)
    SeekBar hvSdSize2;
    @BindView(R.id.rl_total_title_lay_2)
    RelativeLayout rlTotalTitleLay2;


    private static DevSetTFActivity instance;
    @BindView(R.id.ll_main_lay)
    LinearLayout llMainLay;
    @BindView(R.id.btn_try_query)
    Button btnTryQuery;
    @BindView(R.id.rl_net_err_lay)
    RelativeLayout rlNetErrLay;
    @BindView(R.id.tv_msg_tip)
    TextView tvMsgTip;
    @BindView(R.id.tv_tfcard_format)
    TextView tvTfcardFormat;
    @BindView(R.id.nvr_msg_tablayout)
    TabLayout nvrMsgTablayout;
    @BindView(R.id.ui_container)
    FrameLayout uiContainer;
    @BindView(R.id.nvr_msg_pager)
    ViewPager nvrMsgPager;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.activity_base_title_rl)
    RelativeLayout activityBaseTitleRl;
    @BindView(R.id.image_lay)
    LinearLayout imageLay;


    private TFFormatDlg tfFormatDlg;
    private LoadingDialog loadingDialog;

    private List<String> mPageTitles = new ArrayList<>();
    private List<DevNvrTfRecordSetFragment> mFrameList = new ArrayList<>();
    private MyFragmentAdapter mAdapter;

    /**
     * 已插卡的根路径，为了查卡的用量需保存
     */
    private String storagePathName;
    //本地存储
    private DevicesBean device;
    private String deviceSn;
    private MTimer mTimer;
    private int devType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nvr_tf_card_info);
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, activityBaseTitleRl);
        EventBus.getDefault().register(this);
        MNEtsTtunelProcessor.getInstance().register(this);
        processExtraData();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData();
    }

    private void processExtraData() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setTimeOut(15 * 1000);

        initView();
        requestWithMsgTunnel();
        instance = this;
        tfFormatDlg = new TFFormatDlg(this);
        tfFormatDlg.setOnFormatLinstener(this);
    }

    public static DevSetTFActivity getInstance() {
        return instance;
    }

    private void initView() {
        device = (DevicesBean) getIntent().getSerializableExtra("device");
        if (device != null) {
            deviceSn = device.getSn();
            devType = device.getType();
        }

        hvSdSize.setOnTouchListener((v, event) -> {
            return true; // 禁止Seekbar 拖动点击
        });

        hvSdSize2.setOnTouchListener((v, event) -> {
            return true; // 禁止Seekbar 拖动点击
        });
        initDatas();
    }

    private void initDatas() {

        nvrMsgTablayout.setVisibility(View.GONE);
        DevNvrTfRecordSetFragment ipcFragment = DevNvrTfRecordSetFragment.newInstance(device, 0);
        mFrameList.add(ipcFragment);
        String channelName = String.format(getString(R.string.set_channel_no_name), 1);
        mPageTitles.add(channelName);

        mAdapter = new MyFragmentAdapter(getSupportFragmentManager(), mPageTitles, mFrameList);
        nvrMsgPager.setAdapter(mAdapter);

        nvrMsgTablayout.setupWithViewPager(nvrMsgPager);
        if (device.getChannels() < 5) {
            nvrMsgTablayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            nvrMsgTablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        nvrMsgPager.setCurrentItem(0);
        nvrMsgPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mFrameList.get(position) != null) {
                    mFrameList.get(position).onRefresh();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshNvrIpcExist(RefreshNvrIpcExistEvent event) {
        LogUtil.i(TAG, "==== onRefreshNvrIpcExist ====");
        if (loadingDialog != null) {
            loadingDialog.show();
        }
        //ConfigManager.getRemoteDevice(device.getSn());
    }

    @Override
    public void OnEtsTunnel(String uuid, String data, int length) {
        runOnUiThread(() -> {
            try {
                JSONObject dataJson = new JSONObject(data);
                int id = dataJson.getInt("id");
                if (id == 4004) {
                    if (loadingDialog != null) {
                        loadingDialog.dismiss();
                    }
                    ArrayList<NvrIpcStateBean> ipcStateList = NvrIpcStateBean.converRemoteJson(data, device.getChannels());
                    if (ipcStateList != null && ipcStateList.size() != 0) {
                        for (NvrIpcStateBean ipcStateBean : ipcStateList) {
                            int channelId = ipcStateBean.getChannelId();
                            if (ipcStateBean.isEnable() && ipcStateBean.getNetLogin() != 0) {
                                mFrameList.get(channelId).setDeviceIsExist();
                            } else {
                                mFrameList.get(channelId).setDeviceNotExistOrUnsupport();
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onTFStorageFormat(BaseResult bean) {
        // TODO TF卡格式化结果回调
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        isFormat = false;
        if (bean.isResult()) {
            btnFormat.setVisibility(View.VISIBLE);
            progressFormat.setVisibility(View.GONE);
            reboot();
            ToastUtils.MyToastBottom(getString(R.string.format_tf_suc));
        } else {
            btnFormat.setVisibility(View.VISIBLE);
            progressFormat.setVisibility(View.GONE);
            ToastUtils.MyToastBottom(getString(R.string.format_tf_failed));
        }
    }

    public class MyFragmentAdapter extends FragmentPagerAdapter {
        private List<String> lmPageTitles = new ArrayList<>();
        private List<DevNvrTfRecordSetFragment> lmFrameList = new ArrayList<>();

        public MyFragmentAdapter(FragmentManager fm, List<String> pageTitles, List<DevNvrTfRecordSetFragment> fragments) {
            super(fm);
            lmPageTitles.clear();
            lmFrameList.clear();
            lmPageTitles.addAll(pageTitles);
            lmFrameList.addAll(fragments);
        }

        @Override
        public DevNvrTfRecordSetFragment getItem(int position) {
            return lmFrameList.get(position);
        }

        @Override
        public int getCount() {
            return lmFrameList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return lmPageTitles.get(position);
        }

    }

    public void requestWithMsgTunnel() {
        loadingDialog.setTimeOut(15 * 1000);
        loadingDialog.show();
        llMainLay.setVisibility(View.GONE);
        rlNetErrLay.setVisibility(View.GONE);

        //TODO 获取TF卡名称、是否需要格式化、总容量、剩余容量
        MNOpenSDK.getTFStateConfig(deviceSn, new MNOpenSDKInterface.GetTFStorageCallBack() {
            @Override
            public void onGetTFStorage(TFStateConfigBean bean) {
                setTfCardStateView(bean);
                if (bean.isResult() && bean.getParams() != null && bean.getParams().size() > 0) {
                    storagePathName = bean.getParams().get(0).getName();
                }
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void setTfCardStateView(TFStateConfigBean mTfStatebean) {
        if (mTfStatebean == null) {
            // 没卡
            llMainLay.setVisibility(View.GONE);
            rlNetErrLay.setVisibility(View.VISIBLE);
            tvMsgTip.setText(getString(R.string.tv_not_memory_card));
            return;
        }
        String state = TFCardUtils.getTFCardState(mTfStatebean);
        if ("TimeOut".equals(state)) {
            llMainLay.setVisibility(View.GONE);
            rlNetErrLay.setVisibility(View.VISIBLE);
            tvMsgTip.setText(getString(R.string.net_err_and_try));
        } else if ("Failed".equals(state) || "EtsNotOnline".equals(state)) {
            llMainLay.setVisibility(View.GONE);
            rlNetErrLay.setVisibility(View.VISIBLE);
            tvMsgTip.setText(getString(R.string.net_err_and_try));
        } else if ("ProtocolRequestFailed".equals(state)) {

        } else if ("NULL".equals(state)) {
            // 没卡
            llMainLay.setVisibility(View.GONE);
            rlNetErrLay.setVisibility(View.VISIBLE);
            tvMsgTip.setText(getString(R.string.tv_not_memory_card));
        } else if ("Reinsert".equals(state)) {
            // 卡没插好
            llMainLay.setVisibility(View.GONE);
            rlNetErrLay.setVisibility(View.VISIBLE);
            tvMsgTip.setText(getString(R.string.tv_card_not_plugged_in));
        } else if ("Normal".equals(state)) {
            // 有卡 格式正常  并且支持格式化
            llMainLay.setVisibility(View.VISIBLE);
            rlNetErrLay.setVisibility(View.GONE);
            formatLay.setVisibility(View.VISIBLE);
            setTfSizeData(mTfStatebean);
        } else if ("AutoFormat".equals(state)) {
            // 有卡 格式正常  并且只能自动格式化
            formatLay.setVisibility(View.GONE);
            llMainLay.setVisibility(View.VISIBLE);
            rlNetErrLay.setVisibility(View.GONE);
            setTfSizeData(mTfStatebean);
        } else if ("NeedFormat".equals(state)) {
            // 有卡 需要格式化
            llMainLay.setVisibility(View.VISIBLE);
            rlNetErrLay.setVisibility(View.GONE);
            formatLay.setVisibility(View.VISIBLE);
            tvShouldFormat.setVisibility(View.VISIBLE);
            tfSizeLay.setVisibility(View.GONE);
        } else /*if ("UnSupported".equals(state))*/ {
            // 有卡 不正常并且不能手动格式化
            llMainLay.setVisibility(View.GONE);
            rlNetErrLay.setVisibility(View.VISIBLE);
            tvMsgTip.setText(getString(R.string.tv_unsupported_memory_card));
        }
    }

    public void setTfSizeData(TFStateConfigBean bean) {

        if (bean.getParams().size() > 1) {
//            tvTotalTitle1.setText(getString(R.string.capacity_video_tv));
            rlTotalTitleLay2.setVisibility(View.VISIBLE);
        } else {
//            tvTotalTitle1.setVisibility(View.GONE);
            rlTotalTitleLay2.setVisibility(View.GONE);
        }

        TFStateConfigBean.ParamsBean paramsBean = bean.getParams().get(0);
        long freeSpace = paramsBean.getFreeSpace();
        long totalSpace = paramsBean.getTotalSpace();
        tvSdSize.setText(convertFileSize(totalSpace - freeSpace) + "/" + convertFileSize(totalSpace));
        double usedSpace = totalSpace - freeSpace;
        double progress = usedSpace / totalSpace;
        LogUtil.i(TAG, "hvSdSiz (sum - emp)/sun =" + (usedSpace) + " / " + totalSpace + " = " + 30000 * progress);
        hvSdSize.setProgress((int) (30000 * progress));

        if (bean.getParams().size() > 1) {
            TFStateConfigBean.ParamsBean paramsBean1 = bean.getParams().get(1);
            long freeSpace2 = paramsBean1.getFreeSpace();
            long totalSpace2 = paramsBean1.getTotalSpace();

            tvSdSize2.setText(convertFileSize(totalSpace2 - freeSpace2) + "/" + convertFileSize(totalSpace2));

            double usedSpace2 = totalSpace2 - freeSpace2;
            double progress2 = usedSpace2 / totalSpace2;
            LogUtil.i(TAG, "hvSdSiz2 (sum - emp)/sun =" + (usedSpace2) + " / " + totalSpace2 + " = " + 30000 * progress2);
            hvSdSize2.setProgress((int) (30000 * progress));
        }
    }


    public void setTFStorageFormatting() {
        loadingDialog.setTimeOut(60 * 1000);
        MNOpenSDK.setTFStorageFormatting(deviceSn, storagePathName, this);
    }

    public String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        long tb = gb * 1024;
        String formatSize = "";
        if (size >= tb) {
            formatSize = String.format("%.02f TB", (float) size / tb);
        } else if (size >= gb) {
            formatSize = String.format("%.02f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            formatSize = String.format(f > 100 ? "%.02f MB" : "%.02f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            formatSize = String.format(f > 100 ? "%.02f KB" : "%.02f KB", f);
        } else
            formatSize = String.format("%d B", size);
        return formatSize;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MNEtsTtunelProcessor.getInstance().unregister(this);
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        instance = null;
    }

    private void reboot() {
        MNOpenSDK.rebootDevice(deviceSn);
        new RuleAlertDialog(this).builder().setCanceledOnTouchOutside(false).
                setTitle(getString(R.string.tip_title)).setMsg(getString(R.string.format_tf_suc_and_reboot)).setMsgAlignStyle(Gravity.CENTER).
                setOkButton(getString(R.string.buy_person_face_i_know), null).
                setOnDismissListener((dialog) -> {
                    finish();
                    ToastUtils.MyToastCenter(getString(R.string.waiting_device_restart));
                }).show();
    }

    public void onBackClick() {
        if (isFormat) {
            ToastUtils.MyToastBottom(getString(R.string.formating_tf_suc));
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.btn_format, R.id.btn_try_query, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_format:
                tfFormatDlg.show();
                break;
            case R.id.btn_try_query:
                requestWithMsgTunnel();
                break;
            case R.id.iv_back:
                onBackClick();
                break;
        }
    }

    @Override
    public void OnMTimerFinished(int cookieId) {
        runOnUiThread(() -> {
            if (isFormat) {
                isFormat = false;
                btnFormat.setVisibility(View.VISIBLE);
                progressFormat.setVisibility(View.GONE);
                ToastUtils.MyToastBottom(getString(R.string.formating_tf_timeout));
            }
        });
    }

    private boolean isFormat = false;

    @Override
    public void OnClickOK() {
        if (device.getType() == 4) {
            formatTfCard();
        } else {
            mFrameList.get(0).setAlarmRecordAndFormat();
        }
    }

    public void formatTfCard() {
        if (storagePathName != null) {
            if (mTimer == null) {
                mTimer = new MTimer(DevSetTFActivity.this);
            } else {
                mTimer.stopTimer();
            }
            mTimer.startTimer(60 * 1000);
            isFormat = true;
            setTFStorageFormatting();
            btnFormat.setVisibility(View.GONE);
            progressFormat.setVisibility(View.VISIBLE);
        } else {
            ToastUtils.MyToastBottom(getString(R.string.format_tf_failed));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFormatTfCard(String action) {
        LogUtil.i(TAG, "action : " + action);
        if ("format_event".equals(action)) {
            formatTfCard();
        }
    }
}
