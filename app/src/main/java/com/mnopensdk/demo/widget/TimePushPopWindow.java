package com.mnopensdk.demo.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.weigan.loopview.LoopView;

import java.util.ArrayList;

import com.mnopensdk.demo.R;


/**
 * Created by WIN on 2018/4/9.
 */

public class TimePushPopWindow implements View.OnClickListener {
    private Dialog mainDlg;
    private TextView tvCancel, tvOk;
    private OnSelectedListener mOnSelectedListener;
    private LoopView pickerStartHour, pickerStartMinute, pickerEndHour, pickereEndMinut;
    private ArrayList<String> hourS = new ArrayList<>();
    private ArrayList<String> minuteS = new ArrayList<>();

    private ArrayList<String> endHourS = new ArrayList<>();
    private ArrayList<String> endMinuteS = new ArrayList<>();

    public TimePushPopWindow(Context context) {
        mainDlg = new Dialog(context, R.style.ActionSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.pop_wid_timepush, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);

        tvCancel = (TextView) layout.findViewById(R.id.tv_cancel);
        tvOk = (TextView) layout.findViewById(R.id.tv_ok);

        pickerStartHour = (LoopView) layout.findViewById(R.id.picker_start_hour);
        pickerStartMinute = (LoopView) layout.findViewById(R.id.picker_start_minute);
        pickerEndHour = (LoopView) layout.findViewById(R.id.picker_end_hour);
        pickereEndMinut = (LoopView) layout.findViewById(R.id.pickere_end_minut);

        tvCancel.setOnClickListener(this);
        tvOk.setOnClickListener(this);


        initData();
        initPicker();
        Window window = mainDlg.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        mainDlg.onWindowAttributesChanged(lp);
        mainDlg.setCanceledOnTouchOutside(false);
        mainDlg.setCanceledOnTouchOutside(true);
        mainDlg.setContentView(layout);
    }

    private void initPicker() {
        pickerStartHour.setTextSize(18);
        pickerStartMinute.setTextSize(18);
        pickerEndHour.setTextSize(18);
        pickereEndMinut.setTextSize(18);

        pickerStartHour.setItems(hourS);
        pickerStartMinute.setItems(minuteS);
        pickerEndHour.setItems(endHourS);
        pickereEndMinut.setItems(endMinuteS);


        pickerStartHour.setInitPosition(0);
        pickerStartMinute.setInitPosition(0);
        pickerEndHour.setInitPosition(0);
        pickereEndMinut.setInitPosition(0);
    }

    private void initData() {
        hourS.clear();
        minuteS.clear();
        endHourS.clear();
        endMinuteS.clear();
        for (int i = 0; i < 60; i++) {
            String item = "00";
            if (i < 10) {
                item = "0" + i;
            } else {
                item = "" + i;
            }
            if (i < 24) {
                hourS.add(item);
                endHourS.add(item);
            }
            minuteS.add(item);
            endMinuteS.add(item);
        }
    }

    public void showPopupWindow(int startHIndex, int startMIndex, int endHIndex, int endMIndex) {
        pickerStartHour.setCurrentPosition(startHIndex);
        pickerStartMinute.setCurrentPosition(startMIndex);
        pickerEndHour.setCurrentPosition(endHIndex);
        pickereEndMinut.setCurrentPosition(endMIndex);
        mainDlg.show();
    }

    /**
     * 移除PopupWindow
     */
    public void dismissPopupWindow() {
        if (mainDlg != null && mainDlg.isShowing()) {
            mainDlg.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_cancel:
                dismissPopupWindow();
            case R.id.tv_ok:
                dismissPopupWindow();
                String startHour = hourS.get(pickerStartHour.getSelectedItem());
                String startMinute = minuteS.get(pickerStartMinute.getSelectedItem());
                String endHour = endHourS.get(pickerEndHour.getSelectedItem());
                String endMinute = endMinuteS.get(pickereEndMinut.getSelectedItem());

                if (null != mOnSelectedListener) {
                    mOnSelectedListener.OnSelected(startHour, startMinute, endHour, endMinute);
                }
                break;
        }
    }

    /**
     * 设置选择监听
     *
     * @param l
     */
    public void setOnSelectedListener(OnSelectedListener l) {
        this.mOnSelectedListener = l;
    }

    /**
     * 选择监听接口
     */
    public interface OnSelectedListener {
        void OnSelected(String startHour, String startMinute, String endHour, String endMinute);
    }

}
