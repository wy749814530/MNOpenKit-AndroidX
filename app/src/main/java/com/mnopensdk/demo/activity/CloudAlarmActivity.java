package com.mnopensdk.demo.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mn.MnKitAlarmType;
import com.mn.bean.restfull.AlarmTypeBean;
import com.mn.bean.restfull.AlarmsBean;
import com.mn.bean.restfull.CloudAlarmsBean;
import com.mn.bean.restfull.DevicesBean;
import com.mnopensdk.demo.R;
import com.mnopensdk.demo.adapter.CloudAlarmAdapter;
import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.utils.LocalVariable;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mnopensdk.demo.widget.PlayCloudAlarmDlg;
import com.mnopensdk.demo.widget.RuleAlertDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/11/26 0026.
 */

public class CloudAlarmActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, LoadingDialog.OnTimerOutListener, MNKitInterface.CloudAlarmsCallBack, CloudAlarmAdapter.OnItemClickLinstener {

    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.refreshLay)
    SwipeRefreshLayout refreshLay;
    @BindView(R.id.ll_select_lay)
    LinearLayout llSelectLay;
    @BindView(R.id.tv_select_all)
    TextView tvSelectAll;
    @BindView(R.id.tv_select_del)
    TextView tvSelectDel;
    @BindView(R.id.tv_select_read)
    TextView tvSelectRead;
    @BindView(R.id.tv_restricted_permission)
    TextView tvRestrictedPermission;


    CloudAlarmAdapter mAdapter;
    private List<AlarmsBean> mAlarms = new ArrayList<>();
    ArrayList<String> deviceSns = new ArrayList<>();
    int alarmTypeoptions = MnKitAlarmType.Motion_Detection | MnKitAlarmType.Humanoid_Detection | MnKitAlarmType.Occlusion_Detection | MnKitAlarmType.Face_Detection;
    long startSearchTime, endSearchTime;

    PlayCloudAlarmDlg cloudAlarmDlg;
    DevicesBean mDevice;
    int mCurrentPage = 0;
    boolean noMoreLoad = false;
    boolean isRefresh = true;
    boolean isEdit = false;

    LoadingDialog loadingDialog;

    @Override
    protected int setViewLayout() {
        return R.layout.activity_cloud_alarm;
    }

    @Override
    protected void initData() {
        setTitle("云报警视频");
        mDevice = (DevicesBean) getIntent().getSerializableExtra("device_info");
        for (DevicesBean device : HomeActivity.devicesList) {
            deviceSns.add(device.getSn());
        }
        startSearchTime = LocalVariable.getDateBefore(new Date(), 31).getTime();
        endSearchTime = LocalVariable.getDateBeforeNight(new Date(), 0).getTime();

        isEdit = false;
        setRightTv(getString(R.string.tv_edit));
    }

    @Override
    protected void initViews() {
        loadingDialog = new LoadingDialog(this).setReqTimeOutLintener(this);
        refreshLay.setColorSchemeResources(R.color.title_end, R.color.title_start);
        refreshLay.setOnRefreshListener(this);

        mAdapter = new CloudAlarmAdapter(this, mAlarms);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(mAdapter);
        mAdapter.openLoadAnimation(false);
        mAdapter.SetOnItemClickLinstener(this);

        recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totlaItemCount = manager.getItemCount();
                    if (lastVisibleItem == (totlaItemCount - 1) && isSlidingToLast) {
                        if (!noMoreLoad) {
                            onLoadMore();
                        } else {
                            ToastUtils.MyToast("没有数据啦");
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    isSlidingToLast = true;
                } else {
                    isSlidingToLast = false;
                }
            }
        });

    }


    @Override
    protected void initEvents() {
        loadingDialog.show();
        MNKit.getCloudAlarmData(deviceSns, startSearchTime, endSearchTime, alarmTypeoptions, null, mCurrentPage, 20, this);
    }

    @Override
    protected void onBackKeyDown(boolean b) {
        finish();
    }

    @Override
    protected void onRightTvClick() {
        if (isEdit) {
            isEdit = false;
            setRightTv(getString(R.string.tv_edit));
            mAdapter.setEditModel(false);
            mAdapter.setSelcetAll(false);
            llSelectLay.setVisibility(View.GONE);
        } else {
            isEdit = true;
            setRightTv(getString(R.string.label_cancel));
            mAdapter.setEditModel(true);
            mAdapter.setSelcetAll(false);
            llSelectLay.setVisibility(View.VISIBLE);
            tvSelectAll.setTextColor(ContextCompat.getColor(this, R.color.dark));
        }
    }

    @OnClick({R.id.tv_select_all, R.id.tv_select_del, R.id.tv_select_read})
    public void OnClickView(View view) {
        switch (view.getId()) {
            case R.id.tv_select_all:
                mAdapter.setSelcetAll(true);
                break;
            case R.id.tv_select_del:
                new RuleAlertDialog(this).builder().setCancelable(false).
                        setTitle(getString(R.string.tip_title)).
                        setMsg(getString(R.string.tv_select_del_tip)).
                        setOkButton(getString(R.string.label_ok), v1 -> {
                            if (mAdapter.isSelcetAll()) {
                                if (loadingDialog != null) {
                                    loadingDialog.show();
                                }
                                MNKit.delAlarmsByTime(deviceSns, startSearchTime, endSearchTime, alarmTypeoptions, null, new MNKitInterface.DelAlarmsByTimeCallBack() {
                                    @Override
                                    public void onDelAlarmsByTimeSuc() {
                                        onRefresh();
                                    }

                                    @Override
                                    public void onDelAlarmsByTimeFailed(String msg) {
                                        if (loadingDialog != null) {
                                            loadingDialog.dismiss();
                                        }
                                        ToastUtils.MyToastBottom(msg);
                                    }
                                });
                            } else {
                                if (mAdapter.getSelectIds().size() == 0) {
                                    ToastUtils.MyToastBottom("请先选择要删除的报警消息");
                                } else {
                                    if (loadingDialog != null) {
                                        loadingDialog.show();
                                    }
                                    MNKit.delAlarms(mAdapter.getSelectIds(), new MNKitInterface.DelAlarmsCallBack() {
                                        @Override
                                        public void onDelAlarmsSuc() {
                                            onRefresh();
                                        }

                                        @Override
                                        public void onDelAlarmsFailed(String msg) {
                                            if (loadingDialog != null) {
                                                loadingDialog.dismiss();
                                            }
                                            ToastUtils.MyToastBottom(msg);
                                        }
                                    });
                                }
                            }
                        }).setCancelButton(getString(R.string.label_cancel), null).show();

                break;
            case R.id.tv_select_read:
                new RuleAlertDialog(this).builder().setCancelable(false).
                        setTitle(getString(R.string.tip_title)).
                        setMsg(getString(R.string.tv_select_modify_tip)).
                        setOkButton(getString(R.string.label_ok), v1 -> {
                            if (mAdapter.isSelcetAll()) {
                                if (loadingDialog != null) {
                                    loadingDialog.show();
                                }
                                MNKit.modifyStatesByTime(deviceSns, startSearchTime, endSearchTime, alarmTypeoptions, null, 1, new MNKitInterface.AlarmModifyStateByTimeCallBack() {
                                    @Override
                                    public void onModifyStateByTimeFailed(String msg) {
                                        if (loadingDialog != null) {
                                            loadingDialog.dismiss();
                                        }
                                        ToastUtils.MyToastBottom(msg);
                                    }

                                    @Override
                                    public void onModifyStateByTimeSuc() {
                                        onRefresh();
                                    }
                                });
                            } else {
                                if (mAdapter.getSelectIds().size() == 0) {
                                    ToastUtils.MyToastBottom("请先选择要标记的报警消息");
                                } else {
                                    if (loadingDialog != null) {
                                        loadingDialog.show();
                                    }
                                    MNKit.modifyStates(mAdapter.getSelectIds(), 1, new MNKitInterface.AlarmModifyStateCallBack() {
                                        @Override
                                        public void onModifyStateSuc() {
                                            onRefresh();
                                        }

                                        @Override
                                        public void onModifyStateFailed(String msg) {
                                            if (loadingDialog != null) {
                                                loadingDialog.dismiss();
                                            }
                                            ToastUtils.MyToastBottom(msg);
                                        }
                                    });
                                }
                            }
                        }).setCancelButton(getString(R.string.label_cancel), null).show();
                break;
        }
    }

    @Override
    protected void onLeftTvClick() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.onRelease();
        }
    }

    @Override
    public void onRefresh() {
        mCurrentPage = 0;
        isRefresh = true;
        MNKit.getCloudAlarmData(deviceSns, startSearchTime, endSearchTime, alarmTypeoptions, null, mCurrentPage, 20, this);
    }

    private void onLoadMore() {
        mCurrentPage++;
        isRefresh = false;
        if (loadingDialog != null) {
            loadingDialog.show();
        }
        MNKit.getCloudAlarmData(deviceSns, startSearchTime, endSearchTime, alarmTypeoptions, null, mCurrentPage, 20, this);
    }

    @Override
    public void onGetCloudAlarmsFailed(String msg) {
        Log.e("CloudAlarmActivity", "onGetCloudAlarmsFailed : " + msg);
        if (!isRefresh) {
            mCurrentPage--;
        }
        refreshLay.setVisibility(View.VISIBLE);
        if (refreshLay.isRefreshing()) {
            refreshLay.setRefreshing(false);
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onGetCloudAlarmsSuc(CloudAlarmsBean response) {
        if (response.getCode() == 5005) {
            ToastUtils.MyToastCenter(getString(R.string.tv_restricted_permission));
        }
        // 下拉刷新
        if (refreshLay.isRefreshing()) {
            refreshLay.setRefreshing(false);
        }

        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }

        if (isRefresh) {
            mAlarms.clear();
        }

        if (response == null || response.getAlarms() == null || response.getAlarms().size() == 0) {
            // TODO 显示没有数据界面
            if (isRefresh) {
                tvRestrictedPermission.setVisibility(View.VISIBLE);
                setRightVisibility(View.GONE);
                if (response.getCode() == 5005) {
                    tvRestrictedPermission.setText(getString(R.string.tv_restricted_permission));
                } else {
                    tvRestrictedPermission.setText(getString(R.string.tv_no_alarm_data));
                }
            } else {
                noMoreLoad = true;
            }
        } else {
            // 有数据
            mAlarms.addAll(response.getAlarms());
            setRightVisibility(View.VISIBLE);
            tvRestrictedPermission.setVisibility(View.GONE);
        }
        mAdapter.setData(mAlarms);
    }

    @Override
    public void onItemClick(AlarmsBean alarm) {
        if (alarm.getVideoUrl() != null && !"".equals(alarm.getVideoUrl())) {
            Intent intent = new Intent(this, MNPlayControlActivity.class);
            intent.putExtra("cloudVideo", alarm);
            startActivity(intent);
        } else if (alarm.getImageUrl() != null && !"".equals(alarm.getImageUrl())) {
            ToastUtils.MyToastCenter("这是一个图片报警");
        } else {
            ToastUtils.MyToastCenter("这是一个无效报警");
        }
    }

    @Override
    public void OnReqTimerOut() {
        ToastUtils.MyToastBottom("请求超时");
    }


}
