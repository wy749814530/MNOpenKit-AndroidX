package com.mnopensdk.demo.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Created by WIN on 2017/12/1.
 */

public class MTimerTask {

    private static final Handler handler = new Handler(Looper.getMainLooper());
    private OnTimerListener mListener;
    private long mRunNum = 0;
    private long mRunedNum = 0;
    private long mMaxNum = 0;
    private long mDelayMillis = 100;

    public MTimerTask(OnTimerListener listener) {
        this.mListener = listener;
    }

    public void start(final int timer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.OnTimerRun();
                    }
                }, timer);
            }
        }).start();
    }


    private Runnable mPullTasl = new Runnable() {
        @Override
        public void run() {
            Log.i("Runnable", "run");
            if (mMaxNum == 0) {
                mListener.OnTimerRun();
                handler.postDelayed(mPullTasl, mDelayMillis);
                return;
            } else if (mMaxNum < 0) {
                stopPostDelay();
                mListener.OnTimerFinished();
                return;
            }

            if (mRunNum <= 0) {
                stopPostDelay();
                mListener.OnTimerFinished();
            } else if (mRunNum > 0) {
                mRunNum--;
                mListener.OnTimerRun();
                handler.postDelayed(mPullTasl, mDelayMillis);
            }
        }
    };

    /**
     * @param delayMillis 延时多长时间
     * @param runNum      执行次数， 0 表示无限循环
     */
    public void postDelayed(long delayMillis, long runNum) {
        this.mDelayMillis = delayMillis;
        this.mRunNum = runNum;
        this.mMaxNum = runNum;
        handler.postDelayed(mPullTasl, delayMillis);
    }

    public void stopPostDelay() {
        this.mRunNum = -1;
        this.mMaxNum = -1;
        handler.removeCallbacks(mPullTasl);
    }

    public interface OnTimerListener {
        void OnTimerRun();

        void OnTimerFinished();
    }
}
