package com.mnopensdk.demo.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.google.gson.Gson;
import com.mnopensdk.demo.activity.ShareOtherToMeActivity;
import com.mnopensdk.demo.utils.GlideUtil;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.restfull.OtherToMeBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mnopensdk.demo.R;

/**
 * Created by hjz on 2018/12/17.
 */

public class FriendShareFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnRecyclerItemClickListener, MNKitInterface.GetOhterShareDevListsCallBack {
    @BindView(R.id.my_rv)
    RecyclerView myRv;
    @BindView(R.id.my_sr)
    SwipeRefreshLayout mySr;
    @BindView(R.id.iv_share_null)
    ImageView ivShareNull;
    @BindView(R.id.tv_share_null_tip)
    TextView tvShareNullTip;
    @BindView(R.id.rl_my_share_is_null)
    LinearLayout rlMyShareIsNull;
    private View mRootView;
    boolean isGO = true;
    BaseRecyclerAdapter<DevicesBean> baseRecyclerAdapter;

    public static FriendShareFragment newInstance() {
        return new FriendShareFragment();
    }

    private LoadingDialog loadingDialog;
    Unbinder binder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_myshare, null);
            loadingDialog = new LoadingDialog(getContext());
        }
        binder = ButterKnife.bind(this, mRootView);
        if (isGO) {
            mySr.setColorSchemeResources(R.color.title_end, R.color.title_start);
            mySr.setOnRefreshListener(this);
            baseRecyclerAdapter = new BaseRecyclerAdapter<DevicesBean>(getActivity(), new ArrayList<>(), R.layout.item_friend_share) {
                @Override
                protected void convert(BaseViewHolder baseViewHolder, DevicesBean data) {
                    baseViewHolder.setText(R.id.item_share_name, data.getDev_name());
                    baseViewHolder.setText(R.id.item_friend_from, getString(R.string.event_from) + data.getFrom());
                    ImageView iv = (ImageView) baseViewHolder.getView(R.id.item_share_img);
                    if (data.getLogo() != null)
                        GlideUtil.getInstance().loadRoundFs(FriendShareFragment.this, iv, data.getLogo());
                }
            };
            baseRecyclerAdapter.openLoadAnimation(false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            myRv.setLayoutManager(linearLayoutManager);
            myRv.setAdapter(baseRecyclerAdapter);
            MNKit.getShareOtherToMeDevLists(this);
            baseRecyclerAdapter.setOnRecyclerItemClickListener(this);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isGO = false;
        if (binder != null) {
            binder.unbind();
        }
    }

    @Override
    public void onRefresh() {
        MNKit.getShareOtherToMeDevLists(this);
    }


    @Override
    public void onGetOhterShareDevListsSuc(OtherToMeBean response) {
        Log.i("OtherToMeBean", new Gson().toJson(response));
        if (response != null) {
            List<DevicesBean> devices = response.getDevices();
            baseRecyclerAdapter.setData(devices);
        }
        if (mySr.isRefreshing()) {
            mySr.setRefreshing(false);
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (response.getDevices().size() == 0) {
            myRv.setVisibility(View.GONE);
            rlMyShareIsNull.setVisibility(View.VISIBLE);
            ivShareNull.setImageResource(R.mipmap.blank_img_share);
            tvShareNullTip.setText(getString(R.string.tv_not_shared_device_yet));
        } else {
            myRv.setVisibility(View.VISIBLE);
            rlMyShareIsNull.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetOhterShareDevListsFailed(String msg) {
        if (mySr.isRefreshing()) {
            mySr.setRefreshing(false);
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        myRv.setVisibility(View.GONE);
        rlMyShareIsNull.setVisibility(View.VISIBLE);
        ivShareNull.setImageResource(R.mipmap.blank_img_network);
        tvShareNullTip.setText(getString(R.string.net_err_and_try));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 200) {
            if (loadingDialog != null)
                loadingDialog.show();
            MNKit.getShareOtherToMeDevLists(this);
        }
    }

    @Override
    public void onItemClick(View view, int i) {
        DevicesBean devicesBean = baseRecyclerAdapter.getItem(i);
        Intent intent = new Intent(getContext(), ShareOtherToMeActivity.class);
        intent.putExtra("shareDev", (Serializable) devicesBean);
        startActivityForResult(intent, 1000);
    }
}
