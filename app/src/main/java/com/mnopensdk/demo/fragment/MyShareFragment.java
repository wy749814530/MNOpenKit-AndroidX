package com.mnopensdk.demo.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.mnopensdk.demo.activity.ShareMeToFriendsActivity;
import com.mnopensdk.demo.utils.GlideUtil;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.restfull.MeToOtherBean;

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

public class MyShareFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MNKitInterface.GetShareDevListsCallBack {
    @BindView(R.id.my_rv)
    RecyclerView myRv;
    @BindView(R.id.my_sr)
    SwipeRefreshLayout mySr;
    @BindView(R.id.rl_my_share_is_null)
    LinearLayout rlMyShareIsNull;
    @BindView(R.id.iv_share_null)
    ImageView ivShareNull;
    @BindView(R.id.tv_share_null_tip)
    TextView tvShareNullTip;
    private View mRootView;
    boolean isGO = true;
    BaseRecyclerAdapter baseRecyclerAdapter;
    private LoadingDialog loadingDialog;

    public static MyShareFragment newInstance() {
        return new MyShareFragment();
    }

    Unbinder unbinder = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_myshare, null);
            loadingDialog = new LoadingDialog(getContext());
            loadingDialog.show();
        }
        unbinder = ButterKnife.bind(this, mRootView);
        if (isGO) {
            mySr.setColorSchemeResources(R.color.title_end, R.color.title_start);
            mySr.setOnRefreshListener(this);
            baseRecyclerAdapter = new BaseRecyclerAdapter<DevicesBean>(getActivity(), new ArrayList<>(), R.layout.item_share) {
                @Override
                protected void convert(BaseViewHolder baseViewHolder, DevicesBean data) {
                    baseViewHolder.setText(R.id.item_share_name, data.getDev_name());
                    ImageView iv = (ImageView) baseViewHolder.getView(R.id.item_share_img);
                    GlideUtil.getInstance().loadRoundFs(MyShareFragment.this, iv, data.getLogo());
                    List<DevicesBean.UsersBean> users = data.getUsers();
                    int shareSucNum = 0;
                    int sharingNum = 0;
                    if (users != null) {
                        for (DevicesBean.UsersBean usersBean : users) {
                            if (usersBean.getState() == 0) {
                                sharingNum++;
                            } else {
                                shareSucNum++;
                            }
                        }
                    }
                    if (shareSucNum > 0 && sharingNum == 0) {
                        baseViewHolder.setText(R.id.item_share_p, String.format(getString(R.string.tv_shared_person_count), shareSucNum));
                    } else if (shareSucNum == 0 && sharingNum > 0) {
                        baseViewHolder.setText(R.id.item_share_p, String.format(getString(R.string.tv_sharing_person_count), sharingNum));
                    } else if (shareSucNum > 0 && sharingNum > 0) {
                        baseViewHolder.setText(R.id.item_share_p, String.format(getString(R.string.tv_shared_and_sharing_person_count), shareSucNum, sharingNum));
                    } else {
                        baseViewHolder.setText(R.id.item_share_p, "");
                    }
                }
            };
            baseRecyclerAdapter.openLoadAnimation(false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            myRv.setLayoutManager(linearLayoutManager);
            myRv.setAdapter(baseRecyclerAdapter);

            MNKit.getShareToOtherDevLists(this);
            baseRecyclerAdapter.setOnRecyclerItemClickListener((view, i) -> {
                Intent intent = new Intent(getActivity(), ShareMeToFriendsActivity.class);
                DevicesBean devicesBean = (DevicesBean) baseRecyclerAdapter.getItem(i);
                intent.putExtra("myDevicesBean", devicesBean);
                startActivityForResult(intent, 1);
            });
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            MNKit.getShareToOtherDevLists(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isGO = false;
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
    }

    @Override
    public void onRefresh() {
        MNKit.getShareToOtherDevLists(this);
    }

    List<DevicesBean> myDevicesTip = new ArrayList<>();

    @Override
    public void onGetShareDevListsSuc(MeToOtherBean meToFriendBean) {
        myDevicesTip.clear();
        if (meToFriendBean != null) {
            List<DevicesBean> myDevices = meToFriendBean.getMyDevices();
            for (int i = 0; i < myDevices.size(); i++) {
                if (myDevices.get(i).getUsers() != null) {
                    myDevicesTip.add(myDevices.get(i));
                }
            }
            baseRecyclerAdapter.setData(myDevicesTip);
        }
        if (mySr.isRefreshing()) {
            mySr.setRefreshing(false);
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (myDevicesTip.size() == 0) {
            myRv.setVisibility(View.GONE);
            rlMyShareIsNull.setVisibility(View.VISIBLE);
            ivShareNull.setImageResource(R.mipmap.blank_img_share);
            tvShareNullTip.setText(getString(R.string.tv_not_shared_your_device));
        } else {
            myRv.setVisibility(View.VISIBLE);
            rlMyShareIsNull.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetShareDevListsFailed(String msg) {
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
}
