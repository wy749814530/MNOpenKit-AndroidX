package com.mnopensdk.demo.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import com.mnopensdk.demo.R;

/**
 * Created by caojun on 2017/8/26.
 */

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.PersonViewHolder> {
    private Context mContext;
    private List<ScanResult> mList = new ArrayList<>();
    private String deviceKey;
    private boolean mDisable5G;

    public WifiAdapter(Context context, List<ScanResult> list, boolean disable5G) {
        mContext = context;
        mDisable5G = disable5G;
        mList.clear();
        if (list != null) {
            mList.addAll(list);
        }
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_select_wifi, parent, false);
        PersonViewHolder viewHolder = new PersonViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder holder, final int position) {
        ScanResult scanResult = mList.get(position);
        boolean is5GWifi = Integer.toString(scanResult.frequency).startsWith("5");
        int level = scanResult.level;
        int signalLevel = WifiManager.calculateSignalLevel(level, 5);
        if (signalLevel == 4) {
            holder.iv_wifi_level.setImageResource(R.mipmap.add_icon_wifi_strength_4);
        } else if (signalLevel == 3) {
            holder.iv_wifi_level.setImageResource(R.mipmap.add_icon_wifi_strength_3);
        } else {
            holder.iv_wifi_level.setImageResource(R.mipmap.add_icon_wifi_strength_2);
        }

        if (mDisable5G && is5GWifi) {
            holder.tv_item_wifi.setText(scanResult.SSID + "(" + mContext.getString(R.string.nonsupport_5g) + ")");
            holder.tv_item_wifi.setTextColor(mContext.getResources().getColor(R.color.cloud_gray));
        } else {
            holder.tv_item_wifi.setText(scanResult.SSID);
            holder.tv_item_wifi.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is5GWifi) {
                    return;
                }

                if (mOnClickListener != null) {
                    mOnClickListener.onWifiClick(scanResult.SSID);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setData(List<ScanResult> mSRList) {
        mList.clear();
        if (mSRList != null) {
            mList.addAll(mSRList);
        }
        notifyDataSetChanged();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_item_wifi;
        private View mView;
        private ImageView iv_wifi_level;

        public PersonViewHolder(View itemView) {
            super(itemView);
            tv_item_wifi = (TextView) itemView.findViewById(R.id.tv_item_wifi);
            iv_wifi_level = (ImageView) itemView.findViewById(R.id.iv_wifi_level);
            mView = itemView;
        }
    }

    public interface MOnClickListener {
        void onWifiClick(String ssid);
    }

    private MOnClickListener mOnClickListener;

    public void setOnClickListener(MOnClickListener listener) {
        mOnClickListener = listener;
    }
}
