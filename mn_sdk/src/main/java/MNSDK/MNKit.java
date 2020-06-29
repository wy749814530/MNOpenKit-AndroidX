package MNSDK;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.mn.MnKitAlarmType;
import com.mn.bean.restfull.DeviceInfoBean;
import com.mn.bean.restfull.FavoriteDelteBean;
import com.mn.bean.restfull.FavoritePointSaveBean;
import com.mn.bean.restfull.FavoritesInfoBean;
import com.mn.bean.restfull.FirmwareVerBean;
import com.mn.bean.restfull.MeToOtherBean;
import com.mn.bean.restfull.OtherToMeBean;
import com.mn.bean.restfull.PushconfigBean;
import com.mn.bean.restfull.Record24AlarmBean;
import com.mn.bean.restfull.ShareUserListBean;
import com.mn.bean.restfull.SharedHistoryBean;
import com.mn.bean.restfull.WaitingShareDevBean;
import com.mn.okhttp3.JsonGenericsSerializator;
import com.mn.okhttp3.OkHttpUtils;
import com.mn.okhttp3.callback.GenericsCallback;
import com.mn.bean.restfull.AlarmTypeBean;
import com.mn.bean.restfull.AuthenticationBean;
import com.mn.bean.restfull.BaseBean;
import com.mn.bean.restfull.CloudAlarmsBean;
import com.mn.bean.restfull.DevListSortBean;
import com.mn.bean.restfull.DevOnlineBean;
import com.mn.bean.restfull.DevPushConfigBean;
import com.mn.bean.restfull.DevStateInfoBean;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.restfull.LoginBean;
import com.mn.bean.restfull.UpdateDeviceCoverBean;
import com.mn.tools.AbilityTools;
import com.mn.tools.AuthorityManager;
import com.mn.tools.LocalDataUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import MNSDK.inface.MNKitInterface;
import MNSDK.inface.MNOpenSDKInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2020/1/2 0002.
 */

public class MNKit {
    public static MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private MNKit() {
    }


    private static boolean getP2pPreLinkstate() {
        boolean preLike = false;
        if (MNOpenSDK.mContext == null) {
            return false;
        }
        try {
            SharedPreferences preferences = MNOpenSDK.mContext.getSharedPreferences(MNOpenSDK.TAG, MNOpenSDK.mContext.MODE_PRIVATE);
            preLike = preferences.getBoolean("mn_P2pPreLink", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return preLike;
    }

    /**
     * 账号登录（Account login）
     *
     * @param username 用户名
     * @param password 密码
     * @param callback Callback method
     */
    public static void loginWithAccount(String username, String password, MNKitInterface.LoginCallBack callback) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.LoginCallBack> weakReference = new WeakReference<>(callback);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_key", MNOpenSDK.getAppKey());
            jsonObject.put("app_secret", MNOpenSDK.getAppSecret());
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("app_type", "Android");

            OkHttpUtils.postString()
                    .mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v1/users/signin")
                    .content(jsonObject.toJSONString())
                    .build()
                    .execute(new GenericsCallback<LoginBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onLoginFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(LoginBean response, int id) {
                            if (response != null && response.getCode() == 2000) {//密码错误时：{"msg":"username or password error","code":5000}
                                String accessToken = response.getAccess_token();
                                String userId = response.getUser_id();
                                MNOpenSDK.setAccessToken(accessToken);
                                LocalDataUtils.setUseId(userId);
                                LocalDataUtils.setUserName(username);
                                LocalDataUtils.setPassword(password);

                                cachedThreadPool.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        String host = MNOpenSDK.getDomain();
                                        String domain_host;
                                        String domain;
                                        if (host.contains("cn")) {
                                            //中国
                                            domain_host = "CN";
                                            domain = "cn.bullyun.com";
                                        } else if (host.contains("us")) {
                                            //美国
                                            domain_host = "US";
                                            domain = "us.bullyun.com";
                                        } else if (host.contains("in")) {
                                            //印度
                                            domain_host = "IN";
                                            domain = "in.bullyun.com";
                                        } else if (host.contains("te")) {
                                            domain_host = "TE";
                                            domain = "te.bullyun.com";
                                        } else if (host.contains("DV")) {
                                            domain_host = "DV";
                                            domain = "dv.bullyun.com";
                                        } else {
                                            domain_host = "CN";
                                            domain = "cn.bullyun.com";
                                        }
                                        MNJni.Logout();
                                        MNJni.Login(userId, accessToken, domain, domain_host);
                                    }
                                });
                                if (weakReference != null && weakReference.get() != null) {
                                    weakReference.get().onLoginSuccess(response);
                                }
                            } else {
                                if (weakReference != null && weakReference.get() != null) {
                                    weakReference.get().onLoginFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户账号下的设备列表(Get device list under user account)
     *
     * @param callBack Callback method
     */
    public static void getDevicesList(MNKitInterface.DevListCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.DevListCallBack> weakReference = new WeakReference<>(callBack);
        JSONObject jsonObject = new JSONObject();
        String strJson = jsonObject.toJSONString();
        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/devices/sort/list")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .build()
                .execute(new GenericsCallback<DevListSortBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGetDevListFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(DevListSortBean response, int id) {
                        if (weakReference != null && weakReference.get() != null && response != null) {
                            if (response.getCode() == 2000) {
                                if (response.getDevices() != null && response.getDevices().size() != 0) {
                                    for (DevicesBean devicesBean : response.getDevices()) {
                                        if (devicesBean.getFrom() != null) {
                                            AuthorityManager.setDevAuthority(devicesBean.getSn(), devicesBean.getAuthority());
                                        }
                                        cachedThreadPool.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                AbilityTools.isNewProtocol(devicesBean);
                                                if (getP2pPreLinkstate()) {
                                                    MNOpenSDK.linkToDevice(devicesBean.getSn(), devicesBean.getFrom() != null);
                                                }
                                            }
                                        });
                                    }
                                }
                                weakReference.get().onGetDevListSuccess(response);
                            } else if (response.getCode() == 3000) {
                                weakReference.get().onGetDevListFailed(response.getMsg());
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                            } else {
                                weakReference.get().onGetDevListFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 获取单个设备详情(Get individual device details)
     *
     * @param sn
     * @param callBack
     */
    public static void getDeviceBySn(String sn, MNKitInterface.GetDevceBySnCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.GetDevceBySnCallBack> weakReference = new WeakReference<>(callBack);
        OkHttpUtils.get().url(MNOpenSDK.getDomain() + "/api/v1/devices/" + sn)
                .addParams("app_key", MNOpenSDK.getAppKey())
                .addParams("app_secret", MNOpenSDK.getAppSecret())
                .addParams("access_token", MNOpenSDK.getAccessToken())
                .build()
                .execute(new GenericsCallback<DeviceInfoBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGetDevceBySnFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(DeviceInfoBean response, int id) {
                        Log.i("MNKit", "response :" + response);
                        if (weakReference != null && weakReference.get() != null && response != null) {
                            if (response.getCode() == 2000) {
                                DevicesBean devicesBean = response.getDevice();
                                if (devicesBean != null) {
                                    if (devicesBean.getFrom() != null) {
                                        AuthorityManager.setDevAuthority(devicesBean.getSn(), devicesBean.getAuthority());
                                    }
                                    cachedThreadPool.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            AbilityTools.isNewProtocol(devicesBean);
                                            if (getP2pPreLinkstate()) {
                                                MNOpenSDK.linkToDevice(devicesBean.getSn(), devicesBean.getFrom() != null);
                                            }
                                        }
                                    });
                                }
                                weakReference.get().onGetDevceBySnSuc(response);
                            } else if (response.getCode() == 3000) {
                                weakReference.get().onGetDevceBySnFailed(response.getMsg());
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                            } else {
                                weakReference.get().onGetDevceBySnFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 获取账号下已分享给他人观看的设备（Get devices that have been shared with others for viewing under your account）
     *
     * @param callback Callback method
     */
    public static void getShareToOtherDevLists(MNKitInterface.GetShareDevListsCallBack callback) {
        WeakReference<MNKitInterface.GetShareDevListsCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        String strJson = jsonObject.toJSONString();
        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/device/total_list")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .build()
                .execute(new GenericsCallback<MeToOtherBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGetShareDevListsFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(MeToOtherBean response, int id) {
                        if (weakReference != null && weakReference.get() != null && response != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onGetShareDevListsSuc(response);
                            } else if (response.getCode() == 3000) {
                                weakReference.get().onGetShareDevListsFailed(response.getMsg());
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                            } else {
                                weakReference.get().onGetShareDevListsFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 获取他人分享给我的设备（Get devices that others have shared with me）
     *
     * @param callback Callback method
     */
    public static void getShareOtherToMeDevLists(MNKitInterface.GetOhterShareDevListsCallBack callback) {
        WeakReference<MNKitInterface.GetOhterShareDevListsCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", LocalDataUtils.getUseId());
        String strJson = jsonObject.toJSONString();
        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/user/device_share/devices")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .build()
                .execute(new GenericsCallback<OtherToMeBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGetOhterShareDevListsFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(OtherToMeBean response, int id) {
                        if (weakReference != null && weakReference.get() != null && response != null) {
                            if (response.getCode() == 2000) {
                                if (response.getDevices() != null) {
                                    for (DevicesBean devicesBean : response.getDevices()) {
                                        if (devicesBean.getFrom() != null) {
                                            AuthorityManager.setDevAuthority(devicesBean.getSn(), devicesBean.getAuthority());
                                        }
                                    }
                                }
                                weakReference.get().onGetOhterShareDevListsSuc(response);
                            } else if (response.getCode() == 3000) {
                                weakReference.get().onGetOhterShareDevListsFailed(response.getMsg());
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                            } else {
                                weakReference.get().onGetOhterShareDevListsFailed(response.getMsg());
                            }
                        }
                    }
                });
    }


    /**
     * 获取对应设备绑定信息与在线状态(et the corresponding device binding information and online status)
     *
     * @param sn       Device SN
     * @param callback Callback method
     */
    public static void getDeviceStateInfoWithSn(String sn, MNKitInterface.DeviceStateInfoCallBack callback) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.DeviceStateInfoCallBack> weakReference = new WeakReference<>(callback);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sn", sn);
            String s = jsonObject.toJSONString();
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v3/device/info/sn")
                    .addHeader("app_key", MNOpenSDK.getAppKey())
                    .addHeader("app_secret", MNOpenSDK.getAppSecret())
                    .addHeader("access_token", MNOpenSDK.getAccessToken())
                    .content(s)
                    .build()
                    .execute(new GenericsCallback<DevStateInfoBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onGetDeviceStateFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(DevStateInfoBean response, int id) {
                            if (weakReference != null && weakReference.get() != null && response != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onGetDeviceStateSuc(response);
                                } else if (response.getCode() == 3000) {
                                    weakReference.get().onGetDeviceStateFailed(response.getMsg());
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                } else {
                                    weakReference.get().onGetDeviceStateFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备在线状态(Get device online status)
     *
     * @param sn       Device SN
     * @param callBack Callback method
     */
    public static void getDeviceOnlineInfoWithSn(String sn, MNKitInterface.DevOnlineStateCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.DevOnlineStateCallBack> weakReference = new WeakReference<>(callBack);
        OkHttpUtils.get().url(MNOpenSDK.getDomain() + "/api/v1/devices/" + sn + "/online")
                .addParams("app_key", MNOpenSDK.getAppKey())
                .addParams("app_secret", MNOpenSDK.getAppSecret())
                .addParams("access_token", MNOpenSDK.getAccessToken())
                .build()
                .execute(new GenericsCallback<DevOnlineBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGetOnLineStateFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(DevOnlineBean response, int id) {
                        if (weakReference != null && weakReference.get() != null && response != null) {
                            if (response.getCode() == 3000) {
                                weakReference.get().onGetOnLineStateFailed(response.getMsg());
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                            } else if (response.getCode() == 2000) {
                                weakReference.get().onGetOnLineStateSucc(response);
                            } else {
                                weakReference.get().onGetOnLineStateFailed(response.getMsg());
                            }

                        }
                    }
                });
    }

    /**
     * 绑定设备(Bind device)
     *
     * @param sn          Device SN
     * @param vn          Device VN(默认ABCDEF)
     * @param receiveType receiveType默认ABCDEF)
     * @param callBack    Callback method
     */
    public static void bindDeviceBySnAndVn(String sn, String vn, int receiveType, MNKitInterface.DeviceBindViewCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.DeviceBindViewCallBack> weakReference = new WeakReference<>(callBack);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_key", MNOpenSDK.getAppKey());
            jsonObject.put("app_secret", MNOpenSDK.getAppSecret());
            jsonObject.put("access_token", MNOpenSDK.getAccessToken());
            jsonObject.put("vn", vn);
            jsonObject.put("receive_type", receiveType);
            String stringJson = jsonObject.toJSONString();
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v2/devices/" + sn + "/bind")
                    .content(stringJson)
                    .build()
                    .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onBindDeviceFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (weakReference != null && weakReference.get() != null && response != null) {
                                if (response.getCode() == 3000) {
                                    weakReference.get().onBindDeviceFailed(response.getMsg());
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                } else {
                                    weakReference.get().onBindDeviceSuc(response);
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定分享设备(Binding sharing device)
     *
     * @param invite_code 邀请码(Invitation code)
     * @param callBack    Callback method
     */
    public static void bindShareDeviceByInviteCode(String invite_code, MNKitInterface.BindShareDeviceViewCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.BindShareDeviceViewCallBack> weakReference = new WeakReference<>(callBack);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", LocalDataUtils.getUseId());
            jsonObject.put("invite_code", invite_code);
            String strJson = jsonObject.toJSONString();
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v3/user/device_share/bind")
                    .addHeader("app_key", MNOpenSDK.getAppKey())
                    .addHeader("app_secret", MNOpenSDK.getAppSecret())
                    .addHeader("access_token", MNOpenSDK.getAccessToken())
                    .content(strJson)
                    .build()
                    .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                callBack.onBindShareDeviceViewFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (weakReference != null && weakReference.get() != null && response != null) {
                                if (response.getCode() == 3000) {
                                    weakReference.get().onBindShareDeviceViewFailed(response.getMsg());
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                } else {
                                    weakReference.get().onBindShareDeviceViewSuc(response);
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解除账号和设备的绑定关系（Unbind account and device）
     *
     * @param sn       Device SN
     * @param callBack Callback method
     */
    public static void unbindDevice(String sn, MNKitInterface.UnbindDeviceCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.UnbindDeviceCallBack> weakReference = new WeakReference<>(callBack);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("app_key", MNOpenSDK.getAppKey());
            jsonObject.put("app_secret", MNOpenSDK.getAppSecret());
            jsonObject.put("access_token", MNOpenSDK.getAccessToken());
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v1/devices/" + sn + "/unbind")
                    .content(jsonObject.toJSONString())
                    .build()
                    .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onUnbindDeviceFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (weakReference != null && weakReference.get() != null && response != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onUnbindDeviceSuc(response);
                                } else if (response.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onUnbindDeviceFailed(response.getMsg());
                                } else {
                                    weakReference.get().onUnbindDeviceFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 被分享用户主动解除被分享设备的绑定关系（The shared user actively unbinds the shared device）
     *
     * @param device_id Device ID
     * @param callBack  Callback method
     */
    public static void unbindSharedDevice(String device_id, MNKitInterface.UnBindShareDeviceCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.UnBindShareDeviceCallBack> weakReference = new WeakReference<>(callBack);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("device_id", device_id);
            jsonObject.put("user_id", LocalDataUtils.getUseId());
            String strJson = jsonObject.toJSONString();
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v3/user/device_share/unbind")
                    .addHeader("app_key", MNOpenSDK.getAppKey())
                    .addHeader("app_secret", MNOpenSDK.getAppSecret())
                    .addHeader("access_token", MNOpenSDK.getAccessToken())
                    .content(strJson)
                    .build()
                    .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onUnBindShareDeviceFailed(e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (weakReference != null && weakReference.get() != null && response != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onUnBindShareDeviceSuc(response);
                                } else if (response.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onUnBindShareDeviceFailed(response.getMsg());
                                } else {
                                    weakReference.get().onUnBindShareDeviceFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主账号主动取消分享设备（Active account cancellation）
     *
     * @param device_id Device ID
     * @param user_id   user id
     * @param account   account
     * @param callBack  Callback method
     */
    public static void cancelSharedDevice(String device_id, String user_id, String account, MNKitInterface.CancelShareDeviceCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.CancelShareDeviceCallBack> weakReference = new WeakReference<>(callBack);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("device_id", device_id);
            if (!TextUtils.isEmpty(user_id)) {
                jsonObject.put("user_id", user_id);
            }

            if (account != null && account.contains("@") && account.endsWith("com")) {
                jsonObject.put("email", account);
            } else {
                jsonObject.put("phone", account);
            }

            String strJson = jsonObject.toJSONString();
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v3/user/device_share/unbind")
                    .addHeader("app_key", MNOpenSDK.getAppKey())
                    .addHeader("app_secret", MNOpenSDK.getAppSecret())
                    .addHeader("access_token", MNOpenSDK.getAccessToken())
                    .content(strJson)
                    .build()
                    .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onCancelShareDeviceFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (weakReference != null && weakReference.get() != null && response != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onCancelShareDeviceSuc(response);
                                } else if (response.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onCancelShareDeviceFailed(response.getMsg());
                                } else {
                                    weakReference.get().onCancelShareDeviceFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主账号重新设置分享设备的权限（Master account reset permissions for sharing devices）
     *
     * @param device_id Device ID
     * @param user_id   被分享账号ID（Shared account ID）
     * @param authority 新的权限（New permissions）
     * @param callback  Callback method
     */
    public static void updateShareDeviceAuthority(String device_id, String user_id, int authority, MNKitInterface.UpdateShareDeviceAuthorityCallBack callback) {
        WeakReference<MNKitInterface.UpdateShareDeviceAuthorityCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("device_id", device_id);
        jsonObject.put("user_id", user_id);
        jsonObject.put("authority", authority);
        String strJson = jsonObject.toJSONString();
        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/user/device_share/update_authority")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .build()
                .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onUpdateShareDeviceAuthorityFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (weakReference != null && weakReference.get() != null && response != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onUpdateShareDeviceAuthoritySuc();
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onUpdateShareDeviceAuthorityFailed(response.getMsg());
                            } else {
                                weakReference.get().onUpdateShareDeviceAuthorityFailed(response.getMsg());
                            }
                        }
                    }
                });
    }


    /**
     * 修改设备名称(Modify device name)
     *
     * @param sn       Device SN
     * @param devName  New device name
     * @param callBack Callback method
     */
    public static void modifyDeviceNameWithSN(String sn, String devName, MNKitInterface.ModifyDeviceNameCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        try {
            WeakReference<MNKitInterface.ModifyDeviceNameCallBack> weakReference = new WeakReference<>(callBack);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_key", MNOpenSDK.getAppKey());
            jsonObject.put("app_secret", MNOpenSDK.getAppSecret());
            jsonObject.put("access_token", MNOpenSDK.getAccessToken());
            jsonObject.put("dev_name", devName);
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v1/devices/" + sn + "/modify")
                    .content(jsonObject.toJSONString())
                    .build()
                    .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onModifyDeviceNameFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (weakReference != null && weakReference.get() != null && response != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onModifyDeviceNameSuc(response);
                                } else if (response.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onModifyDeviceNameFailed(response.getMsg());
                                } else {
                                    weakReference.get().onModifyDeviceNameFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传设备封面(Upload device cover)
     *
     * @param file      File
     * @param sn        Device SN
     * @param channelId channel Id
     * @param callBack  Callback method
     */
    public static void updateDeviceCover(File file, String sn, int channelId, MNKitInterface.UpdateDeviceCoverCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.UpdateDeviceCoverCallBack> weakReference = new WeakReference<>(callBack);
        String url = MNOpenSDK.getDomain() + "/api/v1/devices/" + sn + "/logo/upload";
        OkHttpUtils.post().url(url)
                .addFile("img", "", file)
                .addParams("channel_no", "" + channelId)
                .addParams("app_key", MNOpenSDK.getAppKey())
                .addParams("app_secret", MNOpenSDK.getAppSecret())
                .addParams("access_token", MNOpenSDK.getAccessToken())
                .build()
                .execute(new GenericsCallback<UpdateDeviceCoverBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onUpdateDeviceCoverFailed(e.getMessage(), id);
                        }
                    }

                    @Override
                    public void onResponse(UpdateDeviceCoverBean response, int id) {
                        if (weakReference != null && weakReference.get() != null && response != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onUpdateDeviceCoverSuc(response, id);
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onUpdateDeviceCoverFailed(response.getMsg(), id);
                            } else {
                                weakReference.get().onUpdateDeviceCoverFailed(response.getMsg(), id);
                            }
                        }
                    }
                });
    }


    /**
     * 获取云存报警信息(Get cloud storage alarm information)
     *
     * @param deviceSns        // 设备SN列表(Device SN list)
     * @param startTime        // 查询开始时间(Query start time)
     * @param endTime          // 查询结束时间(Query end time)
     * @param alarmTypeOptions // 查询报警信息类型(Query alarm information type)
     * @param personName       // 名字，人脸识别有效(Name, face recognition is valid)
     * @param pageStart        // 第几页，从0开始，开始时间与结束时间相同的查询有效(Page number, starting from 0, the query with the same start time and end time is valid)
     * @param pageSize         // 每页查询条数(Number of queries per page)
     * @param callBack         Callback method
     */


    public static void getCloudAlarmData(ArrayList<String> deviceSns, long startTime, long endTime, int alarmTypeOptions,
                                         String personName, int pageStart, int pageSize, MNKitInterface.CloudAlarmsCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.CloudAlarmsCallBack> weakReference = new WeakReference<>(callBack);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceSns", deviceSns);//数组
            jsonObject.put("startTime", startTime);
            jsonObject.put("endTime", endTime);
            jsonObject.put("types", MnKitAlarmType.getAlarmTypeList(alarmTypeOptions));
            jsonObject.put("personName", personName);
            jsonObject.put("pageStart", pageStart);
            jsonObject.put("pageSize", pageSize);
            String jsonData = jsonObject.toJSONString();
            Log.i("", "jsonData :" + jsonData);
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v3/alarms/list_all")
                    .addHeader("app_key", MNOpenSDK.getAppKey())
                    .addHeader("app_secret", MNOpenSDK.getAppSecret())
                    .addHeader("access_token", MNOpenSDK.getAccessToken())
                    .content(jsonData).tag("aaa")
                    .build().execute(
                    new GenericsCallback<CloudAlarmsBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onGetCloudAlarmsFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(CloudAlarmsBean regBean, int id) {
                            if (weakReference != null && weakReference.get() != null && regBean != null) {
                                if (regBean.getCode() == 2000 || regBean.getCode() == 5005) {
                                    weakReference.get().onGetCloudAlarmsSuc(regBean);
                                } else if (regBean.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onGetCloudAlarmsFailed(regBean.getMsg());
                                } else {
                                    weakReference.get().onGetCloudAlarmsFailed(regBean.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取24小时云录像报警信息(Get 24-hour cloud recording alarm information)
     *
     * @param sn         Decice SN
     * @param start_time 查询开始时间(Query start time)yyyy-MM-dd HH:mm:ss
     * @param end_time   查询结束时间(Query end time)yyyy-MM-dd HH:mm:ss
     * @param callback   Callback method
     */
    public static void get24HCloudRecord(String sn, String start_time, String end_time, MNKitInterface.Get24HCloudRecordCallBack callback) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.Get24HCloudRecordCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sn", sn);//数组
        jsonObject.put("start_time", start_time);
        jsonObject.put("end_time", end_time);
        jsonObject.put("channel_no", 0);
        jsonObject.put("page_start", 0);
        jsonObject.put("page_size", 500);
        String strJson = jsonObject.toJSONString();

        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/h24_record/list")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .build()
                .execute(new GenericsCallback<Record24AlarmBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGet24HCloudRecordFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(Record24AlarmBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onGet24HCloudRecordSuc(response);
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onGet24HCloudRecordFailed(response.getMsg());
                            } else {
                                weakReference.get().onGet24HCloudRecordFailed(response.getMsg());
                            }
                        }
                    }
                });
    }


    /**
     * 对报警视频URL信息鉴权(Authentication of alarm video URL information)
     *
     * @param url      需要鉴权的视频地址(Video address requiring authentication)
     * @param callBack Callback method
     */
    public static void authenticationUrl(String url, MNKitInterface.AuthenticationUrlCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.AuthenticationUrlCallBack> weakReference = new WeakReference<>(callBack);
        try {
            ArrayList<String> urls = new ArrayList<>();
            urls.add(url);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_key", MNOpenSDK.getAppKey());
            jsonObject.put("app_secret", MNOpenSDK.getAppSecret());
            jsonObject.put("access_token", MNOpenSDK.getAccessToken());
            jsonObject.put("urls", urls);
            String stringJson = jsonObject.toJSONString();
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v1/presignedurl")
                    .content(stringJson)
                    .build()
                    .execute(new GenericsCallback<AuthenticationBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onAuthenticationUrlFailed(null);
                            }
                        }

                        @Override
                        public void onResponse(AuthenticationBean response, int id) {
                            if (response != null && weakReference != null && weakReference.get() != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onAuthenticationUrlSuc(response);
                                } else if (response.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onAuthenticationUrlFailed(response.getMsg());
                                } else {
                                    weakReference.get().onAuthenticationUrlFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备消息推送配置(Get device message push configuration)
     *
     * @param sn        Device SN
     * @param channelId channel Id
     * @param callBack  Callback method
     */
    public static void getPushConfigWithSN(String sn, int channelId, MNKitInterface.GetDevPushconfigCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        WeakReference<MNKitInterface.GetDevPushconfigCallBack> weakReference = new WeakReference<>(callBack);
        String getUrl = MNOpenSDK.getDomain() + "/api/v3/pushconfig/get" + "?sn=" + sn + "&channel_no=" + channelId;
        OkHttpUtils.get()
                .url(getUrl)
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .build()
                .execute(new GenericsCallback<DevPushConfigBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGetDevPushconfigFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(DevPushConfigBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000 && response.getPushconfig() != null) {
                                PushconfigBean pushconfigBean = response.getPushconfig();
                                int alarmOptions = 0;
                                if (response.getPushconfig().getPushenable() == 0) {
                                    alarmOptions = 0;
                                } else if (response.getPushconfig().getPushenable() == 1 && (response.getPushconfig().getPush_list() == null || response.getPushconfig().getPush_list().size() == 0)) {
                                    alarmOptions = MnKitAlarmType.AllAlarm_Detection;
                                } else {
                                    for (PushconfigBean.PushListBean listBean : response.getPushconfig().getPush_list()) {
                                        if (listBean.getAlarmType() == 1) { // 外部IO报警
                                            alarmOptions = alarmOptions | MnKitAlarmType.IO_detection;
                                        } else if (listBean.getAlarmType() == 3) { // 人脸识别
                                            if ((listBean.getSubAlarmType().contains(3) || listBean.getSubAlarmType().contains(4))) {
                                                alarmOptions = alarmOptions | MnKitAlarmType.Face_Detection;
                                            }
                                            if ((listBean.getSubAlarmType().contains(5))) {
                                                alarmOptions = alarmOptions | MnKitAlarmType.IO_Attendance_detection;
                                            }
                                        } else if (listBean.getAlarmType() == 8) { //N2 移动侦测
                                            alarmOptions = alarmOptions | MnKitAlarmType.Motion_Detection;
                                        } else if (listBean.getAlarmType() == 11) { //人形
                                            alarmOptions = alarmOptions | MnKitAlarmType.Humanoid_Detection;
                                        } else if (listBean.getAlarmType() == 12) { //没有哭声检测
                                            alarmOptions = alarmOptions | MnKitAlarmType.Cry_Detection;
                                        } else if (listBean.getAlarmType() == 13) {//遮挡报警
                                            alarmOptions = alarmOptions | MnKitAlarmType.Occlusion_Detection;
                                        } else if (listBean.getAlarmType() == 14) {//PIR侦测
                                            alarmOptions = alarmOptions | MnKitAlarmType.Infrared_detection;
                                        } else if (listBean.getAlarmType() == 256) { //箱体报警
                                            alarmOptions = alarmOptions | MnKitAlarmType.Box_Detection;
                                        }
                                    }
                                }
                                pushconfigBean.setAlarmTypeOptions(alarmOptions);
                                weakReference.get().onGetDevPushconfigSuc(pushconfigBean);
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onGetDevPushconfigFailed(response.getMsg());
                            } else {
                                weakReference.get().onGetDevPushconfigFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 设置设备消息推送配置(Set device message push configuration)
     *
     * @param sn                 Device SN
     * @param channelId          channel Id
     * @param alarmOptions       推送消息类型配置(Push message type configuration)
     * @param sleepenable        是否启用休眠(Whether to enable hibernation)
     * @param sleepTimeRangeBean 休眠时间段(Sleep period)
     * @param callBack           Callback method
     */

    public static void setPushConfigerationWithSN(String sn, int channelId, int alarmOptions, int sleepenable,
                                                  ArrayList<PushconfigBean.SleepTimeRangeBean> sleepTimeRangeBean, MNKitInterface.SetDevPushconfigCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }

        try {
            WeakReference<MNKitInterface.SetDevPushconfigCallBack> weakReference = new WeakReference<>(callBack);
            if (!AuthorityManager.isHadDeviceConfigAuthority(sn)) {
                if (weakReference != null && weakReference.get() != null) {
                    BaseBean baseBean = new BaseBean();
                    baseBean.setCode(5005);
                    baseBean.setMsg("Restricted permission");
                    weakReference.get().onSetDevPushconfigSuc(baseBean);
                }
                return;
            }
            int pushenable = 1;
            List<PushconfigBean.PushListBean> pushList = MnKitAlarmType.getAlarmTypeList(alarmOptions);
            if (pushList == null || pushList.size() == 0) {
                pushenable = 0;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sn", sn);
            jsonObject.put("pushenable", pushenable);
            jsonObject.put("sleepenable", sleepenable);
            jsonObject.put("sleep_time_range", sleepTimeRangeBean);
            jsonObject.put("push_list", pushList);
            jsonObject.put("level", 1);//大卫说默认1.
            jsonObject.put("channel_no", channelId);

            String stringJson = jsonObject.toJSONString();
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v3/pushconfig/set")
                    .addHeader("app_key", MNOpenSDK.getAppKey())
                    .addHeader("app_secret", MNOpenSDK.getAppSecret())
                    .addHeader("access_token", MNOpenSDK.getAccessToken())
                    .content(stringJson)
                    .build()
                    .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onSetDevPushconfigFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (response != null && weakReference != null && weakReference.get() != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onSetDevPushconfigSuc(response);
                                } else if (response.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onSetDevPushconfigFailed(response.getMsg());
                                } else {
                                    weakReference.get().onSetDevPushconfigFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取注册账号验证码(Get registered account verification code)
     *
     * @param country_code 国家码（手机号码注册有效）(Country code (mobile phone number registration is valid))
     * @param phone        手机号码(mobile phone number)
     * @param email        电子邮箱（E-mail）
     * @param locale       语言 "en_US"或"zh_CN"（Language "en_US" or "zh_CN"）
     * @param valid        注册方式  "sms"或者"email"（Registration method "sms" or "email"）
     * @param callBack     Callback method
     */

    public static void getAuthcode(String country_code, String phone, String email, String locale, String valid, MNKitInterface.AuthcodeCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        try {
            WeakReference<MNKitInterface.AuthcodeCallBack> weakReference = new WeakReference<>(callBack);
            JSONObject jsonObject = new JSONObject();
            if (!TextUtils.isEmpty(country_code)) {
                jsonObject.put("country_code", country_code);
            }
            if (!TextUtils.isEmpty(phone)) {
                jsonObject.put("phone", phone);
            }
            if (!TextUtils.isEmpty(email)) {
                jsonObject.put("email", email);
            }
            if (!TextUtils.isEmpty(locale)) {
                jsonObject.put("locale", locale);
            }
            if (!TextUtils.isEmpty(valid)) {
                jsonObject.put("valid", valid);
            }

            OkHttpUtils.postString().mediaType(jsonType).id(1001)
                    .url(MNOpenSDK.getDomain() + "/api/v3/users/signup/authcode")
                    .addHeader("app_key", MNOpenSDK.getAppKey())
                    .addHeader("app_secret", MNOpenSDK.getAppSecret())
                    .content(jsonObject.toJSONString())
                    .build()
                    .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onGetAuthcodeFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (response != null && weakReference != null && weakReference.get() != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onGetAuthcodeSuc(response);
                                } else if (response.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onGetAuthcodeFailed(response.getMsg());
                                } else {
                                    weakReference.get().onGetAuthcodeFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 新用户注册（New User Registration）
     *
     * @param email       注册邮箱（与手机号码注册相斥）（Registered email (excludes registration with mobile number)）
     * @param phone       注册手机号码（与注册邮箱相斥）（Registered mobile number (excludes registered email)）
     * @param active_code 对应注册方式的验证码（Verification code corresponding to registration method）
     * @param callBack    Callback method
     */

    public static void regiterUser(String email, String phone, String active_code, MNKitInterface.RegiterUserCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        try {
            WeakReference<MNKitInterface.RegiterUserCallBack> weakReference = new WeakReference<>(callBack);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_key", MNOpenSDK.getAppKey());
            jsonObject.put("app_secret", MNOpenSDK.getAppSecret());
            if (!TextUtils.isEmpty(phone)) {
                jsonObject.put("phone", phone);
            } else if (!TextUtils.isEmpty(email)) {
                jsonObject.put("email", email);
            }
            jsonObject.put("active_code", active_code);

            OkHttpUtils.postString().mediaType(jsonType).id(100)
                    .url(MNOpenSDK.getDomain() + "/api/v1/users/signup/validate")
                    .content(jsonObject.toJSONString())
                    .build().execute(
                    new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onRegiterUserFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (response != null && weakReference != null && weakReference.get() != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onRegiterUserSuc(response);
                                } else if (response.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onRegiterUserFailed(response.getMsg());
                                } else {
                                    weakReference.get().onRegiterUserFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 为新用户设置密码（Set password for new user）
     *
     * @param email        邮箱用户（与手机号码注册相斥）（Email user (excludes registration with mobile number)）
     * @param phone        手机号码用户（与注册邮箱相斥）（Mobile number user (excludes registered email)）
     * @param password     用户密码 （user password）
     * @param country_code 国家码（Country code）
     * @param active_code  验证码（Verification code）
     * @param callBack     Callback method
     */

    public static void setUserPassword(String email, String phone, String password, String country_code, String active_code, MNKitInterface.SetUserPasswordCallBack callBack) {
        if (MNOpenSDK.mContext == null) {
            Log.e("MNKit", MNOpenSDK.errMsg);
            return;
        }
        try {
            WeakReference<MNKitInterface.SetUserPasswordCallBack> weakReference = new WeakReference<>(callBack);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_key", MNOpenSDK.getAppKey());
            jsonObject.put("app_secret", MNOpenSDK.getAppSecret());
            jsonObject.put("email", email);
            jsonObject.put("country_code", country_code);
            jsonObject.put("phone", phone);
            jsonObject.put("password", password);
            jsonObject.put("active_code", active_code);
            String data = jsonObject.toJSONString();
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v1/users/signup/save")
                    .content(data)
                    .build().execute(
                    new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onSetUserPasswordFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (response != null && weakReference != null && weakReference.get() != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onSetUserPasswordSuc(response);
                                } else if (response.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onSetUserPasswordFailed(response.getMsg());
                                } else {
                                    weakReference.get().onSetUserPasswordFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取收藏点信息（Get favorite point information）
     *
     * @param deviceId 设备ID（device Id）
     */
    public static void getFavoritePointsInfo(String deviceId, MNKitInterface.GetFavoritePointsInfoCallBack callback) {
        WeakReference<MNKitInterface.GetFavoritePointsInfoCallBack> weakReference = new WeakReference<>(callback);
        OkHttpUtils.get()
                .url(MNOpenSDK.getDomain() + "/api/v3/pre_position/list")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .addParams("device_id", deviceId)
                .build()
                .execute(new GenericsCallback<FavoritesInfoBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGetFavoritePointsInfoFailed(null);
                        }
                    }

                    @Override
                    public void onResponse(FavoritesInfoBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onGetFavoritePointsInfoSuc(response);
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onGetFavoritePointsInfoFailed(response.getMsg());
                            } else {
                                weakReference.get().onGetFavoritePointsInfoFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 删除摇头机设备预置点（Delete the preset of the shaking machine device）
     *
     * @param pre_positon_ids 对应预置点的id （The id of the preset point）
     * @param callback        Callback method
     */
    public static void delteFavoritePoints(ArrayList<String> pre_positon_ids, MNKitInterface.DelteFavoritePointsCallBack callback) {
        WeakReference<MNKitInterface.DelteFavoritePointsCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pre_positon_ids", pre_positon_ids);
        String jsonData = jsonObject.toJSONString();
        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/pre_position/delete")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(jsonData)
                .build().execute(
                new GenericsCallback<FavoriteDelteBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onDelteFavoritePointsFailed(null);
                        }
                    }

                    @Override
                    public void onResponse(FavoriteDelteBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onDelteFavoritePointsSuc(response);
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onDelteFavoritePointsFailed(response.getMsg());
                            } else {
                                weakReference.get().onDelteFavoritePointsFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 保存摇头机设备预置点（Save the shaking machine equipment presets）
     *
     * @param name        预置点名字 （Preset point name）
     * @param position_id 预置点ID  （Preset point ID）
     * @param device_id   设备ID    （Device ID）
     * @param file        预支点位置的图片（Picture of Pivot Point Position）
     * @param callback    Callback method
     */
    public static void saveFavoritePoint(String name, String position_id, String device_id, File file, MNKitInterface.SaveFavoritePointsCallBack callback) {
        WeakReference<MNKitInterface.SaveFavoritePointsCallBack> weakReference = new WeakReference<>(callback);
        OkHttpUtils.post().url(MNOpenSDK.getDomain() + "/api/v3/pre_position/save")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .addParams("name", name)
                .addParams("position_id", position_id)
                .addParams("device_id", device_id)
                .addParams("desc", "desc")
                .addFile("image", file.getName(), file)
                .build().execute(
                new GenericsCallback<FavoritePointSaveBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onSaveFavoritePointsFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(FavoritePointSaveBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onSaveFavoritePointsSuc(response);
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onSaveFavoritePointsFailed(response.getMsg());
                            } else {
                                weakReference.get().onSaveFavoritePointsFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 获取已经接受设备分享的用户基础信息(Get basic user information that has been shared by the device)
     *
     * @param device_id Device Id
     */
    public static void getInviteShareUsers(String device_id, MNKitInterface.GetInviteShareUsersCallBack callback) {
        WeakReference<MNKitInterface.GetInviteShareUsersCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("device_id", device_id);
        String strJson = jsonObject.toJSONString();
        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/user/device_share/users")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .build()
                .execute(new GenericsCallback<ShareUserListBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGetInviteShareUsersFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(ShareUserListBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onGetInviteShareUsersSuc(response);
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onGetInviteShareUsersFailed(response.getMsg());
                            } else {
                                weakReference.get().onGetInviteShareUsersFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 获取某个设备的分享历史（Get the sharing history of a device）
     *
     * @param sn       Device SN
     * @param callback Callback method
     */
    public static void getSharedHistoryBySn(String sn, MNKitInterface.GetSharedHistoryCallBack callback) {
        WeakReference<MNKitInterface.GetSharedHistoryCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sn", sn);//数组
        jsonObject.put("size", 5);//数组
        String strJson = jsonObject.toJSONString();
        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/devive_share/history")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .build()
                .execute(new GenericsCallback<SharedHistoryBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGetSharedHistoryFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(SharedHistoryBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onGetSharedHistorySuc(response);
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onGetSharedHistoryFailed(response.getMsg());
                            } else {
                                weakReference.get().onGetSharedHistoryFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 分享设备到指定账号（Share device to specified account）
     *
     * @param sn          Device SN
     * @param time_limit  分享有效时间（Share effective time）
     * @param authority   被分享人可以查看的权限（Shared permissions that people can view）
     * @param account     被分享人账号（手机账号/邮箱账号）（Shared account (mobile phone account)）
     * @param countryCode 国家码(Country Code)
     * @param locale      语言（language "zh_CN" 或 "en_US"）
     * @param callback    Callback method
     */
    public static void shareDevToAccount(String sn, int time_limit, int authority, String account, String countryCode, String locale, MNKitInterface.ShareDevToAccountCallBack callback) {
        WeakReference<MNKitInterface.ShareDevToAccountCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sn", sn);
        if (time_limit >= 0) {
            jsonObject.put("time_limit", time_limit);
        }
        jsonObject.put("country_code", countryCode);
        jsonObject.put("authority", authority);
        if (account.contains("@") && account.endsWith("com")) {
            jsonObject.put("email", account);
        } else {
            jsonObject.put("phone", account);
        }
        jsonObject.put("locale", locale);

        String strJson = jsonObject.toJSONString();
        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/devices/share")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .build()
                .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onSharedDevToAccountFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onSharedDevToAccountFailed(response.getMsg());
                            } else {
                                weakReference.get().onSharedDevToAccountSuc(response);
                            }
                        }
                    }
                });
    }

    /**
     * 获取分享设备的二维码(Get the QR code of the sharing device)
     *
     * @param device_id  Device Id
     * @param authority  被分享人可以查看的权限（Shared permissions that people can view）
     * @param time_limit 分享有效时间（Share effective time）
     * @param callback   Callback method
     */
    public static void getShareDevQrCode(String device_id, int authority, int time_limit, MNKitInterface.GetShareDevQrCodeCallBack callback) {
        WeakReference<MNKitInterface.GetShareDevQrCodeCallBack> weakReference = new WeakReference<>(callback);
        String url = MNOpenSDK.getDomain() + "/api/v3/user/device_share/qrcode?device_id=" + device_id + "&authority=" + authority;
        if (time_limit > 0) {
            url = url + "&time_limit=" + time_limit;
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (weakReference != null && weakReference.get() != null) {
                    weakReference.get().onGetShareDevQrCodeFailed(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && weakReference != null && weakReference.get() != null) {
                    if (response.isSuccessful()) {
                        byte[] bytes = response.body().bytes();
                        weakReference.get().onGetShareDevQrCodeSuc(bytes);
                    } else {
                        weakReference.get().onGetShareDevQrCodeFailed(request.toString());
                    }
                }
            }
        });
    }

    /**
     * 删除报警消息
     *
     * @param alarmIds 报警消息Ids
     */
    public static void delAlarms(ArrayList<String> alarmIds, MNKitInterface.DelAlarmsCallBack callback) {
        WeakReference<MNKitInterface.DelAlarmsCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("alarm_ids", alarmIds);
        String strJson = jsonObject.toJSONString();

        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/alarms/delete")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .build()
                .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onDelAlarmsFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onDelAlarmsSuc();
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onDelAlarmsFailed(response.getMsg());
                            } else {
                                weakReference.get().onDelAlarmsFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 删除对应时间段的报警消息
     *
     * @param deviceSns        设备SN数组
     * @param startTime        删除报警消息的起始时间
     * @param endTime          删除报警消息的结束时间
     * @param alarmTypeOptions 要删除报警消息的类型
     * @param personName       人脸识别消息类型中对应名字的人
     * @param callback         回调方法
     */

    public static void delAlarmsByTime(ArrayList<String> deviceSns, long startTime, long endTime, int alarmTypeOptions, String personName, MNKitInterface.DelAlarmsByTimeCallBack callback) {
        WeakReference<MNKitInterface.DelAlarmsByTimeCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceSns", deviceSns);//数组
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        jsonObject.put("alarmTypes", MnKitAlarmType.getAlarmTypeList(alarmTypeOptions));
        jsonObject.put("personName", personName);
        String jsonData = jsonObject.toJSONString();
        try {
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v3/alarms/delete")
                    .addHeader("app_key", MNOpenSDK.getAppKey())
                    .addHeader("app_secret", MNOpenSDK.getAppSecret())
                    .addHeader("access_token", MNOpenSDK.getAccessToken())
                    .content(jsonData)
                    .build().execute(
                    new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onDelAlarmsByTimeFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (response != null && weakReference != null && weakReference.get() != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onDelAlarmsByTimeSuc();
                                } else if (response.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onDelAlarmsByTimeFailed(response.getMsg());
                                } else {
                                    weakReference.get().onDelAlarmsByTimeFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 标记报警消息状态
     *
     * @param alarmIds 报警消息Ids
     * @param status   标记值 0：未读，1：已读
     */
    public static void modifyStates(ArrayList<String> alarmIds, int status, MNKitInterface.AlarmModifyStateCallBack callback) {
        WeakReference<MNKitInterface.AlarmModifyStateCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("alarmIds", alarmIds);
        jsonObject.put("status", status);
        String strJson = jsonObject.toJSONString();
        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/alarms/status/modify")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .build()
                .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onModifyStateFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(BaseBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onModifyStateSuc();
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onModifyStateFailed(response.getMsg());
                            } else {
                                weakReference.get().onModifyStateFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 标记对应时间段的报警消息的已读状态
     *
     * @param deviceSns        设备SN数组
     * @param startTime        删除报警消息的起始时间
     * @param endTime          删除报警消息的结束时间
     * @param alarmTypeOptions 要删除报警消息的类型
     * @param personName       人脸识别消息类型中对应名字的人
     * @param callback         回调方法
     */
    public static void modifyStatesByTime(ArrayList<String> deviceSns, long startTime, long endTime, int alarmTypeOptions, String personName, int status, MNKitInterface.AlarmModifyStateByTimeCallBack callback) {
        WeakReference<MNKitInterface.AlarmModifyStateByTimeCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceSns", deviceSns);//数组
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        jsonObject.put("alarmTypes", MnKitAlarmType.getAlarmTypeList(alarmTypeOptions));
        jsonObject.put("personName", personName);
        jsonObject.put("status", status);
        String jsonData = jsonObject.toJSONString();
        try {
            OkHttpUtils.postString().mediaType(jsonType)
                    .url(MNOpenSDK.getDomain() + "/api/v3/alarms/status/modify_all")
                    .addHeader("app_key", MNOpenSDK.getAppKey())
                    .addHeader("app_secret", MNOpenSDK.getAppSecret())
                    .addHeader("access_token", MNOpenSDK.getAccessToken())
                    .content(jsonData)
                    .build().execute(
                    new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (weakReference != null && weakReference.get() != null) {
                                weakReference.get().onModifyStateByTimeFailed(e.getMessage());
                            }
                        }

                        @Override
                        public void onResponse(BaseBean response, int id) {
                            if (response != null && weakReference != null && weakReference.get() != null) {
                                if (response.getCode() == 2000) {
                                    weakReference.get().onModifyStateByTimeSuc();
                                } else if (response.getCode() == 3000) {
                                    loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                    weakReference.get().onModifyStateByTimeFailed(response.getMsg());
                                } else {
                                    weakReference.get().onModifyStateByTimeFailed(response.getMsg());
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取设备固件版本
     *
     * @param sn       设备SN
     * @param pal      设备视频制式
     * @param lang     设备语言
     * @param max_size 固件数量
     * @param callback 回调方法
     */
    public static void getFirmwareVer(String sn, String pal, String lang, int max_size, MNKitInterface.GetFirmwareVerCallBack callback) {
        WeakReference<MNKitInterface.GetFirmwareVerCallBack> weakReference = new WeakReference<>(callback);
        OkHttpUtils.get()
                .url(MNOpenSDK.getDomain() + "/api/v3/devices/firmware")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .addParams("sn", sn)
                .addParams("lang", lang)
                .addParams("pal", pal)
                .addParams("max_size", max_size + "")//写1就好
                .build()
                .execute(new GenericsCallback<FirmwareVerBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGetFirmwareVerFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(FirmwareVerBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onGetFirmwareVerSuc(response);
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onGetFirmwareVerFailed(response.getMsg());
                            } else {
                                weakReference.get().onGetFirmwareVerFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 获取等待分享的设备列表
     *
     * @param callback
     */
    public static void getWaitingSharedDev(MNKitInterface.GetShareWaitingDevCallBack callback) {
        WeakReference<MNKitInterface.GetShareWaitingDevCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        String strJson = jsonObject.toJSONString();
        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/devices_share/waiting_list")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .build()
                .execute(new GenericsCallback<WaitingShareDevBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onGetShareWaitingDevFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(WaitingShareDevBean response, int id) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onGetShareWaitingDevSuc(response);
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onGetShareWaitingDevFailed(response.getMsg());
                            } else {
                                weakReference.get().onGetShareWaitingDevFailed(response.getMsg());
                            }
                        }
                    }
                });
    }

    /**
     * 接受分享设备
     *
     * @param sn
     * @param received
     * @param callback
     */
    public static void receivedSharingDevice(String sn, int received, MNKitInterface.ReceivedShareDeviceCallBack callback) {
        WeakReference<MNKitInterface.ReceivedShareDeviceCallBack> weakReference = new WeakReference<>(callback);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sn", sn);
        jsonObject.put("received", received);
        String strJson = jsonObject.toJSONString();

        OkHttpUtils.postString().mediaType(jsonType)
                .url(MNOpenSDK.getDomain() + "/api/v3/device_share/receive")
                .addHeader("app_key", MNOpenSDK.getAppKey())
                .addHeader("app_secret", MNOpenSDK.getAppSecret())
                .addHeader("access_token", MNOpenSDK.getAccessToken())
                .content(strJson)
                .id(received)
                .build()
                .execute(new GenericsCallback<BaseBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int received) {
                        if (weakReference != null && weakReference.get() != null) {
                            weakReference.get().onReceivedShareDeviceFailed(e.getMessage(), received);
                        }
                    }

                    @Override
                    public void onResponse(BaseBean response, int received) {
                        if (response != null && weakReference != null && weakReference.get() != null) {
                            if (response.getCode() == 2000) {
                                weakReference.get().onReceivedShareDeviceSuc(response, received);
                            } else if (response.getCode() == 3000) {
                                loginWithAccount(LocalDataUtils.getUserName(), LocalDataUtils.getPassword(), null);
                                weakReference.get().onReceivedShareDeviceFailed(response.getMsg(), received);
                            } else {
                                weakReference.get().onReceivedShareDeviceFailed(response.getMsg(), received);
                            }
                        }
                    }
                });
    }

}
