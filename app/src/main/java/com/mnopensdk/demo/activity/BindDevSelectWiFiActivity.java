package com.mnopensdk.demo.activity;

import android.content.Intent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2018/11/24 0024.
 */

public class BindDevSelectWiFiActivity extends BaseActivity {
    @BindView(R.id.wifi_lay_name)
    LinearLayout wifiLayName;
    @BindView(R.id.tv_wifi_ssid)
    TextView tvWifiSsid;
    @BindView(R.id.et_wifi_password)
    EditText etWifiPassword;
    @BindView(R.id.wifi_lay_content)
    LinearLayout wifiLayContent;
    @BindView(R.id.tv_next)
    TextView tvNext;

    @Override
    protected int setViewLayout() {
        return R.layout.activity_bind_dev_select_wifi;
    }

    @Override
    protected void initData() {
        setTitle("WI-Fi 信息");
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

    @OnClick(R.id.tv_next)
    public void onClick() {
        String ssid = tvWifiSsid.getText().toString();
        String password = etWifiPassword.getText().toString();
        if (ssid == null || "".equals(ssid)) {
            ToastUtils.MyToastCenter("错误的wifi信息");
            return;
        }

        Intent intent = new Intent(this, BindDevSendSoundWifiActivity.class);
        intent.putExtra("ssid", ssid);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2000 && resultCode == 200 && data != null) {
            String wifiName = data.getStringExtra("wifiName");
            tvWifiSsid.setText(wifiName);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.tv_wifi_ssid)
    public void onViewClicked() {
        Intent intent = new Intent(this, BindWifiListActivity.class);
        startActivityForResult(intent, 2000);
    }
}
