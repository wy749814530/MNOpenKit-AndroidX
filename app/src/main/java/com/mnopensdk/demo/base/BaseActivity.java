package com.mnopensdk.demo.base;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mnopensdk.demo.BaseApplication;
import com.mnopensdk.demo.R;
import com.mnopensdk.demo.utils.StatusBarUtil;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    private TextView tvTitle;
    private ImageView ivBack;
    private RelativeLayout activityBaseTitleRl;
    private TextView line;
    private FrameLayout activityBaseFl;
    private TextView tvLeft, tvRight;
    protected Context mContext = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        intBaseView();
        LayoutInflater mInflater = LayoutInflater.from(this);
        activityBaseFl.addView(mInflater.inflate(setViewLayout(), null));
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, activityBaseTitleRl);
        BaseApplication.getInstance().mActivityStack.addActivity(this);
        initViews();
        initData();
        initEvents();

    }

    private void intBaseView() {
        activityBaseTitleRl = (RelativeLayout) findViewById(R.id.activity_base_title_rl);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvRight = (TextView) findViewById(R.id.tv_rights);
        line = (TextView) findViewById(R.id.line);
        activityBaseFl = (FrameLayout) findViewById(R.id.activity_base_fl);

        ivBack.setOnClickListener(myOnClick);
        tvLeft.setOnClickListener(myOnClick);
        tvRight.setOnClickListener(myOnClick);
    }


    protected OnClickObj myOnClick = new OnClickObj();

    protected class OnClickObj implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    onBackKeyDown(false);
                    break;
                case R.id.tv_left:
                    onLeftTvClick();
                    break;
                case R.id.tv_rights:
                    onRightTvClick();
                    break;
            }

        }
    }

    // ---------------------- 返回键设置  -------------------------
    public void setBackVisibility(int isVisible) {
        ivBack.setVisibility(isVisible);
    }

    public void setBackImage(int res) {
        ivBack.setImageResource(res);
    }

    // ---------------------- 标题设置  -------------------------
    public void setTitleVisibility(int visibility) {
        activityBaseTitleRl.setVisibility(visibility);
    }

    protected void setTitle(String title) {
        if (title == null) {
            return;
        }
        tvTitle.setText(title);
    }

    public void setTitleColor(int color) {
        tvTitle.setTextColor(color);
    }

    public void setTitleBackgroundImage(int res) {
        activityBaseTitleRl.setBackgroundResource(res);
    }

    public String getTitleTv() {
        String text = tvTitle.getText().toString();
        return text;
    }

    // ---------------------- 右侧按钮设置  -------------------------
    protected void setRightVisibility(int visibility) {
        tvRight.setVisibility(visibility);

    }

    protected void setRightTv(String title) {
        if (title == null) {
            return;
        }
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(title);
    }

    protected void setRightIv(int res) {
        if (res == 0) {
            return;
        }
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0);
    }

    protected void setLeftTv(String title) {
        if (title == null) {
            return;
        }
        tvLeft.setVisibility(View.VISIBLE);
        tvLeft.setText(title);
    }

    protected void setLeftTv(int res) {
        if (res == 0) {
            return;
        }
        tvLeft.setVisibility(View.VISIBLE);
        tvLeft.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0);
    }

    // ---------------------- 标题下方线设置  -------------------------
    protected void setLineGone() {
        line.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackKeyDown(true);
        }
        return true;
    }

    public void finish() {
        super.finish();
        BaseApplication.getInstance().mActivityStack.removeActivity(this);
    }

    protected abstract int setViewLayout();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化View
     */
    protected abstract void initViews();

    /**
     * 注册事件
     */
    protected abstract void initEvents();

    /**
     * 返回键事件
     *
     * @param b
     */
    protected abstract void onBackKeyDown(boolean b);

    /**
     * 标题右侧点击事件
     */
    protected abstract void onRightTvClick();

    /**
     * 标题左侧点击事件
     */
    protected abstract void onLeftTvClick();
}
