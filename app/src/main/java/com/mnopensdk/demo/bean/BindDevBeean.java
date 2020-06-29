package com.mnopensdk.demo.bean;

/**
 * Created by hjz on 2017/12/4.
 */

public class BindDevBeean {

    /**
     * deviceSn : MDAhAQEAbGUwNjFiMjIzOWNmZAAA
     * vn : ABCDEF
     */

    private String deviceSn;
    private String vn;
    private int bindState;
    public int getBindState() {
        return bindState;
    }

    public void setBindState(int bindState) {
        this.bindState = bindState;
    }



    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getVn() {
        return vn;
    }

    public void setVn(String vn) {
        this.vn = vn;
    }
}
