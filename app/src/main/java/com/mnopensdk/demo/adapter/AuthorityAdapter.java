package com.mnopensdk.demo.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.widget.TextView;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.mn.bean.restfull.DevicesBean;
import com.mn.tools.AbilityTools;
import com.mn.tools.AuthorityManager;


import java.util.ArrayList;
import java.util.List;

import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2019/4/24 0024.
 */

public class AuthorityAdapter extends BaseRecyclerAdapter<AuthorityAdapter.AuthorityBean> {

    List<AuthorityBean> mData = new ArrayList<>();
    private Context mContext;
    private OnAuthorityChangedListener mListener;
    private int mAuthority = 0;

    public AuthorityAdapter(Context context, List<AuthorityBean> data) {
        super(context, data, R.layout.item_authority);
        mContext = context;
    }

    public void setOnAuthorityChangedListener(OnAuthorityChangedListener listener) {
        mListener = listener;
    }

    public void setAuthorityDate(int authority, DevicesBean devicesBean) {
        mAuthority = authority;
        mData.clear();
        if (AbilityTools.isSupportPTZControl(devicesBean)) {
            // 摇头机
            AuthorityBean authority1 = new AuthorityBean();
            authority1.icon = R.mipmap.share_permissions_video;
            authority1.selectIcon = R.mipmap.share_permissions_video;
            authority1.enableClick = false;
            authority1.isSelect = true;
            authority1.authorityType = 1;
            authority1.name = mContext.getString(R.string.tv_permission_video);
            mData.add(authority1);

            AuthorityBean authority2 = new AuthorityBean();
            authority2.icon = R.mipmap.share_permissions_card;
            authority2.selectIcon = R.mipmap.share_permissions_card_pre;
            authority2.enableClick = true;
            authority2.authorityType = 2;
            authority2.isSelect = AuthorityManager.hadLocalVideoAuthority(authority);
            authority2.name = mContext.getString(R.string.tv_permission_local_video);
            mData.add(authority2);

            AuthorityBean authority3 = new AuthorityBean();
            authority3.icon = R.mipmap.share_permissions_set;
            authority3.selectIcon = R.mipmap.share_permissions_set_pre;
            authority3.enableClick = true;
            authority3.authorityType = 3;
            authority3.isSelect = AuthorityManager.hadDeviceConfigAuthority(authority);
            authority3.name = mContext.getString(R.string.tv_permission_dev_set);
            mData.add(authority3);

            AuthorityBean authority4 = new AuthorityBean();
            authority4.icon = R.mipmap.share_permissions_cloud;
            authority4.selectIcon = R.mipmap.share_permissions_cloud_pre;
            authority4.enableClick = true;
            authority4.authorityType = 4;
            authority4.isSelect = AuthorityManager.hadCloudAlarmAuthority(authority);
            authority4.name = mContext.getString(R.string.tv_permission_alarm);
            mData.add(authority4);

            AuthorityBean authority5 = new AuthorityBean();
            authority5.icon = R.mipmap.share_permissions_holder;
            authority5.selectIcon = R.mipmap.share_permissions_holder_pre;
            authority5.enableClick = true;
            authority5.authorityType = 5;
            authority5.isSelect = AuthorityManager.hadPTZControlAuthority(authority);
            authority5.name = mContext.getString(R.string.tv_permission_ptz);
            mData.add(authority5);
        } else if (devicesBean.getType() == 11) {
            AuthorityBean authority3 = new AuthorityBean();
            authority3.icon = R.mipmap.share_permissions_set;
            authority3.selectIcon = R.mipmap.share_permissions_set_pre;
            authority3.enableClick = true;
            authority3.authorityType = 3;
            authority3.isSelect = AuthorityManager.hadDeviceConfigAuthority(authority);
            authority3.name = mContext.getString(R.string.tv_permission_dev_set);
            mData.add(authority3);
        } else {
            // 非摇头设备
            AuthorityBean authority1 = new AuthorityBean();
            authority1.icon = R.mipmap.share_permissions_video;
            authority1.selectIcon = R.mipmap.share_permissions_video;
            authority1.enableClick = false;
            authority1.isSelect = true;
            authority1.authorityType = 1;
            authority1.name = mContext.getString(R.string.tv_permission_video);
            mData.add(authority1);

            AuthorityBean authority2 = new AuthorityBean();
            authority2.icon = R.mipmap.share_permissions_card;
            authority2.selectIcon = R.mipmap.share_permissions_card_pre;
            authority2.enableClick = true;
            authority2.authorityType = 2;
            authority2.isSelect = AuthorityManager.hadLocalVideoAuthority(authority);
            authority2.name = mContext.getString(R.string.tv_permission_local_video);
            mData.add(authority2);

            AuthorityBean authority3 = new AuthorityBean();
            authority3.icon = R.mipmap.share_permissions_set;
            authority3.selectIcon = R.mipmap.share_permissions_set_pre;
            authority3.enableClick = true;
            authority3.authorityType = 3;
            authority3.isSelect = AuthorityManager.hadDeviceConfigAuthority(authority);
            authority3.name = mContext.getString(R.string.tv_permission_dev_set);
            mData.add(authority3);

            AuthorityBean authority4 = new AuthorityBean();
            authority4.icon = R.mipmap.share_permissions_cloud;
            authority4.selectIcon = R.mipmap.share_permissions_cloud_pre;
            authority4.enableClick = true;
            authority4.authorityType = 4;
            authority4.isSelect = AuthorityManager.hadCloudAlarmAuthority(authority);
            authority4.name = mContext.getString(R.string.tv_permission_alarm);
            mData.add(authority4);
        }

        setData(mData);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, AuthorityBean authorityBean) {
        TextView textView = baseViewHolder.getView(R.id.tv_authority);
        textView.setText(authorityBean.name);
        if (authorityBean.isSelect) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, authorityBean.selectIcon, 0, 0);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.title_start));
        } else {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, authorityBean.icon, 0, 0);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.text_default));
        }

        baseViewHolder.setOnClickListener(R.id.tv_authority, v -> {
            if (authorityBean.enableClick) {
                if (authorityBean.authorityType == 1) {
                    if (authorityBean.isSelect) {
                        authorityBean.isSelect = false;
                        mAuthority = AuthorityManager.setLiveVideoAuthority(mAuthority, false);
                    } else {
                        authorityBean.isSelect = true;
                        mAuthority = AuthorityManager.setLiveVideoAuthority(mAuthority, true);
                    }
                } else if (authorityBean.authorityType == 2) {
                    if (authorityBean.isSelect) {
                        authorityBean.isSelect = false;
                        mAuthority = AuthorityManager.setLocalVideoAuthority(mAuthority, false);
                    } else {
                        authorityBean.isSelect = true;
                        mAuthority = AuthorityManager.setLocalVideoAuthority(mAuthority, true);
                    }
                } else if (authorityBean.authorityType == 3) {
                    if (authorityBean.isSelect) {
                        authorityBean.isSelect = false;
                        mAuthority = AuthorityManager.setDeviceConfigAuthority(mAuthority, false);
                    } else {
                        authorityBean.isSelect = true;
                        mAuthority = AuthorityManager.setDeviceConfigAuthority(mAuthority, true);
                    }
                } else if (authorityBean.authorityType == 4) {
                    if (authorityBean.isSelect) {
                        authorityBean.isSelect = false;
                        mAuthority = AuthorityManager.setCloudAlarmAuthority(mAuthority, false);
                    } else {
                        authorityBean.isSelect = true;
                        mAuthority = AuthorityManager.setCloudAlarmAuthority(mAuthority, true);
                    }
                } else if (authorityBean.authorityType == 5) {
                    if (authorityBean.isSelect) {
                        authorityBean.isSelect = false;
                        mAuthority = AuthorityManager.setPTZControlAuthority(mAuthority, false);
                    } else {
                        authorityBean.isSelect = true;
                        mAuthority = AuthorityManager.setPTZControlAuthority(mAuthority, true);
                    }
                }
                if (mListener != null) {
                    mListener.OnAuthorityChanged(mAuthority);
                }
                notifyDataSetChanged();
            }
        });
    }

    public class AuthorityBean {
        public String name;
        public int icon;
        public int selectIcon;
        public boolean isSelect;
        public boolean enableClick;
        public int authorityType;
    }

    public interface OnAuthorityChangedListener {
        void OnAuthorityChanged(int authority);
    }
}
