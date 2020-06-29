package com.mnopensdk.demo.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mnopensdk.demo.GlideApp;
import com.mnopensdk.demo.utils.StatusBarUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mnopensdk.demo.widget.RuleAlertDialog;
import com.mn.bean.restfull.BaseBean;
import com.mn.bean.restfull.DevicesBean;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mnopensdk.demo.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2019/1/11 0011.
 */

public class ShareOtherToMeActivity extends AppCompatActivity implements MNKitInterface.UnBindShareDeviceCallBack {
    DevicesBean mDevicesBean;
    @BindView(R.id.iv_head_image)
    CircleImageView ivHeadImage;
    @BindView(R.id.tv_uerName)
    TextView tvUerName;
    @BindView(R.id.tv_dev_name)
    TextView tvDevName;
    @BindView(R.id.bt_del_share)
    Button btDelShare;
    @BindView(R.id.rl_name_lay)
    RelativeLayout rlNameLay;
    @BindView(R.id.tv_remaining_time)
    TextView tvRemainingTime;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_base_lay)
    RelativeLayout rlBaseLay;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_other_to_me);
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, rlBaseLay);
        loadingDialog = new LoadingDialog(this);
        initData();
    }

    private void initData() {
        mDevicesBean = (DevicesBean) getIntent().getSerializableExtra("shareDev");
        tvUerName.setText(mDevicesBean.getFrom());
        tvDevName.setText(mDevicesBean.getDev_name());
        String logoUrl = mDevicesBean.getLogo();
        if (logoUrl != null)
            GlideApp.with(this).load(logoUrl).dontAnimate().placeholder(R.mipmap.share_head1).into(ivHeadImage);

        long remainTime = mDevicesBean.getRemain_time();
        if (remainTime == -1) {
            tvRemainingTime.setText(getString(R.string.tv_sharing_time_1));
        } else if (remainTime <= 60) {
            tvRemainingTime.setText(getString(R.string.tv_less_than_1_minute));
        } else if (remainTime <= 60 * 60) {
            tvRemainingTime.setText(String.format(getString(R.string.tv_remaining_time_minute), remainTime / 60));
        } else {
            tvRemainingTime.setText(String.format(getString(R.string.tv_remaining_time_hour_minute), remainTime / (60 * 60), remainTime / 60 % 60));
        }
    }

    @OnClick({R.id.bt_del_share, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_del_share:
                new RuleAlertDialog(this).builder().setCancelable(false).
                        setTitle(getString(R.string.delete_share_dev)).
                        setMsg(getString(R.string.delete_ask) + ":" + mDevicesBean.getDev_name() + "?").
                        setOkButton(getString(R.string.label_ok), v1 -> {
                            loadingDialog.show();
                            MNKit.unbindSharedDevice(mDevicesBean.getId(), this);
                        }).setCancelButton(getString(R.string.label_cancel), null).show();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onUnBindShareDeviceFailed(String localizedMessage) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        ToastUtils.MyToastBottom(getString(R.string.net_err_and_try));
    }

    @Override
    public void onUnBindShareDeviceSuc(BaseBean response) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }

        setResult(200);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
