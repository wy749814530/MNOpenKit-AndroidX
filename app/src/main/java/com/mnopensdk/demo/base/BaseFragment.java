package com.mnopensdk.demo.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Administrator on 2018/11/16 0016.
 */

public abstract class BaseFragment extends Fragment {
    public View thisView;
    private boolean isFrist = true;
    protected boolean isVisible = false;
    private boolean aflg = false;
    private boolean binded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (thisView == null) {
            thisView = inflater.inflate(getViewLayoutId(), null);
            initView();
            initListeners();
            initData();
            aflg = true;
            if (!isFrist && !binded) {
                binded = true;
                initLoadDate();
            }
        }
        ViewGroup parent = (ViewGroup) thisView.getParent();
        if (parent != null) {
            parent.removeView(thisView);
        }
        return thisView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            if (isFrist) {
                isFrist = false;
                if (!aflg || binded) {
                    return;
                }
                binded = true;
                initLoadDate();
            } else {
                onViewResume();
            }
        } else {
            isVisible = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("", "=====================================================");
        Log.i("", "=====================================================");
    }

    protected abstract int getViewLayoutId();

    protected abstract void initView();

    protected abstract void initListeners();

    protected abstract void initData();

    protected abstract void initLoadDate();

    protected abstract void onViewResume();
}
