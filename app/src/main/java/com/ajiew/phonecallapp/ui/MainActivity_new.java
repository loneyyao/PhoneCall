package com.ajiew.phonecallapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ajiew.phonecallapp.widget.NoScrollViewPager;
import com.ajiew.phonecallapp.R;
import com.ajiew.phonecallapp.widget.TabIndicatorItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity_new extends AppCompatActivity {

    @BindView(R.id.main_viewpager)
    NoScrollViewPager myViewPager;
    @BindView(R.id.main_tab)
    LinearLayout indicator;
    @BindView(R.id.tab_home)
    TabIndicatorItemView tabHome;
    @BindView(R.id.tab_shopcar)
    TabIndicatorItemView tabShopCar;
    @BindView(R.id.tab_mine)
    TabIndicatorItemView tabMine;
    List<Fragment> fragmentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        fragmentList = new ArrayList<>();

        tabHome.setTag(fragmentList.size());
        fragmentList.add(MainFragment.newInstance());

        tabShopCar.setTag(fragmentList.size());
        fragmentList.add(AddressListFragment.newInstance());

        tabMine.setTag(fragmentList.size());
        fragmentList.add(BadCallIListFragment.newInstance(1));
        tabHome.setSelected(true);

        myViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        myViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int childCount = indicator.getChildCount();
                for (int i = 0; i < childCount; ++i) {
                    indicator.getChildAt(i).setSelected(false);
                }
                View child = indicator.findViewWithTag(position);
                child.setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;
        private String[] titles;


        public MyFragmentPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList) {
            super(fragmentManager);
            this.fragmentList = fragmentList;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        }
    }

    public void onTabIndicatorItemClick(View v) {
        int index = (int) v.getTag();
        myViewPager.setCurrentItem(index);
    }
}
