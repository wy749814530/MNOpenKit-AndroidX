package com.mnopensdk.demo.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mnopensdk.demo.R;
import com.mnopensdk.demo.adapter.WifiAdapter;
import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.utils.PermissionUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.utils.WifiUtils;
import com.mnopensdk.demo.widget.RuleAlertDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BindWifiListActivity extends BaseActivity implements WifiAdapter.MOnClickListener {
    @BindView(R.id.ll_header)
    LinearLayout llHeader;
    @BindView(R.id.rlv)
    RecyclerView rlv;
    @BindView(R.id.srl)
    SwipeRefreshLayout srl;

    private String TAG = BindWifiListActivity.class.getSimpleName();

    private WifiAdapter mWifiAdapter;

    private List<String> mList = new ArrayList<>();
    private List<ScanResult> mSRList = new ArrayList<>();
    private List<ScanResult> mSRList_5G = new ArrayList<>();
    private final int WIFI_SCAN_PERMISSION_CODE = 2;
    private LocationManager lm;//【位置管理】
    boolean isClickSetting = false;

    @Override
    protected int setViewLayout() {
        return R.layout.activity_bind_wifi_list;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void initViews() {
        //初始化控件
        setTitle(getString(R.string.wifi_config));
        removeDuplicateScanResult(mSRList);
        mWifiAdapter = new WifiAdapter(this, mSRList, true);
        rlv.setLayoutManager(new LinearLayoutManager(this));
        rlv.setAdapter(mWifiAdapter);
        mWifiAdapter.setOnClickListener(this);

        srl.setColorSchemeResources(R.color.title_end, R.color.title_start);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initNewData();
                srl.setRefreshing(false);
            }
        });
        initNewData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isClickSetting) {
            isClickSetting = false;
            initNewData();
        }
    }

    private void initNewData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            loadWifiData();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            } else {
                permissionCheck();
            }

        }
    }

    public void loadData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            loadWifiData();
        } else {
            getPermission();
        }
    }

    public void getPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .onGranted(permissions -> {
                    permissionCheck();
                })
                .onDenied(permissions -> {
                    if (PermissionUtil.refusedPers.size() > 0) {
                        PermissionUtil.refusedPers.clear();
                    }
                    if (PermissionUtil.caterPers.size() > 0) {
                        PermissionUtil.caterPers.clear();
                    }
                    for (String per : permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(this, per)) {
                            PermissionUtil.refusedPers.add(per);
                        } else {
                            PermissionUtil.caterPers.add(per);
                        }
                    }

                    if (PermissionUtil.caterPers.size() > 0) {
                        new RuleAlertDialog(this).builder().setCancelable(false).
                                setTitle(null).
                                setMsg("[" + getString(R.string.app_name) + "] " + getString(R.string.permission_tip) + "\r\n" + PermissionUtil.transformText(this, PermissionUtil.caterPers)).
                                setOkButton(getString(R.string.authorize_now), v1 -> {
                                    getPermission();
                                }).setCancelButton(getString(R.string.next_time_say), v2 -> {
                            finish();
                        }).show();
                    } else if (PermissionUtil.refusedPers.size() > 0) {
                        new RuleAlertDialog(this).builder().setCancelable(false).
                                setTitle(null).
                                setMsg(getString(R.string.permission_refused_tip1) + getString(R.string.app_name) + getString(R.string.permission_refused_tip2) + "\r\n" + PermissionUtil.transformText(this, PermissionUtil.refusedPers)).
                                setOkButton(getString(R.string.go_to_settings), v1 -> {
                                    PermissionUtil.toPermissionSetting(this);
                                }).setCancelButton(getString(R.string.next_time_say), v2 -> {
                            finish();
                        }).show();
                    }
                })
                .start();
    }

    public void permissionCheck() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//开了定位服务
            // 有权限了，去放肆吧。
            loadWifiData();
        } else {
            new RuleAlertDialog(this).builder().setCancelable(false).
                    setTitle(getString(R.string.add_wifi_tip)).
                    setMsg(getString(R.string.gpsNotifyMsg)).
                    setOkButton(getString(R.string.label_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            isClickSetting = true;
                            startActivity(intent);
                        }
                    }).setCancelButton(getString(R.string.label_cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }).show();
        }
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

    public void removeDuplicateScanResult(List<ScanResult> scans) {
        int count = scans.size();
        for (int i = 0; i < count; i++) {
            ScanResult keys = scans.get(i);
            for (int j = i + 1; j < count; j++) {
                ScanResult lKeys = scans.get(j);
                if (keys.SSID.equals(lKeys.SSID)) {
                    scans.remove(j);
                    count--;
                    j--;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isClickSetting = false;
        switch (requestCode) {
            case WIFI_SCAN_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 允许
                    loadData();
                } else {
                    // 不允许
                    finish();
                }
                break;
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 允许
                    permissionCheck();
                } else {
                    // 不允许
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                    if (showRequestPermission) {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                    } else {
                        //被禁止且点了不再询问按钮
                        new RuleAlertDialog(this).builder().setCancelable(false).
                                setTitle(null).
                                setMsg(getString(R.string.permission_ACCESS_COARSE_LOCATION)).
                                setOkButton(getString(R.string.go_to_settings), v1 -> {
                                    PermissionUtil.toPermissionSetting(this);
                                    isClickSetting = true;
                                }).setCancelButton(getString(R.string.next_time_say), v2 -> {
                            finish();
                        }).show();
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void loadWifiData() {
        //先判断当前wifi是否开启
        if (WifiUtils.isWiFiActive(this)) {
            mList.clear();
            mSRList.clear();
            mSRList_5G.clear();
            List<ScanResult> list = WifiUtils.getScanWifiInfo(this);//获取所有wifi名称
            for (ScanResult scanResult : list) {
                String SSID = scanResult.SSID;
                if (!TextUtils.isEmpty(SSID)) {
                    if (Integer.toString(scanResult.frequency).startsWith("5")) {
                        mSRList_5G.add(scanResult);
                    } else {
                        mSRList.add(scanResult);
                    }
                }
            }
            removeDuplicateScanResult(mSRList_5G);
            removeDuplicateScanResult(mSRList);
            mSRList.addAll(mSRList_5G);
            if (mSRList.size() != 0) {
                llHeader.setVisibility(View.GONE);
                mWifiAdapter.setData(mSRList);
            } else {
                //如果设备wifi列表为空,显示无数据界面
                llHeader.setVisibility(View.VISIBLE);
            }
        } else {
            llHeader.setVisibility(View.VISIBLE);
            ToastUtils.MyToast(getString(R.string.open_wifi_frist));
        }
    }

    @Override
    public void onWifiClick(String ssid) {
        // 返回到选择绑定wifi 界面
        Intent intent = new Intent();
        intent.putExtra("wifiName", ssid);
        setResult(200, intent);
        finish();
    }
}
