package com.mnopensdk.demo.bean;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019/9/16 0016.
 */

public class NvrIpcStateBean {

    /**
     * Enable : true
     * NetLogin : 0
     */
    private int channelId = -1;
    private boolean Enable;
    private int NetLogin;

    public boolean isEnable() {
        return Enable;
    }

    public void setEnable(boolean Enable) {
        this.Enable = Enable;
    }

    public int getNetLogin() {
        return NetLogin;
    }

    public void setNetLogin(int NetLogin) {
        this.NetLogin = NetLogin;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getChannelId() {
        return channelId;
    }

    @Override
    public String toString() {
        return "{\"Enable\":" + Enable + ",\"NetLogin\":" + NetLogin + ",\"channelId\":" + channelId + "}";
    }

    public static ArrayList<NvrIpcStateBean> converRemoteJson(String jsonStr, int channels) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            boolean result = jsonObject.getBoolean("result");
            if (result) {
                String params = jsonObject.getString("params");
                if (TextUtils.isEmpty(params)) {
                    return null;
                }
                JSONObject paramsObject = new JSONObject(params);
                if (paramsObject.has("table")) {
                    String table = paramsObject.getString("table");
                    JSONObject tableObject = new JSONObject(table);
                    ArrayList<NvrIpcStateBean> ipcStateList = new ArrayList<>();
                    for (int i = 0; i < channels; i++) {
                        String cameraKey = "Cam_" + i;
                        String cameraValue = tableObject.getString(cameraKey);
                        NvrIpcStateBean ipcStateBean = new Gson().fromJson(cameraValue, NvrIpcStateBean.class);
                        ipcStateBean.setChannelId(i);
                        ipcStateList.add(ipcStateBean);
                    }
                    return ipcStateList;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
