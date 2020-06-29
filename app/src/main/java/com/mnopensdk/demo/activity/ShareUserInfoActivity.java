package com.mnopensdk.demo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mn.bean.restfull.BaseBean;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.restfull.ShareUserListBean;
import com.mnopensdk.demo.GlideApp;
import com.mnopensdk.demo.R;
import com.mnopensdk.demo.adapter.AuthorityAdapter;
import com.mnopensdk.demo.utils.DateUtil;
import com.mnopensdk.demo.utils.StatusBarUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.IsOkDialog;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mnopensdk.demo.widget.RuleAlertDialog;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WIN on 2018/4/12.
 */

public class ShareUserInfoActivity extends AppCompatActivity implements AuthorityAdapter.OnAuthorityChangedListener, MNKitInterface.CancelShareDeviceCallBack {
    @BindView(R.id.iv_head_image)
    CircleImageView ivHeadImage;
    @BindView(R.id.nike_name)
    TextView nikeName;
    @BindView(R.id.tv_recently_viewed_time)
    TextView tvRecentlyViewedTime;
    @BindView(R.id.tv_total_views)
    TextView tvTotalViews;
    @BindView(R.id.bt_not_sharing)
    Button btNotSharing;
    @BindView(R.id.authortyRecycler)
    RecyclerView authortyRecycler;
    @BindView(R.id.tv_remaining_time)
    TextView tvRemainingTime;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_base_lay)
    RelativeLayout rlBaseLay;
    @BindView(R.id.ll_1_lay)
    LinearLayout ll1Lay;
    @BindView(R.id.ll_see_time)
    LinearLayout llSeeTime;

    private ShareUserListBean.ShareUserBean mUser;
    private DevicesBean mShareDev;
    private LoadingDialog zProgressHUD;
    private IsOkDialog isOkDialog;
    private int mAuthority, mLastAuthority;
    private AuthorityAdapter mAuthorityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_user_info_activity);
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, rlBaseLay);

        mUser = (ShareUserListBean.ShareUserBean) getIntent().getSerializableExtra("userBean");
        String deviceId = getIntent().getStringExtra("shareDeviceId");
        int deviceType = getIntent().getIntExtra("shareDeviceType", 1);
        mAuthority = mUser.getAuthority();
        mLastAuthority = mAuthority;
        mShareDev = new DevicesBean();
        mShareDev.setId(deviceId);
        mShareDev.setType(deviceType);

        String headImag = mUser.getAvatar();
        if (headImag != null) {
            GlideApp.with(this).load(headImag).dontAnimate().placeholder(R.mipmap.share_head1).into(ivHeadImage);
        } else {
            GlideApp.with(this).load(R.mipmap.share_head1).dontAnimate().placeholder(R.mipmap.share_head1).into(ivHeadImage);
        }

        authortyRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        mAuthorityAdapter = new AuthorityAdapter(this, null);
        mAuthorityAdapter.setAuthorityDate(mAuthority, mShareDev);
        mAuthorityAdapter.openLoadAnimation(false);
        mAuthorityAdapter.setOnAuthorityChangedListener(this);
        authortyRecycler.setAdapter(mAuthorityAdapter);

        String nickName = "";
        if (mUser.getNickname() != null && !"".equals(mUser.getNickname())) {
            nickName = mUser.getNickname();
        } else if (mUser.getRealname() != null && !"".equals(mUser.getRealname())) {
            nickName = mUser.getRealname();
        } else if (mUser.getUsername() != null && !"".equals(mUser.getUsername())) {
            nickName = mUser.getUsername();
        } else if (mUser.getPhone() != null && !"".equals(mUser.getPhone())) {
            nickName = mUser.getPhone();
        } else if (mUser.getEmail() != null && !"".equals(mUser.getEmail())) {
            nickName = mUser.getEmail();
        }
        nikeName.setText(nickName.trim());
        if (mUser.getWatch_times() == 0) {
            tvTotalViews.setText(mUser.getWatch_times() + getString(R.string.count_all));
            tvRecentlyViewedTime.setText("");
        } else {
            tvRecentlyViewedTime.setText(DateUtil.getStringDateByLong(mUser.getWatch_time(), "yyyy-MM-dd HH:mm:ss"));
            tvTotalViews.setText(mUser.getWatch_times() + getString(R.string.count_all));
        }

        long remainTime = mUser.getRemain_time();
        if (remainTime == -1) {
            tvRemainingTime.setText(getString(R.string.tv_sharing_time_1));
        } else if (remainTime <= 60) {
            tvRemainingTime.setText(getString(R.string.tv_less_than_1_minute));
        } else if (remainTime <= 60 * 60) {
            tvRemainingTime.setText(String.format(getString(R.string.tv_remaining_time_minute), remainTime / 60));
        } else {
            tvRemainingTime.setText(String.format(getString(R.string.tv_remaining_time_hour_minute), remainTime / (60 * 60), remainTime / 60 % 60));
        }

        isOkDialog = new IsOkDialog(this);
        zProgressHUD = new LoadingDialog(this);
    }


    @Override
    public void onCancelShareDeviceFailed(String localizedMessage) {
        if (zProgressHUD != null) {
            zProgressHUD.dismiss();
        }
        ToastUtils.MyToast(getString(R.string.net_noperfect));
    }

    @Override
    public void onCancelShareDeviceSuc(BaseBean response) {
        if (zProgressHUD != null) {
            zProgressHUD.dismiss();
        }
        if (isOkDialog != null && isOkDialog.isShowing()) {
            isOkDialog.dismiss();
        }
        setResult(100);
        finish();
    }

    @OnClick(R.id.bt_not_sharing)
    public void onClick() {
        new RuleAlertDialog(this).builder().setCancelable(false).
                setTitle(getString(R.string.relieving_sharing_1)).
                setMsg(getString(R.string.relay_relieving_sharing_suc)).setMsgAlignStyle(Gravity.LEFT).
                setOkButton(getString(R.string.cancel_sharing), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        zProgressHUD.show();
                        String account = mUser.getPhone();
                        if (TextUtils.isEmpty(account)) {
                            account = mUser.getEmail();
                        }
                        MNKit.cancelSharedDevice(mShareDev.getId(), mUser.getId(), account, ShareUserInfoActivity.this);
                    }
                }).setCancelButton(getString(R.string.not_cancelled_temporarily), null).show();
    }

    @Override
    public void OnAuthorityChanged(int authority) {
        mAuthority = authority;
        zProgressHUD.show();
        // TODO 主账号重新设置分享设备的权限
        MNKit.updateShareDeviceAuthority(mShareDev.getId(), mUser.getId(), authority, new MNKitInterface.UpdateShareDeviceAuthorityCallBack() {
            @Override
            public void onUpdateShareDeviceAuthoritySuc() {
                if (zProgressHUD != null) {
                    zProgressHUD.dismiss();
                }
                mLastAuthority = mAuthority;
            }

            @Override
            public void onUpdateShareDeviceAuthorityFailed(String msg) {
                if (zProgressHUD != null) {
                    zProgressHUD.dismiss();
                }
                ToastUtils.MyToast(getString(R.string.settings_failed));
                mAuthorityAdapter.setAuthorityDate(mLastAuthority, mShareDev);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (zProgressHUD != null) {
            zProgressHUD.dismiss();
            zProgressHUD = null;
        }
    }

}
