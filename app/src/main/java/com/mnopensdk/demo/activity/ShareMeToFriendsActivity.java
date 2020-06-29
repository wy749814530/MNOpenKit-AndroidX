package com.mnopensdk.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.mnopensdk.demo.GlideApp;
import com.mnopensdk.demo.widget.SharePermissionSetDlg;
import com.mnopensdk.demo.event.ShareDevSucEvent;
import com.mnopensdk.demo.utils.StatusBarUtil;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mnopensdk.demo.widget.RuleAlertDialog;
import com.mn.bean.restfull.BaseBean;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.restfull.ShareUserListBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mnopensdk.demo.R;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by hjz on 2018/12/17.
 */

public class ShareMeToFriendsActivity extends AppCompatActivity implements OnRecyclerItemClickListener, SharePermissionSetDlg.OnSetPermissionLinstener, SwipeRefreshLayout.OnRefreshListener, MNKitInterface.GetInviteShareUsersCallBack, MNKitInterface.CancelShareDeviceCallBack {
    @BindView(R.id.me_friend_tip)
    TextView meFriendTip;
    @BindView(R.id.refreshLay)
    SwipeRefreshLayout refreshLay;
    @BindView(R.id.rl_had_data_lay)
    LinearLayout rlHadDataLay;
    @BindView(R.id.iv_net_err)
    ImageView ivNetErr;
    @BindView(R.id.rl_net_err_lay)
    RelativeLayout rlNetErrLay;
    @BindView(R.id.me_friend_rv)
    RecyclerView meFriendRv;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_base_lay)
    RelativeLayout rlBaseLay;

    private LoadingDialog loadingDialog;
    BaseRecyclerAdapter<ShareUserListBean.ShareUserBean> adapter;
    DevicesBean devicesBean, myDevicesBean;
    private SharePermissionSetDlg permissionSetDlg;
    private String devId = "";
    private int devType = 1;
    private List<ShareUserListBean.ShareUserBean> mShareUserBeen = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metofriend);
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, rlBaseLay);

        EventBus.getDefault().register(this);

        loadingDialog = new LoadingDialog(this);
        permissionSetDlg = new SharePermissionSetDlg(this);
        permissionSetDlg.setOnSetPermissionLinstener(this);
        devicesBean = (DevicesBean) getIntent().getSerializableExtra("devicesBean");
        myDevicesBean = (DevicesBean) getIntent().getSerializableExtra("myDevicesBean");
        if (devicesBean != null) {
            devId = devicesBean.getId();
            devType = devicesBean.getType();
        } else if (myDevicesBean != null) {
            devId = myDevicesBean.getId();
            devType = myDevicesBean.getType();
        }

        adapter = new BaseRecyclerAdapter<ShareUserListBean.ShareUserBean>(this, new ArrayList<>(), R.layout.share_user_list_items) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, ShareUserListBean.ShareUserBean shareUserBean) {
                CircleImageView headView = (CircleImageView) baseViewHolder.getView(R.id.civ_head_image);

                String userName = "";
                if (shareUserBean.getPhone() != null && !"".equals(shareUserBean.getPhone())) {
                    userName = shareUserBean.getPhone();
                } else if (shareUserBean.getEmail() != null && !"".equals(shareUserBean.getEmail())) {
                    userName = shareUserBean.getEmail();
                }
                baseViewHolder.setText(R.id.tv_user_name, userName);

                String headImag = shareUserBean.getAvatar();
                if (headImag != null) {
                    GlideApp.with(ShareMeToFriendsActivity.this).load(headImag).dontAnimate().placeholder(R.mipmap.share_head1).into(headView);

                    if (shareUserBean.getState() == 0) {
                        baseViewHolder.setVisible(R.id.civ_waiting, true);
                        baseViewHolder.setVisible(R.id.tv_waiting, true);
                    } else {
                        baseViewHolder.setVisible(R.id.civ_waiting, false);
                        baseViewHolder.setVisible(R.id.tv_waiting, false);
                    }
                } else {
                    if ("Enable".equals(shareUserBean.getEnableAdd())) {
                        GlideApp.with(ShareMeToFriendsActivity.this).load(R.mipmap.person_btn_add).dontAnimate().into(headView);
                        baseViewHolder.setVisible(R.id.civ_waiting, false);
                        baseViewHolder.setVisible(R.id.tv_waiting, false);
                    } else {
                        GlideApp.with(ShareMeToFriendsActivity.this).load(R.mipmap.share_head1).dontAnimate().into(headView);
                        if (shareUserBean.getState() == 0) {
                            baseViewHolder.setVisible(R.id.civ_waiting, true);
                            baseViewHolder.setVisible(R.id.tv_waiting, true);
                        } else {
                            baseViewHolder.setVisible(R.id.civ_waiting, false);
                            baseViewHolder.setVisible(R.id.tv_waiting, false);
                        }
                    }
                }
            }
        };
        adapter.setOnRecyclerItemClickListener(this);
        adapter.openLoadAnimation(false);
        refreshLay.setOnRefreshListener(this);
        refreshLay.setColorSchemeColors(ContextCompat.getColor(this, R.color.title_start), ContextCompat.getColor(this, R.color.title_end));
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        meFriendRv.setLayoutManager(gridLayoutManager);
        meFriendRv.setAdapter(adapter);
        initRequestLinstener();
    }

    private void initRequestLinstener() {
        loadingDialog.show();
    }

    int limit;
    int sharedSize;

    @Override
    protected void onResume() {
        super.onResume();
        MNKit.getInviteShareUsers(devId, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }

        if (permissionSetDlg != null && permissionSetDlg.isShowing()) {
            permissionSetDlg.dismiss();
        }
    }

    @Override
    public void onItemClick(View view, int i) {
        ShareUserListBean.ShareUserBean user = adapter.getItem(i);

        if ("Enable".equals(user.getEnableAdd())) {
            permissionSetDlg.showDlg(devicesBean);
            return;
        }
        if (user.getState() == 0) {
            new RuleAlertDialog(this).builder().setCancelable(false).
                    setTitle(getString(R.string.hint)).
                    setMsg(getString(R.string.relieving_sharing_1)).
                    setOkButton(getString(R.string.label_ok), v -> {
                        if (loadingDialog != null) {
                            loadingDialog.show();
                        }
                        String account = user.getPhone();
                        if (TextUtils.isEmpty(user.getPhone())) {
                            account = user.getEmail();
                        }
                        MNKit.cancelSharedDevice(user.getDevice_id(), user.getId(), account, ShareMeToFriendsActivity.this);
                    }).setCancelButton(getString(R.string.label_cancel), null).show();

            return;
        }
        // devicesBean.setNI
        Intent intent = new Intent(this, ShareUserInfoActivity.class);
        intent.putExtra("userBean", (Serializable) user);
        intent.putExtra("shareDeviceId", devId);
        intent.putExtra("shareDeviceType", devType);
        startActivity(intent);
    }

    boolean isDelete = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            isDelete = true;
        }
    }

    @Override
    public void onSetPermission(int authority) {
        Intent intent = new Intent(this, ShareMethodSelectActivity.class);
        intent.putExtra("devicesBean", devicesBean);
        intent.putExtra("permission", authority);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        MNKit.getInviteShareUsers(devId, this);
    }

    @OnClick({R.id.iv_net_err, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_net_err:
                loadingDialog.show();
                MNKit.getInviteShareUsers(devId, this);
                break;
            case R.id.iv_back:
                if (isDelete) {
                    setResult(100);
                }
                finish();
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isDelete) {
                setResult(100);
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShareDevSucEvent(ShareDevSucEvent event) {
        onRefresh();
    }

    @Override
    public void onGetInviteShareUsersSuc(ShareUserListBean response) {
        rlNetErrLay.setVisibility(View.GONE);
        rlHadDataLay.setVisibility(View.VISIBLE);
        if (response != null) {
            mShareUserBeen.clear();
            mShareUserBeen.addAll(response.getShareUserBeen());
            limit = response.getLimit();
            sharedSize = 0;
            if (response.getShareUserBeen() != null) {
                sharedSize = response.getShareUserBeen().size();
            }
            ShareUserListBean.ShareUserBean shareBean = new ShareUserListBean.ShareUserBean();
            shareBean.setEnableAdd("Enable");
            if (sharedSize == 0) {
                mShareUserBeen.add(shareBean);
                mShareUserBeen.add(shareBean);
                mShareUserBeen.add(shareBean);
            } else if (sharedSize == 1) {
                mShareUserBeen.add(shareBean);
                mShareUserBeen.add(shareBean);
            } else if (sharedSize == 2) {
                mShareUserBeen.add(shareBean);
            }

            if (sharedSize >= 3 && limit > sharedSize) {
                mShareUserBeen.add(shareBean);
            }

            meFriendTip.setText(String.format(getString(R.string.tv_my_support_sharing), limit, sharedSize));
            adapter.setData(mShareUserBeen);
        }
        if (loadingDialog != null)
            loadingDialog.dismiss();
        if (refreshLay.isRefreshing()) {
            refreshLay.setRefreshing(false);
        }
    }

    @Override
    public void onGetInviteShareUsersFailed(String msg) {
        rlHadDataLay.setVisibility(View.GONE);
        rlNetErrLay.setVisibility(View.VISIBLE);
        if (loadingDialog != null)
            loadingDialog.dismiss();
        if (refreshLay.isRefreshing()) {
            refreshLay.setRefreshing(false);
        }
    }

    @Override
    public void onCancelShareDeviceFailed(String localizedMessage) {
        onRefresh();
    }

    @Override
    public void onCancelShareDeviceSuc(BaseBean response) {
        onRefresh();
    }
}
