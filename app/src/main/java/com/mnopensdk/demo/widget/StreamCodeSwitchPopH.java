package com.mnopensdk.demo.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mnopensdk.demo.utils.DensityUtil;

import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2019/1/9 0009.
 */

public class StreamCodeSwitchPopH extends PopupWindow implements View.OnClickListener{
    public static final int VIDEO_HD = 0;       // 高清
    public static final int VIDEO_FUL = 1;      // 流畅
    public static final int VIDEO_SD = 2;       // 标清

    Context mContext;
    private LayoutInflater mInflater;
    private View mContentView;
    private TextView streamHighs, streamCenters, streamFluents;
    private int popupWidth, popupHeight;
    public StreamCodeSwitchPopH(Context context) {
        super(context);
        this.mContext=context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = mInflater.inflate(R.layout.playtask_stream_pop_full,null);
        streamHighs = (TextView) mContentView.findViewById(R.id.stream_highs);
        streamCenters = (TextView) mContentView.findViewById(R.id.stream_centers);
        streamFluents = (TextView) mContentView.findViewById(R.id.stream_fluents);
        //设置View
        setContentView(mContentView);

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setBackgroundDrawable(new ColorDrawable());//设置背景只有设置了这个才可以点击外边和BACK消失
        setFocusable(true);//设置可以获取集点
        setOutsideTouchable(true);//设置点击外边可以消失
        setTouchable(true);//设置可以触摸
        setTouchInterceptor(new View.OnTouchListener() {//设置点击外部可以消失
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 判断是不是点击了外部
                if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
                    return true;
                }
                //不是点击外部
                return false;
            }
        });

        popupWidth = DensityUtil.dip2px(mContext, 56);
        popupHeight =  DensityUtil.dip2px(mContext, 74);

        streamHighs.setOnClickListener(this);
        streamCenters.setOnClickListener(this);
        streamFluents.setOnClickListener(this);
    }

    public void setStreamCode(int stream){
        if (stream == VIDEO_HD){
            streamHighs.setTextColor(ContextCompat.getColor(mContext, R.color.title_start));
            streamCenters.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            streamFluents.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }else if (stream == VIDEO_SD){
            streamHighs.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            streamCenters.setTextColor(ContextCompat.getColor(mContext, R.color.title_start));
            streamFluents.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }else if (stream == VIDEO_FUL){
            streamHighs.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            streamCenters.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            streamFluents.setTextColor(ContextCompat.getColor(mContext, R.color.title_start));
        }
    }

    public int getPopupHeight() {
        return popupHeight;
    }

    public int getPopupWidth() {
        return popupWidth;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (mListener == null)return;
        switch (v.getId()){
            case R.id.stream_highs:  // 高清
                mListener.onStreamCodeChanged(VIDEO_HD);
                break;
            case R.id.stream_centers:  // 标准
                mListener.onStreamCodeChanged(VIDEO_SD);
                break;
            case R.id.stream_fluents:  // 流畅
                mListener.onStreamCodeChanged(VIDEO_FUL);
                break;
        }
    }

    public interface OnStreamCodeChangedListener{
        void onStreamCodeChanged(int stream);
    }

    private OnStreamCodeChangedListener mListener;
    public void setOnStreamCodeChangedListener(OnStreamCodeChangedListener listener){
        mListener = listener;
    }
}
