package com.mnopensdk.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mnopensdk.demo.adapter.RecentlySharedAdapter;
import com.mnopensdk.demo.utils.StatusBarUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.views.RecycleViewDivider;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.restfull.SharedHistoryBean;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2019/7/9 0009.
 */

public class ShareMethodSelectActivity extends AppCompatActivity implements RecentlySharedAdapter.OnClickItemLinstener, MNKitInterface.GetSharedHistoryCallBack {
    @BindView(R.id.tv_Account_sharing)
    TextView tvAccountSharing;
    @BindView(R.id.tv_QR_code_sharing)
    TextView tvQRCodeSharing;
    @BindView(R.id.line_view)
    View lineView;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.tv_noshare_history)
    TextView tvNoshareHistory;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_base_lay)
    RelativeLayout rlBaseLay;

    private DevicesBean devicesBean;
    private String devId = "";
    private int devType = 1;
    private int mPermission = 1;

    RecentlySharedAdapter mAdapter;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_method_select);
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, rlBaseLay);
        loadingDialog = new LoadingDialog(this).setTimeOut(15 * 1000);
        devicesBean = (DevicesBean) getIntent().getSerializableExtra("devicesBean");
        if (devicesBean != null) {
            devId = devicesBean.getId();
            devType = devicesBean.getType();
        }
        mPermission = getIntent().getIntExtra("permission", 1);
        initData();
    }

    private void initData() {
        recycler.setLayoutManager(new LinearLayoutManager(this));

        recycler.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.gray_cccccc)));
        mAdapter = new RecentlySharedAdapter(this, null);
        mAdapter.setOnClickItemLinstener(this);
        mAdapter.openLoadAnimation(false);
        recycler.setAdapter(mAdapter);
    }

    @OnClick({R.id.tv_Account_sharing, R.id.tv_QR_code_sharing, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_Account_sharing:
                Intent intent = new Intent(this, ShareWithUserNameActivity.class);
                intent.putExtra("devicesBean", devicesBean);
                intent.putExtra("permission", mPermission);
                startActivity(intent);
                break;
            case R.id.tv_QR_code_sharing:
                Intent qrIntent = new Intent(this, ShareQrCodeActivity.class);
                qrIntent.putExtra("devicesBean", devicesBean);
                qrIntent.putExtra("permission", mPermission);
                startActivity(qrIntent);
                break;
        }
    }

    @Override
    public void onClickItem(SharedHistoryBean.ListBean user) {
        Intent intent = new Intent(this, ShareWithUserNameActivity.class);
        intent.putExtra("historyBean", user);
        intent.putExtra("devicesBean", devicesBean);
        intent.putExtra("permission", mPermission);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingDialog.show();
        // TODO 获取分享历史数据
        MNKit.getSharedHistoryBySn(devicesBean.getSn(), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onGetSharedHistoryFailed(String msg) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        tvNoshareHistory.setVisibility(View.VISIBLE);
        tvNoshareHistory.setText(getString(R.string.net_err_and_try));
        ToastUtils.MyToastBottom(getString(R.string.net_err_and_try));
    }

    @Override
    public void onGetSharedHistorySuc(SharedHistoryBean response) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (response == null || response.getList() == null) {
            tvNoshareHistory.setVisibility(View.VISIBLE);
            tvNoshareHistory.setText(getString(R.string.net_err_and_try));
            ToastUtils.MyToastBottom(getString(R.string.net_err_and_try));
        } else {
            if (response.getList().size() == 0) {
                tvNoshareHistory.setVisibility(View.VISIBLE);
                tvNoshareHistory.setText(getString(R.string.tv_no_sharing_history));
            } else {
                tvNoshareHistory.setVisibility(View.GONE);
            }
            mAdapter.setData(response.getList());
        }
    }
}
