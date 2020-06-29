package com.mnopensdk.demo.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by caojun on 2017/9/11.
 */

public class WifiUtils {

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */

    public static final boolean isGPSOpen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查wifi是否处开连接状态
     *
     * @return
     */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifiInfo.isConnected();
    }

    public static void reconnect(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiManager.disconnect();
        mWifiManager.reconnect();
    }

    /**
     * 检查wifi强弱
     */
    public static int checkWifiState(Context context) {
        if (isWifiConnect(context)) {
            WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            int wifi = mWifiInfo.getRssi();//获取wifi信号强度
            if (wifi > -50 && wifi < 0) {//最强
                return 5;
            } else if (wifi > -70 && wifi < -50) {//较强
                return 4;
            } else if (wifi > -80 && wifi < -70) {//较弱
                return 3;
            } else if (wifi > -100 && wifi < -80) {//微弱
                return 2;
            } else {
                return 1;
            }
        } else {
            //无连接
            //  mImageView.setImageResource(R.drawable.wifi_null);
            return 0;
        }
    }


    public static boolean isWiFiActive(Context context) {
        //判断当前wifi是否打开
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] infos = connectivity.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.getType() == ConnectivityManager.TYPE_WIFI && ni.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取WiFi列表
     *
     * @param context ScanResult   Math.abs(scanResult.level) 表示信号强度
     * @return
     */
    public static List<ScanResult> getScanWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        List<ScanResult> wifiList = wifiManager.getScanResults();
        return wifiList.size() > 0 ? wifiList : new ArrayList();
    }

    public static List<WifiConfiguration> getWifiConfiguration(Context context) {
        LogUtil.i("WifiUtils", "=============== start  ===============");
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = mWifiManager.getConnectionInfo();

        // 得到配置好的网络连接
        List<WifiConfiguration> wifiConfigList = mWifiManager.getConfiguredNetworks();

        //当前连接SSID
        String currentSSid = info.getSSID();
        currentSSid = currentSSid.replace("\"", "");
        LogUtil.i("WifiUtils", "currentSSid : " + currentSSid);
        for (WifiConfiguration wifiConfiguration : wifiConfigList) {
            //配置过的SSID
            String configSSid = wifiConfiguration.SSID;
            configSSid = configSSid.replace("\"", "");

            String safe = "";
            //比较networkId，防止配置网络保存相同的SSID
            if (currentSSid.equals(configSSid) && info.getNetworkId() == wifiConfiguration.networkId) {
                safe = "" + getSecurity(wifiConfiguration);
            }

            LogUtil.i("WifiUtils", "configSSid : " + configSSid + "  |  " + getSecurity(wifiConfiguration));
        }

        return wifiConfigList;
    }

    public static WifiConfiguration getCurrentWifiConfiguration(Context context) {
        LogUtil.i("WifiUtils", "=============== start  ===============");
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = mWifiManager.getConnectionInfo();

        // 得到配置好的网络连接
        List<WifiConfiguration> wifiConfigList = mWifiManager.getConfiguredNetworks();

        //当前连接SSID
        String currentSSid = info.getSSID();
        currentSSid = currentSSid.replace("\"", "");
        LogUtil.i("WifiUtils", "currentSSid : " + currentSSid);
        WifiConfiguration currentWifi = null;
        for (WifiConfiguration wifiConfiguration : wifiConfigList) {
            //配置过的SSID
            String configSSid = wifiConfiguration.SSID;
            configSSid = configSSid.replace("\"", "");

            LogUtil.i("WifiUtils", "configSSid : " + configSSid + "  |  " + getSecurity(wifiConfiguration));
            if (currentSSid.equals(configSSid) && info.getNetworkId() == wifiConfiguration.networkId) {
                //比较networkId，防止配置网络保存相同的SSID
                currentWifi = wifiConfiguration;
                break;
            }
        }
        return currentWifi;
    }

    static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;

    static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    /**
     * 连接到指定的WiFi
     *
     * @param context
     * @param SSID
     * @param Password
     * @param Type
     * @return
     */
    public static WifiConfiguration createWifiConfiguration(Context context, String SSID, String Password, int Type) {
        LogUtil.i("createWifiConfiguration", "SSID :" + SSID + " , Password : " + Password + " , Type : " + Type);
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.priority = 1;
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = IsExsits(context, SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 1) //WIFICIPHER_NOPASS
        {
            //config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //config.wepTxKeyIndex = 0;
        } else if (Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else if (Type == 4) { // android6.0 wpa2加密方式的wifi
            config.allowedAuthAlgorithms.clear();
            config.allowedGroupCiphers.clear();
            config.allowedKeyManagement.clear();
            config.allowedPairwiseCiphers.clear();
            config.allowedProtocols.clear();
            config.SSID = "\"" + SSID + "\"";
            config.preSharedKey = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 获取热点的加密类型
     */
    public static int getType(ScanResult scanResult) {
        if (scanResult.capabilities.contains("NOPASS"))
            return 1;
        else if (scanResult.capabilities.contains("WEP"))
            return 2;
        else if (scanResult.capabilities.contains("WPA")) {
            return 3;
        } else if (scanResult.capabilities.contains("WPA2")) {
            return 4;
        }

        return 3;
    }

    /**
     * 判断当前是否存在该WiFi
     *
     * @param context
     * @param SSID
     * @return
     */
    private static WifiConfiguration IsExsits(Context context, String SSID) {
        if (SSID == null) {
            return null;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        try {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID != null) {
                    String cssid = existingConfig.SSID.replace("\"", "").trim();
                    String ssid = SSID.trim();
                    if (cssid.equals(ssid)) {
                        return existingConfig;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static boolean connect2Wifi(Context context, WifiConfiguration configuration) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.disconnect();
        wifiManager.reconnect();
        int netId = wifiManager.addNetwork(configuration);
        return wifiManager.enableNetwork(netId, true);
    }

    public static String getNowSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    public static WifiInfo getCurrentWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        wifiManager.getConfiguredNetworks();
        return wifiInfo;
    }


    public static boolean removeSensorWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String wifiSSID = wifiInfo.getSSID();
        if (!TextUtils.isEmpty(wifiSSID) && "manniu-xxxxxx".length() < wifiSSID.length()) {
            wifiSSID = wifiSSID.substring(1, wifiSSID.length() - 1);
        }
        LogUtil.i("WifiUtils", "==== removeSensorWifi wifiSSID =====" + wifiSSID);
        if (!TextUtils.isEmpty(wifiSSID) && (wifiSSID.startsWith("manniu-")) && (wifiSSID.length() == "manniu-xxxxxx".length())) {
            LogUtil.i("WifiUtils", "==== removeSensorWifi =====");
            return wifiManager.removeNetwork(wifiInfo.getNetworkId());
        }
        return true;
    }
}
