package com.mnopensdk.demo.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mnopensdk.demo.bean.CountryCodeBean;

import com.mnopensdk.demo.R;


/**
 * Created by hjz on 2018/1/15.
 */

public class CoutryTipDialog extends Dialog {
    private Context context;
    private Display display;
    OnSuccCallback onCallback;
    TextView tvOne;
    TextView tvTwo;
    TextView tvContent;
    CountryCodeBean.AreasBean bean;
    int myInttip;

    public interface OnSuccCallback {
        void onSucc(CountryCodeBean.AreasBean bean);//确认

        void onLoginDomainSucc();
    }

    public CoutryTipDialog(Context context, int inttip, OnSuccCallback onCallback) {
        super(context, R.style.Theme_dialog);
        this.context = context;
        this.onCallback = onCallback;
        myInttip = inttip;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_country, null);
        setContentView(view);
        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.8);
        Button btnCancel = view.findViewById(R.id.btnContinueWait);
        Button btnGo = view.findViewById(R.id.btnExit);
        tvOne = view.findViewById(R.id.country_one);
        tvTwo = view.findViewById(R.id.country_two);
        tvContent = view.findViewById(R.id.country_content);
        btnCancel.setOnClickListener(v -> dismiss());
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myInttip == 1) {
                    onCallback.onSucc(bean);
                } else if (myInttip == 2) {
                    onCallback.onLoginDomainSucc();
                }

            }
        });
    }

    public void setSeverData(String oriSever, String oneSever) {
        tvOne.setText(context.getString(R.string.coun_local) + oriSever);
        tvTwo.setText(context.getString(R.string.coun_local_l) + oneSever);
        tvContent.setVisibility(View.VISIBLE);
    }

    public void setItemSeverData(String oriSever, String oneSever) {
        tvOne.setText(context.getString(R.string.coun_local) + oriSever);
        tvTwo.setText(context.getString(R.string.coun_local_l) + oneSever);
        tvContent.setVisibility(View.GONE);
    }

    public void setSeverDataLogin(String oriSever, String oneSever) {
        tvOne.setText(context.getString(R.string.coun_localone) + oriSever);
        tvTwo.setText(context.getString(R.string.coun_local_l) + oneSever);
        tvContent.setVisibility(View.VISIBLE);
    }
    public void showIn(CountryCodeBean.AreasBean bean) {
        this.bean = bean;
        show();
    }
    public  void  setContent(){
        tvContent.setText(context.getString(R.string.coun_content)+"\n"+context.getString(R.string.info_t)+"\n"+context.getString(R.string.info_th));
    }
}
