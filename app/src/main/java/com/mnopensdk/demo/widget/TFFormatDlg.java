package com.mnopensdk.demo.widget;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mnopensdk.demo.R;


/**
 * Created by Administrator on 2019/2/21 0021.
 */

public class TFFormatDlg extends Dialog implements View.OnClickListener {
    private String TAG = TFFormatDlg.class.getSimpleName();
    private Display display;
    private RelativeLayout rlAlarmNextLay;
    private TextView tvDlgTitle;
    private TextView btnCancel, btnOk;
    private Context mContext;
    private OnFormatLinstener mListener;


    public TFFormatDlg(@NonNull Context context) {
        super(context, R.style.loading_dialog);
        mContext = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        initView(context);
    }


    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dlg_tf_format, null);
        setContentView(view);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.9);
        lp.height = (int) (display.getWidth() * 0.9 * 38 / 30);

        setCanceledOnTouchOutside(true);
        rlAlarmNextLay = view.findViewById(R.id.rl_alarm_next_lay);// 加载布局
        tvDlgTitle = view.findViewById(R.id.tv_dlg_title);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnOk = view.findViewById(R.id.btn_ok);

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                dismiss();
                if (mListener!=null){
                    mListener.OnClickOK();
                }
                break;
        }
    }

    public interface OnFormatLinstener{
        void OnClickOK();
    }

    public void setOnFormatLinstener(OnFormatLinstener linstener){
        mListener = linstener;
    }
}
