package com.mnopensdk.demo.activity.setting;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mn.bean.setting.AlarmAsossiatedBean;
import com.mn.bean.setting.BaseResult;

import MNSDK.MNOpenSDK;
import MNSDK.inface.MNOpenSDKInterface;
import butterknife.BindView;
import butterknife.OnClick;
import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2019/8/15.
 */

public class DevAlartModeActivity extends BaseActivity implements MNOpenSDKInterface.SetAlarmAsossiatedConfigCallBack {

    @BindView(R.id.iv_left_icon)
    ImageView ivLeftIcon;
    @BindView(R.id.t1)
    TextView t1;
    @BindView(R.id.t_t1)
    TextView tT1;
    @BindView(R.id.iv_select_1)
    ImageView ivSelect1;
    @BindView(R.id.rlLay1)
    RelativeLayout rlLay1;
    @BindView(R.id.iv_left_icon2)
    ImageView ivLeftIcon2;
    @BindView(R.id.t2)
    TextView t2;
    @BindView(R.id.t_t2)
    TextView tT2;
    @BindView(R.id.iv_select_2)
    ImageView ivSelect2;
    @BindView(R.id.rlLay2)
    RelativeLayout rlLay2;
    @BindView(R.id.iv_left_icon4)
    ImageView ivLeftIcon4;
    @BindView(R.id.t3)
    TextView t3;
    @BindView(R.id.t_t3)
    TextView tT3;
    @BindView(R.id.iv_select_3)
    ImageView ivSelect3;
    @BindView(R.id.rlLay3)
    RelativeLayout rlLay3;
    String devSn;
    LoadingDialog loadingDialog;

    int mLightType = -1;

    @Override
    protected int setViewLayout() {
        return R.layout.activity_devalart_mode;
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.dev_lighting_mode));
        devSn = getIntent().getStringExtra("devSn");
        mLightType = getIntent().getIntExtra("lightType", -1);
        // 0 红外夜视;1 星光全彩2; 双光警戒;
        if (mLightType == 0) {
            ivSelect1.setVisibility(View.VISIBLE);
            ivSelect2.setVisibility(View.INVISIBLE);
            ivSelect3.setVisibility(View.INVISIBLE);
        } else if (mLightType == 1) {
            ivSelect1.setVisibility(View.INVISIBLE);
            ivSelect2.setVisibility(View.VISIBLE);
            ivSelect3.setVisibility(View.INVISIBLE);
        } else if (mLightType == 2) {
            ivSelect1.setVisibility(View.INVISIBLE);
            ivSelect2.setVisibility(View.INVISIBLE);
            ivSelect3.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        MNOpenSDK.getAlarmAsossiatedConfig(devSn, new MNOpenSDKInterface.GetAlarmAsossiatedConfigCallBack() {
            @Override
            public void onGetAlarmAsossiatedConfig(AlarmAsossiatedBean bean) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                if (bean != null && bean.getParams() != null && bean.isResult()) {

                    mLightType = bean.getParams().getLightType();
                    try {
                        audioEnable = bean.getParams().isAudioEnable();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // 0 红外夜视;1 星光全彩2; 双光警戒;（之前是     // 0 星光全彩;1红外夜视 2; 双光警戒）
                    if (mLightType == 0) {//红外夜视
                        ivSelect1.setVisibility(View.VISIBLE);
                        ivSelect2.setVisibility(View.INVISIBLE);
                        ivSelect3.setVisibility(View.INVISIBLE);
                    } else if (mLightType == 1) {//星光全彩
                        ivSelect1.setVisibility(View.INVISIBLE);
                        ivSelect2.setVisibility(View.VISIBLE);
                        ivSelect3.setVisibility(View.INVISIBLE);
                    } else if (mLightType == 2) {//2 双光警戒;
                        ivSelect1.setVisibility(View.INVISIBLE);
                        ivSelect2.setVisibility(View.INVISIBLE);
                        ivSelect3.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    protected void onBackKeyDown(boolean b) {
        Intent intent = new Intent();
        intent.putExtra("lightType", mLightType);
        setResult(200, intent);
        finish();
    }

    @Override
    protected void onRightTvClick() {

    }

    @Override
    protected void onLeftTvClick() {

    }

    boolean audioEnable;

    int setAlartModeTip = -1;

    @OnClick({R.id.rlLay1, R.id.rlLay2, R.id.rlLay3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlLay1://0 红外夜视;
                loadingDialog.show();
                setAlartModeTip = 0;
                MNOpenSDK.setAlarmAsossiatedConfig(devSn, 0, audioEnable, this);
                break;
            case R.id.rlLay2://// 1 星光全彩
                loadingDialog.show();
                setAlartModeTip = 1;
                MNOpenSDK.setAlarmAsossiatedConfig(devSn, 1, audioEnable, this);
                break;
            case R.id.rlLay3://2 双光警戒;
                loadingDialog.show();
                setAlartModeTip = 2;
                MNOpenSDK.setAlarmAsossiatedConfig(devSn, 2, audioEnable, this);
                break;
        }
    }

    @Override
    public void onSetAlarmAsossiatedConfig(BaseResult bean) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (bean != null && bean.isResult()) {
            if (setAlartModeTip == 0) {//红外夜视
                mLightType = 0;
                ivSelect1.setVisibility(View.VISIBLE);
                ivSelect2.setVisibility(View.INVISIBLE);
                ivSelect3.setVisibility(View.INVISIBLE);
            } else if (setAlartModeTip == 1) {//星光全彩
                mLightType = 1;
                ivSelect1.setVisibility(View.INVISIBLE);
                ivSelect2.setVisibility(View.VISIBLE);
                ivSelect3.setVisibility(View.INVISIBLE);
            } else if (setAlartModeTip == 2) {//2 双光警戒;
                mLightType = 2;
                ivSelect1.setVisibility(View.INVISIBLE);
                ivSelect2.setVisibility(View.INVISIBLE);
                ivSelect3.setVisibility(View.VISIBLE);
            }
        }
    }
}
