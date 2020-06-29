package com.mnopensdk.demo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.mnopensdk.demo.BaseApplication;
import com.mnopensdk.demo.widget.RuleAlertDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.List;

import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2018/11/27 0027.
 */

public class PermissionUtils {

    private String TAG = PermissionUtils.class.getSimpleName();
    public static final String READ_CALENDAR = "android.permission.READ_CALENDAR";
    public static final String WRITE_CALENDAR = "android.permission.WRITE_CALENDAR";

    public static final String CAMERA = "android.permission.CAMERA";

    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";

    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";

    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";

    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";
    public static final String ADD_VOICEMAIL = "com.android.voicemail.permission.ADD_VOICEMAIL";
    public static final String USE_SIP = "android.permission.USE_SIP";
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";

    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";

    public static final String SEND_SMS = "android.permission.SEND_SMS";
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";
    public static final String READ_SMS = "android.permission.READ_SMS";
    public static final String RECEIVE_WAP_PUSH = "android.permission.RECEIVE_WAP_PUSH";
    public static final String RECEIVE_MMS = "android.permission.RECEIVE_MMS";

    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";


    public static List<String> refusedPers = new ArrayList<>();
    public static List<String> caterPers = new ArrayList<>();

    public static String transformText(Context context, List<String> permissions) {
        StringBuilder perText = new StringBuilder();
        int index = 1;
        for (String permission : permissions) {
            switch (permission) {
                case READ_CALENDAR:
                case WRITE_CALENDAR: {
                    String message = context.getString(R.string.permission_WRITE_CALENDAR);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" + index + ". " + message);
                        index++;
                    }
                    break;
                }

                case CAMERA: {
                    String message = context.getString(R.string.permission_CAMERA);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" + index + ". " + message);
                        index++;
                    }
                    break;
                }
                case READ_CONTACTS:
                case WRITE_CONTACTS:
                case GET_ACCOUNTS: {
                    String message = context.getString(R.string.permission_READ_CONTACTS);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" + index + ". " + message);
                        index++;
                    }
                    break;
                }
                case ACCESS_FINE_LOCATION:
                case ACCESS_COARSE_LOCATION: {
                    String message = context.getString(R.string.permission_ACCESS_COARSE_LOCATION);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" + index + ". " + message);
                        index++;
                    }
                    break;
                }
                case RECORD_AUDIO: {
                    String message = context.getString(R.string.permission_RECORD_AUDIO);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" + index + ". " + message);
                        index++;
                    }
                    break;
                }
                case READ_PHONE_STATE:
                case CALL_PHONE:
                case READ_CALL_LOG:
                case WRITE_CALL_LOG:
                case USE_SIP:
                case PROCESS_OUTGOING_CALLS: {
                    String message = context.getString(R.string.permission_READ_PHONE_STATE);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" + index + ". " + message);
                        index++;
                    }
                    break;
                }
                case BODY_SENSORS: {
                    String message = context.getString(R.string.permission_BODY_SENSORS);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" + index + ". " + message);
                        index++;
                    }
                    break;
                }
                case SEND_SMS:
                case RECEIVE_SMS:
                case READ_SMS:
                case RECEIVE_WAP_PUSH:
                case RECEIVE_MMS: {
                    String message = context.getString(R.string.permission_SEND_SMS);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" + index + ". " + message);
                        index++;
                    }
                    break;
                }
                case READ_EXTERNAL_STORAGE:
                case WRITE_EXTERNAL_STORAGE: {
                    String message = context.getString(R.string.permission_WRITE_EXTERNAL_STORAGE);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" + index + ". " + message);
                        index++;
                    }
                    break;
                }
            }
        }
        return perText.toString();
    }

    /**
     * 跳转到权限设置界面
     */
    public static void toPermissionSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }

    public static final String[] PermissionGroup = new String[]{
            Permission.READ_PHONE_STATE, Permission.CALL_PHONE,//手机配置
            Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, // 存储,读取文件
            Permission.READ_CONTACTS, Permission.GET_ACCOUNTS, // 存联系人
            Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION, // 位置
            Permission.CAMERA, // 相机
            Permission.RECORD_AUDIO // 麦克风
    };

    public static void getPermission(Context context) {
        AndPermission.with(context)
                .runtime()
                .permission(PermissionGroup)
                .onGranted(permissions -> {
                    //通过所以权限
                })
                .onDenied(permissions -> {
                    if (refusedPers.size() > 0) {
                        refusedPers.clear();
                    }
                    if (caterPers.size() > 0) {
                        caterPers.clear();
                    }
                    for (String per : permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(context, per)) {
                            //    LogUtil.i("SolashActivity", "狠狠拒绝 ： " + per);
                            refusedPers.add(per);
                        } else {
                            //    LogUtil.i("SolashActivity", "欲拒还迎 ： " + per);
                            caterPers.add(per);
                        }
                    }

                    if (caterPers.size() > 0) {
                        new RuleAlertDialog(context).builder().setCancelable(false).
                                setTitle(null).
                                setMsg(context.getString(R.string.order_better_service) + context.getString(R.string.app_name) + context.getString(R.string.order_better_service_permissions) + "\r\n" + transformText(context, caterPers)).
                                setOkButton(context.getString(R.string.authorize_now), v1 -> {
                                    getPermission(context);
                                }).setCancelButton(context.getString(R.string.next_time_say), v2 -> {
                            BaseApplication.getInstance().mActivityStack.AppExit();
                        }).show();
                    } else if (refusedPers.size() > 0) {
                        new RuleAlertDialog(context).builder().setCancelable(false).
                                setTitle(null).
                                setMsg(context.getString(R.string.permission_refused_tip1) + context.getString(R.string.app_name) + context.getString(R.string.permission_refused_tip2) + "\r\n" + transformText(context, refusedPers)).
                                setOkButton(context.getString(R.string.go_to_settings), v1 -> {
                                    toPermissionSetting(context);
                                }).setCancelButton(context.getString(R.string.next_time_say), v2 -> {
                            BaseApplication.getInstance().mActivityStack.AppExit();
                        }).show();
                    }
                })
                .start();
    }

}
