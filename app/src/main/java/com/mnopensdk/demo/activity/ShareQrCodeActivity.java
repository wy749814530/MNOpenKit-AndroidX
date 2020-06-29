package com.mnopensdk.demo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mnopensdk.demo.utils.CanvasImageUtils;
import com.mnopensdk.demo.utils.ShareUtil;
import com.mnopensdk.demo.utils.StatusBarUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mn.bean.restfull.DevicesBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mnopensdk.demo.R;

/**
 * Created by WIN on 2018/4/9.
 */

public class ShareQrCodeActivity extends AppCompatActivity implements View.OnLongClickListener, MNKitInterface.GetShareDevQrCodeCallBack {
    @BindView(R.id.iv_qrcode_image)
    ImageView ivQrcodeImage;
    @BindView(R.id.iv_qrcode_create_failed)
    ImageView ivQrcodeCreateFailed;
    @BindView(R.id.fl_qrcode_create_failed_lay)
    FrameLayout flQrcodeCreateFailedLay;
    @BindView(R.id.tv_uers_msg)
    TextView tvUersMsg;
    @BindView(R.id.tv_5_minutes)
    TextView tv5Minutes;
    @BindView(R.id.bt_share_album)
    Button btSaveAlbum;
    @BindView(R.id.tv_sharing_time_1)
    TextView tvSharingTime1;
    @BindView(R.id.tv_sharing_time_2)
    TextView tvSharingTime2;
    @BindView(R.id.tv_sharing_time_3)
    TextView tvSharingTime3;
    @BindView(R.id.tv_sharing_time_4)
    TextView tvSharingTime4;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_base_lay)
    RelativeLayout rlBaseLay;
    private int mPermission;
    private LoadingDialog loadingDialog;
    private Bitmap mBitmap;
    private DevicesBean devicesBean;
    private DevicesBean myDevicesBean;
    private String devId = "";
    private int devType = 1;
    private int shareTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_qr_code_invite_activity);
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, rlBaseLay);

        devicesBean = (DevicesBean) getIntent().getSerializableExtra("devicesBean");
        myDevicesBean = (DevicesBean) getIntent().getSerializableExtra("myDevicesBean");
        if (devicesBean != null) {
            devId = devicesBean.getId();
            devType = devicesBean.getType();
        } else if (myDevicesBean != null) {
            devId = myDevicesBean.getId();
            devType = myDevicesBean.getType();
        }

        mPermission = getIntent().getIntExtra("permission", 1);
        loadingDialog = new LoadingDialog(this);
        if (devId != null && !"".equals(devId)) {
            loadingDialog.show();
            MNKit.getShareDevQrCode(devId, mPermission, shareTime, this);
        }
        ivQrcodeImage.setOnLongClickListener(this);
    }

    @OnClick({R.id.iv_qrcode_create_failed, R.id.bt_share_album, R.id.tv_5_minutes, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                setResult(200);
                finish();
                break;
            case R.id.iv_qrcode_create_failed:
            case R.id.tv_5_minutes:
                loadingDialog.show();
                MNKit.getShareDevQrCode(devId, mPermission, shareTime, this);
                break;
            case R.id.bt_share_album:
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.share_wechat_img_code);
                if (bitmap == null || mBitmap == null) {
                    ToastUtils.MyToastBottom(getString(R.string.shared_failed));
                } else {
                    Bitmap newBitmap = CanvasImageUtils.createImage(bitmap, mBitmap);
//                Uri uri = saveBmp2Gallery(mBitmap);
                    Uri uri = saveBmp2Gallery(newBitmap);
                    if (uri == null) {
                        ToastUtils.MyToastBottom(getString(R.string.shared_failed));
                    } else {
                        ShareUtil.shareImage(this, uri, getString(R.string.app_name));
                    }
                }
                break;
        }
    }

    /**
     * @param bmp 获取的bitmap数据
     */
    public Uri saveBmp2Gallery(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        String fileName = null;
        //系统相册目录
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;

        // 声明文件对象
        File file = null;
        // 声明输出流
        FileOutputStream outStream = null;

        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = new File(galleryPath, System.currentTimeMillis() + ".png");

            // 获得文件相对路径
            fileName = file.toString();
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(fileName);
            if (null != outStream) {
                bmp.compress(Bitmap.CompressFormat.PNG, 90, outStream);
            }

        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //通知相册更新
        MediaStore.Images.Media.insertImage(getContentResolver(), bmp, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);
        return uri;
    }


    @Override
    public void onGetShareDevQrCodeSuc(byte[] bytes) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (mBitmap == null) {
                    ivQrcodeImage.setVisibility(View.GONE);
                    flQrcodeCreateFailedLay.setVisibility(View.VISIBLE);
                } else {
                    ivQrcodeImage.setVisibility(View.VISIBLE);
                    flQrcodeCreateFailedLay.setVisibility(View.GONE);
                    ivQrcodeImage.setImageBitmap(mBitmap);
                }
            }
        });
    }

    @Override
    public void onGetShareDevQrCodeFailed(String msg) {
        this.runOnUiThread(() -> {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            ivQrcodeImage.setVisibility(View.GONE);
            flQrcodeCreateFailedLay.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(200);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Uri uri = saveBmp2Gallery(mBitmap);
        if (uri != null) {
            ToastUtils.MyToastBottom(getString(R.string.share_qrcode_succ));
        }
        return false;
    }

    @OnClick({R.id.tv_sharing_time_1, R.id.tv_sharing_time_2, R.id.tv_sharing_time_3, R.id.tv_sharing_time_4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_sharing_time_1:
                shareTime = -1;
                tvSharingTime1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_blue_22_normal));
                tvSharingTime2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));

                tvSharingTime1.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSharingTime2.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime3.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime4.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));

                loadingDialog.show();
                MNKit.getShareDevQrCode(devId, mPermission, shareTime, this);
                break;
            case R.id.tv_sharing_time_2:
                shareTime = 30 * 60;
                tvSharingTime1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_blue_22_normal));
                tvSharingTime3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));

                tvSharingTime1.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime2.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSharingTime3.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime4.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));

                loadingDialog.show();
                MNKit.getShareDevQrCode(devId, mPermission, shareTime, this);
                break;
            case R.id.tv_sharing_time_3:
                shareTime = 60 * 60;
                tvSharingTime1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_blue_22_normal));
                tvSharingTime4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));

                tvSharingTime1.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime2.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime3.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSharingTime4.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));

                loadingDialog.show();
                MNKit.getShareDevQrCode(devId, mPermission, shareTime, this);
                break;
            case R.id.tv_sharing_time_4:
                shareTime = 24 * 60 * 60;
                tvSharingTime1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_blue_22_normal));

                tvSharingTime1.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime2.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime3.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime4.setTextColor(ContextCompat.getColor(this, R.color.white));

                loadingDialog.show();
                MNKit.getShareDevQrCode(devId, mPermission, shareTime, this);
                break;
        }
    }

}
