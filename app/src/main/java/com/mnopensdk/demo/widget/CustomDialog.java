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
import android.widget.ImageView;

import com.mnopensdk.demo.R;


/**
 * Created by hjz on 2017/9/18.
 */

public class CustomDialog extends Dialog {

    /**
     * 提示对话框
     */
    public static final int DIALOG_PROMPT = 0;
    private Display display;
    /**
     * wifi列表对话框
     */
    public static final int DIALOG_WIFIS = 1;
    public static final int DIALOG_PROBLEM = 2;
    public static final int DIALOG_EXIT_ADD = 3;
    private Context context;

    /**
     * 标题
     */
    private String title;
    /**
     * 图片资源
     */
    private int imageID;

    /**
     * 对话框类型
     */
    private int dialogType;

    private onBtnCancelListener onBtnCancelListener;

    public interface onBtnCancelListener {
        void onCancel();
    }

    public CustomDialog(Context context, int imageID) {
        super(context, R.style.Theme_dialog);
        this.context = context;
        this.imageID = imageID;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context .WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public CustomDialog(Context context, int type, onBtnCancelListener onBtnCancelListener) {
        super(context, R.style.Theme_dialog);
        this.context = context;
        this.dialogType = type;
        this.onBtnCancelListener = onBtnCancelListener;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context .WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (this.dialogType == DIALOG_EXIT_ADD) {
            View view = inflater.inflate(R.layout.sl_exit_add_dialog, null);
            setContentView(view);
            Button btnCancel = (Button) view.findViewById(R.id.btnExit);

            Button btnwait = (Button) view.findViewById(R.id.btnContinueWait);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBtnCancelListener.onCancel();
                }
            });
            btnwait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialog.this.dismiss();
                }
            });
        } else {
            View view = inflater.inflate(R.layout.sl_custom_dialog, null);
            setContentView(view);
            ImageView iv = (ImageView) view.findViewById(R.id.green_light_no_splash);
            iv.setImageResource(imageID);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialog.this.dismiss();
                }
            });
        }

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.8);
    }

    /**
     * 点击wifi列表时回调的接口
     * @author Andy
     */
    public interface onDialogItemClickListener {
        public void getSSID(String ssid);
    }
}
