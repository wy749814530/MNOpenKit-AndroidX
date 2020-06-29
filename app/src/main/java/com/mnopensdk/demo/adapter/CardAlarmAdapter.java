package com.mnopensdk.demo.adapter;

import android.content.Context;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.mnopensdk.demo.bean.TimeCardBean;

import java.util.List;

import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2019/3/18 0018.
 */

public class CardAlarmAdapter extends BaseRecyclerAdapter <TimeCardBean.InfoBean> {
    public interface OnItemClickLinstener{
        void onItemClick(TimeCardBean.InfoBean item);
    }
    public void setOnItemClickLinstener(OnItemClickLinstener linstener){
        mLinstener = linstener;
    }
    private OnItemClickLinstener mLinstener;
    public CardAlarmAdapter(Context context, List data) {
        super(context, data, R.layout.adapter_card_alarm_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, TimeCardBean.InfoBean item) {
        helper.setText(R.id.tv_type, "视频类型：" + item.getVideotype());
        helper.setText(R.id.tv_time,  item.getStarttime() + " -- " + item.getEndtime());

        helper.setOnClickListener(R.id.rl_item, v -> {
            if (mLinstener!=null){
                mLinstener.onItemClick(item);
            }
        });
    }
}
