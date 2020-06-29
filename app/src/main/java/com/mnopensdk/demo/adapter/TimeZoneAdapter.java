package com.mnopensdk.demo.adapter;

import android.content.Context;
import android.content.res.Resources;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.mnopensdk.demo.BaseApplication;

import java.util.List;

import com.mnopensdk.demo.R;

/**
 * Created by hjz on 2017/8/28.
 */

public class TimeZoneAdapter extends BaseRecyclerAdapter<String> {
    private String TAG = TimeZoneAdapter.class.getSimpleName();
    private Context mContext;
    private int index = 0;
    String[] city;

    public TimeZoneAdapter(Context context, List<String> data) {
        super(context, data, R.layout.item_time_zone);
        mContext = context;
        Resources res = BaseApplication.getInstance().getResources();
        city = res.getStringArray(R.array.timezones_array);
    }

    @Override
    protected void convert(BaseViewHolder helper, String zoneTime) {
        helper.setText(R.id.tv_localtion,zoneTime);
        helper.setText(R.id.tv_zoneText, getZoneLocation(zoneTime));
        if (helper.getPosition() == this.index) {
            helper.setImageResource(R.id.iv_zoneImg, R.mipmap.login_checkbox_selected);
        } else {
            helper.setImageResource(R.id.iv_zoneImg, R.mipmap.st_unchoose);
        }

        helper.setOnClickListener(R.id.zone, v -> {
            index = helper.getPosition();
            if (itemClickLinstener != null) {
                itemClickLinstener.onItemClick(index, zoneTime);
            }
            notifyDataSetChanged();
        });
    }

    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public String getZoneLocation(String zoneKey) {
        if (city == null || city.length == 0) {
            return "";
        }
        for (String zoneLocation : city) {
            String[] zl = zoneLocation.split("\\|");
            if (zoneLocation.startsWith(zoneKey)) {
                return zl[1];
            }
        }
        return "";
    }

    public interface OnItemClickLinstener {
        void onItemClick(int position, String timeZone);
    }

    private OnItemClickLinstener itemClickLinstener;

    public void SetItemClickLinstener(OnItemClickLinstener linstener) {
        this.itemClickLinstener = linstener;
    }
}
