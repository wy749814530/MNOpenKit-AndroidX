package com.mnopensdk.demo.widget;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.mnopensdk.demo.adapter.AuthorityAdapter;
import com.mn.bean.restfull.DevicesBean;
import com.mn.tools.AuthorityManager;

import com.mnopensdk.demo.R;


/**
 * Created by Administrator on 2019/1/10 0010.
 */

public class SharePermissionSetDlg extends Dialog implements View.OnClickListener, AuthorityAdapter.OnAuthorityChangedListener {
    private Display display;
    private Button tvComplete;
    private ImageView ivClose;
    RecyclerView authortyRecycler;

    private int mAuthority = AuthorityManager.Live_Video_Authority;//默认开启直播权限;

    private AuthorityAdapter mAuthorityAdapter;

    private OnSetPermissionLinstener mLinstener;

    @Override
    public void OnAuthorityChanged(int authority) {
        mAuthority = authority;
    }

    public interface OnSetPermissionLinstener {
        void onSetPermission(int authority);
    }

    public void setOnSetPermissionLinstener(OnSetPermissionLinstener linstener) {
        mLinstener = linstener;
    }

    public SharePermissionSetDlg(@NonNull Context context) {
        super(context, R.style.loading_dialog);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        initView(context);
    }


    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dlg_share_permission_set, null);
        setContentView(view);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.9);
        lp.height = (int) (display.getWidth() * 0.9 * 38 / 30);

        setCanceledOnTouchOutside(true);

        ivClose = view.findViewById(R.id.iv_close);// 加载布局

        tvComplete = view.findViewById(R.id.tv_complete);// 加载布局
        authortyRecycler = view.findViewById(R.id.authortyRecycler);// 加载布局

        authortyRecycler.setLayoutManager(new GridLayoutManager(context, 3));
        mAuthorityAdapter = new AuthorityAdapter(context, null);
        mAuthorityAdapter.openLoadAnimation(false);
        mAuthorityAdapter.setOnAuthorityChangedListener(this);
        authortyRecycler.setAdapter(mAuthorityAdapter);

        tvComplete.setOnClickListener(this);
        ivClose.setOnClickListener(this);
    }

    public void showDlg(DevicesBean devicesBean) {
        mAuthority = AuthorityManager.Live_Video_Authority;
        mAuthorityAdapter.setAuthorityDate(mAuthority, devicesBean);
        show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.tv_complete:
                if (mLinstener != null) {
                    mLinstener.onSetPermission(mAuthority);
                }
                dismiss();
                break;
        }
    }
}
