package com.mnopensdk.demo.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mnopensdk.demo.utils.DensityUtil;
import com.mnopensdk.demo.utils.MTimer;
import com.wang.avi.AVLoadingIndicatorView;

import com.mnopensdk.demo.R;

/**
 * Created by wyu on 2018/4/10.
 */

public class LoadingDialog implements MTimer.OnMTimerListener {
    private Dialog loadingDialog;
    private AVLoadingIndicatorView spaceshipImage;
    private TextView tipTextView;
    private Context mContext;
    private int timeout = 15 * 1000;
    private MTimer mTimer;

    public LoadingDialog(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        spaceshipImage = (AVLoadingIndicatorView) v.findViewById(R.id.av_load);
        tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        spaceshipImage.setIndicator("BallBeatIndicator");

        loadingDialog = new Dialog(context, R.style.no_bg_loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                DensityUtil.dip2px(mContext, 100),
                DensityUtil.dip2px(mContext, 100)));// 设置布局
        loadingDialog.setOwnerActivity((Activity) mContext);

    }

    public LoadingDialog setCancelEnable(boolean enable) {
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        return this;
    }

    public LoadingDialog setLoadingText(String msg) {
        tipTextView.setVisibility(View.VISIBLE);
        tipTextView.setText(msg);// 设置加载信息
        return this;
    }

    public LoadingDialog setTimeOut(int timerout) {
        timeout = timerout;
        return this;
    }

    public void show() {
        if (loadingDialog.isShowing()) {
            return;
        } else {
            if (timeout > 0) {
                if (mTimer == null) {
                    mTimer = new MTimer(this);
                } else {
                    mTimer.stopTimer();
                }
                mTimer.startTimer(timeout);
            }
            Activity ownerActivity = loadingDialog.getOwnerActivity();
            if (ownerActivity == null || ownerActivity.isFinishing() || ownerActivity.isDestroyed()) {

            } else
                loadingDialog.show();
        }
    }

    public void dismiss() {
        if (mTimer != null) {
            mTimer.stopTimer();
        }
        Activity ownerActivity = loadingDialog.getOwnerActivity();
        if (ownerActivity == null || ownerActivity.isFinishing() || ownerActivity.isDestroyed()) {

        } else if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void OnMTimerFinished(int cookie) {
        try {
            if (mContext == null) return;
            ((Activity) mContext).runOnUiThread(() -> {
                Activity ownerActivity = loadingDialog.getOwnerActivity();
                if (ownerActivity == null || ownerActivity.isFinishing() || ownerActivity.isDestroyed()) {

                } else if (loadingDialog.isShowing()) {
                    if (loadingDialog.getOwnerActivity() == null) return;
                    loadingDialog.dismiss();
                    if (onTimerOutListener != null) {
                        onTimerOutListener.OnReqTimerOut();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRelease() {
        onTimerOutListener = null;
    }

    public interface OnTimerOutListener {
        void OnReqTimerOut();
    }

    private OnTimerOutListener onTimerOutListener;

    public LoadingDialog setReqTimeOutLintener(OnTimerOutListener timerOutListener) {
        this.onTimerOutListener = timerOutListener;
        return this;
    }

    public void onDestory() {
        this.onTimerOutListener = null;
    }
}
