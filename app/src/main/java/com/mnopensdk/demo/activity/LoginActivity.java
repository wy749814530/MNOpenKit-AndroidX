package com.mnopensdk.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mnopensdk.demo.base.BaseActivity;
import com.mnopensdk.demo.Constants;
import com.mnopensdk.demo.utils.SharedPreferUtils;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mn.bean.restfull.LoginBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import MNSDK.MNKit;
import MNSDK.MNOpenSDK;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.OnClick;

import com.mnopensdk.demo.R;


public class LoginActivity extends BaseActivity {
    public final String TAG = LoginActivity.class.getSimpleName();
    LoadingDialog progressHUD;
    @BindView(R.id.email_login)
    LinearLayout emailLogin;
    @BindView(R.id.email_login_form)
    LinearLayout emailLoginForm;
    @BindView(R.id.tv_forget_passward)
    TextView tvForgetPassward;
    @BindView(R.id.rl_forget_pwd)
    RelativeLayout rlForgetPwd;
    @BindView(R.id.email_sign_in_button)
    TextView emailSignInButton;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.rl_login_lay)
    RelativeLayout rlLoginLay;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;

    String countryID = "";
    String country;
    int tipCount = 0;


    @Override
    protected int setViewLayout() {
        return R.layout.activity_user_login;
    }

    @Override
    protected void initViews() {
        setBackVisibility(View.GONE);
        setTitle("用户登录");
    }

    @Override
    protected void initData() {
        // 更换为自己的APP_KEY 和 APP_SECRET
        setFilePath();
        userName = SharedPreferUtils.read(TAG, "username", "13291820925");
        password = SharedPreferUtils.read(TAG, "password", "wyu123");
        if (!"".equals(userName)) {
            etEmail.setText(userName);
        }
        if (!"".equals(password)) {
            etPassword.setText(password);
        }
    }

    @Override
    protected void initEvents() {
        progressHUD = new LoadingDialog(this);
    }

    @Override
    protected void onBackKeyDown(boolean b) {
        finish();
    }

    @Override
    protected void onRightTvClick() {

    }

    @Override
    protected void onLeftTvClick() {

    }


    @OnClick({R.id.rl_forget_pwd, R.id.email_sign_in_button, R.id.tv_register
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_forget_pwd:
                break;
            case R.id.email_sign_in_button:
                userName = etEmail.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    ToastUtils.MyToast("请输入有效的用户名和密码");
                    return;
                }
                initGoLogin();
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }


    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressHUD != null) {
            progressHUD.onRelease();
        }
    }

    public String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    // 内置SD卡
    private String getFilePath() {
        String tmpPath = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            tmpPath = Environment.getExternalStorageDirectory() == null ? null : Environment.getExternalStorageDirectory() + "/";
        } else {
            tmpPath = Environment.getDataDirectory() == null ? null : Environment.getDataDirectory() + "/";
        }
        return tmpPath;
    }

    public void setFilePath() {
        String path = getFilePath();
        if (path == null || "/data".equals(path)) {
            path = "/data/data/" + getPackageName(); // manniu的安装路径
        }

        Constants.CachesPath = getExternalFilesDir("Caches").getPath() + "/";
        Constants.ImagePath = path + "manniu/images/";
        Constants.RecordPath = path + "manniu/records/";
        Constants.photosPath = path + "manniu/photos/";

        File fileDir = new File(Constants.photosPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File recordDir = new File(Constants.RecordPath);
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        File imageDir = new File(Constants.ImagePath);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }

        File cachesdir = new File(Constants.CachesPath);
        if (!cachesdir.exists()) {
            cachesdir.mkdirs();
        }
    }

    String userName;
    String password;

    private void initGoLogin() {
        userName = etEmail.getText().toString();
        password = etPassword.getText().toString();

        boolean cancel = false;
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            cancel = true;
        }

        if (TextUtils.isEmpty(userName)) {
            cancel = true;
        }

        if (cancel) {
            ToastUtils.MyToast("请输入有效的用户名和密码");
        } else {
            progressHUD.show();
            // 切换域名
            // MNOpenSDK.setMNKitDomain(this, "https://restte.bullyun.com");
            // 用户登录
            MNKit.loginWithAccount(userName, password, new MNKitInterface.LoginCallBack() {
                @Override
                public void onLoginFailed(String s) {
                    if (progressHUD != null) {
                        progressHUD.dismiss();
                    }
                    ToastUtils.MyToastCenter(getString(R.string.net_err_and_try));
                }

                @Override
                public void onLoginSuccess(LoginBean loginBean) {
                    if (progressHUD != null) {
                        progressHUD.dismiss();
                    }
                    SharedPreferUtils.write(TAG, "username", userName);
                    SharedPreferUtils.write(TAG, "password", password);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            });
        }
    }
}

