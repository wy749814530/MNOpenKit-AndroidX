package com.mnopensdk.demo.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.mn.bean.restfull.AlarmsBean;
import com.mnopensdk.demo.R;
import com.mnopensdk.demo.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjz on 2017/8/28.
 */

public class CloudAlarmAdapter extends BaseRecyclerAdapter<AlarmsBean> {
    private boolean isEditModel = false, isSelcetAll = false;
    private Context mContent;
    private ArrayList<String> ids = new ArrayList<>();

    public CloudAlarmAdapter(Context context, List<AlarmsBean> data) {
        super(context, data, R.layout.item_cloud_alarm);
        mContent = context;
    }

    @Override
    public void convert(BaseViewHolder viewHolder, AlarmsBean data) {

        if (isEditModel) {
            viewHolder.setVisible(R.id.iv_del_select, true);
        } else {
            viewHolder.setVisible(R.id.iv_del_select, false);
        }

        if (data.getStatus() == 1) {
            viewHolder.setTextColor(R.id.tv_alarm_time, ContextCompat.getColor(mContent, R.color.cloud_gray));
        } else {
            viewHolder.setTextColor(R.id.tv_alarm_time, ContextCompat.getColor(mContent, R.color.yellow_ffba57));
        }

        if (isSelcetAll || ids.contains(data.getAlarmId())) {
            viewHolder.setImageResource(R.id.iv_del_select, R.mipmap.news_edit_choice_pre);
        } else {
            viewHolder.setImageResource(R.id.iv_del_select, R.mipmap.news_edit_choice);
        }

        viewHolder.setText(R.id.tv_alarm_time, DateUtil.getStringDateByLong(data.getAlarmTime(), DateUtil.DEFAULT_M_D_FORMAT));
        viewHolder.setText(R.id.tv_alarm_type, "type : " + data.getAlarmType() + " , subType" + data.getSubAlarmType());
        viewHolder.setText(R.id.tv_dev_name, data.getPersonName());
        ImageView view = viewHolder.getView(R.id.iv_alarm_image);

        if (data.getVideoUrl() != null && !"".equals(data.getVideoUrl())) {
            viewHolder.setText(R.id.tv_alarm_Image_type, "视频");
        } else if (data.getImageUrl() != null && !"".equals(data.getImageUrl())) {
            viewHolder.setText(R.id.tv_alarm_Image_type, "图片");
        } else {
            viewHolder.setText(R.id.tv_alarm_Image_type, "无效报警");
        }
        Glide.with(mContext).load(data.getImageUrl()).into(view);

        viewHolder.setOnClickListener(R.id.rl_alarm_main_lay, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditModel) {
                    if (ids.contains(data.getAlarmId())) {
                        ids.remove(data.getAlarmId());
                    } else {
                        ids.add(data.getAlarmId());
                    }
                    notifyDataSetChanged();
                } else {
                    if (itemClickLinstener != null) {
                        itemClickLinstener.onItemClick(data);
                    }
                }
            }
        });
    }

    public void setEditModel(boolean editModel) {
        isEditModel = editModel;
        notifyDataSetChanged();
    }

    public void setSelcetAll(boolean selcetAll) {
        isSelcetAll = selcetAll;
        notifyDataSetChanged();
    }

    public boolean isSelcetAll() {
        return isSelcetAll;
    }

    public ArrayList<String> getSelectIds() {
        return ids;
    }

    public interface OnItemClickLinstener {
        void onItemClick(AlarmsBean bean);
    }

    private OnItemClickLinstener itemClickLinstener;

    public void SetOnItemClickLinstener(OnItemClickLinstener linstener) {
        this.itemClickLinstener = linstener;
    }
}
