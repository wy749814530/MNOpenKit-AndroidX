package com.mnopensdk.demo.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.mnopensdk.demo.R;


/**
 * Created by WIN on 2018/3/27.
 */

public class IsOkDialog extends Dialog {
    private Context context;
    private ImageView ivTipImg;
    private TextView tvTipMsg;

    public IsOkDialog(Context context) {
        super(context/*, R.style.PreviewDialogStyle*/);
        this.context = context;
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dlg_preview, null);
            setContentView(view);

            ivTipImg = (ImageView) findViewById(R.id.iv_tip_img);
            tvTipMsg = (TextView) findViewById(R.id.tv_tip_msg);


            Window dialogWindow = getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = context.getResources().getDisplayMetrics();
            lp.width = (int) (d.widthPixels*0.6); // 宽度设置为屏幕的0.6
//            lp.height = (int) (d.widthPixels*0.6*13/21);
            dialogWindow.setAttributes(lp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setContextImage(int image) {
        Glide.with(context).load(image).into(ivTipImg);
    }

    public void setContextMsg(String context) {
        tvTipMsg.setText(context);
    }

    public void show(int res, String msg) {
        show();
        if (res!=0){
            Glide.with(context).load(res).into(ivTipImg);
        }
        tvTipMsg.setText(msg);
    }
}
