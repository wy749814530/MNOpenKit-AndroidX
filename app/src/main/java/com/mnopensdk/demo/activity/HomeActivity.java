package com.mnopensdk.demo.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.mn.bean.restfull.DevListSortBean;
import com.mn.bean.restfull.DevicesBean;
import com.mnopensdk.demo.BaseApplication;
import com.mnopensdk.demo.R;
import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.utils.LogUtil;
import com.mnopensdk.demo.utils.PermissionUtils;
import com.mnopensdk.demo.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import MNSDK.MNKit;
import MNSDK.MNOpenSDK;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;

/**
 * Created by Administrator on 2018/11/9 0009.
 */

public class HomeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, OnRecyclerItemClickListener, MNKitInterface.DevListCallBack {
    private String TAG = HomeActivity.class.getSimpleName();
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private BaseRecyclerAdapter mAdapter;
    public static List<DevicesBean> devicesList = new ArrayList<>();
    public ExecutorService threadPool = Executors.newCachedThreadPool();
    public static HomeActivity instance;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected int setViewLayout() {
        return R.layout.activity_home_dev_type;
    }

    @Override
    protected void initData() {
        setTitle("蛮牛摄像机");
        setRightTv("添加");
        setBackImage(R.mipmap.nav_btn_multi_en);
        setBackVisibility(View.GONE);
    }

    @Override
    protected void initViews() {
        instance = this;
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.title_end, R.color.title_start);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(mAdapter = new BaseRecyclerAdapter<DevicesBean>(this, devicesList, R.layout.itme_home_dev_type) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, DevicesBean device) {

                LogUtil.i(TAG, "convert type = " + device.getType());

                String tvShare = "";
                if (device.getAuthority() != 0) {
                    tvShare = "\t\t分享设备";
                } else {
                    if (device.getUsers() != null && device.getUsers().size() > 0) {
                        tvShare = "\t\t已分享" + device.getUsers().size() + "人";
                    }
                }

                String tvOnline = "\n\n不在线";
                if (device.getOnline() == 1) {
                    tvOnline = "\n\n在线";
                } else if (device.getOnline() == 2) {
                    tvOnline = "\n\n休眠";
                }
                switch (device.getType()) {
                    case 1:
                        baseViewHolder.setText(R.id.tv_dev_type, "N2蛮牛摄像机\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 2:
                        baseViewHolder.setText(R.id.tv_dev_type, "门铃相机\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 3:
                        baseViewHolder.setText(R.id.tv_dev_type, "人脸考勤机\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 4:
                        baseViewHolder.setText(R.id.tv_dev_type, "NVR\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 5:
                        baseViewHolder.setText(R.id.tv_dev_type, "4G摄像机\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 6:
                        baseViewHolder.setText(R.id.tv_dev_type, "N2S(N2升级版)\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 7:
                        baseViewHolder.setText(R.id.tv_dev_type, "小黄人云台摇头机\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 8:
                        baseViewHolder.setText(R.id.tv_dev_type, "小雪人云台摇头机\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 9:
                        baseViewHolder.setText(R.id.tv_dev_type, "智能保险箱\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 10:
                        baseViewHolder.setText(R.id.tv_dev_type, "P2摇头机\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 11:
                        baseViewHolder.setText(R.id.tv_dev_type, "智能传感器\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 13:
                        baseViewHolder.setText(R.id.tv_dev_type, "4G摇头机\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 14:
                        baseViewHolder.setText(R.id.tv_dev_type, "二代人脸门禁\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 15:
                        baseViewHolder.setText(R.id.tv_dev_type, "4G枪机摄像机\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 16:
                        baseViewHolder.setText(R.id.tv_dev_type, "530Wifi枪机摄像机\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    case 17:
                        baseViewHolder.setText(R.id.tv_dev_type, "4G低功耗太阳能电池相机\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                    default:
                        baseViewHolder.setText(R.id.tv_dev_type, "其他设备\n\n" + device.getDev_name() + tvOnline + tvShare);
                        break;
                }
                String logoUrl = device.getLogo();
                ImageView ivLogo = baseViewHolder.getView(R.id.iv_logo);
                Glide.with(HomeActivity.this).load(logoUrl).into(ivLogo);
            }
        });
        mAdapter.openLoadAnimation(false);
        mAdapter.setOnRecyclerItemClickListener(this);
    }


    @Override
    protected void initEvents() {
        MNKit.getDevicesList(this);
        PermissionUtils.getPermission(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    @Override
    protected void onBackKeyDown(boolean isBackKey) {
        if (isBackKey) {
            finish();
        }
    }

    @Override
    protected void onRightTvClick() {
        startActivity(new Intent(this, BindDevStep1Activity.class));
    }

    @Override
    protected void onLeftTvClick() {
    }

    @Override
    public void onRefresh() {
        MNKit.getDevicesList(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        DevicesBean device = (DevicesBean) mAdapter.getItem(position);
        if (device.getType() == 4) {
            ToastUtils.MyToastCenter("NVR设备暂未接入DEMO");
            return;
        }

        if (device.getType() == 2) {
            //门铃
            ToastUtils.MyToastCenter("门铃设备暂未接入DEMO");
            return;
        }

        Intent intent = new Intent(HomeActivity.this, PlayTypeActivity.class);
        intent.putExtra("device_info", device);
        startActivityForResult(intent, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2000 && resultCode == 200) {
            MNKit.getDevicesList(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void linkDev() {
        boolean isShareDev;
        for (int i = 0; i < devicesList.size(); i++) {
            if (devicesList.get(i).getType() != 2) {
                if (devicesList.get(i).getAuthority() != 0) {
                    isShareDev = true;
                } else {
                    isShareDev = false;
                }
                String lSn = devicesList.get(i).getSn();
                //获取到设备信息之后，提前建立起与设备的链接，这里需要放在异步线程中，避免卡顿
                MNOpenSDK.linkToDevice(lSn, isShareDev);
            }
        }
    }

    @Override
    public void onGetDevListFailed(String msg) {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }

        ToastUtils.MyToast("获取数据失败，请检查参数配置或网络");
    }

    @Override
    public void onGetDevListSuccess(DevListSortBean result) {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }

        devicesList.clear();
        devicesList.addAll(result.getDevices());

        if (devicesList.size() == 0) {
            ToastUtils.MyToast("您还没有设备，点击右上角'添加'设备");
        } else {
            linkDev();
            mAdapter.setData(devicesList);
        }

    }

    //按 物理返回键
    long _lLastBack = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.i("isDown", "FrameActivity onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long cur = System.currentTimeMillis();
            if (cur - _lLastBack < 1500) {
                threadPool.execute(() -> {
                    // 注销账号或者退出APP（Cancel account or exit APP）
                    MNOpenSDK.logout();
                    mainHandler.post(() -> {
                        BaseApplication.getInstance().mActivityStack.AppExit();
                    });
                });
            } else {
                _lLastBack = cur;
                ToastUtils.MyToastBottom("再按一次，退出应用");
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
