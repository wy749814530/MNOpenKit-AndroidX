package com.mnopensdk.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mnopensdk.demo.utils.CountDownTimerUtils;
import com.mnopensdk.demo.utils.LogUtil;
import com.mnopensdk.demo.utils.PatternVerify;
import com.mnopensdk.demo.utils.StatusBarUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mn.bean.restfull.BaseBean;

import java.util.Locale;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mnopensdk.demo.R;


public class RegisterActivity extends Activity implements MNKitInterface.AuthcodeCallBack, MNKitInterface.RegiterUserCallBack, MNKitInterface.SetUserPasswordCallBack {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    @BindView(R.id.register_getcode)
    TextView registerGetcode;
    @BindView(R.id.register_login)
    Button registerLogin;
    @BindView(R.id.activity_register)
    LinearLayout activityRegister;
    @BindView(R.id.country_code)
    TextView countryCodeText;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.reg_clear)
    ImageView regClear;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.activity_base_title_rl)
    RelativeLayout activityBaseTitleRl;
    @BindView(R.id.rigster_countyname)
    TextView rigsterCountyname;
    @BindView(R.id.rest_pwds_1)
    EditText restPwds1;
    @BindView(R.id.show_pwd_hint_1)
    ImageView showPwd1;
    @BindView(R.id.rest_pwds_2)
    EditText restPwds_2;
    @BindView(R.id.show_pwd_hint_2)
    ImageView showPwd2;
    private String _mCountryCode = "86";


    String userName, verifyCode; // 用户名，验证码
    private boolean isMobileRegister = true; // 用户注册方式
    String language;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, activityBaseTitleRl);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setTimeOut(30 * 1000);

        language = Locale.getDefault().getLanguage();

        String allCode = countryCodeText.getText().toString();
        _mCountryCode = allCode.substring(1, allCode.length());


        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPhone.getText().toString().trim().length() > 0) {
                    regClear.setVisibility(View.VISIBLE);
                } else {
                    regClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        restPwds_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = restPwds_2.getText().toString().trim();
                if (string.length() > 5) {
                    registerLogin.setEnabled(true);
                    registerLogin.setBackgroundResource(R.mipmap.btn_normal);
                } else {
                    registerLogin.setEnabled(false);
                    registerLogin.setBackgroundResource(R.mipmap.btn_no);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @OnClick({R.id.iv_back, R.id.register_getcode, R.id.country_code, R.id.register_login,
            R.id.reg_clear, R.id.show_pwd_hint_1, R.id.show_pwd_hint_2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back://返回键
                finish();
                break;
            case R.id.register_getcode://获取验证码
                if (codeValid()) {
                    loadingDialog.show();
                    registerGetcode.setEnabled(false);
                    String allCode = countryCodeText.getText().toString();
                    _mCountryCode = allCode.substring(1, allCode.length());
                    if (language.equals("zh")) {//区别语言
                        if (isMobileRegister) {//手机注册
                            MNKit.getAuthcode(_mCountryCode, userName, null, "zh_CN", "sms", this);
                        } else {//邮箱注册
                            MNKit.getAuthcode(_mCountryCode, null, userName, "zh_CN", "email", this);
                        }
                    } else {
                        if (isMobileRegister) {//手机注册
                            MNKit.getAuthcode(_mCountryCode, userName, null, "en_US", "sms", this);
                        } else {//邮箱注册
                            MNKit.getAuthcode(_mCountryCode, null, userName, "en_US", "email", this);
                        }
                    }
                }
                break;
            case R.id.register_login:
                String trim = etPwd.getText().toString().trim();
                if (trim.length() > 5) {
                    if (valid()) {//前后密码一致再调接口（验证验证码是否正确）
                        loadingDialog.show();
                        verifyCode = etPwd.getText().toString().trim();
                        if (isMobileRegister) { // 手机
                            MNKit.regiterUser(null, userName, verifyCode, this);
                        } else { // 邮箱
                            MNKit.regiterUser(userName, null, verifyCode, this);
                        }
                        registerLogin.setEnabled(false);
                    }
                } else {
                    ToastUtils.MyToastCenter("输入正确信息");
                }
                break;
            case R.id.reg_clear:
                etPhone.setText("");
                break;
            case R.id.show_pwd_hint_1:
                if (isEye1) {//设置密文
                    isEye1 = false;
                    showPwd1.setBackgroundResource(R.mipmap.login_icon_close);
                    restPwds1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {//设置明文
                    isEye1 = true;
                    showPwd1.setBackgroundResource(R.mipmap.login_icon_open);
                    restPwds1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                restPwds1.setSelection(restPwds1.getText().toString().length());
                break;
            case R.id.show_pwd_hint_2:
                if (isEye2) {//设置密文
                    isEye2 = false;
                    showPwd2.setBackgroundResource(R.mipmap.login_icon_close);
                    restPwds_2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {//设置明文
                    isEye2 = true;
                    showPwd2.setBackgroundResource(R.mipmap.login_icon_open);
                    restPwds_2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                restPwds_2.setSelection(restPwds_2.getText().toString().length());
                break;
        }
    }

    // 密码是否可见
    private boolean isEye1 = false;
    private boolean isEye2 = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1:
                if (data.getStringExtra("ac") != null && !data.getStringExtra("ac").equals("")) {
                    countryCodeText.setText(data.getStringExtra("ac"));
                    if (language.equals("zh")) {
                        rigsterCountyname.setText(data.getStringExtra("cns_name"));
                    } else {
                        rigsterCountyname.setText(data.getStringExtra("ens_name"));
                    }
                    String allCode = countryCodeText.getText().toString();
                    _mCountryCode = allCode.substring(1, allCode.length());
                    LogUtil.i("Register", "_mCountryCode:" + allCode);
                }
                break;
        }
    }


    /**
     * 验证码按钮验证
     *
     * @return
     */
    private boolean codeValid() {
        userName = etPhone.getText().toString().trim();
        if (userName != null && userName.contains("@")) {
            isMobileRegister = false;
        } else {
            isMobileRegister = true;
        }
        if (isMobileRegister) {//输入的是手机
            if (!TextUtils.isEmpty(userName)) {
                if (PatternVerify.verifyMobile(userName.trim())) {
                    return true;
                } else {
                    ToastUtils.MyToast(getResources().getString(R.string.registe_nona));
                    return false;
                }
            } else {
                ToastUtils.MyToast(getResources().getString(R.string.register_phoneempty));
                return false;
            }
        } else {//输入的是邮箱格式
            if (!TextUtils.isEmpty(userName)) {
                if (PatternVerify.verifyEmial(userName.trim())) {
                    return true;
                } else {
                    ToastUtils.MyToast(getResources().getString(R.string.register_email_invalid));
                    return false;
                }
            } else {
                ToastUtils.MyToast(getResources().getString(R.string.register_emptyemail));
                return false;
            }
        }
    }

    private String password;   // 密码

    private boolean valid() {//两个密码前后比较一下
        String _pwd = restPwds1.getText().toString().trim();
        String _repwd = restPwds_2.getText().toString().trim();
        if (!PatternVerify.isNumAndChar(_pwd)) {
            ToastUtils.MyToast(getResources().getString(R.string.set_request));
            return false;
        }
        if (_pwd.equals(_repwd)) {
            password = _pwd;
            return true;
        } else {
            ToastUtils.MyToast(getResources().getString(R.string.set_noequals));
            return false;
        }

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            //  overridePendingTransition(R.anim.activity_stay, R.anim.activity_close);
        }
        return super.onKeyDown(keyCode, event);
    }

    // 定时刷新数据
    private final CountDownTimerUtils countDownTimer = new CountDownTimerUtils(30000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            //为了优化所作的判断，防止界面不在了还在倒计时
            if (!isFinishing()) {
                registerGetcode.setText(String.valueOf(millisUntilFinished / 1000) + "s");
            }
            if (isFinishing()) {
                cancel();
            }
        }

        @Override
        public void onFinish() {
            registerGetcode.setEnabled(true);
            registerGetcode.setText(getResources().getString(R.string.rister_getcall));
        }
    };

    @Override // 获取手机验证码成功
    public void onGetAuthcodeSuc(BaseBean result) {
        if (isMobileRegister) {
            MNKit.setUserPassword(null, userName, password, _mCountryCode, verifyCode, this);
        } else {//邮箱注册设置密码

            MNKit.setUserPassword(userName, null, password, _mCountryCode, verifyCode, this);
        }
    }

    @Override // 验证手机失败
    public void onGetAuthcodeFailed(String msg) {
        Log.i(TAG, "== onCaptchaError ==" + msg);
        closePro();
        String msgStr = msg == null ? getResources().getString(R.string.net_noperfect) : msg;
        ToastUtils.MyToast(msgStr);
        registerLogin.setEnabled(true);
    }

    @Override
    public void onRegiterUserSuc(BaseBean result) {
        closePro();
        if (result != null) {
            switch (result.getCode()) {
                case 2000:
                    // 获取手机验证码成功
                    registerGetcode.setEnabled(false);
                    countDownTimer.start(); // 设置计时
                    ToastUtils.MyToastCenter(getString(R.string.code_send));
                    break;
                case 3001:
                case 4000:
                    registerGetcode.setEnabled(true);
                    ToastUtils.MyToastCenter(getResources().getString(R.string.net_noperfect));
                    break;
                case 5002://email已被使用
                    registerGetcode.setEnabled(true);
                    ToastUtils.MyToastCenter(getString(R.string.email_has_been_used));
                    break;
                case 5003://手机号被用
                    registerGetcode.setEnabled(true);
                    ToastUtils.MyToastCenter(getString(R.string.phone_has_been_used));
                    break;
                case 6001://短信发送失败
                    registerGetcode.setEnabled(true);
                    ToastUtils.MyToastCenter(getString(R.string.rigster_smsfail));
                    break;
                case 6002://邮件发送失败
                    registerGetcode.setEnabled(true);
                    ToastUtils.MyToastCenter(getString(R.string.rigster_emailfail));
                    break;
                default:
                    registerGetcode.setEnabled(true);
                    ToastUtils.MyToastCenter(getResources().getString(R.string.net_noperfect));
                    break;
            }

        }
    }

    @Override
    public void onRegiterUserFailed(String msg) {
        closePro();
        registerGetcode.setEnabled(true);
        ToastUtils.MyToast(getString(R.string.net_noperfect));
    }

    @Override
    public void onSetUserPasswordSuc(BaseBean result) {
        closePro();
        registerLogin.setEnabled(true);
        if (result != null) {
            if (result.getCode() == 2000) {
                ToastUtils.MyToast(getResources().getString(R.string.set_succ));
                goSucc();
            } else {
                ToastUtils.MyToast(getString(R.string.net_err_and_try));
            }
        }
    }


    @Override
    public void onSetUserPasswordFailed(String msg) {
        closePro();
        ToastUtils.MyToast(getResources().getString(R.string.net_noperfect));
        registerLogin.setEnabled(true);

    }

    private void goSucc() {
        ToastUtils.MyToast("注册成功");
        this.finish();
    }

    private void closePro() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
