package com.mnopensdk.demo.adapter;

import android.content.Context;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.mnopensdk.demo.utils.DateUtil;
import com.mnopensdk.demo.utils.PatternVerify;
import com.mn.bean.restfull.SharedHistoryBean;

import java.util.List;

import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2019/7/9 0009.
 */

public class RecentlySharedAdapter extends BaseRecyclerAdapter<SharedHistoryBean.ListBean> {
    private OnClickItemLinstener mLinstener;
    private Context mContext;

    public RecentlySharedAdapter(Context context, List<SharedHistoryBean.ListBean> data) {
        super(context, data, R.layout.item_recently_shared);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, SharedHistoryBean.ListBean personsBean) {

        if (mContext != null) {
            String userName = personsBean.getUser_name();
            if (PatternVerify.verifyEmial(userName)) {
                String msg = mContext.getString(R.string.tv_shared_with_email) + userName;
                baseViewHolder.setText(R.id.tv_shared_name, msg);
            } else {
                String msg = mContext.getString(R.string.tv_shared_with_phone) + userName;
                baseViewHolder.setText(R.id.tv_shared_name, msg);
            }

            String sharedTime = mContext.getString(R.string.tv_share_time) + DateUtil.getStringDateByLong(personsBean.getShare_time(), DateUtil.DEFAULT_FORMAT);
            baseViewHolder.setText(R.id.tv_shared_time, sharedTime);

            if (personsBean.getStart_time() == 0 || personsBean.getEnd_time() == 0) {
                baseViewHolder.setText(R.id.tv_time, mContext.getString(R.string.tv_sharing_time_1));
            } else if (personsBean.getEnd_time() - personsBean.getStart_time() <= 30 * 60 * 1000) {
                baseViewHolder.setText(R.id.tv_time, mContext.getString(R.string.tv_sharing_time_2));
            } else if (personsBean.getEnd_time() - personsBean.getStart_time() <= 60 * 60 * 1000) {
                baseViewHolder.setText(R.id.tv_time, mContext.getString(R.string.tv_sharing_time_3));
            } else if (personsBean.getEnd_time() - personsBean.getStart_time() <= 24 * 60 * 60 * 1000) {
                baseViewHolder.setText(R.id.tv_time, mContext.getString(R.string.tv_sharing_time_4));
            } else {
                baseViewHolder.setText(R.id.tv_time, "");
            }
        }
        baseViewHolder.setOnClickListener(R.id.rl_share_lay, v -> {
            if (mLinstener != null) {
                mLinstener.onClickItem(personsBean);
            }
        });
    }

    public void setOnClickItemLinstener(OnClickItemLinstener linstener) {
        mLinstener = linstener;
    }

    public interface OnClickItemLinstener {
        void onClickItem(SharedHistoryBean.ListBean userId);
    }
}
