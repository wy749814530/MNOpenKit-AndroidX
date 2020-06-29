package com.mnopensdk.demo.utils;

import android.os.Handler;
import android.os.Looper;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by WIN on 2017/12/1.
 */

public class MTimer {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    Timer mTimer = null;
    MTimerTask mTimerTask = null;
    OnMTimerListener mTimerListener;
    int mCookie = -1;

    public MTimer(OnMTimerListener timerListener){
        mTimerListener = timerListener;
    }

    public MTimer(OnMTimerListener timerListener, int cookie){
        mTimerListener = timerListener;
        mCookie = cookie;
    }

    public void startTimer(long delay) {
        try {
            if (mTimer == null){
                mTimer = new Timer();
            }

            if (mTimerTask == null){
                mTimerTask = new MTimerTask();
            }

            mTimer.schedule(mTimerTask, delay);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private class MTimerTask extends TimerTask{
        @Override
        public void run() {
            if (mTimerListener !=null){
                stopTimer();
                mTimerListener.OnMTimerFinished(mCookie);
            }
        }
    }

    public void stopTimer(){
        if (mTimer !=null){
            mTimer.cancel();
            mTimer = null;
        }
        mTimerTask = null;
    }
    public void destory(){
        stopTimer();
        mTimerListener = null;
    }
    public interface OnMTimerListener {
        void OnMTimerFinished(int mCookie);
    }
}
