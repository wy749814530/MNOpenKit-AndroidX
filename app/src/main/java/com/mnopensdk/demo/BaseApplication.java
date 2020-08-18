package com.mnopensdk.demo;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.mn.MNApplication;
import com.mn.MNRegion;
import com.mn.bean.restfull.BaseBean;
import com.mn.bean.restfull.ServerMsgBean;
import com.mn.bean.restfull.WaitingShareDevBean;
import com.mn.okhttp3.OkHttpUtils;
import com.mn.okhttp3.log.LoggerInterceptor;
import com.mnopensdk.demo.activity.HomeActivity;
import com.mnopensdk.demo.base.ActivityStack;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.RuleAlertDialog;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import MNSDK.MNKit;
import MNSDK.MNOpenSDK;
import MNSDK.inface.MNKitInterface;
import okhttp3.OkHttpClient;


public class BaseApplication extends MNApplication {
//    public static String APP_KEY = "31cc93923faa4bff";// 公共KEY
//    public static String APP_SECRET = "f6c9deec31644885a123c8ffb573a52e";
    public static final String APP_KEY = "8Wa227sQ00S33p4y";// manniu
    public static final String APP_SECRET = "RlA8aCPlsuATT227kKTg003ncP35HYRI";
    private RuleAlertDialog shareDevDlg;
    private MyHandler myHandler = new MyHandler(this);

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化MNSDK
        //  HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
                /* .cookieJar(cookieJar1)*/
                .hostnameVerifier((hostname, session) -> true)
                /*   .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)*/
                .build();
        OkHttpUtils.initClient(okHttpClient);

        MNOpenSDK.initWithKeyAndSecret(this, APP_KEY, APP_SECRET, MNRegion.CN);
        // 设置需要提前建立与设备的连接
        MNOpenSDK.setP2pPreLinkState(true);

        Log.i("BaseApplication", "" + MNOpenSDK.getSDKVersion());
        mnApplication = this;
        mContext = getApplicationContext();
        mActivityStack = new ActivityStack();
        initConfiguration();
    }

    protected static BaseApplication mnApplication = null;
    /**
     * 上下文
     */
    protected Context mContext = null;
    /**
     * Activity 栈
     */
    public ActivityStack mActivityStack = null;

    public static BaseApplication getInstance() {
        return mnApplication;
    }

    private void initConfiguration() {
        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }

        //初始化的时候对上报进程进行控制，只在主进程下上报数据：判断是否是主进程（通过进程名是否为包名来判断）
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));

        //TODO 发布版本最后参数设置为false
        CrashReport.initCrashReport(getApplicationContext(), "327fe57a93", true);
    }

    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void OnServerMSG(ServerMsgBean data) {
        Log.i("BaseApplication", "" + data);
        /**
         * 1：设备分享
         * 2：取消设备分享或者设备分享过期
         * 3：添加设备
         * 4：解绑设备
         * 5：设备上线
         * 6：设备下线
         * 7：用户密码修改
         * 8：低功耗设备休眠
         * 10：被分享设备权限变更
         */
        myHandler.post(() -> {
            if (data.getActionType() == 1) {
                // 他人分享设备给本账号了
                myHandler.sendEmptyMessageDelayed(2000, 800);
            } else if (data.getActionType() == 2) {
                // 主账号取消设备分享或者设备分享过期
                if (HomeActivity.instance != null) {
                    HomeActivity.instance.onRefresh();
                }
            } else if (data.getActionType() == 3) {
                // 添加设备
            } else if (data.getActionType() == 4) {
                // 解绑设备
            } else if (data.getActionType() == 5) {
                // 有设备上线啦
            } else if (data.getActionType() == 6) {
                // 有设备离线啦
            } else if (data.getActionType() == 7) {
                // 7：用户密码修改
            } else if (data.getActionType() == 8) {
                // 8：低功耗设备休眠
            } else if (data.getActionType() == 10) {
                // 10：被分享设备权限变更
            }
        });
    }

    public void showShareWaitingDev(WaitingShareDevBean response) {
        if (response == null || response.getShare_devices() == null || response.getShare_devices().size() == 0 || (shareDevDlg != null && shareDevDlg.isShowing())) {
            return;
        }
        WaitingShareDevBean.ShareDevicesBean shareDevicesBean = response.getShare_devices().get(0);
        String user = shareDevicesBean.getDevice_user().getPhone();
        if (TextUtils.isEmpty(user)) {
            user = shareDevicesBean.getDevice_user().getEmail();
        }

        String shareSn = shareDevicesBean.getSn();
        String msgStr = String.format(getString(R.string.tv_sharing_invitation_description), user);

        if (HomeActivity.instance != null) {
            shareDevDlg = new RuleAlertDialog(HomeActivity.instance).builder().setCancelable(false).
                    setTitle(getString(R.string.tv_sharing_invitation)).
                    setMsg(msgStr).setMsgAlignStyle(Gravity.LEFT).
                    setOkButton(getString(R.string.tv_invitationed), v -> {
                        // 接受邀请
                        MNKit.receivedSharingDevice(shareSn, 1, receivedShareDeviceCallBack);
                    }).setCancelButton(getString(R.string.tv_refuse), v -> {
                // 拒绝邀请
                MNKit.receivedSharingDevice(shareSn, 0, receivedShareDeviceCallBack);
            });

            if (shareDevDlg != null && !shareDevDlg.isShowing()) {
                shareDevDlg.show();
            }
        }

    }

    MReceivedShareDeviceCallBack receivedShareDeviceCallBack = new MReceivedShareDeviceCallBack();

    private class MReceivedShareDeviceCallBack implements MNKitInterface.ReceivedShareDeviceCallBack {

        @Override
        public void onReceivedShareDeviceSuc(BaseBean response, int received) {
            // received 1：接受，0：拒绝
            if (received == 1) {
                if (HomeActivity.instance != null) {
                    HomeActivity.instance.onRefresh();
                }
            }
            myHandler.sendEmptyMessageDelayed(2000, 800);
        }

        @Override
        public void onReceivedShareDeviceFailed(String msg, int id) {
            ToastUtils.MyToastBottom(getString(R.string.net_err_and_try));
        }
    }

    MGetShareWaitingDevCallBack mShareWaitingDevCallBack = new MGetShareWaitingDevCallBack();

    private class MGetShareWaitingDevCallBack implements MNKitInterface.GetShareWaitingDevCallBack {


        @Override
        public void onGetShareWaitingDevFailed(String msg) {

        }

        @Override
        public void onGetShareWaitingDevSuc(WaitingShareDevBean response) {
            showShareWaitingDev(response);
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<BaseApplication> mContext;

        public MyHandler(BaseApplication context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mContext.get() != null) {
                if (msg.what == 2000) {
                    MNKit.getWaitingSharedDev(mContext.get().mShareWaitingDevCallBack);
                }
            }
        }
    }
}
