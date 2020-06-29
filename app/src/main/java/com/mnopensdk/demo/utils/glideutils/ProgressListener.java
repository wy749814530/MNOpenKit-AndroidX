package com.mnopensdk.demo.utils.glideutils;

/**
 * Created by Administrator on 2019/1/4 0004.
 */

public interface ProgressListener {
    void onProgress(int progress);
    void onLoadSuc();
    void onLoadFailed();
}
