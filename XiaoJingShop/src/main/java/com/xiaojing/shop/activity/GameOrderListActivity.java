package com.xiaojing.shop.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.wuzhanglong.library.activity.BaseActivity;
import com.wuzhanglong.library.http.HttpClientUtil;
import com.wuzhanglong.library.mode.BaseVO;
import com.xiaojing.shop.R;
import com.xiaojing.shop.fragment.GameOrderFragment;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;

public class GameOrderListActivity extends BaseActivity {
    private String[] mTitleDataList = {"已付款","未付款"};
    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragmentList;

    @Override
    public void baseSetContentView() {
        contentInflateView(R.layout.game_order_list_activity);
    }

    @Override
    public void initView() {
        mBaseTitleTv.setText("订单管理");
        mViewPager = getViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(mTitleDataList.length);
        initMagicIndicator();
    }

    @Override
    public void bindViewsListener() {

    }

    @Override
    public void getData() {
        HttpClientUtil.show(mThreadUtil);
    }

    @Override
    public void hasData(BaseVO vo) {

    }

    @Override
    public void noData(BaseVO vo) {

    }

    @Override
    public void noNet() {

    }

    private void initMagicIndicator() {
        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);//是固定

        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleDataList.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
//                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);// 文字变化样式
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mTitleDataList[index]);
                simplePagerTitleView.setTextSize(14);
                simplePagerTitleView.setNormalColor(Color.parseColor("#616161"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#f57c00"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(1.6f));
//                indicator.setYOffset(UIUtil.dip2px(context, 39));
                indicator.setLineHeight(UIUtil.dip2px(context, 2));
                indicator.setColors(Color.parseColor("#f57c00"));
//                LinePagerIndicator indicator = new LinePagerIndicator(context);
//                indicator.setColors(Color.parseColor("#40c4ff"));
                return indicator;
            }

        });

        initViewPagerData();
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }


    public void initViewPagerData() {
        mFragmentList = new ArrayList<>();
        for (int i = 0; i < mTitleDataList.length; i++) {
            GameOrderFragment fragment = (GameOrderFragment) GameOrderFragment.newInstance();
            if(i==0){
                fragment.setState(1);
            }else{
                fragment.setState(0);
            }
            mFragmentList.add(fragment);
//            mFragmentList.add( OrderFragment.newInstance());
        }
        mViewPager.setOffscreenPageLimit(mTitleDataList.length);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });

    }
}
