package com.mnopensdk.demo.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;
import com.mnopensdk.demo.fragment.FriendShareFragment;
import com.mnopensdk.demo.fragment.MyShareFragment;
import com.mnopensdk.demo.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mnopensdk.demo.R;

public class MyShareActivity extends AppCompatActivity {

    @BindView(R.id.ip_tablayout)
    TabLayout ipTablayout;
    @BindView(R.id.ip_viewPager)
    ViewPager ipViewPager;
    MyFragmentAdapter tabAdapter;
    List<Fragment> mFraments = new ArrayList<>();
    List<String> mPageTitles = new ArrayList<>();
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_base_lay)
    RelativeLayout rlBaseLay;
    @BindView(R.id.activity_my_share)
    LinearLayout activityMyShare;
    private MyShareFragment myShareFragment;
    private FriendShareFragment friendShareFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_share);
        ButterKnife.bind(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, rlBaseLay);

        myShareFragment = MyShareFragment.newInstance();
        friendShareFragment = FriendShareFragment.newInstance();
        mPageTitles.clear();
        mPageTitles.add(getString(R.string.tv_my_share));
        mPageTitles.add(getString(R.string.tv_other_share_me));
        mFraments.clear();
        mFraments.add(myShareFragment);
        mFraments.add(friendShareFragment);
        tabAdapter = new MyFragmentAdapter(getSupportFragmentManager(), mPageTitles, mFraments);
        ipViewPager.setAdapter(tabAdapter);
        ipTablayout.setupWithViewPager(ipViewPager);
        ipTablayout.setTabMode(TabLayout.MODE_FIXED);
        ipViewPager.setCurrentItem(0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    static class MyFragmentAdapter extends FragmentPagerAdapter {
        List<String> lmPageTitles = new ArrayList<>();
        List<Fragment> lmFragments = new ArrayList<>();

        public MyFragmentAdapter(FragmentManager fm, List<String> pageTitles, List<Fragment> fragments) {
            super(fm);
            this.lmPageTitles.clear();
            this.lmFragments.clear();
            this.lmPageTitles.addAll(pageTitles);
            this.lmFragments.addAll(fragments);
        }

        @Override
        public Fragment getItem(int position) {
            return this.lmFragments.get(position);
        }

        @Override
        public int getCount() {
            return this.lmFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return this.lmPageTitles.get(position);
        }
    }
}
