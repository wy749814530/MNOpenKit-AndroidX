package com.mnopensdk.demo.utils;

/**
 * Created by WIN on 2018/6/12.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import java.util.ArrayList;
import java.util.List;

import com.mnopensdk.demo.R;

public class PermissionUtil {
    private String TAG = PermissionUtil.class.getSimpleName();
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
                        perText.append("\r\n" +index + ". " + message);
                        index ++;
                    }
                    break;
                }

                case CAMERA: {
                    String message = context.getString(R.string.permission_CAMERA);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" +index + ". " + message );
                        index ++;
                    }
                    break;
                }
                case READ_CONTACTS:
                case WRITE_CONTACTS:
                case GET_ACCOUNTS: {
                    String message = context.getString(R.string.permission_READ_CONTACTS);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" +index + ". " + message );
                        index ++;
                    }
                    break;
                }
                case ACCESS_FINE_LOCATION:
                case ACCESS_COARSE_LOCATION: {
                    String message = context.getString(R.string.permission_ACCESS_COARSE_LOCATION);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" +index + ". " + message );
                        index ++;
                    }
                    break;
                }
                case RECORD_AUDIO: {
                    String message = context.getString(R.string.permission_RECORD_AUDIO);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" +index + ". " + message );
                        index ++;
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
                        perText.append("\r\n" +index + ". " + message );
                        index ++;
                    }
                    break;
                }
                case BODY_SENSORS: {
                    String message = context.getString(R.string.permission_BODY_SENSORS);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" +index + ". " + message );
                        index ++;
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
                        perText.append("\r\n" +index + ". " + message );
                        index ++;
                    }
                    break;
                }
                case READ_EXTERNAL_STORAGE:
                case WRITE_EXTERNAL_STORAGE: {
                    String message = context.getString(R.string.permission_WRITE_EXTERNAL_STORAGE);
                    if (!perText.toString().contains(message)) {
                        perText.append("\r\n" +index + ". " + message );
                        index ++;
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
}