package com.mnopensdk.demo.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mnopensdk.demo.utils.ToastUtils;
import com.mn.bean.restfull.BaseBean;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import com.mnopensdk.demo.R;

/**
 * Created by jz on 2017/8/25.
 */

public class ReNameDialog extends Dialog implements OnClickListener, MNKitInterface.ModifyDeviceNameCallBack {
    private Context context;
    private ImageView im_clear;
    private EditText et_new_name;
    private TextView tv_cancel;
    private TextView tv_confirm;
    private TextView textView;
    private String mSn;
    private OnDismissListener dismissListener;
    String nName = "";

    public ReNameDialog(Context context) {
        super(context, R.style.Theme_dialog);
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
            View view = inflater.inflate(R.layout.setting_rename, null);
            setContentView(view);
            textView = (TextView) findViewById(R.id.textView_name);
            et_new_name = (EditText) findViewById(R.id.et_new_name);
            tv_cancel = (TextView) findViewById(R.id.tv_cancel);
            tv_confirm = (TextView) findViewById(R.id.tv_confirm);
            im_clear = (ImageView) findViewById(R.id.del_clear_name);
            im_clear.setOnClickListener(this);
            tv_cancel.setOnClickListener(this);
            tv_confirm.setOnClickListener(this);
            Window dialogWindow = getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = context.getResources().getDisplayMetrics();
            lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.6
            dialogWindow.setAttributes(lp);
            et_new_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (et_new_name.getText().toString().trim().length() > 0) {
                        im_clear.setVisibility(View.VISIBLE);
                    } else {
                        im_clear.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ReNameDialog setContent(String sn, String oldName) {
        mSn = sn;
        et_new_name.setText(oldName);
        et_new_name.setSelection(et_new_name.getText().length());
        return this;
    }

    public ReNameDialog setOnDismissListener(OnDismissListener listener) {
        dismissListener = listener;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_confirm:
                nName = et_new_name.getText().toString().trim();
                if (!nName.isEmpty()) {
                    MNKit.modifyDeviceNameWithSN(mSn, nName, this);
                } else {
                    ToastUtils.MyToast(context.getString(R.string.dev_emty_name));
                }
                break;
            case R.id.del_clear_name:
                et_new_name.setText("");
        }
    }

    @Override
    public void onModifyDeviceNameFailed(String message) {
        ToastUtils.MyToastBottom(getContext().getString(R.string.net_err_and_try));
    }

    @Override
    public void onModifyDeviceNameSuc(BaseBean response) {
        if (dismissListener != null) {
            dismissListener.onDismiss(nName);
        }
        dismiss();
    }

    public interface OnDismissListener {
        void onDismiss(String name);
    }
}