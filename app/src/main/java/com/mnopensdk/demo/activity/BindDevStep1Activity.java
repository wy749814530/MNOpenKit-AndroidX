package com.mnopensdk.demo.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.OnClick;
import com.mnopensdk.demo.R;
import com.mnopensdk.demo.base.BaseActivity;

/**
 * Created by Administrator on 2018/11/24 0024.
 */

public class BindDevStep1Activity extends BaseActivity {
    public static String ADD_DEV_TYPE = "N2";
    @BindView(R.id.btn_sound)
    Button btnSound;
    @BindView(R.id.btn_qr_code)
    Button btnQrCode;

    @Override
    protected int setViewLayout() {
        return R.layout.activity_bind_dev_1;
    }

    @Override
    protected void initData() {
        setTitle("选择添加方式");
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {

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

    @OnClick({R.id.btn_sound, R.id.btn_qr_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sound:
                startActivity(new Intent(this, BindDevSelectWiFiActivity.class));
                finish();
                break;
            case R.id.btn_qr_code:
                startActivity(new Intent(this, QRcodeActivity.class));
                finish();
                break;
        }
    }
}
