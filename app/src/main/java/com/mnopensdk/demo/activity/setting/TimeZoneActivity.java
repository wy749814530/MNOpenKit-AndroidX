package com.mnopensdk.demo.activity.setting;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mnopensdk.demo.adapter.TimeZoneAdapter;
import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.setting.BaseResult;
import com.mn.bean.setting.TimeZoneBean;

import java.util.ArrayList;
import java.util.List;

import MNSDK.MNOpenSDK;
import MNSDK.inface.MNOpenSDKInterface;
import butterknife.BindView;
import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2019/12/14 0014.
 */

public class TimeZoneActivity extends BaseActivity {
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private TimeZoneAdapter mAdapter;
    private List<String> zoneList = new ArrayList<>();
    private LoadingDialog loadingDialog;
    private int zoneIndex = 0;
    private DevicesBean mDevice;

    @Override
    protected int setViewLayout() {
        return R.layout.activity_time_zone;
    }

    @Override
    protected void initData() {
        mDevice = (DevicesBean) getIntent().getSerializableExtra("device_info");
        initzone();
    }

    @Override
    protected void initViews() {
        setTitle(getString(R.string.dev_time_zone));
        loadingDialog = new LoadingDialog(this);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TimeZoneAdapter(this, zoneList);
        mAdapter.openLoadAnimation(false);
        recycler.setAdapter(mAdapter);
    }

    @Override
    protected void initEvents() {
        mAdapter.SetItemClickLinstener(new TimeZoneAdapter.OnItemClickLinstener() {
            @Override
            public void onItemClick(int position, String timeZone) {
                loadingDialog.show();
                zoneIndex = position;
                setTimeZoneConfig();
            }
        });

        loadingDialog.show();
        // 获取设备当前时区
        MNOpenSDK.getTimeZoneConfig(mDevice.getSn(), new MNOpenSDKInterface.GetTimeZoneConfigCallBack() {
            @Override
            public void onGetTimeZoneConfig(TimeZoneBean bean) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                if (bean != null && bean.isResult() && bean.getParams() != null) {
                    zoneIndex = bean.getParams().getTimeZone();
                    mAdapter.setIndex(zoneIndex);
                    recycler.scrollToPosition(zoneIndex);
                }
            }
        });
    }

    public void setTimeZoneConfig() {
        // 设置设备时区
        MNOpenSDK.setTimeZoneConfig(mDevice.getSn(), zoneIndex, new MNOpenSDKInterface.SetTimeZoneConfigCallBack() {
            @Override
            public void onSetTimeZoneConfig(BaseResult bean) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                ToastUtils.MyToastBottom(getString(R.string.dev_set_up_successfully));
                Intent intent1 = new Intent();
                intent1.putExtra("zoneIndex", zoneIndex);
                intent1.putExtra("zoneTime", mAdapter.getItem(zoneIndex));
                setResult(200, intent1);
                finish();
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**
     * 初始化时区列表数据
     */
    private void initzone() {
        for (int i = 0; i < 32; i++) {
            if (i < 15) {
                if (i < 4) {
                    zoneList.add("GMT+0" + i + ":00");
                } else if (i == 4) {
                    zoneList.add("GMT+03:30");
                } else if (i == 5) {
                    zoneList.add("GMT+04:00");
                } else if (i == 6) {
                    zoneList.add("GMT+04:30");
                } else if (i == 7) {
                    zoneList.add("GMT+05:00");
                } else if (i == 8) {
                    zoneList.add("GMT+05:30");
                } else if (i == 9) {
                    zoneList.add("GMT+05:45");
                } else if (i == 10) {
                    zoneList.add("GMT+06:00");
                } else if (i == 11) {
                    zoneList.add("GMT+06:30");
                } else {
                    zoneList.add("GMT+0" + (i - 5) + ":00");
                }
            } else if (i >= 15 && i < 20) {
                if (i == 15) {
                    zoneList.add("GMT+09:30");
                } else {
                    zoneList.add("GMT+" + (i - 6) + ":00");
                }
            } else {
                if (i < 23) {
                    zoneList.add("GMT" + "-0" + (i - 19) + ":00");
                } else if (i == 23) {
                    zoneList.add("GMT-03:30");
                } else if (i > 23 && i < 30) {
                    zoneList.add("GMT" + "-0" + (i - 20) + ":00");
                } else {
                    zoneList.add("GMT" + -(i - 20) + ":00");
                }
            }
        }
    }

}
