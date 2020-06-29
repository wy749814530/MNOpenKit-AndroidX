package com.mnopensdk.demo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.view.*;
import android.view.SurfaceHolder.Callback;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mnopensdk.demo.R;

import com.google.gson.Gson;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.mnopensdk.demo.Constants;
import com.mnopensdk.demo.BaseApplication;
import com.mnopensdk.demo.utils.LogUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.utils.Utils;
import com.mnopensdk.demo.widget.CustomDialog;
import com.mnopensdk.demo.widget.IsOkDialog;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.decoding.RGBLuminanceSource;
import com.mn.bean.restfull.DevStateInfoBean;
import com.mn.bean.restfull.BaseBean;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;

public class QRcodeActivity extends Activity implements Callback, MNKitInterface.DeviceStateInfoCallBack, MNKitInterface.DeviceBindViewCallBack, MNKitInterface.BindShareDeviceViewCallBack {
    public final String TAG = "QRcodeActivity";
    @BindView(R.id.iv_flight)
    ImageView ivFlight;
    @BindView(R.id.scan_back)
    Button scanBack;
    @BindView(R.id.qrcode_photo)
    LinearLayout qrcodePhoto;

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;
    private String location;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    public Handler getHandler() {
        return handler;
    }

    private IsOkDialog isOkDialog;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置为无标题格式
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);
        CameraManager.init(getApplication());
        BaseApplication.getInstance().mActivityStack.addActivity(this);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
        ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(1200);
        mQrLineView.startAnimation(animation);
        //MDAhAQEAbGUwNjFiMjRmNTVkZAAA  //MDAhAQEAbGUwNjFiMjRjMDIwMwAA
        isOkDialog = new IsOkDialog(this);
    }

    protected void light() {
        if (CameraManager.get().isFlashLightOn()) {
            CameraManager.get().offLight();
            ivFlight.setImageResource(R.mipmap.add_scan_code_opne);
        } else {
            CameraManager.get().openLight();
            ivFlight.setImageResource(R.mipmap.add_scan_code_colse);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().mActivityStack.removeActivity(this);
        inactivityTimer.shutdown();
        inactivityTimer = null;
        mContainer = null;
        mCropLayout = null;
        mediaPlayer = null;
        handler = null;
    }

    String vnResult;
    String snResult;
    int type = 0;

    public void handleDecode(String result) {
        //  LogUtil.i("uuuuuu", "eweewew" + result);
        getQrcodeData(result);
    }

    /**
     * 解析：对二维码扫描的结果进行解析
     *
     * @param result
     */
    private void getQrcodeData(String result) {
        LogUtil.i("DevInfoHelper", "result:" + result);
        if (result.contains("sn")) {
            try {
                inactivityTimer.onActivity();
                playBeepSoundAndVibrate();//vn:ABCDEF;sn:MDAhAQEAAGUwNjFiMjM0OTY0ZQAA;mac:e0-61-b2-23-9c-cf
                // LogUtil.i("QRcodeActivity", result);// vn:ABCDEF;sn:MDAhAQEAbDAwMDA4OTM2MjAzMQAA;
                //解析二维码返回结果
                String vnString = result.split(";")[0];
                String snString = result.split(";")[1];
                if (vnString.startsWith("vn:")) {
                    vnResult = vnString.split(":")[1];

                } else if (vnString.startsWith("sn:")) {
                    snResult = vnString.split(":")[1];
                }
                if (snString.startsWith("sn:")) {
                    snResult = snString.split(":")[1];
                } else if (snString.startsWith("vn:")) {
                    vnResult = snString.split(":")[1];
                }
                // 处理返回结果，进行判断以及添加设备
                if (result.equals("")) {
                    ToastUtils.MyToast(getString(R.string.try_other_ways));
                } else {
                    if (vnResult == null || vnResult.equals("") || snResult == null || snResult.equals("")) {
                        ToastUtils.MyToast(getString(R.string.device_not_identified_try_other_ways));
                    } else {
                        type = 1;
                        Constants.VN = vnResult;
                        MNKit.getDeviceStateInfoWithSn(snResult, this);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (result.contains("invite_code")) {//分享设备
            LogUtil.i(TAG, result);
            type = 3; //  添加了共享设备
            int indexStart = result.indexOf("=");
            String inviteCode = result.substring(indexStart + 1, result.length());
            LogUtil.i(TAG, inviteCode);
            MNKit.bindShareDeviceByInviteCode(inviteCode, this);
        } else {
            type = 2;
            snResult = result;
            MNKit.bindDeviceBySnAndVn(result, "ABCDEF", 2,this);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);

            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width
                    / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height
                    / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);

        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(QRcodeActivity.this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        try {
            if (playBeep && mediaPlayer != null) {
                mediaPlayer.start();
            }
            if (vibrate) {
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(VIBRATE_DURATION);
            }
        } catch (Exception e) {
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    String name = "";

    @Override
    public void onBindDeviceSuc(BaseBean result) {
        //此处作出判断
        LogUtil.i(TAG, "onBindDeviceSuc : " + new Gson().toJson(result));
        if (result != null) {
            int code = result.getCode();
            if (code == 2000) {
                ToastUtils.MyToast(getString(R.string.add_addsuccess));
                name = getString(R.string.dev_type_name);
                MNKit.modifyDeviceNameWithSN(snResult, name, null);
                if (HomeActivity.instance != null) {
                    HomeActivity.instance.onRefresh();
                }
                finish();
            } else if (code == 5001) {
                ToastUtils.MyToast(getString(R.string.add_exit_user));
                Intent intent = new Intent(this, AddDeviceExclamationActivity.class);
                intent.putExtra("devSn", snResult);
                startActivity(intent);
                finish();
            } else {
                ToastUtils.MyToast(getString(R.string.device_not_identified));
                finish();
            }
        }
    }

    @Override
    public void onBindDeviceFailed(String msg) {

    }

    @Override
    public void onGetDeviceStateFailed(String message) {
        ToastUtils.MyToast(getString(R.string.net_noperfect));
        finish();
    }

    @Override
    public void onGetDeviceStateSuc(DevStateInfoBean devInfoStateBean) {
        LogUtil.i("onGetDeviceStateSuc", "TAG : " + new Gson().toJson(devInfoStateBean));
        if (devInfoStateBean != null) {
            if ("invalid sn".equals(devInfoStateBean.getMsg())) {
                ToastUtils.MyToastCenter("invalid sn");
            } else if (devInfoStateBean.getCode() == 2000) {
                if (devInfoStateBean.getBind_state() == 0) {//没有被绑定直接去声波绑定流程
                    //判定设备类型进行相应界面
                    MNKit.bindDeviceBySnAndVn(snResult, vnResult, 2,this);
                } else if (devInfoStateBean.getBind_state() == 1) {//被自己绑定
                    //在判断是否在线是否给他配网
                    if (devInfoStateBean.getOnline() == 0) {
                        //TODO
                        ToastUtils.MyToastCenter(getString(R.string.qr_me_notline));
                    } else {
                        //设备在线直接提示
                        LogUtil.i("DevInfoHelper", "1 online:" + devInfoStateBean.getOnline());
                        ToastUtils.MyToastCenter(getString(R.string.qr_me));
                    }
                } else if (devInfoStateBean.getBind_state() == 2) { //被别人绑定
                    //作出相应提示
                    ToastUtils.MyToastCenter(getString(R.string.qr_other));
                    finish();
                }
            } else {
                ToastUtils.MyToastCenter(devInfoStateBean.getCode() + getString(R.string.net_noperfect));
            }
        }
    }

    /**
     * 检查网络是否可用
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager == null) {
                return false;
            }
            NetworkInfo netinfo = manager.getActiveNetworkInfo();
            if (netinfo != null) {
                if (netinfo.isConnected()) {
                    if (netinfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        Constants.netWakeType = 1;
                        return true;
                    } else if (netinfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Constants.netWakeType = 0;
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void showTipDlg(boolean isOk) {
        if (isOk) {
            isOkDialog.show();
            isOkDialog.setContextImage(R.mipmap.share_message_success);
            if (type == 3) {
                isOkDialog.setContextMsg(getString(R.string.add_share_dev_suc));
            }
        }
    }

    @Override
    public void onBindShareDeviceViewSuc(BaseBean response) {
        // 设备分享给用户， 用户扫码绑定成功
        LogUtil.i(TAG, "设备分享给用户， 用户扫码绑定成功" + new Gson().toJson(response));
        if (response.getCode() == 2000) {
            showTipDlg(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isOkDialog.dismiss();
                    if (HomeActivity.instance != null) {
                        HomeActivity.instance.onRefresh();
                    }
                    finish();
                }
            }, 1000);
        } else {
            ToastUtils.MyToastBottom(response.getMsg());
            finish();
        }
    }

    @Override
    public void onBindShareDeviceViewFailed(String msg) {
        // 设备分享给用户， 用户扫码绑定失败
        if (msg == null) {
            ToastUtils.MyToastCenter(getString(R.string.net_noperfect));
        } else {
            ToastUtils.MyToastCenter(msg);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            initDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.iv_flight, R.id.scan_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_flight:
                light();
                break;
            case R.id.scan_back:
                initDialog();
                break;
        }
    }

    private void initDialog() {
        if (customDialog == null) {
            customDialog = new CustomDialog(this, CustomDialog.DIALOG_EXIT_ADD, () -> {
                customDialog.dismiss();
                finish();
            });
            if (!isFinishing()) {
                customDialog.show();
            }
        } else {
            if (!isFinishing()) {
                customDialog.show();
            }
        }
    }

    private static final int REQUEST_CODE = 234;
    private String photo_path;
    private Bitmap scanBitmap;

    //图片选择
    private void photo() {
        Intent innerIntent = new Intent();
        if (VERSION.SDK_INT < 19) {
            innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            // innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            innerIntent.setAction(Intent.ACTION_PICK);
        }
        innerIntent.setType("image/*");

        //  innerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");

        QRcodeActivity.this
                .startActivityForResult(wrapperIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    String[] proj = {Media.DATA};
                    // 获取选中图片的路径
                    Cursor cursor = getContentResolver().query(data.getData(),
                            proj, null, null, null);
                    if (cursor.moveToFirst()) {

                        int column_index = cursor
                                .getColumnIndexOrThrow(Media.DATA);
                        photo_path = cursor.getString(column_index);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(),
                                    data.getData());
                        }
                    }
                    cursor.close();
                    new Thread(() -> {
                        Result result = scanningImage(photo_path);
                        // String result = decode(photo_path);
                        if (result == null) {
                            Looper.prepare();
                            ToastUtils.MyToastCenter(getString(R.string.qrcode_pic));
                            Looper.loop();
                        } else {
                            // 数据返回
                            String recode = recode(result.toString());
                            Intent data1 = new Intent();
                            data1.putExtra("result", recode);
                            setResult(300, data1);
                            String result1 = data1.getStringExtra("result");
                            LogUtil.i("uuuuuu", result1);
                            getQrcodeData(result1);

                        }
                    }).start();
                    break;
            }
        }
    }

    // TODO: 解析部分图片
    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        Options options = new Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小

        int sampleSize = (int) (options.outHeight / (float) 200);

        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);

        if (scanBitmap != null) {
            RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader = new QRCodeReader();
            try {

                return reader.decode(bitmap1, hints);

            } catch (NotFoundException e) {

                e.printStackTrace();

            } catch (ChecksumException e) {

                e.printStackTrace();

            } catch (FormatException e) {

                e.printStackTrace();

            }
        }
        return null;

    }

    /**
     * //TODO: TAOTAO 将bitmap由RGB转换为YUV //TOOD: 研究中
     *
     * @param bitmap 转换的图形
     * @return YUV数据
     */
    public byte[] rgb2YUV(Bitmap bitmap) {
        // 该方法来自QQ空间
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int len = width * height;
        byte[] yuv = new byte[len * 3 / 2];
        int y, u, v;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = pixels[i * width + j] & 0x00FFFFFF;

                int r = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 16) & 0xFF;

                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;

                y = y < 16 ? 16 : (y > 255 ? 255 : y);
                u = u < 0 ? 0 : (u > 255 ? 255 : u);
                v = v < 0 ? 0 : (v > 255 ? 255 : v);

                yuv[i * width + j] = (byte) y;
                // yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
                // yuv[len + (i >> 1) * width + (j & ~1) + 1] = (byte) v;
            }
        }
        return yuv;
    }

    /**
     * 中文乱码
     * <p>
     * 暂时解决大部分的中文乱码 但是还有部分的乱码无法解决 .
     * <p>
     * 如果您有好的解决方式 请联系 2221673069@qq.com
     * <p>
     * 我会很乐意向您请教 谢谢您
     *
     * @return
     */
    private String recode(String str) {
        String formart = "";

        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
            } else {
                formart = str;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formart;
    }

    @OnClick(R.id.qrcode_photo)
    public void onClick() {
        photo();
    }
}
