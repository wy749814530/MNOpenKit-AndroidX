package com.mnopensdk.demo.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mnopensdk.demo.event.ShareDevSucEvent;
import com.mnopensdk.demo.utils.PatternVerify;
import com.mnopensdk.demo.utils.PermissionUtil;
import com.mnopensdk.demo.utils.StatusBarUtil;
import com.mnopensdk.demo.utils.ToastUtils;
import com.mnopensdk.demo.widget.LoadingDialog;
import com.mnopensdk.demo.widget.RuleAlertDialog;
import com.mn.bean.restfull.BaseBean;
import com.mn.bean.restfull.DevicesBean;
import com.mn.bean.restfull.SharedHistoryBean;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import MNSDK.MNKit;
import MNSDK.inface.MNKitInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mnopensdk.demo.R;

/**
 * Created by Administrator on 2019/7/9 0009.
 */

public class ShareWithUserNameActivity extends AppCompatActivity implements MNKitInterface.ShareDevToAccountCallBack {

    @BindView(R.id.ed_share_contact)
    EditText edShareContact;
    @BindView(R.id.iv_select_contact)
    ImageView ivSelectContact;
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
    @BindView(R.id.login_user)
    LinearLayout loginUser;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private SharedHistoryBean.ListBean mHistoryBean;
    private DevicesBean devicesBean;
    private int mPermission = 1;
    private LoadingDialog loadingDialog;
    private int shareTime = -1;
    private static final int ADDRESS_BOOK_CODE = 1000;
    private static final int REQUEST_CALL_PHONE_PERMISSION = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_with_user_name);
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, rlBaseLay);

        loadingDialog = new LoadingDialog(this).setTimeOut(15 * 1000);
        devicesBean = (DevicesBean) getIntent().getSerializableExtra("devicesBean");
        mPermission = getIntent().getIntExtra("permission", 1);
        mHistoryBean = (SharedHistoryBean.ListBean) getIntent().getSerializableExtra("historyBean");

        if (mHistoryBean != null) {
            edShareContact.setText(mHistoryBean.getUser_name());
            if (mHistoryBean.getStart_time() == 0 || mHistoryBean.getEnd_time() == 0) {
                setShareTime(0);
            } else if (mHistoryBean.getEnd_time() - mHistoryBean.getStart_time() <= 30 * 60 * 1000) {
                setShareTime(1);
            } else if (mHistoryBean.getEnd_time() - mHistoryBean.getStart_time() <= 60 * 60 * 1000) {
                setShareTime(2);
            } else if (mHistoryBean.getEnd_time() - mHistoryBean.getStart_time() <= 24 * 60 * 60 * 1000) {
                setShareTime(3);
            } else {
                setShareTime(0);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.iv_back, R.id.iv_select_contact, R.id.tv_sharing_time_1, R.id.tv_sharing_time_2, R.id.tv_sharing_time_3, R.id.tv_sharing_time_4, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_select_contact:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, ADDRESS_BOOK_CODE);
                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, ADDRESS_BOOK_CODE);
                    } else {
                        //如果没有权限那么申请权限
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CALL_PHONE_PERMISSION);
                    }
                }
                break;
            case R.id.btn_confirm:
                // TODO 开始分享设备
                String account = edShareContact.getText().toString().trim();
                if (TextUtils.isEmpty(account)) {
                    ToastUtils.MyToastBottom(getString(R.string.tv_enter_sharer_account_first));
                    return;
                }
                String language = Locale.getDefault().getLanguage();
                String locale = "zh_CN";
                if (language.startsWith("zh")) {
                    locale = "zh_CN";
                } else {
                    locale = "en_US";
                }

                if (PatternVerify.verifyMobile(account) || PatternVerify.verifyEmial(account)) {
                    if (loadingDialog != null) {
                        loadingDialog.show();
                    }
                    MNKit.shareDevToAccount(devicesBean.getSn(), shareTime, mPermission, account, "86", locale, this);
                } else {
                    ToastUtils.MyToastBottom(getString(R.string.tv_format_account_failed));
                }
                break;
            case R.id.tv_sharing_time_1:
                setShareTime(0);
                break;
            case R.id.tv_sharing_time_2:
                setShareTime(1);
                break;
            case R.id.tv_sharing_time_3:
                setShareTime(2);
                break;
            case R.id.tv_sharing_time_4:
                setShareTime(3);
                break;
        }
    }

    public void setShareTime(int timeGrade) {
        switch (timeGrade) {
            case 0:
                shareTime = -1;

                tvSharingTime1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_blue_22_normal));
                tvSharingTime2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));

                tvSharingTime1.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSharingTime2.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime3.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime4.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                break;
            case 1:
                shareTime = 30 * 60;
                tvSharingTime1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_blue_22_normal));
                tvSharingTime3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));

                tvSharingTime1.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime2.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSharingTime3.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime4.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                break;
            case 2:
                shareTime = 60 * 60;
                tvSharingTime1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_blue_22_normal));
                tvSharingTime4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));

                tvSharingTime1.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime2.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime3.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSharingTime4.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                break;
            case 3:
                shareTime = 24 * 60 * 60;
                tvSharingTime1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_gray_22_normal));
                tvSharingTime4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_blue_22_normal));

                tvSharingTime1.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime2.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime3.setTextColor(ContextCompat.getColor(this, R.color.gray_666666));
                tvSharingTime4.setTextColor(ContextCompat.getColor(this, R.color.white));
                break;
        }
    }

    @Override
    public void onSharedDevToAccountFailed(String msg) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        ToastUtils.MyToastBottom(getString(R.string.tv_sharing_failed));
    }

    @Override
    public void onSharedDevToAccountSuc(BaseBean bean) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (bean == null) {
            ToastUtils.MyToastBottom(getString(R.string.tv_sharing_failed));
        } else if (bean.getCode() == 2000) {
            ToastUtils.MyToastBottom(getString(R.string.tv_sharing_success));
            EventBus.getDefault().post(new ShareDevSucEvent());
            finish();
        } else if (bean.getCode() == 5001) {
            ToastUtils.MyToastBottom(getString(R.string.tv_5001_invalid_sn));
        } else if (bean.getCode() == 5002) {
            ToastUtils.MyToastBottom(getString(R.string.tv_5002_not_belong_to_you));
        } else if (bean.getCode() == 5003) {
            ToastUtils.MyToastBottom(getString(R.string.tv_5003_own_device));
        } else if (bean.getCode() == 5004) {
            ToastUtils.MyToastBottom(getString(R.string.tv_already_shared));
        } else if (bean.getCode() == 5006) {
            ToastUtils.MyToastBottom(getString(R.string.tv_shares_reached_limit));
            EventBus.getDefault().post(new ShareDevSucEvent());
            finish();
        } else {
            ToastUtils.MyToastBottom(getString(R.string.tv_sharing_failed));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == this.REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults == null || grantResults.length == 0) {
                return;
            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //执行自己的业务逻辑
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, ADDRESS_BOOK_CODE);
            } else {
                new RuleAlertDialog(this).builder().setCancelable(false).
                        setTitle(getString(R.string.tip_title)).
                        setMsg(getString(R.string.tv_not_opened_address_book_permissions)).setMsgAlignStyle(Gravity.CENTER).
                        setOkButton(getString(R.string.go_to_settings), v1 -> {
                            PermissionUtil.toPermissionSetting(this);
                        }).setCancelButton(getString(R.string.next_time_say), null).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*
  * 跳转联系人列表的回调函数
  * */
    String mName;
    String mPhone;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADDRESS_BOOK_CODE:
                if (data == null) {
                    return;
                }
                //处理返回的data,获取选择的联系人信息
                Uri uri = data.getData();
                String[] contacts = getPhoneContacts(uri);
                if (contacts.length == 2) {
                    mName = contacts[0];
                    mPhone = contacts[1];
                    if (mName == null || "".equals(mName) || mPhone == null || "".equals(mPhone)) {
                        // 信息不完整
                        mName = "";
                        mPhone = "";
                        edShareContact.setText(getString(R.string.select_inviter));
                        edShareContact.setTextColor(ContextCompat.getColor(this, R.color.login_int));
                    } else {
                        // 完整信息
                        mPhone = mPhone.replace(" ", "");
                        edShareContact.setText(mPhone);
                        edShareContact.setTextColor(ContextCompat.getColor(this, R.color.dark));
                    }
                } else {
                    // 信息不完整
                    mName = "";
                    mPhone = "";
                    edShareContact.setText(getString(R.string.select_inviter));
                    edShareContact.setTextColor(ContextCompat.getColor(this, R.color.login_int));
                }
                break;
//            case COUNTRY_CODE_BACK:
//                if (data != null && data.getStringExtra("ac") != null && !"".equals(data.getStringExtra("ac"))) {
//                    tvCountryCode.setText(data.getStringExtra("ac"));
//                    String allCode = tvCountryCode.getText().toString();
//                    _mCountryCode = allCode.substring(1, allCode.length());
//                }
//                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            //取得联系人姓名
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);

            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);

            if (phone != null && phone.getCount() > 0) {
                phone.moveToFirst();
                try {
                    int index = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    if (!phone.isNull(index)) {
                        contact[1] = phone.getString(index);
                    } else {
                        contact[1] = "";
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    contact[1] = "";
                } catch (CursorIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    contact[1] = "";
                }
            }
            phone.close();
            cursor.close();
        } else {
            return null;
        }
        return contact;
    }

}
